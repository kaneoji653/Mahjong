package myAI;

public class PlayerAI extends AbstractPlayerAI {
	boolean naki=true;

	public boolean kyushuSelect(){
		return false;
	}

	public boolean kakanSelect(int id) {
		return naki;
	}

	public boolean minkanSelect(int id) {
		 return naki;
	}

	public boolean ankanSelect(int id) {
		return true;
	}

	public boolean ponSelect(int id) {
		return naki;
	}

	public boolean chi0Select(int id) {
		return naki;
	}

	public boolean chi1Select(int id) {
		return naki;
	}

	public boolean chi2Select(int id) {
		return naki;
	}

	public boolean reachSelect() {
		return true;
	}

	public boolean tsumoSelect() {
		return true;
	}

	public boolean ronSelect() {
		return true;
	}

}
