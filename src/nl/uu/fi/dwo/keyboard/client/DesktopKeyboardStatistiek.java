package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardStatistiek extends DesktopKeyboard {

	public DesktopKeyboardStatistiek() {
		super();
		c25.setHTML("<span class='statistiek statistiek-C25'></span>");
		c26.setHTML("<span class='statistiek statistiek-C26'></span>");
		c27.setHTML("<span class='statistiek statistiek-C27'></span>");
		c28.setHTML("<span class='statistiek statistiek-C28'><span class='path1'></span><span class='path2'></span><span class='path3'></span></span>");
		c29.setHTML("<span class='statistiek statistiek-C29'></span>");
		c30.setHTML("<span class='statistiek statistiek-C30'></span>");
		c31.setHTML("<span class='onderbouw onderbouw-C17'></span>");
	}


	@Override
	void onC25(ClickEvent e) {
		getEditor().insert('Δ');
	}

	@Override
	void onC26(ClickEvent e) {
		getEditor().insert('μ');
	}

	@Override
	void onC27(ClickEvent e) {
		getEditor().insert('σ');		
	}

	@Override
	void onC28(ClickEvent e) {
		getEditor().sigma();
	}

	@Override
	void onC29(ClickEvent e) {
		getEditor().insert("nPr");
	}

	@Override
	void onC30(ClickEvent e) {
		getEditor().insert("nCr");
	}

	@Override
	void onC31(ClickEvent e) {
		getEditor().insert('°');
	}

	

}
