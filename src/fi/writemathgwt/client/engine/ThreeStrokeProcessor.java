package fi.writemathgwt.client.engine;

public class ThreeStrokeProcessor {
	
	private static boolean checkStrokes(Stroke stroke1, String teken1, Stroke stroke2, String teken2, Stroke stroke3, String teken3) {
		return ThreeStrokeDirFilter.checkDir(stroke1,teken1) && ThreeStrokeDirFilter.checkDir(stroke2,teken2) && ThreeStrokeDirFilter.checkDir(stroke3,teken3);
	}
	
	public static String findThreeStrokeTeken(Stroke stroke1, Stroke stroke2, Stroke stroke3) {

//		if(".".equals(woLast.getTeken()))
//			return null;
//		
//		if(".".equals(wo.getTeken())) {
//			return null;
//		}
		
		//A
		if (checkStrokes(stroke1,"AT1",stroke2,"AT2",stroke3,"AT3"))
			if(TwoStrokeProcessor.hasCloseDistance(stroke1,stroke2,20,0,1,0,1) 
					&& TwoStrokeProcessor.hasCloseDistance(stroke1,stroke3,30,6,14,0,1)
					&& TwoStrokeProcessor.hasCloseDistance(stroke2,stroke3,30,6,14,19,20))
				return "A";
		
		//F
		if (checkStrokes(stroke1,"FT1",stroke2,"FT2",stroke3,"FT3"))
			if(TwoStrokeProcessor.hasCloseDistance(stroke1,stroke2,20,0,1,0,1) 
					&& TwoStrokeProcessor.hasCloseDistance(stroke1,stroke3,20,6,14,0,1))
				return "F";
		
		//H
		if (checkStrokes(stroke1,"HT1",stroke2,"HT2",stroke3,"HT3"))
			if(TwoStrokeProcessor.hasCloseDistance(stroke1,stroke3,20,6,14,0,1) 
					&& TwoStrokeProcessor.hasCloseDistance(stroke2,stroke3,20,6,14,19,20))
				return "H";
						
		//pi
		if (checkStrokes(stroke1,"piT1",stroke2,"piT2",stroke3,"piT3"))
			if( TwoStrokeProcessor.hasCloseDistance(stroke1,stroke3,30,0,1,2,7)
					&& TwoStrokeProcessor.hasCloseDistance(stroke2,stroke3,30,0,1,13,18))
				return "pi";
		
		
		return null;
	}
	
	
	
	

}