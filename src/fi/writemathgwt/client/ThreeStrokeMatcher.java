package fi.writemathgwt.client;

public class ThreeStrokeMatcher {
	
	private static boolean checkStrokes(WriteObject woLastLast, String tekenLastLast, WriteObject woLast, String tekenLast, WriteObject wo, String teken) {
		return ThreeStrokeChecker.check(woLastLast,tekenLastLast) && ThreeStrokeChecker.check(woLast,tekenLast) && ThreeStrokeChecker.check(wo,teken);
	}
	
	public static WriteObject findThreeStroke(WriteObject woLastLast, WriteObject woLast, WriteObject wo) {

		if(".".equals(woLast.getTeken()))
			return null;
		
		if(".".equals(wo.getTeken())) {
			
			//if (StrokeChecker.check(woLast,"jH1")) 
			//	if(woLast.hasCloseDistance(10,wo,0,1,0,1))
			//		return new WriteObject("j", woLast, wo);
			return null;
		}
		
			
		
		//A
		if (checkStrokes(woLastLast,"AT1",woLast,"AT2",wo,"AT3"))
			if(woLastLast.hasCloseDistance(20,woLast,0,1,0,1) 
					&& woLastLast.hasCloseDistance(30,wo,6,14,0,1)
					&& woLast.hasCloseDistance(30,wo,6,14,19,20))
				return new WriteObject("A", woLastLast, woLast,  wo);
		
		//F
		if (checkStrokes(woLastLast,"FT1",woLast,"FT2",wo,"FT3"))
			if(woLastLast.hasCloseDistance(20,woLast,0,1,0,1) 
					&& woLastLast.hasCloseDistance(20,wo,6,14,0,1))
				return new WriteObject("F", woLastLast, woLast,  wo);
		
		//H
		if (checkStrokes(woLastLast,"HT1",woLast,"HT2",wo,"HT3"))
			if(woLastLast.hasCloseDistance(20,wo,6,14,0,1) 
					&& woLast.hasCloseDistance(20,wo,6,14,19,20))
				return new WriteObject("H", woLastLast, woLast,  wo);
						
		//pi
		if (checkStrokes(woLastLast,"piT1",woLast,"piT2",wo,"piT3"))
			if( woLastLast.hasCloseDistance(30,wo,0,1,2,7)
					&& woLast.hasCloseDistance(30,wo,0,1,13,18))
				return new WriteObject("pi", woLastLast, woLast,  wo);
		
		
		return null;
	}
	
	
	
	

}
