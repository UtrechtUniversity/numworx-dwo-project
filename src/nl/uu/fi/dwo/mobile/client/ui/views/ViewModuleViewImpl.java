package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleFont;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.InteractionView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT;
import nl.uu.fi.dwo.mobile.utils.StringCodeToHashMap;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.VariableCollection;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

/**
 * 
 * @author Danny Hendrix, Evertson Croes
 * 
 */
public class ViewModuleViewImpl extends Composite implements ViewModuleView, EntryPoint
{
	private HashMap<String, Object> launchData, instellingen;
	private OpdrNav on;
	private Panel mainPanel;
	private TouchPanel contentPanel = null;
	private ScrollPanel contentScrollPanel = null;
	private Panel tekst = null;
	private int font_size = 12;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private FormuleKeyboard kb = null;
	private double zoom = 1;

	private Panel kbp = null;
	private HeaderButton hb;
	private HeaderPanel hp;
	
	private ArrayList<Object> opdrachtObjects;
	private boolean newVersion = true;
	
	private String[] randomVarNamen = null;
	private HashMap randomVarWaarden = null;

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

	
	public HeaderButton getBackButton()
	{
		return hb;
	}

	@Override
	public void setupModule(String name, String file)
	{
		contentPanel.clear();
		loadXML(file);
		//loadTest();
		hp.setCenter(name);
	}

	public void clearContentPanel()
	{
		contentPanel.clear();
	}

	public void loadXML(String xmlPath)
	{
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
					if (!responseText.isEmpty())
					{
						for (int i = 0; i < buttons.size(); i++)
							contentPanel.remove(buttons.get(i));
						Document dom = XMLParser.parse(responseText);
						StringCodeToHashMap sc = new StringCodeToHashMap();
						launchData = sc.decodeStringToHashMap(dom);
						setupView(launchData);
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

	public void setupView(HashMap<String, Object> launchData)
	{
		
		
		this.launchData = launchData;
		
		if (launchData.get("instellingen") != null)
			instellingen = (HashMap<String, Object>) launchData.get("instellingen");
		if (instellingen.get("fontSize") != null)
			font_size = (Integer) instellingen.get("fontSize");
		
		boolean maalTeken = (Boolean)instellingen.get("maalTeken");
		FormuleTeken.zetMaalTeken(maalTeken);
		
		contentPanel.getElement().getStyle().setFontSize(font_size, Unit.PX);
		contentPanel.getElement().getStyle().setPadding(15, Unit.PX);
		//FormuleHolder.setDefaultFont(FormuleFont.createFromFontSize(font_size));

		on = new OpdrNav(launchData, this);
		FlowPanel onp = (FlowPanel) on.getAsPanel();
		kb.addNavPanel(onp);
	}

	public void zetOpdracht(HashMap<String, Object> opdracht)
	{
		String randVarString = "";
		randVarString = (String)opdracht.get("randVarString");
		VariableCollection vc = new VariableCollection();
        boolean wellSet = vc.setVariables(randVarString);
        
        String[] varnamen = null;
        HashMap waarden = null;
        //if(randomise)
        {   try
            {   varnamen = vc.getVariableNames();
                waarden = vc.getRandomValues();
                //RandomVarNamen = varnamen;
                //RandomVarWaarden = waarden;
            }
            catch(Exception ex)
            {   wellSet = false;
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
		ArrayList<Object> opdrachtGegevens = (ArrayList<Object>) opdracht.get("interactiePanelLaunchData");
		TekstBuffer tb = new TekstBuffer(varnamen, waarden);
		newVersion = !(Boolean)opdracht.get("hasAntwoordVak");
		//New editor version
		if (opdrachtGegevens != null || newVersion )
		{
			if((Boolean)opdracht.get("hasTitle"))
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
				if(currentObject instanceof InteractionView)((InteractionView)currentObject).setCommunicationRoot(on);
				System.out.println(""+on.toString());
				if(currentObject instanceof TekstVakPanel)
				{	aantalVakken++;
					Object launchData = opdrachtGegevens.get(aantalVakken);
					HashMap<String, Object> launchState = (HashMap<String, Object>)((HashMap<String, Object>)launchData).get("interactiePanelLaunchState");
					((TekstVakPanel)currentObject).zetInstellingen(instellingen);
					((TekstVakPanel)currentObject).setKeyboard(kb);
					((TekstVakPanel)currentObject).zetOpdracht(launchState);
				}
			}
			setObjects(opdrachtObjects, contentPanel);
		}
		else if(!newVersion) 
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
		String randVarString = "";
		randVarString = (String)opdracht.get("randVarString");
		VariableCollection vc = new VariableCollection();
        boolean wellSet = vc.setVariables(randVarString);
        
        String[] varnamen = null;
        HashMap waarden = null;
        //if(randomise)
        {   try
            {   varnamen = vc.getVariableNames();
                waarden = vc.getRandomValues();
                //RandomVarNamen = varnamen;
                //RandomVarWaarden = waarden;
            }
            catch(Exception ex)
            {   wellSet = false;
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
        
        if(state.get("randomVarNamen")!=null)this.randomVarNamen = (String[])state.get("randomVarNamen");
		if(state.get("randomVarWaarden")!=null)this.randomVarWaarden = (HashMap<String, Object>)state.get("randomVarWaarden");
        
		opdrachtObjects = new ArrayList<Object>();
		ArrayList<Object> opdrachtGegevens = (ArrayList<Object>) opdracht.get("interactiePanelLaunchData");
		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden);
		newVersion = !(Boolean)opdracht.get("hasAntwoordVak");
		//New editor version
		if (opdrachtGegevens != null || newVersion )
		{
			if((Boolean)opdracht.get("hasTitle"))
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
				if(currentObject instanceof InteractionView)((InteractionView)currentObject).setCommunicationRoot(on);
				System.out.println(""+on.toString());
				if(currentObject instanceof TekstVakPanel)
				{	aantalVakken++;
					Object launchData = opdrachtGegevens.get(aantalVakken);
					HashMap<String, Object> launchState = (HashMap<String, Object>)((HashMap<String, Object>)launchData).get("interactiePanelLaunchState");
					((TekstVakPanel)currentObject).zetInstellingen(instellingen);
					((TekstVakPanel)currentObject).setKeyboard(kb);
					((TekstVakPanel)currentObject).zetOpdracht(launchState);
				}
			}
			setObjects(opdrachtObjects, contentPanel);
		}
		else if(!newVersion) 
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
		tekst.getElement().getStyle().setWidth((Integer)opdracht.get("scheidingX")/8, Unit.PCT);
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

		kb.setEditor(fews.getEditor());
		fews.setKeyboard(kb);

		contentPanel.add(fews.getAsPanel());
	}
	
	public void setCommunicationRoot(OpdrNav comRoot)
	{	this.on = comRoot;
	}
	
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		int aantalInteractionViews = 5;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{	Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof InteractionView)
			{	aantalInteractionViews++;
			}
		}
		ArrayList<Object> states =  new ArrayList<Object>(aantalInteractionViews+5);
		for (int i = 0; i < 5; i++)states.add(null);
		aantalInteractionViews = 5;
		
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{	Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof InteractionView)
			{	states.add(aantalInteractionViews,((InteractionView)currentObject).getState());
				aantalInteractionViews++;
			}
		}
		h.put("interactiePanelStates", states);
		h.put("randomVarNamen", randomVarNamen);
		h.put("randomVarWaarden", randomVarWaarden);
		return h;
	}
	
