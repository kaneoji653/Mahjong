import java.util.ArrayList;
import java.util.List;

public class Util {
//	static boolean isYao9(int id){
//		for(int i=0;i<yao9.length;i++){
//			if(i==id)return true;
//		}
//		return false;
//	}

	public static List<Agari> agariEnum(Player player, int agarihai, boolean isTumo, List<Tile> dorahyouList){
		player.printTehai();
		int[] te = player.te;
		List<Agari> agari = new ArrayList<>();

		//頭選択
		List<Integer> headKouho = new ArrayList<>();
		for(int i=0;i<34;i++){
			if(te[i]>=2){
				headKouho.add(i);
			}
		}
		for(Integer head:headKouho){
			List<MatiType> matiKouho=new ArrayList<>();
			if(agarihai==head) matiKouho.add(MatiType.TANKI);
			boolean isPinfu = (player.num_fuuro==0 && !(head==player.bakaze||head==player.jikaze||head==31||head==32||head==33));

			te[head]-=2;

			//面子分解(前から見る。3枚あるなら暗刻として除去、1枚あるなら順子として除去(できないなら終了))
			Mentu[] mentu4=new Mentu[4];
			int m_cnt=0;
			for(int i=0;i<34;i++){
				//刻子判定
				if(te[i]>=3){
					int[] pai={i,i,i};
					mentu4[m_cnt]=new Mentu(MentuType.ANKO, pai);
					te[i]-=3;
					m_cnt++;
					isPinfu=false;
					if(agarihai==i){
						matiKouho.add(MatiType.SHAMPON);
					}
				}
				//順子判定
				while(te[i]>=1){
					//取れないなら終了
					if( !((0<=i&&i<=6)||(9<=i&&i<=15)||(18<=i&&i<=24)) || (te[i+1]==0||te[i+2]==0) ) break;
					else{
						int[] pai={i,i+1,i+2};
						mentu4[m_cnt]=new Mentu(MentuType.SHUN, pai);
						te[i]--; te[i+1]--; te[i+2]--;
						m_cnt++;
						if(agarihai==i)matiKouho.add( (i==6||i==15||i==24)? MatiType.PENCHAN : MatiType.RYAMMEN);
						if(agarihai==i+1)matiKouho.add( MatiType.KANCHAN);
						if(agarihai==i+2)matiKouho.add( (i==0||i==9||i==18)? MatiType.PENCHAN : MatiType.RYAMMEN);
					}
				}
			}

			//4面子取れたら、待ちごとにAgariインスタンス生成
			if(m_cnt+player.num_fuuro==4){
				//フーロを変換（Tile→intは無駄？）
				for(int i=0;i<player.num_fuuro;i++){
					Tile[] ttt=player.fuuro.get(i).pai;
					int[] pai=new int [ttt.length];
					for(int j=0;j<pai.length;j++){
						pai[j]=ttt[j].id;
					}
					mentu4[3-i]=new Mentu(player.fuuro.get(i).type, pai);
				}

				//待ち限定
				if(matiKouho.size()>=2){
					matiKouho.remove(MatiType.SHAMPON);
				}
				if(matiKouho.size()>=2){
					if(matiKouho.contains(MatiType.RYAMMEN) && isPinfu){
						matiKouho.clear();
						matiKouho.add(MatiType.RYAMMEN);
					}else{
						matiKouho.remove(MatiType.RYAMMEN);
						while(matiKouho.size()>=2){
							matiKouho.remove(matiKouho.size()-1);
						}
					}
				}
				MatiType mati = matiKouho.get(0);

				//刻子をロンは明刻
				if(!isTumo&&mati==MatiType.SHAMPON){
					for(int i=0;i<4;i++){
						if(mentu4[i].type==MentuType.ANKO && mentu4[i].pai[0]==agarihai){
							mentu4[i].type=MentuType.PON;
						}
					}
				}

				//あがり生成
				agari.add(new Agari(player,head,mentu4,mati,isTumo,dorahyouList));
			}

			//手牌を戻す
			te[head]+=2;
			for(int i=0;i<m_cnt;i++){
				te[ mentu4[i].pai[0] ]++;
				te[ mentu4[i].pai[1] ]++;
				te[ mentu4[i].pai[2] ]++;
			}
		}

		//あがり候補のスコアを計算して、最大値をとる。(未)
		if(agari.isEmpty()){
			System.out.println("ちーといorこくし");
			System.out.println(player.tehai);
		}
//		for(Agari a:agari){
//			System.out.println(a);
//		}
		return agari;
	}
}




enum MentuType{
	SHUN,
	CHI,
	ANKO,
	PON,
	ANKAN,
	MINKAN,
}

class Mentu{
	MentuType type;
	int[] pai;
	Mentu(MentuType type, int[] pai){
		this.type=type;
		this.pai=pai;
	}

