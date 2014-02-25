package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.interaction.client.touch.TouchCancelEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchEndEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchHandler;
import nl.uu.fi.dwo.interaction.client.touch.TouchMoveEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.AssetAPI;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_2004_API;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.StringCodeToHashMap;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.VariableCollection;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

/**
 * 
 * @author Danny Hendrix, Evertson Croes
 * 
 */
public class ViewModuleViewImpl extends XMLView implements ViewModuleView, EntryPoint
{
	private static final String RANDOM_VAR_WAARDEN = "RandomVarWaarden";
	private static final String RANDOM_VAR_NAMEN = "RandomVarNamen";
	private static Logger logger = Logger.getLogger("ViewModuleViewImpl");
	private boolean standalone = false;

	private OpdrNav on;
	private FocusPanel mainPanel;
	TouchPanel contentPanel = null;
	private ScrollPanel contentScrollPanel = null;
	private Panel tekst = null;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private double zoom = 1;

	private Panel kbp = null;
	private HeaderButton hb;
	private HeaderPanel hp;

	private Scorm2004IF api;

	/*public ViewModuleViewImpl()
	{
		
		
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");
		

		hp = new HeaderPanel();
		//hp.setCenter("Module 1");
		Style style = hp.getElement().getStyle();

		hb = new HeaderButton();
		hb.setBackButton(true);
		hb.setText("Home");

		hp.setLeftWidget(hb);

		mainPanel.add(hp);

		contentScrollPanel = new ScrollPanel();
		contentScrollPanel.setWidth("100%");
		contentScrollPanel.setHeight("100%");
		contentScrollPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

		contentPanel = new TouchPanel();
		contentPanel.getElement().getStyle().setProperty("display", "inline-block");
		contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		contentPanel.setWidth("99%");
		addContentPanelTouchListener(contentPanel);

		contentScrollPanel.setWidget(contentPanel);

		mainPanel.add(contentScrollPanel);

		kb = new FormuleKeyboard();
		Panel kbp = kb.getAsPanel();
		mainPanel.add(kbp);

		initWidget(mainPanel);
		
		 
	}*/

	public ViewModuleViewImpl(boolean b) {
		standalone = b;
	}
	
	public ViewModuleViewImpl() {
	}

	public HeaderButton getBackButton()
	{
		return hb;
	}

	@Override
	public void setupModule(String name, String file)
	{
		contentPanel.clear();
		if(DWOplayer.JSON) loadJSON(file); else loadXML(file);
		hp.setCenter(name);
	}

