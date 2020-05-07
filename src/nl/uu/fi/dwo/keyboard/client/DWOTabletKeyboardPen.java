package nl.uu.fi.dwo.keyboard.client;

import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import fi.writemathgwt.client.WritePanel;
import fi.writemathgwt.client.WritePanelHolder;

public class DWOTabletKeyboardPen extends AbstractKeyboard implements WritePanelHolder {
	private static Logger logger = Logger.getLogger("DWOTabletKeyboardPen");


	private static int HEIGHT = 166+60;
	public int getKeyboardHeight() {
		return HEIGHT;
	}

	@UiField DWOTabletKeyboardPad pad;
	@UiField SimplePanel writePanel;
	
	WritePanel panel;
	
	private static TabletKeyboardPenUiBinder uiBinder = GWT
			.create(TabletKeyboardPenUiBinder.class);

	interface TabletKeyboardPenUiBinder extends
			UiBinder<Widget, DWOTabletKeyboardPen> {
	}

	public DWOTabletKeyboardPen() {
		this(593+200+108);
	}
	public DWOTabletKeyboardPen(int width) {
		initWidget(uiBinder.createAndBindUi(this));
		pad.t4_16.addStyleName("is-active");;
		pad.setDelegate(this);
		
		panel = new WritePanel(width,150+60,this,1);
		writePanel.setWidget(panel);
		writePanel.setPixelSize(width, -1);
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		super.setEditor(formuleEditor);
		pad.setEditor(formuleEditor);
	}

	@Override
	public void blur() {
		getDelegate().blur();
	}

	@Override
	void switchABC() {
		getDelegate().switchABC();
	}

	@Override
	void switch123() {
		getDelegate().switch123();
	}

	@Override
	void switchGreek() {
		getDelegate().switchGreek();
	}
	
	private boolean recurse;
	@Override
	public void writePanelChanged() {
		if(recurse) return;
		
		String s = panel.parseFormule();
		if (s.isEmpty()) {
			// Temporary local fix :: clear all doet geen paint dus als s was bleef de vorige inhoud zichtbaar
			// Huidige fix :: eerst clear all, (voor het geval er een grote formule verwijderd is)
			//                vervolgens wordt een korte string toegevoegd (omdat ischanged geactiveerd dient te worden)
			//                tenslotte wordt de backspace-functionaliteit (removeCurrentElement) aangeroepen
			getEditor().clearAll();
			s="1";
			getEditor().insert(s);
			getEditor().removeCurrentElement();
			
		} else {
			getEditor().clearAll();
			getEditor().insert(s);
		}

	}

	public void focus() {
		recurse = true;
		String formula = getEditor().toString();
		panel.readFormula(formula);
		super.focus();
		recurse = false;
	}

	/**
	 * @param nr 0 basis 1 uitgebreid
	 */
	@Override
	public void setWriteMathSet(int nr) {
		panel.setTekenSet(nr+1); // bias 1
	}

	void setEnterImage(ImageResource resource) {
		pad.setEnterImage(resource);
	}
	void setEnterImage(DataResource resource) {
		pad.setEnterImage(resource);
	}
	@Override
	public void onResize() {
		int w = getWidget().getOffsetWidth();
		int width = w - 27 - pad.getOffsetWidth();
		panel.setPixelSize(width, -1);
	}

}
