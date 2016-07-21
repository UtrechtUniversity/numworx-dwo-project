package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import fi.wiskopdr.AntwoordStelselVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;



public class StelselEditor extends FormuleEditorWithSteps {
	
	StelselRekenVak hoofdPanel;
	private StelselEditor[] kinderen;
	private StelselEditor parent = null;
	private boolean[] oplossingGevonden;
	private String[] varNamen; 
	private Expressie[][] oplossingen; 
	//private int hoogte;
	
	private boolean[][] eindOplossingGevonden;
	private boolean[][] eindOplossingStelselGevonden;
	private boolean[][] eindOplossingExactGevonden;
	private boolean bevatVoldoetNiet = false;
	
	private boolean isEindOplossing = false;
	private boolean isEindOplossingStelsel = false;
	private boolean isEindOplossingExact = false;
	
	private boolean onafhankelijkNodig = false;
	private boolean exactNodig = true;
	
	private boolean ingevuld = false;
	private boolean nagekeken = false;
	private boolean isGelijkwaardig = false;
	private boolean isDeelOplossing = false;
	private boolean bevatFouteOplossing = false;
	
	private boolean hasFeedback = false;
	
	private boolean correct = false;
	private boolean fout = false;
	private int score = 0;
	private int scoreMax = 0; 
	
	//private FormuleEditor editor;
	
	private boolean heeftFocus = false;
	public static TextConstants rb = Text.constants;
	
	//private FormuleButton feedbackButton; //TODO: in later stadium feedback toevoegen. 
	//Met feedbackpopups zoals in formuleEditorWithAnswer? Of met feedbackregel zoals in FormuleEditorWithSteps?
	
	private StelselPijl[] pijlen = null;
	
	
	public StelselEditor(StelselRekenVak hoofdPanel, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, AntwoordVakChecker avChecker)
	{
		super(h, true, randomVarNamen, randomVarWaarden, avChecker);
		setHeader(false);
		zetStandaardOpties();
		heeftFocus = true;
		this.hoofdPanel = hoofdPanel;
		stapH = 15;
		hoogte = hoofdPanel.getOffsetHeight();
		//this.getAsPanel().getElement().getStyle().setBackgroundColor("red");
		
	}
	
	public StelselEditor(StelselEditor parent, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, AntwoordVakChecker avChecker)
	{
		super(h, true, randomVarNamen, randomVarWaarden, avChecker);
		this.parent = parent;
		this.varNamen = parent.geefVarNamen();
		setHeader(false);
		zetStandaardOpties();
		zetCheck(parent.getCheck());
		setCommunicationRoot(parent.getCommunicationRoot());
		hoofdPanel = parent.geefHoofdPanel();
		stapH = 15;
		hoogte = 40;
		scoreMax = parent.scoreMax;
		//this.getAsPanel().getElement().getStyle().setBackgroundColor("blue");
		//oplossingenGevonden en oplossingen instellen. 
	}
	
	public void zetStandaardOpties()
	{
		zetPijl(false);
		
		zetScrollOptie(false);
		zetMetRand(false);
		zetLinkerRand();
		
		/* TODO: nog maken? 
		feedbackButton = new FormuleButton("?");
		feedbackButton.addActionListener(this);
		feedbackButton.setBackground(new Color(215,215,215));
		feedbackButton.setSize(15, 15);
		feedbackButton.setVisible(false);
		add(feedbackButton);
		*/
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
		eindOplossingStelselGevonden = new boolean[oplossingen.length][varNamen.length];
		eindOplossingExactGevonden = new boolean[oplossingen.length][varNamen.length];
		for(int i = 0; i < oplossingen.length; i++)
		{
			for(int j = 0; j < varNamen.length; j++)
			{	
				eindOplossingGevonden[i][j] = false;
				eindOplossingStelselGevonden[i][j] = false;
				eindOplossingExactGevonden[i][j] = false;
			}
		}
		((AntwoordStelselVakChecker) avChecker).zetOplossingen(oplossingen, eindOplossingGevonden, eindOplossingStelselGevonden, eindOplossingExactGevonden);
		
	}
	
