import java.util.ArrayList;
import java.util.List;

class Agari {
	public boolean isYao9(int id) {
		return !(1 <= id && id <= 7 || 10 <= id && id <= 16 || 19 <= id && id <= 25);
	}

	boolean[] yaku = new boolean[36];
	static final int[] yakuHan = { 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2,
			3, 2, 3, 2, 2, 2, 6, 5 };
	static final String[] yakuMei = { "リーチ", "ダブルリーチ", "一発", "ハイテイ", "ホウテイ", "リンシャン", "チャンカン", "ツモ", "自風", "場風", "白",
			"發", "中", "タンヤオ", "ピンフ", "イーペーコー", "三色同順", "三色同順", "三色同刻", "三暗刻", "一気通貫", "一気通貫", "七対子", "トイトイ", "チャンタ",
			"チャンタ", "三槓子", "リャンペーコー", "純チャン", "純チャン", "ホンイツ", "ホンイツ", "小三元", "ホンロートー", "チンイツ", "チンイツ" };

	Player p;
	int head;
	Mentu[] mentu;
	MatiType mati;
	boolean isTumo;
	int num_dora = 0;
	int fu;

	boolean isChitoi;
	int[] chitoiPair = new int[7];
	boolean isKokushi;

	Agari(Player p, int head, Mentu[] mentu, MatiType mati, boolean isTumo, List<Tile> dorahyouList, boolean isChitoi, boolean isKokushi) {
		this.p = p;
		this.head = head;
		this.mentu = mentu;
		this.mati = mati;
		this.isTumo = isTumo;
		this.isChitoi=isChitoi;
		this.isKokushi=isKokushi;
		this.makeYaku();
		countDora(dorahyouList);
		countFu();
		this.printYaku();
	}

	@Override
	public String toString() {
		String str = String.format("[%d,%d]", head, head);
		str += mentu[0].toString();
		str += mentu[1].toString();
		str += mentu[2].toString();
		str += mentu[3].toString();
		str += isTumo ? "TSUMO," : "RON,";
		str += mati.toString();
		return str;
	}
	
	void countFu(){
		if(isChitoi) {
			this.fu=25;
			return;
		}
		int fu=20;
		if(head==p.bakaze||head==p.jikaze||head==31||head==32||head==33) fu+=2;
		if(mati==MatiType.KANCHAN||mati==MatiType.PENCHAN||mati==MatiType.TANKI)fu+=2;
		if(isTumo) fu+=2;
		if(p.isMenzen&&!isTumo) fu+=10;
		for(Mentu m:mentu){
			if(m.type==MentuType.ANKAN) fu+=(isYao9(m.pai[0]))?32:16;
			if(m.type==MentuType.MINKAN) fu+=(isYao9(m.pai[0]))?16:8;
			if(m.type==MentuType.ANKO) fu+=(isYao9(m.pai[0]))?8:4;
			if(m.type==MentuType.PON) fu+=(isYao9(m.pai[0]))?4:2;
		}
		while(fu%10!=0) fu+=2;
		this.fu=fu;
	}
	
