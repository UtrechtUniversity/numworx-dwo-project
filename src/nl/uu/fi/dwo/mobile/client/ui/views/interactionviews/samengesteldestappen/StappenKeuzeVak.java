package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.samengesteldestappen;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

public class StappenKeuzeVak {
	
	SamengesteldeStappenPanel parent;
	//private ObjectMap[] stepContents;
	private ArrayList<Object>[] stepContents;
	private String[] keuzeMogelijkheden;
	
	private Canvas uitklapPijlCanvas;
	private Context2d gIm;
	
	private LayoutPanel basisPanel;
	int breedte = 300; 
	int hoogte = 24;
	
	TekstVak huidigeKeuzeVak;
	TekstVak[] keuzeOptieVakken;
	PopupPanel popupBox;
	LayoutPanel popupPanel;
	boolean isShowing = false;
	ArrayList<Object> kiesObjects;
	private int hoogtePopup;
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;
	private ActivityInterface activity;
	
	
	public StappenKeuzeVak(ActivityInterface a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden)
	{
		this.activity = a;
		HashMap<String, Object> launchState = null;
				
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.containsKey("launchState"))
			launchState = (HashMap<String, Object>) h.get("launchState");
			
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		basisPanel = new LayoutPanel();
		basisPanel.setPixelSize(breedte, hoogte);
		
		ObjectMap map = JSONUtilities.wrapMap(launchState);
		if(map != null)
		{
			ObjectMap[] steps = null;
			if(map.containsKey("steps") )
			{	ObjectList stepsList =map.getObjectList("steps");
				steps = new ObjectMap[stepsList.size()];
				for(int i = 0; i < stepsList.size(); i++)
					steps[i] = stepsList.getObjectMap(i);
				keuzeMogelijkheden = new String[steps.length];
				//stepContents = new ObjectMap[steps.length];
				stepContents = new ArrayList[steps.length];
				for(int i = 0; i < steps.length; i++)
				{
					keuzeMogelijkheden[i] = steps[i].getString("keuze");
					ObjectList contents = steps[i].getObjectList("stepContent");
					stepContents[i] = new ArrayList<Object>();
					for(int j = 0; j < contents.size(); j++)
					{
						ObjectMap objectMap = contents.getObjectMap(j);
						if(objectMap != null)
							stepContents[i].add(objectMap);
						else
						{
							stepContents[i].add(contents.getString(j));
						}
					}
						
					
					//stepContents[i] = steps[i].getObjectMap("stepContent");
				}
				
			}
		}
	    
	    popupBox = new PopupPanel(true);
		popupPanel = new LayoutPanel();
		popupBox.add(popupPanel);
		popupBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		popupBox.getElement().getStyle().setBorderColor("black");
		popupBox.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		popupBox.getElement().getStyle().setPadding(0, Style.Unit.PX);
		