	public void zetOplossingen(Expressie[][] oplossingen, boolean[][] eindOplossing, boolean[][] eindOplossingStelsel, boolean[][] eindOplossingExact)
	{
		this.oplossingen = oplossingen;
		eindOplossingGevonden = eindOplossing;
		eindOplossingStelselGevonden = eindOplossingStelsel;
		eindOplossingExactGevonden = eindOplossingExact;
		((AntwoordStelselVakChecker) avChecker).zetOplossingen(oplossingen, eindOplossingGevonden, eindOplossingStelselGevonden, eindOplossingExactGevonden);
	}
	
	public String[] geefVarNamen()
	{
		return varNamen;
	}
	
	public StelselRekenVak geefHoofdPanel()
	{
		return hoofdPanel;
	}
	
	public void splits()
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
			StelselEditor stelselEditor = new StelselEditor(this, h, randomVarNamen, randomVarWaarden, avChecker);
			Vergelijking vergelijking = vergelijkingen.geefVergelijking(i);
			int teller = 0;
			for(int j = 0; j < oplossingen.length; j++)
			{
				if(vergelijking.isOplossing(oplossingen[j], varNamen))
					teller++;
			}
			Expressie[][] oplossingenKind = new Expressie[teller][varNamen.length];
			boolean[][] eindOplossingen = new boolean[teller][varNamen.length];
			boolean[][] eindOplossingenExact = new boolean[teller][varNamen.length];
			boolean[][] eindOplossingenStelsel = new boolean[teller][varNamen.length];
			teller = 0;
			for(int j = 0; j < oplossingen.length; j++)
			{	
				if(vergelijking.isOplossing(oplossingen[j], varNamen))
				{	
					oplossingenKind[teller] = oplossingen[j];
					for(int n = 0; n < varNamen.length; n++)
					{
						eindOplossingen[teller][n] = eindOplossingGevonden[j][n];
						eindOplossingenExact[teller][n] = eindOplossingExactGevonden[j][n];
						eindOplossingenStelsel[teller][n] = eindOplossingStelselGevonden[j][n];
					}
					teller++;
				}
			}
			stelselEditor.zetOplossingen(oplossingenKind, eindOplossingen, eindOplossingenStelsel, eindOplossingenExact);
			
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
		kinderen[0].requestFocus();
		hoofdPanel.plaatsEditors();
		
	}
	
	public void requestFocus()
	{
		//wat doe je hier precies??
//		if(isHoofdEditor())
//			this.formuleVak = geefLaatsteFormuleVak();
//		else
//			hoofdPanel.geefHoofdEditor().formuleVak = this.formuleVak;
		//this.formuleVak.setEditable(true);
		hoofdPanel.geefHoofdEditor().zetFocusFalse();
		heeftFocus = true;
		this.editor.requestFocus();
	}
	
	public HashMap<String, Object> getState()
	{
		//Hashtable h = super.getState();
		
		//boolean[] takEindes = geefTakEindes();
		int[] aantalKinderen = geefAantalKinderen();
		Vector<Integer> stapNrsVector = geefStapNrsEditorEnKinderen();
		int[] stapNrs = new int[stapNrsVector.size()];
		for(int i = 0; i < stapNrs.length; i++)
			stapNrs[i] = stapNrsVector.get(i);
		Vector<String> formuleVakInhoudenVector = geefFormuleVakInhouden();
		//String[] formuleVakInhouden;
//		if(formuleVakInhoudenVector.size() > 0)
//		{	
			String [] formuleVakInhouden = new String[formuleVakInhoudenVector.size()];
			for(int i = 0; i < formuleVakInhouden.length; i++)
				formuleVakInhouden[i] = formuleVakInhoudenVector.get(i);
//		}
//		else
//		{	formuleVakInhouden = new String[1];
//				formuleVakInhouden[0] = "$f@";
//		}
		Vector<boolean[][]> eindOplExactVector = geefEindOplExactEditorEnKinderen();
		Vector<boolean[][]> eindOplGevondenVector = geefEindOplGevondenEditorEnKinderen();
		Vector<boolean[][]> eindOplStelselVector = geefEindOplStelselEditorEnKinderen();
		boolean[][][] eindOplossingExactGevondenArrays = new boolean[eindOplExactVector.size()][][];
		boolean[][][] eindOplossingGevondenArrays = new boolean[eindOplGevondenVector.size()][][];
		boolean[][][] eindOplossingStelselGevondenArrays = new boolean[eindOplStelselVector.size()][][];
		for(int i = 0; i < eindOplExactVector.size(); i++)
		{
			eindOplossingExactGevondenArrays[i] = eindOplExactVector.get(i);
			eindOplossingGevondenArrays[i] = eindOplGevondenVector.get(i);
			eindOplossingStelselGevondenArrays[i] = eindOplStelselVector.get(i);
		}
		
		
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("aantalKinderen", aantalKinderen);
		h.put("stapNrs", stapNrs);
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put("eindOplossingExactGevondenArrays", eindOplossingExactGevondenArrays);
		h.put("eindOplossingGevondenArrays", eindOplossingGevondenArrays);
		h.put("eindOplossingStelselGevondenArrays", eindOplossingStelselGevondenArrays);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		
		
		return h;
	}
	
	public void setState(HashMap<String, Object> h)
	{
		int[] aantalKinderen = null;
		int[] stapNrs = null;
		String[] formuleVakInhouden = null;
		boolean[][][] eindOplossingExactGevondenArrays = null;
		boolean[][][] eindOplossingGevondenArrays = null;
		boolean[][][] eindOplossingStelselGevondenArrays = null;
		boolean ingevuld = false;
		boolean nagekeken = false;
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("aantalKinderen"))
			aantalKinderen = map.getIntArray("aantalKinderen");
		if(map.containsKey("stapNrs"))
			stapNrs = map.getIntArray("stapNrs");
		if(map.containsKey("formuleVakInhouden"))
			formuleVakInhouden = map.getStringArray("formuleVakInhouden");
		if(map.containsKey("eindOplossingExactGevondenArrays"))
			//TODO: deze arrays netter uit map halen
			eindOplossingExactGevondenArrays = (boolean[][][]) h.get("eindOplossingExactGevondenArrays");
		if(map.containsKey("eindOplossingGevondenArrays"))
			eindOplossingGevondenArrays = (boolean[][][]) h.get("eindOplossingGevondenArrays");
		if(map.containsKey("eindOplossingStelselGevondenArrays"))
			eindOplossingStelselGevondenArrays = (boolean[][][]) h.get("eindOplossingStelselGevondenArrays");
		if(map.containsKey("ingevuld"))
			ingevuld = ((Boolean) map.get("ingevuld")).booleanValue();
		if(map.containsKey("nagekeken"))
			nagekeken = ((Boolean) map.get("nagekeken")).booleanValue();
		
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		
		setStateEditorEnKinderen(aantalKinderen, stapNrs, formuleVakInhouden, eindOplossingExactGevondenArrays, 
				eindOplossingGevondenArrays, eindOplossingStelselGevondenArrays, 0, 0);
	}
	
	public int[] setStateEditorEnKinderen(int[] aantalKinderen, int[] stapNrs, String[] formuleVakInhouden, 
			boolean[][][] exactArrays, boolean[][][] oplossingArrays, boolean[][][] stelselArrays, int formuleTeller, int editorTeller)
	{
		//eerst: setState van deze editor. HashMap met geschikte info maken en super.setState aanroepen;
		HashMap<String, Object> h = new HashMap<String, Object>();
		int stapNr = stapNrs[editorTeller];
		String[] formuleVakInhoudenEditor = new String[stapNr + 1];
		for(int i = 0; i < stapNr + 1; i++)
			formuleVakInhoudenEditor[i] = formuleVakInhouden[formuleTeller + i];
		eindOplossingExactGevonden = exactArrays[editorTeller];
		eindOplossingGevonden = oplossingArrays[editorTeller];
		eindOplossingStelselGevonden = stelselArrays[editorTeller];
		
		//kijken of hier nog meer in moet, zoals ingevuld en nagekeken. Dan misschien toch beter h doorgeven.
		String antwoordString = formuleVakInhoudenEditor[formuleVakInhoudenEditor.length - 1];
		ingevuld = !antwoordString.equals("$f@");
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhoudenEditor);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("antwoordString", antwoordString);
		super.setState(h);
		
		formuleTeller += stapNrs[editorTeller] + 1;
		editorTeller++;
		//dan: setStateEditorEnKinderen voor de kinderen aanroepen
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				int[] tellers = kinderen[i].setStateEditorEnKinderen(aantalKinderen, stapNrs, formuleVakInhouden, exactArrays, oplossingArrays, stelselArrays, formuleTeller, editorTeller);
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
			//	v.add("$f@");
			//else 
				v.add(viewers.get(i).toString());
			else if(i == 0 && editor != null)
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
			if(stapNr > 0 && (viewers.get(stapNr) == null || viewers.get(stapNr).toString().equals("$f@")))
				stapNr--;
			v.add(stapNr);
		}
		return v;
	}
	
	public Vector<boolean[][]> geefEindOplExactEditorEnKinderen()
	{
		Vector<boolean[][]> v = new Vector<boolean[][]>();
		v.add(eindOplossingExactGevonden);
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<boolean[][]> v2 = kinderen[i].geefEindOplExactEditorEnKinderen();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		return v;
	}
	
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
	
	public Vector<boolean[][]> geefEindOplStelselEditorEnKinderen()
	{
		Vector<boolean[][]> v = new Vector<boolean[][]>();
		v.add(eindOplossingStelselGevonden);
		if(kinderen != null)
		{
			for(int i = 0; i < kinderen.length; i++)
			{
				Vector<boolean[][]> v2 = kinderen[i].geefEindOplStelselEditorEnKinderen();
				for(int j = 0; j < v2.size(); j++)
					v.add(v2.get(j));
			}
		}
		return v;
	}
	
	public boolean heeftKinderen()
	{
		return kinderen != null;
	}
	
	public StelselEditor[] geefKinderen()
	{
		return kinderen;
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
			if(hoofdPanel.geefAntwoordVak().oplossingenRegelZichtbaar)
				hoofdPanel.geefAntwoordVak().oplossingenVak.requestFocus();
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
				{	se.requestFocus();
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
			requestFocus();
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
	
	public void splitsOfMaakStap(boolean backStep, boolean show, boolean setState)
	{
		if(FormuleParser.parseVergelijking("$f" + editor.toString() + "@") == null)
			return;
		if(isGelijkwaardig && FormuleParser.parseVergelijking("$f" + editor.toString() + "@").geefAantal() > 1)
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
						feedback = rb.feedbackTekst21b();// "Je hebt alle oplossingen gevonden."
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
		if(kinderen != null)
		{	for(int i = 0; i < kinderen.length; i++)
				kinderen[i].setSizes(kolomBreedte);
		}
	}
	
	public void setLocations()
	{
		int x = this.getAsPanel().getAbsoluteLeft() - hoofdPanel.contentPanel.getAbsoluteLeft();
		int y = this.getAsPanel().getAbsoluteTop() - hoofdPanel.contentPanel.getAbsoluteTop() + hoogte;
		int breedteVergelijkingen = x;// + geefLaatsteFormuleVak().getX();
		FormuleEditor hulpEditor = new FormuleEditor();
		for(int i = 0; i < kinderen.length; i++)
		{	hoofdPanel.contentPanel.setWidgetLeftWidth(kinderen[i], x, Style.Unit.PX, kinderen[i].getWidth(), Style.Unit.PX);
			//als editor geen kinderen heeft, door laten lopen tot onderkant scrollpanel.
			if(kinderen[i].heeftKinderen())
				hoofdPanel.contentPanel.setWidgetTopHeight(kinderen[i], y, Style.Unit.PX, kinderen[i].getHeight(), Style.Unit.PX);
			else
			{	//kinderen[i].setHeight(hoofdPanel.hoofdEditor.geefHoogteEditorEnKinderen() - y);
				if(kinderen[i].getHeight() < hoofdPanel.scrollPane.getOffsetHeight() - y)
					kinderen[i].setHeight(hoofdPanel.scrollPane.getOffsetHeight() - y);
				hoofdPanel.contentPanel.setWidgetTopBottom(kinderen[i], y, Style.Unit.PX, 0, Style.Unit.PX);
			}
			//kinderen[i].scrollRectToVisible(new Rectangle(0, 0, 1, 1));
			
			//TODO: Wat doet dit en doet het dat goed?
			if(kinderen[i].checkimg != null && kinderen[i].checkimg.getParent().equals(hoofdPanel.contentPanel))
			{	hoofdPanel.contentPanel.setWidgetLeftWidth(kinderen[i].checkimg, x + 5, Style.Unit.PX, checkimg.getWidth(), Style.Unit.PX);
				hoofdPanel.contentPanel.setWidgetTopHeight(kinderen[i].checkimg, y + hoogte - 20, Style.Unit.PX, checkimg.getHeight(), Style.Unit.PX);
				//TODO: wat te doen met deze 20? stapH?
			}
			hulpEditor.clearAll();
			hulpEditor.insert(FormuleParser.parseVergelijking("$f" + this.getLatestAnswer().toString() + "@").geefVergelijking(i).toString());
			pijlen[i].zetBeginX(breedteVergelijkingen + hulpEditor.getMainRegel().getWidth() / 2 + 23); //23 correctie voor ruimte voor checkimg, nog checken of juiste waarde
			breedteVergelijkingen += hulpEditor.getMainRegel().getWidth() + 20;//20 correctie voor woordje 'of'.
			pijlen[i].zetEindX(x + kinderen[i].getWidth()/3);
			hoofdPanel.contentPanel.setWidgetLeftWidth(pijlen[i].getCanvas(), Math.min(pijlen[i].xBegin, pijlen[i].xEind), Style.Unit.PX, Math.max(5, Math.abs(pijlen[i].xBegin - pijlen[i].xEind)), Style.Unit.PX);
			hoofdPanel.contentPanel.setWidgetTopHeight(pijlen[i].getCanvas(), y - 20, Style.Unit.PX, pijlen[i].hoogte, Style.Unit.PX);
			if(kinderen[i].heeftKinderen())
				kinderen[i].setLocations();
			x += kinderen[i].getWidth();
			
		}
	}
	
	public void checkAntwoord()
	{
		ingevuld = false;
		
		//TODO: bevatVoldoetNiet bepalen (??).
		VergelijkingMeerv antwoord = null;
		
		String formuleVakString = editor.toString();
		
		VergelijkingMeerv antwoordIngevuld = FormuleParser.parseVergelijking(formuleVakString);
		
		antwoord = antwoordIngevuld;
		
		if (antwoord != null)
		{ 	ingevuld = true;
			
			String diffVar = "x";
			for(int i = 0; i < antwoord.geefAantal(); i++)
			{	String diffVar2 = antwoord.geefVergelijking(i).geefVarNaam();
				if(diffVar2 != null && !diffVar2.equals(""))
				{	diffVar = diffVar2;
					break;
				}
			}

			boolean isGelijkwaardigEind = antwoord.isStelselOplossing(oplossingen, varNamen);
			isGelijkwaardig = isGelijkwaardigEind;
			isEindOplossing = true;
			isEindOplossingExact = true;
			isEindOplossingStelsel = true;
			
			for(int i = 0; i < oplossingen.length; i++)
			{
				for(int j = 0; j < varNamen.length; j++)
				{	if(!eindOplossingGevonden[i][j])
					{	eindOplossingGevonden[i][j] = isGelijkwaardigEind && antwoord.isEindOplossing(oplossingen[i][j], varNamen[j], "=");
						if(!eindOplossingGevonden[i][j])
							isEindOplossing = false;
					}
					if(!eindOplossingStelselGevonden[i][j]) //TODO: kijken of hier ook nog als argument de oplossing moet worden meegegeven en zoja hoe.
					{	eindOplossingStelselGevonden[i][j] = isGelijkwaardigEind && antwoord.isStelselEindOplossing(varNamen[j], varNamen);
						if(!eindOplossingStelselGevonden[i][j])
							isEindOplossingStelsel = false;
					}
					if(!eindOplossingExactGevonden[i][j])
					{	eindOplossingExactGevonden[i][j] = isGelijkwaardigEind && antwoord.isEindOplossingExact(oplossingen[i], varNamen[j], "=");
						if(!eindOplossingExactGevonden[i][j])
							isEindOplossingExact = false;
					}
				}
			}
			
			isDeelOplossing = antwoord.isStelselDeelOplossing(oplossingen, varNamen);
			bevatFouteOplossing = antwoord.bevatFouteStelselOplossing(oplossingen, varNamen);
		}
		else
		{
			isGelijkwaardig = false;
			isEindOplossing = false;
			isEindOplossingExact = false;
			isEindOplossingStelsel = false;
			if (editor.toString().indexOf("|") > -1)
			{ 	setFeedback(rb.feedbackTekst08());//, false);
				addFeedbackComponent();
			}
			else if (editor.toString().length() > 3)
			{ 	if (mode == 2 || mode == 3)
					ingevuld = true;
				setFeedback(rb.feedbackTekst09());//, false);
				addFeedbackComponent();
			}
		}
		Algebra.setDefaultTestValues();
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
		if(heeftFocus)
			splitsOfMaakStap(backStep, show, setState);
		else
		{
			StelselEditor editorMetFocus = vindKindMetFocus();
			editorMetFocus.splitsOfMaakStap(backStep, show, setState);
		}
	}
	
	public int bepaalHoogte()
	{
		int hoogte = super.bepaalHoogte();
		//if(getFeedbackComponent() == null || !getFeedbackComponent().isShowing())
		//	hoogte += 20;
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
				editor.clearAll();
			//formuleVak.vulVak("$f@");
			if(this.getStapNr() == 0 && !isHoofdEditor())
			{
				//focus in meest linker kolom
				if(parent.kinderen[0].heeftFocus)
				{	for(int i = 0; i < parent.kinderen.length; i++)
					{	hoofdPanel.contentPanel.remove(parent.kinderen[i]); 
						hoofdPanel.contentPanel.remove(parent.pijlen[i].getCanvas());
					}
					//Component huidigIC = parent.getHuidigIC();
					hoofdPanel.contentPanel.remove(checkimg);
					parent.kinderen = null;
					parent.pijlen = null;
					parent.hoogte = parent.bepaalHoogte();
					hoofdPanel.plaatsEditors();
					parent.vervangViewerDoorEditor(setState);
					parent.requestFocus();
					
				}
				else
				{
					for(int i = 1; i < parent.kinderen.length; i++)
					{
						if(parent.kinderen[i].heeftFocus)
						{
							parent.kinderen[i-1].requestFocus();
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
		//editor = hoofdPanel.geefAntwoordVak().oplossingenVak.geefFormuleVak();
	}
	
	public void addFeedbackComponent()
	{
//		Component c = getFeedbackComponent();
//		hoofdPanel.contentPanel.add(c, 0);
//		c.setLocation(Math.min(this.getX(), hoofdPanel.getWidth() - 200), 
//				Math.min(this.getY() + this.geefLaatsteFormuleVak().getY() + this.geefLaatsteFormuleVak().getHeight() + 5, hoofdPanel.getHeight() - c.getHeight()));
	}

//	public void actionPerformed(ActionEvent e) 
//	{
//		if(e.getSource()==feedbackButton)
//		{	addFeedbackComponent();
//			
//			feedbackButton.setVisible(false);
//		}
//		else if(e.getActionCommand().equals("closeFeedback"))
//		{	feedbackButton.setVisible(true);
//			hoofdPanel.contentPanel.remove(getFeedbackComponent());
//			hoofdPanel.repaint();
////			if(feedbackPanel.getParent()!=null)
////			{	Container c = feedbackPanel.getParent();
////				c.remove(feedbackPanel);
////				c.repaint();
////			}
////			return;
//		}
//		else
//		{
//			if(e.getActionCommand().equals("focus"))
//			{
//				if(isHoofdEditor())
//					this.formuleVak = geefLaatsteFormuleVak();
//				else
//					hoofdPanel.geefHoofdEditor().formuleVak = this.formuleVak;
//				this.formuleVak.setEditable(true);
//				hoofdPanel.geefHoofdEditor().zetFocusFalse();
//				heeftFocus = true;
//			}
//			if(e.getActionCommand().equals("formChanged"))
//			{	feedbackButton.setVisible(false);
//				hoofdPanel.contentPanel.remove(getFeedbackComponent());
//				if(hoofdPanel.ic != null)
//				{	hoofdPanel.contentPanel.remove(hoofdPanel.ic);
//				}
//				hoofdPanel.repaint();
//			}
//			super.actionPerformed(e);
//		}
//	}
	
	
	
}