	void makeYaku() {
		List<Integer> shunList = new ArrayList<>();
		List<Integer> kouList = new ArrayList<>();
		int cnt_anko = 0;
		int cnt_kan = 0;
		int cnt_manzu = 0;
		int cnt_pinzu = 0;
		int cnt_souzu = 0;
		int cnt_jihai = 0;
		int cnt_19 = 0;

		if(isChitoi){
			for(int i=0;i<34;i++){
				if(p.te[i]==2){
					if (i==0||i==8||i==9||i==17||i==18||i==26) cnt_19++;
					switch (i/9) {
					case 0:cnt_manzu+=2;break;
					case 1:cnt_pinzu+=2;break;
					case 2:cnt_souzu+=2;break;
					case 3:cnt_jihai+=2;break;
					}
				}
			}
		}else{
			int[] te = new int[34];
			te[head] += 2;
			switch (head/9) {
			case 0:cnt_manzu+=2;break;
			case 1:cnt_pinzu+=2;break;
			case 2:cnt_souzu+=2;break;
			case 3:cnt_jihai+=2;break;
			}
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < mentu[i].pai.length; j++) {
					int pai = mentu[i].pai[j];
					te[pai]++;
					if (pai == 0 || pai == 8 || pai == 9 || pai == 17 || pai == 18 || pai == 26) {
						cnt_19++;
					}
					switch (pai/9) {
					case 0:cnt_manzu++;break;
					case 1:cnt_pinzu++;break;
					case 2:cnt_souzu++;break;
					case 3:cnt_jihai++;break;
					}
				}
				if (mentu[i].type == MentuType.SHUN || mentu[i].type == MentuType.CHI) {
					shunList.add(Math.min(mentu[i].pai[0], mentu[i].pai[1]));
				} else {
					kouList.add(mentu[i].pai[0]);
				}
				if (mentu[i].type == MentuType.ANKO || mentu[i].type == MentuType.ANKAN) {
					cnt_anko++;
				}
				if (mentu[i].type == MentuType.ANKAN || mentu[i].type == MentuType.MINKAN) {
					cnt_kan++;
				}
			}
			p.te = te;
		}

		yaku[0] = p.isReach;
		yaku[1] = p.isDoubleReach;
		if (yaku[1]) {
			yaku[0] = false;
		}
		yaku[2] = p.isIppatu;
		yaku[3] = p.isHaitei;
		yaku[4] = p.isHoutei;
		yaku[5] = p.isRinshan;
		yaku[6] = p.isChankan;
		yaku[7] = p.isMenzen && this.isTumo;
		yaku[8] = kouList.contains(p.jikaze);
		yaku[9] = kouList.contains(p.bakaze);
		yaku[10] = kouList.contains(31);
		yaku[11] = kouList.contains(32);
		yaku[12] = kouList.contains(33);
		yaku[13] = cnt_19 + cnt_jihai == 0;
		yaku[22] = isChitoi;
		if(!isChitoi){
			yaku[14] = p.isMenzen && mati == MatiType.RYAMMEN && head != p.jikaze && head != p.bakaze && head != 31
					&& head != 32 && head != 33 && mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[2].type == MentuType.SHUN && mentu[3].type == MentuType.SHUN;
			yaku[15] = p.isMenzen && (mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[0].pai[0] == mentu[1].pai[0]
					|| mentu[1].type == MentuType.SHUN && mentu[2].type == MentuType.SHUN
							&& mentu[1].pai[0] == mentu[2].pai[0]
					|| mentu[2].type == MentuType.SHUN && mentu[3].type == MentuType.SHUN
							&& mentu[2].pai[0] == mentu[3].pai[0]);
			for (int i = 0; i <= 6; i++) {
				if (shunList.contains(i) && shunList.contains(i + 9) && shunList.contains(i + 18)) {
					if (p.isMenzen) {
						yaku[16] = true;
					} else {
						yaku[17] = true;
					}
				}
			}
			for (int i = 0; i < 9; i++) {
				if (kouList.contains(i) && kouList.contains(i + 9) && kouList.contains(i + 18)) {
					yaku[18] = true;
				}
			}
			yaku[19] = cnt_anko == 3;
			if (shunList.contains(0) && shunList.contains(3) && shunList.contains(6)
					|| shunList.contains(9) && shunList.contains(12) && shunList.contains(15)
					|| shunList.contains(18) && shunList.contains(21) && shunList.contains(24)) {
				if (p.isMenzen) {
					yaku[20] = true;
				} else {
					yaku[21] = true;
				}
			}
			yaku[23] = kouList.size() == 4;
			if (isYao9(head) && (isYao9(mentu[0].pai[0]) || isYao9(mentu[0].pai[1]) || isYao9(mentu[0].pai[2]))
					&& (isYao9(mentu[1].pai[0]) || isYao9(mentu[1].pai[1]) || isYao9(mentu[1].pai[2]))
					&& (isYao9(mentu[2].pai[0]) || isYao9(mentu[2].pai[1]) || isYao9(mentu[2].pai[2]))
					&& (isYao9(mentu[3].pai[0]) || isYao9(mentu[3].pai[1]) || isYao9(mentu[3].pai[2]))) {
				if (cnt_jihai != 0) {
					if (p.isMenzen) {
						yaku[24] = true;
					} else {
						yaku[25] = true;
					}
				} else {
					if (p.isMenzen) {
						yaku[28] = true;
					} else {
						yaku[29] = true;
					}
				}
			}
			yaku[26] = cnt_kan == 3;
			yaku[27] = p.isMenzen && mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[0].pai[0] == mentu[1].pai[0] && mentu[2].type == MentuType.SHUN
					&& mentu[3].type == MentuType.SHUN && mentu[2].pai[0] == mentu[3].pai[0];
		}

		if (cnt_manzu == 0 && cnt_pinzu == 0 && cnt_souzu != 0 || cnt_manzu == 0 && cnt_pinzu != 0 && cnt_souzu == 0
				|| cnt_manzu != 0 && cnt_pinzu == 0 && cnt_souzu == 0) {
			if (cnt_jihai != 0) {
				if (p.isMenzen) {
					yaku[30] = true;
				} else {
					yaku[31] = true;
				}
			} else {
				if (p.isMenzen) {
					yaku[34] = true;
				} else {
					yaku[35] = true;
				}
			}
		}
		if (head == 31 && kouList.contains(32) && kouList.contains(33)
				|| head == 32 && kouList.contains(31) && kouList.contains(33)
				|| head == 33 && kouList.contains(31) && kouList.contains(32)) {
			yaku[32] = true;
		}
		yaku[33] = (yaku[23] && (yaku[24] || yaku[25])) || (isChitoi && cnt_19+cnt_jihai==14);
		if (yaku[33]) {
			yaku[24] = yaku[25] = false;
		}

	}

	void countDora(List<Tile> dorahyouList) {
		// System.out.println("ドラ表示:" + dorahyouList.size());
		for (Tile t : dorahyouList) {
			int dora = -1;
			switch (t.id) {
			case 8:
				dora = 0;
				break;
			case 17:
				dora = 9;
				break;
			case 26:
				dora = 18;
				break;
			case 30:
				dora = 27;
				break;
			case 33:
				dora = 31;
				break;
			default:
				dora = t.id + 1;
			}
			num_dora += p.te[dora];
		}
	}

	void printYaku() {
		int han = num_dora;
		for (int i = 0; i < 36; i++) {
			if (yaku[i]) {
				han += yakuHan[i];
				System.out.println(yakuMei[i]);
			}
		}
		if (num_dora >= 1) {
			System.out.println("ドラ" + num_dora);
		}
		System.out.println(this.fu + "符" + han + "翻");
	}
}