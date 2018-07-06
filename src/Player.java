import java.util.ArrayList;
import java.util.List;

public class Player {
	int[] te = new int[34];
	List<Tile> tehai = new ArrayList<>();
	List<Tile> sutehai = new ArrayList<>();
	List<Mentu> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	int shanten;

	int jikaze;
	int bakaze;
	boolean isReach = false;
	boolean isDoubleReach = false;
	boolean isIppatu = false;
	boolean isHaitei = false;
	boolean isHoutei = false;
	boolean isRinshan = false;
	boolean isChankan = false;

	boolean isMenzen = true;
	String name;
	PlayerAI ai;

	Player(String name, int bakaze,int jikaze) {
		this.name = name;
		this.jikaze=jikaze;
		ai = new PlayerAI();
	}

	// 一枚ツモる
	public void tumo(Tile tile) {
		tehai.add(tile);
		te[tile.id]++;
		this.ripai();
		this.shanten = MillionMahjong.shanten(this);
	}

	public Tile dahai(Tile tumohai) {
		Tile da = isReach ? tumohai : selectDahai();
		tehai.remove(da);
		te[da.id]--;
		sutehai.add(da);
		return da;
	}

	// 捨て牌選択
	public Tile selectDahai() {
		List<Tile> dahaiKouho = new ArrayList<>();
		for (int i = 0; i < tehai.size(); i++) {
			Tile t = tehai.remove(i);
			te[t.id]--;
			if (shanten == MillionMahjong.shanten(this)) {
				dahaiKouho.add(t);
			}
			tehai.add(i, t);
			te[t.id]++;
		}
		if (dahaiKouho.isEmpty()) {
			dahaiKouho.addAll(tehai);
		}

		return dahaiKouho.get((int) (Math.random() * dahaiKouho.size()));
	}

	void minkan(int id) {
		// System.out.println(this.name+"：カン！("+t+")");
		te[id] -= 3;
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));

		int[] pai = { id, id, id, id };
		fuuro.add(new Mentu(MentuType.MINKAN, pai));
		num_fuuro++;
		num_kan++;
		isMenzen = false;
	}

	void kakan(int id) {
		// System.out.println(this.name+"：カン！("+t+")");
		te[id]--;
		tehai.remove(tehai.indexOf(new Tile(id)));

		Mentu f = null;
		for (int i = 0; i < num_fuuro; i++) {
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
		// System.out.println(this.name+"：カン！("+t+")");
		te[id] -= 4;
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));

		int[] pai = { id, id, id, id };
		fuuro.add(new Mentu(MentuType.ANKAN, pai));
		num_fuuro++;
		num_kan++;
	}

	void pon(int id) {
		// System.out.println(this.name+"：ポン！("+t+")");
		te[id] -= 2;
		tehai.remove(tehai.indexOf(new Tile(id)));
		tehai.remove(tehai.indexOf(new Tile(id)));

		int[] pai = { id, id, id };
		fuuro.add(new Mentu(MentuType.PON, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii0(int id) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[id - 2]--;
		te[id - 1]--;
		tehai.remove(tehai.indexOf(new Tile(id - 2)));
		tehai.remove(tehai.indexOf(new Tile(id - 1)));

		int[] pai = { id - 2, id - 1, id };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii1(int id) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[id - 1]--;
		te[id + 1]--;
		tehai.remove(tehai.indexOf(new Tile(id - 1)));
		tehai.remove(tehai.indexOf(new Tile(id + 1)));

		int[] pai = { id - 1, id, id + 1 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void chii2(int id) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[id + 1]--;
		te[id + 2]--;
		tehai.remove(tehai.indexOf(new Tile(id + 1)));
		tehai.remove(tehai.indexOf(new Tile(id + 2)));

		int[] pai = { id, id + 1, id + 2 };
		fuuro.add(new Mentu(MentuType.CHI, pai));
		num_fuuro++;
		isMenzen = false;
	}

	void ripai() {
		this.tehai.sort((t1, t2) -> t1.id - t2.id);
	}

	void printTehai() {
		ripai();
		String str = "";
		for (int i = 0; i < tehai.size(); i++) {
			if (i >= 1 && !tehai.get(i - 1).shu.equals(tehai.get(i).shu)) {
				str += tehai.get(i - 1).shu;
			}
			str += tehai.get(i).kazu;
		}
		str += tehai.get(tehai.size() - 1).shu;
		for (int i = num_fuuro - 1; i >= 0; i--) {
			str += fuuro.get(i);
		}
		System.out.println(str);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
