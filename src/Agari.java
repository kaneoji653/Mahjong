import java.util.ArrayList;
import java.util.Arrays;
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

	Player houra;
	Player houju;
	int head;
	Mentu[] mentu;
	MatiType mati;
	boolean isTumo;
	int num_yaku;
	int num_dora = 0;
	int fu;
	int han;

	boolean isChitoi;
	int[] chitoiPair = new int[7];
	boolean isKokushi;

	enum MatiType {
		RYAMMEN, KANCHAN, PENCHAN, SHAMPON, TANKI;
	}

	Agari(Player houra, Player houju, int head, Mentu[] mentu, MatiType mati, boolean isTumo, List<Tile> dorahyouList, boolean isChitoi, boolean isKokushi) {
		this.houra = houra;
		this.houju = houju;
		this.head = head;
		this.mentu = mentu;
		this.mati = mati;
		this.isTumo = isTumo;
		this.isChitoi=isChitoi;
		this.isKokushi=isKokushi;
		this.makeYaku();
		countDora(dorahyouList);
		han=num_dora+num_yaku;
		countFu();
	}

	public static Agari agari(Player houra,Player houju, int agarihai, boolean isTumo, List<Tile> dorahyouList) {
		List<Agari> agari = new ArrayList<>();

		// 頭選択
		List<Integer> headKouho = new ArrayList<>();
		for (int i = 0; i < 34; i++) {
			if (houra.te[i] >= 2) {
				headKouho.add(i);
			}
		}
		for (Integer head : headKouho) {
			int[] te = Arrays.copyOf(houra.te, houra.te.length);
			te[head] -= 2;

			List<MatiType> matiKouho = new ArrayList<>();
			if (agarihai == head) {
				matiKouho.add(MatiType.TANKI);
			}
			boolean isPinfu = houra.num_fuuro == 0
					&& !(head == houra.bakaze || head == houra.jikaze || head == 31 || head == 32 || head == 33);

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
			if (m_cnt + houra.num_fuuro == 4) {
				for (int i = 0; i < houra.num_fuuro; i++) {
					mentu4[3 - i] = houra.fuuro.get(i);
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
				agari.add(new Agari(houra, houju, head, mentu4, mati, isTumo, dorahyouList,false,false));
			}
		}

		if (agari.isEmpty()) {
			if(headKouho.size()==7){
				agari.add(new Agari(houra, houju, -1, null, MatiType.TANKI, isTumo, dorahyouList,true,false));
			}
		}
		return agari.get(0);
	}

	void countFu(){
		if(isChitoi) {
			this.fu=25;
			return;
		}
		int fu=20;
		if(head==houra.bakaze||head==houra.jikaze||head==31||head==32||head==33) fu+=2;
		if(mati==MatiType.KANCHAN||mati==MatiType.PENCHAN||mati==MatiType.TANKI)fu+=2;
		if(isTumo) fu+=2;
		if(houra.isMenzen&&!isTumo) fu+=10;
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
				if(houra.te[i]==2){
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
//			houra.te = te;
		}

		yaku[0] = houra.isReach;
		yaku[1] = houra.isDoubleReach;
		if (yaku[1]) {
			yaku[0] = false;
		}
		yaku[2] = houra.isIppatu;
		yaku[3] = houra.isHaitei;
		yaku[4] = houra.isHoutei;
		yaku[5] = houra.isRinshan;
		yaku[6] = houra.isChankan;
		yaku[7] = houra.isMenzen && this.isTumo;
		yaku[8] = kouList.contains(houra.jikaze);
		yaku[9] = kouList.contains(houra.bakaze);
		yaku[10] = kouList.contains(31);
		yaku[11] = kouList.contains(32);
		yaku[12] = kouList.contains(33);
		yaku[13] = cnt_19 + cnt_jihai == 0;
		yaku[22] = isChitoi;
		if(!isChitoi){
			yaku[14] = houra.isMenzen && mati == MatiType.RYAMMEN && head != houra.jikaze && head != houra.bakaze && head != 31
					&& head != 32 && head != 33 && mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[2].type == MentuType.SHUN && mentu[3].type == MentuType.SHUN;
			yaku[15] = houra.isMenzen && (mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[0].pai[0] == mentu[1].pai[0]
					|| mentu[1].type == MentuType.SHUN && mentu[2].type == MentuType.SHUN
							&& mentu[1].pai[0] == mentu[2].pai[0]
					|| mentu[2].type == MentuType.SHUN && mentu[3].type == MentuType.SHUN
							&& mentu[2].pai[0] == mentu[3].pai[0]);
			for (int i = 0; i <= 6; i++) {
				if (shunList.contains(i) && shunList.contains(i + 9) && shunList.contains(i + 18)) {
					if (houra.isMenzen) {
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
				if (houra.isMenzen) {
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
					if (houra.isMenzen) {
						yaku[24] = true;
					} else {
						yaku[25] = true;
					}
				} else {
					if (houra.isMenzen) {
						yaku[28] = true;
					} else {
						yaku[29] = true;
					}
				}
			}
			yaku[26] = cnt_kan == 3;
			yaku[27] = houra.isMenzen && mentu[0].type == MentuType.SHUN && mentu[1].type == MentuType.SHUN
					&& mentu[0].pai[0] == mentu[1].pai[0] && mentu[2].type == MentuType.SHUN
					&& mentu[3].type == MentuType.SHUN && mentu[2].pai[0] == mentu[3].pai[0];
		}

		if (cnt_manzu == 0 && cnt_pinzu == 0 && cnt_souzu != 0 || cnt_manzu == 0 && cnt_pinzu != 0 && cnt_souzu == 0
				|| cnt_manzu != 0 && cnt_pinzu == 0 && cnt_souzu == 0) {
			if (cnt_jihai != 0) {
				if (houra.isMenzen) {
					yaku[30] = true;
				} else {
					yaku[31] = true;
				}
			} else {
				if (houra.isMenzen) {
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

		num_yaku=0;
		for (int i=0;i<36;i++) {
			if (yaku[i]) {
				num_yaku += yakuHan[i];
			}
		}
	}

	void countDora(List<Tile> dorahyouList) {
		for (Tile t : dorahyouList) {
			//ドラ表示からドラを計算
			int dora = -1;
			switch (t.id) {
			case 8:  dora = 0; break;
			case 17: dora = 9; break;
			case 26: dora = 18;	break;
			case 30: dora = 27;	break;
			case 33: dora = 31;	break;
			default: dora = t.id+1;break;
			}

			//ドラを数える
			num_dora += houra.te[dora];
			for(Mentu m:houra.fuuro){
				for(int p:m.pai){
					if(p==dora)num_dora++;
				}
			}
		}
	}

	void print(){
		System.out.println(houra.name +(isTumo? ":" : "←"+houju+":"));
		System.out.println(houra.tehaiToString());
		printYaku();
		System.out.println(this.fu + "符" + han + "翻");
		PointManager.printScore(fu,han,houra.jikaze==27,isTumo);
	}

	void printYaku() {
		for (int i = 0; i < 36; i++) {
			if (yaku[i]) {
				System.out.println(yakuMei[i]);
			}
		}
		if (num_dora >= 1) {
			System.out.println("ドラ" + num_dora);
		}
	}
}