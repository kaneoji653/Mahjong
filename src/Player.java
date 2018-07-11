import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
	int[] te = new int[34];
	List<Integer> sutehai = new ArrayList<>();
	List<Mentu> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	int shanten;

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
	PlayerAI ai;
	int point = 25000;

	void initialize(){
		te = new int[34];
		sutehai = new ArrayList<>();
		fuuro = new ArrayList<>();
		num_fuuro = 0;
		num_kan = 0;
		shanten=8;
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
		ai = new PlayerAI();
	}

	public boolean isOya(){
		return this.jikaze==27;
	}

	boolean is9shu(){
		int cnt=0;
		if(te[0]>=1)cnt++;
		if(te[8]>=1)cnt++;
		if(te[9]>=1)cnt++;
		if(te[17]>=1)cnt++;
		if(te[18]>=1)cnt++;
		if(te[26]>=1)cnt++;
		if(te[27]>=1)cnt++;
		if(te[28]>=1)cnt++;
		if(te[29]>=1)cnt++;
		if(te[30]>=1)cnt++;
		if(te[31]>=1)cnt++;
		if(te[32]>=1)cnt++;
		if(te[33]>=1)cnt++;
		return cnt>=9;
	}

	// 一枚ツモる
	public void tumo(Integer tile) {
		te[tile]++;
		this.shanten = MillionMahjong.shanten(this);
	}

	public int dahai(Integer tumohai, boolean isReachTurn) {
		int da = isReach&&!isReachTurn ? tumohai : selectDahai();
		te[da]--;
		sutehai.add(da);
		return da;
	}

	// 捨て牌選択
	public int selectDahai() {
		List<Integer> dahaiKouho = new ArrayList<>();
		for(int t=0;t<34;t++){
			if(te[t]==0) continue;
			te[t]--;
			if (shanten == MillionMahjong.shanten(this)) {
				dahaiKouho.add(t);
			}
			te[t]++;
		}
		if (dahaiKouho.isEmpty()) {
			for(int t=0;t<34;t++){
				if(te[t]>=1) dahaiKouho.add(t);
			}
		}

		return dahaiKouho.get((int) (Math.random() * dahaiKouho.size()));
	}

	void minkan(int id) {
//		System.out.println(this.name+"：カン！("+new Tile(id)+")");
		te[id] -= 3;
		int[] pai = {id,id,id,id};
		fuuro.add(new Mentu(MentuType.MINKAN, pai));
		num_fuuro++;
		num_kan++;
		isMenzen = false;
	}

	void kakan(int id) {
//		 System.out.println(this.name+"：カン！("+new Tile(id)+")");
		te[id]--;
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
		te[id] -= 4;
		int[] pai = { id, id, id, id };
		fuuro.add(new Mentu(MentuType.ANKAN, pai));
		num_fuuro++;
		num_kan++;
	}

	void pon(int id) {
//		System.out.println(this.name+"：ポン！("+new Tile(id)+")");
		te[id] -= 2;
		int[] pai = { id, id, id };
		fuuro.add(new Mentu(MentuType.PON, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii0(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		te[id - 2]--;
		te[id - 1]--;
		int[] pai = { id - 2, id - 1, id };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii1(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		te[id - 1]--;
		te[id + 1]--;
		int[] pai = { id - 1, id, id + 1 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii2(int id) {
//		 System.out.println(this.name+"：チー！("+new Tile(id)+")");
		te[id + 1]--;
		te[id + 2]--;
		int[] pai = { id, id + 1, id + 2 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	String tehaiToString(){
		return Arrays.toString(te);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
