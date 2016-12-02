package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;


import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.dom.client.Style;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import fi.wiskopdr.AntwoordStelselVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;



public class StelselEditor extends FormuleEditorWithSteps {
	
	StelselRekenVak hoofdPanel;
	private StelselEditor[] kinderen;
	private StelselEditor parent = null;
	//private boolean[] oplossingGevonden;
	private String[] varNamen; 
	private Expressie[][] oplossingen; 
	//private int hoogte;
	
	private boolean[][] eindOplossingGevonden;
	private boolean[][] eindOplossingGevondenVoorSplits;
	//private boolean[][] eindOplossingStelselGevonden;
	//private boolean[][] eindOplossingExactGevonden;
	//private boolean bevatVoldoetNiet = false;
	
	//private boolean isEindOplossing = false;
	//private boolean isEindOplossingStelsel = false;
	//private boolean isEindOplossingExact = false;
	
	//private boolean onafhankelijkNodig = false;
	//private boolean exactNodig = true;
	
	private boolean ingevuld = false;
	private boolean nagekeken = false;
	private boolean isGelijkwaardig = false;
	//private boolean isDeelOplossing = false;
	//private boolean bevatFouteOplossing = false;
	
	//private boolean hasFeedback = false;
	
	private boolean correct = false;
	private boolean fout = false;
	private int score = 0;
	private int scoreMax = 0; 
	
	private boolean heeftFocus = false;
	public static TextConstants rb = Text.constants;
	
	
	private StelselPijl[] pijlen = null;
	
	
	public StelselEditor(StelselRekenVak hoofdPanel, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, AntwoordVakChecker avChecker)
	{
		super(h, true, randomVarNamen, randomVarWaarden, avChecker);
		zetStandaardOpties();
		heeftFocus = true;
		this.hoofdPanel = hoofdPanel;
		stapH = 15;
		hoogte = hoofdPanel.getOffsetHeight();
	}
	
	public StelselEditor(StelselEditor parent, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, AntwoordVakChecker avChecker)
	{
		super(h, true, randomVarNamen, randomVarWaarden, avChecker);
		this.parent = parent;
		this.varNamen = parent.geefVarNamen();
		zetStandaardOpties();
		zetCheck(parent.getCheck());
		setCommunicationRoot(parent.getCommunicationRoot());
		hoofdPanel = parent.geefHoofdPanel();
		stapH = 15;
		hoogte = 40;
		scoreMax = parent.scoreMax;
	}
	
	public void zetStandaardOpties()
	{
		setHeader(false);
		zetPijl(false);
		
		zetScrollOptie(false);
		zetMetRand(false);
		zetLinkerRand();
		
	}
	
	public void zetScoreMax(int scoreMax)
	{
		this.scoreMax = scoreMax;
	}
	
	public void zetVarNamen(String[] varNamen)
	{
		this.varNamen = varNamen;
		((AntwoordStelselVakChecker) avChecker).zetVarNamen(varNamen);
	}
	
	public void zetOplossingen(Expressie[][] oplossingen)
	{
		this.oplossingen = oplossingen;
		eindOplossingGevonden = new boolean[oplossingen.length][varNamen.length];
		eindOplossingGevondenVoorSplits = new boolean[oplossingen.length][varNamen.length];
		//eindOplossingStelselGevonden = new boolean[oplossingen.length][varNamen.length];
		//eindOplossingExactGevonden = new boolean[oplossingen.length][varNamen.length];
		for(int i = 0; i < oplossingen.length; i++)
		{
			for(int j = 0; j < varNamen.length; j++)
			{	
				eindOplossingGevonden[i][j] = false;
				eindOplossingGevondenVoorSplits[i][j] = false;
				//eindOplossingStelselGevonden[i][j] = false;
				//eindOplossingExactGevonden[i][j] = false;
			}
		}
		((AntwoordStelselVakChecker) avChecker).zetOplossingen(oplossingen, eindOplossingGevonden, eindOplossingGevondenVoorSplits);//eindOplossingStelselGevonden, eindOplossingExactGevonden);
		
	}
	
	public void zetOplossingen(Expressie[][] oplossingen, boolean[][] eindOplossing, boolean[][] eindOplossingVoorSplits)//boolean[][] eindOplossingStelsel, boolean[][] eindOplossingExact)
	{
		this.oplossingen = oplossingen;
		eindOplossingGevonden = eindOplossing;
		eindOplossingGevondenVoorSplits = eindOplossingVoorSplits;
		//eindOplossingStelselGevonden = eindOplossingStelsel;
		//eindOplossingExactGevonden = eindOplossingExact;
		
		((AntwoordStelselVakChecker) avChecker).zetOplossingen(oplossingen, eindOplossingGevonden, eindOplossingGevondenVoorSplits);//eindOplossingStelselGevonden, eindOplossingExactGevonden);
	}
	
