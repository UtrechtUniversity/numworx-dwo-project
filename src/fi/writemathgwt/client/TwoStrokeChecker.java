package fi.writemathgwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class TwoStrokeChecker {
	
	private static Logger logger = Logger.getLogger("StrokeChecker");

	public static boolean logging;
	
	private static HashMap samples;
	
	
	public static boolean check(WriteObject wo, String teken) {
		ArrayList<Boolean> checkers = new ArrayList<Boolean>();
		ArrayList<Boolean> checkersPlus = new ArrayList<Boolean>();
		
		//A
		if("AH1".equals(teken)) {
			checkers.add(wo.hasDirection(70, 20, 1, 10, 6));
			checkers.add(wo.hasDirection(290, 30, 10, 19, 6));
		}
		if("AH2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
		}
		
		//B
		if("BH1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 12));
		}
		if("BH2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 60, 1, 5, 3));
			checkers.add(wo.hasDirection(180, 90, 5, 10, 2));
			checkers.add(wo.hasDirection(0, 90, 8, 15, 2));
			checkers.add(wo.hasDirection(180, 90, 13, 19, 3));
			checkers.add(wo.hasDirection(270, 90, 1, 19, 10));
			checkers.add(!wo.hasDirection(90, 90, 7, 13, 4));
		}
		
		//D
		if("DH1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 12));
		}
		if("DH2".equals(teken)) {logging=true;
			checkers.add(wo.hasDirection(320, 30, 1, 7, 2));
			checkers.add(wo.hasDirection(300, 30, 3, 9, 2));
			checkers.add(wo.hasDirection(270, 40, 5, 15, 3));
			checkers.add(wo.hasDirection(240, 30, 11, 17, 2));
			checkers.add(wo.hasDirection(220, 30, 13, 19, 2));
			checkers.add(wo.hasDecreasingAngle(10,2,19));
		}
		
		//y
		if("yH1".equals(teken)) {
			checkers.add(wo.hasDirection(280, 30, 1, 15, 8));
			checkers.add(wo.hasDirection(300, 40, 10, 18, 4));
		}
		if("yH2".equals(teken)) {
			checkers.add(wo.hasDirection(240, 30, 1, 15, 8));
			checkers.add(wo.hasDirection(230, 40, 10, 19, 4));
		}
		
		//x
		if("x1H1".equals(teken)) {
			checkers.add(wo.hasDirection(320, 50, 1, 9, 4));
			checkers.add(wo.hasDirection(270, 90, 5, 15, 6));
			checkers.add(wo.hasDirection(220, 50, 11, 19, 4));
		}
		if("x1H2".equals(teken)) {
			checkers.add(wo.hasDirection(220, 50, 1, 9, 4));
			checkers.add(wo.hasDirection(270, 90, 5, 15, 6));
			checkers.add(wo.hasDirection(320, 50, 11, 19, 4));
		}
		if("x2H1".equals(teken)) {
			checkers.add(wo.hasDirection(310, 30, 1, 19, 10));
		}
		if("x2H2".equals(teken)) {
			checkers.add(wo.hasDirection(240, 30, 1, 19, 10));
		}
		
		//5
		if("5H1".equals(teken)) {
			checkers.add(wo.hasDirection(240, 60, 1, 4, 2));
			checkers.add(wo.hasDirection(0, 60, 5, 12, 3));
			checkers.add(wo.hasDirection(270, 50, 10, 19, 2));
			checkers.add(wo.hasDirection(180, 45, 15, 19, 2));
			//checkers.add(!wo.hasDirection(90, 70, 1, 3, 1));
			//checkers.add(!wo.hasDirection(90, 30, 9, 13, 1));
		}
		if("5H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 13));
		}
		if("5H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 30, 1, 19, 16));
		}
		
		//9
		if("9H1".equals(teken)) {
			checkers.add(wo.hasDirection(220, 50, 1, 9, 4));
			checkers.add(wo.hasDirection(270, 90, 5, 15, 6));
			checkers.add(wo.hasDirection(320, 50, 11, 19, 4));
		}
		if("9H2".equals(teken)) {
			checkers.add(wo.hasDirection(270, 30, 1, 19, 8));
			checkers.add(wo.hasDirection(180, 70, 12, 19, 2));
		}
		
		//4
		if("4H1".equals(teken)) {
			checkers.add(wo.hasDirection(240, 40, 1, 10, 5));
			checkers.add(wo.hasDirection(0, 30, 11, 19, 3));
		}
		if("4H2".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 12));
		}
		
		//+
		if("+H1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 16));
		}
		if("+H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if("+H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//7
		if("7H1".equals(teken)) {
			checkers.add(wo.hasDirection(0, 45, 1, 7, 3));
			checkers.add(wo.hasDirection(250, 20, 7, 19, 8));
		}
		if("7H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if("7H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//>=
		if(">=H1".equals(teken)) {
			checkers.add(wo.hasDirection(340, 25, 1, 10, 6));
			checkers.add(wo.hasDirection(200, 25, 10, 19, 6));
		}
		if(">=H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if(">=H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//<=
		if("<=H1".equals(teken)) {
			checkers.add(wo.hasDirection(200, 25, 1, 10, 7));
			checkers.add(wo.hasDirection(340, 25, 10, 19, 7));
		}
		if("<=H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if("<=H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//=
		if("=H1".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 14));
			checkers.add(!wo.hasDirection(300, 25, 1, 3, 1)); // ivm lang wortelteken
			checkers.add(!wo.hasDirection(70, 15, 2, 7, 2));
		}
		if("=H1+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 30, 1, 19, 16));
		}
		if("=H2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 14));
		}
		if("=H2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 30, 1, 19, 16));
		}
		
		//f
		if("fH1".equals(teken)) {
			checkers.add(wo.hasDirection(180, 45, 1, 5, 2));
			checkers.add(wo.hasDirection(260, 25, 5, 19, 10));
		}
		if("fH2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if("fH2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//t
		if("tH1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 25, 1, 15, 10));
			checkers.add(wo.hasDirection(0, 90, 15, 19, 2));
		}
		if("tH2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 20, 1, 19, 14));
		}
		if("tH2+".equals(teken)) {
			checkers.add(wo.hasDirection(180, 20, 1, 19, 16));
		}
		
		//j
		if("jH1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 40, 1, 15, 8));
			checkers.add(wo.hasDirection(180, 55, 15, 19, 2));
		}
		if("jH2".equals(teken)) {
			checkers.add(".".equals(wo.getTeken()));
		}
		
		//i
		if("iH1".equals(teken)) {logging = true;
			checkers.add(wo.hasDirection(260, 40, 1, 15, 8));
			checkers.add(wo.hasDirection(0, 90, 15, 19, 2));
		}
		if("iH2".equals(teken)) {
			checkers.add(".".equals(wo.getTeken()));
		}
		
		
		
		
		
		
		return combine(checkers,checkersPlus);
		
	}
	
	private static boolean combine(ArrayList<Boolean> checkers, ArrayList<Boolean> checkersPlus) {
		boolean check = true;
		boolean checkPlus = true;
		
		for(int i = 0 ; i<checkers.size() ; i++) {
			check = check && checkers.get(i);
			if(logging)
				logger.info("    checker "+i+" :"+checkers.get(i));
		}
		for(int i = 0 ; i<checkersPlus.size() ; i++) {
			checkPlus = checkPlus && checkersPlus.get(i);
			if(logging)
				logger.info("    checkerPlus "+i+" :"+checkersPlus.get(i));
		}
		if(checkers.size()==0) 
			check = false;
		if(checkersPlus.size()==0)	
			checkPlus = false;
		logging=false;
		return check || checkPlus;

	}
}