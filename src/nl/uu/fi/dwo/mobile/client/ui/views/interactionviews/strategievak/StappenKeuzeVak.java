package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

public class StappenKeuzeVak {
	
	StrategieVak parent;
	private ArrayList<Object>[] stepContents;
	private String[] keuzeMogelijkheden;
		
	private LayoutPanel basisPanel;
	int breedte = 300; 
	int hoogte = 24;
	
	HorizontalPanel huidigeKeuzeVak;
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;
	private ActivityComponent activity;
	
	
	public StappenKeuzeVak(ActivityComponent a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden)
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
	    		
		huidigeKeuzeVak = new HorizontalPanel();
		huidigeKeuzeVak.getElement().getStyle().setBorderColor(CssColor.make(128, 128, 128).toString());
		huidigeKeuzeVak.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		huidigeKeuzeVak.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		huidigeKeuzeVak.setPixelSize(breedte - 2, hoogte - 2);
		basisPanel.add(huidigeKeuzeVak);
		Label kies = new Label(Text.constants.keuzeVakKiesLabel());
		huidigeKeuzeVak.add(kies);

		int aantalKeuzes = 0;
		if (keuzeMogelijkheden != null)
			aantalKeuzes = keuzeMogelijkheden.length;
		
		TekstVak[] keuzeOptiePanels = new TekstVak[aantalKeuzes];
        TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		
		for (int i = 0; i < aantalKeuzes; i++)
		{			
			keuzeOptiePanels[i] = maakKeuzeVak();
	        keuzeOptiePanels[i].setPasHoogteBreedteAan(true, true);
	        try {
              keuzeMogelijkheden[i] = FormuleParser.randomizeTekstVakString(keuzeMogelijkheden[i], randomVarNamen, randomVarWaarden);
            } catch (Exception e) {
            }
            ArrayList<Object> keuzeOptie = tb.convertTekst(keuzeMogelijkheden[i].trim(), Collections.emptyList(), false);
            
            keuzeOptiePanels[i].setObjects(keuzeOptie);
            keuzeOptiePanels[i].resize();


            final int index = i + 1;
			keuzeOptiePanels[i].addDomHandler(new ClickHandler() {
              
              @Override
              public void onClick(ClickEvent event) {
                zetSelectie(index);
              }
            }, ClickEvent.getType());
			huidigeKeuzeVak.add(keuzeOptiePanels[i]);
		}
	
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
	}
	
	public void zetSelectie(int index)
	{		
		parent.makeStep(stepContents[index - 1]);
		parent.addSelectedStep(index - 1);
	    
	}
	
	public ArrayList<Object>[] getStepContents()
	{
		return stepContents;
	}
	
	public void setParent(StrategieVak parent)
	{
	    this.parent = parent;
	}
	
	public Widget asWidget()
	{
		return basisPanel;
	}
}
