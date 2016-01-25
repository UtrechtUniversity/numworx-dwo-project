package nl.uu.fi.dwo.keyboard.client;

import nl.uu.fi.dwo.interaction.client.keyboard.AbstractEditor;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class DWOKeyboardGWT implements EntryPoint {

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
		
		AbstractKeyboard panel = new DWOTabbedDesktopKeyboard();
		panel.setEditor(editor);
		RootPanel root = RootPanel.get();
		root.add(new Label("algebra"));
		root.add(panel);
		TabletKeyboard tk;
		
		AbstractKeyboard tablet = new DWOTabbedTouchKeyboard(1);
		tablet.setEditor(editor);
		tablet.setEnterType(EnterType.ENTER);
		root.add(new Label("touch"));root.add(tablet);

		root.add(tekst);

	}

	protected void x(String string) {
		tekst.setText(string);
	}

}
