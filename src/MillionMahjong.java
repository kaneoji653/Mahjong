import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MillionMahjong {
	public static void main(String[] args){
		long cnt = 1;
		while(!kyokuStart()){
			System.out.println(++cnt +"局目");
		};
	}

	public static boolean kyokuStart(){
		boolean tyankanFlg = false;

		//プレイヤーの生成
		Player[] player = new Player[4];
		for(int i=0;i<4;i++){
			player[i]= new Player("P"+(i+1));
		}

		//牌山の生成
		ArrayList<Tile> yama = new ArrayList<>(136);
		for(int id=0;id<34;id++){
			for(int i=0;i<4;i++){
				yama.add(new Tile(id));
			}
		}
		Collections.shuffle(yama);

		//王牌
		List<Tile> wanpai = new ArrayList<>();
		List<Tile> dorahyouList = new ArrayList<>();
		for(int i=0;i<14;i++){
			wanpai.add(yama.remove(0));
		}
		dorahyouList.add(wanpai.remove(0));

		//配牌
		for(int i=0;i<13;i++){
			for(int j=0;j<4;j++){
				player[j].tumo(yama.remove(0));
			}
		}


		//和了った人
		int houra=-1;
		int total_kan=0;

		////////////////局開始/////////////////////
		//親の第一ツモ
		int ban=0;
		Tile tumohai = yama.remove(0);
		player[ban].tumo(tumohai);

		dahaiWait:while(true){
			Player p = player[ban];

			//ツモあがりする？
			if(p.shanten==-1){
				if(p.ai.tsumoSelect()){
					System.out.println(p+":ツモ！("+tumohai+")");
					Util.agariEnum(p, tumohai.id, true, dorahyouList);
					houra=ban;
					break dahaiWait;
				}
			}


			if(!yama.isEmpty()){
				//暗カンする？
				for(int i=0;i<34;i++){
					if(p.te[i]==4){
						if(p.ai.ankanSelect(new Tile(i))){
							p.ankan(new Tile(i));
							total_kan++;
							dorahyouList.add(wanpai.remove(0));
							tumohai = yama.remove(0);//とりあえず山の上から引く。
							player[ban].tumo(tumohai);
							continue dahaiWait;
						}
					}
				}

				//加カンする？
				for(int i=0;i<p.num_fuuro;i++){
					Tile t=p.fuuro.get(i).pai[0];
					if(p.fuuro.get(i).type==MentuType.PON && p.tehai.contains(t)){
						if(p.ai.kakanSelect(t)){
							p.kakan(t);

							//チャンカンチェック
							for(int j=1;j<=3;j++){
								Player ro = player[(ban+j)%4];
								if(ro.shanten==0 && !ro.sutehai.contains(t) && ronCheck(ro,t)){
									if(ro.ai.ronSelect()){
										houra=(ban+j)%4;
										System.out.println(ro+":チャンカンロン！("+t+") 放銃："+p);
										ro.tehai.add(t);
										ro.te[t.id]++;
										Util.agariEnum(ro, t.id, false, dorahyouList);

										tyankanFlg = true;
										break dahaiWait;
									}
								}
							}

							tumohai = yama.remove(0);//とりあえず山の上から引く。
							player[ban].tumo(tumohai);
							continue dahaiWait;
						}
					}
				}
			}

			//打牌は？？　まだAIに委譲してない
			Tile da = p.dahai(tumohai);

			//リーチ宣言する？
			if(!p.isReach && yama.size()>=4 && p.shanten==0 && p.isMenzen){
				if(p.ai.reachSelect()){
					p.isReach = true;
					System.out.println(p+":リーチ！");
				}
			}

			//ロンする？
			for(int i=1;i<=3;i++){
				Player ro = player[(ban+i)%4];
				if(ro.shanten==0 && !ro.sutehai.contains(da) && ronCheck(ro,da)){
					if(ro.ai.ronSelect()){
						System.out.println(ro+":ロン！("+da+") 放銃："+p);
						ro.tehai.add(da);
						ro.te[da.id]++;
						Util.agariEnum(ro, da.id, false, dorahyouList);

						houra=(ban+i)%4;
						break dahaiWait;
					}
				}
			}

			total_kan=player[0].num_kan+player[1].num_kan+player[2].num_kan+player[3].num_kan;
			
			while(dorahyouList.size()!=1+total_kan){
				dorahyouList.add(wanpai.remove(0));
			}

			if(!yama.isEmpty()){
				//大明カンする？
				for(int i=1;i<=3;i++){
					Player tg = player[(ban+i)%4];
					if(tg.isReach) continue;
					if(tg.te[da.id]==3){
						if(tg.ai.minkanSelect(da)){
							tg.minkan(da);
							ban = (ban+i)%4;

							tumohai = yama.remove(0);//とりあえず山の上から引くことにしてる。
							player[ban].tumo(tumohai);
							continue dahaiWait;
						}
					}
				}

				//ポンする？
				for(int i=1;i<=3;i++){
					Player tg = player[(ban+i)%4];
					if(tg.isReach) continue;
					if(tg.te[da.id]>=2){
						if(tg.ai.ponSelect(da)){
							tg.pon(da);
							ban = (ban+i)%4;
							continue dahaiWait;
						}
					}
				}

				//チーする？
				Player tg = player[(ban+1)%4];
				if(!tg.isReach){
					//チー0する？
					if(!da.shu.equals("z") && da.kazu!=1 && da.kazu!=2 && tg.te[da.id-2]>=1 && tg.te[da.id-1]>=1){
						if(tg.ai.chii0Select(da)){
							tg.chii0(da);
							ban = (ban+1)%4;
							continue dahaiWait;
						}
					}
					//チー1する？
					if(!da.shu.equals("z") && da.kazu!=1 && da.kazu!=9 && tg.te[da.id-1]>=1 && tg.te[da.id+1]>=1){
						if(tg.ai.chii1Select(da)){
							tg.chii1(da);
							ban = (ban+1)%4;
							continue dahaiWait;
						}
					}
					//チー2する？
					if(!da.shu.equals("z") && da.kazu!=8 && da.kazu!=9 && tg.te[da.id+1]>=1 && tg.te[da.id+2]>=1){
						if(tg.ai.chii2Select(da)){
							tg.chii2(da);
							ban = (ban+1)%4;
							continue dahaiWait;
						}
					}
				}
			}

			//次のツモへ。
			if(yama.isEmpty()) break;
			ban=(ban+1)%4;
			tumohai = yama.remove(0);
			player[ban].tumo(tumohai);
		}

		//////////////////////局終了////////////////////////////////////

		if(houra==-1){
			System.out.println("流局");
		}else{

		}
		System.out.println("-----------------------");
//		for(int i=0;i<4;i++){
//			System.out.println("P"+(i+1));
//			player[i].printTehai();
//			System.out.println(shanten(player[i])+"向聴");
//			System.out.println();
//		}

		return tyankanFlg;
	}

	static boolean ronCheck(Player p, Tile t){
		p.tehai.add(t);
		p.te[t.id]++;
		boolean canRon = (shanten(p)==-1);
		p.tehai.remove(p.tehai.size()-1);
		p.te[t.id]--;
		return canRon;
	}

	static int shanten(Player p){
		int[] te=p.te;

		//普通手のシャンテン
		int shanten = selectBlock(te,p.num_fuuro,0,0);

		//メンゼンならチートイと国士のシャンテン計算
		if(p.num_fuuro==0){

			//七対子のシャンテン
			int shanten_chitoi = 6;
			if(p.num_fuuro==0){
				for(int i=0;i<34;i++){
					if(te[i]>=2)shanten_chitoi--;
				}
			}

			//国士無双のシャンテン
			int shanten_kokushi = 14;
			int kokushiHead = 0;
			if(te[0]>=1)shanten_kokushi--; if(te[0]>=2)kokushiHead=1;
			if(te[8]>=1)shanten_kokushi--; if(te[8]>=2)kokushiHead=1;
			if(te[9]>=1)shanten_kokushi--; if(te[9]>=2)kokushiHead=1;
			if(te[17]>=1)shanten_kokushi--; if(te[17]>=2)kokushiHead=1;
			if(te[18]>=1)shanten_kokushi--; if(te[18]>=2)kokushiHead=1;
			if(te[26]>=1)shanten_kokushi--; if(te[26]>=2)kokushiHead=1;
			if(te[27]>=1)shanten_kokushi--; if(te[27]>=2)kokushiHead=1;
			if(te[28]>=1)shanten_kokushi--; if(te[28]>=2)kokushiHead=1;
			if(te[29]>=1)shanten_kokushi--; if(te[29]>=2)kokushiHead=1;
			if(te[30]>=1)shanten_kokushi--; if(te[30]>=2)kokushiHead=1;
			if(te[31]>=1)shanten_kokushi--; if(te[31]>=2)kokushiHead=1;
			if(te[32]>=1)shanten_kokushi--; if(te[32]>=2)kokushiHead=1;
			if(te[33]>=1)shanten_kokushi--; if(te[33]>=2)kokushiHead=1;
			shanten_kokushi-=kokushiHead;

			//最小のシャンテン数を返す
			shanten=Math.min(shanten, shanten_chitoi);
			shanten=Math.min(shanten, shanten_kokushi);
		}
		return shanten;
	}

	static int selectBlock(int[] te, int men, int ta, int head){
		if(men+ta==4) return 8-2*men-ta-head;
		int min_shanten=8;

		//頭選択
		if(head==0){
			List<Integer> headKouho = new ArrayList<>();
			for(int i=0;i<34;i++){
				if(te[i]>=2){
					headKouho.add(i);
				}
			}
			for(Integer h:headKouho){
				te[h]-=2;
				min_shanten=Math.min(min_shanten, selectBlock(te, men, ta, head+1));
				te[h]+=2;
			}
		}

		//面子選択
		List<int[]> mentuKouho = new ArrayList<>();
		for(int i=0;i<34;i++){
			if(te[i]>=3){
				int[] koutu = {i,i,i};
				mentuKouho.add(koutu);
			}
			if( (0<=i&&i<=6 || 9<=i&&i<=15 ||18<=i&&i<=24) && (te[i]>=1 && te[i+1]>=1 && te[i+2]>=1)){
				int[] shuntu = {i,i+1,i+2};
				mentuKouho.add(shuntu);
			}
		}
		for(int[] m:mentuKouho){
			te[m[0]]--; te[m[1]]--; te[m[2]]--;
			min_shanten=Math.min(min_shanten, selectBlock(te, men+1, ta, head));
			te[m[0]]++; te[m[1]]++; te[m[2]]++;
		}

		//ターツ選択
		List<int[]> tatuKouho = new ArrayList<>();
		for(int i=0;i<34;i++){
			//トイツ
			if(te[i]>=2){
				int[] toitu = {i,i};
				tatuKouho.add(toitu);
			}
			//カンチャン
			if( (0<=i&&i<=6 || 9<=i&&i<=15 ||18<=i&&i<=24) && (te[i]>=1 && te[i+2]>=1)){
				int[] kanchan = {i,i+2};
				tatuKouho.add(kanchan);
			}
			//ペンチャン・リャンメン
			if( (0<=i&&i<=7 || 9<=i&&i<=16 ||18<=i&&i<=25) && (te[i]>=1 && te[i+1]>=1)){
				int[] ryanmen = {i,i+1};
				tatuKouho.add(ryanmen);
			}
		}
		for(int[] t:tatuKouho){
			te[t[0]]--; te[t[1]]--;
			min_shanten=Math.min(min_shanten, selectBlock(te, men, ta+1, head));
			te[t[0]]++; te[t[1]]++;
		}

		return Math.min(min_shanten, 8-2*men-ta-head);
	}

}

