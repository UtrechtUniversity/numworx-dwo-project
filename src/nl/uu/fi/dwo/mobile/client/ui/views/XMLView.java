package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

//import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.JSONObjectMapImpl;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordTekstVak2;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen.StelselOplossingenVak;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
/**
 * Common code voor ViewModuleViewImpl en DescriptionViewImpl.
 * @author wim
 *
 */
public abstract class XMLView {

	protected NeedLogin<JSONValue> OOPS = NeedLogin.instance();
	private final boolean RESPONSIVE = DWOplayer.RESPONSIVE;
    protected HashMap<String, Object> launchData;
	protected ObjectMap instellingen;
	protected int font_size = 12;
	protected String font_name = "Arial";
	protected String[] randomVarNamen = null;
	protected HashMap randomVarWaarden = null;
	protected ArrayList<Object> opdrachtObjects;
	protected boolean newVersion = true;
	protected FormuleKeyboardIF kb = null;
	protected StatusBarIF sb = null;
	protected FormuleClipboardIF cb = null;
	private Logger logger = Logger.getLogger("XMLView");
	private static int defaultFontSize = 12;
	private static String defaultFontName = "Arial";
	
	private boolean bolletjesZichtbaar = true;
	protected boolean volgendeKnopZichtbaar = false;
	protected boolean vorigeKnopZichtbaar = false;
	protected boolean scoresZichtbaar = true;
	
	protected boolean condNav = false;
	protected boolean condNavPerc = false;
	protected boolean condNavVoorwaarden = false;
	protected int[][][] navVoorwaarden = null;
	public boolean[][] bezocht = null;
	protected int condPerc = 100;
	
	protected boolean allesCorrectNodig = false;
	public boolean zelftoetsGeenCorr = false;
	public boolean eerderGeenCorr = false;
	/**
	 * Boolean die aangeeft of de geschiedenis van zelftoetsscores
	 * (percentages) moet worden bijgehouden en getoond.
	 */
	public boolean zelftoetsGeschiedenis = false;
	/**
	 * Boolean die aangeeft of de high score van de zelftoets
	 * moet worden getoond als totaalscore (percentage).
	 * Kan alleen true zijn als zelftoetsGeschiedenis true is.
	 */
	public boolean zelftoetsHighScore = false;
	/**
	 * Boolean indicating timer for a tempotoets.
	 */
	public boolean timer = false;
	/**
	 * The time limit in seconds for a tempotoets.
	 */
	public int timeLimitSeconds = 0;
	/**
	 * Boolean indicating whether the tempotoets is locked
	 */
	public boolean tempotoetsLocked = false;
	
	//private int[][][][] scoresObjectives;
	public static String[][] objectives;
	private String[] categorieString;
	//private int[][][][] scoresMaxObjectives;
	protected boolean pilotObjectives = false;
	public static String[][] misconceptions;
	private String[] mccCategorieString;
	public int[][][][] measuredMisconceptions;
	boolean objectivesAanwezig;
	boolean globalParam;
	
	private Map styles;
	private String templateName;
	
	
	TekstVakPanel hoofdPanel;
	protected FlowPanel contentPanel;
	public final ActivityComponent activity;