	public String toString(){
		if(type==MentuType.PON||type==MentuType.CHI||type==MentuType.MINKAN){
			return "("+pai[0]+","+pai[1]+","+pai[2]+(type==MentuType.MINKAN?","+pai[3]:"")+")";
		}else{
			return "["+pai[0]+","+pai[1]+","+pai[2]+(type==MentuType.ANKAN?","+pai[3]:"")+"]";
		}
	}

	boolean contains(int t){
		if(pai[0]==t||pai[1]==t||pai[2]==t)return true;
		else return false;
	}
}

class Agari{
	final static int[] yao9={0,8,9,17,18,26,27,28,29,30,31,32,33};
	public boolean isYao9(int id){
		return !((1<=id&&id<=7)||(10<=id&&id<=16)||(19<=id&&id<=25));
	}
	
	boolean[] yaku=new boolean[36];
	static final int[] yakuHan={
			1,2,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,2,1,2,2,
			2,1,2,2,2,1,2,2,3,2,
			3,2,2,2,6,5};
	static final String[] yakuMei={
			"リーチ","ダブルリーチ","一発","ハイテイ","ホウテイ","リンシャン","チャンカン","ツモ","自風",
			"場風","白","發","中","タンヤオ","ピンフ","イーペーコー","三色同順","三色同順",
			"三色同刻","三暗刻","一気通貫","一気通貫","七対子","トイトイ","チャンタ","チャンタ","三槓子",
			"リャンペーコー","純チャン","純チャン","ホンイツ","ホンイツ","小三元","ホンロートー","チンイツ","チンイツ"};
	
	Player p;
	int head;
	Mentu[] mentu=new Mentu[4];
	MatiType mati;
	boolean isTumo;
	int num_dora=0;
	
	boolean isChitoi=false;
	int[] chitoiPair=new int[7];
	boolean isKokushi=false;

	Agari(Player p,int head,Mentu[] mentu,MatiType mati, boolean isTumo, List<Tile> dorahyouList){
		this.p=p;
		this.head=head;
		this.mentu=mentu;
		this.mati=mati;
		this.isTumo=isTumo;
		this.makeYaku();
		countDora(dorahyouList);
		this.printYaku();
	}

	public String toString(){
		String str = String.format("[%d,%d]",head,head);
		str+=mentu[0].toString();
		str+=mentu[1].toString();
		str+=mentu[2].toString();
		str+=mentu[3].toString();
		str+=isTumo?"TSUMO,":"RON,";
		str+=mati.toString();
		return str;
	}

