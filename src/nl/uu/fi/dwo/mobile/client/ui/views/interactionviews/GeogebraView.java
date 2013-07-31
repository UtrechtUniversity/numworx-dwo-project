package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

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

		Map ggbMap = (Map) launchData.get("interactiePanelLaunchState");
		final Object object = ggbMap.get("ggbFile");
		ggb = object != null ? object.toString() : null;
		frame = new Frame("SlopeTestWeb.html");
		Object width = launchData.get("breedte");
		if (width == null)
			width = "400";
		Object height = launchData.get("hoogte");
		if (height == null)
			height = "400";
		frame.setSize(width + "px", height + "px");
		frame.addLoadHandler(this);
		mainPanel.setWidget(frame);
		return this;

	}

	@Override
	public HashMap<String, Object> getState()
	{
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{

	}

	@Override
	public int getScore()
	{
		return 0;
	}

	@Override
	public boolean isCorrect()
	{
		return true;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
	}

	@Override
	public Widget asWidget()
	{
		return mainPanel;
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

}
