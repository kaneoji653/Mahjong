import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MillionMahjong {
	public static void main(String[] args) {
		int cnt = 0;
		while (true) {
			System.out.println(++cnt + "局目");
			kyokuStart();
		}
	}

	public static void kyokuStart() {
		// プレイヤーの生成
		Player[] player = new Player[4];
		for (int i=0;i<4;i++) {
			player[i] = new Player("P"+(i+1), 27, 27+i);
		}

		// 牌山の生成
		ArrayList<Tile> yama = new ArrayList<>(136);
		for (int id = 0; id < 34; id++) {
			for (int i = 0; i < 4; i++) {
				yama.add(new Tile(id));
			}
		}
		Collections.shuffle(yama);

		// 王牌
		List<Tile> wanpai = new ArrayList<>();
		List<Tile> dorahyouList = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			wanpai.add(yama.remove(0));
		}
		dorahyouList.add(wanpai.remove(0));

		// 配牌
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				player[j].tumo(yama.remove(0));
			}
		}

		// 和了った人
		int houra = -1;
		int total_kan = 0;

		//////////////// 局開始/////////////////////
		// 親の第一ツモ
		int ban = 0;
		Tile tumohai = yama.remove(0);
		player[ban].tumo(tumohai);
		boolean isFirstTurn = true;

		dahaiWait: while (true) {
			Player p = player[ban];
			if (isFirstTurn) {
				// 九種チェック

				if (!p.sutehai.isEmpty()) {
					isFirstTurn = false;
				}
			}

			// ツモあがりする？
			if (p.shanten == -1) {
				if (p.ai.tsumoSelect()) {
					System.out.println(p + ":ツモ！(" + tumohai + ")");
					if (yama.isEmpty()) p.isHaitei = true;
					

					if (p.isReach) {
						for (int i = 0; i < total_kan + 1; i++) {
							dorahyouList.add(wanpai.remove(0));
						}
					}
					Util.agari(p, tumohai.id, true, dorahyouList);
					houra = ban;
					break dahaiWait;
				}
			}
			p.isRinshan = false;

			if (!yama.isEmpty()) {
				// 暗カンする？
				for (int i = 0; i < 34; i++) {
					if (p.te[i] == 4) {
						if (p.ai.ankanSelect(i)) {
							p.ankan(i);
							total_kan++;
							isFirstTurn = false;
							dorahyouList.add(wanpai.remove(0));
							tumohai = yama.remove(0);// とりあえず山の上から引く。
							player[ban].tumo(tumohai);
							p.isRinshan = true;
							continue dahaiWait;
						}
					}
				}

				// 加カンする？
				for (int i = 0; i < p.num_fuuro; i++) {
					int id = p.fuuro.get(i).pai[0];
					if (p.fuuro.get(i).type == MentuType.PON && p.te[id] == 1) {
						if (p.ai.kakanSelect(id)) {
							p.kakan(id);

							for (int j = 1; j <= 3; j++) {
								Player ro = player[(ban + j) % 4];
								if (ro.shanten == 0 && !ro.sutehai.contains(new Tile(id)) && ronCheck(ro, id)) {
									if (ro.ai.ronSelect()) {
										houra = (ban + j) % 4;
										ro.isChankan = true;
										System.out.println(ro + ":チャンカンロン！(" + id + ") 放銃：" + p);
										ro.tehai.add(new Tile(id));
										ro.te[id]++;
										if (ro.isReach) {
											for (int a = 0; a < total_kan + 1; a++) {
												dorahyouList.add(wanpai.remove(0));
											}
										}
										Util.agari(ro, id, false, dorahyouList);
										break dahaiWait;
									}
								}
							}

							tumohai = yama.remove(0);// とりあえず山の上から引く。
							player[ban].tumo(tumohai);
							p.isRinshan = true;
							continue dahaiWait;
						}
					}
				}
			}

			// 打牌は？？ まだAIに委譲してない
			Tile da = p.dahai(tumohai);

			// リーチ宣言する？
			if (!p.isReach && yama.size() >= 4 && p.shanten == 0 && p.isMenzen) {
				if (p.ai.reachSelect()) {
					p.isReach = true;
					p.isDoubleReach = isFirstTurn;
					System.out.println(p + ":リーチ！");
				}
			}

			// ロンする？
			for (int i = 1; i <= 3; i++) {
				Player ro = player[(ban + i) % 4];
				if (ro.shanten == 0 && !ro.sutehai.contains(da) && ronCheck(ro, da.id)) {
					if (ro.ai.ronSelect()) {
						if (yama.isEmpty()) {
							ro.isHoutei = true;
						}
						System.out.println(ro + ":ロン！(" + da + ") 放銃：" + p);
						ro.tehai.add(da);
						ro.te[da.id]++;
						if (ro.isReach) {
							for (int a = 0; a < total_kan + 1; a++) {
								dorahyouList.add(wanpai.remove(0));
							}
						}
						Util.agari(ro, da.id, false, dorahyouList);

						houra = (ban + i) % 4;
						break dahaiWait;
					}
				}
			}

			total_kan = player[0].num_kan + player[1].num_kan + player[2].num_kan + player[3].num_kan;

			while (dorahyouList.size() != 1 + total_kan) {
				dorahyouList.add(wanpai.remove(0));
			}

			if (!yama.isEmpty()) {
				// 大明カンする？
				for (int i = 1; i <= 3; i++) {
					Player tg = player[(ban + i) % 4];
					if (tg.isReach) {
						continue;
					}
					if (tg.te[da.id] == 3) {
						if (tg.ai.minkanSelect(da.id)) {
							tg.minkan(da.id);
							isFirstTurn = false;
							ban = (ban + i) % 4;

							tumohai = yama.remove(0);// とりあえず山の上から引くことにしてる。
							player[ban].tumo(tumohai);
							tg.isRinshan = true;
							continue dahaiWait;
						}
					}
				}

				// ポンする？
				for (int i = 1; i <= 3; i++) {
					Player tg = player[(ban + i) % 4];
					if (tg.isReach) {
						continue;
					}
					if (tg.te[da.id] >= 2) {
						if (tg.ai.ponSelect(da.id)) {
							tg.pon(da.id);
							isFirstTurn = false;
							ban = (ban + i) % 4;
							continue dahaiWait;
						}
					}
				}

				// チーする？
				Player tg = player[(ban + 1) % 4];
				if (!tg.isReach) {
					// チー0する？
					if (!da.shu.equals("z") && da.kazu != 1 && da.kazu != 2 && tg.te[da.id - 2] >= 1
							&& tg.te[da.id - 1] >= 1) {
						if (tg.ai.chii0Select(da.id)) {
							tg.chii0(da.id);
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							continue dahaiWait;
						}
					}
					// チー1する？
					if (!da.shu.equals("z") && da.kazu != 1 && da.kazu != 9 && tg.te[da.id - 1] >= 1
							&& tg.te[da.id + 1] >= 1) {
						if (tg.ai.chii1Select(da.id)) {
							tg.chii1(da.id);
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							continue dahaiWait;
						}
					}
					// チー2する？
					if (!da.shu.equals("z") && da.kazu != 8 && da.kazu != 9 && tg.te[da.id + 1] >= 1
							&& tg.te[da.id + 2] >= 1) {
						if (tg.ai.chii2Select(da.id)) {
							tg.chii2(da.id);
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							continue dahaiWait;
						}
					}
				}
			}

			// 次のツモへ。
			if (yama.isEmpty()) {
				break;
			}
			ban = (ban + 1) % 4;
			tumohai = yama.remove(0);
			player[ban].tumo(tumohai);
		}

		////////////////////// 局終了////////////////////////////////////

		if (houra == -1) {
			System.out.println("流局");
		} else {

		}
		System.out.println("-----------------------");
	}

	static boolean ronCheck(Player p, int id) {
		p.tehai.add(new Tile(id));
		p.te[id]++;
		boolean canRon = shanten(p) == -1;
		p.tehai.remove(p.tehai.size() - 1);
		p.te[id]--;
		return canRon;
	}

	static int shanten(Player p) {
		int[] te = p.te;

		// 普通手のシャンテン
		int shanten = selectBlock(te, p.num_fuuro, 0, 0, true);

		// メンゼンならチートイと国士のシャンテン計算
		if (p.num_fuuro == 0) {

			// 七対子のシャンテン
			int shanten_chitoi = 6;
			if (p.num_fuuro == 0) {
				for (int i = 0; i < 34; i++) {
					if (te[i] >= 2) {
						shanten_chitoi--;
					}
				}
			}

			// 国士無双のシャンテン
			int shanten_kokushi = 14;
			int kokushiHead = 0;
			if (te[0]>=1)shanten_kokushi--; if(te[0]>=2)kokushiHead=1;
			if (te[8]>=1)shanten_kokushi--; if(te[8]>=2)kokushiHead=1;
			if (te[9]>=1)shanten_kokushi--; if(te[9]>=2)kokushiHead=1;
			if (te[17]>=1)shanten_kokushi--; if(te[17]>=2)kokushiHead=1;
			if (te[18]>=1)shanten_kokushi--; if(te[18]>=2)kokushiHead=1;
			if (te[26]>=1)shanten_kokushi--; if(te[26]>=2)kokushiHead=1;
			if (te[27]>=1)shanten_kokushi--; if(te[27]>=2)kokushiHead=1;
			if (te[28]>=1)shanten_kokushi--; if(te[28]>=2)kokushiHead=1;
			if (te[29]>=1)shanten_kokushi--; if(te[29]>=2)kokushiHead=1;
			if (te[30]>=1)shanten_kokushi--; if(te[30]>=2)kokushiHead=1;
			if (te[31]>=1)shanten_kokushi--; if(te[31]>=2)kokushiHead=1;
			if (te[32]>=1)shanten_kokushi--; if(te[32]>=2)kokushiHead=1;
			if (te[33]>=1)shanten_kokushi--; if(te[33]>=2)kokushiHead=1;
			shanten_kokushi -= kokushiHead;

			// 最小のシャンテン数を返す
			shanten = Math.min(shanten, shanten_chitoi);
			shanten = Math.min(shanten, shanten_kokushi);
		}
		return shanten;
	}

	static int selectBlock(int[] te, int men, int ta, int head, boolean shortcut) {
		if (men + ta == 4) {
			return 8 - 2 * men - ta - head;
		}
		int min_shanten = 8;

		// 頭選択
		if (head == 0) {
			List<Integer> headKouho = new ArrayList<>();
			for (int i = 0; i < 34; i++) {
				if (te[i] >= 2) {
					headKouho.add(i);
				}
			}
			for (Integer h : headKouho) {
				te[h] -= 2;
				min_shanten = Math.min(min_shanten, selectBlock(te, men, ta, head + 1, true));
				te[h] += 2;
			}
		}

		// 面子選択
		if(shortcut){
			List<int[]> mentuKouho = new ArrayList<>();
			for (int i = 0; i < 34; i++) {
				if (te[i] >= 3) {
					int[] koutu = { i, i, i };
					mentuKouho.add(koutu);
				}
				if ((0 <= i && i <= 6 || 9 <= i && i <= 15 || 18 <= i && i <= 24) && te[i] >= 1 && te[i + 1] >= 1
						&& te[i + 2] >= 1) {
					int[] shuntu = { i, i + 1, i + 2 };
					mentuKouho.add(shuntu);
				}
			}
			for (int[] m : mentuKouho) {
				te[m[0]]--;
				te[m[1]]--;
				te[m[2]]--;
				min_shanten = Math.min(min_shanten, selectBlock(te, men + 1, ta, head, true));
				te[m[0]]++;
				te[m[1]]++;
				te[m[2]]++;
			}
		}

		// ターツ選択
		List<int[]> tatuKouho = new ArrayList<>();
		for (int i = 0; i < 34; i++) {
			// トイツ
			if (te[i] >= 2) {
				int[] toitu = { i, i };
				tatuKouho.add(toitu);
			}
			// カンチャン
			if ((0 <= i && i <= 6 || 9 <= i && i <= 15 || 18 <= i && i <= 24) && te[i] >= 1 && te[i + 2] >= 1) {
				int[] kanchan = { i, i + 2 };
				tatuKouho.add(kanchan);
			}
			// ペンチャン・リャンメン
			if ((0 <= i && i <= 7 || 9 <= i && i <= 16 || 18 <= i && i <= 25) && te[i] >= 1 && te[i + 1] >= 1) {
				int[] ryanmen = { i, i + 1 };
				tatuKouho.add(ryanmen);
			}
		}
		for (int[] t : tatuKouho) {
			te[t[0]]--;
			te[t[1]]--;
			min_shanten = Math.min(min_shanten, selectBlock(te, men, ta + 1, head,false));
			te[t[0]]++;
			te[t[1]]++;
		}

		return Math.min(min_shanten, 8 - 2 * men - ta - head);
	}

}
