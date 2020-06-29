package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextArea;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;

public class MLTextBox extends Composite implements HasText {

	private TextEditor area;
	
	public MLTextBox() {
		area = new TextEditor(300,150, true, true);
		initWidget(area.asWidget());
	}

	@Override
	public String getText() {
		return area.getText();
	}

	@Override
	public void setText(String text) {
		area.setText(text);
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		area.setCommunicationRoot(comRoot);
	}

}