	public void setState(HashMap<String, Object> h)
	{	
		if(h.get("randomVarNamen")!=null)this.randomVarNamen = (String[])h.get("randomVarNamen");
		if(h.get("randomVarWaarden")!=null)this.randomVarWaarden = (HashMap<String, Object>)h.get("randomVarWaarden");
		ArrayList<Object> states = (ArrayList<Object>) h.get("interactiePanelStates");
		int stateNr = 5;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{	Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof InteractionView)
			{	HashMap<String, Object> state = (HashMap<String, Object>)states.get(stateNr);
				((InteractionView)currentObject).setState(state);
				stateNr++;
			}
		}
		
	}
	
	public int getScore()
	{	int score = 0;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{	Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof InteractionView)
			{	score += ((InteractionView)currentObject).getScore();
			}
		}
		return score;
	}
	
	public boolean isCorrect()
	{	boolean correct = true;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{	Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof InteractionView)
			{	correct = correct && ((InteractionView)currentObject).isCorrect();
			}
		}
		return correct;
	}

	//Puts objects on screen
	public void setObjects(ArrayList<Object> opdrachtObjects, Panel destination)
	{

		for (int i = 0; i < opdrachtObjects.size(); i++)
		{

			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof String)
			{
				Element element = DOM.createSpan();
				element.setInnerHTML((String) currentObject);
				//element.getElement().getStyle().setFloat(Float.LEFT);
				destination.getElement().appendChild(element);

				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String)
					destination.getElement().appendChild(DOM.createElement("br"));
				//destination.add(new HTML((String) currentObject));
				//destination.getElement().setInnerHTML(destination.getElement().getInnerHTML() + ((String) currentObject));
				//element.setInnerHTML((String) currentObject);
				//element.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				//element.getElement().getStyle().setPaddingTop(5, Unit.PX);

			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{	((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				int asHoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getHeight();
				Panel a = getPanelElement((FormuleEditorWithAnswer) currentObject);
				//((FormuleEditorWithAnswer) currentObject).getMainRegel().getCanvas().getElement().getStyle().setMarginBottom(-3, Unit.PX);
				//a.getElement().getStyle().setMarginBottom(-4, Unit.PX);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("position", "relative");
				a.getElement().getStyle().setProperty("top", (hoogte-asHoogte-Math.rint(font_size*0.33)-2)+"px");
				kb.setEditor((FormuleEditorWithAnswer) currentObject);
				destination.add(a);
			}
			else if (currentObject instanceof FormuleViewer)
			{	
				((FormuleViewer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				int asHoogte = ((FormuleViewer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleViewer) currentObject).getMainRegel().getHeight();
				Panel a = ((FormuleViewer) currentObject).getAsPanel();
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("position", "relative");
				a.getElement().getStyle().setProperty("top", (hoogte-asHoogte-Math.rint(font_size*0.33))+"px");
				
				destination.add(a);
				
				
				
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Panel a = ((FormuleEditorWithSteps) currentObject).getAsPanel();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				kb.setEditor(((FormuleEditorWithSteps) currentObject).getEditor());
				((FormuleEditorWithSteps) currentObject).setKeyboard(kb);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "top");
				
				destination.add(a);
			}
			
			else if (currentObject instanceof NabouwenAanzichtenGWT)
			{
				Panel a = ((NabouwenAanzichtenGWT) currentObject).getAsPanel();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", (-font_size*0.45)+"px");
				//a.getElement().getStyle().setProperty("position", "relative");
				//a.getElement().getStyle().setProperty("top", (-font_size*0.1)+"px");
				
				destination.add(a);
			}
			else if (currentObject instanceof TekstVakPanel)
			{
				Panel a = ((TekstVakPanel) currentObject).getAsPanel();
				//a.getElement().getStyle().setFloat(Float.LEFT);
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "top");
				
				destination.add(a);
			}

		}

	}

	public Panel getPanelElement(final FormuleEditor editor)
	{
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

	private Panel getFormuleKeyboard(FormuleEditor editor)
	{
		if (kb == null)
			kb = new FormuleKeyboard();
		kb.setEditor(editor);
		Panel kbp = kb.getAsPanel();

		kbp.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		kbp.getElement().getStyle().setBottom(0, Style.Unit.PX);
		kbp.getElement().getStyle().setLeft(0, Style.Unit.PX);

		return kbp;
	}

	private void addFormulePanelListeners(final TouchPanel tp, final FormuleEditor editor)
	{
		tp.addTouchHandler(new FormuleEditorTouchHandler(tp, kb, editor));
	}

	private void addContentPanelTouchListener(TouchPanel contentPanel)
	{
		final HashMap<String, Double> dif = new HashMap<String, Double>();
		contentPanel.addTouchHandler(new TouchHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				if (event.touches().length() == 2)
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

				if (event.touches().length() == 2)
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

	public OpdrNav getOpdrNav()
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


	@Override
	public void onModuleLoad() {
		
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		//viewport.setUserScaleAble(true);//.setMinimumScale(1.0).setMaximumScale(1.0);
		viewport.setWidthToDeviceWidth();
		//viewport.setHeightToDeviceHeight();
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		//settings.setPreventScrolling(true);
		MGWT.applySettings(settings);
		
		mainPanel = new FlowPanel();
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");
		
		
		

		hp = new HeaderPanel();
		//hp.setCenter("Module 1");
		Style style = hp.getElement().getStyle();

		hb = new HeaderButton();
		hb.setBackButton(true);
		hb.setText("Home");

		hp.setLeftWidget(hb);

		//mainPanel.add(hp);

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
		

		//initWidget(mainPanel);
		
		
		
		//RootPanel.get("viewholder").add(new Label("titel"));
		RootPanel.get("main").add(mainPanel);
		
		
		
		
		RequestBuilder.Method method = RequestBuilder.GET;
		String url = "activity.xmx";
		setupModule(url,url);
		
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
	}
}