	public String[] geefVarNamen()
	{
		return varNamen;
	}
	
	public StelselRekenVak geefHoofdPanel()
	{
		return hoofdPanel;
	}
	
	public void splits() throws RestartException
	{
		VergelijkingMeerv vergelijkingen = FormuleParser.parseVergelijking("$f" + editor.toString() + "@");
		hoogte = bepaalHoogte();
		checkimg.removeFromParent();
		int breedteVergelijkingen = this.getAsPanel().getAbsoluteLeft() + this.editor.getAsPanel().getAbsoluteLeft();
		
		sluitRegelAf("$f" + editor.toString() + "@", true, false);
		
		int k = vergelijkingen.geefAantal();
		kinderen = new StelselEditor[k];
		pijlen = new StelselPijl[k];
		
		for(int i = 0; i < k; i++)
		{
			AntwoordStelselVakChecker newAvChecker = new AntwoordStelselVakChecker((AntwoordStelselVakChecker) avChecker);
			StelselEditor stelselEditor = new StelselEditor(this, h, randomVarNamen, randomVarWaarden, newAvChecker);
			Vergelijking vergelijking = vergelijkingen.geefVergelijking(i);
			int teller = 0;
			for(int j = 0; j < oplossingen.length; j++)
			{
				try {
					if(vergelijking.isOplossing(oplossingen[j], varNamen))
						teller++;
				} catch (RestartException e) {
					// Hier is de oplossing onbekend.
				}
			}
			Expressie[][] oplossingenKind = new Expressie[teller][varNamen.length];
			boolean[][] eindOplossingen = new boolean[teller][varNamen.length];
			//boolean[][] eindOplossingenExact = new boolean[teller][varNamen.length];
			boolean[][] eindOplossingenVoorSplits = new boolean[teller][varNamen.length];
			//boolean[][] eindOplossingenStelsel = new boolean[teller][varNamen.length];
			teller = 0;
			for(int j = 0; j < oplossingen.length; j++)
			{	
				boolean oplossing;
				try {
					oplossing = vergelijking.isOplossing(oplossingen[j], varNamen);
				} catch (RestartException e) {
					oplossing = false; // Weet niet
				}
				if(oplossing)
				{	
					oplossingenKind[teller] = oplossingen[j];
					for(int n = 0; n < varNamen.length; n++)
					{
						//voor situatie waarin in ene vergelijking de oplossing voor één variabele wordt gegeven en in andere vergelijking de oplossing voor een andere variabele
						if(vergelijking.isOplossing(oplossingen[j][n], varNamen[n], "="))
							eindOplossingen[teller][n] = eindOplossingGevonden[j][n];
						else
							eindOplossingen[teller][n] = eindOplossingGevondenVoorSplits[j][n];
						//eindOplossingenExact[teller][n] = eindOplossingExactGevonden[j][n];
						//eindOplossingenStelsel[teller][n] = eindOplossingStelselGevonden[j][n];
					}
					teller++;
				}
			}
			for(int j = 0; j < eindOplossingenVoorSplits.length; j++)
			{
				for(int n = 0; n < eindOplossingenVoorSplits[j].length; n++)
				{
					eindOplossingenVoorSplits[j][n] = eindOplossingen[j][n];
				}
					
			}
			stelselEditor.zetOplossingen(oplossingenKind, eindOplossingen, eindOplossingenVoorSplits);//eindOplossingenStelsel, eindOplossingenExact);
			
			kinderen[i] = stelselEditor;
			hoofdPanel.contentPanel.add(kinderen[i]);
			stelselEditor.editor.clearAll();
			stelselEditor.editor.insert(vergelijking.toString());
			int xBegin = breedteVergelijkingen + stelselEditor.editor.getWidth()/2;
			breedteVergelijkingen += stelselEditor.editor.getWidth() + 20; //+20 voor breedte van woordje 'of', moet misschien nog wat preciezer ingesteld.
			stelselEditor.editor.clearAll();
			stelselEditor.editor.insert("");
			pijlen[i] = new StelselPijl(xBegin, stelselEditor.editor.getWidth()/3); //wat hier op de tweede plek staat maakt niets uit, dat regel je nog in plaatsEditors.
			hoofdPanel.contentPanel.add(pijlen[i].getCanvas()); //stond nog in: ,0)
		}
		hoofdPanel.plaatsEditors();
		kinderen[0].editor.setCurrent(0, 0); //om te zorgen dat cursor ook getekend wordt.
		kinderen[0].requestFocus(false);
		
		
	}
	
