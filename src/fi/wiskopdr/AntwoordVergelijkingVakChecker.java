package fi.wiskopdr;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

//import fi.beans.ideas.IdeasIF;
//import fi.beans.ideas.RuleIF;
//import fi.beans.stringutils.StringUtils;
//import fi.wiskopdr.formuleobjects.*;
//import fi.wiskopdr.tekstobjects.TekstArea;
import fi.wiskopdr.expressies.*;
//import fi.wiskopdr.WiskOpdr;

//import fi.beans.wiskopdrbeans.InteractieEditPanel;
//import fi.beans.wiskopdrbeans.InteractiePanel;
//import fi.beans.wnwidgets.NWButtonUI;
import nl.uu.fi.dwo.mobile.DWOplayer;


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
	
	

	private ArrayList<HashMap<String,Object>> answerModels;
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

	private HashMap<String,Object> changedTexts = new HashMap<String,Object>();

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
	
	
	public AntwoordVergelijkingVakChecker(HashMap<String,Object> avvCheckerModel, String[] randomVars, HashMap<String,Object> randomValues )
	{	
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
		ArrayList<HashMap<String,Object>> answerModels = null;
		boolean hasFeedback = false;
		String vormString = "$f@";
		boolean tips = false;
		String strategieDomein = "";
		//int feedbackModus = 0;
		ArrayList<String> antwoordSubStrings = null;
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
		HashMap<String,Object> changedTexts = new HashMap<String,Object>();
		HashMap<String,Object> ideasInstellingen = null;
		double eqTestValueMin = 0;
		double eqTestValueMax = 5;
		int scoreMax = 0;
		boolean uitw = false;
		boolean casAntw = false;
		boolean boxMetRand = true;
		
		if (avvCheckerModel.containsKey("antwoordString"))
			antwoordString = (String) avvCheckerModel.get("antwoordString");
		if (avvCheckerModel.containsKey("startString"))
			startString = (String) avvCheckerModel.get("startString");
		if (avvCheckerModel.containsKey("vorm"))
			vorm = ((Boolean) avvCheckerModel.get("vorm")).booleanValue();
		if (avvCheckerModel.containsKey("exact"))
			exact = ((Boolean) avvCheckerModel.get("exact")).booleanValue();
		if (avvCheckerModel.containsKey("stappen"))
			stappen = ((Boolean) avvCheckerModel.get("stappen")).booleanValue();
		if (avvCheckerModel.containsKey("puntenGelijkwaardig"))
			puntenGelijkwaardig = ((Integer) avvCheckerModel.get("puntenGelijkwaardig")).intValue();
		if (avvCheckerModel.containsKey("puntenExact"))
			puntenExact = ((Integer) avvCheckerModel.get("puntenExact")).intValue();
		if (avvCheckerModel.containsKey("puntenVorm"))
			puntenVorm = ((Integer) avvCheckerModel.get("puntenVorm")).intValue();
		if (avvCheckerModel.containsKey("puntenEindOplossing"))
			puntenEindOplossing = ((Integer) avvCheckerModel.get("puntenEindOplossing")).intValue();
		if (avvCheckerModel.containsKey("eindOplossingNodig"))
			eindOplossingNodig = ((Boolean) avvCheckerModel.get("eindOplossingNodig")).booleanValue();
		if (avvCheckerModel.containsKey("bewerkingKnoppen"))
			bewerkingKnoppen = ((Boolean) avvCheckerModel.get("bewerkingKnoppen")).booleanValue();
		if (avvCheckerModel.containsKey("bewerkingKnoppenExtra"))
			bewerkingKnoppenExtra = ((Boolean) avvCheckerModel.get("bewerkingKnoppenExtra")).booleanValue();
		if (avvCheckerModel.containsKey("abcKnop"))
			abcKnop = ((Boolean) avvCheckerModel.get("abcKnop")).booleanValue();
		if (avvCheckerModel.containsKey("subKnop"))
			subKnop = ((Boolean) avvCheckerModel.get("subKnop")).booleanValue();
		if (avvCheckerModel.containsKey("subKnopExtra"))
			subKnopExtra = ((Boolean) avvCheckerModel.get("subKnopExtra")).booleanValue();
		if (avvCheckerModel.containsKey("answerModels"))
			answerModels = (ArrayList<HashMap<String,Object>>) avvCheckerModel.get("answerModels");
		if (avvCheckerModel.containsKey("hasFeedback"))
			hasFeedback = ((Boolean) avvCheckerModel.get("hasFeedback")).booleanValue();
		if (avvCheckerModel.containsKey("vormString"))
			vormString = (String) avvCheckerModel.get("vormString");
		if (avvCheckerModel.containsKey("tips"))
			tips = ((Boolean) avvCheckerModel.get("tips")).booleanValue();
		if (tips) {
			if (avvCheckerModel.containsKey("ideasInstellingen"))
				ideasInstellingen = (HashMap<String,Object>) avvCheckerModel.get("ideasInstellingen");
			else { // voor backward comp.
				if (avvCheckerModel.containsKey("tipOpBalk"))
					tipOpBalk = ((Boolean) avvCheckerModel.get("tipOpBalk")).booleanValue();
				if (avvCheckerModel.containsKey("hulpOpBalk"))
					hulpOpBalk = ((Boolean) avvCheckerModel.get("hulpOpBalk")).booleanValue();
				if (avvCheckerModel.containsKey("stapOpBalk"))
					stapOpBalk = ((Boolean) avvCheckerModel.get("stapOpBalk")).booleanValue();
				if (avvCheckerModel.containsKey("solveOpBalk"))
					solveOpBalk = ((Boolean) avvCheckerModel.get("solveOpBalk")).booleanValue();
				if (avvCheckerModel.containsKey("meerTips"))
					meerTips = ((Boolean) avvCheckerModel.get("meerTips")).booleanValue();
				if (avvCheckerModel.containsKey("tipBijFout"))
					tipBijFout = ((Boolean) avvCheckerModel.get("tipBijFout")).booleanValue();
				if (avvCheckerModel.containsKey("feedbackBijFout"))
					feedbackBijFout = ((Boolean) avvCheckerModel.get("feedbackBijFout")).booleanValue();
				if (avvCheckerModel.containsKey("hulpBijTip"))
					hulpBijTip = ((Boolean) avvCheckerModel.get("hulpBijTip")).booleanValue();
				if (avvCheckerModel.containsKey("changedTexts"))
					changedTexts = (HashMap<String,Object>) avvCheckerModel.get("changedTexts");
				//Hashtable sod = AntwoordVergelijkingVakEditPanel.strategieOudNieuw;
				//if (sod.containsKey(strategieDomein))
				//	strategieDomein = (String) sod.get(strategieDomein);
				if (avvCheckerModel.containsKey("strategieDomein"))
					strategieDomein = (String) avvCheckerModel.get("strategieDomein");
			}
		}

//		if (avvCheckerModel.containsKey("feedbackModus"))
//			feedbackModus = ((Integer) avvCheckerModel.get("feedbackModus")).intValue();
		if (avvCheckerModel.containsKey("antwoordSubStrings"))
			antwoordSubStrings = (ArrayList<String>) avvCheckerModel.get("antwoordSubStrings");
		if (avvCheckerModel.containsKey("pijl"))
			pijl = ((Boolean) avvCheckerModel.get("pijl")).booleanValue();
		if (avvCheckerModel.containsKey("linStrategieVersie"))
			linStrategieVersie = ((Boolean) avvCheckerModel.get("linStrategieVersie")).booleanValue();
		if (avvCheckerModel.containsKey("bordjesMethode"))
			bordjesMethode = ((Boolean) avvCheckerModel.get("bordjesMethode")).booleanValue();
		if (avvCheckerModel.containsKey("linOefenVersie"))
			linOefenVersie = ((Boolean) avvCheckerModel.get("linOefenVersie")).booleanValue();
		
		if (avvCheckerModel.containsKey("eqTestValueMin"))
			eqTestValueMin = ((Double) avvCheckerModel.get("eqTestValueMin")).doubleValue();
		if (avvCheckerModel.containsKey("eqTestValueMax"))
			eqTestValueMax = ((Double) avvCheckerModel.get("eqTestValueMax")).doubleValue();
		if (avvCheckerModel.containsKey("scoreMax"))
			scoreMax = ((Integer) avvCheckerModel.get("scoreMax")).intValue();
		if (avvCheckerModel.containsKey("uitw"))
			uitw = ((Boolean) avvCheckerModel.get("uitw")).booleanValue();
		if (avvCheckerModel.containsKey("casAntw"))
			casAntw = ((Boolean) avvCheckerModel.get("casAntw")).booleanValue();
		if (avvCheckerModel.containsKey("boxMetRand"))
			boxMetRand = ((Boolean) avvCheckerModel.get("boxMetRand")).booleanValue();
		
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

		
		this.answerModels = answerModels;
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
	
	public HashMap<String,Object> checkAnswer(String answer, String answerPrevious)
	{	return checkAnswer(answer, answerPrevious, null,null);
	}
	
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
		
		
		return checkResults;
	}
	
	
	
	
	
	
	
	
	public void setAnswerModel(int nr)
	{	HashMap<String,Object> h = answerModels.get(nr);
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
		
		
		if(h!=null) 
		{	if(h.containsKey("antwoordString")) antwoordString = (String)h.get("antwoordString");
			if(h.containsKey("gelijkwaardig")) gelijkwaardig = ((Boolean)h.get("gelijkwaardig")).booleanValue();
			if(h.containsKey("vorm")) vorm = ((Boolean)h.get("vorm")).booleanValue();
			if(h.containsKey("eindOplossingNodig")) eindOplossingNodig = ((Boolean)h.get("eindOplossingNodig")).booleanValue();
			if(h.containsKey("exact")) exact = ((Boolean)h.get("exact")).booleanValue();
			if(h.containsKey("puntenFeedback")) puntenFeedback = ((Integer)h.get("puntenFeedback")).intValue();
			if(h.containsKey("feedback")) feedback = (String)h.get("feedback");
			if(h.containsKey("vormString")) vormString = (String)h.get("vormString");
			if(h.containsKey("goedHalfFout")) goedHalfFout = ((Integer)h.get("goedHalfFout")).intValue();
			
		}	
		exactP = exact;
		vormP = vorm;
		eindOplossingNodigP = eindOplossingNodig;
		gelijkwaardigP = gelijkwaardig;
		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;
		
		System.out.println("antwoordString: "+antwoordString);
        try         
        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVarNamen,randomVarWaarden);
        }
        catch(Exception e)
        {   antwoordString = "$f???@";
        }
        System.out.println("antwoordString na randomizing: "+antwoordString);
        
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
        zetJuisteAntwoord(antwoordString);
		zetJuisteVorm(vormString);
       
        this.feedback = feedback;
       
       
	}
	
	
	
	public void setIdeas(HashMap<String,Object> h)
	{
		if(h.containsKey("strategieDomein")) strategieDomein = (String)h.get("strategieDomein");
		if(h.containsKey("meerTips")) meerTips = ((Boolean)h.get("meerTips")).booleanValue();
        if(h.containsKey("tipBijFout")) tipBijFout = ((Boolean)h.get("tipBijFout")).booleanValue();
        if(h.containsKey("feedbackBijFout")) feedbackBijFout = ((Boolean)h.get("feedbackBijFout")).booleanValue();
        if(h.containsKey("hulpBijTip")) hulpBijTip = ((Boolean)h.get("hulpBijTip")).booleanValue();
        if (h.containsKey("diagnose"))
			diagnose = ((Boolean) h.get("diagnose")).booleanValue();
		if (h.containsKey("aftrekTip"))
			aftrekTip = ((Integer) h.get("aftrekTip")).intValue();
		if (h.containsKey("aftrekHulp"))
			aftrekHulp = ((Integer) h.get("aftrekHulp")).intValue();
		if (h.containsKey("aftrekStap"))
			aftrekStap = ((Integer) h.get("aftrekStap")).intValue();
		if (h.containsKey("aftrekSolve"))
			aftrekSolve = ((Integer) h.get("aftrekSolve")).intValue();
        
       
		
	}
	
	
	
	public void zetJuisteAntwoord(String s)
	{	
		if (s.length() > 3 && s.substring(0, 4).equals("CAS{")) {
			casCheck = true;
			return;
		}
		// System.out.println(s);
		//if (DWOplayer.language.getLanguage().equals("en"))
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
			feedback = DWOplayer.rb.getString("feedbackTekst09");
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
			feedback = DWOplayer.rb.getString("feedbackTekst09");
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
					feedback = DWOplayer.rb.getString("feedbackTekst16");
					//"Dit is een correcte vergelijking"
				} else // isGelijkwaardig && vorm && !isJuisteVorm
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig;
					correct = false;
					fout = false;
					feedback = DWOplayer.rb.getString("feedbackTekst17");
					// "Deze vergelijking heeft (nog)niet de juiste vorm"
				}
			} else if (eindOplossingNodig) {
				if (bevatVoldoetNiet) // isGelijkwaardig && eindOplossingNodig
				{	goedHalfFout = HALF;
					score = 0;
					correct = false;
					fout = false;
					feedback = DWOplayer.rb.getString("feedbackTekst02");
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
								feedback = DWOplayer.rb.getString("feedbackTekst03");
								// "De ongelijkheid is correct opgelost"
							} else if (gewensteEindOplossing.isAfronding()) {
								goedHalfFout = GOED;
								correct = true;
								fout = false;
								feedback = DWOplayer.rb.getString("feedbackTekst11");
								// "De oplossing is correct afgerond"
							} else {
								goedHalfFout = GOED;
								correct = true;
								fout = false;
								feedback = DWOplayer.rb.getString("feedbackTekst04");
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
							feedback = DWOplayer.rb.getString("feedbackTekst10");
							//"Oplossing is goed, maar nog niet in de juiste vorm."
						}
					} else // isGelijkwaardig && eindOplossingNodig &&
							// isEindOplossing && ! exactNodig
					{	goedHalfFout = GOED;
						score = puntenGelijkwaardig + puntenEindOplossing;
						if (gewensteEindOplossing.isOngelijkheid()) {
							correct = true;
							fout = false;
							feedback = DWOplayer.rb.getString("feedbackTekst03");
							// "De ongelijkheid is correct opgelost"
						} else if (gewensteEindOplossing.isAfronding()) {
							goedHalfFout = GOED;
							correct = true;
							fout = false;
							feedback = DWOplayer.rb.getString("feedbackTekst11");
							// "De oplossing is correct afgerond"
						} else {
							goedHalfFout = GOED;
							correct = true;
							fout = false;
							feedback = DWOplayer.rb.getString("feedbackTekst04");
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
						feedback = DWOplayer.rb.getString("feedbackTekst05");
						// "Geef de gevraagde afronding"
					} else if (moetNogOngelijkheid) // isGelijkwaardig &&
													// eindOplossingNodig &&
													// !isEindOplossing
					{	goedHalfFout = DOOR;
						score = 0;
						correct = false;
						fout = false;
						feedback = DWOplayer.rb.getString("feedbackTekst06");
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
				feedback = DWOplayer.rb.getString("feedbackTekst16");
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
					feedback = DWOplayer.rb.getString("feedbackTekst01");
					// "Deze stap bevat correcte en niet correcte onderdelen. Verwijder of vervang de delen die niet correct zijn"
				} 
				else // !isGelijkwaardig && isDeelOplossing &&
						// !bevatFouteOplossing
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
					feedback = DWOplayer.rb.getString("feedbackTekst07");
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
			antwoord = antwoordIngevuld.substitueer(substitutie, "p");

		//updateGebruikersSubstituties();
		if (gebruikersSubstituties != null && antwoord != null) {
			for (int i = 0; i < gebruikersSubstituties.length; i++) {
				antwoord = antwoord.substitueer(gebruikersSubstituties[i].geefExpRechts(), gebruikersSubstituties[i].geefExpLinks().geefVarNaam());
			}
		}

		if (antwoordSubstituties != null && antwoord != null) {
			for (int i = 0; i < antwoordSubstituties.length; i++) {
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
			String var = "x";
			if (gewensteEindOplossing != null)
				var = gewensteEindOplossing.geefVergelijkingVar();

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
			
			if (linOefenVersie) {
				isGelijkwaardig = isJuistUitgevoerdeStap();
				if (!isGelijkwaardig) {
					isEindOplossingExact = false;
					isEindOplossing = false;
					isDeelOplossing = false;
				}
			}
		} else {
			syntaxFout = true;
			isGelijkwaardig = false;
			isEindOplossing = false;
			isEindOplossingExact = false;
			isDeelOplossing = false;
			bevatFouteOplossing = false;
			bevatVoldoetNiet = false;
			if (antwoordVergString.indexOf("|") > -1) { // setFeedback("Gebruik geen absoluut strepen ( bv: |x-3| )");
				feedback = DWOplayer.rb.getString("feedbackTekst08");
			} else if (antwoordVergString.length() > 3) { // setFeedback("De notatie van de vergelijking of oplossingen is niet juist");
				//if (mode == 2 || mode == 3)
					ingevuld = true;
					feedback = DWOplayer.rb.getString("feedbackTekst09");
			}
		}
		Algebra.setDefaultTestValues();
	}
	
	public boolean isJuistUitgevoerdeStap() {
		/*if (stapNr == 0)
			return isGelijkwaardig;
		String op = pijlVakken[stapNr - 1].geefOperator();
		pijlVakken[stapNr - 1].formuleVak.setEditable(false);
		Expressie en = pijlVakken[stapNr - 1].formuleVak.geefExpressie();
		if (op.equals("implicatie") || en == null)
			return isGelijkwaardig;
		VergelijkingMeerv verg = formuleVakken[stapNr - 1].geefVergelijking();

		VergelijkingMeerv vergNieuw = null;

		int aantalDelen = verg.geefAantal();
		for (int i = 0; i < aantalDelen && aantalDelen > 0; i++) {
			if (formuleVakken[stapNr - 1].partEquationSelected(i)) {
				vergNieuw = verg.bewerkVergelijking(op, en, i);
				break;
			}
		}
		if (vergNieuw == null)
			vergNieuw = verg.bewerkVergelijking(op, en);

		VergelijkingMeerv vergAntwoord = formuleVakken[stapNr].geefVergelijking();
		if (op.equals("sub"))
			vergAntwoord = vergAntwoord.substitueer(substitutie, "p");
		return vergNieuw.isGelijkMet(vergAntwoord);*/
		return isGelijkwaardig;
	}

	public String vertaalIdeasExpressie(String s)
	{
		//System.out.println(s);
		//s = StringUtils.replaceStr(s,"?",DWOplayer.rb.getString("ofLabel"));
		s = s.replaceAll("\u2228",DWOplayer.rb.getString("ofLabel"));
        if(s.equals("false"))s = "x=geen";
        return s;
	}
	
	public String vertaalNaarIdeasExpressie(String s)
	{
		s = s.replaceAll(DWOplayer.rb.getString("ofLabel"),"\u2228");
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
			s = DWOplayer.rb.getString(s);
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
