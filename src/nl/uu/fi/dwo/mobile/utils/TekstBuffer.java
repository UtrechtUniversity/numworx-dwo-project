package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.IFrameView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordKeuzeVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordTekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSelectieUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSleepUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckValueUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GetallenlijnSprongPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.SymboolPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;

/**
 * Uses information in the launchdata HashMap to create objects that will be
 * used to show on the screen.
 * 
 * @author Evertson Croes
 * 
 */
public class TekstBuffer
{
	private static final String CROSS_WIDGET_ID = "crossWidgetId";
	String[] randomVarNamen;
	HashMap<String, Object> randomVarWaarden;
	int aantalVakken = 0;
	int[] volleBreedtes;
	int huidigeKolom = 0;
	//TekstVakPanel parent = null;

	public TekstBuffer(String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		aantalVakken = 0;
	}
	
	/*
	public TekstBuffer(String[] randomVarNamen, HashMap randomVarWaarden, TekstVakPanel parent)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		aantalVakken = 0;
		this.parent = parent;
	}
	*/

	public TekstBuffer()
	{
		aantalVakken = 0;
	}

	public ArrayList<Object> convertTekst(HashMap<String, Object> opdracht)
	{
		return convertTekst(opdracht, 0, 0);
	}

	
	
	
	public ArrayList<Object> convertTekst(HashMap<String, Object> opdracht, int row, int column)
	{	huidigeKolom = column;
		boolean vanTekstVakPanel = false;
		ObjectMap opdrachtMap = JSONUtilities.wrapMap(opdracht);
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		String tekst = opdrachtMap.getString("tekst");
		if (tekst == null)
		{	ObjectList teksten = opdrachtMap.getObjectList("teksten");
		
			boolean random = false;
			if(opdracht.containsKey("random"))
				random = opdrachtMap.getBoolean("random");
			if(random)
			{
				String randomVar = opdrachtMap.getString("randomVar");
				int aantalRandom = opdrachtMap.getInt("aantalRandom");
				for (int i = 0; i < randomVarNamen.length; i++)
				{
					if(randomVar.equals(randomVarNamen[i]))
					{
						int tabNummer = ((Number) randomVarWaarden.get(randomVar)).intValue() - 1;
						if (tabNummer < aantalRandom && tabNummer > -1)
						{
							ObjectList randomteksten = (opdrachtMap.getObjectList("randomteksten"));
							teksten = randomteksten.getObjectList(tabNummer);
							//teksten = randomteksten[tabNummer];
							ObjectList randomIpLaunchdata = opdrachtMap.getObjectList("randomIpLaunchdata");
							opdrachtGegevens = randomIpLaunchdata.getList(tabNummer);
							
						}
						break;
					}
				}
				
			}
			
			if (teksten != null)
				tekst = teksten.getStringArray(row)[column];
			vanTekstVakPanel = true;
		}
		
		
		return convertTekst(tekst, opdrachtGegevens, vanTekstVakPanel);
		
	}
	
	
	
	
	//vanTekstVakPanel is standaard false
	//Methodes convertTekst uit elkaar getrokken op 9-4-2014. Zou niets veranderd moeten hebben aan werking, foutjes voorbehouden.
	//maar nu kan ik ook aanroepen: convertTekst(tekst, null, false); Dit in eerste instantie vooral tbv antwoordKeuzeVak.
	
