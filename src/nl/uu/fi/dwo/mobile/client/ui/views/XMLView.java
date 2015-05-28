package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.StringCodeToHashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
/**
 * Common code voor ViewModuleViewImpl en DescriptionViewImpl.
 * @author wim
 *
 */
public abstract class XMLView {

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
	private static Logger logger = Logger.getLogger("XMLView");
	private static int defaultFontSize = 12;
	private static String defaultFontName = "Arial";
	
	protected boolean bolletjesZichtbaar = true;
	protected boolean volgendeKnopZichtbaar = false;
	protected boolean vorigeKnopZichtbaar = false;
	
	protected boolean condNav = false;
	protected boolean condNavPerc = false;
	protected boolean condNavVoorwaarden = false;
	protected int[][][] navVoorwaarden = null;
	public boolean[][] bezocht = null;
	protected int condPerc = 100;
	
	protected boolean allesCorrectNodig = false;
	public boolean zelftoetsGeenCorr = false;
	
	TekstVakPanel hoofdPanel;

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
			boolean hoekGraden = wrap.getBoolean("hoekGraden");
			Expressie.zetHoekGraden(hoekGraden);
			if(wrap.containsKey("fontName")) // optional
			{	font_name = wrap.getString("fontName");
				if(font_name.equals("SansSerif"))
					font_name = "Arial";
			} else font_name = "Arial";
			FormuleFont.zetDefaultFont(font_name);
			boolean formTimes = wrap.containsKey("formTimes") && wrap.getBoolean("formTimes"); // optional
			FormuleFont.zetFormTimes(formTimes);
			FormuleHolder.setDefaultActiviteitFont(FormuleFont.createFromFontSize(font_size));
			//AntwoordKeuzeVakGWT.setFontSize(font_size);
			defaultFontSize = font_size;
			defaultFontName = font_name;
			//AntwoordKeuzeVakGWT.setFont(fontName);
			
			if(wrap.containsKey("woordFormule"))
				FormuleParser.zetWoordFormule(wrap.getBoolean("woordFormule"));	
			if(wrap.containsKey("fontOvererving"))
				TekstVakPanel.zetFontOvererving(wrap.getBoolean("fontOvererving"));
			if(wrap.containsKey("fontOverervingForm"))
			{
				FormuleEditorWithAnswer.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
				FormuleEditorWithSteps.zetFontOverervingForm(wrap.getBoolean("fontOverervingForm"));
			}
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
			
			
		}
		
	}

	protected void loadXML(String xmlPath) {
		RequestBuilder.Method method = RequestBuilder.GET;
		String url = xmlPath;
		RequestBuilder rb = new RequestBuilder(method, url);
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
					logger.info("Data: " + responseText.substring(0, Math.min(300, responseText.length()) ));
					if (!responseText.isEmpty())
					{
						Document dom = XMLParser.parse(responseText);
//						if(dom == null) 
//						{
//						}
						StringCodeToHashMap sc = new StringCodeToHashMap();
						launchData = sc.decodeStringToHashMap(dom);
						setupView(launchData);
					} else {
						logger.severe("response empty");
					}
	
				}
	
				@Override
				public void onError(Request request, Throwable exception)
				{
					Window.alert("error");
				}
			});
	
		}
		catch (RequestException e)
		{
			RootPanel.get().add(new Label("cannot load xml: " + e.getMessage()));
		}
	}

	//public void setObjects(ArrayList<Object> opdrachtObjects, Panel destination) 
	public void setObjects(HashMap<String, Object> opdracht, final Panel destination, OpdrNavIF comRoot)
	{	
			
		int hoogte = 500;
		int breedte = 800;
		
		if(opdracht.containsKey("scheidingX"))
		{
			breedte = ((Number) opdracht.get("scheidingX")).intValue();
		}

		if(!MGWT.getOsDetection().isDesktop() 
				&& false // FIXME staat uit omdat Graphtool clientX en clientY niet goed doet.
		) {
			final int width=breedte;

		// FIXED is dit op tijd? ALLEEN OP TABLET!!!!
			ResizeHandler resize = new ResizeHandler() {

				@Override
				public void onResize(ResizeEvent event) {
					double factor = Window.getClientWidth();
					logger.info("zoom " + factor + " / " + width);
					factor = factor / width;
					Style style = destination.getElement().getStyle();
					if(factor > 0.6 && factor < 1.4)
					{
						style.setProperty("zoom", String.valueOf(factor));
					} else {
						style.clearProperty("zoom");			
					}
				}
			};
			resize.onResize(null);
			Window.addResizeHandler(resize);
		}
// FIXME ....	
		hoofdPanel = new TekstVakPanel(breedte, hoogte, randomVarNamen, randomVarWaarden);
		hoofdPanel.setCommunicationRoot(comRoot);
		hoofdPanel.zetInstellingen(instellingen);
		hoofdPanel.setKeyboard(kb);
		hoofdPanel.setHoofdPanel(true);
		hoofdPanel.zetOpdracht(opdracht);
		
		destination.add(hoofdPanel);
		opdrachtObjects.add(hoofdPanel);
		//marges vanuit instellingen meenemen:
		Style style = hoofdPanel.asWidget().getElement().getStyle();
		style.setMarginBottom( instellingen.getInt("margeOnder"), Style.Unit.PX);
		style.setMarginTop(instellingen.getInt("margeBoven"), Style.Unit.PX);
		style.setMarginLeft(instellingen.getInt("margeLinks"), Style.Unit.PX);
		style.setMarginRight(instellingen.getInt("margeRechts"), Style.Unit.PX);		
	}

	public Panel getPanelElement(final FormuleHolder editor) {
		FlowPanel fp = new FlowPanel();
		editor.paint();
	
		final Panel p = editor.getAsPanel();
	
		if (p instanceof TouchPanel)
		{
			TouchPanel tp = (TouchPanel) p;
			this.addFormulePanelListeners(tp, editor);
		}
	
		fp.add(p);
		return p;
	}
	
	public static int getDefaultFontSize()
	{
		return defaultFontSize;
	}
	
	public static String getDefaultFont()
	{
		return defaultFontSize + "px " + defaultFontName;
	}

	private void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor) {
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}
	
	public boolean bolletjesZichtbaar()
	{
		return bolletjesZichtbaar;
	}
}
