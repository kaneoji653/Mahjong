package millionMahjong;

public class MessageManager {
	boolean useLog=false;
	boolean useHassei=false;
	public MessageManager(boolean useLog, boolean useHassei){
		this.useLog=useLog;
		this.useHassei=useHassei;
	}
	void log(Object o){
		if(useLog) System.out.println(o);
	}
	void hassei(String s){
		if(useHassei) System.out.println(s);
	}
	
}
