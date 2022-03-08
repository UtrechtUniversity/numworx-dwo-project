package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.HashMap;
import java.util.logging.Logger;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class BerekeningVakCheckManager {
	
	static private Logger logger = Logger.getLogger("BerekeningVakChecker");
	
	private BerekeningVak berekeningVak;
	private AntwoordFormuleVakChecker avChecker ;
	private OpdrNavIF comRoot;
	
	private int mode;
	private boolean review;
	private boolean browse;
	
	private Boolean correct;
	private boolean fout;
	private int score;
	
	private boolean nagekeken;
	private int errorCount;
	private boolean ingevuld;
	private int foutStraf = 2;
	
	private String lastAnswer = "";
	
	public BerekeningVakCheckManager(BerekeningVak berekeningVak) {
		this.berekeningVak = berekeningVak;
		avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) berekeningVak.settings.launchState(), berekeningVak.randomVarNamen, berekeningVak.randomVarWaarden);
	}
	
	public void check_enter() {
		logger.info("check-Enter");
		if(mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegel(true);
			setChanged();
		}
	}
	
	public void check_kijkNa() {
		logger.info("check-KijkNa");
		if(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || (mode==OpdrNavIF.ZELFTOETS && !nagekeken )) {
			checkRegel(true);
			zetNagekeken(true);
			setChanged();
		}
	}
	
	public void check_getState() {
		logger.info("check-getState");
		if(mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegel(true);
			setChanged();
		}
		if(mode==OpdrNavIF.ZELFTOETS && !nagekeken || mode == OpdrNavIF.EINDTOETS) {
			checkRegel(false);
		}
	}
	
	public void check_setState() {
		logger.info("check-setState");
		if(browse || review || mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegel(true);
			setChanged();
		}
		if(mode==OpdrNavIF.ZELFTOETS && nagekeken) {
			checkRegel(true);
			setChanged();
		}
		else if(mode==OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS) {
			checkRegel(false);
		}
	}
	
	public void setAnswerChanged() {
		nagekeken = false;
	}
	
	private void checkRegel(boolean view) {
		int regelScore = 0;
		boolean regelCorrect = false;
		String regelString = berekeningVak.geefVakRegel(0).geefFormuleEditor().toString();
		ingevuld = (regelString.equals("") ? false : true);
		if(!ingevuld) 
			return;
		regelString = regelString.replace("\u2248", "§\u2248§");
		regelString = regelString.replace("=", "§=§");
		regelString = regelString.replace(";", "§;§");
		String[] deelStrings = regelString.split("§");
		
		int[] scoreContainer = new int[deelStrings.length];
		for(int i=0 ; i<deelStrings.length ; i++) {
			scoreContainer[i] = -1;
		}
		for(int i=0 ; i<deelStrings.length ; i++) {
			Expressie expressie = FormuleParser.geefExpressie("$f"+deelStrings[i]+"@");
			if(expressie!=null) {
				HashMap<String, Object> checkResults = new HashMap<String, Object>();
				try {
					checkResults = avChecker.checkAnswer("$f"+deelStrings[i]+"@");
					int answerModelNr = (Integer)checkResults.get("answerModelNr");
					int goedHalfFout = (Integer)checkResults.get("goedHalfFout");
					if(view && goedHalfFout==avChecker.GOED)
						deelStrings[i] = deelStrings[i]+"\u2705";
					else if(view && (goedHalfFout==avChecker.HALF || goedHalfFout==avChecker.DOOR))
						deelStrings[i] = deelStrings[i]+"\u2714";
					else if(view && goedHalfFout==avChecker.FOUT) {
						deelStrings[i] = deelStrings[i]+"\u274c";
						errorCount ++;
					}
					regelScore = Math.max(regelScore, (Integer)checkResults.get("score"));
					regelCorrect = regelCorrect || (Boolean)checkResults.get("correct");
					
					if(berekeningVak.settings.scoreCumulatief())
						scoreContainer[i] = answerModelNr;
				}
				catch (RestartException e){}
			}
		}
		if(berekeningVak.settings.scoreCumulatief()) 
			regelScore = getCumScore(scoreContainer);
		
		score = regelScore;
		correct = regelCorrect;
		
		String checkedString = "";
		for(int i=0 ; i<deelStrings.length ; i++) {
			checkedString = checkedString + deelStrings[i];
		}
		berekeningVak.geefVakRegel(0).geefFormuleEditor().clearMain();
		berekeningVak.geefVakRegel(0).geefFormuleEditor().insert(checkedString);
		berekeningVak.geefVakRegel(0).geefFormuleEditor().paint();
		berekeningVak.geefVakRegel(0).regelResize();
	}
	
	public void getCheckerState(HashMap<String, Object> state) {
		state.put("ingevuld", Boolean.valueOf(ingevuld));
		state.put("nagekeken", Boolean.valueOf(nagekeken));
		state.put("errorCount", new Integer(errorCount));
	}
	
	public void setCheckerState(ObjectMap state) {
		ingevuld = state.getBoolean("ingevuld");
		nagekeken = state.getBoolean("nagekeken");
		errorCount = state.getInt("errorCount");
	}
	
	private int getCumScore(int[] scoreContainerTemp) {
		int scoreCum = 0;
		for(int i=0 ; i<scoreContainerTemp.length ; i++) {
			int amNr = scoreContainerTemp[i];
			if(amNr>-1) {
				scoreCum = scoreCum + avChecker.getAnswerModelScore(amNr);
				for(int j=i ; j<scoreContainerTemp.length ; j++) {
					if(scoreContainerTemp[j] == amNr) 
						scoreContainerTemp[j] = -1;
				}
			}
		}
		return scoreCum;
	}
	
	public int getScore() {
		if(!berekeningVak.settings.teltMee())
			return 0;
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			return(Math.max(0, score - errorCount * foutStraf));
		return score;
	}
	
	public Boolean getCorrect() {
		if(!berekeningVak.settings.teltMee())
			return Boolean.TRUE;
		return correct;
	}
	
	public boolean getFout() {
		return fout;
	}
	
	public void setChanged() {
		if(!lastAnswer.equals(berekeningVak.geefVakRegel(0).geefFormuleEditor().toString())) {
			lastAnswer = berekeningVak.geefVakRegel(0).geefFormuleEditor().toString();
			if(berekeningVak.settings.teltMee() && comRoot!=null)
				comRoot.setChanged(false);
		}
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode(), comRoot.getLessonMode());
	}
	
	public void zetMode(int mode, LessonMode lessonMode) {
		this.mode = mode;
		this.review = lessonMode == LessonMode.review;
		this.browse = lessonMode == LessonMode.browse;
	}
	
	public void zetNagekeken(boolean b) {
		nagekeken = b;
	}

	public boolean isNagekeken() {
		return nagekeken;
	}
}
