package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.event.dom.client.ClickEvent;

public class DesktopKeyboardStatistiek extends DesktopKeyboard {

	public DesktopKeyboardStatistiek() {
		super();
		c31.getUpFace().setHTML("");c31.getDownFace().setHTML("");c31.setEnabled(false);
		c24.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C24.png'>");
		c24.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C24-2.png'>");
		c25.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C25.png'>");
		c25.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C25-2.png'>");
		c26.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C26.png'>");
		c26.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C26-2.png'>");
		c27.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C27.png'>");
		c27.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C27-2.png'>");
		c28.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C28.png'>");
		c28.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C28-2.png'>");
		c29.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C29.png'>");
		c29.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C29-2.png'>");
		c30.getUpFace().setHTML("<img src='images/kb/Statistiek/Desktop/C30.png'>");
		c30.getDownFace().setHTML("<img src='images/kb/Statistiek/Desktop/C30-2.png'>");
	}


	@Override
	void onC24(ClickEvent e) {
		getEditor().insert("0");
	}

	@Override
	void onC25(ClickEvent e) {
		getEditor().insert("delta");
	}

	@Override
	void onC26(ClickEvent e) {
		getEditor().insert("mu");
	}

	@Override
	void onC27(ClickEvent e) {
		getEditor().insert("sigma");		
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
	}
}
