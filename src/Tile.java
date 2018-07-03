public class Tile{
	int id;
	int kazu;
	String shu;
	Tile(int id){
		this.id=id;
		this.kazu = id%9+1;
		switch(id/9){
		case 0: this.shu = "m"; break;
		case 1: this.shu = "p"; break;
		case 2: this.shu = "s"; break;
		case 3: this.shu = "z"; break;
		}
	}

	public String toString(){
		return this.kazu+this.shu;
//		return ""+this.id;
	}

	public boolean equals(Object t){
		if(t == null || !(t instanceof Tile)) {
			return false;
		}else{
			return this.id == ((Tile)t).id;
		}
	}
}

