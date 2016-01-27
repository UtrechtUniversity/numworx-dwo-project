package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class AnchorView implements IsWidget, ClickHandler, TekstElementWithFont /*, TekstElement*/ {

	public interface AnchorContext {

		void gotoUrl(String href);
	}
	
	static class AnchorAdapter implements AnchorContext {

		@Override
		public void gotoUrl(String href) {
		}
	}
	
	static final AnchorContext NULL = new AnchorAdapter();
	
	Anchor anchor;
	AnchorContext context;
	String href;
	/**
	 * Let op voor 'goto' URLS
	 * @param tekst
	 * @param href
	 * @param context 
	 */
	public AnchorView(String tekst, String href, AnchorContext context) {
		tekst = tekst.trim().replace(' ', '\u00A0');
		if(href.startsWith("goto:"))
		{
			anchor = new Anchor(tekst, "javascript:return false;"); // bug in firefox?
			anchor.addClickHandler(this);
			this.context = context==null?NULL:context;
			this.href = href;
		} else
			anchor = new Anchor(tekst, href);
			anchor .setTarget("_blank");
		ctx = Canvas.createIfSupported().getContext2d();
		setContextFont();
	}


	private void setContextFont() {
		ctx.setFont("bold " + fontSize + "px " + fontName);
		FormuleFont fm = FormuleFont.createFromFontSize(fontSize, true);
		fm.setFont(fontName);
		height = fm.getHeight();
		asHoogte = fm.getAscent();
	}
	
	
	@Override
	public Widget asWidget() {
		return anchor;
	}

	private String fontName = XMLView.getDefaultFontName();
	private int    fontSize = XMLView.getDefaultFontSize();
	private Context2d ctx;
	
	@Override
	public void setFontName(String name) {
		fontName = name;
		anchor.getElement().getStyle().setProperty("fontFamily", name);
		setContextFont();
	}
	@Override
	public void setFontSize(int size) {
		fontSize = size;
		anchor.getElement().getStyle().setFontSize(size, Unit.PX);
		setContextFont();
	}

	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
		context.gotoUrl(href);
	}


	@Override
	public String toString() {
		return anchor.getText();
	}

	private int asHoogte;
	public int getAsHoogte() {
		return asHoogte;
	}

	private int height;
	public int getHeight() {
		return height;
	}


	public int getWidth() {
		return (int) Math.round(ctx.measureText(toString()).getWidth()+1.5);
	}

	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;	
	}


	@Override
	public void setFontStyle(int font_style) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setParentRegel(TekstRegel regel) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
