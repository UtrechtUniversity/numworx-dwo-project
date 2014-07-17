package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AnchorView implements IsWidget, ClickHandler{

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
		if(href.startsWith("goto:"))
		{
			anchor = new Anchor(tekst);
			anchor.addClickHandler(this);
			this.context = context==null?NULL:context;
			this.href = href;
		} else
			anchor = new Anchor(tekst, href);
			anchor .setTarget("_blank");
	}
	
	
	@Override
	public Widget asWidget() {
		return anchor;
	}


	@Override
	public void onClick(ClickEvent event) {
		context.gotoUrl(href);
	}

}
