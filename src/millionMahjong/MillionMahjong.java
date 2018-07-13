package millionMahjong;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myAI.KaneojiAI000;
import myAI.KaneojiAI001;
import myAI.ZettaiNakuMan;

public class MillionMahjong {
	public static void main(String[] args) {
		// プレイヤーの生成
		Player[] player = new Player[4];
		for (int i=0;i<4;i++) {
			player[i] = new Player("P"+(i+1), 27, 27+i);
		}
		
		player[0].setAI(new KaneojiAI001(player[0]));
		player[1].setAI(new KaneojiAI001(player[1]));
		player[2].setAI(new ZettaiNakuMan(player[2]));
		player[3].setAI(new KaneojiAI000(player[3]));
		
		PointManager pm = new PointManager(player);
		MessageManager mm = new MessageManager(false,false);

		int cnt=0;
		while(++cnt<=3000){
			if(cnt%100==0){
				System.out.println(cnt+"試合");
				pm.printScore();
			}
			pm.initialize();
			int kyoku = 0;
			while(kyoku<8 || kyoku<12&&pm.isShaNyu()) {
				boolean tokushuNagare = !kyokuStart(player,pm,kyoku,mm);
				if(pm.existTobi()) break;
				if(pm.honba==0)kyoku++;
				if(kyoku==7 && pm.honba>=1 && !tokushuNagare && !pm.isShaNyu() && pm.rank(3)==1) break;
			}
			if(mm.useLog) pm.printPoint();
			pm.scoreUpdate();
		}
		pm.printScore();
	}

