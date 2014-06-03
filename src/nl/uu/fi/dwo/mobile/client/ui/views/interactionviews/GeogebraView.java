package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
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
		Map<String, Object> geogebraParams = new HashMap<String,Object>();
		Map ggbMap = (Map) launchData.get("interactiePanelLaunchState");
		Object object = ggbMap.get("ggbFile"); // java:ByteArray struct
		if(object instanceof Map) object = ((HashMap<String, Object>) object).get("string");
		ggb = object != null ? object.toString() : "";
		object = ggbMap.get("geogebraParams");
		if( object instanceof Map)
		{
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)object;
			geogebraParams.putAll(map);
		}
		bewaarOptie = Boolean.TRUE.equals( ggbMap.get("bewaarOptie"));
		nakijken    = Boolean.TRUE.equals( ggbMap.get("nakijken"));
		check       = !Boolean.FALSE.equals(ggbMap.get("check")); // default is true
		object      = ggbMap.get("scoreMax");
		if(object instanceof Number) scoreMax = ((Number) object).intValue();
		
		
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
		int width = 400;
		Object w = launchData.get("breedte");
		if (w != null)
			width = (int)Double.parseDouble(w.toString());
		int height = 400;
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
		return 0;
	}

	@Override
	public int getHeight() {
		return mainPanel.getOffsetHeight();
	}

	@Override
	public int getWidth() {
		return mainPanel.getOffsetWidth();
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
	}

}
