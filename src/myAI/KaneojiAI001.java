package myAI;

import java.util.ArrayList;
import java.util.List;

import millionMahjong.GameInfo;
import millionMahjong.Player;
import millionMahjong.TehaiManager;

public class KaneojiAI001 extends BasePlayerAI {
	int id;
	Player me;
	Player[] players;
	TehaiManager tm;
	int honba;
	int kyotaku;
	boolean naki=false;

	public KaneojiAI001(Player me) {
		this.me=me;
		this.tm=me.getTM();
	}
	
	public void update(GameInfo gi){
		this.id=gi.getID();
		this.players=gi.getPlayer();
		this.tm = players[id].getTM();
		this.honba=gi.getHonba();
		this.kyotaku=gi.getKyotaku();
	}

	public int dahaiSelect(){
		List<Integer> dahaiKouho = new ArrayList<>();
		int shanten= tm.shantenUpdate();
		for(int t=0;t<34;t++){
			if(tm.te[t]==0) continue;
			tm.te[t]--;
			if (shanten == tm.shantenUpdate()) {
				dahaiKouho.add(t);
			}
			tm.te[t]++;
		}
		if (dahaiKouho.isEmpty()) {
			for(int t=0;t<34;t++){
				if(tm.te[t]>=1) dahaiKouho.add(t);
			}
		}

		return dahaiKouho.get((int) (Math.random() * dahaiKouho.size()));
	}

	public boolean kyushuSelect(){
		return tm.shantenKokusi()>=3;
	}

	public boolean kakanSelect(int id) {
		return id==me.bakaze||id==me.getJikaze()||id==31||id==32||id==33;//役牌だけ
	}

	public boolean minkanSelect(int id) {
		 return false;
	}

	public boolean ankanSelect(int id) {
		return id>=27; //字牌だけ暗カン
	}

	public boolean ponSelect(int id) {
		return tm.te[id]==2 &&(id==me.bakaze||id==me.getJikaze()||id==31||id==32||id==33);//役牌だけ
	}

	public boolean chi0Select(int id) {
		return false;
	}

	public boolean chi1Select(int id) {
		return false;
	}

	public boolean chi2Select(int id) {
		return false;
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
