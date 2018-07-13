package millionMahjong;

import java.util.Arrays;

public class GameInfo {
	int playerID;
	Player[] players;
	PointManager pm;
	
	public Player[] getPlayer(){
		return Arrays.copyOf(players, 4);
	}
	
	public int getID(){
		return playerID;
	}
	
	public int getHonba(){
		return pm.getHonba();
	}
	
	public int getKyotaku(){
		return pm.getKyotaku();
	}
	
}
