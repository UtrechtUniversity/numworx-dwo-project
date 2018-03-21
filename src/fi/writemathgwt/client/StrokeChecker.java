package fi.writemathgwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class StrokeChecker {
	
	private static Logger logger = Logger.getLogger("StrokeChecker");

	private static boolean logging;
	
	private static HashMap samples;
	
	public static String checkerBooleans = "";
	
	
	public static String parse(WriteObject wo) {
		
//		samples = Samples20.init(2);
//		
//		logging = true;
//		boolean check = check(wo);
//		logging = false;
//		
//		logger.info("check Teken "+" : "+check);
//		
//		
//		Set keys = samples.keySet();
//		Iterator<String> iterator = (Iterator<String>)keys.iterator();
//	    while(iterator.hasNext()) 
//	    {
//	    	String key = iterator.next();
//	        int[] sample = (int[])samples.get(key);
//	    	WriteObject wSample = WriteObject.getWriteObjectFromSample(key, sample);
//	    	boolean checkSample = false;
//	    	if(sample.length==40)
//	    		checkSample = check(wSample);
//	    	if(checkSample)
//	    		logger.info("  check "+key+" : "+checkSample);
//	    }
	    
	    return "";
	}
	
	public static boolean check(WriteObject wo, String teken) {
		ArrayList<Boolean> checkers = new ArrayList<Boolean>();
		ArrayList<Boolean> checkersPlus = new ArrayList<Boolean>();
		
		//if("test".equals(teken)) {logging = true;
		//logger.info("has dAngle: " + wo.hasDAngle(330, 240, 0, 90, 1, 12));	//checkers.add(wo.hasDAngle(90, 180, 0, 90, 1, 10));
			
		//}
		if("-".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
			checkers.add(wo.hasDAngle(-30, 30, -30, 30, 6, 18));
		}
		if(">".equals(teken)) {
			checkers.add(wo.hasDirection(335, 30, 1, 10, 7));
			checkers.add(wo.hasDirection(205, 30, 10, 19, 7));
			checkers.add(wo.hasSharpAngle(-140, 30, 5, 15));
		}
		if("<".equals(teken)) {
			checkers.add(wo.hasDirection(205, 30, 1, 10, 7));
			checkers.add(wo.hasDirection(335, 30, 10, 19, 7));
			checkers.add(wo.hasSharpAngle(140, 30, 5, 15));
		}
		
		if("(".equals(teken)) {
			checkers.add(wo.hasDirection(230, 30, 1, 6, 1));
			checkers.add(wo.hasDirection(240, 40, 1, 9, 3));
			checkers.add(wo.hasDirection(270, 40, 5, 15, 6));
			checkers.add(wo.hasDirection(300, 40, 11, 19, 3));
			checkers.add(wo.hasDirection(310, 30, 14, 19, 1));
			checkers.add(wo.hasIncreasingAngle(20,2,19));
			checkers.add(wo.getBox().width<0.6*wo.getBox().height);
			//checkers.add(wo.hasDAngle(60, 140, -5, 20, 2, 18));
		}
		if(")".equals(teken)) {
			checkers.add(wo.hasDirection(300, 20, 1, 6, 1));
			checkers.add(wo.hasDirection(300, 30, 1, 9, 4));
			checkers.add(wo.hasDirection(270, 40, 5, 15, 6));
			checkers.add(wo.hasDirection(240, 30, 11, 19, 4));
			checkers.add(wo.hasDirection(240, 20, 14, 19, 1));
			checkers.add(wo.hasDecreasingAngle(10,2,19));
		}
		if("0".equals(teken)) {
			checkers.add(wo.hasDirection(180, 90, 1, 5, 2));
			checkers.add(wo.hasDirection(270, 90, 1, 10, 4));
			checkers.add(wo.hasDirection(0, 90, 5, 15, 4));
			checkers.add(wo.hasDirection(90, 90, 10, 19, 4));
			checkers.add(wo.hasDirection(180, 90, 15, 19, 2));
			
			checkers.add(!wo.hasDirection(0, 60, 16, 19, 2));
			
			checkers.add(wo.hasCloseDistance(40, wo, 0, 1, 19, 20));
			checkers.add(wo.hasIncreasingAngle(10,2,19));
			checkers.add(wo.hasDAngle(250, 450, -5, 120, 1, 19));
		}
		if("1".equals(teken)) {
			checkers.add(wo.hasDirection(265, 20, 1, 19, 12));
			checkers.add(wo.hasDAngle(-20, 20, -20, 20, 3, 18));
			
			checkersPlus.add(wo.hasDirection(45, 45, 1, 4, 2));
			checkersPlus.add(wo.hasDirection(265, 20, 1, 19, 13));
			checkersPlus.add(wo.hasDAngle(-20, 20, -20, 20, 6, 18));
		}
		if("2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 80, 1, 5, 3));
			checkers.add(wo.hasDirection(240, 40, 5, 17, 5));
			checkers.add(wo.hasDirection(0, 40, 15, 19, 2));
			checkers.add(!wo.hasDirection(90, 90, 7, 13, 4));
			
			//checkers.add(wo.hasDecreasingAngle(20,2,7));
			checkers.add(wo.hasDAngle(-250, -80, -120, 0, 2, 10));
		}
		if("3".equals(teken)) {
			checkers.add(wo.hasDirection(0, 60, 1, 5, 3));
			checkers.add(wo.hasDirection(180, 90, 5, 10, 2));
			checkers.add(wo.hasDirection(0, 90, 8, 15, 2));
			checkers.add(wo.hasDirection(180, 90, 13, 19, 3));
			checkers.add(wo.hasDirection(270, 90, 1, 19, 10));
			checkers.add(!wo.hasDirection(90, 90, 7, 13, 4));
			//checkers.add(wo.hasSharpAngle(135, 45, 5, 15));
		}
		if("4".equals(teken)) {
			checkers.add(wo.hasDirection(250, 30, 1, 6, 4));
			checkers.add(wo.hasDirection(0, 60, 4, 15, 3));
			checkers.add(wo.hasDirection(90, 90, 8, 16, 2));
			checkers.add(wo.hasDirection(260, 30, 10, 19, 3));
			checkers.add(!wo.hasDirection(210, 20, 17, 19, 1));
			checkers.add(!wo.hasDirection(60, 20, 17, 19, 1));
			checkers.add(!wo.hasSharpAngle(180, 30, 4, 10));
			checkers.add(wo.hasYDistance(35, 22, wo, 4, 10, 19, 20));
			checkers.add(wo.hasMinCusps(10,19));
		}
		if("6".equals(teken)) {
			checkers.add(wo.dMinBoxTop(0, 10, 0, 1));
			checkers.add(wo.dMinBoxBottom(0, 10, 10, 15));
			checkers.add(wo.hasDirection(240, 50, 1, 12, 7));
//			checkers.add(wo.hasDirection(0, 90, 8, 15, 3));
//			checkers.add(wo.hasDirection(90, 90, 12, 19, 2));
//			checkers.add(wo.hasDirection(180, 90, 15, 19, 1));
//			checkers.add(wo.hasCloseDistance(25, wo, 19, 20, 5, 10));
//			checkers.add(!wo.hasSharpAngle(180, 30, 5, 15));
			checkers.add(wo.hasDAngle(0, 80, -15, 140, 2, 10));
			checkers.add(wo.hasDAngle(210, 400, -15, 140, 2, 19));
			checkers.add(wo.hasCloseDistance(30, wo, 19, 20, 5, 10));
		}
		if("7".equals(teken)) {
			checkers.add(!wo.hasDirection(300, 30, 1, 7, 3));
			checkers.add(wo.hasDirection(0, 45, 1, 7, 3));
			checkers.add(wo.hasDirection(250, 20, 7, 19, 8));
			checkers.add(!wo.hasDirection(0, 50, 15, 19, 2));
		}
		
		if("8".equals(teken)) {
			checkers.add(wo.hasDirection(180, 90, 1, 3, 1));
			checkers.add(wo.hasDirection(270, 90, 1, 6, 2));
			checkers.add(wo.hasDirection(0, 90, 3, 8, 2));
			checkers.add(wo.hasDirection(90, 90, 5, 10, 2));
			checkers.add(wo.hasDirection(180, 90, 8, 13, 2));
			checkers.add(wo.hasDirection(90, 90, 10, 15, 2));
			checkers.add(wo.hasDirection(0, 90, 12, 17, 2));
			checkers.add(wo.hasDirection(270, 90, 15, 19, 2));
			checkers.add(wo.hasDirection(180, 90, 17, 19, 1));
			
			checkers.add(wo.hasCloseDistance(40, wo, 0, 1, 18, 19));
		}
		if("9".equals(teken)) {
			
			checkers.add(wo.hasDirection(180, 60, 1, 4, 2));
			checkers.add(wo.hasDirection(270, 60, 2, 5, 2));
			checkers.add(wo.hasDirection(0, 90, 4, 9, 2));
			checkers.add(wo.hasDirection(70, 60, 5, 11, 2));
			checkers.add(wo.hasDirection(270, 30, 10, 19, 4));
			checkers.add(wo.hasDirection(180, 70, 16, 19, 1));
			checkers.add(wo.hasCloseDistance(25, wo, 0, 1, 7, 13));
			checkers.add(!wo.hasDirection(90, 90, 13, 19, 2));
		}
		if("a".equals(teken)) {
//			checkers.add(wo.hasDirection(180, 90, 1, 5, 3));
//			checkers.add(wo.hasDirection(270, 90, 2, 7, 4));
//			checkers.add(wo.hasDirection(0, 90, 6, 11, 3));
//			checkers.add(wo.hasDirection(80, 50, 8, 13, 3));
//			checkers.add(wo.hasDirection(270, 40, 13, 19, 3));
//			
//			checkers.add(wo.hasSharpAngle(180, 30, 10, 19));
//			checkers.add(wo.hasCloseDistance(30, wo, 0, 1, 8, 16));
//			checkers.add(wo.hasCloseYDistance(25, wo, 3, 10, 19, 20));
//			checkers.add(!wo.hasYDistance(-50, 25, wo, 0, 1, 12, 20));
			
			checkers.add(wo.hasCloseDistance(30, wo, 0, 1, 8, 16));
			checkers.add(wo.dMinBoxTop(0, 20, 0, 3));
			checkers.add(wo.dMinBoxTop(0, 40, 8, 15));
			checkers.add(wo.dMinBoxBottom(0, 35, 6, 10));
			checkers.add(wo.dMinBoxBottom(0, 35, 18, 19));
			checkers.add(wo.hasDAngle(200, 360, -5, 180, 1, 12));
		}
		if("b".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 10, 6));
			checkers.add(wo.hasDirection(80, 60, 8, 15, 3));
			checkers.add(wo.hasDirection(0, 90, 11, 16, 2));
			checkers.add(wo.hasDirection(270, 90, 13, 19, 2));
			checkers.add(wo.hasDirection(180, 90, 15, 19, 2));
			checkers.add(!wo.hasDirection(0, 90, 16, 19, 2));
			checkers.add(wo.hasCloseDistance(25, wo, 19, 20, 5, 12));
			checkers.add(wo.hasYDistance(50, 30, wo, 0, 1, 8, 15));
			checkers.add(wo.hasSharpAngle(180, 30, 5, 15));
		}
		if("c".equals(teken)) {
			checkers.add(wo.hasDirection(180, 30, 1, 4, 2));
			checkers.add(wo.hasDirection(270, 90, 4, 16, 6));
			checkers.add(wo.hasDirection(0, 30, 15, 19, 3));
			
			checkers.add(wo.hasIncreasingAngle(10,2, 18));
			checkers.add(!wo.hasSharpAngle(140, 30, 5, 15));
		}
		if("d".equals(teken)) {
			checkers.add(wo.hasDirection(180, 90, 1, 4, 2));
			checkers.add(wo.hasDirection(270, 90, 2, 6, 2));
			checkers.add(wo.hasDirection(0, 90, 5, 9, 2));
			checkers.add(wo.hasDirection(80, 40, 7, 13, 4));
			checkers.add(wo.hasDirection(260, 40, 14, 19, 4));
			
			checkers.add(wo.hasCloseDistance(20, wo, 0, 1, 8, 13));
			checkers.add(wo.hasSharpAngle(180, 20, 7, 16));
			checkers.add(wo.hasYDistance(-50, 20, wo, 0, 1, 12, 20));
		}
		if("e".equals(teken)) {
			checkers.add(wo.hasDirection(0, 90, 1, 6, 3));
			checkers.add(wo.hasDirection(90, 90, 2, 8, 3));
			checkers.add(wo.hasDirection(180, 90, 8, 14, 3));
			checkers.add(wo.hasDirection(270, 90, 8, 16, 5));
			checkers.add(wo.hasDirection(0, 90, 14, 19, 3));
			
			checkers.add(wo.hasCloseDistance(30, wo, 0, 2, 7, 16));
			checkers.add(wo.hasIncreasingAngle(0,2, 16));
		}
		
		if("g".equals(teken)) {
			
			checkers.add(wo.hasDirection(180, 90, 1, 4, 2));
			checkers.add(wo.hasDirection(270, 90, 2, 5, 2));
			checkers.add(wo.hasDirection(0, 90, 3, 9, 2));
			checkers.add(wo.hasDirection(90, 90, 5, 11, 1));
			checkers.add(wo.hasDirection(270, 30, 8, 19, 4));
			checkers.add(wo.hasDirection(160, 70, 14, 19, 2));
			checkers.add(wo.hasDirection(90, 90, 17, 19, 2));
			
			checkers.add(wo.hasCloseDistance(20, wo, 0, 1, 6, 13));
			checkers.add(wo.hasIncreasingAngle(5,2, 6));
		}
		if("h".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 10, 6));
			checkers.add(wo.hasDirection(80, 60, 8, 15, 3));
			checkers.add(wo.hasDirection(0, 90, 11, 16, 2));
			checkers.add(wo.hasDirection(270, 90, 13, 19, 2));
			checkers.add(!wo.hasCloseDistance(25, wo, 19, 20, 5, 12));
			checkers.add(wo.hasDecreasingAngle(20, 12, 19));
			checkers.add(wo.hasSharpAngle(180, 30, 5, 15));
			checkers.add(wo.hasYDistance(55, 30, wo, 0, 1, 7, 20));
			checkers.add(wo.hasCloseYDistance(20, wo, 6, 11, 19, 20));
		}
		if("k".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 7, 5));
			checkers.add(wo.hasDirection(60, 30, 8, 12, 3));
			checkers.add(wo.hasDirection(240, 30, 11, 16, 1));
			checkers.add(wo.hasDirection(320, 40, 15, 19, 2));
			checkers.add(wo.hasCloseDistance(20, wo, 4, 7, 14, 17));
			checkers.add(wo.hasSharpAngle(180, 40, 5, 12));
		}
		if("l".equals(teken)) {
			checkers.add(wo.hasDirection(260, 20, 1, 15, 10));
			checkers.add(wo.hasDirection(0, 40, 15, 19, 3));
			checkers.add(wo.hasIncreasingAngle(20,15, 19));
		}
		if("m".equals(teken)) {
			checkers.add(wo.hasDirection(270, 30, 1, 5, 2));
			checkers.add(wo.hasDirection(80, 30, 4, 9, 2));
			checkers.add(wo.hasDirection(270, 30, 7, 13, 2));
			checkers.add(wo.hasDirection(80, 30, 11, 17, 2));
			checkers.add(wo.hasDirection(270, 30, 15, 19, 2));
		}
		if("m".equals(teken)) {
			checkers.add(wo.hasDirection(270, 30, 1, 5, 2));
			checkers.add(wo.hasDirection(80, 30, 4, 9, 2));
			checkers.add(wo.hasDirection(270, 30, 7, 13, 2));
			checkers.add(wo.hasDirection(80, 30, 11, 17, 2));
			checkers.add(wo.hasDirection(270, 30, 15, 19, 2));
		}
		if("n".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 6, 4));
			checkers.add(wo.hasDirection(80, 30, 6, 12, 3));
			checkers.add(wo.hasDirection(260, 30, 12, 19, 3));
			checkers.add(wo.hasDirection(0, 90, 1, 19, 4));
			checkers.add(wo.hasSharpAngle(-180, 30, 0, 9));
			checkers.add(!wo.hasSharpAngle(180, 30, 8, 17));
			checkers.add(wo.hasDecreasingAngle(20, 9, 17));
			checkers.add(wo.hasYDistance(0, 25, wo, 0, 1, 7, 20));
			checkers.add(wo.hasYDistance(0, 25, wo, 5, 9, 19, 20));
		}
		if("o".equals(teken)) {
			checkers.add(wo.hasDirection(180, 90, 1, 5, 2));
			checkers.add(wo.hasDirection(270, 90, 1, 10, 4));
			checkers.add(wo.hasDirection(0, 90, 5, 15, 4));
			checkers.add(wo.hasDirection(90, 90, 10, 19, 4));
			checkers.add(wo.hasDirection(180, 90, 15, 19, 1));
			checkers.add(wo.hasDirection(0, 60, 17, 19, 2));
			
			checkers.add(wo.hasCloseDistance(25, wo, 0, 1, 16, 17));
			checkers.add(wo.hasIncreasingAngle(10,2,16));
		}
		if("p".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 7, 5));
			checkers.add(wo.hasDirection(80, 60, 6, 12, 5 ));
			checkers.add(wo.hasDirection(0, 90, 11, 16, 2));
			checkers.add(wo.hasDirection(270, 90, 13, 19, 2));
			checkers.add(wo.hasDirection(180, 90, 15, 19, 2));
			
			checkers.add(wo.hasCloseDistance(25, wo, 19, 20, 2, 4));
			checkers.add(wo.hasSharpAngle(180, 30, 4, 9));
		}
		if("q".equals(teken)) {
			
			checkers.add(wo.hasDirection(180, 60, 1, 4, 2));
			checkers.add(wo.hasDirection(270, 60, 2, 5, 2));
			checkers.add(wo.hasDirection(0, 90, 4, 9, 2));
			checkers.add(wo.hasDirection(90, 60, 5, 11, 2));
			checkers.add(wo.hasDirection(270, 30, 10, 19, 4));
			checkers.add(wo.hasDirection(50, 20, 16, 19, 2));
			checkers.add(wo.hasCloseDistance(25, wo, 0, 1, 6, 13));
			checkers.add(!wo.hasDirection(160, 70, 14, 19, 2));
			
		}
		if("r".equals(teken)) {
			checkers.add(wo.hasDirection(250, 30, 1, 7, 5));
			checkers.add(wo.hasDirection(60, 30, 7, 16, 3));
			checkers.add(wo.hasDirection(0, 30, 14, 19, 2));
			checkers.add(wo.hasSharpAngle(180, 40, 5, 15));
			checkers.add(wo.hasCloseYDistance(20, wo, 0, 1, 14, 19));
		}
		if("s".equals(teken)) {
			checkers.add(wo.hasDirection(180, 70, 1, 5, 3));
			checkers.add(wo.hasDirection(270, 70, 3, 8, 3));
			checkers.add(wo.hasDirection(330, 40, 4, 12, 4));
			checkers.add(wo.hasDirection(270,70, 10, 16, 3));
			checkers.add(wo.hasDirection(180, 50, 15, 19, 3));
			checkers.add(!wo.hasSharpAngle(180, 30, 5, 15));
			
		}
		if("u".equals(teken)) {
			checkers.add(!wo.hasDirection(170, 60, 1, 6, 2));
			checkers.add(wo.hasDirection(260, 30, 1, 6, 3));
			checkers.add(wo.hasDirection(0, 90, 5,12, 3));
			checkers.add(wo.hasDirection(90, 90, 8, 13, 3));
			checkers.add(wo.hasDirection(260, 30, 12, 19, 3));
			
			//checkers.add(wo.hasIncreasingAngle(20,2,9));
			checkers.add(wo.hasDAngle(120, 200, -10, 120, 2, 10));
			checkers.add(wo.hasSharpAngle(180, 30, 8, 17));
			//checkers.add(wo.hasCloseDistance(20, wo, 0, 1, 8, 16));
			checkers.add(wo.hasCloseYDistance(20, wo, 3, 10, 19, 20));
			checkers.add(wo.hasCloseYDistance(30, wo, 0, 1, 8, 17));
			//checkers.add(!wo.hasYDistance(-50, 25, wo, 0, 1, 12, 20));
		}
		if("v".equals(teken)) {
			checkers.add(wo.hasDirection(270, 60, 1, 10, 5));
			checkers.add(wo.hasDirection(60, 40, 10, 19, 6));
			checkers.add(wo.hasCloseYDistance(30, wo, 0, 1, 19, 20));
			checkers.add(wo.hasIncreasingAngle(30,7,13));
			//checkers.add(!wo.hasCloseDistance(35, wo, 0, 1, 19, 20));
			checkers.add(wo.dMinBoxLeft(0, 20, 0, 0));
			checkers.add(!wo.hasDirection(315, 20, 1, 10, 7));
			checkers.add(!wo.hasDirection(35, 20, 10, 19, 7));
			checkers.add(!wo.hasDirection(270, 50, 10, 13, 2));
		}
		if("w".equals(teken)) {
			checkers.add(wo.hasDirection(270, 60, 1, 6, 3));
			checkers.add(wo.hasDirection(90, 60, 5, 10, 2));
			checkers.add(wo.hasDirection(270, 60, 10, 15, 2));
			checkers.add(wo.hasDirection(90, 60, 8, 15, 3));
			
			checkers.add(wo.hasCloseYDistance(25, wo, 0, 1, 19, 20));
			checkers.add(wo.hasIncreasingAngle(20,3,9));
			checkers.add(wo.hasIncreasingAngle(20,12,20));
			checkers.add(!wo.hasYDistance(-50, 25, wo, 0, 1, 12, 20));
		}
		if("x".equals(teken)) {
			checkers.add(wo.hasDirection(0, 90, 1, 3, 1));
			checkers.add(wo.hasDirection(270, 90, 2, 6, 3));
			checkers.add(wo.hasDirection(180, 90, 3, 7, 2));
			checkers.add(wo.hasDirection(45, 90, 7, 12, 4));
			checkers.add(wo.hasDirection(180, 90, 12, 16, 2));
			checkers.add(wo.hasDirection(270, 90, 12, 19, 3));
			checkers.add(wo.hasDirection(0, 90, 16, 19, 2));
			checkers.add(wo.hasCloseYDistance(30, wo, 0, 1, 9, 14));
			checkers.add(!wo.hasCloseYDistance(30, wo, 0, 1, 19, 20));
			checkers.add(wo.hasDecreasingAngle(-5,1,5));
			
		}
		if("y".equals(teken)) {
			checkers.add(wo.hasDirection(290, 30, 1, 5, 3));
			checkers.add(wo.hasDirection(60, 30, 5, 10, 3));
			checkers.add(wo.hasDirection(240, 40, 10, 19, 6));
			checkers.add(wo.hasSharpAngle(180, 30, 5, 15));
		}
		if("z".equals(teken)) {
			checkers.add(wo.hasDirection(0, 60, 1, 5, 3));
			checkers.add(wo.hasDirection(240, 30, 5, 17, 5));
			checkers.add(wo.hasDirection(0, 40, 15, 19, 2));
			checkers.add(!wo.hasDirection(90, 90, 7, 13, 4));
			checkers.add(wo.hasSharpAngle(-150, 30, 2, 10));
			checkers.add(wo.hasMinCusps(0,10));
		}
		if(" of ".equals(teken)) {
			checkers.add(wo.hasDirection(315, 20, 1, 10, 7));
			checkers.add(wo.hasDirection(45, 20, 10, 19, 7));
		}
		if("sqrt".equals(teken)) {
			checkers.add(wo.hasDirection(300, 35, 1, 3, 1));
			checkers.add(wo.hasDirection(70, 25, 3, 12, 4));
			checkers.add(wo.hasDirection(0, 20, 10, 19, 5));
			
			checkersPlus.add(wo.hasDirection(300, 25, 1, 3, 1));
			checkersPlus.add(wo.hasDirection(70, 23, 2, 7, 2));
			checkersPlus.add(wo.hasDirection(0, 20, 7, 19, 8));
		}
		if("/".equals(teken)) { 
			checkers.add(wo.hasDirection(230, 20, 1, 19, 16));
			checkers.add(wo.hasDAngle(-20, 20, -20, 20, 2, 18));
		}
		
		
		return combine(checkers,checkersPlus);
		
	}
	
	private static boolean combine(ArrayList<Boolean> checkers, ArrayList<Boolean> checkersPlus) {
		boolean check = true;
		boolean checkPlus = true;
		checkerBooleans = "";
		
		for(int i = 0 ; i<checkers.size() ; i++) {
			check = check && checkers.get(i);
//			if(logging)
//				logger.info("    checker "+i+" :"+checkers.get(i));
			checkerBooleans = checkerBooleans+"-checker "+i+" :"+checkers.get(i)+"\n";
		}
		checkerBooleans = checkerBooleans+"\n";
		for(int i = 0 ; i<checkersPlus.size() ; i++) {
			checkPlus = checkPlus && checkersPlus.get(i);
//			if(logging)
//				logger.info("    checkerPlus "+i+" :"+checkersPlus.get(i));
			checkerBooleans = checkerBooleans+"checkerPlus "+i+" :"+checkersPlus.get(i)+"\n";
		}
		if(checkers.size()==0) 
			check = false;
		if(checkersPlus.size()==0)	
			checkPlus = false;
		logging=false;
		return check || checkPlus;

	}
	
	public static String getCheckerBooleans() {
		return checkerBooleans;
	}
			
		
	
}