	public void requestFocus(boolean fromEditor)
	{
		if(!heeftFocus)
		{
			hoofdPanel.geefHoofdEditor().zetFocusFalse();
			heeftFocus = true;
		}
		if(!fromEditor)
			editor.requestFocus();
	}
	
	public HashMap<String, Object> getState()
	{
		int[] aantalKinderen = geefAantalKinderen();
		Vector<Integer> stapNrsVector = geefStapNrsEditorEnKinderen();
		int[] stapNrs = new int[stapNrsVector.size()];
		for(int i = 0; i < stapNrs.length; i++)
			stapNrs[i] = stapNrsVector.get(i);
		Vector<String> formuleVakInhoudenVector = geefFormuleVakInhouden();
		String [] formuleVakInhouden = new String[formuleVakInhoudenVector.size()];
		for(int i = 0; i < formuleVakInhouden.length; i++)
			formuleVakInhouden[i] = formuleVakInhoudenVector.get(i);
		//Vector<boolean[][]> eindOplExactVector = geefEindOplExactEditorEnKinderen();
		Vector<boolean[][]> eindOplGevondenVector = geefEindOplGevondenEditorEnKinderen();
		Vector<boolean[][]> eindOplGevondenVoorSplitsVector = geefEindOplGevondenVoorSplitsEditorEnKinderen();
		//Vector<boolean[][]> eindOplStelselVector = geefEindOplStelselEditorEnKinderen();
		//boolean[][][] eindOplossingExactGevondenArrays = new boolean[eindOplExactVector.size()][][];
		boolean[][][] eindOplossingGevondenArrays = new boolean[eindOplGevondenVector.size()][][];
		boolean[][][] eindOplossingGevondenVoorSplitsArrays = new boolean[eindOplGevondenVoorSplitsVector.size()][][];
		//boolean[][][] eindOplossingStelselGevondenArrays = new boolean[eindOplStelselVector.size()][][];
		for(int i = 0; i < eindOplGevondenVector.size(); i++)
		{
			//eindOplossingExactGevondenArrays[i] = eindOplExactVector.get(i);
			eindOplossingGevondenArrays[i] = eindOplGevondenVector.get(i);
			eindOplossingGevondenVoorSplitsArrays[i] = eindOplGevondenVoorSplitsVector.get(i);
			//eindOplossingStelselGevondenArrays[i] = eindOplStelselVector.get(i);
		}
		
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("aantalKinderen", aantalKinderen);
		h.put("stapNrs", stapNrs);
		h.put("formuleVakInhouden", formuleVakInhouden);
		//h.put("eindOplossingExactGevondenArrays", eindOplossingExactGevondenArrays);
		h.put("eindOplossingGevondenArrays", eindOplossingGevondenArrays);
		h.put("eindOplossingGevondenVoorSplitsArrays", eindOplossingGevondenVoorSplitsArrays);
		//h.put("eindOplossingStelselGevondenArrays", eindOplossingStelselGevondenArrays);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		
		return h;
	}
	
