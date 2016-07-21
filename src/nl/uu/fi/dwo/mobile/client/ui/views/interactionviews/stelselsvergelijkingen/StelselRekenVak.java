package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.TreeMap;
import java.util.Vector;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.wiskopdr.AntwoordStelselVakChecker;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;


public class StelselRekenVak extends LayoutPanel  {
	
	StelselEditor hoofdEditor;
	String[] varNamen = {"n", "p"};
	Expressie[][] oplossingen = {{new BasisExpressie(0), new BasisExpressie(2)}};
	
	private boolean check;
	private boolean teltMee;
	
	private String[] randomVars;
	private HashMap<String, Number> randomVarWaarden;
	
	int headerHoogte = 23;
	int marge = 3;
	
	StelselAntwoordVak antwoordVak;
	
	ScrollPanel scrollPane;
	LayoutPanel contentPanel;
	FlowPanel headerPanel;
	
	Image ic;
	private OpdrNavIF comRoot;
	
	public StelselRekenVak(StelselAntwoordVak antwoordVak, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		int hoogte = antwoordVak.hoogte - (antwoordVak.oplossingenRegelZichtbaar?27:0);
		this.setPixelSize(antwoordVak.breedte, hoogte);
		contentPanel = new LayoutPanel();
		contentPanel.getElement().getStyle().setBackgroundColor("white");
//contentPanel.getElement().getStyle().setBackgroundColor("red");
		contentPanel.setPixelSize(antwoordVak.breedte - 2, hoogte - headerHoogte - marge); // wordt aangepast zodra hoogte hoofdEditor wordt aangepast.
		scrollPane = new ScrollPanel();
		scrollPane.setPixelSize(antwoordVak.breedte - 2, hoogte - headerHoogte - marge);
		scrollPane.getElement().getStyle().setOverflow(Overflow.AUTO);
		scrollPane.getElement().getStyle().setFloat(Style.Float.LEFT);
		scrollPane.setWidget(contentPanel);
		add(scrollPane);
		
		this.antwoordVak = antwoordVak;
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		Map<String, Object> launchState = null;
		if(map.containsKey("interactiePanelLaunchState"))
		{	launchState = map.getMap("interactiePanelLaunchState");
		}
		AntwoordStelselVakChecker avChecker = new AntwoordStelselVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
		hoofdEditor = new StelselEditor(this, h, randomVarNamen, randomVarWaarden, avChecker);
		contentPanel.add(hoofdEditor);
		contentPanel.setWidgetLeftRight(hoofdEditor, -1, Style.Unit.PX, 0, Style.Unit.PX);
		contentPanel.setWidgetTopHeight(hoofdEditor, 0, Style.Unit.PX, 100, Style.Unit.PX);
		headerPanel = hoofdEditor.getHeaderPanel();
		add(headerPanel);
		setWidgetLeftRight(headerPanel, 0, Style.Unit.PX, 2, Style.Unit.PX);
		setWidgetTopHeight(headerPanel, 0, Style.Unit.PX, headerHoogte, Style.Unit.PX);
		setWidgetLeftRight(scrollPane, 0, Style.Unit.PX, 2, Style.Unit.PX);
		setWidgetTopBottom(scrollPane, headerHoogte + marge, Style.Unit.PX, 0, Style.Unit.PX);
		
		if(launchState != null)
			init(launchState, randomVarNamen, randomVarWaarden);
		
	}
	
	public void init(Map<String, Object> h, String[] randomVars, HashMap<String, Number> randomValues)
	{
		int scoreMax = 10;
		boolean check = true;
		boolean teltMee = true;
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if (map.containsKey("scoreMax"))
			scoreMax = map.getInt("scoreMax");
		if (map.containsKey("check"))
			check = map.getBoolean("check");
		if (map.containsKey("teltMee"))
			teltMee = map.getBoolean("teltMee");
		
		this.check = check;
		this.teltMee = teltMee;
		this.randomVars = randomVars;
		this.randomVarWaarden = randomValues;
		
		hoofdEditor.zetScoreMax(scoreMax);
		hoofdEditor.zetCheck(check);
		
	}
	
