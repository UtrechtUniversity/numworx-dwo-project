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
			if(popupBtn==null)
			{	popupBtn = new PopupButton(container, getImage(), null);
				list.add(popupBtn);
			}
			return popupBtn;
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
	final static int heights[] = { 20,20,20,20,20 };
	final static int widths [] = { 20,20,20,20,20 };
	
	
	private PopupButton popupBtn;
	private int imageHeight = 20;
	private int imageWidth  = 20;
	
	private Image getImage() {
		if(popupImageString != null && popupImageString.length()>0) {
			ImageView imageView = new ImageView(popupImageString);
			imageHeight = imageView.getHeight();
			imageWidth = imageView.getWidth();
			return imageView.getImage();
		}
// TODO gebruik resources!
		imageWidth = widths[setNr];
		imageHeight = heights[setNr];
		
		return newImage(interactiePanelSetNames[setNr]);
	}
	
	public InteractionView getDelegate()
	{
		return delegate;
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

	public Boolean isCorrect() {
		return delegate.isCorrect();
	}
	
	public void kijkNa() {
		delegate.kijkNa();
	}
	
	public void zetNagekeken(boolean b) {
		delegate.zetNagekeken(b);
	}

	public void setCommunicationRoot(OpdrNavIF comRoot) {
		delegate.setCommunicationRoot(comRoot);
	}

	public Widget asWidget() {
		return wrap(Widget.asWidgetOrNull(delegate));
	}

	@Override
	public int getAsHoogte() {
		if(popup) // FIXME wat is de ashoogte van de button?
			return getPopupHeight() / 2;
		return delegate.getAsHoogte();
	}

	@Override
	public int getHeight() {
		if(popup) // FIXME hoogte van button?
			return getPopupHeight();
		return delegate.getHeight();
	}

	private int getPopupHeight() {
		return imageHeight;
	}
	private int getPopupWidth() {
		return imageWidth; 
	}
	

	@Override
	public int getWidth() {
		if(popup)
			return getPopupWidth();
		return delegate.getWidth();
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
		if(!popup)
		{	delegate.zetVolledigeBreedte(breedte);
		}
			
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

	public int wrapHeight(int height) {
		if(popup) return getPopupHeight();
		return height;
	}
	
	public int wrapWidth(int width) {
		if(popup) return getPopupWidth();
		return width;
	}
	
	public int wrapAsHoogte(int asHoogte) {
		if(popup) return getAsHoogte();
		return asHoogte;
	}
	
}