class Player{
	int[] te = new int[34];
	List<Tile> tehai = new ArrayList<>();
	List<Tile> sutehai = new ArrayList<>();
	List<Fuuro> fuuro = new ArrayList<>();
	int num_fuuro = 0;
	int num_kan = 0;
	int shanten;

	int jikaze=30;//未
	int bakaze=27;//未
	boolean isReach=false;
	boolean isDoubleReach=false; //未実装
	boolean isIppatu=false; //未実装
	boolean isHaitei=false; //未実装
	boolean isHoutei=false; //未実装
	boolean isRinshan=false; //未実装
	boolean isChankan=false; //未実装

	boolean isMenzen=true;
	String name;
	PlayerAI ai;

	Player(String name){
		this.name=name;
		ai = new PlayerAI();
	}

	class Fuuro{
		MentuType type;
		Tile[] pai;
		Fuuro(MentuType type){
			this.type=type;
			switch(type){
			case MINKAN:	this.pai = new Tile[4]; break;
			case ANKAN:		this.pai = new Tile[4]; break;
			default: 		this.pai = new Tile[3]; break;
			}
		}

		public String toString(){
			String str=this.type==MentuType.ANKAN?"[":"(";
			for(int i=0;i<pai.length;i++){
				str+=pai[i].kazu;
			}
			str+=pai[0].shu+(this.type==MentuType.ANKAN?"]":")");
			return str;
		}
	}

