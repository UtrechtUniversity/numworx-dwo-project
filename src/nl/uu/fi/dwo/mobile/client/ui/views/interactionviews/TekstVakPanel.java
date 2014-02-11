package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Aftrekking;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Optelling;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;

public class TekstVakPanel implements InteractionView
{
	private int font_size = 12;
	private int font_style = 0;
	private FormuleKeyboard kb = null;
	private OpdrNavIF comRoot = null;
	private int breedte = 600;
	private int hoogte = 250;
	private HashMap<String, Object> launchState, instellingen;
	private LayoutPanel mainPanel2 = null;
	private Grid mainPanel = null;
	private FlowPanel randPanel = null;
	private LayoutPanel[][] tekstHulsVakken = null;
	private FlowPanel[][] tekstVakken = null;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	private LayoutPanel parent = null;

	ArrayList<Object> interactionViewObjects = new ArrayList<Object>();

	List<Object> breedtes = null;
	List<Object> hoogtes = null;
	int cellSpaceColumn = 0;
	int cellSpaceRow = 0;
	int cellMarge = 0;
	int bovenMarge = 0;
	CssColor bgColor = CssColor.make(255, 255, 255);
	CssColor fgColor = CssColor.make(0, 0, 0);
	CssColor randColor = CssColor.make(150, 150, 150);
	CssColor selectionColor = CssColor.make(255, 128, 0);
	CssColor grijs = CssColor.make(128, 128, 128);
	int randDikte = 0;
	private boolean popup;
	
	private boolean sleepdoel = false;
	private boolean sleepHandle = false;
	private int sleepdoelMarge = 10;
	private boolean sleepSnap = false;
	
	private boolean selectable;
	private boolean sleepbaar;
	private boolean selected;
	private String checkExpressieString = "$f1@";
	private boolean defaultBijNull;
	private int ipId = 0;
	private boolean colorSelection;
	private boolean zwevend;
	
	private Point[] doelPosities;
	private TekstVakPanel[] sleepObjecten;

	private boolean relocate = false;
	private int startSleepX;
	private int startSleepY;
	
	private int locationX, locationY;
	private int startX, startY;

	
	static CssColor getColor(Map<String,Object> map, String key, int r, int g, int b) {
		Map colorMap = (Map)map.get(key);
		if(map != null) {
			r = ((Number)colorMap.get("red")).intValue();
			g = ((Number)colorMap.get("green")).intValue();
			b = ((Number)colorMap.get("blue")).intValue();
		}
		return CssColor.make(r, g, b);
	}
	
	
	
	
	public TekstVakPanel(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		facade = new PopupFacade(h);
		if (h != null && h.get("breedte") != null)
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.get("hoogte") != null)
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		System.out.println("launchState: " + launchState);
		boolean bgColorZichtbaar = false;
		boolean randZichtbaar = false;
		boolean tableBorders = false;
		boolean centerV = false;
		boolean centerH = false;

		int bgColor_red = 255;
		int bgColor_green = 255;
		int bgColor_blue = 255;
		int fgColor_red = 0;
		int fgColor_green = 0;
		int fgColor_blue = 0;
		int randColor_red = 0;
		int randColor_green = 0;
		int randColor_blue = 0;

		int ronding = 0;

		if (launchState != null && launchState.get("breedtes") != null)
			breedtes = Memento.toArrayList( launchState.get("breedtes") );
		else
			breedtes = new ArrayList<Object>(Arrays.asList(600.0));
		if (launchState != null && launchState.get("hoogtes") != null)
			hoogtes = Memento.toArrayList( launchState.get("hoogtes") );
		else
			hoogtes = new ArrayList<Object>(Arrays.asList(250.0));
		
