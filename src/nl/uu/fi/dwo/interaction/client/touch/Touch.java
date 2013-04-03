package nl.uu.fi.dwo.interaction.client.touch;


public class Touch {

	private int x,y;

	public Touch(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getPageX() {
		
		return x;
	}

	public int getPageY() {
		
		return y;
	}

}
