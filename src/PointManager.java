import java.util.ArrayList;
import java.util.List;

public class PointManager {
	Player[] players;
	int honba=0;
	int kyotaku=0;

	PointManager(Player[] players){
		this.players=players;
	}
	
	int rank(int no){
		int r=1;
		for(int i=0;i<=no-1;i++){
			if(players[no].point<=players[i].point) r++;
		}
		for(int i=no+1;i<4;i++){
			if(players[no].point<players[i].point) r++;			
		}
		return r;
	}
	
	void scoreUpdate(){
		for(int i=0;i<4;i++){
			switch(rank(i)){
			case 1 : players[i].score+=3; break;
			case 2 : players[i].score+=1; break;
			case 3 : players[i].score-=1; break;
			case 4 : players[i].score-=3; break;
			}
		}
	}

	void initialize(){
		honba=0;
		kyotaku=0;
		for(Player p:players) p.point=25000;
	}

	void ron(Agari a){
		a.houra.point += houraScore(a, a.houju) + honba*300;
		a.houju.point -= houraScore(a, a.houju) + honba*300;
		a.houra.point+= kyotaku*1000;
		kyotaku=0;
		if(a.houra.isOya()) honba++;
		else honba=0;
	}

	boolean existTobi(){
		for(Player p:players){
			if(p.point<0)return true;
		}
		return false;
	}

	boolean isShaNyu(){
		for(Player p:players){
			if(p.point>=30000) return false;
		}
		return true;
	}

	void printPoint(){
		for(int i=0;i<4;i++){
			System.out.print(players[i].name+":"+players[i].point+" ");
		}
		System.out.println();
	}

	void printScore(){
		for(int i=0;i<4;i++){
			System.out.print(players[i].name+":"+players[i].score+" ");
		}
		System.out.println();
	}

	void reach(Player r){
		r.point-=1000;
		kyotaku++;
	}

	void bappu(){
		List<Player> tempai = new ArrayList<>();
		List<Player> noten = new ArrayList<>();
		for(int i=0;i<4;i++){
			if(players[i].tm.shantenUpdate()==0){
				tempai.add(players[i]);
			}else{
				noten.add(players[i]);
			}
		}
		switch(tempai.size()){
		case 1:
			tempai.get(0).point+=3000;
			noten.get(0).point -=1000;
			noten.get(1).point -=1000;
			noten.get(2).point -=1000;
			break;
		case 2:
			tempai.get(0).point+=1500;
			tempai.get(1).point+=1500;
			noten.get(0).point -=1500;
			noten.get(1).point -=1500;
			break;
		case 3:
			tempai.get(0).point+=1000;
			tempai.get(1).point+=1000;
			tempai.get(2).point+=1000;
			noten.get(0).point -=3000;
			break;
		}
	}

	void tumo(Agari a){
		for(int i=0;i<4;i++){
			if(players[i]==a.houra) continue;
			a.houra.point += houraScore(a, players[i]) + honba*100;
			players[i].point -= houraScore(a, players[i]) + honba*100;
		}
		if(a.houra.isOya()) honba++;
		else honba=0;
		a.houra.point+= kyotaku*1000;
		kyotaku=0;
	}

	int houraScore(Agari a, Player tg){
		int han=Math.min(a.han, 13);
		if(a.num_yakuman!=0)han=12+a.num_yakuman;
		if(a.isTumo){
			if(a.houra.isOya()||tg.isOya()){
				return tumoOya[fu_id(a.fu)][han-1];
			}else{
				return tumoKo[fu_id(a.fu)][han-1];
			}
		}else{
			if(a.houra.isOya()){
				return ronOya[fu_id(a.fu)][han-1];
			}else{
				return ronKo[fu_id(a.fu)][han-1];
			}
		}
	}

	static int fu_id(int fu){
		switch(fu){
		case 20:return 0;
		case 25:return 1;
		default:return fu/10-1;
		}
	}

	static void printScore(Agari a){
		int han=Math.min(a.han, 13);
		if(a.num_yakuman!=0)han=12+a.num_yakuman;
		if(a.isTumo){
			if(a.houra.isOya()){
				System.out.println(tumoOya[fu_id(a.fu)][han-1]+"オール");
			}else{
				System.out.println(tumoKo[fu_id(a.fu)][han-1]+"-"+tumoOya[fu_id(a.fu)][han-1]);
			}
		}else{
			if(a.houra.isOya()){
				System.out.println(ronOya[fu_id(a.fu)][han-1]);
			}else{
				System.out.println(ronKo[fu_id(a.fu)][han-1]);
			}
		}
	}

	final static int[][] ronKo ={
			{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{-1,1600,3200,6400,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{1000,2000,3900,7700,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{1300,2600,5200,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{1600,3200,6400,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{2000,3900,7700,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{2300,4500,8000,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{2600,5200,8000,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{2900,5800,8000,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{3200,6400,8000,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000},
			{3600,7100,8000,8000,8000,12000,12000,16000,16000,16000,24000,24000,36000,72000,108000,144000}};

	final static int[][] ronOya ={
			{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{-1,2400,4800,9600,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{1500,2900,5800,11600,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{2000,3900,7700,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{2400,4800,9600,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{2900,5800,11600,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{3400,6800,12000,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{3900,7700,12000,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{4400,8700,12000,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{4800,9600,12000,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000},
			{5300,10600,12000,12000,12000,18000,18000,24000,24000,24000,36000,36000,48000,96000,144000,192000}};

	final static int[][] tumoKo ={
			{-1  ,400 ,700 ,1300,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{-1  ,-1  ,800 ,1600,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{300 ,500 ,1000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{400 ,700 ,1300,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{400 ,800 ,1600,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{500 ,1000,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{600 ,1200,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{700 ,1300,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{800 ,1500,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{800 ,1600,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000},
			{900 ,1800,2000,2000,2000,3000,3000,4000,4000,4000,6000,6000,8000,16000,24000,32000}};

	final static int[][] tumoOya ={
			{-1  ,700 ,1300,2600,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{-1  ,-1  ,1600,3200,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{500 ,1000,2000,3900,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{700 ,1300,2600,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{800 ,1600,3200,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1000,2000,3900,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1200,2300,4000,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1300,2600,4000,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1500,2900,4000,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1600,3200,4000,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000},
			{1800,3600,4000,4000,4000,6000,6000,8000,8000,8000,12000,12000,16000,32000,48000,64000}};
}