	public static boolean kyokuStart(Player[] player, PointManager pm, int kyoku, MessageManager mm) {
		if(mm.useLog){
			switch(kyoku/4){
			case 0: System.out.println("東"+(1+kyoku%4)+"局　"+pm.honba+"本場　供託："+pm.kyotaku ); break;
			case 1: System.out.println("南"+(1+kyoku%4)+"局　"+pm.honba+"本場　供託："+pm.kyotaku ); break;
			case 2: System.out.println("西"+(1+kyoku%4)+"局　"+pm.honba+"本場　供託："+pm.kyotaku ); break;
			}
			pm.printPoint();
			System.out.println();
		}

		// 牌山の生成
		ArrayList<Integer> yama = new ArrayList<>(136);
		for (int id=0;id<34;id++) {
			for (int i=0;i<4;i++) {
				yama.add(id);
			}
		}
		Collections.shuffle(yama);

		// 王牌
		List<Integer> wanpai = new ArrayList<>();
		List<Integer> doraList = new ArrayList<>();
		List<Integer> uraList = new ArrayList<>();
		for (int i=0;i<14;i++) {
			wanpai.add(yama.remove(0));
		}
		doraList.add(wanpai.remove(0));
		uraList.add(wanpai.remove(0));

		for(int i=0;i<4;i++){
			player[i].initialize();
		}

		// 配牌
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				player[j].tumo(yama.remove(0));
			}
		}

		Agari agari=null;
		int total_kan = 0;
		boolean isNormalRyukyoku=false;
		boolean isNakiTurn = false;
		boolean isFirstTurn = true;
		int ban = kyoku%4;
		for(int i=0;i<4;i++){
			player[(ban+i)%4].bakaze=27+kyoku/4;
			player[(ban+i)%4].jikaze=27+i;
		}

		//////////////// 局開始/////////////////////

		// 親の第一ツモ
		int tumohai = yama.remove(0);
		player[ban].tumo(tumohai);

		dahaiWait:while(true){
			//切り番のプレイヤー（１枚ツモった状態）
			Player p=player[ban];

			//１巡目のみ
			if(isFirstTurn){
				// 九種チェック
				if(p.tm.is9shu()){
					if(p.ai.kyushuSelect()){
						mm.log(p.name+"：九種九牌");
						break dahaiWait;
					}
				}
				// 四風チェック
				if(p.isOya() && p.sutehai.size()==1){
					int sute=p.sutehai.get(0);
					if((sute==27||sute==28||sute==29||sute==30)
							&& sute==player[(ban+1)%4].sutehai.get(0)
							&& sute==player[(ban+2)%4].sutehai.get(0)
							&& sute==player[(ban+3)%4].sutehai.get(0)){
						mm.log("四風連打");
						break dahaiWait;
					}
				}
				//２巡目なら何か捨て牌がある
				if (!p.sutehai.isEmpty()) {
					isFirstTurn = false;
				}
			}

			//ツモ和了判定
			if (!isNakiTurn && p.tm.shantenUpdate()==-1 ){
				p.isHaitei = yama.isEmpty();
				p.isTenho = isFirstTurn&&p.isOya();
				p.isChiho = isFirstTurn&&!p.isOya();
				Agari a=Agari.agari(p,null,tumohai,null,null);
				if(a.han>=1||a.num_yakuman>=1){
					if (p.ai.tsumoSelect()) {
						mm.hassei(p.name+":ツモ！("+tumohai+")");
						agari=Agari.agari(p,null, tumohai, doraList, (p.isReach?uraList:null));
						break dahaiWait;
					}
				}
			}
			p.isTenho = false;
			p.isChiho = false;
			p.isRinshan = false;

			//カン判定
			if (!yama.isEmpty() && !isNakiTurn) {
				// 暗カン
				for (int i = 0; i < 34; i++) {
					if (p.tm.te[i] == 4) {
						if (p.ai.ankanSelect(i)) {
							p.ankan(i);
							total_kan++;
							isFirstTurn = false;
							doraList.add(wanpai.remove(0));
							uraList.add(wanpai.remove(0));
							tumohai = yama.remove(0);
							player[ban].tumo(tumohai);
							p.isRinshan = true;
							for(Player all:player) all.isIppatu=false;
							continue dahaiWait;
						}
					}
				}

				// 加カン
				for (int i = 0; i < p.num_fuuro; i++) {
					int id = p.fuuro.get(i).pai[0];
					if (p.fuuro.get(i).type == MentuType.PON && p.tm.te[id] == 1) {
						if (p.ai.kakanSelect(id)) {
							p.kakan(id);
							for(Player all:player) all.isIppatu=false;

							//チャンカン判定
							for(int j=1;j<=3;j++){
								Player ro = player[(ban+j)%4];
								ro.tm.te[id]++;
								ro.isChankan = true;
								if (ro.tm.shantenUpdate()==-1 && !ro.sutehai.contains(id)) {
									Agari a=Agari.agari(ro,p,id,null,null);
									if(a.han>=1||a.num_yakuman>=1){
										if (ro.ai.ronSelect()) {
											mm.hassei(ro + ":チャンカンロン！(" + id + ") 放銃：" + p.name);
											agari=Agari.agari(ro, p, id, doraList, (ro.isReach?uraList:null));
											break dahaiWait;
										}
									}
								}
								ro.isChankan=false;
								ro.tm.te[id]--;
							}
							tumohai = yama.remove(0);
							player[ban].tumo(tumohai);
							p.isRinshan = true;
							continue dahaiWait;
						}
					}
				}
			}

			//リーチ判定
			boolean isReachTurn=false;
			if (!p.isReach && yama.size() >= 4 && p.tm.shantenUpdate() <= 0 && p.isMenzen && p.point>=1000) {
				if (p.ai.reachSelect()) {
					isReachTurn=true;
					p.isReach = true;
					p.isIppatu=true;
					p.isDoubleReach = isFirstTurn;
					mm.hassei(p + ":リーチ！");
				}
			}

			//打牌決定
			int da = p.dahai(tumohai,isReachTurn);
			if(!isReachTurn)p.isIppatu=false;
			isNakiTurn = false;

			//ロン判定
			for (int i=1;i<=3;i++) {
				Player ro =player[(ban+i)%4];
				ro.tm.te[da]++;
				ro.isHoutei = yama.isEmpty();
				if(ro.tm.shantenUpdate()==-1 && !ro.sutehai.contains(da)){
					Agari a=Agari.agari(ro,p,da,null,null);
					if(a.han>=1||a.num_yakuman>=1){
						ro.tm.te[da]--;
						if (ro.ai.ronSelect()) {
							ro.tm.te[da]++;
							mm.hassei(ro + ":ロン！(" + da + ") 放銃：" + p);
							agari=Agari.agari(ro, p, da, doraList, (ro.isReach?uraList:null));
							break dahaiWait;
						}
					}
				}
				ro.tm.te[da]--;
			}

			//ロンされなかったら、明カンとリーチ成立
			total_kan = player[0].num_kan + player[1].num_kan + player[2].num_kan + player[3].num_kan;
			while (doraList.size() < 1+total_kan) {
				doraList.add(wanpai.remove(0)); //カンドラ
				uraList.add(wanpai.remove(0)); //カン裏ドラ
			}
			if(isReachTurn)	pm.reach(p);

			//四家立直チェック
			if(isReachTurn)	{
				if(player[0].isReach&&player[1].isReach&&player[2].isReach&&player[3].isReach){
					mm.log("四家立直");
					break dahaiWait;
				}
			}

			//四槓散了チェック
			if(total_kan==5 || total_kan==4 
					&& !(player[0].num_kan==4 ||player[1].num_kan==4||player[2].num_kan==4 ||player[3].num_kan==4)){
				mm.log("四槓散了");
				break dahaiWait;
			}

			//山がなければ流局
			if (yama.isEmpty()){
				isNormalRyukyoku=true;
				break dahaiWait;
			}

			//山があったら鳴き判定
			if (!yama.isEmpty()) {
				//大明カン
				for (int i=1;i<=3;i++) {
					Player tg = player[(ban+i)%4];
					if (!tg.isReach && tg.tm.te[da]==3) {
						if (tg.ai.minkanSelect(da)) {
							tg.minkan(da);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban =(ban+i)%4;
							tumohai = yama.remove(0);
							player[ban].tumo(tumohai);
							tg.isRinshan = true;
							continue dahaiWait;
						}
					}
				}

				//ポン
				for (int i=1;i<=3;i++) {
					Player tg = player[(ban+i)%4];
					if (!tg.isReach && tg.tm.te[da]>=2){
						if (tg.ai.ponSelect(da)) {
							tg.pon(da);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban=(ban+i)%4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
				}

				// チー
				Player tg = player[(ban+1)%4];
				if (!tg.isReach) {
					//12(3)の形
					if (da<27 && da%9!=0 && da%9!=1 && tg.tm.te[da-2]>=1 && tg.tm.te[da-1]>=1) {
						if (tg.ai.chi0Select(da)) {
							tg.chi0(da);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban+1)%4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
					//1(2)3の形
					if (da<27 && da%9!=0 && da%9!=8 && tg.tm.te[da-1]>=1 && tg.tm.te[da+1]>=1) {
						if (tg.ai.chi1Select(da)) {
							tg.chi1(da);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban+1)%4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
					//(1)23の形
					if (da<27 && da%9!=7 && da%9!=8 && tg.tm.te[da+1]>=1 && tg.tm.te[da+2]>=1) {
						if (tg.ai.chi2Select(da)) {
							tg.chi2(da);
							for(Player all:player) all.isIppatu=false;
							isFirstTurn = false;
							ban = (ban+1)%4;
							isNakiTurn = true;
							continue dahaiWait;
						}
					}
				}
			}

			// 次のツモへ。
			ban = (ban+1)%4;
			tumohai = yama.remove(0);
			player[ban].tumo(tumohai);
		}

		////////////////////// 局終了////////////////////////////////////

		//点棒移動
		if(agari==null){
			if(mm.useLog){
				System.out.println("流局");
				System.out.println("-----------------------");
			}
			pm.honba++;
			if(isNormalRyukyoku){
				pm.bappu();
			}
		}else{
			if(mm.useLog){
				agari.print();
				System.out.println("-----------------------");
			}
			if(agari.isTumo){
				pm.tumo(agari);
			}else{
				pm.ron(agari);
			}
		}
		
		return isNormalRyukyoku;
	}
}
