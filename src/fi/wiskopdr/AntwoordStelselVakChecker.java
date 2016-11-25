package fi.wiskopdr;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Logger;

import com.google.gwt.user.client.Window;

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
import fi.wiskopdr.text.TextConstants;



public class AntwoordStelselVakChecker implements AntwoordVakChecker
{
	private int score;
	private int scoreMax;
	private boolean correct;
	
	private boolean gelijkwaardig;
		
	private String[] varNamen;
	//private Expressie[][] juisteOplossingen;
	    
    private List<Map<String,Object>> answerModels;
    private String[] randomVarNamen = null;
	private HashMap<String, Number> randomVarWaarden = null;
	
	private boolean hasFeedback;
	private int puntenFeedback;
	private int goedHalfFout;
	private String feedback;
	
	private boolean syntaxFout;
	private boolean leeg;
	
	private Expressie[][] oplossingen; 
	private boolean[][] eindOplossingGevonden;
	private boolean[][] eindOplossingStelselGevonden;
	private boolean[][] eindOplossingExactGevonden;
	
	boolean isEindOplossing = false;
	boolean isEindOplossingExact = false;
	boolean isEindOplossingStelsel = false;
	boolean isDeelOplossing = false;
	boolean bevatFouteOplossing = false;
	//private boolean bevatVoldoetNiet;
	
	public static TextConstants rb = Text.constants;
	
	
	
	private final static Logger logger = Logger.getLogger("AntwoordStelselVakChecker");
    
	
	public AntwoordStelselVakChecker(HashMap<String,Object> map, String[] randomVars, HashMap<String,Number> randomValues )
	{	
		randomVarNamen = randomVars;
		randomVarWaarden = randomValues;
		ObjectMap asvCheckerModel = JSONUtilities.wrapMap(map);
		List<Map<String,Object>> answerModels = null;
		boolean hasFeedback = false;
		int scoreMax = 0;
				
		if(asvCheckerModel.containsKey("answerModels")) answerModels = asvCheckerModel.getMapList("answerModels");
		if(asvCheckerModel.containsKey("hasFeedback")) hasFeedback = asvCheckerModel.getBoolean("hasFeedback");
		if(asvCheckerModel.containsKey("scoreMax")) scoreMax = asvCheckerModel.getInt("scoreMax");
			
		
	    this.answerModels = new ArrayList<Map<String, Object>> ();
		for(int i = 0; i < answerModels.size(); i++)
		{	this.answerModels.add(answerModels.get(i));
		}
		initialiseerAnswerModels();
        this.hasFeedback = hasFeedback;
        
        this.scoreMax = scoreMax;
    }
	
	public AntwoordStelselVakChecker(AntwoordStelselVakChecker avChecker)
	{
		randomVarNamen = avChecker.randomVarNamen;
		randomVarWaarden = avChecker.randomVarWaarden;
		answerModels = avChecker.answerModels;
		hasFeedback = avChecker.hasFeedback;
		scoreMax = avChecker.scoreMax;
		varNamen = avChecker.varNamen;
		oplossingen = avChecker.oplossingen;
	}
	
	//gebruiken voor antwoorden uit StelselOplossingenVak
	public HashMap checkAnswer(String answer) throws RestartException
	{	this.score = 0;
		this.correct = false;
		this.goedHalfFout = AntwoordVakChecker.GEEN;
		this.syntaxFout = false;
		this.gelijkwaardig = false;
		this.feedback = "";
		
		if (hasFeedback)
		//TODO: hasFeedback eerst in javaversie testen, is denk ik nog niet goed geimplementeerd.
		{
			int aantalAnswerModels = answerModels.size();
			for (int h = 0; h < aantalAnswerModels; h++)
			{
				setAnswerModel(h);
				gelijkwaardig = bepaalGelijkwaardig(answer);
				if (gelijkwaardig || h == aantalAnswerModels - 1)
					break;
			}
		}
		else
		{
			gelijkwaardig = bepaalGelijkwaardig(answer);
			evaluate();
		}
		System.out.println("check: goedHalfFout = " + goedHalfFout);
		HashMap checkResults = new HashMap();
		if(goedHalfFout != GEEN) 
			checkResults.put("correct", new Boolean(correct));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
		checkResults.put("syntaxFout", new Boolean(syntaxFout));
				
		return checkResults;
	}
	