	//	一枚ツモる
	public void tumo(Tile tile){
		tehai.add(tile);
		te[tile.id]++;
		this.ripai();
		this.shanten = MillionMahjong.shanten(this);
	}

	public Tile dahai(Tile tumohai){
		Tile da = isReach ? tumohai : selectDahai();
		tehai.remove(da);
		te[da.id]--;
		sutehai.add(da);
		return da;
	}

	//捨て牌選択
	public Tile selectDahai(){
		List<Tile> dahaiKouho = new ArrayList<>();
		for(int i=0;i<tehai.size();i++){
			Tile t=tehai.remove(i);
			te[t.id]--;
			if(shanten==MillionMahjong.shanten(this)){
				dahaiKouho.add(t);
			}
			tehai.add(i,t);
			te[t.id]++;
		}
		if(dahaiKouho.isEmpty()) dahaiKouho.addAll(tehai);

		return dahaiKouho.get((int)(Math.random()*dahaiKouho.size()));
	}

	void minkan(Tile t){
		num_kan++;
//		System.out.println(this.name+"：カン！("+t+")");
		te[t.id]-=3;
		Fuuro f=new Fuuro(MentuType.MINKAN);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		f.pai[3] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
		isMenzen=false;
	}

	void kakan(Tile t){
		num_kan++;
//		System.out.println(this.name+"：カン！("+t+")");
		Fuuro f=null;
		for(int i=0;i<num_fuuro;i++){
			f=fuuro.get(i);
			if(f.type==MentuType.PON && f.pai[0].equals(t)){
				break;
			}
		}
		f.type=MentuType.MINKAN;
		te[t.id]--;
		Tile[] kan=new Tile[4];
		kan[0] = f.pai[0];
		kan[1] = f.pai[1];
		kan[2] = f.pai[2];
		kan[3] = tehai.remove(tehai.indexOf(t));
		f.pai = kan;
	}

