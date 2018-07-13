package millionMahjong;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import myAI.BasePlayerAI;
import myAI.KaneojiAI000;

public class Player {
	List<Integer> sutehai = new ArrayList<>();
	List<Mentu> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	TehaiManager tm;
	int score=0;

	int jikaze;
	public int bakaze;//プレイヤーにあるべきではない
	boolean isTenho=false;
	boolean isChiho=false;
	boolean isReach = false;
	boolean isDoubleReach = false;
	boolean isIppatu = false;
	boolean isHaitei = false;
	boolean isHoutei = false;
	boolean isRinshan = false;
	boolean isChankan = false;
	boolean visible = false; //他家からみえるか
	boolean isMenzen = true;
	boolean isFuriten = false;
	String name;
	BasePlayerAI ai;
	int point = 25000;

	public List<Mentu> getFuuro() {
		return new ArrayList<>(fuuro);
	}

	public TehaiManager getTM(){
		return visible ? new TehaiManager(Arrays.copyOf(tm.te, 34)) : null;
	}

	public int getJikaze() {
		return jikaze;
	}

	public boolean isReach() {
		return isReach;
	}

	public boolean isMenzen() {
		return isMenzen;
	}

	void initialize(){
		Arrays.fill(tm.te, 0);
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
		this.tm=new TehaiManager(new int[34]);
		this.initialize();
		setAI(new KaneojiAI000(this));
	}

	public void setAI(BasePlayerAI ai){
		this.ai=ai;
	}

	public boolean isOya(){
		return this.jikaze==27;
	}

	// 一枚ツモる
	void tumo(Integer tile) {
		tm.te[tile]++;
		tm.shantenUpdate();
	}

	int dahai(Integer tumohai, boolean isReachTurn) {
		int da = isReach&&!isReachTurn ? tumohai : ai.dahaiSelect();//本来はゲームで見える情報だけ渡すべき。
		tm.te[da]--;
		sutehai.add(da);
		return da;
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
