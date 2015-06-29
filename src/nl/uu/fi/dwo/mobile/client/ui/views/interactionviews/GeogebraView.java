package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ImageTextButton;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GeogebraView implements InteractionView, LoadHandler
{

	private static final String KIJK_NA = "<span>kijk na</span>";
	private static final int KIJK_NA_HEIGHT = 20;
	private SimplePanel mainPanel;
	private Object ggbApplet;
	private Frame frame;
	private Button btn;
	private String ggb;
	private Boolean correct;
	private boolean bewaarOptie, nakijken,check;
	private int score, scoreMax;
	private PopupFacade facade;
	private int width;
	private int height;
	private int barHeight;
	private boolean volledigeBreedte;
	private boolean ingevuld;
	private boolean nagekeken;
	private String pendingState;
	private ObjectMap randomVars;
	private ImageTextButton checkBtn;
	private int aantalExistingObjects;
	private boolean nakijkenGemaakteObjecten;
	private String[] geogebraCheckObjects;
	private int[] geogebraCheckScores;
	private OpdrNavIF comRoot;
	
	public native static Object getGgbWindow(Element frame) /*-{
		return frame.contentWindow;
	}-*/;
	
	public static native int getObjectNumber(Object ggb) /*-{
		return ggb.getObjectNumber()
	}-*/; 
	

	public String install(Object o)
	{
		ggbApplet = o;
		setRandomVars();
		if(o != null) aantalExistingObjects = getObjectNumber(o);
		setPendingState();
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

	public GeogebraView(HashMap<String, Object> launchdata, String[] randomVarNamen, HashMap<String,?> randomVarWaarden)
	{
		this();
		if(!randomVarWaarden.isEmpty()) 
			randomVars = JSONUtilities.wrapMap(randomVarWaarden);
		init(launchdata);
	}

	private GeogebraView init(HashMap<String, Object> launchData)
	{
		facade = new PopupFacade(launchData);
		ObjectMap json = JSONUtilities.wrapMap(launchData);
		Map<String, Object> geogebraParams = new HashMap<String,Object>();
		String randje = "#FFFFFF";
		if(json.getBoolean("border", false))
			randje = "#000000";
		geogebraParams.put("borderColor", randje);
		ObjectMap ggbMap = json.getObjectMap("interactiePanelLaunchState");
		ObjectMap object = ggbMap.getObjectMap("ggbFile"); // java:ByteArray struct
		String string = object.getString("string");
		ggb = string != null ? string.toString() : "";
		object = ggbMap.getObjectMap("geogebraParams");
		if( object instanceof Map)
		{
			Set<String> keys = object.keySet();
			for(String key: keys) {
				geogebraParams.put(key, object.getString(key));
			}
		}
		bewaarOptie = ggbMap.containsKey("bewaarOptie") && ggbMap.getBoolean("bewaarOptie");
		nakijken    = ggbMap.containsKey("nakijken") &&  ggbMap.getBoolean("nakijken");
		check       = (!ggbMap.containsKey("check")) || ggbMap.getBoolean("check"); // default is true
		nakijkenGemaakteObjecten = ggbMap.containsKey("nakijkenGemaakteObjecten") && ggbMap.getBoolean("nakijkenGemaakteObjecten");
		if(ggbMap.containsKey("geogebraCheckObjects"))
			geogebraCheckObjects = ggbMap.getStringArray("geogebraCheckObjects");
		if(ggbMap.containsKey("geogebraCheckScores"))
			geogebraCheckScores  = ggbMap.getIntArray("geogebraCheckScores");		
		
		if(ggbMap.containsKey("scoreMax")) 
			scoreMax = ggbMap.getInt("scoreMax");
		
		
		frame = new Frame(DWOplayer.PARAMETERS.getStubView() + "GeoGebra.html?locale=" + StubView.getLocale());
		frame.setStylePrimaryName(".gwt-StubView");
		frame.addStyleDependentName("borderless");

		
		ggb = "data-param-ggbbase64='" + ggb + "'";
		StringBuilder params = new StringBuilder();
		for(Map.Entry<String, Object> entry: geogebraParams.entrySet())
		{
			params.append( "data-param-" + entry.getKey() + "='" + entry.getValue() + "' ");
		}
		params.append( "data-param-language='" + StubView.getLocale() + "' ");
		if("true".equals(geogebraParams.get("showMenuBar")))
			barHeight += 33;
		if("true".equals(geogebraParams.get("showToolBar")))
			barHeight += 9; //57;
		
		params.append(ggb);
		ggb = params.toString();
		width = 400;
		if (json.containsKey("breedte"))
			width = json.getInt("breedte");
		height = 400;
		if (json.containsKey("hoogte"))
			height = json.getInt("hoogte");
		volledigeBreedte = json.containsKey("volledigeBreedte") && json.getBoolean("volledigeBreedte");
		
		//if(!volledigeBreedte) //als volledigeBreedte dan wordt initFrame gedaan in zetVolledigeBreedte.
		//initFrame();
		return this;

	}

	private void initFrame() {
		int height = this.height;
		int width  = this.width;
		if(nakijken)
			height -= KIJK_NA_HEIGHT; // button size?
		
		frame.setPixelSize(width, height);
		//height -= 57 + 2; // toolbar aftrekken? ja dus!
		height -= 2;
		width  -= 2;  // 1 pixel border
		height -= barHeight;
		ggb += " data-param-width='" + width + "' data-param-height='" + height + "'"; // geeft een scrollbar
		frame.addLoadHandler(this);
		if(nakijken)
		{
			checkBtn = new ImageTextButton(KIJK_NA, new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onCheck();
				}} );
			checkBtn.getElement().getStyle().setPaddingBottom(0, Unit.PX);
			checkBtn.getElement().getStyle().setPaddingTop(0,Unit.PX);
			HeaderPanel hp = new HeaderPanel();
			hp.setPixelSize(this.width, this.height);
			hp.setContentWidget(frame);
			VerticalPanel vp = new VerticalPanel();
			vp.setPixelSize(this.width, KIJK_NA_HEIGHT);
			vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			vp.add(checkBtn);
			if(nakijken && comRoot != null && comRoot.getMode() > 1) 
				checkBtn.setVisible(false);
			hp.setFooterWidget(vp);
			mainPanel.setWidget(hp);
		}
		else 
			mainPanel.setWidget(frame);
	}

	protected void onCheck() {
		kijkNa();
		setCheckImg();
		comRoot.setChanged(Boolean.FALSE.equals(correct));
		
	}

	/**
	 * Kijk na button met en zonder rood kruisje/groen vinke
	 */
	private void setCheckImg() {
		if(checkBtn == null) return;
		
		if(Boolean.TRUE.equals(correct))
			checkBtn.setHTML(KIJK_NA +
					"<img src='" +
				FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri().asString() +
				"' >");
		else if(Boolean.FALSE.equals(correct))
			checkBtn.setHTML(KIJK_NA +
					"<img src='" +
				FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri().asString() +
				"' >");
		else if(nagekeken)
		{
			checkBtn.setHTML(KIJK_NA +
					"<img src='" +
				FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri().asString() +
				"' >");
		
		} else
			checkBtn.setHTML(KIJK_NA);
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
		if(bewaarOptie && xml != null)
		{
			if (ggbApplet != null)
			{	pendingState = null;
				setXML(ggbApplet, xml);
			}
			else
				this.pendingState = xml;
		} else {
			this.pendingState = null;
		}
	}

	public void kijkNa()
	{
		if(nakijken)
		{
			if(nakijkenGemaakteObjecten)
			{
				int length = getObjectNumber(ggbApplet) - aantalExistingObjects;
				if( length <= 0 ) 
				{
					setCorrect(false);
					return;
				}
				int checkLength = geogebraCheckObjects.length;
				int checkStart  = 0;
				String[] checkObjects = new String[checkLength];
				System.arraycopy(geogebraCheckObjects, 0, checkObjects, 0, checkLength);
				score = 0;
				int matches = 0;
				for(int i = 0; i < length; i++) {
					String objectName = getObjectName(ggbApplet, i+aantalExistingObjects);
					String valueString = getValueString(ggbApplet, objectName);
					String objectString;
					objectString = valueString.replace(objectName +"(x)", "");
					objectString = objectString.replace(objectName, "");
					objectString = objectString.replace(" ", "");
					objectString = objectString.substring(1);
					boolean match = false;
					for(int j = checkStart ; j < checkLength; j ++ )
					{
						if(checkObjects[j] == null) continue;
						match = checkObjects[j].equals(objectString);
						if(!match && ! "boolean".equals(getObjectType(ggbApplet, objectName))) {
							evalCommand(ggbApplet, "checkDWO=" + checkObjects[j] + "==" + objectName);
							match = 1.0 == getValue(ggbApplet, "checkDWO");
						}
						if(match) {
							setColor(ggbApplet, objectName, 0, 180, 0);
							score += geogebraCheckScores[j];
							checkObjects[j] = null; // used!
							matches ++;
	// Optimalisatie, maak checkObjects array kleiner als mogelijk, maar met behoud van indexen.
							if(j == checkStart) checkStart++;
							else if(j == checkLength-1) checkLength = j;
							break;
						}						
					}
				}
				if(matches > 0 && matches < checkObjects.length)
					setCorrect(null);
				else
					setCorrect(matches != 0);
			
			} else {
 				double val = getValue(ggbApplet, "checkDWO");
 				setCorrect(val == 1.0);
			}
//			nagekeken = true;				
		}
		
	}

	private void setCorrect(Boolean b) {
		if(b == null)
			correct = null;
		else if( b)
		{
			score = scoreMax;
			correct = Boolean.TRUE;
		} else {
			score = 0;
			correct = Boolean.FALSE;
		}
	}
	
	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public Boolean isCorrect()
	{
		if(nakijken)
			return correct;
		return Boolean.TRUE;
	}
	
	public void zetNagekeken(boolean b) {
		if (ingevuld)
			nagekeken = b;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		int mode = comRoot.getMode();
		if(nakijken & mode > 1 && checkBtn != null) {
			// FIXME haal checkbutton weg.
			checkBtn.setVisible(false);
		}
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
			setRandomVars();
			if(ggbApplet != null) aantalExistingObjects = getObjectNumber(ggbApplet);
			setPendingState();
		}
	}

	private void setPendingState() {
		if(pendingState != null && ggbApplet != null) {
			setXML(ggbApplet, pendingState);
			pendingState = null;
		}
	}
	
	private void setRandomVars() {
		if(ggbApplet != null && randomVars != null) {
			Set<String> keys = randomVars.keySet();
			for (String key : keys) {
				double value = randomVars.getDouble(key);
				key = "dwo_" + key.replace("?(", "").replace(")", "");
				setValue(ggbApplet, key, value);
			}
		}
	}
	
	private static native void setValue(Object ggb, String key, double value) /*-{
		ggb.setValue(key, value);
	}-*/;
	
	private static native double getValue(Object ggb, String key) /*-{
		return ggb.getValue(key);
	}-*/;
	
	private static native String getValueString(Object ggb, String key) /*-{
		return ggb.getValueString(key);
	}-*/;
	private static native String getObjectName(Object ggb, int i) /*-{
		return ggb.getObjectName(i);
	}-*/;
	private static native String getObjectType(Object ggb, String name) /*-{
		return ggb.getObjectType(name);
	}-*/;
	private static native boolean evalCommand(Object ggb, String name) /*-{
		return ggb.evalCommand(name);
	}-*/;

	private static native void setColor(Object ggb, String name, int red, int green, int blue) /*-{
		ggb.setColor(name, red, green, blue);
	}-*/;
	
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
		{
			width = breedte;
			//initFrame();
		}
		initFrame();
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
	}

}
