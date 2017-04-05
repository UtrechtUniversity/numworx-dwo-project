package nl.uu.fi.dwo.mobile.utils;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;

public class PopupFacadeWithFont extends PopupFacade implements
		TekstElementWithFont, CBookEventListener {

	TekstElementWithFont delegate;
	public PopupFacadeWithFont(ObjectMap h, InteractionView delegate) {
		super(h, delegate);
		this.delegate = (TekstElementWithFont) delegate;
	}


	@Override
	public void setFontSize(int font_size) {
		delegate.setFontSize(font_size);
	}

	@Override
	public void setFontName(String font_name) {
		delegate.setFontName(font_name);
	}

	@Override
	public void setFontStyle(int font_style) {
		delegate.setFontStyle(font_style);
	}

	@Override
	public void setParentRegel(TekstRegel regel) {
		delegate.setParentRegel(regel);
	}


	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if (delegate instanceof CBookEventListener) {
			((CBookEventListener) delegate).acceptCBookEvent(event);
		}
		
	}

}
