package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;

public class BerekeningVakFeedbackPanel extends PopupPanel{
	
	private LayoutPanel feedbackTekst = new LayoutPanel();
	
	public BerekeningVakFeedbackPanel(Panel vakPanel) {
		feedbackTekst.getElement().setInnerText("Niet duidelijk wat er moet worden uitgerekend. Selecteer een berekening en klik opnieuw.");
		feedbackTekst.getElement().getStyle().setColor(""+CssColor.make(49,71,112));
		
		BerekeningVakButton closeButton = new BerekeningVakButton("sluit");
		closeButton.setSize(15, 15);
		closeButton.addButtonListener(new CloseButtonListener());
		closeButton.asWidget().getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.add(closeButton);
		hp.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		vp.add(hp);
		vp.add(feedbackTekst);
		
		getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		getElement().getStyle().setBorderColor(""+CssColor.make(38,115,182));
		getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		getElement().getStyle().setPadding(5, Style.Unit.PX);
		getElement().getStyle().setBackgroundColor(""+CssColor.make(239,241,243));
		getElement().getStyle().setProperty("boxShadow", "3px 3px 3px #96A1BD");
		add(vp);
		setAutoHideEnabled(false);
		setWidth("180px");
		
		vakPanel.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				hide();
			}
		});
	}
	
	public void show(int x, int y) {
		setPopupPosition(x,y);
		super.show();
		setVisible(true);
	}
	
	public void hide() {
		super.hide();
	}
	
	private class CloseButtonListener implements ButtonListener {
		
		public void onClick(Object sender) {
			hide();
		}
	}

}