	public ArrayList<Object> convertTekst(String tekst, List<Object> opdrachtGegevens, boolean vanTekstVakPanel)
	{
		
		ArrayList<Object> result = new ArrayList<Object>();
		

		if(tekst==null) tekst = "";
		
		

		int lastIndex = 0;

		for (int i = 0; i < tekst.length() - 1; i++)
		{
			if (tekst.charAt(i) == '$' && (tekst.charAt(i + 1) == 'V' || tekst.charAt(i + 1) == 'f' || tekst.charAt(i + 1) == 'H' || tekst.charAt(i + 1) == 'I'))
			{
				String identifier = tekst.substring(i, i + 2);
				String normalTekst = tekst.substring(lastIndex, i);
				int endIndex = getEndIndex(tekst.substring(i, tekst.length()));
				lastIndex = lastIndex + normalTekst.length() + endIndex + 1;
				if (normalTekst.length() != 0)
				{
					String[] breaks = getBreaks(normalTekst);
					for (int j = 0; j < breaks.length; j++)
						result.add(breaks[j]);
				}
				//"vakken"
				if (identifier.equals("$V"))
				{ // Hier ook de offset 5 was 1 FIXME Wim
					Object vak = getVak(vanTekstVakPanel ? aantalVakken - 5 : aantalVakken, opdrachtGegevens);//, result); //result toegevoegd tbv checkSelectieUnit
					result.add(vak);
					aantalVakken++;
				}
				//FormuleViewer
				else if (identifier.equals("$f"))
				{
					FormuleViewer fv = getFormuleViewer(tekst, i, endIndex);
					i = i + endIndex;
					result.add(fv);
				}
				else if (identifier.equals("$H"))
				{
					Object lr = getLinkRegel(tekst, i, endIndex);
					i = i + endIndex;
					result.add(lr);
				}

				else if (identifier.equals("$I"))
				{
					ImageView iv = getImageView(tekst, i, endIndex);
					i = i + endIndex;
					result.add(iv);
				}
				//Not supported
//				else //dollarteken in tekst
//				{	i = i + endIndex; // skip item
//					result.add("");
//				}

			}
		}
		if (result.size() == 0)
		{
			String[] breaks = getBreaks(tekst);
			for (int j = 0; j < breaks.length; j++)
				result.add(breaks[j]);
		}

		if (lastIndex < tekst.length() && lastIndex != 0)
		{
			String[] breaks = getBreaks(tekst.substring(lastIndex, tekst.length()));
			for (int j = 0; j < breaks.length; j++)
				result.add(breaks[j]);

		}

		return result;
	}

	private ImageView getImageView(String tekst, int i, int endIndex)
	{
		String naam = tekst.substring(i + 2, i + endIndex);

		return new ImageView(naam);
	}

	private FormuleViewer getFormuleViewer(String tekst, int i, int endIndex)
	{
		FormuleViewer fv;
		fv = new FormuleViewer(tekst.substring(i + 2, i + endIndex), randomVarNamen, randomVarWaarden);
		return fv;
	}
	
	private Object getLinkRegel(String tekst, int i, int endIndex) {
		String data = tekst.substring(i + 2, i + endIndex);
		int u = data.indexOf("$U");
		int b = data.indexOf("@", u);
		tekst = data.substring(0,u);
		String href = data.substring(u+2, b);
		boolean embedded = data.contains("$Etrue@");
		if(embedded) {
			u = data.indexOf("$B"); b = data.indexOf('@',u);
			String width = data.substring(u+2,b);
			u = data.indexOf("$C"); b = data.indexOf('@',u);
			String height = data.substring(u+2,b);
			return new IFrameView(href, width, height);
		}
		
		AnchorContext anchorContext = DWOplayer.clientfactory.getEntryView().getAnchorContext();
		return new AnchorView(tekst, href, anchorContext);
	}
	
	private String[] getBreaks(String normalTekst)
	{
		//if(normalTekst != null && normalTekst.length() > 0 && normalTekst.endsWith("\n"))
		//	normalTekst = normalTekst.substring(0, normalTekst.length() - 1);
		normalTekst = normalTekst.replaceAll("\n", "\n ");
		String[] result = normalTekst.split("\n");
		return result;
	}

