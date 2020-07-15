package nl.uu.fi.dwo.mobile.client.template;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class TemplateUUTestConstants implements TemplateConstants {
	
	HashMap<String,Object> MapAnswerboxFEWA = new HashMap<String,Object>();
	
	HashMap<String,Object> MapAnswerboxFEWS = new HashMap<String,Object>();
	
	HashMap<String,Object> MapAnswerboxCombo = new HashMap<String,Object>();
	
	HashMap<String,Object> MapCheckButton = new HashMap<String,Object>();
	
	public TemplateUUTestConstants() {
		//answerboxFEWA
		MapAnswerboxFEWA.put("background-color", "#FFFFFF");
		
		MapAnswerboxFEWA.put("border-style", "solid");
		MapAnswerboxFEWA.put("border-color", "#314770");
		MapAnswerboxFEWA.put("border-width", new Integer(2));
		MapAnswerboxFEWA.put("border-radius", new Integer(3));
		
		MapAnswerboxFEWA.put("margin-left", new Integer(0));
		MapAnswerboxFEWA.put("margin-right", new Integer(0));
		MapAnswerboxFEWA.put("margin-top", new Integer(0));
		MapAnswerboxFEWA.put("margin-bottom", new Integer(2));
		
		MapAnswerboxFEWA.put("padding-left", new Integer(3));
		MapAnswerboxFEWA.put("padding-right", new Integer(3));
		MapAnswerboxFEWA.put("padding-top", new Integer(1));
		MapAnswerboxFEWA.put("padding-bottom", new Integer(2));
		
		//answerboxFEWS
		MapAnswerboxFEWS.put("background-color", "#FFFFFF");
		
		MapAnswerboxFEWS.put("border-style", "solid");
		MapAnswerboxFEWS.put("border-color", "#314770");
		MapAnswerboxFEWS.put("border-width", new Integer(2));
		MapAnswerboxFEWS.put("border-radius", new Integer(3));
		
		MapAnswerboxFEWS.put("margin-left", new Integer(0));
		MapAnswerboxFEWA.put("margin-right", new Integer(0));
		MapAnswerboxFEWS.put("margin-top", new Integer(0));
		MapAnswerboxFEWS.put("margin-bottom", new Integer(2));
		
		MapAnswerboxFEWS.put("padding-left", new Integer(0));
		MapAnswerboxFEWS.put("padding-right", new Integer(0));
		MapAnswerboxFEWS.put("padding-top", new Integer(0));
		MapAnswerboxFEWS.put("padding-bottom", new Integer(0));
		
		//answerboxCombo
		MapAnswerboxCombo.put("background-color", "#FFFFFF");
		
		MapAnswerboxCombo.put("border-style", "solid");
		MapAnswerboxCombo.put("border-color", "#314770");
		MapAnswerboxCombo.put("border-width", new Integer(2));
		MapAnswerboxCombo.put("border-radius", new Integer(3));
		
		MapAnswerboxCombo.put("margin-left", new Integer(0));
		MapAnswerboxCombo.put("margin-right", new Integer(0));
		MapAnswerboxCombo.put("margin-top", new Integer(0));
		MapAnswerboxCombo.put("margin-bottom", new Integer(2));
		
		MapAnswerboxCombo.put("padding-left", new Integer(3));
		MapAnswerboxCombo.put("padding-right", new Integer(3));
		MapAnswerboxCombo.put("padding-top", new Integer(1));
		MapAnswerboxCombo.put("padding-bottom", new Integer(2));
		
		//checkButton
		MapCheckButton.put("background-color", "#314770");
		MapCheckButton.put("border-color", "#314770");
		MapCheckButton.put("foreground-color", "#FFFFFF");
		
		
	}
	
	public Object answerboxFEWA(String constantName) {
		return MapAnswerboxFEWA.get(constantName);
	}
	
	public Object answerboxFEWS(String constantName) {
		return MapAnswerboxFEWS.get(constantName);
	}
	
	public Object answerboxCombo(String constantName) {
		return MapAnswerboxCombo.get(constantName);
	}
	
	public Object checkButton(String constantName) {
		return MapCheckButton.get(constantName);
	}

}
