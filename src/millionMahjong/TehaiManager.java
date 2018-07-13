package millionMahjong;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TehaiManager{
	static int[] mentatu=null;
	public int[] te;
	int size;
	int shanten;

	public TehaiManager(int[] te){
		if(mentatu==null) mentatu=makeMentatu();
		this.te=te;
	}
	

	public boolean is9shu(){
		int cnt=0;
		if(te[0]>=1)cnt++;
		if(te[8]>=1)cnt++;
		if(te[9]>=1)cnt++;
		if(te[17]>=1)cnt++;
		if(te[18]>=1)cnt++;
		if(te[26]>=1)cnt++;
		if(te[27]>=1)cnt++;
		if(te[28]>=1)cnt++;
		if(te[29]>=1)cnt++;
		if(te[30]>=1)cnt++;
		if(te[31]>=1)cnt++;
		if(te[32]>=1)cnt++;
		if(te[33]>=1)cnt++;
		return cnt>=9;
	}

	public void print(){
		String str="";
		boolean b;
		//萬子
		b=false;
		for(int i=0;i<9;i++){
			for(int t=0;t<te[i];t++){
				b=true;
				str+=(i+1);
			}
		}
		if(b)str+="m";
		//筒子
		b=false;
		for(int i=0;i<9;i++){
			for(int t=0;t<te[i+9];t++){
				b=true;
				str+=(i+1);
			}
		}
		if(b)str+="p";
		//索子
		b=false;
		for(int i=0;i<9;i++){
			for(int t=0;t<te[i+18];t++){
				b=true;
				str+=(i+1);
			}
		}
		if(b)str+="s";
		//字牌
		b=false;
		for(int i=0;i<7;i++){
			for(int t=0;t<te[i+27];t++){
				b=true;
				str+=(i+1);
			}
		}
		if(b)str+="z";
		System.out.println(str);
	}

	public int shantenUpdate(){
		size=0;
		for(int i=0;i<34;i++) size+=te[i];
		int num_fuuro=4-size/3;
		shanten=13;
		if(num_fuuro==0){
			shanten=Math.min(shanten, shantenChitoi());
			shanten=Math.min(shanten, shantenKokusi());
		}
		shanten=Math.min(shanten, shantenNormal(num_fuuro));
		return shanten;
	}

	public int shantenChitoi(){
		int shanten=6;
		for(int i=0;i<34;i++){
			if(te[i]>=2) shanten--;
		}
		return shanten;
	}

	public int shantenKokusi(){
		int shanten = 13;
		int head = 0;
		if (te[0]>=1)shanten--; if(te[0]>=2)head=1;
		if (te[8]>=1)shanten--; if(te[8]>=2)head=1;
		if (te[9]>=1)shanten--; if(te[9]>=2)head=1;
		if (te[17]>=1)shanten--; if(te[17]>=2)head=1;
		if (te[18]>=1)shanten--; if(te[18]>=2)head=1;
		if (te[26]>=1)shanten--; if(te[26]>=2)head=1;
		if (te[27]>=1)shanten--; if(te[27]>=2)head=1;
		if (te[28]>=1)shanten--; if(te[28]>=2)head=1;
		if (te[29]>=1)shanten--; if(te[29]>=2)head=1;
		if (te[30]>=1)shanten--; if(te[30]>=2)head=1;
		if (te[31]>=1)shanten--; if(te[31]>=2)head=1;
		if (te[32]>=1)shanten--; if(te[32]>=2)head=1;
		if (te[33]>=1)shanten--; if(te[33]>=2)head=1;
		return shanten-head;
	}

	public int shantenNormal(int num_fuuro){
		List<Integer> headKouho = new ArrayList<>();
		for(int i=0;i<34;i++){
			if(te[i]>=2) headKouho.add(i);
		}

		int shanten=8;
		//頭あり
		for(int head:headKouho){
			te[head]-=2;
			int mt=0;
			for(int i=27;i<34;i++){
				if(te[i]>=3) mt+=10;
				if(te[i]==2) mt+=1;
			}
			for(int i=0;i<3;i++){
				mt+= mentatu[tehaiToInt(Arrays.copyOfRange(te, i*9, i*9+9))];
			}
			shanten=Math.min(shanten, 7-2*num_fuuro-2*(mt/10)-Math.min((4-num_fuuro-mt/10),(mt%10)));
			te[head]+=2;
		}
		//頭なし
		int mt=0;
		for(int i=27;i<34;i++){
			if(te[i]>=3) mt+=10;
			if(te[i]==2) mt+=1;
		}
		for(int i=0;i<3;i++){
			mt+= mentatu[tehaiToInt(Arrays.copyOfRange(te, i*9, i*9+9))];
		}
		shanten=Math.min(shanten, 8-2*num_fuuro-2*(mt/10)-Math.min((4-num_fuuro-mt/10),(mt%10)));
		return shanten;
	}

	static int[] makeMentatu(){
		mentatu=new int[1953125]; //1953125= 5^9（全手牌を表現可）
		Arrays.fill(mentatu, -1);
		mentatu[0]=0;
		int[] te9=new int[9];
		for(int t=0;t<=12;t++){
			enumHands(0,te9,t,mentatu);
		}
		return mentatu;
	}

	//indexと同じかそれより数の大きい牌のみをtile枚teに追加して作られる手牌を全列挙する。
	//列挙した手牌はcutBlockで面子とターツを数える。
	static void enumHands(int index, int[] te9, int tile, int[] mentatu){
		if(tile==0){
			cutBlock(te9,mentatu);
		}

		//4枚ある場合はこれ以上追加しない
		else if(te9[index]==4){
			if(index==8){
				return;
			}else{
				enumHands(index+1, te9, tile, mentatu);
			}
		}

		//i(i>=index)番目に牌を追加して再帰
		else{
			for(int i=index;i<9;i++){
				te9[i]++;
				enumHands(i, te9, tile-1, mentatu);
				te9[i]--;
			}
		}
	}

	//teに含まれる最大の(面子数*10+ターツ数)を、mentatuに記録
	//teの枚数が少ない順にmentatuに記録することでDPっぽくできる。
	static void cutBlock(int[] te9, int[] mentatu) {
		List<int[]> meKouho = new ArrayList<>();
		List<int[]> taKouho = new ArrayList<>();
		List<int[]> koKouho = new ArrayList<>();
		for(int i=0;i<9;i++){
			//コーツ
			if(te9[i]>=3){
				int[] mentu={i,i,i};
				meKouho.add(mentu);
			}
			//シュンツ
			if(i<=6 && te9[i]>=1&&te9[i+1]>=1&&te9[i+2]>=1){
				int[] mentu={i,i+1,i+2};
				meKouho.add(mentu);
			}
			//トイツ
			if(te9[i]>=2){
				int[] tatu={i,i};
				taKouho.add(tatu);
			}
			//ペンチャン、リャンメン
			if(i<=7 && te9[i]>=1&&te9[i+1]>=1){
				int[] tatu={i,i+1};
				taKouho.add(tatu);
			}
			//カンチャン
			if(i<=6 && te9[i]>=1&&te9[i+2]>=1){
				int[] tatu={i,i+2};
				taKouho.add(tatu);
			}
			//孤立
			if(te9[i]>=1){
				int[] koritu={i};
				koKouho.add(koritu);
			}
		}

		int maxScore=0;
		for(int[] me:meKouho){
			for(int t:me) te9[t]--;
			maxScore=Math.max(maxScore, 10+mentatu[tehaiToInt(te9)]);
			for(int t:me) te9[t]++;
		}
		for(int[] ta:taKouho){
			for(int t:ta) te9[t]--;
			maxScore=Math.max(maxScore, 1+mentatu[tehaiToInt(te9)]);
			for(int t:ta) te9[t]++;
		}
		for(int[] ko:koKouho){
			for(int t:ko) te9[t]--;
			maxScore=Math.max(maxScore, mentatu[tehaiToInt(te9)]);
			for(int t:ko) te9[t]++;
		}
		mentatu[tehaiToInt(te9)]=maxScore;
	}

	//各牌の所持枚数を桁とした５進数に変換
	static Integer tehaiToInt(int[] te9){
		int result=0;
		for(int i=0;i<9;i++){
			result+=te9[i]*Math.pow(5, i);
		}
		return result;
	}
}
