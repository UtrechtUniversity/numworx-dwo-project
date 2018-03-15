package fi.writemathgwt.client;

import java.util.logging.Logger;

public class OneStrokeMatcher {
	
	private static Logger logger = Logger.getLogger("OneStrokeMatcher");
	
	public static String[] tekens = { "(", ")", ">", "<", "y", "0","1",  "2", "3",  "4", "6","7", "8","9", "a","b","c","d", "e","g","h","l","m","n", "o","p","q","r","s","u","v","w","x","z","-"," of ","sqrt","k"};
	
	public static String findTeken(WriteObject wo) {
		String s = "{";
		String teken = null;
		for(int i = 0 ; i<tekens.length ; i++) {
			if(StrokeChecker.check(wo, tekens[i])) {
				//StrokeChecker.logging = true;
				StrokeChecker.check(wo, tekens[i]);
				teken = tekens[i];
				s = s+teken+",";
				
			}
		}
		logger.info(s);
		return teken;		
	}
	
	

}
