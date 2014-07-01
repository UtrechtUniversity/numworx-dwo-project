package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GeogebraView implements InteractionView, LoadHandler
{

	private SimplePanel mainPanel;
	private Object ggbApplet;
	private Frame frame;
	private Button btn;
	private String ggb;
	private boolean bewaarOptie, nakijken, correct,check;
	private int score, scoreMax;
	private PopupFacade facade;
	private int width;
	private int height;
	private boolean volledigeBreedte;
	public native static Object getGgbWindow(Element frame) /*-{
		return frame.contentWindow;
	}-*/;

	public String install(Object o)
	{
		ggbApplet = o;
		return ggb;
	}

	public static native Object getApplet(Object wnd, GeogebraView view) /*-{
		wnd.viewer = view;
		wnd.install = function(o, viewer) {
			return viewer.@nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView::install(Ljava/lang/Object;)(o)
		}
		return wnd.ggbApplet;

	}-*/;

	public GeogebraView()
	{
		super();
		mainPanel = new SimplePanel();
	}

	public GeogebraView(HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this();
		init(launchdata);
	}

	public GeogebraView init(HashMap<String, Object> launchData)
	{
		facade = new PopupFacade(launchData);
		ObjectMap json = JSONUtilities.wrapMap(launchData);
		Map<String, Object> geogebraParams = new HashMap<String,Object>();
		ObjectMap ggbMap = json.getObjectMap("interactiePanelLaunchState");
		ObjectMap object = ggbMap.getObjectMap("ggbFile"); // java:ByteArray struct
		String string = object.getString("string");
		ggb = string != null ? string.toString() : "";
		object = ggbMap.getObjectMap("geogebraParams");
		if( object instanceof Map)
		{
			@SuppressWarnings("unchecked")
			Set<String> keys = ((Map)object).keySet();
			for(String key: keys) {
				geogebraParams.put(key, object.get(key));
			}
			//geogebraParams.putAll(map);
		}
		bewaarOptie = ggbMap.containsKey("bewaarOptie") && ggbMap.getBoolean("bewaarOptie");
		nakijken    = ggbMap.containsKey("nakijken") &&  ggbMap.getBoolean("nakijken");
		check       = (!ggbMap.containsKey("check")) || ggbMap.getBoolean("check"); // default is true
		if(ggbMap.containsKey("scoreMax")) 
			scoreMax = ggbMap.getInt("scoreMax");
		
		
		frame = new Frame(DWOplayer.PARAMETERS.getStubView() + "SlopeTestWeb.html");
		frame.setStylePrimaryName(".gwt-StubView");
		frame.addStyleDependentName("borderless");

		
		ggb = "data-param-ggbbase64='" + ggb + "'";
		StringBuilder params = new StringBuilder();
		for(Map.Entry<String, Object> entry: geogebraParams.entrySet())
		{
			params.append( "data-param-" + entry.getKey() + "='" + entry.getValue() + "' ");
		}
		params.append(ggb);
		ggb = params.toString();
		width = 400;
		Object w = launchData.get("breedte");
		if (w != null)
			width = (int)Double.parseDouble(w.toString());
		height = 400;
		Object h = launchData.get("hoogte");
		if (h != null)
			height = (int)Double.parseDouble(h.toString());
		frame.setSize(width + "px", height + "px");
		height -= 57 + 2; // toolbar aftrekken?
		width  -= 2;  // 1 pixel border
		ggb += " data-param-width='" + width + "' data-param-height='" + height + "'"; // geeft een scrollbar
		frame.addLoadHandler(this);
		mainPanel.setWidget(frame);
		return this;

	}

	@Override
	public HashMap<String, Object> getState()
	{
		HashMap map = new HashMap();
		if(bewaarOptie && ggbApplet != null) {
			map.put("state", getXML(ggbApplet));
		}
		return map;
	}

	private static native String getXML(Object ggb)
	/*-{
		return ggb.getXML();
	}-*/;
	
	private static native String setXML(Object ggb, String xml)
	/*-{
		ggb.setXML(xml);
	}-*/;

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(h == null) return;
		String xml = (String) h.get("state");
		if(bewaarOptie && xml != null && ggbApplet != null) setXML(ggbApplet, xml);
	}

	public void kijkNa()
	{
		
	}
	
	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public boolean isCorrect()
	{
		if(nakijken)
			return correct;
		return true;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(mainPanel);
	}

	@Override
	public void onLoad(LoadEvent event)
	{
		Object w = getGgbWindow(frame.getElement());
		if (w != null)
		{
			ggbApplet = getApplet(w, this);
		}
	}

	private native static int execute(Object js) /*-{
		return js.execute();
	}-*/;

	private native static void reset(Object js) /*-{
		return js.reset();
	}-*/;
	
	@Override
	public int getAsHoogte() {
		return facade.wrapAsHoogte(0);
	}

	@Override
	public int getHeight() {
		return facade.wrapHeight(height);
	}

	@Override
	public int getWidth() {
		return facade.wrapWidth(width);
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
			width = breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
	}

}
