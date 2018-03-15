package fi.writemathgwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class ThreeStrokeChecker {
	
	private static Logger logger = Logger.getLogger("StrokeChecker");

	public static boolean logging;
	
	private static HashMap samples;
	
	
	
	
	public static boolean check(WriteObject wo, String teken) {
		ArrayList<Boolean> checkers = new ArrayList<Boolean>();
		ArrayList<Boolean> checkersPlus = new ArrayList<Boolean>();
		
		//A
		if("AT1".equals(teken)) {
			checkers.add(wo.hasDirection(240, 30, 1, 19, 10));
		}
		if("AT2".equals(teken)) {
			checkers.add(wo.hasDirection(290, 30, 1, 19, 10));
		}
		if("AT3".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
			checkers.add(!wo.hasDirection(300, 25, 1, 3, 1)); // ivm lang wortelteken
			checkers.add(!wo.hasDirection(70, 15, 2, 7, 2));
		}
		
		//F
		if("FT1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 10));
		}
		if("FT2".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
		}
		if("FT3".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
		}
		
		//pi
		if("piT1".equals(teken)) {
			checkers.add(wo.hasDirection(265, 20, 1, 19, 16));
		}
		if("piT2".equals(teken)) {
			checkers.add(wo.hasDirection(265, 20, 1, 19, 16));
		}
		if("piT3".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
		}
		
		//H
		if("HT1".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 10));
		}
		if("HT2".equals(teken)) {
			checkers.add(wo.hasDirection(260, 30, 1, 19, 10));
		}
		if("HT3".equals(teken)) {
			checkers.add(wo.hasDirection(0, 30, 1, 19, 12));
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