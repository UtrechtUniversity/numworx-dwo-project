package fi.wiskopdr;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
//import fi.beans.ideas.IdeasIF;
//import fi.beans.ideas.RuleIF;
//import fi.beans.stringutils.StringUtils;
//import fi.wiskopdr.formuleobjects.*;
//import fi.wiskopdr.tekstobjects.TekstArea;
import fi.wiskopdr.expressies.*;
import fi.wiskopdr.text.Text;
//import fi.wiskopdr.WiskOpdr;

//import fi.beans.wiskopdrbeans.InteractieEditPanel;
//import fi.beans.wiskopdrbeans.InteractiePanel;
//import fi.beans.wnwidgets.NWButtonUI;


public class AntwoordVergelijkingVakChecker implements AntwoordVakChecker
{
	//private AntwoordVergelijkingVak antwoordVergelijkingVak;
	
	private boolean ingevuld = false;
	boolean nagekeken = false;

	private boolean vorm;
	private boolean eindOplossingNodig;
	private boolean exact;
	private boolean isDeelOplossing = false;
	private boolean isGelijkwaardig = false;
	private boolean isEindOplossing = false;
	private boolean isEindOplossingExact = false;
	private boolean isJuisteVorm = false;

	private boolean bevatFouteOplossing = false;
	private boolean bevatVoldoetNiet = false;
	private boolean moetNogAfgerond = false;
	private boolean moetNogOngelijkheid = false;
	private boolean hasStartString = false;

	private int puntenGelijkwaardig = 0;
	private int puntenVorm = 0;
	private int puntenEindOplossing = 10;
	private int puntenExact = 0;

	private int score;
	private int scoreMax;
	private boolean correct;
	private boolean fout;
	
	private VergelijkingMeerv gewensteEindOplossing;
	private VergelijkingMeerv gewensteTussenOplossing;

	private VergelijkingMeerv[] juisteVormen;

	
	private Expressie substitutie;
	private boolean geenOplossing;
	
	

	private List<Map<String,Object>> answerModels;
	private String[] randomVarNamen;
	private HashMap<String,Object> randomVarWaarden;
	
	private String feedback;
	private boolean exactP;
	private boolean vormP;
	private boolean eindOplossingNodigP;
	private boolean gelijkwaardigP;
	private boolean hasFeedback;
	private int puntenFeedback;
	private int goedHalfFout;

	private boolean tips; // ideas aan
	private String strategieDomein;
	private int foutenTeller;
	//private int feedbackModus;
	//private boolean tipGebruikt;
	//private boolean hulpGebruikt;
	//private int aftrekTipHulp;

	private Vergelijking[] antwoordSubstituties;
	private Vergelijking[] gebruikersSubstituties;
	//private FormuleEditor gebruikersSubstitutiesVak;
	
	private boolean linStrategieVersie = false;
	private boolean linOefenVersie = false;

	
	private boolean meerTips = false;
	private boolean tipBijFout = false;
	private boolean feedbackBijFout = false;
	private boolean hulpBijTip = false;
	
	private int aftrekTip = 0;
	private int aftrekHulp = 0;
	private int aftrekStap = 0;
	private int aftrekSolve = 0;
	
	private boolean diagnose = false;
	
	private int ideasPuntenAftrek;

	private Map<String,Object> changedTexts = new HashMap<String,Object>();

	private double eqTestValueMin = 0;
	private double eqTestValueMax = 5;

	private boolean bordjesMethode = false;
	private boolean stepsForLinKwad = false;

	private boolean uitw = false;
	private boolean casAntw = false;
	private boolean casCheck = false;
	
	private VergelijkingMeerv huidigeVergelijking;
	
