package fi.wiskopdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

//import fi.beans.ideas.IdeasIF;
//import fi.beans.ideas.RuleIF;
//import fi.beans.stringutils.StringUtils;
//import fi.wiskopdr.formuleobjects.*;
//import fi.wiskopdr.tekstobjects.TekstArea;
import fi.wiskopdr.expressies.*;
import fi.wiskopdr.text.Text;
//import fi.wiskopdr.WiskOpdr;



public class AntwoordFormuleVakChecker implements AntwoordVakChecker
{
	private boolean	herleiding;
	private boolean	exact;
	
	private boolean isGelijkwaardig = false;
	private boolean isHerleid = false;
	private int soortHerleiding = 0;
	private boolean isExact = false;
	private int puntenGelijkwaardig = 10;
	private int puntenHerleiding = 0;
	private int puntenExact = 0;
	
	private int score;
	private int scoreMax;
	private boolean correct;
	private boolean fout;
	private boolean stapOk;
	
	private Expressie[] juisteAntwoorden;
	private double[] absPrecisions;
	private Expressie[] juisteVormen;
	
	private Expressie substitutie;
	private Vergelijking[] gebruikersSubstituties;
	
	//private TekstArea feedbackTekst;
    
    private String gekozenAntwoordString, gekozenStartString, formuleVakString;
    
    private ObjectList answerModels;
    private String[] randomVarNamen = null;
	private HashMap<String,Object> randomVarWaarden = null;
	
	private String feedback;
	private boolean exactP;
	private boolean herleidingP;
	private boolean gelijkwaardigP;
	private boolean hasFeedback;
	private int puntenFeedback;
	private int goedHalfFout;
	
	private boolean syntaxFout;
	private boolean leeg;
	
	private double eqTestValueMin = 0;
	private double eqTestValueMax = 5;
	
	private int aantalDecRm = 10;
	
	private boolean casCheck = false;
	private String casString = "";
	private boolean casResult = false;
	
	//ideas instellingen
	
	private boolean tips; //ideas aan
	private String strategieDomein;
	private int foutenTeller;
	private int feedbackModus;
	private boolean tipGebruikt;
	private boolean hulpGebruikt;
	private int aftrekTipHulp;
	
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
         