	//gebruiken voor antwoorden uit StelselEditors in StelselRekenVak
	public HashMap checkAnswer(String answer, String answerPrevious, Expressie substitutie, Vergelijking[] gebruikersSubstituties) throws RestartException
	{	
		this.score = 0;
		this.correct = false;
		this.goedHalfFout = AntwoordVakChecker.GEEN;
		this.syntaxFout = false;
		this.gelijkwaardig = false;
		this.feedback = "";
	
		VergelijkingMeerv antwoord = null;
		VergelijkingMeerv antwoordIngevuld = FormuleParser.parseVergelijking(answer);
				
		antwoord = antwoordIngevuld;
		
		if (antwoord != null)
		{ 	String diffVar = "x";
			for(int i = 0; i < antwoord.geefAantal(); i++)
			{	String diffVar2 = antwoord.geefVergelijking(i).geefVarNaam();
				if(diffVar2 != null && !diffVar2.equals(""))
				{	diffVar = diffVar2;
					break;
				}
			}
			String[] varNamen = new String[this.varNamen.length];
			for(int i = 0; i < this.varNamen.length; i++)
			{	Expressie e = FormuleParser.geefExpressie("$f" + this.varNamen[i] + "@");
				if(e.isVar())
					varNamen[i] = e.geefVarNaam();
			}
			
			boolean isGelijkwaardigEind = antwoord.isStelselOplossing(oplossingen, varNamen);
			gelijkwaardig = isGelijkwaardigEind;
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
			if (answer.indexOf("|") > -1)
			{ 	feedback = rb.feedbackTekst08();//, false);
				
			}
			else if (answer.length() > 3)
			{ 	feedback = rb.feedbackTekst09();//, false);
			}
		}
		Algebra.setDefaultTestValues();
		evaluateStap();
		HashMap checkResults = new HashMap();
		if(goedHalfFout != GEEN) 
			checkResults.put("correct", new Boolean(correct));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
		checkResults.put("syntaxFout", new Boolean(syntaxFout));
				
		return checkResults;

	}
	
	public void zetOplossingen(Expressie[][] oplossingen, boolean[][] eindOplossing, boolean[][] eindOplossingStelsel, boolean[][] eindOplossingExact)
	{
		this.oplossingen = oplossingen;
		eindOplossingGevonden = eindOplossing;
		eindOplossingStelselGevonden = eindOplossingStelsel;
		eindOplossingExactGevonden = eindOplossingExact;
	}
	
	public boolean bepaalGelijkwaardig(String answer)
	{
		boolean gelijkwaardig = true;
		Expressie[][] leerlingOplossingen = bepaalOplossingen(answer);
		if(leerlingOplossingen == null)
			return false;
		boolean[] oplossingenCorrect = new boolean[oplossingen.length];
		for(int i = 0; i < oplossingenCorrect.length; i++)
			oplossingenCorrect[i] = false;
		for(int i = 0; i < leerlingOplossingen.length; i++)
		{
			Expressie[] leerlingOpl = leerlingOplossingen[i];
			//mbv isOplossing houd je bij of deze leerlingOplossing inderdaad een oplossing is
			boolean isOplossing = false;
			for(int j = 0; j < oplossingen.length; j++)
			{
				boolean gelijk = true;
				for(int k = 0; k < varNamen.length; k++)
				{
					if(!Algebra.isGelijkwaardig(leerlingOpl[k], oplossingen[j][k]))
					{
						gelijk = false;
						break;
					}
				}
				if(gelijk)
				{	oplossingenCorrect[j] = true;
					isOplossing = true;
				}
			}
			if(!isOplossing)
				return false;
		}
		//controleren of alle oplossingen gevonden zijn. 
		for(int i = 0; i < oplossingenCorrect.length; i++)
		{	if(!oplossingenCorrect[i])
			{	gelijkwaardig = false;
				break;
			}
		}
		return gelijkwaardig;
	}
	
	public Expressie[][] bepaalOplossingen(String antwoordString)
	{
		antwoordString = antwoordString.replace(" ", "");
		Expressie[][] oplossingen;
		try{
			//splitsen in verschillende oplossingen. Eerst $f en @ weghalen.
			antwoordString = antwoordString.substring(2, antwoordString.length() - 1);
			antwoordString = antwoordString.replace("),(", "):(");
			String[] oplossingenStrings = antwoordString.split(":");
			oplossingen = new Expressie[oplossingenStrings.length][varNamen.length];
			for(int i = 0; i < oplossingenStrings.length; i++)
			{
				if(oplossingenStrings[i].length() < 2) return null;
				//haakjes verwijderen:
				String opl = oplossingenStrings[i].substring(1, oplossingenStrings[i].length() - 1);
				String[] varWaardes;
				if(opl.contains(";"))
					varWaardes = opl.split(";");
				else
					varWaardes = opl.split(",");
				for(int j = 0; j < varNamen.length; j++)
				{	oplossingen[i][j] = FormuleParser.geefExpressie("$f" + varWaardes[j] + "@");
				}
			}
			return oplossingen;
		}
		catch(Exception e)
		{return null;}
	}
	