		if (launchState != null && launchState.get("cellSpaceColumn") != null)
			cellSpaceColumn = ((Number) launchState.get("cellSpaceColumn")).intValue();
		if (launchState != null && launchState.get("cellSpaceRow") != null)
			cellSpaceRow = ((Number) launchState.get("cellSpaceRow")).intValue();
		if (launchState != null && launchState.get("cellMarge") != null)
			cellMarge = ((Number) launchState.get("cellMarge")).intValue();
		if (launchState != null && launchState.get("bovenMarge") != null)
			bovenMarge = ((Number) launchState.get("bovenMarge")).intValue();
		if (launchState != null && launchState.get("ronding") != null)
			ronding = ((Number) launchState.get("ronding")).intValue();
		if (launchState != null && launchState.get("bgColorZichtbaar") != null)
			bgColorZichtbaar = (Boolean) launchState.get("bgColorZichtbaar");
		if (launchState != null && launchState.get("bgColor_red") != null)
			bgColor_red = ((Number) launchState.get("bgColor_red")).intValue();
		if (launchState != null && launchState.get("bgColor_green") != null)
			bgColor_green = ((Number) launchState.get("bgColor_green")).intValue();
		if (launchState != null && launchState.get("bgColor_blue") != null)
			bgColor_blue = ((Number) launchState.get("bgColor_blue")).intValue();
		if (launchState != null && launchState.get("fgColor_red") != null)
			fgColor_red = ((Number) launchState.get("fgColor_red")).intValue();
		if (launchState != null && launchState.get("fgColor_green") != null)
			fgColor_green = ((Number) launchState.get("fgColor_green")).intValue();
		if (launchState != null && launchState.get("fgColor_blue") != null)
			fgColor_blue = ((Number) launchState.get("fgColor_blue")).intValue();
		if (launchState != null && launchState.get("randZichtbaar") != null)
			randZichtbaar = (Boolean) launchState.get("randZichtbaar");
		if (launchState != null && launchState.get("randColor_red") != null)
			randColor_red = ((Number) launchState.get("randColor_red")).intValue();
		if (launchState != null && launchState.get("randColor_green") != null)
			randColor_green = ((Number) launchState.get("randColor_green")).intValue();
		if (launchState != null && launchState.get("randColor_blue") != null)
			randColor_blue = ((Number) launchState.get("randColor_blue")).intValue();
		if (launchState != null && launchState.get("randDikte") != null)
			randDikte = ((Number) launchState.get("randDikte")).intValue();
		if (launchState != null && launchState.get("font_size") != null)
			font_size = ((Number) launchState.get("font_size")).intValue();
		if (launchState != null && launchState.get("font_style") != null)
			font_style = ((Number) launchState.get("font_style")).intValue();
		
		if(launchState != null && launchState.containsKey("font")) {
			Map m = (Map) launchState.get("font");
			font_size = ((Number) m.get("size")).intValue();
			font_style = ((Number) m.get("style")).intValue();
		}
		
		
		
		if (launchState != null && launchState.get("selectable") != null)
			selectable = ((Boolean) launchState.get("selectable")).booleanValue(); 
		if (launchState != null && launchState.get("sleepbaar") != null)
			sleepbaar = ((Boolean) launchState.get("sleepbaar")).booleanValue();
		if (launchState != null && launchState.get("sleepdoel") != null)
			sleepdoel = ((Boolean) launchState.get("sleepdoel")).booleanValue();
		if (launchState != null && launchState.get("sleepHandle") != null)
			sleepHandle = ((Boolean) launchState.get("sleepHandle")).booleanValue();
		if (launchState != null && launchState.get("checkExpressieString") != null)
			checkExpressieString = (String) launchState.get("checkExpressieString");
		if (launchState != null && launchState.get("defaultBijNull") != null)
			defaultBijNull = ((Boolean) launchState.get("defaultBijNull")).booleanValue();
		if (launchState != null && launchState.get("ipId") != null)
			ipId = ((Number) launchState.get("ipId")).intValue();
		if (launchState != null && launchState.get("colorSelection") != null)
			colorSelection = ((Boolean) launchState.get("colorSelection")).booleanValue();
		if (launchState != null && launchState.get("tableBorders") != null)
			tableBorders = ((Boolean) launchState.get("tableBorders")).booleanValue();
		if (launchState != null && launchState.get("centerV") != null)
			centerV = ((Boolean) launchState.get("centerV")).booleanValue();
		if (launchState != null && launchState.get("centerH") != null)
			centerH = ((Boolean) launchState.get("centerH")).booleanValue();
		if (launchState != null && launchState.get("zwevend") != null)
			zwevend = ((Boolean) launchState.get("zwevend")).booleanValue();
		if (launchState != null && launchState.get("locationX") != null)
			locationX = ((Number) launchState.get("locationX")).intValue();
		if (launchState != null && launchState.get("locationY") != null)
			locationY = ((Number) launchState.get("locationY")).intValue();
		
		bgColor = getColor(launchState, "bgColor", bgColor_red, bgColor_green, bgColor_blue);
		fgColor = getColor(launchState, "fgColor",fgColor_red, fgColor_green, fgColor_blue);
		randColor = getColor(launchState, "randColor",randColor_red, randColor_green, randColor_blue);
		randDikte = randZichtbaar ? randDikte : 0; 

		mainPanel2 = new LayoutPanel();
		mainPanel2.setSize(breedte + "px", hoogte + "px");
		MouseHandler mouseHandler = new MouseHandler();
		mainPanel2.addDomHandler(mouseHandler, MouseDownEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseMoveEvent.getType());
		mainPanel2.addDomHandler(mouseHandler, MouseUpEvent.getType());
		
		randPanel = new FlowPanel();
		if(bgColorZichtbaar)
			randPanel.getElement().getStyle().setBackgroundColor(bgColor.toString());
		randPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
		randPanel.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
		
		//tabelranden ook regelen in het randPanel? 
		//Waar komen de randen te staan? Aan de linkerkanten van de nieuwe kolommen? Nee, midden tussen (maar afgerond naar rechts/beneden)
		
		mainPanel = new Grid(hoogtes.size(), breedtes.size());
		