    private HashMap changedTexts = new HashMap<String,Object>();
	private final static Logger logger = Logger.getLogger("AntwoordFormuleVakChecker");
    
	
	public AntwoordFormuleVakChecker(HashMap<String,Object> map, String[] randomVars, HashMap<String,Object> randomValues )
	{	
		randomVarNamen = randomVars;
		randomVarWaarden = randomValues;
		ObjectMap afvCheckerModel = JSONUtilities.wrapMap(map);
		//this.antwoordFormuleVak = antwoordFormuleVak;
		String antwoordString = "$f@";
		boolean herleiding = false;
		boolean exact = false;
		int soortHerleiding = 0;
		int puntenGelijkwaardig = 10;
		int puntenHerleiding = 0;
		int puntenExact = 0;
		ObjectList answerModels = null;
		boolean hasFeedback = false;
		String vormString = "$f@";
		double eqTestValueMin = 0;
		double eqTestValueMax = 5;
		int scoreMax = 0;
		boolean tips = false;
		String strategieDomein = "";
		boolean meerTips = false;
        boolean tipBijFout = false;
        boolean feedbackBijFout = false;
        boolean hulpBijTip = false;
        HashMap<String,Object> changedTexts = new HashMap<String,Object>();
        ObjectMap ideasInstellingen = null;
        		
		if(afvCheckerModel.containsKey("antwoordString")) antwoordString = afvCheckerModel.getString("antwoordString");
		if(afvCheckerModel.containsKey("herleiding")) herleiding = afvCheckerModel.getBoolean("herleiding");
		if(afvCheckerModel.containsKey("exact")) exact = afvCheckerModel.getBoolean("exact");
		if(afvCheckerModel.containsKey("soortHerleiding")) soortHerleiding = afvCheckerModel.getInt("soortHerleiding");
		if(afvCheckerModel.containsKey("puntenGelijkwaardig")) puntenGelijkwaardig = afvCheckerModel.getInt("puntenGelijkwaardig");
		if(afvCheckerModel.containsKey("puntenHerleiding")) puntenHerleiding = afvCheckerModel.getInt("puntenHerleiding");
		if(afvCheckerModel.containsKey("puntenExact")) puntenExact = afvCheckerModel.getInt("puntenExact");
		if(afvCheckerModel.containsKey("answerModels")) answerModels = afvCheckerModel.getObjectList("answerModels");
		if(afvCheckerModel.containsKey("hasFeedback")) hasFeedback = afvCheckerModel.getBoolean("hasFeedback");
		if(afvCheckerModel.containsKey("vormString")) vormString = afvCheckerModel.getString("vormString");
		if(afvCheckerModel.containsKey("eqTestValueMin")) eqTestValueMin = afvCheckerModel.getDouble("eqTestValueMin");
		if(afvCheckerModel.containsKey("eqTestValueMax")) eqTestValueMax = afvCheckerModel.getDouble("eqTestValueMax");
		if(afvCheckerModel.containsKey("aantalDecRm")) aantalDecRm = afvCheckerModel.getInt("aantalDecRm");
		if(afvCheckerModel.containsKey("scoreMax")) scoreMax = afvCheckerModel.getInt("scoreMax");
		if(afvCheckerModel.containsKey("tips")) tips = afvCheckerModel.getBoolean("tips");
		if(tips){
			if(afvCheckerModel.containsKey("ideasInstellingen")) ideasInstellingen = afvCheckerModel.getObjectMap("ideasInstellingen");
	    }
		
		this.herleiding = herleiding;
		this.soortHerleiding = soortHerleiding;
		this.exact = exact;
		this.puntenGelijkwaardig = puntenGelijkwaardig;
		this.puntenHerleiding = puntenHerleiding;
		this.puntenExact = puntenExact;
		
        try         
        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVars,randomValues);
        }
        catch(Exception e)
        {   antwoordString = "$f???@";
            
        }
        try         
        {   vormString = FormuleParser.randomizeString(vormString,randomVars,randomValues);
        }
        catch(Exception e)
        {   vormString = "$f???@";
            
        }
        zetJuisteAntwoord(antwoordString);
        zetJuisteVorm(vormString);
         
        
        this.gekozenAntwoordString = antwoordString;
        this.answerModels = answerModels;
        this.hasFeedback = hasFeedback;
        
        this.eqTestValueMin = eqTestValueMin;
        this.eqTestValueMax = eqTestValueMax;
        this.scoreMax = scoreMax;
        this.tips = tips;
        
                
		if(tips && ideasInstellingen!=null)
		{	setIdeas(ideasInstellingen);
		}
       
	     
	}
	
