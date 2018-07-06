import java.util.ArrayList;
import java.util.List;

public class Player {
	int[] te = new int[34];
	List<Tile> tehai = new ArrayList<>();
	List<Tile> sutehai = new ArrayList<>();
	List<Fuuro> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	int shanten;

	int jikaze = 30;// 未
	int bakaze = 27;// 未
	boolean isReach = false;
	boolean isDoubleReach = false; // 未実装
	boolean isIppatu = false; // 未実装
	boolean isHaitei = false; // 未実装
	boolean isHoutei = false; // 未実装
	boolean isRinshan = false; // 未実装
	boolean isChankan = false; // 未実装

	boolean isMenzen = true;
	String name;
	PlayerAI ai;

	Player(String name) {
		this.name = name;
		ai = new PlayerAI();
	}

	class Fuuro {
		MentuType type;
		Tile[] pai;

		Fuuro(MentuType type) {
			this.type = type;
			switch (type) {
			case MINKAN:
				this.pai = new Tile[4];
				break;
			case ANKAN:
				this.pai = new Tile[4];
				break;
			default:
				this.pai = new Tile[3];
				break;
			}
		}

		@Override
		public String toString() {
			String str = this.type == MentuType.ANKAN ? "[" : "(";
			for (int i = 0; i < pai.length; i++) {
				str += pai[i].kazu;
			}
			str += pai[0].shu + (this.type == MentuType.ANKAN ? "]" : ")");
			return str;
		}
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

	void minkan(Tile t) {
		num_kan++;
		// System.out.println(this.name+"：カン！("+t+")");
		te[t.id] -= 3;
		Fuuro f = new Fuuro(MentuType.MINKAN);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		f.pai[3] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
		isMenzen = false;
	}

	void kakan(Tile t) {
		num_kan++;
		// System.out.println(this.name+"：カン！("+t+")");
		Fuuro f = null;
		for (int i = 0; i < num_fuuro; i++) {
			f = fuuro.get(i);
			if (f.type == MentuType.PON && f.pai[0].equals(t)) {
				break;
			}
		}
		f.type = MentuType.MINKAN;
		te[t.id]--;
		Tile[] kan = new Tile[4];
		kan[0] = f.pai[0];
		kan[1] = f.pai[1];
		kan[2] = f.pai[2];
		kan[3] = tehai.remove(tehai.indexOf(t));
		f.pai = kan;
	}

	void ankan(Tile t) {
		num_kan++;
		// System.out.println(this.name+"：カン！("+t+")");
		te[t.id] -= 4;
		Fuuro f = new Fuuro(MentuType.ANKAN);
		f.pai[0] = tehai.remove(tehai.indexOf(t));
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		f.pai[3] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
	}

	void pon(Tile t) {
		// System.out.println(this.name+"：ポン！("+t+")");
		te[t.id] -= 2;
		Fuuro f = new Fuuro(MentuType.PON);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
		isMenzen = false;
	}

	void chii0(Tile t) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[t.id - 2]--;
		te[t.id - 1]--;
		Fuuro f = new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id - 2)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id - 1)));
		fuuro.add(f);
		num_fuuro++;
		isMenzen = false;
	}

	void chii1(Tile t) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[t.id - 1]--;
		te[t.id + 1]--;
		Fuuro f = new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id - 1)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id + 1)));
		fuuro.add(f);
		num_fuuro++;
		isMenzen = false;
	}

	void chii2(Tile t) {
		// System.out.println(this.name+"：チー！("+t+")");
		te[t.id + 1]--;
		te[t.id + 2]--;
		Fuuro f = new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id + 1)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id + 2)));
		fuuro.add(f);
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