	protected void setupView(HashMap<String, Object> launchData)
	{
		this.launchData = launchData;
		ObjectMap wrap = JSONUtilities.wrapMap(launchData);
		Map<String, Object> imagemap = wrap.getMap("$IMAGE$MAP$");
		ImageView.setMap(imagemap);
		
		if (wrap.containsKey("instellingen"))
		{	instellingen = wrap.getObjectMap("instellingen");
			wrap = (instellingen);
			if (wrap.containsKey("fontSize") )
				font_size = wrap.getInt("fontSize");
	
			boolean maalTeken =  wrap.getBoolean("maalTeken");
			FormuleTeken.zetMaalTeken(maalTeken);
			boolean diffOperatoren = false;
			if(wrap.containsKey("diffOperatoren"))
				diffOperatoren = wrap.getBoolean("diffOperatoren");
			FormuleTeken.zetDiffOperatoren(diffOperatoren);
			FormuleParser.zetDiffOperatoren(diffOperatoren);
			boolean hoekGraden = wrap.getBoolean("hoekGraden");
			Expressie.zetHoekGraden(hoekGraden);
			if(wrap.containsKey("fontName")) // optional
			{	font_name = wrap.getString("fontName");
				//if(font_name.equals("SansSerif"))
				//	font_name = "sans-serif";
			} else font_name = "Arial";
			FormuleFont.zetDefaultFont(font_name);
			boolean formTimes = wrap.containsKey("formTimes") && wrap.getBoolean("formTimes"); // optional
			FormuleFont.zetFormTimes(formTimes);
			FormuleHolder.setDefaultActiviteitFont(FormuleFont.createFromFontSize(font_size));
			StubView.createDefaultFont(font_size);
			//AntwoordKeuzeVakGWT.setFontSize(font_size);
			defaultFontSize = font_size;
			defaultFontName = font_name;
			defaultFontName = defaultFontName.replace("SansSerif", "sans-serif");
			//AntwoordKeuzeVakGWT.setFont(fontName);
			
			if(wrap.containsKey("woordFormule"))
				FormuleParser.zetWoordFormule(wrap.getBoolean("woordFormule"));
			if(wrap.containsKey("tweeHoofdletterVar"))
				FormuleParser.zetTweeHoofdletterVariabele(wrap.getBoolean("tweeHoofdletterVar"));
			if(wrap.containsKey("significantie"))
				FormuleParser.zetSignificantie(wrap.getBoolean("significantie"));
			if(wrap.containsKey("fontOvererving"))
				TekstVakPanel.zetFontOvererving(wrap.getBoolean("fontOvererving"));
			if(wrap.containsKey("fontOverervingForm"))
			{
				FormuleEditorWithAnswer.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
				FormuleEditorWithSteps.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
				StelselOplossingenVak.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
				AntwoordTekstVak2.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
			}
			if(wrap.containsKey("scoresZichtbaar"))
				scoresZichtbaar = wrap.getBoolean("scoresZichtbaar");
			if(wrap.containsKey("volgendeKnopZichtbaar"))
				volgendeKnopZichtbaar = wrap.getBoolean("volgendeKnopZichtbaar");
			if(wrap.containsKey("vorigeKnopZichtbaar"))
				vorigeKnopZichtbaar = wrap.getBoolean("vorigeKnopZichtbaar");
			if(wrap.containsKey("bolletjesZichtbaar"))
				bolletjesZichtbaar = wrap.getBoolean("bolletjesZichtbaar");
			if(wrap.containsKey("condNav"))
				condNav = wrap.getBoolean("condNav");
			if(wrap.containsKey("condNavPerc"))
				condNavPerc = wrap.getBoolean("condNavPerc");
			if(wrap.containsKey("condNavVoorwaarden"))
				condNavVoorwaarden = wrap.getBoolean("condNavVoorwaarden");
			if(wrap.containsKey("navVoorwaarden")) //int[][][]
			{
				ObjectList navVoorwaardenList = wrap.getObjectList("navVoorwaarden");
				navVoorwaarden = new int[navVoorwaardenList.size()][][];
				for(int i = 0; i < navVoorwaardenList.size(); i++)
				{	ObjectList lijst = navVoorwaardenList.getObjectList(i);
					navVoorwaarden[i] = new int[lijst.size()][];
					for(int j = 0; j < lijst.size(); j++)
					{	try{
							navVoorwaarden[i][j] = lijst.getIntArray(j);
						}
						catch(Exception e)
						{}
					}
				}
			}
			if(wrap.containsKey("condPerc"))
				condPerc = wrap.getInt("condPerc");
			if(wrap.containsKey("allesCorrectNodig"))
				allesCorrectNodig = wrap.getBoolean("allesCorrectNodig");
			if(wrap.containsKey("zelftoetsGeenCorr"))
				zelftoetsGeenCorr = wrap.getBoolean("zelftoetsGeenCorr");
			if (wrap.containsKey("eerderGeenCorr"))
				eerderGeenCorr = wrap.getBoolean("eerderGeenCorr");
			if (wrap.containsKey("zelftoetsGeschiedenis"))
				zelftoetsGeschiedenis = wrap.getBoolean("zelftoetsGeschiedenis");
			if (wrap.containsKey("zelftoetsHighScore"))
				zelftoetsHighScore = wrap.getBoolean("zelftoetsHighScore");
			if (wrap.containsKey("timer"))
				timer = wrap.getBoolean("timer");
			if (wrap.containsKey("timeLimit"))
				timeLimitSeconds = wrap.getInt("timeLimit");
			
			if (wrap.containsKey("objectives"))
			{	
				ObjectList objectivesList = wrap.getObjectList("objectives");
				objectives = new String[objectivesList.size()][];
				for(int i = 0; i < objectives.length; i++)
				{	try{
					objectives[i] = objectivesList.getStringArray(i);
					}
					catch(Exception e)
					{}
				}
			}
			else
				objectives = null;
			if(wrap.containsKey("categorieString"))
				categorieString = wrap.getStringArray("categorieString");
			else
				categorieString = null;
			if(wrap.containsKey("pilotObjectives"))
				pilotObjectives = wrap.getBoolean("pilotObjectives");
			if (wrap.containsKey("misconceptions"))
			{	
				ObjectList misconceptionsList = wrap.getObjectList("misconceptions");
				misconceptions = new String[misconceptionsList.size()][];
				for(int i = 0; i < misconceptions.length; i++)
				{	try{
					misconceptions[i] = misconceptionsList.getStringArray(i);
					}
					catch(Exception e)
					{}
				}
			}
			else
				misconceptions = null;
			if(wrap.containsKey("mccCategorieString"))
				mccCategorieString = wrap.getStringArray("mccCategorieString");
			else
				mccCategorieString = null;
			OpdrNav.setObjectives(objectives);
			OpdrNav.setCategorieString(categorieString);
			OpdrNav.setMisconceptions(misconceptions);
			OpdrNav.setMccCategorieString(mccCategorieString);

			globalParam = wrap.getBoolean("globalParam", false);
			
			if(wrap.containsKey("TekstVakPanelStyles"))
				styles = wrap.getMap("TekstVakPanelStyles");
			if(styles!=null)
				TekstVakPanel.styles = styles;
			
			if(wrap.containsKey("templateName"))
				templateName = wrap.getString("templateName");
			DWOplayer.setTemplateCss(templateName);
			
		}
		
	}