	private boolean geenAntwoord = false;
	private boolean syntaxFout = false;
	
	
	public AntwoordVergelijkingVakChecker(HashMap<String,Object> avvCheckerModel, String[] randomVars, HashMap<String,Object> randomValues)
	{	
		ObjectMap map = JSONUtilities.wrapMap(avvCheckerModel);
		
		randomVarNamen = randomVars;
		randomVarWaarden = randomValues;

		String antwoordString = "$f@";
		String startString = "$f@";
		boolean vorm = false;
		boolean exact = false;
		boolean stappen = true;
		int puntenGelijkwaardig = 10;
		int puntenExact = 0;
		boolean eindOplossingNodig = true;
		int puntenEindOplossing = 0;
		int puntenVorm = 0;
		boolean bewerkingKnoppen = false;
		boolean bewerkingKnoppenExtra = false;
		boolean abcKnop = false;
		boolean subKnop = false;
		boolean subKnopExtra = false;
		List<Map<String,Object>> answerModels = null;
		boolean hasFeedback = false;
		String vormString = "$f@";
		boolean tips = false;
		String strategieDomein = "";
		//int feedbackModus = 0;
		List<String> antwoordSubStrings = null;
		boolean pijl = true;
		boolean linStrategieVersie = false;
		boolean linOefenVersie = false;
		boolean bordjesMethode = false;
		boolean tipOpBalk = true;
		boolean hulpOpBalk = false;
		boolean stapOpBalk = false;
		boolean solveOpBalk = false;
		boolean meerTips = false;
		boolean tipBijFout = false;
		boolean feedbackBijFout = false;
		boolean hulpBijTip = false;
		Map<String,Object> changedTexts = new HashMap<String,Object>();
		Map<String,Object> ideasInstellingen = null;
		double eqTestValueMin = 0;
		double eqTestValueMax = 5;
		int scoreMax = 0;
		boolean uitw = false;
		boolean casAntw = false;
		boolean boxMetRand = true;
		
		if (map.containsKey("antwoordString"))
			antwoordString = map.getString("antwoordString");
		if (map.containsKey("startString"))
			startString = map.getString("startString");
		if (map.containsKey("vorm"))
			vorm = map.getBoolean("vorm");
		if (map.containsKey("exact"))
			exact = map.getBoolean("exact");
		if (map.containsKey("stappen"))
			stappen = map.getBoolean("stappen");
		if (map.containsKey("puntenGelijkwaardig"))
			puntenGelijkwaardig = map.getInt("puntenGelijkwaardig");
		if (map.containsKey("puntenExact"))
			puntenExact = map.getInt("puntenExact");
		if (map.containsKey("puntenVorm"))
			puntenVorm = map.getInt("puntenVorm");
		if (map.containsKey("puntenEindOplossing"))
			puntenEindOplossing = map.getInt("puntenEindOplossing");
		if (map.containsKey("eindOplossingNodig"))
			eindOplossingNodig = map.getBoolean("eindOplossingNodig");
		if (map.containsKey("bewerkingKnoppen"))
			bewerkingKnoppen = map.getBoolean("bewerkingKnoppen");
		if (map.containsKey("bewerkingKnoppenExtra"))
			bewerkingKnoppenExtra = map.getBoolean("bewerkingKnoppenExtra");
		if (map.containsKey("abcKnop"))
			abcKnop = map.getBoolean("abcKnop");
		if (map.containsKey("subKnop"))
			subKnop = map.getBoolean("subKnop");
		if (map.containsKey("subKnopExtra"))
			subKnopExtra = map.getBoolean("subKnopExtra");
		
		if(map.containsKey("answerModels"))
			answerModels = map.getMapList("answerModels");
		
		
		
		
		//if (avvCheckerModel.containsKey("answerModels"))
		//	answerModels = (ArrayList<HashMap<String,Object>>) avvCheckerModel.get("answerModels");
		if (map.containsKey("hasFeedback"))
			hasFeedback = map.getBoolean("hasFeedback");
		if (map.containsKey("vormString"))
			vormString = map.getString("vormString");
		if (map.containsKey("tips"))
			tips = map.getBoolean("tips");
		if (tips) {
			if (map.containsKey("ideasInstellingen"))
				ideasInstellingen = map.getMap("ideasInstellingen");
			else { // voor backward comp.
				if (map.containsKey("tipOpBalk"))
					tipOpBalk = map.getBoolean("tipOpBalk");
				if (map.containsKey("hulpOpBalk"))
					hulpOpBalk = map.getBoolean("hulpOpBalk");
				if (map.containsKey("stapOpBalk"))
					stapOpBalk = map.getBoolean("stapOpBalk");
				if (map.containsKey("solveOpBalk"))
					solveOpBalk = map.getBoolean("solveOpBalk");
				if (map.containsKey("meerTips"))
					meerTips = map.getBoolean("meerTips");
				if (map.containsKey("tipBijFout"))
					tipBijFout = map.getBoolean("tipBijFout");
				if (map.containsKey("feedbackBijFout"))
					feedbackBijFout = map.getBoolean("feedbackBijFout");
				if (map.containsKey("hulpBijTip"))
					hulpBijTip = map.getBoolean("hulpBijTip");
				if (map.containsKey("changedTexts"))
					changedTexts = map.getMap("changedTexts");
				//Hashtable sod = AntwoordVergelijkingVakEditPanel.strategieOudNieuw;
				//if (sod.containsKey(strategieDomein))
				//	strategieDomein = (String) sod.get(strategieDomein);
				if (map.containsKey("strategieDomein"))
					strategieDomein = map.getString("strategieDomein");
			}
		}

//		if (avvCheckerModel.containsKey("feedbackModus"))
//			feedbackModus = ((Integer) avvCheckerModel.get("feedbackModus")).intValue();
		if (map.containsKey("antwoordSubStrings"))
			antwoordSubStrings = map.getStringList("antwoordSubStrings");
		if (map.containsKey("pijl"))
			pijl = map.getBoolean("pijl");
		if (map.containsKey("linStrategieVersie"))
			linStrategieVersie = map.getBoolean("linStrategieVersie");
		if (map.containsKey("bordjesMethode"))
			bordjesMethode = map.getBoolean("bordjesMethode");
		if (map.containsKey("linOefenVersie"))
			linOefenVersie = map.getBoolean("linOefenVersie");
		
		if (map.containsKey("eqTestValueMin"))
			eqTestValueMin = map.getDouble("eqTestValueMin");
		if (map.containsKey("eqTestValueMax"))
			eqTestValueMax = map.getDouble("eqTestValueMax");
		if (map.containsKey("scoreMax"))
			scoreMax = map.getInt("scoreMax");
		if (map.containsKey("uitw"))
			uitw = map.getBoolean("uitw");
		if (map.containsKey("casAntw"))
			casAntw = map.getBoolean("casAntw");
		if (map.containsKey("boxMetRand"))
			boxMetRand = map.getBoolean("boxMetRand");
		
		this.linStrategieVersie = linStrategieVersie;
		this.linOefenVersie = linOefenVersie;
		this.bordjesMethode = bordjesMethode;
		this.eqTestValueMin = eqTestValueMin;
		this.eqTestValueMax = eqTestValueMax;

		this.vorm = vorm;
		this.eindOplossingNodig = eindOplossingNodig;
		this.exact = exact;
		this.puntenGelijkwaardig = puntenGelijkwaardig;
		this.puntenVorm = puntenVorm;
		this.puntenEindOplossing = puntenEindOplossing;
		this.puntenExact = puntenExact;
		
		try {
			antwoordString = FormuleParser.randomizeString(antwoordString, randomVars, randomValues);
		} catch (Exception e) {
			antwoordString = "$f???@";
			zetGeenAntwoord(true);
		}
		zetJuisteAntwoord(antwoordString);

		if (antwoordSubStrings != null) {
			boolean subCorrect = true;
			antwoordSubstituties = new Vergelijking[antwoordSubStrings.size()];
			for (int i = 0; i < antwoordSubStrings.size(); i++) {
				try {
					String ass = FormuleParser.randomizeString(antwoordSubStrings.get(i), randomVars, randomValues);
					antwoordSubstituties[i] = (FormuleParser.parseVergelijking(ass)).geefVergelijking(0);
					if (!antwoordSubstituties[i].geefExpLinks().isVar())
						subCorrect = false;
				} catch (Exception e) {
					subCorrect = false;
				}
			}
			if (!subCorrect)
				antwoordSubstituties = null;
		}

		try {
			vormString = FormuleParser.randomizeString(vormString, randomVars, randomValues);
		} catch (Exception e) {
			vormString = "$f???@";
			zetGeenAntwoord(true);
			// antwoordSyntaxFout = true;
		}
		zetJuisteVorm(vormString);

		try {
			startString = FormuleParser.randomizeString(startString, randomVars, randomValues);
		} catch (Exception e) {
			startString = "$f???@";
		}

		if (bordjesMethode && startString.length()>3) {
			startString = "$f" + (FormuleParser.parseVergelijking(startString)).toStringStrikt() + "@";
			// System.out.println(startString);
		}
		zetStartString(startString);

		//this.answerModels = answerModels;
		
		this.answerModels = new ArrayList<Map<String, Object>> ();
		for(int i = 0; i < answerModels.size(); i++)
		{	this.answerModels.add(answerModels.get(i));
		}
		
		initialiseerAnswerModels();
		this.hasFeedback = hasFeedback;

		this.tips = tips;
		//this.feedbackModus = feedbackModus;
		this.scoreMax = scoreMax;
		this.uitw = uitw;
		this.casAntw = casAntw;
		
		
		if (tips && ideasInstellingen != null)
			setIdeas(ideasInstellingen);
		else if (tips) {
			// voor backwards comp
			this.strategieDomein = strategieDomein;
			this.meerTips = meerTips;
			this.tipBijFout = tipBijFout;
			this.feedbackBijFout = feedbackBijFout;
			this.hulpBijTip = hulpBijTip;
			this.changedTexts = changedTexts;
			
			
		}
		
	}
	