	public void zetVarNamen(String[] namen)
	{
		varNamen = namen;
	}
	
	public void zetOplossingen(Expressie[][] oplossingen)
	{
		this.oplossingen = oplossingen;
	}
	
	public void initialiseerAnswerModels()
	{
		for(int i = 0; i < answerModels.size(); i++)
		{
			Map<String, Object> h = answerModels.get(i);
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
		if(h==null) 
			return;
	
		//String antwoordString = "$f@";
		int puntenFeedback = 0;
		String feedback = "";
		int goedHalfFout = 0;
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map!=null) 
		{	//if(map.containsKey("antwoordString")) antwoordString = map.getString("antwoordString");
			if(map.containsKey("puntenFeedback")) puntenFeedback = map.getInt("puntenFeedback");
			if(map.containsKey("feedback")) feedback = map.getString("feedback");
			if(map.containsKey("goedHalfFout")) goedHalfFout = map.getInt("goedHalfFout");
		}
		
		

		//TODO: Randomiseren niet hier pas, maar al bij initialisatie. 
		//Dan hoeft het niet bij elke keer nakijken.
//        try         
//        {   antwoordString = FormuleParser.randomizeString(antwoordString,randomVarNamen,randomVarWaarden);
//        }
//        catch(Exception e)
//        {   antwoordString = "$f???@";
//        }
        try         
        {   feedback = modifyFeedback(feedback);
        	feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
        }
        catch(Exception e)
        {   feedback = "$f???@";
        }
        this.goedHalfFout = goedHalfFout;
        this.puntenFeedback = puntenFeedback;
        //this.antwoordString = antwoordString;
		this.feedback = feedback.trim();

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
			}
			else if(goedHalfFout==DOOR)
			{	score = puntenFeedback;
				correct = false;
			}
			else if(goedHalfFout==FOUT)
			{	score = puntenFeedback;
				correct = false;
			}
		}
		
		else
		{	
			if(gelijkwaardig)
			{	goedHalfFout = GOED;
				score = scoreMax;
				correct = true;
			}
			else
			{
				goedHalfFout = FOUT;
				score = 0;
				correct = false;
			}
		}
	}
	
	public void evaluateStap()
	{
		if (gelijkwaardig)
		{
			if (bevatFouteOplossing) // isGelijkwaardig && eindOplossingNodig (// kan deze situatie voorkomen?)
			{
				correct = false;
				goedHalfFout = HALF;
				feedback = rb.feedbackTekst02();// "Niet alle oplossingen voldoen aan de oorspronkelijke vergelijking. Verwijder de oplossingen die niet voldoen."
			}
			else if (isEindOplossing)
			{
				correct = true;
				goedHalfFout = GOED;
			}
			else
			{
				correct = false;
				goedHalfFout = DOOR;
			}
		}
		else
		// niet isGelijkwaardig
		{	if (isDeelOplossing)
			{	if (bevatFouteOplossing) // !isGelijkwaardig && isDeelOplossing
											// && bevatFouteOplossing
				{
					correct = false;
					goedHalfFout = FOUT;
					feedback = rb.feedbackTekst01();// "Deze stap bevat correcte en niet correcte onderdelen. Verwijder of vervang de delen die niet correct zijn"
				}
				else
				// !isGelijkwaardig && isDeelOplossing &&
				// !bevatFouteOplossing
				{
					correct = false;
					goedHalfFout = FOUT;
					feedback = rb.feedbackTekst07();// "Er ontbreken oplossingen. Vul aan."
				
				}
			}
			else
			// niet isDeelOplossing
			{
				correct = false;
				goedHalfFout = FOUT;
			}
		}
	}
	
	
	@Override
	public FunctieMVDefSet getFunctieMVDefSet() {
		return null;
	}

	@Override
	public void zetSubstitutie(Expressie e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetJuisteAntwoord(String answer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[][] getMeasuredMisconceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[][] getPossibleMisconceptions() {
		// TODO Auto-generated method stub
		return null;
	}
}