	private void loadJSON(String file) {
		 {
			RequestBuilder.Method method = RequestBuilder.GET;
			String url = file;
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
							JSONValue dom = JSONParser.parseStrict(responseText);
//							if(dom == null) 
//							{
//							}
							
							launchData = JSONUtilities.fromJSONObject(dom.isObject());
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
	}

	public void preSetupModule(final String link, final String url)
	{
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, link);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{

				@Override
				public void onResponseReceived(Request request, Response response)
				{

					String link = response.getText(); // another link? 
					if (response.getStatusCode() > 399)
						link = "";
					int i = link.indexOf('\r');
					if (i >= 0)
						link = link.substring(0, i);
					i = link.indexOf('\n');
					if (i >= 0)
						link = link.substring(0, i);
					if (!link.isEmpty())
					{
						setupModule(link, link);
					}
					else
					{
						setupModule(url, url);
					}

				}

				@Override
				public void onError(Request request, Throwable exception)
				{
					setupModule(url, url);

				}
			});
		}
		catch (RequestException ignore)
		{
			setupModule(url, url);
		}
	}

	public void clearContentPanel()
	{
		contentPanel.clear();
		PopupFacade.hide();	
		kb.setEditor(null);
	}

	public void setupView(HashMap<String, Object> launchData)
	{
		for (int i = 0; i < buttons.size(); i++)
			contentPanel.remove(buttons.get(i));

		super.setupView(launchData);


		contentPanel.getElement().getStyle().setFontSize(font_size, Unit.PX);
		contentPanel.getElement().getStyle().setPadding(15, Unit.PX);
		//FormuleHolder.setDefaultFont(FormuleFont.createFromFontSize(font_size));

		OpdrNav onimpl;
		on = onimpl = new OpdrNav(launchData, this, new Memento(api));
		FlowPanel onp = (FlowPanel) onimpl.getAsPanel();
		kb.addNavPanel(onp);
	}

	public void zetOpdracht(HashMap<String, Object> opdracht)
	{
		String randVarString = "";
		randVarString = (String) opdracht.get("randVarString");
		if(randVarString == null) randVarString = "";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

		String[] varnamen = null;
		HashMap waarden = null;
		//if(randomise)
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
				//RandomVarNamen = varnamen;
				//RandomVarWaarden = waarden;
			}
			catch (Exception ex)
			{
				wellSet = false;
			}
		}
		//else
		//{   varnamen = RandomVarNamen;
		//    waarden = RandomVarWaarden;
		//}

		//System.out.println("randvarnamen: "+varnamen[0]);
		//System.out.println("waarden: "+waarden);

		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(varnamen, waarden);
		newVersion = Boolean.FALSE.equals( opdracht.get("hasAntwoordVak") );
		//New editor version
		if (opdrachtGegevens != null || newVersion)
		{
			if (Boolean.TRUE.equals( opdracht.get("hasTitle")))
			{
				SimplePanel title = new SimplePanel();
				title.getElement().setInnerHTML((String) opdracht.get("titel") + "<br />");
				title.getElement().getStyle().setProperty("fontWeight", "bold");
				title.getElement().getStyle().setFontSize(font_size * 1.33, Unit.PX);
				title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				title.getElement().getStyle().setPaddingTop(5, Unit.PX);
				//title.getElement().getStyle().setFloat(Float.LEFT);
				contentPanel.add(title);
			}
			opdrachtObjects = tb.convertTekst(opdracht);
			int aantalVakken = 0;
			for (int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object currentObject = opdrachtObjects.get(i);
				if (currentObject instanceof InteractionView)
					((InteractionView) currentObject).setCommunicationRoot(on);
				//				System.out.println("" + on.toString());
				if (currentObject instanceof TekstVakPanel)
				{
					aantalVakken++;
					Object launchData = opdrachtGegevens.get(aantalVakken + 4); // FIXME Hier ook een +5-1 Wim
					((TekstVakPanel) currentObject).zetInstellingen(instellingen);
					((TekstVakPanel) currentObject).setKeyboard(kb);
					if(launchData != null)
					{  HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
						((TekstVakPanel) currentObject).zetOpdracht(launchState);
					}
				}
			}
			setObjects(opdrachtObjects, contentPanel);
		}
		else if (!newVersion)
		{ //Old editor version 
			if (opdrachtGegevens != null && opdrachtGegevens.size() == 1)
			{
				HashMap<String, Object> ips = (HashMap<String, Object>) opdrachtGegevens.get(0);
				HashMap<String, Object> state = (HashMap<String, Object>) ips.get("interactiePanelLaunchState");
				opdracht.put("antwoordString", state.get("antwoordString"));
			}

			setupOldVersion(opdracht, tb);
		}
	}

	public void zetOpdrachtPlusState(HashMap<String, Object> opdracht, HashMap<String, Object> state)
	{
		String randVarString;
		randVarString = (String) opdracht.get("randVarString");
		if(randVarString == null ) randVarString = "";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

		String[] varnamen = null;
		HashMap waarden = null;
		//if(randomise)
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
				//RandomVarNamen = varnamen;
				//RandomVarWaarden = waarden;
			}
			catch (Exception ex)
			{
				wellSet = false;
			}
		}
		//else
		//{   varnamen = RandomVarNamen;
		//    waarden = RandomVarWaarden;
		//}

		//System.out.println("randvarnamen: "+varnamen[0]);
		//System.out.println("waarden: "+waarden);

		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		if (state.get(RANDOM_VAR_NAMEN) != null)
			this.randomVarNamen = JSONUtilities.toStringArray(state.get(RANDOM_VAR_NAMEN));
		if (state.get(RANDOM_VAR_WAARDEN) != null)
			this.randomVarWaarden = (HashMap<String, Object>) state.get(RANDOM_VAR_WAARDEN);

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden);
		newVersion = !(Boolean) opdracht.get("hasAntwoordVak");
		//New editor version
		if (opdrachtGegevens != null || newVersion)
		{
			if ((Boolean) opdracht.get("hasTitle"))
			{
				SimplePanel title = new SimplePanel();
				title.getElement().setInnerHTML((String) opdracht.get("titel") + "<br />");
				title.getElement().getStyle().setProperty("fontWeight", "bold");
				title.getElement().getStyle().setFontSize(font_size * 1.33, Unit.PX);
				title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				title.getElement().getStyle().setPaddingTop(5, Unit.PX);
				//title.getElement().getStyle().setFloat(Float.LEFT);
				contentPanel.add(title);
			}
			opdrachtObjects = tb.convertTekst(opdracht);
			int aantalVakken = 0;
			for (int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object currentObject = opdrachtObjects.get(i);
				if (currentObject instanceof InteractionView)
					((InteractionView) currentObject).setCommunicationRoot(on);
				if (currentObject instanceof TekstVakPanel)
				{
					aantalVakken++;
					Object launchData = opdrachtGegevens.get(aantalVakken + 4); // nog een +5 voor het launchdata Wim
					((TekstVakPanel) currentObject).zetInstellingen(instellingen);
					((TekstVakPanel) currentObject).setKeyboard(kb);
					if (launchData != null)
					{	HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
						((TekstVakPanel) currentObject).zetOpdracht(launchState);
					}
				}
			}
			setObjects(opdrachtObjects, contentPanel);
		}
		else if (!newVersion)
		{ //Old editor version 
			if (opdrachtGegevens != null && opdrachtGegevens.size() == 1)
			{
				HashMap<String, Object> ips = (HashMap<String, Object>) opdrachtGegevens.get(0);
				HashMap<String, Object> interactiePanelLaunchState = (HashMap<String, Object>) ips.get("interactiePanelLaunchState");
				opdracht.put("antwoordString", interactiePanelLaunchState.get("antwoordString"));
			}

			setupOldVersion(opdracht, tb);
		}

		setState(state);
	}

	//Sets up a FormuleEditorWithSteps for each assignment
	private void setupOldVersion(HashMap<String, Object> opdracht, TekstBuffer tb)
	{
		//ArrayList<Object> opdrachtObjects;
		tekst = new FlowPanel();
		Object object = opdracht.get("scheidingX");
		if(object == null ) object = new Integer ( 0 );
		tekst.getElement().getStyle().setWidth((Integer) object / 8, Unit.PCT);
		tekst.getElement().getStyle().setFloat(Float.LEFT);
		tekst.getElement().getStyle().setPadding(5, Unit.PX);
		SimplePanel title = new SimplePanel();
		title.getElement().setInnerText((String) opdracht.get("titel"));
		title.getElement().getStyle().setProperty("fontWeight", "bold");
		title.getElement().getStyle().setFontSize(font_size * 2, Unit.PX);
		title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		title.getElement().getStyle().setPaddingTop(5, Unit.PX);
		tekst.add(title);
		opdrachtObjects = tb.convertTekst(opdracht);
		setObjects(opdrachtObjects, tekst);
		contentPanel.add(tekst);
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(opdracht, false, tb.getVarNamen(), tb.getVarWaarden());

		//fews.getEditor().requestFocus();
		

		contentPanel.add(fews.getAsPanel());
	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.on = (OpdrNav) comRoot;
	}

	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		int aantalInteractionViews = 5;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				aantalInteractionViews++;
			}
		}
		ArrayList<Object> states = new ArrayList<Object>(aantalInteractionViews + 5);
		for (int i = 0; i < 5; i++)
			states.add(null);
		aantalInteractionViews = 5;

		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				states.add(aantalInteractionViews, ((InteractionView) currentObject).getState());
				aantalInteractionViews++;
			}
		}
		h.put("interactiePanelStates", states);
		h.put(RANDOM_VAR_NAMEN, randomVarNamen);
		h.put(RANDOM_VAR_WAARDEN, randomVarWaarden);
		return h;
	}

	public void setState(HashMap<String, Object> h)
	{
		if (h.get(RANDOM_VAR_NAMEN) != null)
			this.randomVarNamen = JSONUtilities.toStringArray(h.get(RANDOM_VAR_NAMEN));
		if (h.get(RANDOM_VAR_WAARDEN) != null)
			this.randomVarWaarden = (HashMap<String, Object>) h.get(RANDOM_VAR_WAARDEN);
		List<Object> states = JSONUtilities.toArrayList(h.get("interactiePanelStates"));
		int stateNr = 5;
		for (int i = 0; states != null && i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				HashMap<String, Object> state = (HashMap<String, Object>) states.get(stateNr);
				((InteractionView) currentObject).setState(state);
				stateNr++;
			}
		}

	}

	public int getScore()
	{
		int score = 0;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				score += ((InteractionView) currentObject).getScore();
			}
		}
		return score;
	}

	public boolean isCorrect()
	{
		boolean correct = true;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				correct = correct && ((InteractionView) currentObject).isCorrect();
			}
		}
		return correct;
	}

