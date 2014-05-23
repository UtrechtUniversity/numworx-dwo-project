package nl.uu.fi.dwo.mobile.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.PopupButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import static nl.uu.fi.dwo.mobile.utils.ImageUtils.newImage;

public class PopupFacade implements InteractionView {

	private static List<PopupButton> list = new LinkedList<PopupButton>();
	
	public static void hide() {
		for (PopupButton btn : list) {
			btn.hide();
		}
	}
	
	public static void removeAll() {
		hide();
		list.clear();
	}
	
	private boolean popup;
	private int setNr;
	private String popupImageString;
	private InteractionView delegate;
	
	public PopupFacade(HashMap<String, Object> h)
	{
		if(h == null) return;
		popup =  Boolean.TRUE.equals(h.get("popup"));
		Object value = h.get("setNr");
		if(value == null)
			setNr = 0;
		else {
			setNr = ((Number) value).intValue();
		}
		popupImageString = (String) h.get("popupImageString");
	}
	
	public PopupFacade(HashMap<String,Object> h, InteractionView delegate)
	{
		this(h);
		this.delegate = delegate;
	}
	
	
	public Widget wrap(Widget container) {
		if(popup) {
			PopupButton btn = new PopupButton(container, getImage(), null);
			list.add(btn);
			return btn;
		}
		return container;
	}
	
	public Widget wrap(StubView container) {
		if(popup) {
			PopupButton btn = new PopupButton(container.getWidget(), getImage(), container);
			list.add(btn);
			return btn;
		}
		return container;
	}

	final static String[] interactiePanelSetNames = {
		"images/resources/antwoordknop.gif", // antwoordvakLabel
		"images/resources/appletknop.gif",  // interactieVakLabel
		"images/resources/grafiekknop.gif", // grafiekComponentLabel
		"images/resources/tekstknop.gif",   // textVakLabel
		"images/resources/geogebra.gif",    // geogebra
	};
	
	private Image getImage() {
		if(popupImageString != null && popupImageString.length()>0)
			return new ImageView(popupImageString).getImage();
		return newImage(interactiePanelSetNames[setNr]);
	}

	public HashMap<String, Object> getState() {
		return delegate.getState();
	}

	public void setState(HashMap<String, Object> h) {
		delegate.setState(h);
	}

	public int getScore() {
		return delegate.getScore();
	}

	public boolean isCorrect() {
		return delegate.isCorrect();
	}
	
	public void kijkNa() {
		delegate.kijkNa();
	}

	public void setCommunicationRoot(OpdrNavIF comRoot) {
		delegate.setCommunicationRoot(comRoot);
	}

	public Widget asWidget() {
		return wrap(delegate.asWidget());
	}

	@Override
	public int getAsHoogte() {
		if(popup) // FIXME wat is de hashoogte van de button?
			return asWidget().getOffsetHeight() / 2;
		return delegate.getAsHoogte();
	}

	@Override
	public int getHeight() {
		if(popup) // FIXME hoogte van button?
			return asWidget().getOffsetHeight();
		return delegate.getHeight();
	}

	@Override
	public int getWidth() {
		if(popup)
			return asWidget().getOffsetWidth();
		return delegate.getWidth();
	}

	/**
	 * Als popup dan ashoogte van button en niet van kind.
	 */
	@Override
	public void setAsHoogte(int ashoogte) {
		if(!popup)
		{
			delegate.setAsHoogte(ashoogte);
		}
	}
	
}
