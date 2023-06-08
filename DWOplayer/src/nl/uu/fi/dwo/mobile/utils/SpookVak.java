package nl.uu.fi.dwo.mobile.utils;

import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SpookVak extends Composite implements InteractionView {

	public int getAsHoogte() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public void setAsHoogte(int ashoogte) {
	}

	public HashMap<String, Object> getState() {
		return new HashMap();
	}

	public SpookVak() {
		super();
		Widget w = new Label();
		initWidget(w);
	}

	public void setState(HashMap<String, Object> h) {
	}

	public int getScore() {
		return 0;
	}

	public int[][] getScoreObjectives() {
		return null;
	}

	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	public void kijkNa() {
	}

	public void zetNagekeken(boolean b) {
	}

	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	public void zetVolledigeBreedte(int breedte) {
	}
	public String toString() {
		return "SPOOKVAK";
	}
}