		huidigeKeuzeVak = maakKeuzeVak();
		huidigeKeuzeVak.getElement().getStyle().setBorderColor(CssColor.make(128, 128, 128).toString());
		huidigeKeuzeVak.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		huidigeKeuzeVak.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		huidigeKeuzeVak.setPixelSize(breedte - 2, hoogte - 2);
		basisPanel.add(huidigeKeuzeVak);
		kiesObjects = new ArrayList<Object> ();
		kiesObjects.add(Text.constants.keuzeVakKiesLabel());
		huidigeKeuzeVak.setObjects(kiesObjects);
		
		
		huidigeKeuzeVak.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				int top = basisPanel.getAbsoluteTop() + basisPanel.getOffsetHeight();
			    int topMax = activity.getWindowHeight() - hoogtePopup;
			    top = Math.min(top,topMax);
			    popupBox.setPopupPosition(basisPanel.getAbsoluteLeft(), top);
				if(isShowing)
				{	popupBox.hide();
					isShowing = false;					
				}
				else
				{	popupBox.show();
					isShowing = true;
				}
			}
		}, ClickEvent.getType());
		
		huidigeKeuzeVak.addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent e)
			{	if(!isShowing && popupBox.isShowing())
				{	isShowing = true;
					
				}
			}
		}, MouseOverEvent.getType());
		
		huidigeKeuzeVak.addDomHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent e)
			{	isShowing = false;
				
			}
		}, MouseOutEvent.getType());
		
		int hoogtePanels = 0;
		int aantalKeuzes = 0;
		TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		if (keuzeMogelijkheden != null)
			aantalKeuzes = keuzeMogelijkheden.length;
		keuzeOptieVakken = new TekstVak[aantalKeuzes + 1];
		keuzeOptieVakken[0] = maakKeuzeVak();
		keuzeOptieVakken[0].setPasHoogteBreedteAan(true, false);
		keuzeOptieVakken[0].setObjects(kiesObjects);
		popupPanel.add(keuzeOptieVakken[0]);
		keuzeOptieVakken[0].resize();
		popupPanel.setWidgetLeftRight(keuzeOptieVakken[0], 0, Style.Unit.PX, 0, Style.Unit.PX);
		popupPanel.setWidgetTopHeight(keuzeOptieVakken[0], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[0].getHeight(), Style.Unit.PX);
		keuzeOptieVakken[0].getElement().getStyle().setBackgroundColor(CssColor.make(163, 184, 204).toString());
		keuzeOptieVakken[0].addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				zetVakAangewezen(0);
				
			}
		}, MouseOverEvent.getType());
		
		FlowPanel[] keuzeOptiePanels = new FlowPanel[aantalKeuzes + 1];
		keuzeOptiePanels[0] = new FlowPanel();
		keuzeOptiePanels[0].addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				zetVakAangewezen(0);
			}
		}, MouseOverEvent.getType());
		keuzeOptiePanels[0].addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				zetSelectie(0);
			}
		}, ClickEvent.getType());
		popupPanel.add(keuzeOptiePanels[0]);
		popupPanel.setWidgetLeftRight(keuzeOptiePanels[0], 0, Style.Unit.PX, 0, Style.Unit.PX);
		popupPanel.setWidgetTopHeight(keuzeOptiePanels[0], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[0].getHeight(), Style.Unit.PX);
		hoogtePanels += keuzeOptieVakken[0].getHeight();
		
		
		for (int i = 0; i < aantalKeuzes; i++)
		{
			keuzeOptieVakken[i + 1] = maakKeuzeVak();
			keuzeOptieVakken[i + 1].setPasHoogteBreedteAan(true, false);
			
			keuzeOptiePanels[i + 1] = new FlowPanel();
			
			try
			{
				keuzeMogelijkheden[i] = FormuleParser.randomizeTekstVakString(keuzeMogelijkheden[i], randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{	
			}
			ArrayList<Object> keuzeOptie = tb.convertTekst(keuzeMogelijkheden[i].trim(), null, false);
					
			keuzeOptieVakken[i + 1].setObjects(keuzeOptie);
			keuzeOptieVakken[i + 1].resize();
			popupPanel.add(keuzeOptieVakken[i + 1]);
			popupPanel.setWidgetLeftRight(keuzeOptieVakken[i + 1], 0, Style.Unit.PX, 0, Style.Unit.PX);
			popupPanel.setWidgetTopHeight(keuzeOptieVakken[i + 1], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[i + 1].getHeight(), Style.Unit.PX);
			popupPanel.add(keuzeOptiePanels[i + 1]);
			popupPanel.setWidgetLeftRight(keuzeOptiePanels[i + 1], 0, Style.Unit.PX, 0, Style.Unit.PX);
			popupPanel.setWidgetTopHeight(keuzeOptiePanels[i + 1], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[i + 1].getHeight(), Style.Unit.PX);
			
			hoogtePanels += keuzeOptieVakken[i + 1].getHeight();
			
			final int index = i + 1;
			keuzeOptiePanels[i + 1].addDomHandler(new MouseOverHandler(){
				public void onMouseOver(MouseOverEvent event) {
					zetVakAangewezen(index);
				}
			}, MouseOverEvent.getType());
			keuzeOptiePanels[i + 1].addDomHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					event.preventDefault();
					zetSelectie(index);
				}
			}, ClickEvent.getType());
			
		}
		popupPanel.setPixelSize(breedte - 23, hoogtePanels);
		this.hoogtePopup = hoogtePanels;
		uitklapPijlCanvas = Canvas.createIfSupported();
		gIm = uitklapPijlCanvas.getContext2d();
		
		uitklapPijlCanvas.setWidth(20 + "px");
		uitklapPijlCanvas.setHeight(hoogte + "px");
		uitklapPijlCanvas.setCoordinateSpaceWidth(20);
		uitklapPijlCanvas.setCoordinateSpaceHeight(hoogte);
		
		CanvasGradient gradient = gIm.createLinearGradient(0, 0, 20, hoogte);
		gradient.addColorStop(0, "white");
		gradient.addColorStop(1, CssColor.make(200, 200, 200).toString());
		gIm.setFillStyle(gradient);
		gIm.setStrokeStyle("black");
		gIm.setLineWidth(1.0d);
		gIm.rect(0, 0, 20, hoogte);
		gIm.fill();
		gIm.stroke();
		
		gIm.beginPath();
		gIm.moveTo(6, hoogte / 2 - 2);
		gIm.lineTo(14, hoogte / 2 - 2);
		gIm.lineTo(11, hoogte / 2 + 2);
		gIm.lineTo(10, hoogte / 2 + 2);
		gIm.closePath();
		gIm.setFillStyle("black");
		gIm.fill();
		
		basisPanel.add(uitklapPijlCanvas);
		basisPanel.setWidgetRightWidth(uitklapPijlCanvas, 0, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetTopBottom(uitklapPijlCanvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		
		uitklapPijlCanvas.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				int top = basisPanel.getAbsoluteTop() + basisPanel.getOffsetHeight();
				int topMax = activity.getWindowHeight() - hoogtePopup;
				top = Math.min(top, topMax);
				popupBox.setPopupPosition(basisPanel.getAbsoluteLeft(), top);
				if(isShowing)
				{	popupBox.hide();
					isShowing = false;
				}
				else
				{	popupBox.show();
					isShowing = true;
				}
			}
		}, ClickEvent.getType());
		
		uitklapPijlCanvas.addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent e)
			{	if(!isShowing && popupBox.isShowing())
				{	isShowing = true;
					
				}
			}
		}, MouseOverEvent.getType());
		
		uitklapPijlCanvas.addDomHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent e)
			{	isShowing = false;
			}
		}, MouseOutEvent.getType());
	
	}
	
	
	public TekstVak maakKeuzeVak()
	{
		TekstVak vak = new TekstVak();
		vak.setSize(breedte - 3, hoogte);
		vak.setMarges(2, 5);
		vak.getElement().getStyle().setBackgroundColor(CssColor.make(238, 238, 238).toString());
		vak.setFontName(XMLView.getDefaultFontName());
		vak.setFontSize(XMLView.getDefaultFontSize());
		vak.setColor(CssColor.make("black"));
		vak.setCentering(false, true);
		return vak;
	}
	
	public void zetBreedte(int breedte)
	{
		this.breedte = breedte;
		basisPanel.setPixelSize(breedte, hoogte);
	}
	
	public void zetVakAangewezen(int index)
	{
		for(int i = 0; i < keuzeOptieVakken.length; i++)
		{
			keuzeOptieVakken[i].getElement().getStyle().setBackgroundColor(CssColor.make(238, 238, 238).toString());
		}
		keuzeOptieVakken[index].getElement().getStyle().setBackgroundColor(CssColor.make(163, 184, 204).toString());
	}
	
	public void zetSelectie(int index)
	{
		popupBox.hide();
		isShowing = false;
		
		parent.makeStep(stepContents[index - 1]);
		parent.addSelectedStep(index - 1);
	    
	}
	
	//public ObjectMap[] getStepContents()
	//public ArrayList<ObjectMap>[] getStepContents()
	public ArrayList<Object>[] getStepContents()
	{
		return stepContents;
	}
	
	public void setParent(SamengesteldeStappenPanel parent)
	{
	    this.parent = parent;
	}
	
	public Widget asWidget()
	{
		return basisPanel;
	}
}
