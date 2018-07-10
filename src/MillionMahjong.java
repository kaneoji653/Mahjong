import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MillionMahjong {
	public static void main(String[] args) {
		int cnt = 0;

		// プレイヤーの生成
		Player[] player = new Player[4];
		for (int i=0;i<4;i++) {
			player[i] = new Player("P"+(i+1), 27, 27+i);
		}
		PointManager pm = new PointManager(player);

		while (true) {
			System.out.println(++cnt + "局目");
			pm.print();
			System.out.println();
			kyokuStart(player,pm);
			System.out.println("-----------------------");
		}
	}

	public static void kyokuStart(Player[] player, PointManager pm) {
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

		for(int i=0;i<4;i++){
			player[i].initialize();
		}

		// 配牌
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				player[j].tumo(yama.remove(0));
			}
		}

		// 和了った人
		Agari agari=null;
		int total_kan = 0;
		boolean nagare9shu=false;
		boolean nagare4fuu=false;
		boolean nagare4kan=false;
		boolean nagare4reach=false;
		boolean nagashima=false;
		//////////////// 局開始/////////////////////
		// 親の第一ツモ
		int ban = 0;
		Tile tumohai = yama.remove(0);
		player[ban].tumo(tumohai);

		boolean isFirstTurn = true;
		boolean isNakiTurn = false;

		dahaiWait: while (true) {
			Player p = player[ban];
			if (isFirstTurn) {
				// 九種チェック
				if(p.is9shu()){
					if(p.ai.kyushuSelect()){
						System.out.println(p.name+"：九種九牌");
						System.out.println(p.tehaiToString());
						nagare9shu=true;
						break dahaiWait;
					}
				}
				// 四風チェック
				if(p.isOya() && p.sutehai.size()==1){
					int sute=p.sutehai.get(0).id;
					if((sute==27||sute==28||sute==29||sute==30)
							&& sute==player[(ban+1)%4].sutehai.get(0).id
							&& sute==player[(ban+2)%4].sutehai.get(0).id
							&& sute==player[(ban+3)%4].sutehai.get(0).id){
						nagare4fuu=true;
						System.out.println("四風連打");
						break dahaiWait;
					}
				}
				if (!p.sutehai.isEmpty()) {
					isFirstTurn = false;
				}
			}

			// ツモあがりする？
			if (!isNakiTurn && p.shanten==-1){
				if(Agari.agari(p,null,tumohai.id,true,new ArrayList<>()).han>=1) {
					if (p.ai.tsumoSelect()) {
//						System.out.println(p + ":ツモ！(" + tumohai + ")");
						if (yama.isEmpty()) p.isHaitei = true;
						if(isFirstTurn&&p.isOya()) p.isTenho=true;
						if(isFirstTurn&&!p.isOya()) p.isChiho=true;

						if (p.isReach) {
							for (int i = 0; i < total_kan + 1; i++) {
								dorahyouList.add(wanpai.remove(0));
							}
						}
						agari=Agari.agari(p,null, tumohai.id, true, dorahyouList);
						break dahaiWait;
					}
				}
			}
			p.isRinshan = false;

			if (!yama.isEmpty() && !isNakiTurn) {
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
							for(Player all:player) all.isIppatu=false;
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
							for(Player all:player) all.isIppatu=false;

							for (int j = 1; j <= 3; j++) {
								Player ro = player[(ban + j) % 4];
								if (ro.shanten == 0 && !ro.sutehai.contains(new Tile(id)) && ronCheck(ro, id)) {
									if (ro.ai.ronSelect()) {
										ro.isChankan = true;
//										System.out.println(ro + ":チャンカンロン！(" + id + ") 放銃：" + p);
										ro.tehai.add(new Tile(id));
										ro.te[id]++;
										if (ro.isReach) {
											for (int a = 0; a < total_kan + 1; a++) {
												dorahyouList.add(wanpai.remove(0));
											}
										}
										agari=Agari.agari(ro, p, id, false, dorahyouList);
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
			p.isIppatu=false;
			isNakiTurn = false;

			// リーチ宣言する？
			boolean isReachTurn=false;
			if (!p.isReach && yama.size() >= 4 && p.shanten == 0 && p.isMenzen) {
				if (p.ai.reachSelect()) {
					isReachTurn=true;
					p.isReach = true;
					p.isIppatu=true;
					p.isDoubleReach = isFirstTurn;
//					System.out.println(p + ":リーチ！");
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
//						System.out.println(ro + ":ロン！(" + da + ") 放銃：" + p);
						ro.tehai.add(da);
						ro.te[da.id]++;
						if (ro.isReach) {
							for (int a = 0; a < total_kan + 1; a++) {
								dorahyouList.add(wanpai.remove(0));
							}
						}
						agari=Agari.agari(ro, p, da.id, false, dorahyouList);
						break dahaiWait;
					}
				}
			}
			
			if(isReachTurn)	{
				pm.reach(p);
				if(player[0].isReach&&player[1].isReach&&player[2].isReach&&player[3].isReach){
					nagare4reach=true;
					System.out.println("四家立直");
					break dahaiWait;
				}
			}
			total_kan = player[0].num_kan + player[1].num_kan + player[2].num_kan + player[3].num_kan;
			if(total_kan==4 && !(player[0].num_kan==4 ||player[1].num_kan==4
					||player[2].num_kan==4 ||player[3].num_kan==4)){
				nagare4kan=true;
				System.out.println("四槓散了");
				break dahaiWait;
			}

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
							for(Player all:player) all.isIppatu=false;
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
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban + i) % 4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
				}

				// チーする？
				Player tg = player[(ban+1)%4];
				if (!tg.isReach) {
					// チー0する？
					if (!da.shu.equals("z") && da.kazu != 1 && da.kazu != 2 && tg.te[da.id - 2] >= 1
							&& tg.te[da.id - 1] >= 1) {
						if (tg.ai.chii0Select(da.id)) {
							tg.chii0(da.id);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
					// チー1する？
					if (!da.shu.equals("z") && da.kazu != 1 && da.kazu != 9 && tg.te[da.id - 1] >= 1
							&& tg.te[da.id + 1] >= 1) {
						if (tg.ai.chii1Select(da.id)) {
							tg.chii1(da.id);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
					// チー2する？
					if (!da.shu.equals("z") && da.kazu != 8 && da.kazu != 9 && tg.te[da.id + 1] >= 1
							&& tg.te[da.id + 2] >= 1) {
						if (tg.ai.chii2Select(da.id)) {
							tg.chii2(da.id);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban + 1) % 4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
				}
			}

			// 次のツモへ。
			if (yama.isEmpty()) {
				break;
			}
			ban = (ban+1)%4;
			tumohai = yama.remove(0);
			player[ban].tumo(tumohai);

		}
		////////////////////// 局終了////////////////////////////////////
		if(agari==null){
			System.out.println("流局");
			pm.honba++;
			if(!nagare9shu && !nagare4fuu && !nagare4kan && !nagare4reach && !nagashima){
				pm.bappu();
			}
		}else{
			agari.print();
			if(agari.isTumo){
				pm.tumo(agari);
			}else{
				pm.ron(agari);
			}
		}
	}

	static boolean ronCheck(Player ro, int id) {
		ro.tehai.add(new Tile(id));
		ro.te[id]++;
		boolean canRon = shanten(ro)==-1 && Agari.agari(ro,null,id,false,new ArrayList<>()).han>=1;

		ro.tehai.remove(ro.tehai.size() - 1);
		ro.te[id]--;
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
