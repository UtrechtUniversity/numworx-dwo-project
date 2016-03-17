package nl.uu.fi.dwo.mobile.client.sco;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;


/**
 * Decorator pattern.
 * @author wim
 *
 */
public class ShareFacade implements InteractionView, TekstElementWithFont {

	public static final String SHARE_KEY = "shareKey";
	private static JSONObject stateMap = new JSONObject();
	
	public ShareFacade(String key, InteractionView view,
			TekstElementWithFont withfont) {
		delegate = view;
		shareKey = key;
		this.withfont = withfont;
	}

	public InteractionView unwrap() {
		return delegate;
	}
	
	public static InteractionView wrap(ObjectMap launchData, InteractionView view)
	{
		if( ! launchData.containsKey(SHARE_KEY))		
			return view;
		String key = launchData.getString(SHARE_KEY);
		if ( view instanceof TekstElementWithFont )
			return new ShareFacade(key, view, (TekstElementWithFont) view );
		else
			return new ShareFacade(key, view, null);
	}
	
	static void setSharedState(JSONObject stateMap) {
		if(stateMap == null) stateMap = new JSONObject();
		ShareFacade.stateMap = stateMap;
	}
	
	public static void clearSharedState() {
		if(stateMap != null) {
			stateMap = new JSONObject();
		}
	}

	
	private InteractionView delegate;
	private TekstElementWithFont withfont;
	private String shareKey;

	public int getAsHoogte() {
		return delegate.getAsHoogte();
	}

	public int getHeight() {
		return delegate.getHeight();
	}

	public int getWidth() {
		return delegate.getWidth();
	}

	public void setAsHoogte(int ashoogte) {
		delegate.setAsHoogte(ashoogte);
	}

	public HashMap<String, Object> getState() {
		HashMap<String, Object> state = delegate.getState();
		JSONValue stateobject = JSONUtilities.toJSONObject(state);
		stateMap.put(shareKey, stateobject);
		Memento.instance().setShareMap(stateMap);
		return state;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setState(HashMap<String, Object> h) {
		if ( ! stateMap.containsKey(shareKey))
			delegate.setState(h);			
		JSONObject other = stateMap.get(shareKey).isObject();
		Map otherMap = JSONUtilities.fromJSONObject(other);
		HashMap hh = h != null ? new HashMap(h): new HashMap();
		hh.putAll(otherMap);
		delegate.setState(hh);
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

	public void zetVolledigeBreedte(int breedte) {
		delegate.zetVolledigeBreedte(breedte);
	}

	public Widget asWidget() {
		return delegate.asWidget();
	}

	@Override
	public void setFontSize(int font_size) {
		if(withfont != null)
			withfont.setFontSize(font_size);
	}

	@Override
	public void setFontName(String font_name) {
		if(withfont != null)
			withfont.setFontName(font_name);
	}

	@Override
	public void setFontStyle(int font_style) {
		if(withfont != null)
			withfont.setFontStyle(font_style);
	}

	@Override
	public void setParentRegel(TekstRegel regel) {
		if(withfont != null)
			withfont.setParentRegel(regel);
	}

}
