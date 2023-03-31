package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
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

class StappenKeuzeVak implements IsWidget {
	
	StrategieVak parent;
	private ArrayList<Object>[] stepContents;
	private String[] keuzeMogelijkheden;
		
	private FlowPanel basisPanel;
	int breedte = 300; 
	int hoogte = 24;
	
	DockLayoutPanel huidigeKeuzeVak;
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;
	private ActivityInterface activity;
	
	
	StappenKeuzeVak(ActivityInterface a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden)
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
		
		basisPanel = new FlowPanel();
		basisPanel.setStyleName("stappenKeuzeVak");
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
	    		
		huidigeKeuzeVak = new DockLayoutPanel(Style.Unit.PX);
		huidigeKeuzeVak.addStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().strategieKeuzeVak());
		huidigeKeuzeVak.setPixelSize(breedte-2, hoogte - 2);
		basisPanel.add(huidigeKeuzeVak);
		Label kies = new Label(Text.constants.keuzeVakKiesLabel());
		kies.setStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().strategieLabel());
		huidigeKeuzeVak.addNorth(new FlowPanel(), 3);
		huidigeKeuzeVak.addWest(kies, 50);

		int aantalKeuzes = 0;
		if (keuzeMogelijkheden != null)
			aantalKeuzes = keuzeMogelijkheden.length;
		
		TekstVak[] keuzeOptiePanels = new TekstVak[aantalKeuzes];
        TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		
		for (int i = 0; i < aantalKeuzes; i++)
		{			
			keuzeOptiePanels[i] = maakKeuzeVak();
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
                //zetSelectie(index);
                int pos = parent.stappenVak.getRowOf(basisPanel.getParent());
                zetSelectie(pos, index);
                parent.moveKeuzeVak();
              }
            }, ClickEvent.getType());
			int bVak = keuzeOptiePanels[i].breedte;
			bVak = Math.max(bVak, 50);
			bVak += 10;
			huidigeKeuzeVak.addWest(keuzeOptiePanels[i], bVak);
		}
	
	}
	
	   public TekstVak maakKeuzeVak()
	    {
	        TekstVak vak = new TekstVak();
	        vak.addStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().strategieKeuze());
	        vak.setPasHoogteBreedteAan(false, false);
	        vak.setSize(51, 23);
	        vak.setFontName(XMLView.getDefaultFontName());
	        vak.setFontSize(XMLView.getDefaultFontSize());
	        vak.setColor(CssColor.make(38, 115, 182));
	        vak.setCentering(true, true);
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
		parent.makeStep(new ArrayList<>());
		parent.addSelectedStep(index - 1);	    
	}

	public void zetSelectie(int pos, int index) {
	    if (parent.stappenVak.isFull()) return;
	    parent.makeStep(pos, new ArrayList<>());
	    parent.makeSmall(pos);
	    parent.makeStep(pos+1, stepContents[index-1]);
	    parent.addSelectedStep(pos/2, index-1);
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