	private int getEndIndex(String currentTekst)
	{
		int result = -1;
		ArrayList<Integer> begins = new ArrayList<Integer>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		for (int i = 0; i < currentTekst.length(); i++)
		{
			if (currentTekst.charAt(i) == '$')
			{
				begins.add(i);
			}
			if (currentTekst.charAt(i) == '@')
			{
				ends.add(i);
			}
		}

		for (int i = 0; i < begins.size(); i++)
		{
			for (int j = 0; j < ends.size(); j++)
			{
				if (begins.get(i) > ends.get(j))
				{
					if ((i - j) == 1)
					{
						result = ends.get(i - 1);
						break;
					}
				}
			}
			if (result != -1)
			{
				break;
			}
		}

		if (result == -1)
		{
			result = ends.get(ends.size() - 1);
		}

		return result;
	}

	
	private Object getVak(int index, List<Object> opdrachtGegevens)//, List<Object> objectenLijst)
	{
		Object result = null;
		HashMap<String, Object> currentVakGegevens = null;
		ObjectMap map;
		// ik denk dat het +5 is en niet +1
		if (opdrachtGegevens.size() > index + 5) // FIXME size() = 6, index = 0 get(0)= null
			currentVakGegevens = (HashMap<String, Object>) opdrachtGegevens.get(index + 5);
		else
			return ""; // 1 gegeven, 2 $V
		if (currentVakGegevens == null) // FIXME Komt voor in kladje
			return new SpookVak();

		map = JSONUtilities.wrapMap(currentVakGegevens);
		int soortVak = map.getInt("soortInteractiePanel");

		switch (soortVak)
		{
		case -2:
// copy classname to inner, so that MCSquared.html can read it.
			mc2FixInner(currentVakGegevens);
			
			return x(map, new MC2View(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 4: 
			return x(map, new PopupFacadeWithFont(map, new TextEditor( currentVakGegevens, randomVarNamen, randomVarWaarden )));
		
		
		case 39: case 10: // geogebra3
			return x(map, new GeogebraView(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 45: // GraphTool
			return x(map,
					//new StubView("GraphToolGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
					new PopupFacade( 
						map,
							new fi.graphtoolgwt.client.GraphToolGWT(currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom])
					)
		);
			
		case 15: 
			return new StubView("DoorzienGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		    //return new PopupFacade( currentVakGegevens, new fi.doorziengwt.client.DoorzienGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 20: 
// Een eerste pesterij: gooi currentVakGegevens door JSONUtilities heen.
			//currentVakGegevens = JSONUtilities.fromJSONObject( JSONUtilities.toJSONObject(currentVakGegevens).isObject());

			return new StubView("GeomAlgGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade( currentVakGegevens, new fi.geomalggwt.client.GeomAlgGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 5 :
			return new StubView("AlgebraPijlenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade( currentVakGegevens, new fi.algebrapijlengwt.client.AlgebraPijlenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 35 :
			return new StubView("AlgebraExprGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.algebraexprgwt.client.AlgebraExprGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));

		case 46 :
			return new StubView("DraaibankGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.draaibankgwt.client.DraaibankGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 30:
			return x(map, new StubView("StatistiekGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return x(map, new PopupFacade(currentVakGegevens, new fi.statistiekgwt.client.StatistiekGWT(currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom])));
		case 43:
			return new StubView("CalculatorGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.calculatorgwt.client.CalculatorGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 42:
			return new StubView("KansbomenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.kansbomengwt.client.KansbomenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 26:
			return new StubView("TekenVeelvlakGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.tekenveelvlakgwt.client.TekenVeelvlakGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 19:
			return new StubView("VerknippenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 22: 
			return new StubView("NormVerdGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 24:
			return new StubView("BinomVerdGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 27:
			return new StubView("MozarchGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.mozarchgwt.client.MozarchGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 48:
			return new StubView("StatSimGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.statsimgwt.client.StatSimGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 54:
			return new StubView("WebLogoGWT.html",currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.weblogogwt.client.WebLogoGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 38:
			return new StubView("Grafiek3DGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.grafiek3dgwt.client.Grafiek3DGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 32:
			return new StubView("TegelsLeggenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 50:
			return x(map, 
					new StubView("SliderWidgetGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
			);
		}

		if (soortVak == 0)
		{
			result = new FormuleEditorWithSteps(currentVakGegevens, false, randomVarNamen, randomVarWaarden, null);
		}
		else if (soortVak == 1)
		{
			result = x(map,new FormuleEditorWithSteps(currentVakGegevens, true, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 2)
		{
			result = x(map,new FormuleEditorWithAnswer(currentVakGegevens, false, null, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 3)
		{
			result = new FormuleEditorWithAnswer(currentVakGegevens, true, null, randomVarNamen, randomVarWaarden, null);
		}
		else if (soortVak == 6)
		{
			result = //new fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden);
					new StubView("NabouwenAanzichtenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 9)
		{
			AnchorContext anchorContext = DWOplayer.clientfactory.getEntryView().getAnchorContext();
			result = x(map, x(new TekstVakPanel(currentVakGegevens, randomVarNamen, randomVarWaarden, anchorContext)));
		}
		else if(soortVak == 11) 
		{
			result =x(map,
					//new fi.balansfruitgwt.client.BalansFruitGWT(currentVakGegevens)
					new StubView("BalansFruitGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
					);
		}
		else if(soortVak == 12)
		{	
			result = new CheckSelectieUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
			
		}
		else if(soortVak == 49)
		{
			result = new CheckButton(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 14) 
		{
// Deze werkt niet als stub.
			result = //new StubView("AntwoordKeuzeVakGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
					 new AntwoordKeuzeVak(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 16)
		{	
			result = new CheckSleepUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if(soortVak == 33)
		{	result = new CheckValueUnit(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 41)
		{
			//result = new PopupFacade(currentVakGegevens, new fi.kladjegwt.client.KladjeGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
			result = new StubView("KladjeGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 13)
		{
			result = new AntwoordTekstVak(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 25)
		{
			result = new GetallenlijnSprongPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 55)
		{
			result = new SymboolPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else
		{
			result = "";
		}

		return result;
	}

	private Map<InteractionView, Connector> xWidgetMap = new HashMap<InteractionView,Connector>();
			
	/**
	 * @return the xWidgetMap
	 */
	public Map<InteractionView, Connector> getXWidgetMap() {
		return xWidgetMap;
	}

	private InteractionView x(ObjectMap g, InteractionView v)
	{
		String value = null;
		if(g.containsKey(CROSS_WIDGET_ID)) value = g.getString(CROSS_WIDGET_ID);
		//if(value != null) 
		{
			ObjectList connections = null;
			ObjectMap  subscriptions = null;
// the old way, deprecated
			if(g.containsKey("connections"))
					connections = g.getObjectList("connections");
// the new way
			if(g.containsKey("subscriptions"))
					subscriptions = g.getObjectMap("subscriptions");
			xWidgetMap.put(v, new Connector(v, value, connections, subscriptions));
		}
		return v;
	}
	
	
	private TekstVakPanel x(TekstVakPanel v) {
		xWidgetMap.putAll(v.getXWidgetMap());
		return v;
	}

	
	
	private HashMap<String, Object> mc2FixInner(HashMap<String, Object> currentVakGegevens) {
		ObjectMap launchdata = JSONUtilities.wrapMap(currentVakGegevens);
		@SuppressWarnings("unchecked")
		HashMap<String,Object> inner = (HashMap<String,Object>)currentVakGegevens.get("interactiePanelLaunchState");
		String className = currentVakGegevens.get("soortInteractiePanelClass").toString();
		int haak = className.indexOf('[');
		if(haak > 0 ) className = className.substring(0,haak);
		Object value = currentVakGegevens.get(CROSS_WIDGET_ID);
		inner.put(CROSS_WIDGET_ID, value);
		inner.put("className", className);
		String subscriptions = "{}"; // TODO vullen uit currentVakGegevens.
		if(launchdata.containsKey("subscriptions"))
		{
			// FIXME !!!!
			String fix = DWOplayer.clientfactory.getEntryView().getOpdrNav().getUUID();
			int last = fix.lastIndexOf('-');
			fix = fix.substring(0,last+1);
			
			ObjectMap o = launchdata.getObjectMap("subscriptions");
			JSONObject output = new JSONObject();
			Set<String> keys = o.keySet();
			for (String key : keys) {
				JSONArray array = new JSONArray();
				ObjectList list = o.getObjectList(key);
				int size = list.size();
				for (int i = 0; i < size; i++) {
					ObjectMap map = list.getObjectMap(i);
					String xwid = map.keySet().iterator().next();
					String command = map.getString(xwid);
					//JSONObject oo = new JSONObject(); oo.put(fix + xwid, new JSONString(command));
					JSONString oo = new JSONString( fix + xwid + "." + command);
					array.set(array.size(), oo);
				}
				output.put(key, array);
			}
			
			subscriptions = output.toString();
		}
		inner.put("subscriptions", subscriptions);
		return currentVakGegevens;
	}

	public String[] getVarNamen()
	{
		return randomVarNamen;
	}

	public HashMap getVarWaarden()
	{
		return randomVarWaarden;
	}
	
	public void zetVolleBreedtes(int[] breedtes)
	{
		volleBreedtes = breedtes;
	}
}