		mainPanel.getElement().getStyle().setProperty("borderSpacing", "" + cellSpaceColumn + "px " + cellSpaceRow + "px");
		
		if (breedtes.size() > 1)
			mainPanel.getElement().getStyle().setProperty("margin", "" + (-cellSpaceRow) + "px " + (-cellSpaceColumn) + "px");
		
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		mainPanel.getElement().getStyle().setBorderWidth(0, Unit.PX);
		
		//tabelranden hier regelen?
		
		tekstHulsVakken = new LayoutPanel[hoogtes.size()][breedtes.size()];
		tekstVakken = new FlowPanel[hoogtes.size()][breedtes.size()];
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{	double tekstVakBreedte = (Double) breedtes.get(j) - 2 * cellMarge;
				double tekstVakHoogte = (Double) hoogtes.get(i) - 2 * bovenMarge;
				
				tekstHulsVakken[i][j] = new LayoutPanel();
				tekstHulsVakken[i][j].setSize(tekstVakBreedte + "px", tekstVakHoogte + "px");
				tekstVakken[i][j] = new FlowPanel();
				//if (bgColorZichtbaar)
				//	tekstVakken[i][j].getElement().getStyle().setBackgroundColor(bgColor.toString());
				tekstVakken[i][j].getElement().getStyle().setColor(fgColor.toString());
				tekstVakken[i][j].getElement().getStyle().setFontSize(font_size, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setProperty("lineHeight", "1.2");
				tekstVakken[i][j].getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
				tekstVakken[i][j].getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
				
				tekstVakken[i][j].getElement().getStyle().setProperty("margin", "" + cellMarge + "px " + bovenMarge + "px");
				tekstVakken[i][j].getElement().getStyle().setWidth(tekstVakBreedte, Unit.PX); //nog nodig?
				tekstVakken[i][j].getElement().getStyle().setHeight(tekstVakHoogte, Unit.PX); //nog nodig?
				
				/*
				tekstVakken[i][j].getElement().getStyle().setPaddingTop(bovenMarge - randDikte, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setPaddingBottom(bovenMarge - randDikte, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setPaddingLeft(cellMarge - randDikte, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setPaddingRight(cellMarge - randDikte, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setWidth((Double) breedtes.get(j) - 2 * cellMarge, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setHeight((Double) hoogtes.get(i) - 2 * bovenMarge, Unit.PX);
				*/
				
				//tekstVakken[i][j].getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
				//tekstVakken[i][j].getElement().getStyle().setBorderColor(randColor.toString());
				//tekstVakken[i][j].getElement().getStyle().setBorderWidth(tableBorders ? 1 : 0, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
				if(centerH)
					tekstVakken[i][j].getElement().getStyle().setTextAlign(TextAlign.CENTER);
				//if(centerV)
					//tekstVakken[i][j].getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				tekstHulsVakken[i][j].add(tekstVakken[i][j]);
				mainPanel.setWidget(i, j, tekstHulsVakken[i][j]);
			}
		}
		mainPanel2.add(randPanel);
		mainPanel2.setWidgetLeftWidth(randPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		mainPanel2.setWidgetTopHeight(randPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftWidth(mainPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		mainPanel2.setWidgetTopHeight(mainPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		if(sleepbaar && sleepHandle)
		{	Image ic = new Image("images/resources/crosshair.gif");
			mainPanel2.add(ic);
			mainPanel2.setWidgetLeftWidth(ic, 0, Style.Unit.PX, 20, Style.Unit.PX);
			mainPanel2.setWidgetTopHeight(ic, 0, Style.Unit.PX, 20, Style.Unit.PX);
		}
		
		
		//mainPanel.setSize((breedte - 2 * randDikte) + "px", (hoogte - 2 * randDikte) + "px");
		
	}

	public void setTableBounds()
	{
		int b = breedte;
		int h = hoogte;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				tekstVakken[i][j].setSize("" + (int) Math.round((Double) breedtes.get(j)) + "px", "" + (int) Math.round((Double) hoogtes.get(i)) + "px");
				tekstHulsVakken[i][j].setSize("" + (int) Math.round((Double) breedtes.get(j)) + "px", "" + (int) Math.round((Double) hoogtes.get(i)) + "px");
			}
		}
	}

	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
		if(instellingen.get("fontSize") != null)
			font_size = ((Number) instellingen.get("fontSize")).intValue();
	}

	public void setKeyboard(FormuleKeyboard kb)
	{
		this.kb = kb;
	}

	public void zetOpdracht(HashMap<String, Object> interactiePanelLaunchState)
	{
		String randVarString = "";
		ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = Memento.toArrayList( interactiePanelLaunchState.get("interactiePanelLaunchData") );

		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden);
		int aantalVakken = 0;
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				opdrachtObjects = tb.convertTekst(interactiePanelLaunchState, i, j);

