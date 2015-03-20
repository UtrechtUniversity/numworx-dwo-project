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

		@Override
		public void setCurrentElementRepaint() {
			x("loose focus");
		}

		@Override
		public void enter() {
			x("enter()");
		}

		@Override
		public void ndewortel() {
			x("ndewortel()");
		}

		@Override
		public void integraal() {
			x("integraal()");
		}

		@Override
		public void prv() {
			x("prv()");
		}

		@Override
		public void ndelog() {
			x("ndelog()");
		}

		@Override
		public void abs() {
			x("abs()");
		}

		@Override
		public void subscript() {
			x("subscript()");
		}

		@Override
		public void bin() {
			x("bin()");
		}

		@Override
		public void diff() {
			x("diff()");
		}

		@Override
		public void limiet0() {
			x("limiet0()");
		}

		@Override
		public void limiet1() {
			x("limiet1()");
		}

		@Override
		public void limiet2() {

			x("limiet2()");
		}

		@Override
		public void primitieve() {
			x("primitieve()");
		}

		@Override
		public void sigma() {
			x("sigma");
		}
		
	};
	
	
	
	
	@Override
	public void onModuleLoad() {
		
		AbstractKeyboard panel = new TabbedDesktopKeyboard(1);
		panel.setEditor(editor);
		RootPanel root = RootPanel.get();
		root.add(new Label("algebra"));
		root.add(panel);
		TabletKeyboard tk;
		
		AbstractKeyboard tablet = new TabbedTouchKeyboard(1);
		tablet.setEditor(editor);
		root.add(new Label("touch"));root.add(tablet);
		root.add(new Label("statistiek"));
		panel = new TabbedDesktopKeyboard(3);
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("touch")); tablet = new TabbedTouchKeyboard(3);
		tablet.setEditor(editor);
		root.add(tablet);
		root.add(new Label("meetkunde"));
		panel = new TabbedDesktopKeyboard(4);
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("touch")); tablet = new TabbedTouchKeyboard(4);
		tablet.setEditor(editor);
		root.add(tablet);
		root.add(new Label("gonio"));
		panel = new TabbedDesktopKeyboard(2);
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("touch")); tablet = new TabbedTouchKeyboard(2);
		tablet.setEditor(editor);
		root.add(tablet);
		root.add(new Label("onderbouw"));
		panel = new TabbedDesktopKeyboard(0);
		panel.setEditor(editor);
		root.add(panel);
		root.add(new Label("touch")); tablet = new TabbedTouchKeyboard(0);
		tablet.setEditor(editor);
		tablet.switchABC();
		root.add(tablet);

//		TabletKeyboardABC abc = new TabletKeyboardABC();
//		abc.setEditor(editor);
//		root.add(new Label("abc"));
//		root.add(abc);
//		TabletKeyboardUpper ABC = new TabletKeyboardUpper();
//		ABC.setEditor(editor);
//		root.add(new Label("ABC"));
//		root.add(ABC);
//
//		TabletKeyboardPen pen = new TabletKeyboardPen();
//		pen.setEditor(editor);
//		root.add(new Label("handschrift"));
//		root.add(pen);
		
//		AlphaKeys alpha = new AlphaKeys();
//		root.add(new Label("alpha"));
//		alpha.setEditor(editor);
//		root.add(alpha);

		root.add(tekst);

	}

	protected void x(String string) {
		tekst.setText(string);
	}

}
