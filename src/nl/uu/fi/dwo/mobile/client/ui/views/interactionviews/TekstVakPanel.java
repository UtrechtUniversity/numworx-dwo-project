package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
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
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Aftrekking;
import fi.wiskopdr.expressies.Expressie;
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
	private FlowPanel[][] tekstVakken = null;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	ArrayList<Object> interactionViewObjects = new ArrayList<Object>();

	ArrayList<Object> breedtes = null;
	ArrayList<Object> hoogtes = null;
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
	
	private boolean selectable;
	private boolean selected;
	private String checkExpressieString = "$f1@";
	private boolean defaultBijNull;
	private int ipId = 0;
	private boolean colorSelection;
	

	public TekstVakPanel(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		facade = new PopupFacade(h);
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
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
			breedtes = (ArrayList<Object>) launchState.get("breedtes");
		else
			breedtes = new ArrayList<Object>(Arrays.asList(600.0));
		if (launchState != null && launchState.get("hoogtes") != null)
			hoogtes = (ArrayList<Object>) launchState.get("hoogtes");
		else
			hoogtes = new ArrayList<Object>(Arrays.asList(250.0));
		
		if (launchState != null && launchState.get("cellSpaceColumn") != null)
			cellSpaceColumn = (Integer) launchState.get("cellSpaceColumn");
		if (launchState != null && launchState.get("cellSpaceRow") != null)
			cellSpaceRow = (Integer) launchState.get("cellSpaceRow");
		if (launchState != null && launchState.get("cellMarge") != null)
			cellMarge = (Integer) launchState.get("cellMarge");
		if (launchState != null && launchState.get("bovenMarge") != null)
			bovenMarge = (Integer) launchState.get("bovenMarge");
		if (launchState != null && launchState.get("ronding") != null)
			ronding = (Integer) launchState.get("ronding");
		if (launchState != null && launchState.get("bgColorZichtbaar") != null)
			bgColorZichtbaar = (Boolean) launchState.get("bgColorZichtbaar");
		if (launchState != null && launchState.get("bgColor_red") != null)
			bgColor_red = (Integer) launchState.get("bgColor_red");
		if (launchState != null && launchState.get("bgColor_green") != null)
			bgColor_green = (Integer) launchState.get("bgColor_green");
		if (launchState != null && launchState.get("bgColor_blue") != null)
			bgColor_blue = (Integer) launchState.get("bgColor_blue");
		if (launchState != null && launchState.get("fgColor_red") != null)
			fgColor_red = (Integer) launchState.get("fgColor_red");
		if (launchState != null && launchState.get("fgColor_green") != null)
			fgColor_green = (Integer) launchState.get("fgColor_green");
		if (launchState != null && launchState.get("fgColor_blue") != null)
			fgColor_blue = (Integer) launchState.get("fgColor_blue");
		if (launchState != null && launchState.get("randZichtbaar") != null)
			randZichtbaar = (Boolean) launchState.get("randZichtbaar");
		if (launchState != null && launchState.get("randColor_red") != null)
			randColor_red = (Integer) launchState.get("randColor_red");
		if (launchState != null && launchState.get("randColor_green") != null)
			randColor_green = (Integer) launchState.get("randColor_green");
		if (launchState != null && launchState.get("randColor_blue") != null)
			randColor_blue = (Integer) launchState.get("randColor_blue");
		if (launchState != null && launchState.get("randDikte") != null)
			randDikte = (Integer) launchState.get("randDikte");
		if (launchState != null && launchState.get("font_size") != null)
			font_size = (Integer) launchState.get("font_size");
		if (launchState != null && launchState.get("font_style") != null)
			font_style = (Integer) launchState.get("font_style");
		if (launchState != null && launchState.get("selectable") != null)
			selectable = ((Boolean) launchState.get("selectable")).booleanValue(); //misschien niet nodig.
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
		
		bgColor = CssColor.make(bgColor_red, bgColor_green, bgColor_blue);
		fgColor = CssColor.make(fgColor_red, fgColor_green, fgColor_blue);
		randColor = CssColor.make(randColor_red, randColor_green, randColor_blue);
		randDikte = randZichtbaar ? randDikte : 0; 

		mainPanel2 = new LayoutPanel();
		mainPanel2.setSize(breedte + "px", hoogte + "px");
		mainPanel2.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e){
				if(!selectable)
					return;
				
				selected = !selected;
				setSelected(selected);
			}
		}, ClickEvent.getType());
		
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
		
		
		tekstVakken = new FlowPanel[hoogtes.size()][breedtes.size()];
		for (int i = 0; i < hoogtes.size(); i++)
		{
			for (int j = 0; j < breedtes.size(); j++)
			{
				tekstVakken[i][j] = new FlowPanel();
				//if (bgColorZichtbaar)
				//	tekstVakken[i][j].getElement().getStyle().setBackgroundColor(bgColor.toString());
				tekstVakken[i][j].getElement().getStyle().setColor(fgColor.toString());
				tekstVakken[i][j].getElement().getStyle().setFontSize(font_size, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setProperty("lineHeight", "1.2");
				tekstVakken[i][j].getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
				tekstVakken[i][j].getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
				
				tekstVakken[i][j].getElement().getStyle().setProperty("margin", "" + cellMarge + "px " + bovenMarge + "px");
				
				
				tekstVakken[i][j].getElement().getStyle().setWidth((Double) breedtes.get(j) - 2 * cellMarge, Unit.PX);
				tekstVakken[i][j].getElement().getStyle().setHeight((Double) hoogtes.get(i) - 2 * bovenMarge, Unit.PX);
				
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

				if(centerV)
					tekstVakken[i][j].getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				//bovenstaande doet niet wat ik wil.
				//if(centerH)
				//	tekstVakken[i][j].getElement().getStyle()..setHorizontalAlign();
				
				mainPanel.setWidget(i, j, tekstVakken[i][j]);
			}
		}
		mainPanel2.add(randPanel);
		mainPanel2.setWidgetLeftWidth(randPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		mainPanel2.setWidgetTopHeight(randPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		
		mainPanel2.add(mainPanel);
		mainPanel2.setWidgetLeftWidth(mainPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		mainPanel2.setWidgetTopHeight(mainPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		
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
			}
		}
	}

	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
		if(instellingen.get("fontSize") != null)
			font_size = (Integer) instellingen.get("fontSize");
	}

	public void setKeyboard(FormuleKeyboard kb)
	{
		this.kb = kb;
	}

	public void zetOpdracht(HashMap<String, Object> interactiePanelLaunchState)
	{
		String randVarString = "";
		ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
		ArrayList<Object> opdrachtGegevens = (ArrayList<Object>) interactiePanelLaunchState.get("interactiePanelLaunchData");

		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden, this);
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
				setObjects(opdrachtObjects, tekstVakken[i][j]);
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

	public void setObjects(ArrayList<Object> opdrachtObjects, Panel destination)
	{
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
				destination.add(a);
			}
			else if (currentObject instanceof TekstVakPanel)
			{
				Panel a = ((TekstVakPanel) currentObject).getAsPanel();
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
		if(instellingen.get("zwevend") != null)
			return ((Boolean) instellingen.get("zwevend")).booleanValue();
		else
			return false;
	}
	
	public Point geefLocatie()
	{
		double locationX = 0;
		double locationY = 0;
		if(instellingen.get("locationX") != null)
			locationX = ((Number)instellingen.get("locationX")).doubleValue();
		if(instellingen.get("locationY") != null)
			locationY = ((Number)instellingen.get("locationY")).doubleValue();
		return new Point(locationX, locationY);
	}
	
	public void zetLocatie(double x, double y) //moeten dit doubles worden?
	{
		instellingen.put("locationX", new Double(x));
		instellingen.put("locationY", new Double(y));
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
				
				//tekstVakken[i][j].geefInteractiePanels(v); //hier wil ik eigenlijk die opdrachtObjects gebruiken die we al eerder zagen..
			}
		}
		return v;
	}
	
	public int getIpId()
	{
		return ipId;
	}
	
	public String getIpExpString()
	{
		for(int i = 0; i < interactionViewObjects.size(); i++)
		{	Object object = interactionViewObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{	// waarde uitzoeken en returnen
				
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

	/*
	public void onClick(ClickEvent event) {
		if(!selectable)
			return;
		
		selected = !selected;
		setSelected(selected);
			
	}
	*/
}
