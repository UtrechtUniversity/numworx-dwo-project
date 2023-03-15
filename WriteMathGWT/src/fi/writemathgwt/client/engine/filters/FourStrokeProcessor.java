package fi.writemathgwt.client.engine.filters;

import fi.writemathgwt.client.engine.Stroke;

public class FourStrokeProcessor {
	
	private static boolean checkStrokes(Stroke stroke1, String teken1, Stroke stroke2, String teken2, Stroke stroke3, String teken3, Stroke stroke4, String teken4) {
		return FourStrokeDirFilter.checkDir(stroke1,teken1) && FourStrokeDirFilter.checkDir(stroke2,teken2) && FourStrokeDirFilter.checkDir(stroke3,teken3) && FourStrokeDirFilter.checkDir(stroke4,teken4);
	}
	
	public static String findFourStrokeTeken(Stroke stroke1, Stroke stroke2, Stroke stroke3, Stroke stroke4) {
		
		//E
		if (checkStrokes(stroke1,"EQ1",stroke2,"EQ2",stroke3,"EQ3", stroke4,"EQ4"))
			if(TwoStrokeProcessor.hasCloseDistance(stroke1,stroke2,20,0,2,0,2) 
					&& TwoStrokeProcessor.hasCloseDistance(stroke1,stroke3,20,12,28,0,2)
					&& TwoStrokeProcessor.hasCloseDistance(stroke1,stroke4,20,38,39,0,2))
				return "E";
	
		return null;
	}
	
	
	
	

}