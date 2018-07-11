package millionMahjong;
import java.util.ArrayList;
import java.util.List;

import myAI.AbstractPlayerAI;
import myAI.BasePlayerAI;

public class Player {
	List<Integer> sutehai = new ArrayList<>();
	List<Mentu> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	TehaiManager tm;
	int score=0;

	int jikaze;
	int bakaze;
	boolean isTenho=false;
	boolean isChiho=false;
	boolean isReach = false;
	boolean isDoubleReach = false;
	boolean isIppatu = false;
	boolean isHaitei = false;
	boolean isHoutei = false;
	boolean isRinshan = false;
	boolean isChankan = false;

	boolean isMenzen = true;
	boolean isFuriten = false;
	String name;
	AbstractPlayerAI ai;
	int point = 25000;

	void initialize(){
		tm=new TehaiManager(new int[34]);
		sutehai = new ArrayList<>();
		fuuro = new ArrayList<>();
		num_fuuro = 0;
		num_kan = 0;
//		shanten=8;
		isTenho=false;
		isChiho=false;
		isReach = false;
		isDoubleReach = false;
		isIppatu = false;
		isHaitei = false;
		isHoutei = false;
		isRinshan = false;
		isChankan = false;
		isMenzen = true;
	}

	Player(String name, int bakaze,int jikaze) {
		this.name = name;
		this.bakaze=bakaze;
		this.jikaze=jikaze;
		ai = new BasePlayerAI();
	}

	public boolean isOya(){
		return this.jikaze==27;
	}

	// 一枚ツモる
	public void tumo(Integer tile) {
		tm.te[tile]++;
		tm.shantenUpdate();
	}

	public int dahai(Integer tumohai, boolean isReachTurn) {
		int da = isReach&&!isReachTurn ? tumohai : selectDahai();
		tm.te[da]--;
		sutehai.add(da);
		return da;
	}

	// 捨て牌選択
	public int selectDahai() {
		List<Integer> dahaiKouho = new ArrayList<>();
		boolean kokusiMode = tm.is9shu();
		int shanten=(kokusiMode ? tm.shantenKokusi() : tm.shantenUpdate());
		for(int t=0;t<34;t++){
			if(tm.te[t]==0) continue;
			tm.te[t]--;
			if (shanten == (kokusiMode ? tm.shantenKokusi() : tm.shantenUpdate())) {
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

	void minkan(int id) {
//		System.out.println(this.name+"：カン！("+new Tile(id)+")");
		tm.te[id] -= 3;
		int[] pai = {id,id,id,id};
		fuuro.add(new Mentu(MentuType.MINKAN, pai));
		num_fuuro++;
		num_kan++;
		isMenzen = false;
	}

	void kakan(int id) {
//		System.out.println(this.name+"：カン！("+new Tile(id)+")");
		tm.te[id]--;
		Mentu f = null;
		for (int i = 0; i < fuuro.size(); i++) {
			f = fuuro.get(i);
			if (f.type == MentuType.PON && f.pai[0] == id) {
				int[] pai = { id, id, id, id };
				fuuro.set(i, new Mentu(MentuType.MINKAN, pai));
				break;
			}
		}
		num_kan++;
	}

	void ankan(int id) {
//		System.out.println(this.name+"：カン！("+new Tile(id)+")");
		tm.te[id] -= 4;
		int[] pai = { id, id, id, id };
		fuuro.add(new Mentu(MentuType.ANKAN, pai));
		num_fuuro++;
		num_kan++;
	}

	void pon(int id) {
//		System.out.println(this.name+"：ポン！("+new Tile(id)+")");
		tm.te[id] -= 2;
		int[] pai = { id, id, id };
		fuuro.add(new Mentu(MentuType.PON, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chi0(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		tm.te[id - 2]--;
		tm.te[id - 1]--;
		int[] pai = { id - 2, id - 1, id };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
		if(tm.te[id-2]<0)System.out.println("チー2エラー");
		if(tm.te[id-1]<0)System.out.println("チー1エラー");
		if(tm.te[id]<0)System.out.println("チー0エラー");
	}

	void chi1(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		tm.te[id - 1]--;
		tm.te[id + 1]--;
		int[] pai = { id - 1, id, id + 1 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chi2(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		tm.te[id + 1]--;
		tm.te[id + 2]--;
		int[] pai = { id, id + 1, id + 2 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
