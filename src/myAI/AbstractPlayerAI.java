package myAI;

public abstract class AbstractPlayerAI {
	public abstract boolean kyushuSelect();
	public abstract boolean kakanSelect(int tile);
	public abstract boolean minkanSelect(int tile);
	public abstract boolean ankanSelect(int tile);
	public abstract boolean ponSelect(int tile);
	public abstract boolean chi0Select(int tile);
	public abstract boolean chi1Select(int tile);
	public abstract boolean chi2Select(int tile);
	public abstract boolean reachSelect();
	public abstract boolean tsumoSelect();
	public abstract boolean ronSelect();
}
