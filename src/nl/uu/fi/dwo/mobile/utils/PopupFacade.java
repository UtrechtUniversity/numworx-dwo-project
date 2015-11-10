package nl.uu.fi.dwo.mobile.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.PopupButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import static nl.uu.fi.dwo.mobile.utils.ImageUtils.newImage;

public class PopupFacade implements InteractionView, FacetAware {

	private static List<HasHide> list = new LinkedList<HasHide>();
	
	public static void hide() {
		for (HasHide btn : list) {
			btn.hide();
		}
	}
	
	public static void addPopup(HasHide w) {
		//if(!list.contains(w)) XXX ?????
			list.add(w);
	}
	
	public static void removeAll() {
		hide();
		list.clear();
	}
	
	private boolean popup;
	private int setNr;
	private String popupImageString;
	private Image  popupImage;
	private InteractionView delegate;
	
	@Deprecated
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
	
	@Deprecated
	public PopupFacade(HashMap<String,Object> h, InteractionView delegate)
	{
		this(h);
		this.delegate = delegate;
	}
	public PopupFacade(ObjectMap h, InteractionView delegate)
	{
		this(h);
		this.delegate = delegate;
	}
	
	
	public PopupFacade(ObjectMap h) {
		if(h == null) return;
		popup = h.getBoolean("popup", false);
		if(h.containsKey("setNr"))
			setNr = h.getInt("setNr");
		popupImageString = h.getString("popupImageString");
	}

	public Widget wrap(Widget container) {
		return wrap(container, null);
	}
	
	public Widget wrap(Widget container, InteractionView view) {
		if(popup) {
			if(popupBtn==null)
			{	popupBtn = new PopupButton(container, getImage(), view);
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
		if(popupImage != null) return popupImage;
		if(popupImageString != null && popupImageString.length()>0) {
			ImageView imageView = new ImageView(popupImageString);
			imageHeight = imageView.getHeight();
			imageWidth = imageView.getWidth();
			return popupImage = imageView.getImage();
		}
// TODO gebruik resources!
		imageWidth = widths[setNr];
		imageHeight = heights[setNr];
		
		return popupImage = newImage(interactiePanelSetNames[setNr]);
	}
	
	public InteractionView getDelegate()
	{
		return delegate;
	}

	public HashMap<String, Object> getState() {
		if (popupBtn != null)
		{
			if(popupBtn.popupShowing()) {
				return delegate.getState();
			}
			return popupBtn.getState();
		}
		return delegate.getState();
	}

	public void setState(HashMap<String, Object> h) {
			setPopupState(h);
			delegate.setState(h);
	}

	public void setPopupState(HashMap<String, Object> h) {
		if( popupBtn != null)
			popupBtn.setState(h);
	}

	public int getScore() {
		return delegate.getScore();
	}
	
	public int[][] getScoreObjectives() {
		return delegate.getScoreObjectives();
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
		if(popup) // wat is de ashoogte van de button? altijd dezelfde waarde: regelhoogte van het default font
			return XMLView.getDefaultFontSize() + 1;
		return delegate.getAsHoogte();
	}

	@Override
	public int getHeight() {
		if(popup) // FIXME hoogte van button?
			return getPopupHeight();
		return delegate.getHeight();
	}

	private int getPopupHeight() {
		getImage();
		return imageHeight;
	}
	private int getPopupWidth() {
		getImage();
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
		//if(!popup)
		{	delegate.zetVolledigeBreedte(breedte);
		}
			
	}

	/**
	 * Als popup dan ashoogte van button en niet van kind.
	 */
	@Override
	public void setAsHoogte(int ashoogte) {
		if(!popup)
		{	delegate.setAsHoogte(ashoogte);
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

	@Override
	public void getResponses(List<String> responses) {
		if(delegate instanceof FacetAware)
			((FacetAware) delegate).getResponses(responses);
	}

	public boolean hasState() {
		return popupBtn != null && !popupBtn.popupShowing();
	}
	
}