	public void zetGeenAntwoord(boolean b)
	{ 	geenAntwoord = b;
	}
	
//	public HashMap<String,Object> checkAnswer(String answer, String answerPrevious)
//	{	return checkAnswer(answer, answerPrevious, null,null);
//	}
	
	public HashMap<String,Object> checkAnswer(String answer)
	{	return checkAnswer(answer,null,null,null);
	}
	
	public void zetStartString(String s) {
		
		stepsForLinKwad = false;
		try {
			VergelijkingMeerv v = FormuleParser.parseVergelijking(s);
			int graad = Algebra.geefCoefficienten(v.geefVergelijking(0)).length;
			stepsForLinKwad = graad < 4;
		} catch (Exception e) {
		}

		if (s.length() > 3) {
			
			hasStartString = true;
			
		} else {
			hasStartString = false;
		}
	}
	
	public HashMap<String,Object> checkAnswer(String answer, String answerPrevious, Expressie substitutie, Vergelijking[] gebruikersSubstituties)
	{	isDeelOplossing = false;
		isGelijkwaardig = false;
		isEindOplossing = false;
		isEindOplossingExact = false;
		isJuisteVorm = false;
		syntaxFout = false;
		
		this.feedback = "";
		this.score = 0;
		this.correct = false;
		this.fout = false;
		this.goedHalfFout = GEEN;
		
		this.substitutie = substitutie;
		this.gebruikersSubstituties = gebruikersSubstituties;
		
		if(answerPrevious==null)
		{	//Algebra.setTestValues(eqTestValueMin, eqTestValueMax);
			if(hasFeedback)checkFeedback(answer);
			else check(answer);
			//Algebra.setDefaultTestValues();
			evaluate();
		}
		else
		{	//Algebra.setTestValues(eqTestValueMin, eqTestValueMax);
			if(hasFeedback)checkFeedback(answer);
			else check(answer);
			//Algebra.setDefaultTestValues();
			evaluate();
		}
		//else if(tips && diagnose)
		//{	checkEvaluateIdeas(answer, answerPrevious);
		//}
		
		HashMap<String,Object> checkResults = new HashMap<String,Object>();
		checkResults.put("isGelijkwaardig", new Boolean(isGelijkwaardig));
		checkResults.put("isDeelOplossing", new Boolean(isDeelOplossing));
		checkResults.put("isEindOplossing", new Boolean(isEindOplossing));
		checkResults.put("isEindOplossingExact", new Boolean(isEindOplossingExact));
		checkResults.put("isJuisteVorm", new Boolean(isJuisteVorm));
		
		checkResults.put("correct", new Boolean(correct));
		checkResults.put("fout", new Boolean(fout));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
		checkResults.put("syntaxFout", new Boolean(syntaxFout));
		
		
		return checkResults;
	}
	
	
	public void initialiseerAnswerModels()
	{
		for(int i = 0; i < answerModels.size(); i++)
		{
			Map<String,Object> h = answerModels.get(i);
			if(h != null)
			{
				String antwoordString = "$f@";
				String feedback = "";
				String vormString = "$f@";
				
				ObjectMap map = JSONUtilities.wrapMap(h);
				
				if(map!=null) 
				{	if(map.containsKey("antwoordString")) 
						antwoordString = map.getString("antwoordString");
					if(map.containsKey("feedback")) 
						feedback = map.getString("feedback");
					if(map.containsKey("vormString")) 
						vormString = map.getString("vormString");
				}
				
				try         
		        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVarNamen,randomVarWaarden);
		        }
		        catch(Exception e)
		        {   antwoordString = "$f???@";
		        }
		        //System.out.println("antwoordString na randomizing: "+antwoordString);
		        
		        try         
		        {   vormString = FormuleParser.randomizeString(vormString,randomVarNamen,randomVarWaarden);
		        }
		        catch(Exception e)
		        {   vormString = "$f???@";
		            
		        }
		        
		        try         
		        {   feedback = modifyFeedback(feedback);
		        	feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
		        }
		        catch(Exception e)
		        {   feedback = "$f???@";
		        }
		        
		        //Nieuwe hashmap maken die niet meer gekoppeld is aan h; h is namelijk nog gekoppeld aan de launchstate
		        //en daarin moeten de randomvariabelen niet worden vervangen door waarden (dan gaat 'item opnieuw' en 
		        //'alles opnieuw' fout). 
		        Map<String, Object> h2 = new HashMap<String,Object>();
		        Iterator<String> keys = h.keySet().iterator();
		        while(keys.hasNext())
		        {
		        	String key = keys.next();
		        	h2.put(key, h.get(key));
		        }
		        //h2.putAll((Map<String, Object>) map); //FIXME: waarom werkt dit niet??
		        h2.put("antwoordString", antwoordString);
		        h2.put("feedback", feedback);
		        h2.put("vormString", vormString);
		        answerModels.remove(i);
		        answerModels.add(i, h2);
			}
		}
	}
	
	
	
	
	
	public void setAnswerModel(int nr)
	{	Map<String,Object> h = answerModels.get(nr);
		if(h==null) return;
	
		String antwoordString = "$f@";
		boolean gelijkwaardig = true;
		boolean vorm = false;
		boolean eindOplossingNodig = false;
		boolean exact = false;
		
		int puntenFeedback = 0;
		String feedback = "";
		String vormString = "$f@";
		int goedHalfFout = 4;
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map!=null) 
		{	if(map.containsKey("antwoordString")) antwoordString = map.getString("antwoordString");
			if(map.containsKey("gelijkwaardig")) gelijkwaardig = map.getBoolean("gelijkwaardig");
			if(map.containsKey("vorm")) vorm = map.getBoolean("vorm");
			if(map.containsKey("eindOplossingNodig")) eindOplossingNodig = map.getBoolean("eindOplossingNodig");
			if(map.containsKey("exact")) exact = map.getBoolean("exact");
			if(map.containsKey("puntenFeedback")) puntenFeedback = map.getInt("puntenFeedback");
			if(map.containsKey("feedback")) {
				feedback = map.getString("feedback");
			}
			if(map.containsKey("vormString")) vormString = map.getString("vormString");
			if(map.containsKey("goedHalfFout")) goedHalfFout = map.getInt("goedHalfFout");
			
		}	
		exactP = exact;
		vormP = vorm;
		eindOplossingNodigP = eindOplossingNodig;
		gelijkwaardigP = gelijkwaardig;
		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;
		
		//System.out.println("antwoordString: "+antwoordString);
		//Randomiseren niet hier pas, maar al bij initialisatie. 
		//Dan hoeft het niet bij elke keer nakijken.
