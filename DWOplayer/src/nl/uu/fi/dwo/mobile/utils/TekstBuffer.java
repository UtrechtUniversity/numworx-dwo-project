package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.sco.ShareFacade;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.IFrameView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordKeuzeVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.AntwoordTekstVak2;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSelectieUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckSleepUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckValueUnit;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GetallenlijnSprongPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.ScoreWidget;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
// Deprecated import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.MC2View;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.SymboolPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.GeogebraView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak.BerekeningVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.samengesteldestappen.SamengesteldeStappenPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen.StelselAntwoordVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak.StrategieVak;

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
	HashMap<String, Number> randomVarWaarden;
	int aantalVakken = 0;
	int[] volleBreedtes;
	int huidigeKolom = 0;
	//TekstVakPanel parent = null;

	/**
	 * Zo min mogelijk gebruiken.
	 * @deprecated use {@link #TekstBuffer(String[], HashMap, AnchorContext)}
	 * @param randomVarNamen
	 * @param randomVarWaarden
	 */
	public TekstBuffer(ActivityComponent a, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this(a);
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
	}
	
	private AnchorContext context;
	private ActivityInterface activity;
	/**
	 * Tekst parser met context voor goto links.
	 * @param names
	 * @param values
	 * @param anchorContext
	 */
	public TekstBuffer(ActivityInterface activity2, String[] names, HashMap<String,Number> values, AnchorContext anchorContext) {
		this(activity2);
		randomVarNamen = names;
		randomVarWaarden = values;
		context = anchorContext;
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

	public TekstBuffer(ActivityInterface activity)
	{
		this.activity = activity;
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
		boolean  hasCodepoint = false;
		for (int i = 0; i < tekst.length() - 1; i++)
		{
			if (tekst.charAt(i) == '$' && (tekst.charAt(i + 1) == 'V' || tekst.charAt(i + 1) == 'f' || tekst.charAt(i + 1) == 'H' || tekst.charAt(i + 1) == 'I' || tekst.charAt(i+1) == 'Z'))
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
				} else if (identifier.equals("$Z"))
				{
					String codepoint = tekst.substring(i+2, endIndex+i);
					result.add((Character.toChars(Integer.parseInt(codepoint))));
					i += endIndex;
					hasCodepoint = true;
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

		// wat nu:
		if (hasCodepoint) {
			for(int i = 0; i < result.size(); i++ ) {
				Object item = result.get(i);
				if (item instanceof char[]) {
					item = new String( (char[]) item );
					if (i > 0 && result.get(i-1) instanceof String) {
						// combine with i-1
						item = result.get(i-1) + item.toString();
						result.set(i-1, item);
						result.remove(i);
						i = i-1;
					} else {
						// keep
						result.set(i, item);
					}
					if (i < result.size()-1 && result.get(i+1) instanceof String) {
						item = result.get(i).toString() + result.get(i+1);
						result.remove(i+1);
						result.set(i, item);
					}
				}
			}
		}
		return result;
	}

	private ImageView getImageView(String tekst, int i, int endIndex)
	{
		String naam = tekst.substring(i + 2, i + endIndex);

		int kolom = -1; // De default: geen volle breedte
		if (volleBreedtes != null && volleBreedtes.length > huidigeKolom)
			kolom = volleBreedtes[huidigeKolom];
		return new ImageView(naam, kolom, activity);
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
		boolean volledigebreedte = data.contains("$Eresp@");
		boolean embedded = volledigebreedte || data.contains("$Etrue@");
		String target = "_blank";
		if(embedded) {
			int kolom = -1; // De default: geen volle breedte
			if (volleBreedtes != null && volleBreedtes.length > huidigeKolom)
				kolom = volleBreedtes[huidigeKolom];
			u = data.indexOf("$B"); b = data.indexOf('@',u);
			String width = data.substring(u+2,b);
			u = data.indexOf("$C"); b = data.indexOf('@',u);
			String height = data.substring(u+2,b);
			return new IFrameView(href, width, height, volledigebreedte, kolom);
		} else {
			u = data.indexOf("$E_"); // _top _blank _self _parent
			b = data.indexOf('@',u);
			if (u >= 0) {
				target = data.substring(u+2, b);
			}
		}
// FIXME OOK ALS DESCRIPTIONVIEW IN GEBRUIK IS!		
		AnchorContext anchorContext = getAnchorContext();
		
		return new AnchorView(tekst, href, target, anchorContext);
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
		// ik denk dat het +5 is en niet +1
		if (opdrachtGegevens.size() > index + 5) // FIXME size() = 6, index = 0 get(0)= null
			currentVakGegevens = (HashMap<String, Object>) opdrachtGegevens.get(index + 5);
		else
			return ""; // 1 gegeven, 2 $V
		if (currentVakGegevens == null) // FIXME Komt voor in kladje
			return new SpookVak();

		result = getVak0(currentVakGegevens);
		if(result instanceof InteractionView)
			result = ShareFacade.wrap(JSONUtilities.wrapMap(currentVakGegevens), (InteractionView)result, activity);
		return result;
	}
	
	public Object getVak0(HashMap<String, Object> currentVakGegevens) {
		Object result;
		ObjectMap map;
		map = JSONUtilities.wrapMap(currentVakGegevens);
		int soortVak = map.getInt("soortInteractiePanel");

		switch (soortVak)
		{
		case -1:
			return new SpookVak();
//		case -2:
//// copy classname to inner, so that MCSquared.jsp can read it.
//			mc2FixInner(currentVakGegevens);
//			
//			return x(map, new MC2View(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 4: 
			return x(map, new PopupFacadeWithFont(map, new TextEditor(activity, currentVakGegevens, randomVarNamen, randomVarWaarden ), activity));
		
		
		case 39: case 10: // geogebra3
			return x(map, new GeogebraView(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 45: // GraphTool
			return x(map, new StubView(activity, "GraphToolGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return x(map, new PopupFacade(JSONUtilities.wrapMap(currentVakGegevens), new fi.graphtoolgwt.client.GraphToolGWT(currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom]), activity));

		case 15: 
			return new StubView(activity, "DoorzienGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		    //return new PopupFacade( currentVakGegevens, new fi.doorziengwt.client.DoorzienGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 20: 
// Een eerste pesterij: gooi currentVakGegevens door JSONUtilities heen.
			//currentVakGegevens = JSONUtilities.fromJSONObject( JSONUtilities.toJSONObject(currentVakGegevens).isObject());

			return new StubView(activity, "GeomAlgGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade( currentVakGegevens, new fi.geomalggwt.client.GeomAlgGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 5 :
			return new StubView(activity, "AlgebraPijlenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade( currentVakGegevens, new fi.algebrapijlengwt.client.AlgebraPijlenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 35 :
			return new StubView(activity, "AlgebraExprGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.algebraexprgwt.client.AlgebraExprGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 56 :
			return new StubView(activity, "HeksGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.heksgwt.client.HeksGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));

		
		case 46 :
			return new StubView(activity, "DraaibankGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.draaibankgwt.client.DraaibankGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 30:
			return x(map, new StubView(activity, "StatistiekGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return x(map, new PopupFacade(currentVakGegevens, new fi.statistiekgwt.client.StatistiekGWT(currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom])));
		case 43:
			return new StubView(activity, "CalculatorGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.calculatorgwt.client.CalculatorGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 42:
			return new StubView(activity, "KansbomenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(currentVakGegevens, new fi.kansbomengwt.client.KansbomenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 26:
			return new StubView(activity, "TekenVeelvlakGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.tekenveelvlakgwt.client.TekenVeelvlakGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 19:
			return new StubView(activity, "VerknippenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 22: 
			return new StubView(activity, "NormVerdGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 24:
			return new StubView(activity, "BinomVerdGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 27:
			return new StubView(activity, "MozarchGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.mozarchgwt.client.MozarchGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 48:
			return x(map,new StubView(activity, "StatSimGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return x(map,new PopupFacade(map, new fi.statsimgwt.client.StatSimGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)));
		case 54:
			return x(map, new StubView(activity, "WebLogoGWT.html",currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return new PopupFacade(map, new fi.weblogogwt.client.WebLogoGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 58:
			return x(map, new StubView(activity, "WebLogo3dGWT.html",currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return new PopupFacade(map, new fi.weblogo3dgwt.client.WebLogo3dGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 38:
			return new StubView(activity, "Grafiek3DGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			//return new PopupFacade(map, new fi.grafiek3dgwt.client.Grafiek3DGWT(currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 32:
			return new StubView(activity, "TegelsLeggenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 40:
			return x(map,
					new StubView(activity, "StippelPatronenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
					//new fi.stippelpatronengwt.client.StippelPatronenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)
					);
		case 31:
			return new StubView(activity, "StroomDiagrammenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
		case 50:
			return x(map, new StubView(activity, "SliderWidgetGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//return x(map, new PopupFacade(currentVakGegevens, new fi.sliderwidgetgwt.client.SliderWidgetGWT(currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom])));
		case 57:
			//if(true) return new nl.numworx.geodefinergwt.client.GeoDefinerGWT(currentVakGegevens, randomVarWaarden, volleBreedtes[huidigeKolom]);
			return x(map, 
					new StubView(activity, "GeoDefinerGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
			);
// Wim: ander check-tekstantwoordvak in tekstmodus
		case 13:
			return x(map, new AntwoordTekstVak2(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 62:
			//if(true) return x(map, new nl.numworx.uploadwidgetgwt.client.UploadWidgetGWT(currentVakGegevens, randomVarWaarden, volleBreedtes[huidigeKolom]));
			return x(map, 
					new StubView(activity, "UploadWidgetGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
			);
		case 64:
			return x(map, new StrategieVak(activity, currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom]));
		case 65:
			//if(true) return new nl.numworx.notebookgwt.client.NotebookGWT(currentVakGegevens, randomVarWaarden, volleBreedtes[huidigeKolom]);
			return x(map, new StubView(activity, "NotebookGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
		case 66:
			{	AnchorContext anchorContext = getAnchorContext();
				return x(map, new ScoreWidget(activity, currentVakGegevens, randomVarNamen, randomVarWaarden, anchorContext));
			}
		case 67:
			//if (true) return new nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT(currentVakGegevens, randomVarWaarden, volleBreedtes[huidigeKolom]);
			StubView ldw = new StubView(activity, "LeerdoelWidgetGWT.jsp", currentVakGegevens, randomVarNamen, randomVarWaarden);
			ldw.setAContext(getAnchorContext());
			return x(map, ldw);
			//return x(map, new nl.numworx.leerdoelwidgetgwt.client.LeerdoelWidgetGWT(currentVakGegevens, randomVarWaarden, volleBreedtes[huidigeKolom]));
		case 68:
			StubView repl = new StubView(activity, "ReplGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return x(map,repl);
		case 69:
			StubView aimodel = new StubView(activity, "AIModelGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return x(map,aimodel);
		case 70:
			StubView sqlite = new StubView(activity, "SQLiteGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return x(map,sqlite);
		case 71:
			StubView stream = new StubView(activity, "StreamGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return x(map, stream);
		case 72:
			StubView fsm = new StubView(activity, "Fsmgwt.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
			return x(map, fsm);
		}

		if (soortVak == 0)
		{
			result = x(map,new FormuleEditorWithSteps(activity, currentVakGegevens, false, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 1)
		{
			result = x(map,new FormuleEditorWithSteps(activity, currentVakGegevens, true, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 2)
		{
			result = x(map,new FormuleEditorWithAnswer(activity, currentVakGegevens, false, null, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 3)
		{
			result = x(map,new FormuleEditorWithAnswer(activity, currentVakGegevens, true, null, randomVarNamen, randomVarWaarden, null));
		}
		else if (soortVak == 6)
		{
			result = x(map, new StubView(activity, "NabouwenAanzichtenGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
			//result = x(map, new PopupFacade(currentVakGegevens, new fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)));
		}
		else if (soortVak == 9)
		{
			AnchorContext anchorContext = getAnchorContext();
			result = x(map, x(new TekstVakPanel(activity, currentVakGegevens, randomVarNamen, randomVarWaarden, anchorContext, volleBreedtes[huidigeKolom])));
		}
		else if(soortVak == 11) 
		{
			result =x(map,
					//new fi.balansfruitgwt.client.BalansFruitGWT(currentVakGegevens)
					new StubView(activity, "BalansFruitGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden)
					);
		}
		else if(soortVak == 12)
		{	
			result = x(map, new CheckSelectieUnit(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
			
		}
		else if(soortVak == 49)
		{
			result = x(map, new CheckButton(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if(soortVak == 14) 
		{
// Deze werkt niet als stub.
			result = //new StubView("AntwoordKeuzeVakGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden);
					 x(map, new AntwoordKeuzeVak(activity, currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom]));
		}
		else if(soortVak == 16)
		{	
			result = x(map, new CheckSleepUnit(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if(soortVak == 33)
		{	result = x(map, new CheckValueUnit(activity, currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if (soortVak == 41)
		{
			//result =  x(map, new PopupFacade(JSONUtilities.wrapMap(currentVakGegevens), new fi.kladjegwt.client.KladjeGWT(currentVakGegevens, randomVarNamen, randomVarWaarden), activity));
			result = x(map, new StubView(activity, "KladjeGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if (soortVak == 61)
		{
			//result =  x(map, new PopupFacade(currentVakGegevens, new fi.mathscratchgwt.client.MathScratchGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)));
			result = x(map, new StubView(activity, "MathScratchGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if (soortVak == 59)
		{
			//result =  x(map, new PopupFacade(currentVakGegevens, new fi.ivmdrawgwt.client.IVMdrawGWT(currentVakGegevens, randomVarNamen, randomVarWaarden)));
			result = x(map, new StubView(activity, "IVMdrawGWT.html", currentVakGegevens, randomVarNamen, randomVarWaarden));
		}
		else if (soortVak == 25)
		{
			result = new GetallenlijnSprongPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		//binnenkort
		else if (soortVak == 53)
		{
			result = new StelselAntwoordVak(activity, currentVakGegevens, randomVarNamen, randomVarWaarden);
			//result = "";
		}
		else if (soortVak == 55)
		{
			result = new SymboolPanel(currentVakGegevens, randomVarNamen, randomVarWaarden);
		}
		else if (soortVak == 60)
		{
			result = x(map, new SamengesteldeStappenPanel(activity, currentVakGegevens, randomVarNamen, randomVarWaarden, volleBreedtes[huidigeKolom]));
		}
		else if (soortVak == 63)
		{
			result = x(map,new BerekeningVak(activity, currentVakGegevens,  randomVarNamen, randomVarWaarden));
		}
		else
		{
			result = "";
		}

		return result;
	}

	protected AnchorContext getAnchorContext() {
		if(context != null) return context;
		return NULL_CONTEXT;
	}

	private Map<InteractionView, Connector> xWidgetMap = new HashMap<InteractionView,Connector>();
	private static final AnchorContext NULL_CONTEXT = new AnchorContext() {

		@Override
		public void gotoUrl(String href) {
		}

		@Override
		public void gotoPlace(String token) {
		}};
			
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

	
	
//	private HashMap<String, Object> mc2FixInner(HashMap<String, Object> currentVakGegevens) {
//		ObjectMap launchdata = JSONUtilities.wrapMap(currentVakGegevens);
//		@SuppressWarnings("unchecked")
//		HashMap<String,Object> inner = (HashMap<String,Object>)currentVakGegevens.get("interactiePanelLaunchState");
//		String className = currentVakGegevens.get("soortInteractiePanelClass").toString();
//		int haak = className.indexOf('[');
//		if(haak > 0 ) className = className.substring(0,haak);
//		Object value = currentVakGegevens.get(CROSS_WIDGET_ID);
//		inner.put(CROSS_WIDGET_ID, value);
//		inner.put("className", className);
//		String subscriptions = "{}"; // TODO vullen uit currentVakGegevens.
//		if(launchdata.containsKey("subscriptions"))
//		{
//			// FIXME !!!!
//			String fix = DWOplayer.clientfactory.getEntryView().getOpdrNav().getUUID();
//			int last = fix.lastIndexOf('-');
//			fix = fix.substring(0,last+1);
//			
//			ObjectMap o = launchdata.getObjectMap("subscriptions");
//			JSONObject output = new JSONObject();
//			Set<String> keys = o.keySet();
//			for (String key : keys) {
//				JSONArray array = new JSONArray();
//				ObjectList list = o.getObjectList(key);
//				int size = list.size();
//				for (int i = 0; i < size; i++) {
//					ObjectMap map = list.getObjectMap(i);
//					String xwid = map.keySet().iterator().next();
//					String command = map.getString(xwid);
//					//JSONObject oo = new JSONObject(); oo.put(fix + xwid, new JSONString(command));
//					JSONString oo = new JSONString( fix + xwid + "." + command);
//					array.set(array.size(), oo);
//				}
//				output.put(key, array);
//			}
//			
//			subscriptions = output.toString();
//		}
//		inner.put("subscriptions", subscriptions);
//		return currentVakGegevens;
//	}

	public String[] getVarNamen()
	{
		return randomVarNamen;
	}

	public HashMap<String,Number> getVarWaarden()
	{
		return randomVarWaarden;
	}
	
	public void zetVolleBreedtes(int[] breedtes)
	{
		volleBreedtes = breedtes;
	}
}