//	public HashMap checkAnswer(String answer, String answerPrevious)
//	{	return checkAnswer(answer, answerPrevious, null,null);
//	}
	
	public HashMap checkAnswer(String answer)
	{	return checkAnswer(answer,null,null,null);
	}
	
	public HashMap checkAnswer(String answer, String answerPrevious, Expressie substitutie, Vergelijking[] gebruikersSubstituties)
	{	this.isGelijkwaardig = false;
		this.isHerleid = false;
		this.isExact = false;
		this.feedback = "";
		this.score = 0;
		this.correct = false;
		this.fout = false;
		this.goedHalfFout = AntwoordVakChecker.GEEN;
		this.syntaxFout = false;
		
		this.substitutie = substitutie;
		this.gebruikersSubstituties = gebruikersSubstituties;
		
		if(answerPrevious==null)
		{	Algebra.setTestValues(eqTestValueMin, eqTestValueMax);
			check(answer);
			Algebra.setDefaultTestValues();
			evaluate();
		}
		else if(tips && diagnose)
		{	//checkEvaluateIdeas(answer, answerPrevious);
		}
		else
		{
			Algebra.setTestValues(eqTestValueMin, eqTestValueMax);
			check(answer);
			Algebra.setDefaultTestValues();
			evaluate();
		}
		
		HashMap checkResults = new HashMap();
		checkResults.put("isGelijkwaardig", new Boolean(isGelijkwaardig));
		checkResults.put("isHerleid", new Boolean(isHerleid));
		checkResults.put("isExact", new Boolean(isExact));
		
		if(goedHalfFout != GEEN) checkResults.put("correct", new Boolean(correct));
		checkResults.put("fout", new Boolean(fout));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
		checkResults.put("syntaxFout", new Boolean(syntaxFout));
		
		
		return checkResults;
	}
		
	public void setAnswerModel(int nr)
	{	ObjectMap h = answerModels.getObjectMap(nr);
		if(h==null) return;
	
		String antwoordString = "$f@";
		boolean gelijkwaardig = true;
		boolean herleiding = false;
		boolean exact = false;
		int soortHerleiding = 0;
		int puntenGelijkwaardig = 10;
		int puntenHerleiding = 0;
		int puntenExact = 0;
		int puntenFeedback = 0;
		String feedback = "";
		String vormString = "$f@";
		int goedHalfFout = 0;
		
		if(h!=null) 
		{	if(h.containsKey("antwoordString")) antwoordString = h.getString("antwoordString");
			if(h.containsKey("gelijkwaardig")) gelijkwaardig = h.getBoolean("gelijkwaardig");
			if(h.containsKey("herleiding")) herleiding = h.getBoolean("herleiding");
			if(h.containsKey("exact")) exact = h.getBoolean("exact");
			if(h.containsKey("soortHerleiding")) soortHerleiding = h.getInt("soortHerleiding");
			if(h.containsKey("puntenGelijkwaardig")) puntenGelijkwaardig = h.getInt("puntenGelijkwaardig");
			if(h.containsKey("puntenHerleiding")) puntenHerleiding = h.getInt("puntenHerleiding");
			if(h.containsKey("puntenExact")) puntenExact = h.getInt("puntenExact");
			if(h.containsKey("puntenFeedback")) puntenFeedback = h.getInt("puntenFeedback");
			if(h.containsKey("feedback")) feedback = h.getString("feedback");
			if(h.containsKey("vormString")) vormString = h.getString("vormString");
			if(h.containsKey("goedHalfFout")) goedHalfFout = h.getInt("goedHalfFout");
		}
		
		//correctie voor afstemming op antwoordVergelijkingVakChecker (met extra optie: "door")
		goedHalfFout = goedHalfFout==2 ? goedHalfFout+1 : goedHalfFout;
				
		exactP = exact;
		herleidingP = herleiding;
		gelijkwaardigP = gelijkwaardig;
		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;

        try         
        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVarNamen,randomVarWaarden);
        }
        catch(Exception e)
        {   antwoordString = "$f???@";
        }
        
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
        zetJuisteAntwoord(antwoordString, false);
        zetJuisteVorm(vormString);
       
        this.gekozenAntwoordString = antwoordString;
        this.feedback = feedback.trim();

	}
	
	
	
	public void setIdeas(ObjectMap ideasInstellingen)
	{
		if(ideasInstellingen.containsKey("strategieDomein")) strategieDomein = ideasInstellingen.getString("strategieDomein");
		if(ideasInstellingen.containsKey("meerTips")) meerTips = ideasInstellingen.getBoolean("meerTips");
        if(ideasInstellingen.containsKey("tipBijFout")) tipBijFout = ideasInstellingen.getBoolean("tipBijFout");
        if(ideasInstellingen.containsKey("feedbackBijFout")) feedbackBijFout = ideasInstellingen.getBoolean("feedbackBijFout");
        if(ideasInstellingen.containsKey("hulpBijTip")) hulpBijTip = ideasInstellingen.getBoolean("hulpBijTip");
        if (ideasInstellingen.containsKey("diagnose"))
			diagnose = ideasInstellingen.getBoolean("diagnose");
		if (ideasInstellingen.containsKey("aftrekTip"))
			aftrekTip = ideasInstellingen.getInt("aftrekTip");
		if (ideasInstellingen.containsKey("aftrekHulp"))
			aftrekHulp = ideasInstellingen.getInt("aftrekHulp");
		if (ideasInstellingen.containsKey("aftrekStap"))
			aftrekStap = ideasInstellingen.getInt("aftrekStap");
		if (ideasInstellingen.containsKey("aftrekSolve"))
			aftrekSolve = ideasInstellingen.getInt("aftrekSolve");
        
       
		
	}
	
	public void zetSubstitutie(Expressie e)
	{
		substitutie = e;
	}
	
	public void zetJuisteAntwoord(String s)
	{	zetJuisteAntwoord(s,true);
	}
	
	public void zetJuisteAntwoord(String s, boolean preparePrefix)
	{	
		if(s.length()>5 && s.substring(0,6).equals("$fCAS{"))
		{	casCheck = true;
			casString = s.substring(6,s.length()-2);
			return;
		}
		else casCheck = false;
		
		int index = s.indexOf("=");
        if(index==-1) index = s.indexOf("\u2248");
		if(index>-1)
		{	//prefix = s.substring(0,index+1)+"@";
			//hasPrefix = true;
			s = "$f"+ s.substring(index+1);
			if(preparePrefix)
			{	
			}
		}
	
		s = s.substring(2,s.length()-1);
		String[] antwoordStrings = s.split(";");
		
		if(antwoordStrings.length==1)antwoordStrings = s.split("::");
		
		juisteAntwoorden = new Expressie[antwoordStrings.length];
		absPrecisions = new double[antwoordStrings.length];
		
		FormuleParser p = new FormuleParser();
		for(int i=0 ; i<antwoordStrings.length; i++) 
		{	if(antwoordStrings[i]!=null)
			{	String[] antwoordDelen = antwoordStrings[i].split("�");//"�");
				if(antwoordDelen.length>1)
				{
					String antwoordStr = "$f" + antwoordDelen[0] + "@";
					juisteAntwoorden[i] = p.parse(p.schoon(p.formuleString(antwoordStr)));
					Expressie e = FormuleParser.geefExpressie("$f" + antwoordDelen[1] + "@");
					if (e !=null && !Double.isNaN(e.geefWaarde())) absPrecisions[i] = e.geefWaarde();
					
				}
				else
				{
					String antwoordStr = "$f" + antwoordStrings[i] + "@";
					juisteAntwoorden[i] = p.parse(p.schoon(p.formuleString(antwoordStr)));
				}
			}
			else
			{
				String antwoordStr = "$f" + antwoordStrings[i] + "@";
				juisteAntwoorden[i] = p.parse(p.schoon(p.formuleString(antwoordStr)));
			}
		}
		
	}
	
	public void zetJuisteVorm(String s)
	{	s = s.substring(2,s.length()-1);
		String[] antwoordStrings = s.split(";");
		
		if(antwoordStrings.length==1)antwoordStrings = s.split("::");
		
		juisteVormen = new Expressie[antwoordStrings.length];
		
		FormuleParser p = new FormuleParser();
		for(int i=0 ; i<antwoordStrings.length; i++) 
		{	String antwoordStr = "$f" + antwoordStrings[i] + "@";
			juisteVormen[i] = p.parse(p.schoon(p.formuleString(antwoordStr)));
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
	{	if(WiskOpdr.ideas==null)
		{	JOptionPane.showMessageDialog(antwoordFormuleVak,"Feedbackservice not available");
			return;
		}
	
		if(expAnswerString.equals("$f@"))return;
		else if(FormuleParser.geefExpressie(expAnswerString)==null)
		{	feedback = WiskOpdr.rb.getString("feedbackTekst14");
			correct = false;
			fout = false;
			return;
		}
		
		String eStringVorig = expAnswerStringPrevious;
		eStringVorig = verwijderIsTeken(eStringVorig);
		String eStriktVorig = FormuleParser.geefExpressie(eStringVorig).toStringStrikt();
		eStriktVorig = vertaalNaarIdeasExpressie(eStriktVorig);
		
		String eStringHuidig = expAnswerString;
		eStringHuidig = verwijderIsTeken(eStringHuidig);
		String eStriktHuidig = FormuleParser.geefExpressie(eStringHuidig).toStringStrikt();
		eStriktHuidig = vertaalNaarIdeasExpressie(eStriktHuidig);
		
		RuleIF rule = WiskOpdr.ideas.diagnose(eStriktVorig,eStriktHuidig,strategieDomein);
		
		String feedback = translateRule(rule.getName());
		if(rule.getName().equals("notequiv")) 
		{	feedback = translateRule(rule.getName());
			goedHalfFout = FOUT;
			correct = false;
			fout = true;
		}
		else if(rule.getName().equals("buggy")) 
		{	if(feedbackBijFout) feedback = translateRule(rule.getId());
			goedHalfFout = FOUT;
			correct = false;
			fout = true;
		}
		else if(rule.isReady()) 
		{	feedback = translateRule("ready");
			goedHalfFout = GOED;
			if(!hasFeedback)correct = true;
			fout = false;
			score = puntenExact+puntenHerleiding+puntenGelijkwaardig;
		}
		else if(rule.getName().equals("similar")) 
		{	feedback = translateRule(rule.getName());
			goedHalfFout = GEEN;
			correct = false;
			fout = false;
		}
		
		else if(rule.getName().equals("expected")) 
		{	feedback = translateRule(rule.getName());
			goedHalfFout = DOOR;
			correct = false;
			fout = false;
		}
		else if(rule.getName().equals("detour")) 
		{	feedback = translateRule(rule.getName());
			goedHalfFout = DOOR;
			correct = false;
			fout = false;
		}
		else if(rule.getName().equals("correct")) 
		{	feedback = translateRule(rule.getName());
			goedHalfFout = DOOR;
			correct = false;
			fout = false;
		}
		
		if(fout && tipBijFout)
		{	RuleIF rules = WiskOpdr.ideas.getOneFirst(eStriktVorig,strategieDomein);
			if(hulpBijTip); //setFeedback(feedback + "\n\nTip: \n" 
					//+ translateRule(rules.getId()) + "\n"
					//,true,3);
			else feedback = "Tip: \n" + translateRule(rules.getId()) + "\n";
		}
		
		if(hasFeedback)
		{	check(expAnswerString);
			evaluate();
		}
	}
	*/
	public void evaluate()
	{	if(syntaxFout)
		{	goedHalfFout = FOUT;
			feedback = Text.constants.feedbackTekst14();
			return;
		}
		else if(leeg)
		{	goedHalfFout = GEEN;
			return;
		}
		
		else if(hasFeedback)
		{	if(goedHalfFout==GOED)
			{	score = puntenFeedback;
				correct = true;
				fout = false;
			}
			else if(goedHalfFout==DOOR)
			{	score = puntenFeedback;
				correct = false;
				fout = false;
			}
			else if(goedHalfFout==FOUT)
			{	score = puntenFeedback;
				correct = false;
				fout = true;
			}
		}
		else if(casCheck)
		{	if(casResult)
			{	goedHalfFout = GOED;
				score = scoreMax;
				correct = true;
				fout = false;
			}
			else 
			{	goedHalfFout = FOUT;
				score = 0;
				correct = false;
				fout = true;
			}
		}
		else
		{	if(!herleiding && !exact) 
			{	if(isGelijkwaardig)
				{	goedHalfFout = GOED;
					score = puntenGelijkwaardig;
					correct = true;
					fout = false;
				}
				else
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
				}
			}
			else if(herleiding && !exact)
			{	if(isGelijkwaardig && isHerleid)
				{	goedHalfFout = GOED;
					score = puntenGelijkwaardig + puntenHerleiding;
					correct = true;
					fout = false;
				}
				else if(isGelijkwaardig && !isHerleid)
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig;
					correct = false;
					fout = false;
				}
				else 
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
				}
			}
			else if(exact && !herleiding)
			{	if(isGelijkwaardig && isExact)
				{	goedHalfFout = GOED;
					score = puntenGelijkwaardig + puntenExact;
					correct = true;
					fout = false;
				}
				else if(isGelijkwaardig && !isExact)
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig;
					correct = false;
					fout = false;
				}
				else 
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
				}
			}
			else if(exact && herleiding)
			{	if(isGelijkwaardig && isExact)
				{	goedHalfFout = GOED;
					score = puntenGelijkwaardig + puntenHerleiding + puntenExact;
					correct = true;
					fout = false;
				}
				else if(isGelijkwaardig && isHerleid)
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig + puntenHerleiding;
					correct = false;
					fout = false;
				}
				else if(isGelijkwaardig)
				{	goedHalfFout = DOOR;
					score = puntenGelijkwaardig;
					correct = false;
					fout = false;
				}
				else 
				{	goedHalfFout = FOUT;
					score = 0;
					correct = false;
					fout = true;
				}
			}
		}
	}
	/*
	public void checkCasStatement(String expAntwoordString)	
	{
		String checkString = casString;
		for(int j=checkString.length()-1 ; j>-1; j--)
		{	if(checkString.charAt(j)=='}')
			{	int index1 = checkString.substring(0,j).lastIndexOf("{");
				String parseString = checkString.substring(index1+1,j);
				if(parseString.equals("ANS"))
				{	parseString = (FormuleParser.geefExpressie(expAntwoordString)).toStringCAS();
				}
				checkString = ""+checkString.substring(0,index1)+parseString+checkString.substring(j+1);
				j=index1;
			}	
		}
		Expressie e = Expressie.evalWithCAS(checkString);
		String casResultString = "False";
		if(e!=null) casResultString = e.toString();
		casResult = "True".equals(casResultString);
		
	}
	*/
	public String verwijderIsTeken(String inputStr){
		if(inputStr.charAt(inputStr.length()-2)=='=' || inputStr.charAt(inputStr.length()-2)=='\u2248')
		{	int isIndex = inputStr.length()-2;
			inputStr = inputStr.substring(0,isIndex)+"@";
		}
		return inputStr;
	}
	
	public void check(String expAntwoordString)	
	{	
		syntaxFout = false;
		leeg = false;
		
		Expressie antwoord = FormuleParser.geefExpressie(expAntwoordString);
		//logger.fine("check antwoord  " + (antwoord == null) );
		//if(antwoord != null) logger.fine(antwoord.getClass().toString());
// XXX Vraag me niet waarom? Bug in String.charAt(int)
		//if(antwoord != null && antwoord.toString().isEmpty()) antwoord = null;
		
		Expressie antwoordNonSub = antwoord;
		
		if(gebruikersSubstituties!=null && antwoord!=null) 
		{	for(int i=0 ; i<gebruikersSubstituties.length  ; i++)
			{	antwoord = antwoord.substitueer(gebruikersSubstituties[i].geefExpRechts(),gebruikersSubstituties[i].geefExpLinks().geefVarNaam());
			}
		}
		
		if(antwoord==null) 
		{	String antwoordString = expAntwoordString;
			if(antwoordString.charAt(antwoordString.length()-2)=='=' || antwoordString.charAt(antwoordString.length()-2)=='\u2248')
			{	int isIndex = antwoordString.length()-2;
				antwoordString = antwoordString.substring(0,isIndex)+"@";
				antwoord = FormuleParser.geefExpressie(antwoordString);
			}
		}
		
		if(antwoord==null && expAntwoordString.length()>3)
		{	syntaxFout = true;
			return;
		}
		else if(antwoord==null)
		{
			leeg = true;
			return;
		}
		
		if(substitutie!=null && antwoord!=null) 
		{	antwoord = antwoord.substitueer(substitutie,"u");
		}
		
		
		Expressie antwoordEvalCAS = null;
		boolean casNodig = false;
		if(antwoord!=null) casNodig = antwoord.toString().indexOf("$i")>-1 || antwoord.toString().indexOf("$d")>-1 || antwoord.toString().indexOf("$T")>-1  || antwoord.toString().indexOf("$S")>-1  || antwoord.toString().indexOf("$P")>-1;
		
		if(casNodig)
		{	//antwoordEvalCAS = Expressie.evalWithCAS(antwoord);
			
		}
		
		isGelijkwaardig = false;
		
		if(hasFeedback) 
		{	int aantalAnswerModels = answerModels.size();
			for(int h=0 ; h< aantalAnswerModels; h++)
			{	setAnswerModel(h);
				boolean pastGelijkwaardig = false;
				boolean pastHerleid = false;
				boolean pastExact = false;
				
				if(casCheck)
				{	/*checkCasStatement(expAntwoordString);	
					if(casResult)
					{	pastGelijkwaardig = true;
						pastHerleid = true;
						pastExact = true;
					}*/
				}
				else
				{	for(int i=0 ; i<juisteAntwoorden.length ; i++)
					{	//if(casNodig)pastGelijkwaardig = pastGelijkwaardig || AntwoordChecker.checkGelijkwaardig(antwoordEvalCAS,juisteAntwoorden[i], absPrecisions[i]);
						//else 
							pastGelijkwaardig = pastGelijkwaardig || AntwoordChecker.checkGelijkwaardig(antwoord,juisteAntwoorden[i], absPrecisions[i]);
						
						if(Algebra.isBreukPlusGetal(juisteAntwoorden[i]))pastExact = pastExact || AntwoordChecker.checkExactBreukPlusGetal(expAntwoordString,juisteAntwoorden[i]);
						else if(substitutie!=null)pastExact = pastExact || AntwoordChecker.checkExact(antwoordNonSub,juisteAntwoorden[i]);
                        else pastExact = pastExact || AntwoordChecker.checkExact(antwoord,juisteAntwoorden[i]);
					}
					for(int i=0 ; juisteVormen!=null && i<juisteVormen.length ; i++)
					{	pastHerleid = pastHerleid || AntwoordChecker.checkHerleiding(antwoord, juisteVormen[i], soortHerleiding);	
					}
					if(!gelijkwaardigP)pastGelijkwaardig = true;
					if(!herleidingP)pastHerleid = true;
					if(!exactP)pastExact = true;
				}
				
				boolean answerModelFits = pastGelijkwaardig && pastHerleid && pastExact;
				//if(juisteAntwoorden[0].toString().equals("else"))answerModelFits = true;
				if(answerModelFits) 
				{	// feedback van dit tabblad wordt gebruikt
					break;
				}
			}
		}
		else if(casCheck)
		{	//checkCasStatement(expAntwoordString);
		}
		else 
		{	
			//System.out.println(formuleVak.toString());
			//System.out.println(FormuleParser.formuleString(formuleVak.toString()));
			//System.out.println(FormuleParser.schoon(FormuleParser.formuleString(formuleVak.toString())));
			//System.out.println(FormuleParser.pel(FormuleParser.schoon(FormuleParser.formuleString(formuleVak.toString()))));
			
			isHerleid = false;	
			for(int i=0 ; i<juisteAntwoorden.length ; i++)
			{	if(casNodig);//isGelijkwaardig = isGelijkwaardig || AntwoordChecker.checkGelijkwaardig(antwoordEvalCAS,juisteAntwoorden[i],absPrecisions[i]);
				else isGelijkwaardig = isGelijkwaardig || AntwoordChecker.checkGelijkwaardig(antwoord,juisteAntwoorden[i],absPrecisions[i]);
				if(soortHerleiding!=0)isHerleid = AntwoordChecker.checkHerleiding(antwoord,juisteAntwoorden[0], soortHerleiding);
				
				if(Algebra.isBreukPlusGetal(juisteAntwoorden[i]))isExact = AntwoordChecker.checkExactBreukPlusGetal(expAntwoordString,juisteAntwoorden[i]);
				else if(gebruikersSubstituties!=null)isExact = AntwoordChecker.checkExact(antwoordNonSub,juisteAntwoorden[i]);
				else isExact = AntwoordChecker.checkExact(antwoord,juisteAntwoorden[i]);
				
				if(isExact)break;
			}
			for(int i=0 ; soortHerleiding==0 && i<juisteVormen.length ; i++)
			{	isHerleid = isHerleid || AntwoordChecker.checkHerleiding(antwoord,juisteVormen[i], soortHerleiding);
				if(isHerleid)break;
			}
		}
	}	
	
	public String vertaalIdeasExpressie(String s)
	{
		//System.out.println(s);
		//s = StringUtils.replaceStr(s,"?",WiskOpdr.rb.getString("ofLabel"));
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
}