	public void setState(HashMap<String, Object> h)
	{
		int[] aantalKinderen = null;
		int[] stapNrs = null;
		String[] formuleVakInhouden = null;
		//boolean[][][] eindOplossingExactGevondenArrays = null;
		boolean[][][] eindOplossingGevondenArrays = null;
		boolean[][][] eindOplossingGevondenVoorSplitsArrays = null;
		//boolean[][][] eindOplossingStelselGevondenArrays = null;
		boolean ingevuld = false;
		boolean nagekeken = false;
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("aantalKinderen"))
			aantalKinderen = map.getIntArray("aantalKinderen");
		if(map.containsKey("stapNrs"))
			stapNrs = map.getIntArray("stapNrs");
		if(map.containsKey("formuleVakInhouden"))
			formuleVakInhouden = map.getStringArray("formuleVakInhouden");
//		if(map.containsKey("eindOplossingExactGevondenArrays"))
//		{
//			ObjectList list = ( map.getObjectList("eindOplossingExactGevondenArrays"));
//			eindOplossingExactGevondenArrays = new boolean[list.size()][][];
//			for(int i = 0; i < list.size(); i++)
//			{
//				ObjectList list2 = list.getObjectList(i);
//				//List listx = JSONUtilities.toArrayList(list.get(i));
//				//ObjectList list2 = JSONUtilities.wrapList(listx);
//				try{
//					eindOplossingExactGevondenArrays[i] = new boolean[list2.size()][];
//					for(int j = 0; j < list2.size(); j++)
//					{	eindOplossingExactGevondenArrays[i][j] = list2.getBooleanArray(j);
//					}
//				}
//				catch(Exception e)
//				{
//				}
//			}
//		}
		if(map.containsKey("eindOplossingGevondenArrays"))
		{	ObjectList list = ( map.getObjectList("eindOplossingGevondenArrays"));
			eindOplossingGevondenArrays = new boolean[list.size()][][];
			for(int i = 0; i < list.size(); i++)
			{
				ObjectList list2 = list.getObjectList(i);
				//List listx = JSONUtilities.toArrayList(list.get(i));
				//ObjectList list2 = JSONUtilities.wrapList(listx);
				
				try{
					eindOplossingGevondenArrays[i] = new boolean[list2.size()][];
					for(int j = 0; j < list2.size(); j++)
					{	eindOplossingGevondenArrays[i][j] = list2.getBooleanArray(j);
					}
				}
				catch(Exception e)
				{
				}
			}
		}
		if(map.containsKey("eindOplossingGevondenVoorSplitsArrays"))
		{	ObjectList list = ( map.getObjectList("eindOplossingGevondenVoorSplitsArrays"));
			eindOplossingGevondenVoorSplitsArrays = new boolean[list.size()][][];
			for(int i = 0; i < list.size(); i++)
			{
				ObjectList list2 = list.getObjectList(i);
				//List listx = JSONUtilities.toArrayList(list.get(i));
				//ObjectList list2 = JSONUtilities.wrapList(listx);
				
				try{
					eindOplossingGevondenVoorSplitsArrays[i] = new boolean[list2.size()][];
					for(int j = 0; j < list2.size(); j++)
					{	eindOplossingGevondenVoorSplitsArrays[i][j] = list2.getBooleanArray(j);
					}
				}
				catch(Exception e)
				{
				}
			}
		}
		else
		{
			eindOplossingGevondenVoorSplitsArrays = new boolean[eindOplossingGevondenArrays.length][][];
			for(int i = 0; i < eindOplossingGevondenArrays.length; i++)
			{
				eindOplossingGevondenVoorSplitsArrays[i] = new boolean[eindOplossingGevondenArrays[i].length][];
				for(int j = 0; j < eindOplossingGevondenArrays[i].length; j++)
				{
					eindOplossingGevondenVoorSplitsArrays[i][j] = new boolean[eindOplossingGevondenArrays[i][j].length];
					for(int k = 0; k < eindOplossingGevondenArrays[i][j].length; k++)
						eindOplossingGevondenVoorSplitsArrays[i][j][k] = eindOplossingGevondenArrays[i][j][k];
				}
			}
		}
		/*if(map.containsKey("eindOplossingStelselGevondenArrays"))
		{	ObjectList list = ( map.getObjectList("eindOplossingStelselGevondenArrays"));
			eindOplossingStelselGevondenArrays = new boolean[list.size()][][];
			for(int i = 0; i < list.size(); i++)
			{
				ObjectList list2 = list.getObjectList(i);
				//List listx = JSONUtilities.toArrayList(list.get(i));
				//ObjectList list2 = JSONUtilities.wrapList(listx);
				
				try{
					eindOplossingStelselGevondenArrays[i] = new boolean[list2.size()][];
					for(int j = 0; j < list2.size(); j++)
					{	eindOplossingStelselGevondenArrays[i][j] = list2.getBooleanArray(j);
					}
				}
				catch(Exception e)
				{
				}
			}
		}*/
		if(map.containsKey("ingevuld"))
			ingevuld = map.getBoolean("ingevuld");
		if(map.containsKey("nagekeken"))
			nagekeken = map.getBoolean("nagekeken");
		
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		
		setStateEditorEnKinderen(aantalKinderen, stapNrs, formuleVakInhouden, eindOplossingGevondenArrays, 
				eindOplossingGevondenVoorSplitsArrays, 0, 0);//eindOplossingExactGevondenArrays, eindOplossingStelselGevondenArrays, 0, 0);
	}
	
	public int[] setStateEditorEnKinderen(int[] aantalKinderen, int[] stapNrs, String[] formuleVakInhouden, 
			boolean[][][] oplossingArrays, boolean[][][] voorSplitsArrays, int formuleTeller, int editorTeller)// boolean[][][] exactArrays, boolean[][][] stelselArrays, int formuleTeller, int editorTeller)
	{
		//eerst: setState van deze editor. HashMap met geschikte info maken en super.setState aanroepen;
		HashMap<String, Object> h = new HashMap<String, Object>();
		int stapNr = stapNrs[editorTeller];
		String[] formuleVakInhoudenEditor = new String[stapNr + 1];
		for(int i = 0; i < stapNr + 1; i++)
			formuleVakInhoudenEditor[i] = formuleVakInhouden[formuleTeller + i];
		//eindOplossingExactGevonden = exactArrays[editorTeller];
		eindOplossingGevonden = oplossingArrays[editorTeller];
		eindOplossingGevondenVoorSplits = voorSplitsArrays[editorTeller];
		//eindOplossingStelselGevonden = stelselArrays[editorTeller];
		
		String antwoordString = formuleVakInhoudenEditor[formuleVakInhoudenEditor.length - 1];
		ingevuld = !antwoordString.equals("$f@");
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhoudenEditor);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("antwoordString", antwoordString);
		super.setState(h);
		//in setState wordt geen maakNakijkenAf meer gedaan. Daarom mis je het weghalen van het eennalaatste oranje vinkje.
		if(editor == null && viewers.size() > 1)
		{	FormuleViewer viewer = viewers.get(viewers.size() - 2);
			viewer.showResult(FormuleViewer.NONE);
		}
		else if(latest_answer_viewer != null && editor != null && !editor.toString().equals(""))
			latest_answer_viewer.showResult(FormuleViewer.NONE);
		
		formuleTeller += stapNrs[editorTeller] + 1;
		editorTeller++;
		//dan: setStateEditorEnKinderen voor de kinderen aanroepen
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				int[] tellers = kinderen[i].setStateEditorEnKinderen(aantalKinderen, stapNrs, formuleVakInhouden, oplossingArrays, voorSplitsArrays, formuleTeller, editorTeller);//exactArrays, stelselArrays, formuleTeller, editorTeller);
				formuleTeller = tellers[0];
				editorTeller = tellers[1];
			}
		}
		int[] tellers = new int[2];
		tellers[0] = formuleTeller;
		tellers[1] = editorTeller;
		return tellers;
	}
	
	public int[] geefAantalKinderen()
	{
		if(kinderen == null)
			return new int[] {0};
		Vector<Integer> v = new Vector<Integer>();
		v.add(kinderen.length);
		for(int i = 0; i < kinderen.length; i++)
		{
			int[] k = kinderen[i].geefAantalKinderen();
			for(int j = 0; j < k.length; j++)
				v.add(k[j]);
		}
		int[] aantalKinderen = new int[v.size()];
		for(int i = 0; i < aantalKinderen.length; i++)
			aantalKinderen[i] = v.get(i);
		return aantalKinderen;
	}
	
	public Vector<String> geefFormuleVakInhouden()
	{
		Vector<String> v = new Vector<String>();
		for(int i = 0; i < getStapNr() + 1; i++)
		{
			if(viewers.size() > i && viewers.get(i) != null && (i == 0 || !viewers.get(i).toString().equals("$f@"))) 
				v.add(viewers.get(i).toString());
			else if(editor != null)
				v.add(editor.toString());
		}
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<String> v2 = kinderen[i].geefFormuleVakInhouden();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		
		return v;
	}
	
	
	
	public Vector<Integer> geefStapNrsEditorEnKinderen()
	{
		Vector<Integer> v = new Vector<Integer>();
		
		if(kinderen != null)
		{	v.add(getStapNr());
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<Integer> v2 = kinderen[i].geefStapNrsEditorEnKinderen();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		else
		{
			int stapNr = getStapNr();
			//is dit handig?
			if(stapNr > 0 && (viewers.size() < stapNr + 1 && (editor == null || editor.toString().equals("$f@"))))
				stapNr--;
			v.add(stapNr);
		}
		return v;
	}
	
//	public Vector<boolean[][]> geefEindOplExactEditorEnKinderen()
//	{
//		Vector<boolean[][]> v = new Vector<boolean[][]>();
//		v.add(eindOplossingExactGevonden);
//		if(kinderen != null)
//		{
//			for(int i = 0; i < kinderen.length; i++)
//			{
//				Vector<boolean[][]> v2 = kinderen[i].geefEindOplExactEditorEnKinderen();
//				for(int j = 0; j < v2.size(); j++)
//					v.add(v2.get(j));
//			}
//		}
//		return v;
//	}
	
	public Vector<boolean[][]> geefEindOplGevondenEditorEnKinderen()
	{
		Vector<boolean[][]> v = new Vector<boolean[][]>();
		v.add(eindOplossingGevonden);
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<boolean[][]> v2 = kinderen[i].geefEindOplGevondenEditorEnKinderen();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		return v;
	}
	
	public Vector<boolean[][]> geefEindOplGevondenVoorSplitsEditorEnKinderen()
	{
		Vector<boolean[][]> v = new Vector<boolean[][]>();
		v.add(eindOplossingGevondenVoorSplits);
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<boolean[][]> v2 = kinderen[i].geefEindOplGevondenVoorSplitsEditorEnKinderen();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		return v;
	}
	
//	public Vector<boolean[][]> geefEindOplStelselEditorEnKinderen()
//	{
//		Vector<boolean[][]> v = new Vector<boolean[][]>();
//		v.add(eindOplossingStelselGevonden);
//		if(kinderen != null)
//		{
//			for(int i = 0; i < kinderen.length; i++)
//			{
//				Vector<boolean[][]> v2 = kinderen[i].geefEindOplStelselEditorEnKinderen();
//				for(int j = 0; j < v2.size(); j++)
//					v.add(v2.get(j));
//			}
//		}
//		return v;
//	}
	
	public boolean heeftKinderen()
	{
		return kinderen != null;
	}
	
	public int geefHoogte()
	{
		return hoogte;
	}
	
	public int geefHoogteEditorEnKinderen()
	{
		if(kinderen == null)
			return hoogte;
		else
		{
			int maxKindHoogte = 0;
			for(int i = 0; i < kinderen.length; i++)
				maxKindHoogte = Math.max(maxKindHoogte, kinderen[i].geefHoogteEditorEnKinderen());
			return hoogte + maxKindHoogte;
		}
	}
	
	public int geefBreedte(int kolomBreedte)
	{
		if(kinderen == null)
			return kolomBreedte;
		int breedte = 0;
		for(int i = 0; i < kinderen.length; i++)
			breedte += kinderen[i].geefBreedte(kolomBreedte);			
		return breedte;
	}
	
	public int geefEindAantalKinderen()
	{
		if(kinderen == null)
			return 1;
		int aantalKinderen = 0;
		for(int i = 0; i < kinderen.length; i++)
		{
			aantalKinderen += kinderen[i].geefEindAantalKinderen();
		}
		return aantalKinderen;
	}
	
		
	public void geefFocusDoor()
	{
		if(hoofdPanel.geefHoofdEditor().zijnEditorOfKinderenCorrect())
		{
			hoofdPanel.geefAntwoordVak().focusNaarOplossingenVak();
		}
		else
			//volgende nog niet compleet afgeronde kind bepalen; als aan eind gekomen, dan aan begin verder, tot je weer bij dit kind komt.
			//Dit kind heeft in elk geval geen kinderen, dus eerste stap is naar parent.
		{	
			StelselEditor se = parent;
			StelselEditor se2 = this;
			while(se != null)
			{
				if(se.kinderen == null)
				{	se.requestFocus(false);
					return;
				}
				for(int i = 0; i < se.kinderen.length; i++)
				{	boolean kindGevonden = false;
					if(se2.equals(se.kinderen[i]))
					{
						kindGevonden = true;
					}
					else if(kindGevonden && !se.kinderen[i].zijnEditorOfKinderenCorrect())
					{
						//focus moet naar dit kind, of één van zijn kinderen/kleinkinderen etc
						se.kinderen[i].focusEersteVrijeKind();
						return;
					}
				}
				//alle volgende kinderen zijn nu kennelijk al klaar.
				//door naar parent.
				se2 = se;
				se = se.parent;
			}
			//als hier gekomen, dan is verderop geen vrije tak meer. Nu vanaf begin verder zoeken, tot aan this.
			for(int i = 0; i < hoofdPanel.geefHoofdEditor().kinderen.length; i++)
			{
				if(!hoofdPanel.geefHoofdEditor().kinderen[i].zijnEditorOfKinderenCorrect())
				{
					hoofdPanel.geefHoofdEditor().kinderen[i].focusEersteVrijeKind();
					return;
				}
					
			}
		}
	}
	
	public void focusEersteVrijeKind()
	{
		if(kinderen == null)
		{	editor.setCurrent(0, 0); //om cursor daadwerkelijk te tekenen.
			requestFocus(false);
		
		}
		else 
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				if(!kinderen[i].zijnEditorOfKinderenCorrect())
				{	kinderen[i].focusEersteVrijeKind();
					return;
				}
			}
		}
	}
	
	public void splitsOfMaakStap(boolean backStep, boolean show, boolean setState) throws RestartException
	{
		if(FormuleParser.parseVergelijking("$f" + editor.toString() + "@") == null)
			return;
		if(parent != null)
			parent.latest_answer_viewer.showResult(FormuleViewer.NONE);
		if(isGelijkwaardig && editor.getGoedHalfFout() != AntwoordVakChecker.GOED && FormuleParser.parseVergelijking("$f" + editor.toString() + "@").geefAantal() > 1)
			splits();
		else
		{	
			//hier juiste feedback bepalen?
			boolean focusDoorgeven = false;
			if(editor.getGoedHalfFout() == AntwoordVakChecker.GOED)
			{	correct = true;
				fout = false;
				focusDoorgeven = true;
				String feedback = "";
				if(hoofdPanel.geefHoofdEditor().zijnEditorOfKinderenCorrect())
				{	if(hoofdPanel.geefAntwoordVak().oplossingenRegelZichtbaar)
						feedback = rb.feedbackTekst21a();// "Je hebt alle oplossingen gevonden, vul ze onderaan in."
					else
					{	feedback = rb.feedbackTekst21b();// "Je hebt alle oplossingen gevonden."
						score = scoreMax;
					}
				}
				else
				{	feedback = rb.feedbackTekst22();// "Je hebt de oplossingen in deze tak gevonden, ga verder met een andere tak."
				}
				editor.zetFeedbackTekst(feedback);
			}
			else if(editor.getGoedHalfFout() == AntwoordVakChecker.DOOR || editor.getGoedHalfFout() == AntwoordVakChecker.HALF || editor.getGoedHalfFout() == AntwoordVakChecker.GEEN)
			{
				correct = false;
				fout = false;
			}
			else
			{
				correct = false;
				fout = true;
			}
			super.maakNakijkenAf(backStep, show, setState);
			hoogte = bepaalHoogte();
			hoofdPanel.plaatsEditors();
			if(focusDoorgeven)
				geefFocusDoor();
		}
	}
	
	public void setSizes(int kolomBreedte)
	{
		int h = hoogte;
		breedte = geefBreedte(kolomBreedte);
		this.getAsPanel().setPixelSize(geefBreedte(kolomBreedte), h);
		if(!getFeedback().equals(""))
			setAndAddFeedback(getFeedback());
		if(kinderen != null)
		{	for(int i = 0; i < kinderen.length; i++)
				kinderen[i].setSizes(kolomBreedte);
		}
	}
	
	public void setLocations(int x, int y)
	{
		y += hoogte;
		int breedteVergelijkingen = x;// + geefLaatsteFormuleVak().getX();
		FormuleEditor hulpEditor = new FormuleEditor();
		for(int i = 0; i < kinderen.length; i++)
		{	hoofdPanel.contentPanel.setWidgetLeftWidth(kinderen[i], x, Style.Unit.PX, kinderen[i].getWidth(), Style.Unit.PX);
			//als editor geen kinderen heeft, door laten lopen tot onderkant contentpanel.
			if(kinderen[i].heeftKinderen())
				hoofdPanel.contentPanel.setWidgetTopHeight(kinderen[i], y, Style.Unit.PX, kinderen[i].getHeight(), Style.Unit.PX);
			else
			{	//kinderen[i].setHeight(hoofdPanel.hoofdEditor.geefHoogteEditorEnKinderen() - y);
				if(kinderen[i].getHeight() < hoofdPanel.contentPanel.getOffsetHeight() - y)
					kinderen[i].setHeight(hoofdPanel.contentPanel.getOffsetHeight() - y);
				hoofdPanel.contentPanel.setWidgetTopBottom(kinderen[i], y, Style.Unit.PX, 0, Style.Unit.PX);
			}
			
			hulpEditor.clearAll();
			hulpEditor.insert(FormuleParser.parseVergelijking("$f" + this.getLatestAnswer().toString() + "@").geefVergelijking(i).toString());
			pijlen[i].zetBeginX(breedteVergelijkingen + hulpEditor.getMainRegel().getWidth() / 2 + 23); //23 correctie voor ruimte voor checkimg, nog checken of juiste waarde
			breedteVergelijkingen += hulpEditor.getMainRegel().getWidth() + 20;//20 correctie voor woordje 'of'.
			pijlen[i].zetEindX(x + kinderen[i].getWidth()/3);
			hoofdPanel.contentPanel.setWidgetLeftWidth(pijlen[i].getCanvas(), Math.min(pijlen[i].xBegin, pijlen[i].xEind), Style.Unit.PX, Math.max(5, Math.abs(pijlen[i].xBegin - pijlen[i].xEind)), Style.Unit.PX);
			hoofdPanel.contentPanel.setWidgetTopHeight(pijlen[i].getCanvas(), y - 20, Style.Unit.PX, pijlen[i].hoogte, Style.Unit.PX);
			if(kinderen[i].heeftKinderen())
				kinderen[i].setLocations(x, y);
			x += kinderen[i].getWidth();
			
		}
	}
	
		public Boolean isCorrect()
	{
		return correct;
	}
	
	public boolean zijnEditorOfKinderenCorrect()
	{
		if(kinderen == null && !correct)
			return false;
		else if(kinderen == null)
			return true;
		else
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				if(!kinderen[i].zijnEditorOfKinderenCorrect())
					return false;
			}
		}
		return true;
	}
	
	public int getScoreEditorOfKinderen()
	{
		if(kinderen == null)
			return score;
		else
		{
			for (int i = 0; i < kinderen.length; i++)
			{
				if(kinderen[i].getScoreEditorOfKinderen() > 0)
					return kinderen[i].getScoreEditorOfKinderen();
			}
		}
		return 0;
	}
	
	

	public boolean isHoofdEditor()
	{
		return this.equals(hoofdPanel.geefHoofdEditor());
	}
	
	
	public void maakNakijkenAf(boolean backStep, boolean show, boolean setState) 
	{
		int goedHalfFout = editor.getGoedHalfFout();
		if (goedHalfFout == AntwoordVakChecker.GEEN)
			ingevuld = false;
		else
			ingevuld = true;
		if(goedHalfFout == AntwoordVakChecker.GOED || goedHalfFout == AntwoordVakChecker.HALF || goedHalfFout == AntwoordVakChecker.DOOR)
			isGelijkwaardig = true;
		else
			isGelijkwaardig = false;
		
		try{
		if(heeftFocus)
			splitsOfMaakStap(backStep, show, setState);
		else 
		{
			StelselEditor editorMetFocus = vindKindMetFocus();
			editorMetFocus.splitsOfMaakStap(backStep, show, setState);
		}
		}
		catch(RestartException e)
		{
			
		}
	}
	
	public int bepaalHoogte()
	{
		int hoogte = super.bepaalHoogte();
		return hoogte;
	}
	
	public void backStep(boolean setState)
	{
		if(heeftFocus)
		{
			if(this.getStapNr() > 0)
			{	super.backStep(setState);
				return;
			}
			//nu: stapNr = 0, dus in eerste regel van de huidige editor. Deze regel leegmaken.
			checkimg.removeFromParent();
			if(editor != null)
			{	editor.clearAll();
				editor.paint();
			}
			if(this.getStapNr() == 0 && !isHoofdEditor())
			{
				//focus in meest linker kolom
				if(parent.kinderen[0].heeftFocus)
				{	for(int i = 0; i < parent.kinderen.length; i++)
					{	hoofdPanel.contentPanel.remove(parent.kinderen[i]); 
						hoofdPanel.contentPanel.remove(parent.pijlen[i].getCanvas());
					}
					hoofdPanel.contentPanel.remove(checkimg);
					parent.kinderen = null;
					parent.pijlen = null;
					parent.hoogte = parent.bepaalHoogte();
					hoofdPanel.plaatsEditors();
					parent.vervangViewerDoorEditor(setState);
					parent.requestFocus(false);
				}
				else
				{
					for(int i = 1; i < parent.kinderen.length; i++)
					{
						if(parent.kinderen[i].heeftFocus)
						{	parent.kinderen[i-1].vervangViewerDoorEditor(setState);
							parent.kinderen[i-1].requestFocus(false);
							break;
						}
					}
				}
				
			}
		}
		else
		{
			StelselEditor editorMetFocus = vindKindMetFocus();
			editorMetFocus.backStep(setState);
		}
	}
	
	public StelselEditor vindKindMetFocus()
	{
		if(heeftFocus)
			return this;
		else if(kinderen == null)
			return null;
		for(int i = 0; i < kinderen.length; i++)
		{
			StelselEditor kind = kinderen[i].vindKindMetFocus();
			if(kind != null)
				return kind;
		}
		return null;
	}
	
	public void zetFocusFalse()
	{
		heeftFocus = false;
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
				kinderen[i].zetFocusFalse();
		}
	}
	
	public void zetFocusOplossingenRegel()
	{
		zetFocusFalse();
	}
	
	
	
	
	
}
