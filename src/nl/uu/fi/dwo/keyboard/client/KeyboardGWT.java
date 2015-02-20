package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class KeyboardGWT implements EntryPoint {

	Label tekst = new Label();
	
	AbstractEditor editor = new AbstractEditor() {

		@Override
		public void macht() {
			x("macht");
		}

		@Override
		public void wortel() {
			x("wortel");
		}

		@Override
		public void breuk() {
			x("breuk");
		}

		@Override
		public void kwadraat() {
			x("kwadraat");
		}

		@Override
		public void haakjes() {
			x("haakjes ()");
		}

		@Override
		public void insert(String text) {
			x( "insert " + text);
		}
		
		
	};
	
	
	
	
	@Override
	public void onModuleLoad() {
		
		DesktopKeyboard panel = new DesktopKeyboard();
		panel.setEditor(editor);
		RootPanel.get().add(panel);
		RootPanel.get().add(tekst);

	}

	protected void x(String string) {
		tekst.setText(string);
	}

}
