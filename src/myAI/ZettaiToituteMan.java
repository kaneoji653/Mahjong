package myAI;

import java.util.ArrayList;
import java.util.List;

import millionMahjong.Player;

public class ZettaiToituteMan extends KaneojiAI000 {
	public ZettaiToituteMan(Player me) {
		super(me);
		this.naki=false;
	}
	
	@Override
	public int dahaiSelect() {
		List<Integer> dahaiKouho = new ArrayList<>();
		int shanten= tm.shantenUpdate();
		for(int t=0;t<34;t++){
			if(tm.te[t]==0) continue;
			if(tm.te[t]==1) return t;
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
	
	@Override
	public boolean ponSelect(int id) {
		return true;
	}
	
	@Override
	public boolean kakanSelect(int id) {
		return true;
	}
	
	@Override
	public boolean ankanSelect(int id) {
		return true;
	}
	
	
}