				for (int k = 0; k < opdrachtObjects.size(); k++)
				{
					Object currentObject = opdrachtObjects.get(k);
					if (currentObject instanceof InteractionView)
					{ 	
						((InteractionView) currentObject).setCommunicationRoot(comRoot);
						interactionViewObjects.add(currentObject);
					}

					if (currentObject instanceof TekstVakPanel)
					{
						Object launchData = opdrachtGegevens.get(aantalVakken);
						aantalVakken++;
						HashMap<String, Object> launchState = (HashMap<String, Object>) ((HashMap<String, Object>) launchData).get("interactiePanelLaunchState");
						((TekstVakPanel) currentObject).zetInstellingen(instellingen);
						((TekstVakPanel) currentObject).setKeyboard(kb);
						((TekstVakPanel) currentObject).zetOpdracht(launchState);
					}
					else if (currentObject instanceof FormuleEditorWithAnswer)
					{
						aantalVakken++;
						((FormuleEditorWithAnswer) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject instanceof FormuleEditorWithSteps)
					{
						aantalVakken++;
						((FormuleEditorWithSteps) currentObject).zetInstellingen(instellingen);
					}
					else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
					{
						aantalVakken++;
					}
					else if (currentObject.getClass().getName().equals("fi.kladjegwt.client.KladjeGWT"))
					{
						aantalVakken++;
					}
					else if (currentObject instanceof StubView)
					{
						aantalVakken++;
					}
				}
				//setObjects(opdrachtObjects, tekstVakken[i][j]);
				setObjects(opdrachtObjects, i, j);
			}
		}

	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
	}

	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		ArrayList<Object> states = new ArrayList<Object>();
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			states.add(((InteractionView) currentObject).getState());
		}
		h.put("interactiePanelStates", states);
		h.put("selected", new Boolean(selected));
		if(zwevend)
		{	h.put("locationX", new Integer(locationX));
			h.put("locationY", new Integer(locationY));
		}
		return h;
	}

	public void setState(HashMap<String, Object> h)
	{
		List<Object> states = Memento.toArrayList(h.get("interactiePanelStates"));
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			HashMap<String, Object> state = (HashMap<String, Object>) states.get(i);
			((InteractionView) currentObject).setState(state);
		}
		if(h.containsKey("selected"))
			selected = ((Boolean) h.get("selected")).booleanValue();
		if(h.containsKey("locationX"))
			locationX = ((Integer) h.get("locationX")).intValue();
		if(h.containsKey("locationY"))
			locationY = ((Integer) h.get("locationY")).intValue();
		
		if(parent != null)
		{	parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		setSelected(selected);
	}

	public int getScore()
	{
		int score = 0;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			score += ((InteractionView) currentObject).getScore();
		}
		return score;
	}

	public boolean isCorrect()
	{
		boolean correct = true;
		for (int i = 0; i < interactionViewObjects.size(); i++)
		{
			Object currentObject = interactionViewObjects.get(i);
			correct = correct && ((InteractionView) currentObject).isCorrect();
		}
		return correct;
	}

	//public void setObjects(ArrayList<Object> opdrachtObjects, Panel destination)
	public void setObjects(ArrayList<Object> opdrachtObjects, int rij, int kolom)
	{	Panel destination = tekstVakken[rij][kolom];
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{

			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof String)
			{
				Element element = DOM.createSpan();
				element.setInnerHTML((String) currentObject);
				destination.getElement().appendChild(element);

				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String)
					destination.getElement().appendChild(DOM.createElement("br"));
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				int asHoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleEditorWithAnswer) currentObject).getMainRegel().getHeight();

				TouchPanel tp = (TouchPanel) ((FormuleEditorWithAnswer) currentObject).getAsPanel();
				tp.getElement().getStyle().setProperty("display", "inline-block");
				kb.setEditor(((FormuleEditorWithAnswer) currentObject));
				addFormulePanelListeners(tp, ((FormuleEditorWithAnswer) currentObject));

				tp.getElement().getStyle().setProperty("display", "inline-block");
				tp.getElement().getStyle().setProperty("verticalAlign", "" + (-hoogte + asHoogte + Math.rint(font_size * 0.33)) + "px");
				kb.setEditor((FormuleEditorWithAnswer) currentObject);
				destination.add(tp);
			}
			else if (currentObject instanceof FormuleViewer)
			{
				((FormuleViewer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				int asHoogte = ((FormuleViewer) currentObject).getMainRegel().getAsHoogte();
				int hoogte = ((FormuleViewer) currentObject).getMainRegel().getHeight();
				Panel a = ((FormuleViewer) currentObject).getAsPanel();
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "" + (-hoogte + asHoogte + Math.rint(font_size * 0.33)) + "px");
				destination.add(a);
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Panel a = ((FormuleEditorWithSteps) currentObject).getAsPanel();
				((FormuleEditorWithSteps) currentObject).getEditor().requestFocus();

				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "top");
				destination.add(a);
			}
			else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
			{
				Panel a = (Panel) (((InteractionView) currentObject).asWidget());
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				destination.add(a);
			}
			else if (currentObject instanceof InteractionView)
			{		
				Widget a = (((InteractionView) currentObject).asWidget());
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				//destination.add(a);
				if(currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).isZwevend())
				{	tekstHulsVakken[rij][kolom].add(a);
					tekstHulsVakken[rij][kolom].setWidgetLeftWidth(a, ((TekstVakPanel)currentObject).getLocationX(), Style.Unit.PX, 
							((TekstVakPanel)currentObject).getBreedte(), Style.Unit.PX);
					tekstHulsVakken[rij][kolom].setWidgetTopHeight(a, ((TekstVakPanel)currentObject).getLocationY(), Style.Unit.PX, 
							((TekstVakPanel)currentObject).getHoogte(), Style.Unit.PX);
					((TekstVakPanel) currentObject).setParent(tekstHulsVakken[rij][kolom]);
				}
				else
					destination.add(a);
				
			}
			//Sietske: volgens mij is dit overbodig, want een TekstVakPanel implements InteractionView
			//en die staat hierboven.
			else if (currentObject instanceof TekstVakPanel)  
			{	Panel a = ((TekstVakPanel) currentObject).getAsPanel();
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				destination.add(a);
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				Widget w = iv.getImage();
				destination.add(w);
			}
		}
	}
	
	public void setParent(LayoutPanel panel)
	{
		parent = panel;
	}

	public Panel getPanelElement(final FormuleHolder editor)
	{
		FlowPanel fp = new FlowPanel();
		editor.paint();

		final Panel p = editor.getAsPanel();
		if (p instanceof TouchPanel)
		{
			TouchPanel tp = (TouchPanel) p;
		}

		fp.add(p);
		return p;
	}

	public Panel getAsPanel()
	{
		return mainPanel2;
	}

	private void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor)
	{
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}

	private PopupFacade facade;
	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	public boolean isZwevend()
	{
		return zwevend;
	}
	
	public Point geefLocatie()
	{
		return new Point(locationX, locationY);
	}
	
	
	
	public void zetLocatie(double x, double y) //moeten dit doubles worden?
	{
		locationX = (int) x;
		locationY = (int) y;
		
	}
	
	public void setStartSleep(int x, int y)
	{
		startSleepX = x;
		startSleepY = y;
	}

	public void setStartSleep()
	{
		startSleepX = locationX;
		startSleepY = locationY;
	}

	public Point getStartSleep()
	{
		return new Point(startSleepX, startSleepY);
	}

	public void zetSleepDoelPosities(Point[] doelPosities)
	{
		this.doelPosities = doelPosities;
	}

	public void zetSleepObjecten(TekstVakPanel[] sleepObjecten)
	{
		this.sleepObjecten = sleepObjecten;
	}

	public Point[] geefSleepDoelPosities()
	{
		return doelPosities;
	}

	public void zetSleepSnap(boolean sleepSnap)
	{
		this.sleepSnap = sleepSnap;
	}

	public boolean geefSleepSnap()
	{
		return sleepSnap;
	}

	public void zetSleepdoelMarge(int sleepdoelMarge)
	{
		this.sleepdoelMarge = sleepdoelMarge;
	}

	public int geefSleepdoelMarge()
	{
		return sleepdoelMarge;
	}
	
	public void setRelocate(boolean relocate)
	{
		this.relocate = relocate;
	}
	
	public void setSelected(boolean b)
	{ 
		selected = b;
		if (selected)
		{
			if (colorSelection)
			{	
				randPanel.getElement().getStyle().setBorderColor(selectionColor.toString());
				randPanel.getElement().getStyle().setOpacity(0.4); //checken of dit goed gaat zo..
				randPanel.getElement().getStyle().setBorderWidth(400, Unit.PX);
				//setBorder(selectionColor, 400);
			}
			else
			{	randPanel.getElement().getStyle().setBorderColor(grijs.toString());
				randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
			}
		
			//mainPanel.getElement().getStyle().setBorderColor("gray");
			//mainPanel.getElement().getStyle().setBorderWidth(0, Unit.PX);

			//tekstVakken[i][j].getElement().getStyle().setBackgroundColor(bgColor.toString());
		
		}
		else
		{
			randPanel.getElement().getStyle().setBorderColor(randColor.toString());
			randPanel.getElement().getStyle().setOpacity(1);
			randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
			//setBorder(randColor, randZichtbaar ? randDikte : 0);

		}
		
		
	}
	
	
	public Vector geefInteractiePanels()
	{
		Vector v = new Vector();
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				for(int k = 0; k < tekstVakken[i][j].getWidgetCount(); k++)
					if(tekstVakken[i][j].getWidget(k) != null)
						v.add(tekstVakken[i][j].getWidget(k));
				for(int k = 0; k < tekstHulsVakken[i][j].getWidgetCount(); k++)
					if(tekstHulsVakken[i][j].getWidget(k) != null)
						v.add(tekstHulsVakken[i][j].getWidget(k));
				
				//hier moet iets met de tekstVakHulzen... Dit wordt nog wel een beetje ingewikkeld.
				
				
				//tekstVakken[i][j].geefInteractiePanels(v); //hier wil ik eigenlijk die opdrachtObjects gebruiken die we al eerder zagen..
			}
		}
		return v;
	}
	
	public void zetGoedFout(boolean b)
	{
		//zetTransparant(false);
		if (b)
		{
			randPanel.getElement().getStyle().setBorderColor(CssColor.make(50, 225, 50).toString());
			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
		}
		else
		{	randPanel.getElement().getStyle().setBorderColor(CssColor.make(225, 50, 50).toString());
			randPanel.getElement().getStyle().setBorderWidth(5, Unit.PX);
		}
		
	}

	public void wisGoedFout()
	{
		//zetTransparant(!bgColorZichtbaar);
		/*
		if (randZichtbaar)
			setBorder(Color.gray);
		repaint();
		*/
		randPanel.getElement().getStyle().setBorderColor(randColor.toString());
		randPanel.getElement().getStyle().setOpacity(1);
		randPanel.getElement().getStyle().setBorderWidth(randDikte, Unit.PX);
	}

	public void zetGoedFoutSleep(boolean b)
	{
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					sleepObjecten[i].zetGoedFout(b);
					break;
				}
			}
		}
	}
	
	public int getIpId()
	{
		return ipId;
	}
	
	public int getLocationX()
	{
		return locationX;
	}
	
	public int getLocationY()
	{
		return locationY;
	}
	
	public int getHoogte()
	{
		return hoogte;
	}
	
	public int getBreedte()
	{
		return breedte;
	}
	
	//misschien ook setters bij bovenstaande nodig.
	
	public String getIpExpString()
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleRegel fr = ((FormuleEditorWithAnswer) object).getCurrentRegel();
				if (fr != null)
				{
					String string = fr.toString();
					return "$f" + string + "@";
				}
				
			}
		}
			//Als er een formulevak inzit: expressie uit formulevak geven
			//Als er een vergelijkingsvak inzit: 
			/*
			String string = null;
			FormuleVak fv = ((SimpelAntwoordVergelijkingVak) ip).geefFormuleVak();
			if (fv != null)
			{
				string = fv.toString();
				VergelijkingMeerv vgm = FormuleParser.parseVergelijking(string);
				Vergelijking vg = null;
				if (vgm != null)
					vg = vgm.geefVergelijking(0);
				Expressie e = null;
				if (vg != null)
					e = new Aftrekking(vg.geefExpLinks(), vg.geefExpRechts());
				if (e != null)
					return "$f" + e.toString() + "@";
			}
			*/
			
			
			/* Uit oude TekstVakPanel: veel interfaces en types die niet meer worden gebruikt
			try{
				
			}
			InteractiePanelContainerIF ipc = (InteractiePanelContainerIF) v.elementAt(0);
			if (ipc instanceof TekstVakPanel)
			{
				InteractiePanel ip = ((TekstInteractiePanelVak) ipc).getInteractiePanel();
				if (ip instanceof SimpelAntwoordFormuleVak)
				{
					FormuleVak fv = ((SimpelAntwoordFormuleVak) ip).geefFormuleVak();
					if (fv != null)
						return fv.toString();
				}
				if (ip instanceof SimpelAntwoordVergelijkingVak)
				{
					String string = null;
					FormuleVak fv = ((SimpelAntwoordVergelijkingVak) ip).geefFormuleVak();
					if (fv != null)
					{
						string = fv.toString();
						VergelijkingMeerv vgm = FormuleParser.parseVergelijking(string);
						Vergelijking vg = null;
						if (vgm != null)
							vg = vgm.geefVergelijking(0);
						Expressie e = null;
						if (vg != null)
							e = new Aftrekking(vg.geefExpLinks(), vg.geefExpRechts());
						if (e != null)
							return "$f" + e.toString() + "@";
					}
				}
			} */
		
		return checkExpressieString;
	}

	public boolean isIpSelected()
	{
		return selected;
	}
	
	public boolean bevatSleepObject()
	{
		boolean bevat = false;
		if (sleepObjecten != null && sleepdoel)
		{	for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				//if(dx*dx+dy*dy < Math.min(1, marge*marge));
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					bevat = true;
					break;
				}
			}
		}
		return bevat;
	}

	public Expressie geefSleepObjectWaarde()
	{
		Expressie waarde = null;
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int dx = (int) Math.abs(geefLocatie().getX() - sleepObjecten[i].geefLocatie().getX());
				int dy = (int) Math.abs(geefLocatie().getY() - sleepObjecten[i].geefLocatie().getY());
				int marge = sleepObjecten[i].geefSleepdoelMarge();
				//if(dx*dx+dy*dy < Math.min(1, marge*marge))
				if (dx < Math.min(1, marge) && dy < Math.min(1, marge))
				{
					waarde = FormuleParser.geefExpressie(sleepObjecten[i].getIpExpString());
					break;
				}
			}
		}
		return waarde;
	}

	public Expressie geefSleepObjectVerzamelWaarde()
	{
		Expressie waarde = new BasisExpressie(0);
		if (sleepObjecten != null && sleepdoel)
		{
			for (int i = 0; i < sleepObjecten.length; i++)
			{
				int x = (int) (sleepObjecten[i].geefLocatie().getX() - geefLocatie().getX());
				int y = (int) (sleepObjecten[i].geefLocatie().getY() - geefLocatie().getY());
				int b = sleepObjecten[i].breedte;
				int h = sleepObjecten[i].hoogte;
				boolean binnen = x >= 0 && y >= 0 && x + b <= breedte && y + h <= hoogte;
						//this.contains(x, y) && this.contains(x + b, y + h);
				if (binnen)
				{
					waarde = new Optelling(waarde, FormuleParser.geefExpressie(sleepObjecten[i].getIpExpString()));
				}
			}
		}
		return waarde;
	}

	public boolean ipObjectIsCorrect()
	{
		/*
		boolean juist = false;
		Vector v = geefInteractiePanels();
		if (v.size() > 0)
		{
			InteractiePanelContainerIF ipc = (InteractiePanelContainerIF) v.elementAt(0);
			if (ipc instanceof TekstInteractiePanelVak)
			{
				InteractiePanel ip = ((TekstInteractiePanelVak) ipc).getInteractiePanel();
				if (ip instanceof SimpelAntwoordFormuleVak)
				{
					ip.kijkNa();
					juist = ((SimpelAntwoordFormuleVak) ip).isCorrectStrikt();
				}
			}
		}
		return juist;
		*/
		
		boolean juist = false;
		
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	FormuleEditorWithAnswer object2 = (FormuleEditorWithAnswer) object;
				object2.check();//nodig? Ja, maar ik wil eigenlijk het kruisje/krulletje niet weergeven in het vakje. 
				juist = object2.isCorrect();
				
				/*
				FormuleRegel fr = ((FormuleEditorWithAnswer) object).getCurrentRegel();
				if (fr != null)
				{
					String string = fr.toString();
					return "$f" + string + "@";
				}
				*/
			}
		}
		
		
		/*
		
		Vector v = geefInteractiePanels();
		System.out.println("size van v: " + v.size());
		if (v.size() > 0)
		{
			if(v.elementAt(0) instanceof FormuleEditor)
			{	System.out.println("instance of FormuleEditor");
				FormuleEditor ip = (FormuleEditor) v.elementAt(0);
				if (ip instanceof FormuleEditorWithAnswer)
				{	FormuleEditorWithAnswer ip2 = (FormuleEditorWithAnswer) ip;
					ip2.check();
					juist = ((FormuleEditorWithAnswer) ip2).isCorrect(); //was: isCorrectStrikt; moet ik dat nog maken?
				}
			}
			
			/*
				InteractionView ip = (InteractionView) v.elementAt(0);
			InteractiePanelContainerIF ipc = (InteractiePanelContainerIF) v.elementAt(0);
			if (ipc instanceof TekstInteractiePanelVak)
			{
				InteractiePanel ip = ((TekstInteractiePanelVak) ipc).getInteractiePanel();
				if (ip instanceof FormuleEditorWithAnswer)
				{
					ip.kijkNa();
					juist = ((FormuleEditorWithAnswer) ip).isCorrect(); //was: isCorrectStrikt; moet ik dat nog maken?
				}
			}
			
		}
	*/
		return juist;
	}

	
	public Expressie geefObjectWaarde()
	{
		Expressie waarde = FormuleParser.geefExpressie(getIpExpString());
		if("$f@".equals(getIpExpString()) && defaultBijNull)
			waarde = FormuleParser.geefExpressie(checkExpressieString);
		return waarde;
	}
	
	public TekstVakPanel zoekTekstVakPanel(int id)
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{
			if(interactionViewObjects.get(i) instanceof TekstVakPanel)
			{	TekstVakPanel panel = (TekstVakPanel) interactionViewObjects.get(i);
				if(id == panel.ipId)
					return panel;
			}
		}
		return null;
	}
	
	public boolean objectNullWaarde()
	{
		return "".equals(getIpExpString()) || "$f@".equals(getIpExpString());
	}

	/*
	public void onClick(ClickEvent event) {
		if(!selectable)
			return;
		
		selected = !selected;
		setSelected(selected);
			
	}
	*/
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if(selectable)
		{
			selected = !selected;
			setSelected(selected);
			return;
		}
		
		if(!sleepbaar)
		{
			//hier zorgen dat de pagina wordt versleept?
			return;
		}
		
		startX = eventX - locationX;
		startY = eventY - locationY;
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if(!sleepbaar)
		{	//hier zorgen dat de pagina wordt versleept?
			return;
		}
		
		locationX = eventX - startX;
		locationY = eventY - startY;
		
		if(parent != null)
		{	locationX = Math.max(locationX, 0);
			locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
			locationY = Math.max(locationY, 0);
			locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);
	
			parent.remove(this.asWidget());
			parent.add(this.asWidget());
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		/* Nog zorgen voor het volgende:
		 * Ofwel zorgen dat alles snel genoeg is, zodat de muis niet uit het paneltje kan verdwijnen;
		 * ofwel zorgen dat het paneltje toch nog beweegt als de muis eruit is bewogen (met mouseDown nog aan).
		 * Dus misschien eerst checken of er ergens nog een mouseDown is, en dan pas stopPropagation; als 
		 * ergens mouseDown is, de mouseMoved doorgeven aan dat panel. Maar ik weet niet of en hoe dat kan.
		 * 
		 * Verder: zorgen dat het panel blijft bewegen als hij achter een ander panel langs beweegt.
		 * 
		 * En: zorgen dat hij niet het veld uit kan. Nu stopt hij nog iets te ver van de rand.. (heeft dat met randdikte te maken?)
		 * 
		 * In de gewone wiskOpdr komt het panel dat op dat moment gesleept wordt altijd boven de andere panels te liggen.
		 * Dat is wel mooi; je hebt hem immers als het ware opgetild en legt hem ergens anders weer neer. Dan is het tweede
		 * probleem hierboven ook opgelost. Misschien kun je ze gewoon uit hun parent halen en weer toevoegen. 
		 */
		
	}
	
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	if(!sleepbaar)
		{	//hier zorgen dat de pagina wordt versleept?
			return;
		}
		locationX = eventX - startX;
		locationY = eventY - startY;
		if(parent != null)
		{	locationX = Math.max(locationX, 0);
			locationX = Math.min(locationX, parent.getOffsetWidth() - breedte);
			locationY = Math.max(locationY, 0);
			locationY = Math.min(locationY, parent.getOffsetHeight() - hoogte);

			if(sleepSnap) 
			{
				//Point[] doelPosities = ((TekstVakPanel)interactiePanel).geefSleepDoelPosities();
				//int marge = ((TekstVakPanel)interactiePanel).geefSleepdoelMarge();
				//boolean snap = ((TekstVakPanel)interactiePanel).geefSleepSnap();
				if(doelPosities != null) 
				{	boolean snapped = false;
					for(int i=0 ; i<doelPosities.length ; i++)
					{	int dx = (int) Math.abs(locationX - doelPosities[i].getX());
						int dy = (int) Math.abs(locationY - doelPosities[i].getY());
						//if(snap && dx*dx+dy*dy < marge*marge)
						if(sleepSnap && dx < sleepdoelMarge && dy < sleepdoelMarge) 
						{	locationX = (int) doelPosities[i].getX();
							locationY = (int) doelPosities[i].getY();
							snapped = true;
							break;
						}
					}
					if(!snapped && relocate)
					{	locationX = startSleepX;
						locationY = startSleepY;
					}
				}
			}
			parent.remove(this.asWidget());
			parent.add(this.asWidget());
			parent.setWidgetLeftWidth(this.asWidget(), locationX, Style.Unit.PX, breedte, Style.Unit.PX);
			parent.setWidgetTopHeight(this.asWidget(), locationY, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{   
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
				return;
			e.stopPropagation();
			
			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			//int eventX = e.getX();
			//int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
			{	mouseDown = false;
				return;
			}
			
			e.stopPropagation(); 
			
			if (!mouseDown)
				return;

			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			//int eventX = e.getX();
			//int eventY = e.getY();

			mouseMoveTouchMoveAction(eventX, eventY);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{	//e.preventDefault();
			
			// prevent scrolling
			if(sleepbaar && sleepHandle && (e.getX() > 20 || e.getY() > 20) )
			{	mouseDown = false;
				return;
			}
			
			e.stopPropagation();
			
			mouseDown = false;

			int eventX = e.getClientX();
			int eventY = e.getClientY();
			
			//int eventX = e.getX();
			//int eventY = e.getY();
			
			mouseUpTouchEndAction(eventX,eventY);

		}

	} //MouseHandler
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - getAsPanel().getAbsoluteLeft();
				int eventY = touch.getPageY() - getAsPanel().getAbsoluteTop();				
				
				//lastStartX = eventX; 
				//lastStartY = eventY;
				//lastMoveX = -1000;
				//lastMoveY = -1000;
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
			    int eventX = touch.getPageX() - getAsPanel().getAbsoluteLeft();
				int eventY = touch.getPageY() - getAsPanel().getAbsoluteTop();				
			    
				//lastMoveX = eventX; 
				//lastMoveY = eventY;
				
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			
			int eventX = 0;
			int eventY = 0;
		
			/*
			if (lastMoveX <= -999)
			{
				eventX = lastStartX;
				eventY = lastStartY;
			}
			else
			{
				eventX = lastMoveX;
				eventY = lastMoveY;
			}
			*/
			    
			mouseUpTouchEndAction(eventX,eventY);
				
		    
		}

	}
}
