package nl.uu.fi.dwo.mobile.client.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateBasicConstants implements TemplateConstants {
	
	HashMap<String,Object> MapAnswerboxFEWA = new HashMap<String,Object>();
	
	HashMap<String,Object> MapAnswerboxFEWS = new HashMap<String,Object>();
	
	public TemplateBasicConstants() {
		//answerboxFEWA
		MapAnswerboxFEWA.put("background-color", "#FFFFFF");
		
		MapAnswerboxFEWA.put("border-style", "solid");
		MapAnswerboxFEWA.put("border-color", "#808080");
		MapAnswerboxFEWA.put("border-width", new Integer(1));
		MapAnswerboxFEWS.put("border-radius", new Integer(0));
		
		MapAnswerboxFEWA.put("margin-left", new Integer(1));
		MapAnswerboxFEWA.put("margin-right", new Integer(0));
		MapAnswerboxFEWA.put("margin-top", new Integer(0));
		MapAnswerboxFEWA.put("margin-bottom", new Integer(2));
		
		MapAnswerboxFEWA.put("padding-left", new Integer(0));
		MapAnswerboxFEWA.put("padding-right", new Integer(0));
		MapAnswerboxFEWA.put("padding-top", new Integer(1));
		MapAnswerboxFEWA.put("padding-bottom", new Integer(2));
		
		//answerboxFEWS
		MapAnswerboxFEWS.put("background-color", "#FFFFFF");
		
		MapAnswerboxFEWS.put("border-style", "solid");
		MapAnswerboxFEWS.put("border-color", "#808080");
		MapAnswerboxFEWS.put("border-width", new Integer(1));
		MapAnswerboxFEWS.put("border-radius", new Integer(0));
		
		MapAnswerboxFEWS.put("margin-left", new Integer(0));
		MapAnswerboxFEWS.put("margin-right", new Integer(0));
		MapAnswerboxFEWS.put("margin-top", new Integer(0));
		MapAnswerboxFEWS.put("margin-bottom", new Integer(2));
		
		MapAnswerboxFEWS.put("padding-left", new Integer(0));
		MapAnswerboxFEWS.put("padding-right", new Integer(0));
		MapAnswerboxFEWS.put("padding-top", new Integer(0));
		MapAnswerboxFEWS.put("padding-bottom", new Integer(0));
	}
	
	public Object answerboxFEWA(String constantName) {
		return MapAnswerboxFEWA.get(constantName);
	}
	
	public Object answerboxFEWS(String constantName) {
		return MapAnswerboxFEWS.get(constantName);
	}

}
