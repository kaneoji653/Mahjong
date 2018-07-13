package myAI;

import java.util.ArrayList;
import java.util.List;

import millionMahjong.Player;

//シャンテンを下げる牌からランダムに切り、テンパったらリーチをする人です。
public class KaneojiAI000 extends BasePlayerAI {
	Player me;
	boolean naki=false;

	public KaneojiAI000(Player me) {
		this.me=me;
	}

	public int dahaiSelect(){
		List<Integer> dahaiKouho = new ArrayList<>();
		int shanten= me.tm.shantenUpdate();
		for(int t=0;t<34;t++){
			if(me.tm.te[t]==0) continue;
			me.tm.te[t]--;
			if (shanten == me.tm.shantenUpdate()) {
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
		return true;
	}

	public boolean kakanSelect(int id) {
		return naki;
	}

	public boolean minkanSelect(int id) {
		return naki;
	}

	public boolean ankanSelect(int id) {
		return naki;
	}

	public boolean ponSelect(int id) {
		return naki;
	}

	public boolean chi0Select(int id) {
		return naki;
	}

	public boolean chi1Select(int id) {
		return naki;
	}

	public boolean chi2Select(int id) {
		return naki;
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
