package myAI;

import java.util.ArrayList;
import java.util.List;

import millionMahjong.GameInfo;
import millionMahjong.Player;
import millionMahjong.TehaiManager;

//シャンテンを下げる牌からランダムに切り、テンパったらリーチをする人です。
public class KaneojiAI000 extends BasePlayerAI {
	int id;
	Player me;
	TehaiManager tm;
	boolean naki=false;
	Player[] players;
	int honba;
	int kyotaku;

	public KaneojiAI000(Player me) {
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
