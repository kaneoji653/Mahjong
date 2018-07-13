package myAI;

import java.util.ArrayList;
import java.util.List;

import millionMahjong.Player;

public class KaneojiAI001 extends BasePlayerAI {
	Player me;
	boolean naki=false;
	
	public KaneojiAI001(Player me) {
		this.me=me;
	}
	
	public int dahaiSelect(){
		List<Integer> dahaiKouho = new ArrayList<>();
		boolean kokusiMode = me.tm.is9shu();
		int shanten=(kokusiMode ? me.tm.shantenKokusi() : me.tm.shantenUpdate());
		for(int t=0;t<34;t++){
			if(me.tm.te[t]==0) continue;
			me.tm.te[t]--;
			if (shanten == (kokusiMode ? me.tm.shantenKokusi() : me.tm.shantenUpdate())) {
				dahaiKouho.add(t);
			}
			me.tm.te[t]++;
		}
		if (dahaiKouho.isEmpty()) {
			for(int t=0;t<34;t++){
				if(me.tm.te[t]>=1) dahaiKouho.add(t);
			}
		}

		return dahaiKouho.get((int) (Math.random() * dahaiKouho.size()));
	}
	
	public boolean kyushuSelect(){
		return me.tm.shantenKokusi()>=3;
	}

	public boolean kakanSelect(int id) {
		return id==me.bakaze||id==me.jikaze||id==31||id==32||id==33;//役牌だけ
	}

	public boolean minkanSelect(int id) {
		 return false;
	}

	public boolean ankanSelect(int id) {
		return id>=27; //字牌だけ暗カン
	}

	public boolean ponSelect(int id) {
		return id==me.bakaze||id==me.jikaze||id==31||id==32||id==33;//役牌だけ
	}

	public boolean chi0Select(int id) {
		return !me.isMenzen;
	}

	public boolean chi1Select(int id) {
		return !me.isMenzen;
	}

	public boolean chi2Select(int id) {
		return !me.isMenzen;
	}

	public boolean reachSelect() {
		return true;
	}

	public boolean tsumoSelect() {
		return true;
	}

	public boolean ronSelect() {
		return true;
	}

}