//        try         
//        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVarNamen,randomVarWaarden);
//        }
//        catch(Exception e)
//        {   antwoordString = "$f???@";
//        }
//        //System.out.println("antwoordString na randomizing: "+antwoordString);
//        
//        try         
//        {   vormString = FormuleParser.randomizeString(vormString,randomVarNamen,randomVarWaarden);
//        }
//        catch(Exception e)
//        {   vormString = "$f???@";
//            
//        }
//        
//        try         
//        {   feedback = modifyFeedback(feedback);
//        	feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
//        }
//        catch(Exception e)
//        {   feedback = "$f???@";
//        }
        zetJuisteAntwoord(antwoordString);
		zetJuisteVorm(vormString);
       
        this.feedback = feedback.trim();
       
       
	}
	
	
	
	public void setIdeas(Map<String,Object> h)
	{
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("strategieDomein")) strategieDomein = map.getString("strategieDomein");
		if(map.containsKey("meerTips")) meerTips = map.getBoolean("meerTips");
        if(map.containsKey("tipBijFout")) tipBijFout = map.getBoolean("tipBijFout");
        if(map.containsKey("feedbackBijFout")) feedbackBijFout = map.getBoolean("feedbackBijFout");
        if(map.containsKey("hulpBijTip")) hulpBijTip = map.getBoolean("hulpBijTip");
        if (map.containsKey("diagnose"))
			diagnose = map.getBoolean("diagnose");
		if (map.containsKey("aftrekTip"))
			aftrekTip = map.getInt("aftrekTip");
		if (map.containsKey("aftrekHulp"))
			aftrekHulp = map.getInt("aftrekHulp");
		if (map.containsKey("aftrekStap"))
			aftrekStap = map.getInt("aftrekStap");
		if (map.containsKey("aftrekSolve"))
			aftrekSolve = map.getInt("aftrekSolve");
        
       
		
	}
	
	public void zetSubstitutie(Expressie e)
	{
		System.out.println("antwoordvergelijkingvakChecker zetSubstitutie"); 
		if(e != null)
			System.out.println("zet substitutie: " + e.toString());
		substitutie = e;
	}
	
	
	public void zetJuisteAntwoord(String s)
	{	
		if (s.length() > 3 && s.substring(0, 4).equals("CAS{")) {
			casCheck = true;
			return;
		}
		// System.out.println(s);
		//if (Text.language.getLanguage().equals("en"))
		//	s = s.replaceAll("of", "or");
		FormuleParser p = new FormuleParser();
		int index = s.indexOf(";");
		if (index > -1) {
			String s1 = s.substring(0, index) + "@";
			gewensteTussenOplossing = p.parseVergelijking(s1);
			s = "$f" + s.substring(index + 1);
		}
		gewensteEindOplossing = p.parseVergelijking(s);
	}
	
	public void zetJuisteVorm(String s)
	{	
		s = s.substring(2, s.length() - 1);
		String[] antwoordStrings = s.split("::");

		juisteVormen = new VergelijkingMeerv[antwoordStrings.length];

		FormuleParser p = new FormuleParser();
		for (int i = 0; i < antwoordStrings.length; i++) {
			String antwoordStr = "$f" + antwoordStrings[i] + "@";
			juisteVormen[i] = p.parseVergelijking(antwoordStr);
		}
		
	}
	
	
	
	public String modifyFeedback(String tekst)
	{
		for(int i=tekst.length()-1 ; i>-1; i--)
		{	if(tekst.charAt(i)=='@')
			{	int index = tekst.substring(0,i).lastIndexOf("$f");
				
				int indexF = tekst.substring(0,i).lastIndexOf("$f");
				int indexA = tekst.substring(0,i).lastIndexOf("$A");
				int indexV = tekst.substring(0,i).lastIndexOf("$V");
				int indexH = tekst.substring(0,i).lastIndexOf("$H");
				int indexI = tekst.lastIndexOf("$I", i);
				index = Math.max(indexF, indexA);
				index = Math.max(index, indexV);
				index = Math.max(index, indexH);
				index = Math.max(index, indexI);
				
				String formString = tekst.substring(index,i+1);
				for(int j=formString.length()-1 ; j>-1; j--)
				{	if(formString.charAt(j)=='}')
					{	int index1 = formString.substring(0,j).lastIndexOf("{");
						String parseString = formString.substring(index1+1,j);
						if(parseString.equals("ANS"))
						{	//parseString = formuleVak.toString();
							parseString = parseString.substring(2,parseString.length()-1);
						}
						formString = ""+formString.substring(0,index1)+parseString+formString.substring(j+1);
						j=index1;
					}	
				}	
				
				tekst = ""+tekst.substring(0,index)+formString+tekst.substring(i+1);
				
				i=index;
			}
		}
		return tekst;
	}
	
	
	
	/*
	public void checkEvaluateIdeas(String expAnswerString , String expAnswerStringPrevious)
	{	
		//if (WiskOpdr.ideas == null) {
		//	JOptionPane.showMessageDialog(antwoordVergelijkingVak, "Feedbackservice not available");
		//	return;
		//}
		score = 0;
		
		
		boolean geenOplossing = false;
		VergelijkingMeerv antwoordGeen = null;

		if (expAnswerString.equals("$f@"))
			return;
		else if (expAnswerString.length() > 6 && expAnswerString.substring(2, 6).equals("geen") || expAnswerString.length() > 4
				&& expAnswerString.substring(2, 4).equals("no")) {
			geenOplossing = true;
			Vergelijking v = new Vergelijking(new BasisExpressie(FormuleParser.parseVergelijking(expAnswerStringPrevious).geefVergelijkingVar()), new BasisExpressie(0.1234567));
			Vergelijking[] vn = new Vergelijking[1];
			vn[0] = v;
			antwoordGeen = new VergelijkingMeerv(vn);
		} 
		else if (expAnswerString.length() > 7 && expAnswerString.substring(2, 7).equals("alles") || expAnswerString.length() > 5
                && expAnswerString.substring(2, 5).equals("all")) {
            geenOplossing = true;
             Vergelijking v = new Vergelijking(new BasisExpressie(FormuleParser.parseVergelijking(expAnswerStringPrevious).geefVergelijkingVar()), new BasisExpressie(0.7654321));
            Vergelijking[] vn = new Vergelijking[1];
            vn[0] = v;
            antwoordGeen = new VergelijkingMeerv(vn);
        }
		else if (FormuleParser.parseVergelijking(expAnswerString) == null) {
			feedback = Text.rb.getString("feedbackTekst09");
			correct = false;
			fout = false;
			return;
		}

		ingevuld = true;
		
		String vglStriktVorig = FormuleParser.parseVergelijking(expAnswerStringPrevious).toStringStrikt();
		vglStriktVorig = vertaalNaarIdeasExpressie(vglStriktVorig);
		String vglStriktHuidig = null;
		if (antwoordGeen != null)
			vglStriktHuidig = antwoordGeen.toStringStrikt();
		else
			vglStriktHuidig = FormuleParser.parseVergelijking(expAnswerString).toStringStrikt();
		// System.out.println(vglStriktHuidig);
		vglStriktHuidig = vertaalNaarIdeasExpressie(vglStriktHuidig);

		// System.out.println("diagnose("+vglStriktVorig+","+vglStriktHuidig+","+strategieDomein+")");

		RuleIF rule = WiskOpdr.ideas.diagnose(vglStriktVorig, vglStriktHuidig, strategieDomein);
		// System.out.println(rule.getName());

		String feedback = translateRule(rule.getName());
		if (rule.getName().equals("notequiv")) {
			feedback = translateRule(rule.getName());
			goedHalfFout = FOUT;
			correct = false;
			fout = true;
		} else if (rule.getName().equals("buggy")) {
			if (feedbackBijFout)
				feedback = formatRuleText(rule, translateRule(rule.getId()));
			goedHalfFout = FOUT;
			correct = false;
			fout = true;
		} else if (rule.isReady()) {
			feedback = translateRule("ready");
			goedHalfFout = GOED;
			if (!hasFeedback)
				correct = true;
			fout = false;
			score = puntenExact + puntenEindOplossing + puntenGelijkwaardig;
		} else if (rule.getName().equals("similar")) {
			feedback = translateRule(rule.getName());
			goedHalfFout = GEEN;
			correct = false;
			fout = false;
		}

		else if (rule.getName().equals("expected")) {
			feedback = translateRule(rule.getName());
			goedHalfFout = HALF;
			correct = false;
			fout = false;
		} else if (rule.getName().equals("detour")) {
			feedback = translateRule(rule.getName());
			goedHalfFout = HALF;
			correct = false;
			fout = false;
		} else if (rule.getName().equals("correct")) {
			feedback = translateRule(rule.getName());
			goedHalfFout = HALF;
			correct = false;
			fout = false;
		}

		if (fout && tipBijFout) {
			RuleIF rules = WiskOpdr.ideas.getOneFirst(vglStriktVorig, strategieDomein);
			if (hulpBijTip)
				feedback = feedback + "\n\nTip: \n" + formatRuleText(rules,translateRule(rules.getId())) + "\n";
			else
				feedback = "Tip: \n" + formatRuleText(rules,translateRule(rules.getId())) + "\n";
			
			
		} 

		if (hasFeedback) {
			evaluate();
			if (rule.getName().equals("similar"))
				;//stapOk = false;
		} 
	}
	*/
	public void evaluate()
	{	
		
		if (!ingevuld) {
			goedHalfFout = GEEN;
			return;
		} 
		if(syntaxFout)
		{	goedHalfFout = FOUT;
			score = 0;
			correct = false;
			fout = true;
			feedback = Text.constants.feedbackTekst09();
			return;
		}
		else if (hasFeedback) {
			if (goedHalfFout == GOED) {
				score = puntenFeedback;
				correct = true;
				fout = false;
				
			} else if (goedHalfFout == DOOR) {
				score = puntenFeedback;
				correct = false;
				fout = false;
				
			} else if (goedHalfFout == HALF) {
				score = puntenFeedback;
				correct = false;
				fout = false;
				
			} else if (goedHalfFout == FOUT) { // zetGoedFout(FOUT,stapNr);
				score = puntenFeedback;
				correct = false;
				fout = true;
			}
		} 
		else if (isGelijkwaardig) {
			if (bevatFouteOplossing) // !isGelijkwaardig && isDeelOplossing &&
										// bevatFouteOplossing
			{	goedHalfFout = FOUT;
				score = 0;
				correct = false;
				fout = true;
				feedback = "";
				
			} else if (vorm) {
				if (isJuisteVorm) // isGelijkwaardig && vorm && isJuisteVorm
				{	goedHalfFout = GOED;
					score = puntenGelijkwaardig + puntenVorm;
					correct = true;
					fout = false;
					feedback = Text.constants.feedbackTekst16();
					//"Dit is een correcte vergelijking"
				} else // isGelijkwaardig && vorm && !isJuisteVorm
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig;
					correct = false;
					fout = false;
					feedback = Text.constants.feedbackTekst17();
					// "Deze vergelijking heeft (nog)niet de juiste vorm"
				}
			} else if (eindOplossingNodig) {
				if (bevatVoldoetNiet) // isGelijkwaardig && eindOplossingNodig
				{	goedHalfFout = HALF;
					score = 0;
					correct = false;
					fout = false;
					feedback = Text.constants.feedbackTekst02();
					// "Niet alle oplossingen voldoen aan de oorspronkelijke vergelijking. Verwijder de oplossingen die niet voldoen."
				} else if (isEindOplossing) {
					if (exact) {
						if (isEindOplossingExact) // isGelijkwaardig &&
													// eindOplossingNodig &&
													// isEindOplossing &&
													// exactNodig &&
													// isEindOplossingExact
						{
							score = puntenGelijkwaardig + puntenEindOplossing + puntenExact;
							if (gewensteEindOplossing.isOngelijkheid()) {
								goedHalfFout = GOED;
								correct = true;
								fout = false;
								feedback = Text.constants.feedbackTekst03();
								// "De ongelijkheid is correct opgelost"
							} else if (gewensteEindOplossing.isAfronding()) {
								goedHalfFout = GOED;
								correct = true;
								fout = false;
								feedback = Text.constants.feedbackTekst11();
								// "De oplossing is correct afgerond"
							} else {
								goedHalfFout = GOED;
								correct = true;
								fout = false;
								feedback = Text.constants.feedbackTekst04();
								// "De vergelijking is correct opgelost"
							}
						} 
						else // isGelijkwaardig && eindOplossingNodig &&
								// isEindOplossing && exactNodig &&
								// isEindOplossingExact
						{	goedHalfFout = DOOR;
							score = puntenGelijkwaardig + puntenEindOplossing;
							correct = false;
							fout = false;
							feedback = Text.constants.feedbackTekst10();
							//"Oplossing is goed, maar nog niet in de juiste vorm."
						}
					} else // isGelijkwaardig && eindOplossingNodig &&
							// isEindOplossing && ! exactNodig
					{	goedHalfFout = GOED;
						score = puntenGelijkwaardig + puntenEindOplossing;
						if (gewensteEindOplossing.isOngelijkheid()) {
							correct = true;
							fout = false;
							feedback = Text.constants.feedbackTekst03();
							// "De ongelijkheid is correct opgelost"
						} else if (gewensteEindOplossing.isAfronding()) {
							goedHalfFout = GOED;
							correct = true;
							fout = false;
							feedback = Text.constants.feedbackTekst11();
							// "De oplossing is correct afgerond"
						} else {
							goedHalfFout = GOED;
							correct = true;
							fout = false;
							feedback = Text.constants.feedbackTekst04();
							// "De vergelijking is correct opgelost"
						}
					}

				} 
				else // isGelijkwaardig && eindOplossingNodig &&
						// !isEindOplossing
				{
					if (moetNogAfgerond) // isGelijkwaardig &&
											// eindOplossingNodig &&
											// !isEindOplossing
					{	goedHalfFout = DOOR;
						score = 0;
						correct = false;
						fout = false;
						feedback = Text.constants.feedbackTekst05();
						// "Geef de gevraagde afronding"
					} else if (moetNogOngelijkheid) // isGelijkwaardig &&
													// eindOplossingNodig &&
													// !isEindOplossing
					{	goedHalfFout = DOOR;
						score = 0;
						correct = false;
						fout = false;
						feedback = Text.constants.feedbackTekst06();
						// "Geef nu de oplossing(en) van de ongelijkheid"
					} else {
						goedHalfFout = DOOR;
						score = puntenGelijkwaardig;
						correct = false;
						fout = false;
						feedback = "";
					}
				}
			} else // isGelijkwaardig && !vorm && !eindOplossingNodig
			{	goedHalfFout = GOED;
				score = puntenGelijkwaardig;
				// zetCorrectFoutStap(stapNr,true,false,false,"feedbackTekst16");//"Dit is een correcte vergelijking"

				// Nu kan het vak gebruikt worden als 'balans' voor het checken
				// van ware beweringen
				correct = true;
				fout = false;
				feedback = Text.constants.feedbackTekst16();
				// "Dit is een correcte vergelijking"
			}
		}

		else // niet isGelijkwaardig
		{
			if (isDeelOplossing) {
				if (bevatFouteOplossing) // !isGelijkwaardig && isDeelOplossing
											// && bevatFouteOplossing
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
					feedback = Text.constants.feedbackTekst01();
					// "Deze stap bevat correcte en niet correcte onderdelen. Verwijder of vervang de delen die niet correct zijn"
				} 
				else // !isGelijkwaardig && isDeelOplossing &&
						// !bevatFouteOplossing
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
					feedback = Text.constants.feedbackTekst07();
					// "Er ontbreken oplossingen. Vul aan."
				}
			} else // niet isDeelOplossing
			{	goedHalfFout = FOUT;
				score = 0;
				correct = false;
				fout = true;
				feedback = "";
			}
		}

	}

	
	public void checkFeedback(String antwoordVergString) {
		int aantalAnswerModels = answerModels.size();
		for (int h = 0; h < aantalAnswerModels; h++) {
			setAnswerModel(h);
			
			//System.out.println("checkAnswer: "+gewensteEindOplossing);
			//System.out.println("userAnswer: "+antwoordVergString);
			//System.out.println("correct: "+correct);
			//System.out.println("score: "+score);
			//System.out.println("goedHalfFout: "+goedHalfFout);
			//System.out.println(" feedback: "+ feedback);

			check(antwoordVergString);
			
			//System.out.println("checkAnswer: "+gewensteEindOplossing);
			//System.out.println("userAnswer: "+antwoordVergString);
			//System.out.println("correct: "+correct);
			//System.out.println("score: "+score);
			//System.out.println("goedHalfFout: "+goedHalfFout);
			//System.out.println(" feedback: "+ feedback);

			boolean pastGelijkwaardig = isGelijkwaardig && !bevatFouteOplossing;
			boolean pastVorm = isJuisteVorm;
			boolean pastEindAntwoord = isEindOplossing;
			boolean pastExact = isEindOplossingExact;

			if (!gelijkwaardigP)
				pastGelijkwaardig = true;
			if (!vormP)
				pastVorm = true;
			if (!eindOplossingNodigP)
				pastEindAntwoord = true;
			if (!exactP)
				pastExact = true;

			boolean answerModelFits = pastGelijkwaardig && pastVorm && pastEindAntwoord && pastExact;
			if (answerModelFits) {
				break;
			}
		}
	}
	
	public void check(String antwoordVergString) {
		if (gewensteEindOplossing == null)
			return;

		
		Algebra.setTestValues(eqTestValueMin, eqTestValueMax);
		ingevuld = false;
		
		VergelijkingMeerv antwoord = null;
		VergelijkingMeerv antwoordGeen = null;
		VergelijkingMeerv antwoordAlles = null;
		if (antwoordVergString.length() > 6 && antwoordVergString.substring(2, 6).equals("geen") || antwoordVergString.length() > 4
				&& antwoordVergString.substring(2, 4).equals("no")) {
			geenOplossing = true;
			// Vergelijking v = new Vergelijking(new BasisExpressie("x"), new
			// BasisExpressie(0.1234567));
			Vergelijking v = new Vergelijking(new BasisExpressie(gewensteEindOplossing.geefVergelijkingVar()), new BasisExpressie(0.1234567));
			Vergelijking[] vn = new Vergelijking[1];
			vn[0] = v;
			antwoordGeen = new VergelijkingMeerv(vn);
		}
		if (antwoordVergString.length() > 7 && antwoordVergString.substring(2, 7).equals("alles") || antwoordVergString.length() > 5
                && antwoordVergString.substring(2, 5).equals("all")) {
            geenOplossing = true;
            // Vergelijking v = new Vergelijking(new BasisExpressie("x"), new
            // BasisExpressie(0.1234567));
            Vergelijking v = new Vergelijking(new BasisExpressie(gewensteEindOplossing.geefVergelijkingVar()), new BasisExpressie(0.7654321));
            Vergelijking[] vn = new Vergelijking[1];
            vn[0] = v;
            antwoordAlles = new VergelijkingMeerv(vn);
        }
		VergelijkingMeerv antwoordIngevuld = FormuleParser.parseVergelijking(antwoordVergString);
		antwoord = antwoordIngevuld;
		if (antwoord == null) {
			antwoord = antwoordGeen;
			antwoordIngevuld = antwoordGeen;
		}
		if (antwoord == null) {
            antwoord = antwoordAlles;
            antwoordIngevuld = antwoordAlles;
        }

		
		if (substitutie != null && antwoordIngevuld != null)
		{	antwoord = antwoordIngevuld.substitueer(substitutie, "p");
		}
		
		//updateGebruikersSubstituties();
		if (gebruikersSubstituties != null && antwoord != null) {
			for (int i = 0; i < gebruikersSubstituties.length; i++) {
				antwoord = antwoord.substitueer(gebruikersSubstituties[i].geefExpRechts(), gebruikersSubstituties[i].geefExpLinks().geefVarNaam());
			}
		}
		
		String var = "x";
		if (gewensteEindOplossing != null)
			var = gewensteEindOplossing.geefVergelijkingVar();
		

		if (antwoordSubstituties != null && antwoord != null) {
			for (int i = 0; i < antwoordSubstituties.length; i++)
			{
				if(antwoord.isEindOplossing(var))
					antwoord = antwoord.substitueerEindOplossing(antwoordSubstituties[i].geefExpRechts(), antwoordSubstituties[i].geefExpLinks().geefVarNaam());
				else	
					antwoord = antwoord.substitueer(antwoordSubstituties[i].geefExpRechts(), antwoordSubstituties[i].geefExpLinks().geefVarNaam());
			}
		}

		if (antwoord != null) { // if(tips)
								// { ingevuld = true;
								// return;
								// }
			if (!geenOplossing)
				if (!bordjesMethode){
					//formuleVak.vulVak("$f" + antwoordIngevuld.toString() + "@");
					huidigeVergelijking = antwoord;
					//produceAction("balansvergelijking");
				}
			// System.out.println("$f" + antwoordIngevuld.toString() + "@");
			ingevuld = true;
//			String var = "x";
//			if (gewensteEindOplossing != null)
//				var = gewensteEindOplossing.geefVergelijkingVar();

			boolean isGelijkwaardigEind = antwoord.isOplossing(gewensteEindOplossing.geefEindOplossingen(var), var, gewensteEindOplossing.geefVergTekens());

			// Hiermee wordt, in geval er geen eindoplossing is, maar wel een
			// voorlopige tussenoplossing, aan het eind gevraagd de oplossing te
			// verwerpen
			if (gewensteEindOplossing.isOplossing(0.1234567))
				isGelijkwaardigEind = true;
			//

			isGelijkwaardig = isGelijkwaardigEind;
			if (gewensteTussenOplossing != null && !isGelijkwaardig)
				isGelijkwaardig = antwoord.isOplossing(gewensteTussenOplossing.geefEindOplossingen(var), var, gewensteTussenOplossing.geefVergTekens());

			isEindOplossing = isGelijkwaardigEind && antwoord.isEindOplossing(var);

			isEindOplossingExact = isGelijkwaardigEind
					&& antwoord.isEindOplossingExact(gewensteEindOplossing.geefEindOplossingen(var), var, gewensteEindOplossing.geefVergTekens());

			isDeelOplossing = antwoord.isDeelOplossing(gewensteEindOplossing.geefEindOplossingen(var), var, gewensteEindOplossing.geefVergTekens());
			if (gewensteTussenOplossing != null && !isDeelOplossing)
				isDeelOplossing = antwoord.isDeelOplossing(gewensteTussenOplossing.geefEindOplossingen(var), var, gewensteTussenOplossing.geefVergTekens());

			boolean bevatFouteOplossingEind = antwoord.bevatFouteOplossing(gewensteEindOplossing, var, gewensteEindOplossing.geefVergTekens());
			bevatFouteOplossing = bevatFouteOplossingEind;
			if (gewensteTussenOplossing != null && bevatFouteOplossing)
				bevatFouteOplossing = antwoord.bevatFouteOplossing(gewensteTussenOplossing, var, gewensteTussenOplossing.geefVergTekens());

			bevatVoldoetNiet = bevatFouteOplossingEind && !bevatFouteOplossing && isEindOplossing;
			// System.out.println(""+bevatVoldoetNiet);

			moetNogAfgerond = isGelijkwaardig && !isGelijkwaardigEind && antwoord.isEindOplossing(var)
					&& gewensteEindOplossing.toString().indexOf("\u2248") > -1;

			moetNogOngelijkheid = isGelijkwaardig && !isGelijkwaardigEind && antwoord.isEindOplossing(var) && gewensteEindOplossing.isOngelijkheid();

			isJuisteVorm = false;
			for (int i = 0; i < juisteVormen.length; i++) {
				isJuisteVorm = isJuisteVorm || Algebra.gelijkGevormd(antwoord, juisteVormen[i]);
				if (isJuisteVorm)
					break;
			}
			
			//juiste-stap-check wordt nu pas in formule-editor with steps gedaan.
//			if (linOefenVersie) {
//			System.out.println("in check linOefenversie");
//				VergelijkingMeerv vorigAntwoord = FormuleParser.parseVergelijking(vorigAntwoordString);
//				isGelijkwaardig = isJuistUitgevoerdeStap(antwoord, vorigAntwoord, operator, exp);
//				if (!isGelijkwaardig) {
//					isEindOplossingExact = false;
//					isEindOplossing = false;
//					isDeelOplossing = false;
//				}
//			}
		} else {
			syntaxFout = true;
			isGelijkwaardig = false;
			isEindOplossing = false;
			isEindOplossingExact = false;
			isDeelOplossing = false;
			bevatFouteOplossing = false;
			bevatVoldoetNiet = false;
			if (antwoordVergString.indexOf("|") > -1) { // setFeedback("Gebruik geen absoluut strepen ( bv: |x-3| )");
				feedback = Text.constants.feedbackTekst08();
			} else if (antwoordVergString.length() > 3) { // setFeedback("De notatie van de vergelijking of oplossingen is niet juist");
				//if (mode == 2 || mode == 3)
					ingevuld = true;
					feedback = Text.constants.feedbackTekst09();
			}
		}
		Algebra.setDefaultTestValues();
	}

	public String vertaalIdeasExpressie(String s)
	{
		//System.out.println(s);
		//s = StringUtils.replaceStr(s,"?",Text.rb.getString("ofLabel"));
		s = s.replaceAll("\u2228",Text.constants.ofLabel());
        if(s.equals("false"))s = "x=geen";
        return s;
	}
	
	public String vertaalNaarIdeasExpressie(String s)
	{
		s = s.replaceAll(Text.constants.ofLabel(),"\u2228");
		s = s.replaceAll(" ", "");
		if(s.equals("geenoplossingen"))s = "false";
        return s;
	}
	
	public String translateRule(String s)
	{
		if(s != null && changedTexts.containsKey(s))return((String)changedTexts.get(s));
		else if(s != null) return translateRuleToStandard(s);
		else return("null");
	}
	
	public static String translateRuleToStandard(String s) {
		if (s.equals("ready"))
			return "correct opgelost";
		else if (s.equals("similar"))
			return "geen verschil met de vorige stap";
		else if (s.equals("correct"))
			return "juiste stap";
		else if (s.equals("expected"))
			return "correct (standaard strategie)";
		else if (s.equals("detour"))
			return "dit lijkt een omweg";
		else if (s.equals("buggy"))
			return "bekende fout: ";
		else if (s.equals("notequiv"))
			return "onbekende fout";
		else try {
			s = Text.rb.getString(s);
		}
		catch(MissingResourceException e) {
		}
		return s;
	}
	/*
	public String formatRuleText(RuleIF rule, String text) {
		String argument = rule.getArgument();
		String argument1 = null;
		String argument2 = null;

		if (argument != null) {
			int indexAnd = argument.indexOf("\u2227");
			if (indexAnd > -1){
				argument1 = argument.substring(0,indexAnd);
				argument2 = argument.substring(indexAnd);
				
				int index = argument1.indexOf("=");
				if (index > -1)
					argument1 = argument1.substring(index + 1);
				argument1 = "$f" + argument1 + "@";
				
				index = argument2.indexOf("=");
				if (index > -1)
					argument2 = argument2.substring(index + 1);
				argument2 = "$f" + argument2 + "@";
			}
		
			else{
				int index = argument.indexOf("=");
				if (index > -1)
					argument = argument.substring(index + 1);
				argument = "$f" + argument + "@";
			}
		}
		if (text.indexOf("{1}") > -1)
			text = text.replaceAll("{1}", argument1);
		if (text.indexOf("{2}") > -1)
			text = text.replaceAll("{2}", argument2);
		if (text.indexOf("{?}") > -1 && argument1!=null)
			text = text.replaceAll("{?}", argument1);
		else if (text.indexOf("{?}") > -1)
			text = text.replaceAll("{?}", argument);
		return text;
	}
	*/
}
