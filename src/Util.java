import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

	public static List<Agari> agariEnum(Player player, int agarihai, boolean isTumo, List<Tile> dorahyouList) {
		player.printTehai();
		// int[] te = player.te;
		List<Agari> agari = new ArrayList<>();

		// 頭選択
		List<Integer> headKouho = new ArrayList<>();
		for (int i = 0; i < 34; i++) {
			if (player.te[i] >= 2) {
				headKouho.add(i);
			}
		}
		for (Integer head : headKouho) {
			int[] te = Arrays.copyOf(player.te, player.te.length);
			te[head] -= 2;

			List<MatiType> matiKouho = new ArrayList<>();
			if (agarihai == head) {
				matiKouho.add(MatiType.TANKI);
			}
			boolean isPinfu = player.num_fuuro == 0
					&& !(head == player.bakaze || head == player.jikaze || head == 31 || head == 32 || head == 33);

			// 面子分解(前から見る。3枚あるなら暗刻として除去、1枚あるなら順子として除去(できないなら終了))
			Mentu[] mentu4 = new Mentu[4];
			int m_cnt = 0;
			for (int i = 0; i < 34; i++) {
				// 刻子判定
				if (te[i] >= 3) {
					int[] pai = { i, i, i };
					mentu4[m_cnt] = new Mentu(MentuType.ANKO, pai);
					te[i] -= 3;
					m_cnt++;
					isPinfu = false;
					if (agarihai == i) {
						matiKouho.add(MatiType.SHAMPON);
					}
				}
				// 順子判定
				while (te[i] >= 1) {
					// 取れないなら終了
					if (!(0 <= i && i <= 6 || 9 <= i && i <= 15 || 18 <= i && i <= 24) || te[i + 1] == 0
							|| te[i + 2] == 0) {
						break;
					} else {
						int[] pai = { i, i + 1, i + 2 };
						mentu4[m_cnt] = new Mentu(MentuType.SHUN, pai);
						te[i]--;
						te[i + 1]--;
						te[i + 2]--;
						m_cnt++;
						if (agarihai == i) {
							matiKouho.add(i == 6 || i == 15 || i == 24 ? MatiType.PENCHAN : MatiType.RYAMMEN);
						}
						if (agarihai == i + 1) {
							matiKouho.add(MatiType.KANCHAN);
						}
						if (agarihai == i + 2) {
							matiKouho.add(i == 0 || i == 9 || i == 18 ? MatiType.PENCHAN : MatiType.RYAMMEN);
						}
					}
				}
			}

			// 4面子取れたら、待ちごとにAgariインスタンス生成
			if (m_cnt + player.num_fuuro == 4) {
				for (int i = 0; i < player.num_fuuro; i++) {
					mentu4[3 - i] = player.fuuro.get(i);
				}

				// 待ち限定
				if (matiKouho.size() >= 2) {
					matiKouho.remove(MatiType.SHAMPON);
				}
				if (matiKouho.size() >= 2) {
					if (matiKouho.contains(MatiType.RYAMMEN) && isPinfu) {
						matiKouho.clear();
						matiKouho.add(MatiType.RYAMMEN);
					} else {
						matiKouho.remove(MatiType.RYAMMEN);
						while (matiKouho.size() >= 2) {
							matiKouho.remove(matiKouho.size() - 1);
						}
					}
				}
				MatiType mati = matiKouho.get(0);

				// 刻子をロンは明刻
				if (!isTumo && mati == MatiType.SHAMPON) {
					for (int i = 0; i < 4; i++) {
						if (mentu4[i].type == MentuType.ANKO && mentu4[i].pai[0] == agarihai) {
							mentu4[i].type = MentuType.PON;
						}
					}
				}

				// あがり生成
				agari.add(new Agari(player, head, mentu4, mati, isTumo, dorahyouList));
			}
		}

		// あがり候補のスコアを計算して、最大値をとる。(未)
		if (agari.isEmpty()) {
			System.out.println("ちーといorこくし");
			System.out.println(player.tehai);
		}
		// for(Agari a:agari){
		// System.out.println(a);
		// }
		return agari;
	}
}

enum MentuType {
	SHUN, CHI, ANKO, PON, ANKAN, MINKAN,
}

class Mentu {
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
			return "(" + (pai[0] / 9 + 1) + (pai[1] / 9 + 1) + (pai[2] / 9 + 1)
					+ (type == MentuType.MINKAN ? pai[3] / 9 + 1 : "") + shu + ")";
		} else {
			return "[" + (pai[0] / 9 + 1) + (pai[1] / 9 + 1) + (pai[2] / 9 + 1)
					+ (type == MentuType.MINKAN ? pai[3] / 9 + 1 : "") + shu + "]";
		}
	}
}

enum MatiType {
	RYAMMEN, KANCHAN, PENCHAN, SHAMPON, TANKI;
}
