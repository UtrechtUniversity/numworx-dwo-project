package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class BerekeningVakCheckManager {
	
	static private Logger logger = Logger.getLogger("BerekeningVakChecker");
	
	private BerekeningVak berekeningVak;
	private AntwoordFormuleVakChecker afvChecker;
	private AntwoordVergelijkingVakChecker avvChecker;
	private boolean checkerActief;
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
		if(berekeningVak.settings.formuleAntwoordModel()!=null)
			afvChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) berekeningVak.settings.formuleAntwoordModel(), berekeningVak.randomVarNamen, berekeningVak.randomVarWaarden);
		if(berekeningVak.settings.vergelijkingAntwoordModel()!=null)
			avvChecker = new AntwoordVergelijkingVakChecker((HashMap<String, Object>) berekeningVak.settings.vergelijkingAntwoordModel(), berekeningVak.randomVarNamen, berekeningVak.randomVarWaarden);
		checkerActief = afvChecker!=null || avvChecker!=null;
	}
	
	public void check_enter() {
		if(!checkerActief) return;
		logger.info("check-Enter");
		if(mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegels(true);
			setChanged();
		}
	}
	
	public void check_kijkNa() {
		if(!checkerActief) return;
		logger.info("check-KijkNa");
		if(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || (mode==OpdrNavIF.ZELFTOETS && !nagekeken )) {
			checkRegels(true);
			zetNagekeken(true);
			setChanged();
		}
	}
	
	public void check_getState() {
		if(!checkerActief) return;
		logger.info("check-getState");
		if(mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegels(true);
			setChanged();
		}
		if(mode==OpdrNavIF.ZELFTOETS && !nagekeken || mode == OpdrNavIF.EINDTOETS) {
			checkRegels(false);
		}
	}
	
	public void check_setState() {
		if(!checkerActief) return;
		logger.info("check-setState");
		if(browse || review || mode==OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			checkRegels(true);
			setChanged();
		}
		if(mode==OpdrNavIF.ZELFTOETS && nagekeken) {
			checkRegels(true);
			setChanged();
		}
		else if(mode==OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS) {
			checkRegels(false);
		}
	}
	
	public void setAnswerChanged() {
		nagekeken = false;
	}
	
	private int tempVakScore = 0;
	private boolean tempVakCorrect = false;
	private ArrayList<Integer> scoreContainer = null;
	
	private void checkRegels(boolean view) {
		if(afvChecker != null)
			checkRegelsFormule(view);
		if(avvChecker != null)
			checkRegelsVergelijking(view);
	}
	
	private void checkRegelsFormule(boolean view) {
		tempVakScore = 0;
		tempVakCorrect = false;
		scoreContainer = new ArrayList<Integer>();
		for(int i=0 ; i<berekeningVak.geefVakRegels().size() ; i++) {
			checkRegelFormule(i, view);
		}
		score = tempVakScore;
		if(berekeningVak.settings.scoreCumulatief()) 
			score = getCumScore(scoreContainer);
		correct = tempVakCorrect;
	}
	
	private void checkRegelsVergelijking(boolean view) {
		tempVakScore = 0;
		tempVakCorrect = false;
		scoreContainer = new ArrayList<Integer>();
		if(view)
			berekeningVak.prepareGoedFout();
		for(int i=0 ; i<berekeningVak.geefVakRegels().size() ; i++) {
			checkRegelVergelijking(i, view);
		}
		score = tempVakScore;
		if(berekeningVak.settings.scoreCumulatief()) 
			score = getCumScore(scoreContainer);
		correct = tempVakCorrect;
	}
	
	private void checkRegelFormule(int regelNr, boolean view) {
		int regelScore = 0;
		boolean regelCorrect = false;
		String regelString = berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().toString();
		ingevuld = (regelString.equals("") ? false : true);
		if(!ingevuld) 
			return;
		regelString = regelString.replace("\u2248", "§\u2248§");
		regelString = regelString.replace("=", "§=§");
		regelString = regelString.replace(";", "§;§");
		String[] deelStrings = regelString.split("§");
		
		for(int i=0 ; i<deelStrings.length ; i++) {
			Expressie expressie = FormuleParser.geefExpressie("$f"+deelStrings[i]+"@");
			if(expressie!=null) {
				HashMap<String, Object> checkResults = new HashMap<String, Object>();
				try {
					checkResults = afvChecker.checkAnswer("$f"+deelStrings[i]+"@");
					int goedHalfFout = (Integer)checkResults.get("goedHalfFout");
					if(view && goedHalfFout==afvChecker.GOED)
						deelStrings[i] = deelStrings[i]+"\u2705";
					else if(view && (goedHalfFout==afvChecker.HALF || goedHalfFout==afvChecker.DOOR))
						deelStrings[i] = deelStrings[i]+"\u2714";

					else if(view && goedHalfFout==afvChecker.FOUT) {

						deelStrings[i] = deelStrings[i]+"\u274c";
						errorCount ++;
					}
					regelScore = Math.max(regelScore, (Integer)checkResults.get("score"));
					regelCorrect = regelCorrect || (Boolean)checkResults.get("correct");
					
					if(berekeningVak.settings.scoreCumulatief()) {
						int answerModelNr = (Integer)checkResults.get("answerModelNr");
						scoreContainer.add(new Integer(answerModelNr));
					}
				}
				catch (RestartException e){}
			}
		}
		tempVakScore = Math.max(tempVakScore, regelScore);
		tempVakCorrect = tempVakCorrect || regelCorrect;
		
		String checkedString = "";
		for(int i=0 ; i<deelStrings.length ; i++) {
			checkedString = checkedString + deelStrings[i];
		}
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().clearMain();
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().insert(checkedString);
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().paint();
		berekeningVak.geefVakRegel(regelNr).regelResize();
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().requestFocus();
	}
	
	private void checkRegelVergelijking(int regelNr, boolean view) {
		int regelScore = 0;
		boolean regelCorrect = false;
		String regelString = berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().toString();
		ingevuld = (regelString.equals("") ? false : true);
		if(!ingevuld) 
			return;
		VergelijkingMeerv vergelijking = FormuleParser.parseVergelijking("$f"+regelString+"@");
		if(vergelijking!=null) {
			HashMap<String, Object> checkResults = new HashMap<String, Object>();
			try {
				checkResults = avvChecker.checkAnswer("$f"+regelString+"@");
				int goedHalfFout = (Integer)checkResults.get("goedHalfFout");
				if(view && goedHalfFout==afvChecker.GOED)
					berekeningVak.geefVakRegel(regelNr).zetGoedFout(goedHalfFout);
					//regelString = "\u2705" + regelString;
				else if(view && (goedHalfFout==afvChecker.HALF || goedHalfFout==afvChecker.DOOR))
					berekeningVak.geefVakRegel(regelNr).zetGoedFout(goedHalfFout);
					//regelString = "\u2714" + regelString;

				else if(view && goedHalfFout==afvChecker.FOUT) {
					berekeningVak.geefVakRegel(regelNr).zetGoedFout(goedHalfFout);
					//regelString = regelString +"\u274c";
					//errorCount ++;
				}
				regelScore = Math.max(regelScore, (Integer)checkResults.get("score"));
				regelCorrect = regelCorrect || (Boolean)checkResults.get("correct");
				
				if(berekeningVak.settings.scoreCumulatief()) {
					int answerModelNr = (Integer)checkResults.get("answerModelNr");
					scoreContainer.add(new Integer(answerModelNr));
				}
			}
			catch (RestartException e){}
		}
		tempVakScore = Math.max(tempVakScore, regelScore);
		tempVakCorrect = tempVakCorrect || regelCorrect;
		
		String checkedString = "";
		checkedString = checkedString + regelString;
		
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().clearMain();
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().insert(checkedString);
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().paint();
		berekeningVak.geefVakRegel(regelNr).regelResize();
		berekeningVak.geefVakRegel(regelNr).geefFormuleEditor().requestFocus();
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
				scoreCum = scoreCum + afvChecker.getAnswerModelScore(amNr);
				for(int j=i ; j<scoreContainerTemp.length ; j++) {
					if(scoreContainerTemp[j] == amNr) 
						scoreContainerTemp[j] = -1;
				}
			}
		}
		return scoreCum;
	}
	
	private int getCumScore(ArrayList<Integer> scoreContainerTemp) {
		int scoreCum = 0;
		for(int i=0 ; i<scoreContainerTemp.size() ; i++) {
			int amNr = scoreContainerTemp.get(i);
			if(amNr>-1) {
				scoreCum = scoreCum + afvChecker.getAnswerModelScore(amNr);
				for(int j=i ; j<scoreContainerTemp.size() ; j++) {
					if(scoreContainerTemp.get(j) == amNr) 
						scoreContainerTemp.set(j, -1);
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
//		if(!lastAnswer.equals(berekeningVak.geefVakRegel(0).geefFormuleEditor().toString())) {
//			lastAnswer = berekeningVak.geefVakRegel(0).geefFormuleEditor().toString();
			if(berekeningVak.settings.teltMee() && comRoot!=null)
				comRoot.setChanged(false);
//		}
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
