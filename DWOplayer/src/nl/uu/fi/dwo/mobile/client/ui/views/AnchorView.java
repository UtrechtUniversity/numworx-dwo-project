package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class AnchorView implements IsWidget, ClickHandler, TekstElementWithFont /*, TekstElement*/ {

	static class AnchorAdapter implements AnchorContext {

		@Override
		public void gotoUrl(String href) {
		}

		@Override
		public void gotoPlace(String token) {
			UrlBuilder builder = Window.Location.createUrlBuilder();
			builder.setHash(token);
			prepareLeave();
			Window.Location.assign(builder.buildString());			
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
	 * @deprecated Use {@link #AnchorView(String,String,String,AnchorContext)} instead
	 */
	public AnchorView(String tekst, String href, AnchorContext context) {
		this(tekst, href, "_blank", context);
	}

	private void onLeave(ClickEvent ev) {
		if (context != null)			
			context.prepareLeave();
		else
			GWT.log("CONTEXT NULL");
	}

	/**
	 * Let op voor 'goto' URLS
	 * @param tekst
	 * @param href
	 * @param target TODO
	 * @param context 
	 */
	public AnchorView(String tekst, String href, String target, AnchorContext context) {
		tekst = tekst.trim().replace(' ', '\u00A0');
		if (href.startsWith(Actions.PROTO))
		{
			anchor = new Anchor(tekst, "javascript:return false;"); // bug in firefox?
			anchor.addClickHandler(new Actions.Handler(href));
			
		} else
		if (href.startsWith("history:")) {
			if (href.equals("history:back")) {
				anchor = new Anchor(tekst, "javascript:history.back()");				
			} else {
				try {
					int go = Integer.parseInt(href.substring(9));
					anchor = new Anchor(tekst, "javascript:history.go(" + go + ")");
				} catch(Exception oops) {				
					anchor = new Anchor(tekst, "javascript:return false;");
				}
			}
		} else
		if(href.startsWith("goto:") || href.startsWith("anchor:"))
		{
			anchor = new Anchor(tekst, "javascript:return false;"); // bug in firefox?
			anchor.addClickHandler(this);
			this.context = context==null?NULL:context;
			this.href = href;
		} else
		{	
			href = href.replace('!', '#'); // een echte ! is %21
			if (href.startsWith("#") && context != null) {
				final String token = href.substring(1);
				anchor = new Anchor(tekst,"javascript:return false;");
				anchor.addClickHandler(event -> { 
					event.stopPropagation();
					event.preventDefault();
					context.gotoPlace(token);
				});
			} else	{
			
			
			
			anchor = new Anchor(tekst, href);
			if (! href.startsWith("#") && !"_self".equals(target))
				anchor .setTarget(target);
			if (href.startsWith("#")) {
				UrlBuilder builder = Window.Location.createUrlBuilder();
				builder.setHash(href);
				anchor.setHref(builder.buildString());
				this.context = context;
				anchor.addClickHandler(this::onLeave);
			}}
		}
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
		return asHoogte * 106/120; // 1 pixel te hoog bij 12px font
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