	void makeYaku(){
		List<Integer> shunList=new ArrayList<>();
		List<Integer> kouList=new ArrayList<>();
		int cnt_anko=0;
		int cnt_kan=0;
		int cnt_manzu=0;
		int cnt_pinzu=0;
		int cnt_souzu=0;
		int cnt_jihai=0;
		int cnt_19=0;
		int[] te=new int[34];

		te[head]+=2;
		for(int i=0;i<4;i++){
			for(int j=0;j<mentu[i].pai.length;j++){
				int pai=mentu[i].pai[j];
				te[pai]++;
				if(pai==0||pai==8||pai==9||pai==17||pai==18||pai==26) cnt_19++;
				switch(pai/9){
				case 0:cnt_manzu++; break;
				case 1:cnt_pinzu++; break;
				case 2:cnt_souzu++; break;
				case 3:cnt_jihai++; break;
				}
			}
			if(mentu[i].type==MentuType.SHUN||mentu[i].type==MentuType.CHI){
				shunList.add(Math.min(mentu[i].pai[0],mentu[i].pai[1]));
			}else{
				kouList.add(mentu[i].pai[0]);
			}
			if(mentu[i].type==MentuType.ANKO||mentu[i].type==MentuType.ANKAN){
				cnt_anko++;
			}
			if(mentu[i].type==MentuType.ANKAN||mentu[i].type==MentuType.MINKAN){
				cnt_kan++;
			}
		}
		p.te=te;

		yaku[0]=p.isReach;
		yaku[1]=p.isDoubleReach; if(yaku[1]) yaku[0]=false;
		yaku[2]=p.isIppatu;
		yaku[3]=p.isHaitei;
		yaku[4]=p.isHoutei;
		yaku[5]=p.isRinshan;
		yaku[6]=p.isChankan;
		yaku[7]=p.isMenzen&&this.isTumo;
		yaku[8]=kouList.contains(p.jikaze);
		yaku[9]=kouList.contains(p.bakaze);
		yaku[10]=kouList.contains(31);
		yaku[11]=kouList.contains(32);
		yaku[12]=kouList.contains(33);
		yaku[13]=(cnt_19+cnt_jihai==0);
		yaku[14] = p.isMenzen && mati==MatiType.RYAMMEN
				&& head!=p.jikaze && head!=p.bakaze
				&& head!=31 && head!=32 && head!=33
				&& mentu[0].type==MentuType.SHUN
				&& mentu[1].type==MentuType.SHUN
				&& mentu[2].type==MentuType.SHUN
				&& mentu[3].type==MentuType.SHUN;
		yaku[15] = p.isMenzen &&(
				(mentu[0].type==MentuType.SHUN && mentu[1].type==MentuType.SHUN && mentu[0].pai[0]==mentu[1].pai[0])
				||(mentu[1].type==MentuType.SHUN && mentu[2].type==MentuType.SHUN && mentu[1].pai[0]==mentu[2].pai[0])
				||(mentu[2].type==MentuType.SHUN && mentu[3].type==MentuType.SHUN && mentu[2].pai[0]==mentu[3].pai[0]));
		for(int i=0;i<=6;i++){
			if(shunList.contains(i)&&shunList.contains(i+9)&&shunList.contains(i+18)){
				if(p.isMenzen){
					yaku[16]=true;
				}else{
					yaku[17]=true;
				}
			}
		}
		for(int i=0;i<9;i++){
			if(kouList.contains(i)&&kouList.contains(i+9)&&kouList.contains(i+18)){
				yaku[18]=true;
			}
		}
		yaku[19]=(cnt_anko==3);
		if(	shunList.contains(0)&&shunList.contains(3)&&shunList.contains(6)
			||shunList.contains(9)&&shunList.contains(12)&&shunList.contains(15)
			||shunList.contains(18)&&shunList.contains(21)&&shunList.contains(24)){
			if(p.isMenzen){
				yaku[20]=true;
			}else{
				yaku[21]=true;
			}
		}
		yaku[22]=isChitoi;
		yaku[23]=(kouList.size()==4);
		if(isYao9(head) 
				&& (isYao9(mentu[0].pai[0])||isYao9(mentu[0].pai[1])||isYao9(mentu[0].pai[2]))
				&& (isYao9(mentu[1].pai[0])||isYao9(mentu[1].pai[1])||isYao9(mentu[1].pai[2]))
				&& (isYao9(mentu[2].pai[0])||isYao9(mentu[2].pai[1])||isYao9(mentu[2].pai[2]))
				&& (isYao9(mentu[3].pai[0])||isYao9(mentu[3].pai[1])||isYao9(mentu[3].pai[2]))){
			if(cnt_jihai!=0){
				if(p.isMenzen){
					yaku[24]=true;
				}else{
					yaku[25]=true;
				}
			}else{
				if(p.isMenzen){
					yaku[28]=true;
				}else{
					yaku[29]=true;
				}				
			}
		}
		yaku[26]=(cnt_kan==3);
		yaku[27] = p.isMenzen &&(
				(mentu[0].type==MentuType.SHUN && mentu[1].type==MentuType.SHUN && mentu[0].pai[0]==mentu[1].pai[0])
				&&(mentu[2].type==MentuType.SHUN && mentu[3].type==MentuType.SHUN && mentu[2].pai[0]==mentu[3].pai[0]));
		if(   (cnt_manzu==0 && cnt_pinzu==0 && cnt_souzu!=0)
			||(cnt_manzu==0 && cnt_pinzu!=0 && cnt_souzu==0)
			||(cnt_manzu!=0 && cnt_pinzu==0 && cnt_souzu==0)){
			if(cnt_jihai!=0){
				if(p.isMenzen){
					yaku[30]=true;
				}else{
					yaku[31]=true;
				}
			}else{
				if(p.isMenzen){
					yaku[34]=true;
				}else{
					yaku[35]=true;
				}				
			}
		}
		if(head==31 && kouList.contains(32) && kouList.contains(33)
			||head==32 && kouList.contains(31) && kouList.contains(33)
			||head==33 && kouList.contains(31) && kouList.contains(32)){
			yaku[32]=true;
		}
		yaku[33]=yaku[23]&&(yaku[24]||yaku[25]);
		if(yaku[33]) yaku[24]=yaku[25]=false;
		
	}
	
	void countDora(List<Tile> dorahyouList){
		for(Tile t:dorahyouList){
			int dora=-1;
			switch(t.id){
			case 8: dora=0;break;
			case 17: dora=9;break;
			case 26: dora=18;break;
			case 30: dora=27;break;
			case 33: dora=31;break;
			default: dora=t.id+1;
			}
			num_dora+=p.te[dora];
		}
	}
	
	void printYaku(){
		int han=num_dora;
		for(int i=0;i<36;i++){
			if(yaku[i]){
				han+=yakuHan[i];
				System.out.println(yakuMei[i]);
			}
		}
		if(num_dora>=1)System.out.println("ドラ"+num_dora);
		System.out.println(han+"翻");
	}
}


enum MatiType{
	RYAMMEN,
	KANCHAN,
	PENCHAN,
	SHAMPON,
	TANKI;
}



