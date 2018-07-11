
public abstract class AbstractPlayerAI {
	public abstract boolean kyushuSelect();
	abstract boolean kakanSelect(int tile);
	abstract boolean minkanSelect(int tile);
	abstract boolean ankanSelect(int tile);
	abstract boolean ponSelect(int tile);
	abstract boolean chi0Select(int tile);
	abstract boolean chi1Select(int tile);
	abstract boolean chi2Select(int tile);
	abstract boolean reachSelect();
	abstract boolean tsumoSelect();
	abstract boolean ronSelect();
}
