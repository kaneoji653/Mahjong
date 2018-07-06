public class Mentu {
	MentuType type;
	int[] pai;
	String shu;

	Mentu(MentuType type, int[] pai) {
		this.type = type;
		this.pai = pai;
		this.shu = new Tile(pai[0]).shu;
	}

	@Override
	public String toString() {
		if (type == MentuType.PON || type == MentuType.CHI || type == MentuType.MINKAN) {
			return "(" + (pai[0] % 9 + 1) + (pai[1] % 9 + 1) + (pai[2] % 9 + 1)
					+ (type == MentuType.MINKAN ? pai[3] % 9 + 1 : "") + shu + ")";
		} else {
			return "[" + (pai[0] % 9 + 1) + (pai[1] % 9 + 1) + (pai[2] % 9 + 1)
					+ (type == MentuType.ANKAN ? pai[3] % 9 + 1 : "") + shu + "]";
		}
	}
}

enum MentuType {
	SHUN, CHI, ANKO, PON, ANKAN, MINKAN,
}