	abstract AnchorContext getAnchorContext();

	public void setObjects(HashMap<String, Object> opdracht, final Panel destination, OpdrNavIF comRoot)
	{	
			
		int hoogte = 500;
		int breedte = 800;

		if(opdracht.containsKey("scheidingX"))
		{
			breedte = ((Number) opdracht.get("scheidingX")).intValue();
		}

		hoofdPanel = new TekstVakPanel(activity, breedte, hoogte, randomVarNamen, randomVarWaarden, getAnchorContext());
		hoofdPanel.setCommunicationRoot(comRoot);
		hoofdPanel.setHoofdPanel(true);
		hoofdPanel.zetInstellingen(instellingen);
		hoofdPanel.setKeyboard(kb);
		hoofdPanel.zetOpdracht(opdracht);
		
		
		
		
		
		int margeLinks = 0;
		int margeRechts = 0;
		
		destination.add(hoofdPanel);
		opdrachtObjects.add(hoofdPanel);
		//marges vanuit instellingen meenemen:
		Style style = hoofdPanel.asWidget().getElement().getStyle();
		if(instellingen.containsKey("margeOnder"))
			style.setMarginBottom( instellingen.getInt("margeOnder"), Style.Unit.PX);
		if(instellingen.containsKey("margeBoven"))
		style.setMarginTop(instellingen.getInt("margeBoven"), Style.Unit.PX);
		if(instellingen.containsKey("margeLinks")) {
			margeLinks = instellingen.getInt("margeLinks");
			style.setMarginLeft(margeLinks, Style.Unit.PX);
		}
		if(instellingen.containsKey("margeRechts")) {
			margeRechts = instellingen.getInt("margeRechts");
			style.setMarginRight(margeRechts, Style.Unit.PX);
		}
		
		final int ml = margeLinks;
		final int mr = margeLinks;
		
		ResizeHandler resize = new ResizeHandler() {
		  int clientWidth = -1;
			@Override
			public void onResize(ResizeEvent event) {
			  if (clientWidth != Window.getClientWidth()) {
			    clientWidth = Window.getClientWidth();
                final int w  = clientWidth-ml-mr;//-20;
                hoofdPanel.zetVolledigeBreedte(w);
			    
			  }
			}
		};
		//resize.onResize(null);
		
		if(RESPONSIVE) {
			breedte = Window.getClientWidth()-margeLinks-margeRechts;//-20;
			hoofdPanel.zetVolledigeBreedte(breedte);
			hoofdPanel.addResizeHandler(resize);
		}
	}
	
	public static int getDefaultFontSize()
	{
		return defaultFontSize;
	}
	
	public static String getDefaultFontName()
	{
		return defaultFontName;
	}
	
	public static String getDefaultFont()
	{
		return defaultFontSize + "px " + defaultFontName;
	}

