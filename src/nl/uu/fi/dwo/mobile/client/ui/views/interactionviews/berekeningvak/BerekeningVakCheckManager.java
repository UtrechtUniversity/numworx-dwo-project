package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.HashMap;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.interaction.client.LessonMode;

public class BerekeningVakCheckManager {
	
	private BerekeningVak berekeningVak;
	private AntwoordFormuleVakChecker avChecker ;
	
	private int mode;
	private boolean review;
	
	private boolean correct;
	private boolean fout;
	private int score;
	private boolean nagekeken;
	
	public BerekeningVakCheckManager(BerekeningVak berekeningVak) {
		this.berekeningVak = berekeningVak;
		avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) berekeningVak.settings.launchState(), berekeningVak.randomVarNamen, berekeningVak.randomVarWaarden);
		
	}
	
	public void check_enter() {
		if(mode==0 || mode == 1) {
			checkRegel(true);
		}
	}
	
	public void check_kijkNa() {
		if(mode==0 || mode == 1 || mode == 2) {
			checkRegel(true);
		}
	}
	
	public void check_getState() {
		if(mode==2 || mode == 3) {
			checkRegel(false);
		}
	}
	
	public void check_setState() {
		if(mode==0 || mode == 1) {
			checkRegel(true);
		}
		if(mode==2 && nagekeken) {
			checkRegel(false);
		}
		else if(mode==2 || mode == 3) {
			checkRegel(false);
		}
	}
	
	
	private void checkRegel(boolean view) {
		if(!berekeningVak.settings.check())
			return;
		int regelScore = 0;
		boolean regelCorrect = false;
		String regelString = berekeningVak.geefVakRegel(0).geefFormuleEditor().toString();
		regelString = regelString.replace("\u2248", "§\u2248§");
		regelString = regelString.replace("=", "§=§");
		regelString = regelString.replace(";", "§;§");
		String[] deelStrings = regelString.split("§");
		for(int i=0 ; i<deelStrings.length ; i++) {
			Expressie expressie = FormuleParser.geefExpressie("$f"+deelStrings[i]+"@");
			if(expressie!=null) {
				HashMap<String, Object> checkResults = new HashMap<String, Object>();
				try {
					checkResults = avChecker.checkAnswer("$f"+deelStrings[i]+"@");
					int goedHalfFout = (Integer)checkResults.get("goedHalfFout");
					if(view && goedHalfFout==avChecker.GOED)
						deelStrings[i] = deelStrings[i]+"\u2705";
					else if(view && (goedHalfFout==avChecker.HALF || goedHalfFout==avChecker.DOOR))
						deelStrings[i] = deelStrings[i]+"\u2714";
					else if(view && goedHalfFout==avChecker.FOUT)
						deelStrings[i] = deelStrings[i]+"\u274c";
					regelScore = Math.max(regelScore, (Integer)checkResults.get("score"));
					regelCorrect = regelCorrect || (Boolean)checkResults.get("correct");
					
				}
				catch (RestartException e){}
			}
		}
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
		
		berekeningVak.geefVakRegel(0).berekeningVak.setChanged();
	}
	
	public int getScore() {
		if(!berekeningVak.settings.teltMee())
			return 0;
		return score;
	}
	
	public boolean getCorrect() {
		if(!berekeningVak.settings.teltMee())
			return Boolean.TRUE;
		return correct;
	}
	
	public boolean getFout() {
		return fout;
	}
	
	public void zetMode(int mode, LessonMode lessonMode) {
		this.mode = mode;
		this.review = lessonMode == LessonMode.review;
	}
	
	public void zetNagekeken(boolean b) {
		nagekeken = b;
	}

	public boolean isNagekeken() {
		return nagekeken;
	}
}