//	private Panel getFormuleKeyboard(FormuleEditor editor)
//	{
//		if (kb == null)
//			kb = new FormuleKeyboard();
//		kb.setEditor(editor);
//		Panel kbp = kb.getAsPanel();
//
//		kbp.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
//		kbp.getElement().getStyle().setBottom(0, Style.Unit.PX);
//		kbp.getElement().getStyle().setLeft(0, Style.Unit.PX);
//
//		return kbp;
//	}


	private void addContentPanelTouchListener(TouchPanel contentPanel)
	{
		final HashMap<String, Double> dif = new HashMap<String, Double>();
		contentPanel.addTouchHandler(new TouchHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				if (event.touches().size() == 2)
				{
					event.stopPropagation();
					double touch1X = event.touches().get(0).getPageX();
					double touch1Y = event.touches().get(0).getPageY();
					double touch2X = event.touches().get(1).getPageX();
					double touch2Y = event.touches().get(1).getPageY();
					dif.put("x", Math.abs(touch1X - touch2X));
					dif.put("y", Math.abs(touch1Y - touch2Y));
				}

			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				double difx;
				double dify;

				if (event.touches().size() == 2)
				{
					event.stopPropagation();
					difx = Math.abs(event.touches().get(0).getPageX() - event.touches().get(1).getPageX());
					dify = Math.abs(event.touches().get(0).getPageY() - event.touches().get(1).getPageY());
					double ratio = 0;

					if (dif.get("x") - difx > 0 && dif.get("y") - dify > 0)
					{
						ratio = ((dif.get("x") - difx) + (dif.get("y") - dify)) / 200;
						DWOplayer.log("ratio: " + ratio);
						zoomOut(ratio);
					}
					else if (dif.get("x") - difx < 0 && dif.get("y") - dify < 0)
					{
						ratio = ((Math.abs(dif.get("x") - difx)) + (Math.abs(dif.get("y") - dify))) / 200;
						DWOplayer.log("ratio: " + ratio);
						zoomIn(ratio);
					}
				}
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}

		});
	}

	public OpdrNavIF getOpdrNav()
	{
		return on;
	}

	public void zoomIn(double ratio)
	{
		if (zoom < 3)
		{
			zoom = zoom + ratio;
			if (zoom > 3)
			{
				zoom = 3;
			}
			//contentPanel.getElement().getStyle().setProperty("zoom", Double.toString(zoom));
			//if(kb!=null)kb.zoomIn();
		}

	}

	public void zoomOut(double ratio)
	{
		if (zoom > 1)
		{
			zoom = zoom - ratio;
			if (zoom < 1)
			{
				zoom = 1;
			}
			//if(kb!=null)kb.zoomOut();
			//contentPanel.getElement().getStyle().setProperty("zoom", Double.toString(zoom));
		}
	}

	public ViewModuleViewImpl initialize()
	{
		api = GWT.create(Scorm2004IF.class);
		FlowPanel fp = new FlowPanel();
		mainPanel = FocusOnTouch.wrap(fp);
		
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");

		kb = new FormuleKeyboard();
		FocusOnTouch.installKeyboard(kb);
		FormuleHolder.installKeyboard(kb);
		
		hp = new HeaderPanel();
		hp.setCenter("Module 1");
		Style style = hp.getElement().getStyle();

		hb = new HeaderButton();
		hb.setBackButton(true);
		hb.setText("Home");

		hp.setLeftWidget(hb);

		if(!standalone) fp.add(hp);

		contentScrollPanel = new ScrollPanel();
		contentScrollPanel.setWidth("100%");
		contentScrollPanel.setHeight("100%");
		contentScrollPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

		contentPanel = new TouchPanel();
		contentPanel.getElement().getStyle().setProperty("display", "inline-block");
		contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		contentPanel.setWidth("99%");
		//addContentPanelTouchListener(contentPanel);

		contentScrollPanel.setWidget(contentPanel);
		//contentScrollPanel.setScrollingEnabledX(false);
		//contentScrollPanel.setScrollingEnabledY(false);

		fp.add(contentScrollPanel);

		Panel kbp = kb.getAsPanel();

		fp.add(kbp);

		//initWidget(mainPanel);
		return this;

	}

	@Override
	public void onModuleLoad()
	{
		standalone = true;
		initialize();
		String url = "index.xml";
		String link = "index.xmr"; // reference.
		String path = Window.Location.getPath();
		// strip basename
		int slash = path.lastIndexOf('/');
		//if (slash >= 0)
		//	path = path.substring(slash + 1);
		// strip extension
		int dot = path.lastIndexOf('.');
		if (dot > 0)
		{
			path = path.substring(0, dot);
		}
		if (!path.isEmpty())
		{
			url = path + ".xml";
			link = path + ".xmr";
		}
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(true);
		//viewport.setMinimumScale(0.2).setInitialScale(1.0).setMaximumScale(5);

		viewport.setMinimumScale(0.15);
		viewport.setMaximumScale(3.0);
		//viewport.setWidthToDeviceWidth();
		//viewport.setHeightToDeviceHeight();
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		//settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		//RootPanel.get("viewholder").add(new Label("titel"));
		RootPanel.get("main").add(this);

		RequestBuilder.Method method = RequestBuilder.GET;
		preSetupModule(link, url);

		//contentPanel.add(kbp);

		/*RequestBuilder rb = new RequestBuilder(method, url);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{

				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					setupModule("",responseText);
					
				}

				@Override
				public void onError(Request request, Throwable exception)
				{
					Window.alert("error loading activity.xmx");
				}
			});

		}
		catch (RequestException e)
		{
			Window.alert("error loading activity.xmx");
		}*/

		//

		FocusOnTouch.requestFocus(mainPanel);

	}

	@Override
	public Widget asWidget()
	{
		return mainPanel;
	}

	public Scorm2004IF getApi() {
		return api;
	}

	public void setApi(Scorm2004IF api) {
		this.api = api;
	}

	@Override
	public void close() {
		if(on != null)
			on.close();
		PopupFacade.removeAll();
	}

	public FormuleKeyboard getKeyboard() {
		return kb;
	}

}
