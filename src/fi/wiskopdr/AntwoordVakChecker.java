package fi.wiskopdr;

import java.util.HashMap;

import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Vergelijking;

public interface AntwoordVakChecker {

	public static final int GOED = 0;
	public static final int DOOR = 1;
	public static final int HALF = 2;
	public static final int FOUT = 3;
	public static final int GEEN = 4;
	
	public HashMap<String,Object> checkAnswer(String answer, String answerPrevious, Expressie substitutie, Vergelijking[] gebruikersSubstituties);
	
	public HashMap<String,Object> checkAnswer(String answer);
	
	public void zetSubstitutie(Expressie e);
	
}
