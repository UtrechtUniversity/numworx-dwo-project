package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;

public interface TekstElementWithFont extends TekstElement {

	void setFontSize(int font_size);

	void setFontName(String font_name);

	void setFontStyle(int font_style);

	void setParentRegel(TekstRegel regel);

}
