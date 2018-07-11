
public class PlayerAI extends AbstractPlayerAI {
	boolean naki=true;

	public boolean kyushuSelect(){
		return false;
	}

	boolean kakanSelect(int id) {
		return naki;
	}

	boolean minkanSelect(int id) {
		 return naki;
	}

	boolean ankanSelect(int id) {
		return true;
	}

	boolean ponSelect(int id) {
		return naki;
	}

	boolean chi0Select(int id) {
		return naki;
	}

	boolean chi1Select(int id) {
		return naki;
	}

	boolean chi2Select(int id) {
		return naki;
	}

	boolean reachSelect() {
		return true;
	}

	boolean tsumoSelect() {
		return true;
	}

	boolean ronSelect() {
		return true;
	}

}