	public void zetVarNamen(String[] varNamen)
	{
		this.varNamen = varNamen;
		hoofdEditor.zetVarNamen(varNamen);
	}
	
	public void setHeight(int h)
	{
		super.setHeight(h + "px");
		scrollPane.setPixelSize(antwoordVak.breedte - 2, h - headerHoogte - marge);
		if(contentPanel.getOffsetHeight() > scrollPane.getOffsetHeight())
			contentPanel.setPixelSize(scrollPane.getOffsetWidth() - 20, contentPanel.getOffsetHeight());
		else
			contentPanel.setPixelSize(scrollPane.getOffsetWidth() - 3, contentPanel.getOffsetHeight());
		
		scrollPane.scrollToBottom();
	}
	
	public void zetVolledigeBreedte(int b)
	{
		super.setWidth(b + "px");
		int scrollPaneWidth = b - 2;
		scrollPane.setPixelSize(scrollPaneWidth, scrollPane.getOffsetHeight());
		if(contentPanel.getOffsetHeight() > scrollPane.getOffsetHeight())
			contentPanel.setPixelSize(scrollPaneWidth - 20, contentPanel.getOffsetHeight());
		else
			contentPanel.setPixelSize(scrollPaneWidth - 3, contentPanel.getOffsetHeight());
	}
	
	public HashMap<String, Object> getState()
	{
		return hoofdEditor.getState();
		
	}
	
	public void setState(HashMap<String, Object> h)
	{
		hoofdEditor.setState(h);
	}
	
	public void zetJuisteOplossingen(Expressie[][] oplossingen)
	{
		this.oplossingen = oplossingen;
		hoofdEditor.zetOplossingen(oplossingen);
	}
	
	public void plaatsEditors()
	{
		int aantalKolommen = hoofdEditor.geefEindAantalKinderen();
		int kolomBreedte = contentPanel.getOffsetWidth()/aantalKolommen;
		hoofdEditor.setSizes(kolomBreedte);
		if(hoofdEditor.heeftKinderen())
			hoofdEditor.setLocations();
		int h = hoofdEditor.geefHoogteEditorEnKinderen();
		if(h > scrollPane.getOffsetHeight())
			contentPanel.setPixelSize(scrollPane.getOffsetWidth() - 20, h);
		else
			contentPanel.setPixelSize(scrollPane.getOffsetWidth() - 3, scrollPane.getOffsetHeight());
		contentPanel.setWidgetTopHeight(hoofdEditor, 0, Style.Unit.PX, hoofdEditor.geefHoogte(), Style.Unit.PX);
		scrollPane.scrollToBottom();
	}
	
	public StelselAntwoordVak geefAntwoordVak()
	{
		return antwoordVak;
	}
	
	public StelselEditor geefHoofdEditor()
	{
		return hoofdEditor;
	}
	
	public int getScore()
	{
		if (!teltMee)
			return 0;
		return hoofdEditor.getScoreEditorOfKinderen();
	}
	
	public boolean isCorrect()
	{
		if(!teltMee)
			return true;
		return hoofdEditor.zijnEditorOfKinderenCorrect();
	}
	
	public boolean isFout()
	{
		if(!teltMee)
			return false;
		//TODO: invullen.
		return false;
	}
	
	public void kijkNa()
	{
		//TODO: invullen (editors nakijken).
	}
	
	public void kijkNa(int stapNr)
	{
		//TODO: invullen (editors nakijken).
	}
	
	public void start()
	{
		//TODO: invullen.
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		if(hoofdEditor != null)
		{
			hoofdEditor.setCommunicationRoot(comRoot);
		}
			
		
	}
	
}