	void ankan(Tile t){
		num_kan++;
//		System.out.println(this.name+"：カン！("+t+")");
		te[t.id]-=4;
		Fuuro f=new Fuuro(MentuType.ANKAN);
		f.pai[0] = tehai.remove(tehai.indexOf(t));
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		f.pai[3] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
	}

	void pon(Tile t){
//		System.out.println(this.name+"：ポン！("+t+")");
		te[t.id]-=2;
		Fuuro f=new Fuuro(MentuType.PON);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(t));
		f.pai[2] = tehai.remove(tehai.indexOf(t));
		fuuro.add(f);
		num_fuuro++;
		isMenzen=false;
	}

	void chii0(Tile t){
//		System.out.println(this.name+"：チー！("+t+")");
		te[t.id-2]--; te[t.id-1]--;
		Fuuro f=new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id-2)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id-1)));
		fuuro.add(f);
		num_fuuro++;
		isMenzen=false;
	}

	void chii1(Tile t){
//		System.out.println(this.name+"：チー！("+t+")");
		te[t.id-1]--; te[t.id+1]--;
		Fuuro f=new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id-1)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id+1)));
		fuuro.add(f);
		num_fuuro++;
		isMenzen=false;
	}

	void chii2(Tile t){
//		System.out.println(this.name+"：チー！("+t+")");
		te[t.id+1]--; te[t.id+2]--;
		Fuuro f=new Fuuro(MentuType.CHI);
		f.pai[0] = t;
		f.pai[1] = tehai.remove(tehai.indexOf(new Tile(t.id+1)));
		f.pai[2] = tehai.remove(tehai.indexOf(new Tile(t.id+2)));
		fuuro.add(f);
		num_fuuro++;
		isMenzen=false;
	}

	void ripai(){
		this.tehai.sort((t1,t2)->t1.id-t2.id);
	}

	void printTehai(){
		ripai();
		String str="";
		for(int i=0;i<tehai.size();i++){
			if(i>=1&&( !tehai.get(i-1).shu.equals(tehai.get(i).shu) )) str+=tehai.get(i-1).shu;
			str+=tehai.get(i).kazu;
		}
		str+=tehai.get(tehai.size()-1).shu;
		for(int i=num_fuuro-1;i>=0;i--){
			str+=fuuro.get(i);
		}
		System.out.println(str);
	}

	public String toString(){
		return this.name;
	}
}


