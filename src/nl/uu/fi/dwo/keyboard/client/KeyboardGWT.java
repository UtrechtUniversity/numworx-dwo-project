package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class KeyboardGWT implements EntryPoint {

	Label tekst = new Label("...");
	
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
		@Override
		public void insert(char text) {
			x( "insert " + text);
		}
		
		
	};
	
	
	
	
	@Override
	public void onModuleLoad() {
		
		DesktopKeyboard panel = new DesktopKeyboard();
		panel.setEditor(editor);
		RootPanel root = RootPanel.get();
		root.add(new Label("algebra"));
		root.add(panel);
		TabletKeyboard tablet = new TabletKeyboard();
		tablet.setEditor(editor);
		root.add(tablet);
		root.add(new Label("statistiek"));
		panel = new DesktopKeyboardStatistiek();
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("meetkunde"));
		panel = new DesktopKeyboardMeetkunde();
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("gonio"));
		panel = new DesktopKeyboardGonio();
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("onderbouw"));
		panel = new DesktopKeyboardOnderbouw();
		panel.setEditor(editor);
		root.add(panel);

		
		AlphaKeys alpha = new AlphaKeys();
		root.add(new Label("alpha"));
		alpha.setEditor(editor);
		root.add(alpha);

		root.add(tekst);

	}

	protected void x(String string) {
		tekst.setText(string);
	}

}