	public boolean bolletjesZichtbaar()
	{
		return bolletjesZichtbaar;
	}

	  public void abort() {
	    logger = null; // no more output to user.
	  }

	Promise<Boolean> loadJSON(String file) {
		int is = file.lastIndexOf('=');
// no scoid=
		if(is == -1) {
			return loadJSON_org(file);
		}
// via rest interface.
		file = file.substring(is+1);
		Failure failure = new Failure() {
			
			@Override
			public void fail(Promise<?> resolved) throws Exception {
			    if (logger == null || OOPS.needed(resolved)) return;
				Throwable exception = resolved.getFailure();
				Window.alert(Text.constants.noJSONreceived() + 
						"\nerror " + exception);
				logger.log(Level.SEVERE, exception.toString(), exception);
			}
		};
		return getJSONLaunchDataBytes(file).recoverWith(OOPS).then(success, failure);
	}

  protected final RPCHandler rpc;
  protected final Success<JSONValue, Boolean> success = new Success<JSONValue, Boolean>() {

	@Override
	public Promise<Boolean> call(Promise<JSONValue> resolved) throws Exception {
		JSONValue response = resolved.getValue();
		JSONObjectMapImpl map;
		launchData = map = JSONUtilities.wrapMap(response.isObject());
		Promise<Boolean> p = Promises.resolved(needsPremium(map));
		if (!p.getValue()) setupView(launchData);
		return p;
	}
};
  protected XMLView(RPCHandler rpc, ActivityComponent a) {
	this.rpc = rpc;
	this.activity = a;
  }

  protected Promise<JSONValue> getJSONLaunchDataBytes(String file) {
    return rpc.getJSONLaunchDataBytes(file);
  }
	
	Promise<Boolean> loadJSON_org(String file) {
		 
			RequestBuilder.Method method = RequestBuilder.GET;
			String url = file;
			logger.info("request " + method + " " + url);
			logger.fine("requesting url = " + Window.Location.getHref());
			RequestBuilder rb = new RequestBuilder(method, url);
			rb.setTimeoutMillis(1000000);
			Deferred<Boolean> defer = new Deferred<>();
			try
			{
				rb.sendRequest(null, new RequestCallback()
				{
		
					@Override
					public void onResponseReceived(Request request, Response response)
					{
						String responseText = response.getText();
						logger.info("Status: " + response.getStatusCode() + " " + response.getStatusText());
						logger.info(response.getHeadersAsString());
						logger.info("Data: " + responseText.substring(0, Math.min(30, responseText.length()) ));
						if (responseText.length() > 4 && response.getStatusCode() == 200)
						{
							defer.resolve(setupView(responseText));
						} else {
							Window.alert(Text.constants.noJSONreceived());
							logger.severe("response empty");
							defer.fail(new RuntimeException(Text.constants.noJSONreceived()));
						}
					}
		
					@Override
					public void onError(Request request, Throwable exception)
					{
					    if (logger != null) {
    						Window.alert(Text.constants.noJSONreceived() + 
    								"\nerror " + exception);
    						logger.log(Level.SEVERE, exception.toString(), exception);
					    }
					    defer.fail(exception);
					}
				});
		
			}
			catch (RequestException e)
			{
				RootPanel.get().add(new Label("cannot load xml: " + e.getMessage()));
				defer.fail(e);
			}
		return defer.getPromise();
		
	}

	public boolean setupView(String launchDataString) {
		contentPanel.clear();
//		// voor huub: allow old XML data 
//		if(launchDataString.startsWith("<"))
//		{		
//			Document dom = XMLParser.parse(launchDataString);
//			StringCodeToHashMap sc = new StringCodeToHashMap();
//			launchData = sc.decodeStringToHashMap(dom);
//	
//		} else
		{
			JSONValue dom = JSONParser.parseStrict(launchDataString);
			//launchData = JSONUtilities.fromJSONObject(dom.isObject());
			launchData = JSONUtilities.wrapMap(dom.isObject());
		}
		if (needsPremium(JSONUtilities.wrapMap(launchData)))
			return true;
		setupView(launchData);
		return false;
	}
	
	protected boolean needsPremium(ObjectMap data) {
		boolean premiumfeatures = Boolean.TRUE.toString().equals(data.getString("premium"));
		return premiumfeatures && ! activity.isPremium();
	}

	/**
	 * Returns whether the activity is a tempo test.
	 * 
	 * @return
	 */
	public boolean isTempotoets()
	{
		return this.timer;
	}
}
