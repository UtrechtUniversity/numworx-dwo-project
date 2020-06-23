package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextArea;

public class MLTextBox extends Composite implements HasText {

	private TextArea area;
	
	public MLTextBox() {
		area = new TextArea();
		area.setVisibleLines(4);
		area.setCharacterWidth(20);
		initWidget(area);
	}

	@Override
	public String getText() {
		return area.getText();
	}

	@Override
	public void setText(String text) {
		area.setText(text);
	}

}
