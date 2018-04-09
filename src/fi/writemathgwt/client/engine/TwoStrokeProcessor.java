package fi.writemathgwt.client.engine;

public class TwoStrokeProcessor {

	private static double averageHeight = 30;
	
	public static String findTwoStrokeTeken(Stroke stroke1, Stroke stroke2) {
		
		//y = \ (klein) + /
		if (checkStrokes(stroke1,"yH1",stroke2,"yH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,34,39,14,24) && hasCloseYDistance(stroke1, stroke2, 30,0,1,0,1))
				return "y";
				
		//x
			// x = ) + (
		if (checkStrokes(stroke1,"x1H1",stroke2,"x1H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,14,26,14,26))
				return "x";
			
		// x = / + \
		if (checkStrokes(stroke1,"x2H1",stroke2,"x2H2"))
			if(hasCloseDistance(stroke1, stroke2, 10,10,30,10,30))
				return "x";
			
		// x = \ + /
		if (checkStrokes(stroke1,"x2H2",stroke2,"x2H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,16,24,16,24))
				return "x";
				
		// 5 = 5 + -
		if (checkStrokes(stroke1,"5H1",stroke2,"5H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,4,0,4))
				return "5";
		
		// 5 = 5 + back
		if (checkStrokes(stroke1,"5H1",stroke2,"5H2+"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,4,36,39))
				return "5";
		
		//9				  
		if (checkStrokes(stroke1,"9H1",stroke2,"9H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,2) && hasCloseDistance(stroke1, stroke2, 20,38,39,38,39))
				return "9";
				
		//4				  
		if (checkStrokes(stroke1,"4H1",stroke2,"4H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,30,39,10,30))
				return "4";
				
		// 7 met extra streepje
		if (checkStrokes(stroke1,"7H1",stroke2,"7H2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,20,32,14,26))
				return "7";	
		
		// 7 met extra streepje back
		if (checkStrokes(stroke1,"7H1",stroke2,"7H2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,20,32,16,24))
				return "7";	
			
		//+
		if (checkStrokes(stroke1,"+H1",stroke2,"+H2"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				return "+";
		
		if (checkStrokes(stroke1,"+H1",stroke2,"+H2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				return "+";
		
		if (checkStrokes(stroke1,"+H2",stroke2,"+H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				return "+";
		
		if (checkStrokes(stroke1,"+H2+",stroke2,"+H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				return "+";
		
		// =
		if (checkStrokes(stroke1,"=H1",stroke2,"=H2") || checkStrokes(stroke1,"=H1+",stroke2,"=H2+"))
			if(hasCloseXDistance(stroke1, stroke2, 80,0,4,0,4) && hasCloseXDistance(stroke1, stroke2, 80,36,39,36,39) && hasCloseYDistance(stroke1, stroke2, 120,0,4,0,4))
				return "=";
		
		if (checkStrokes(stroke1,"=H1",stroke2,"=H2+") || checkStrokes(stroke1,"=H1+",stroke2,"=H2"))
			if(hasCloseXDistance(stroke1, stroke2, 40,0,4,36,39) && hasCloseXDistance(stroke1, stroke2, 40,39,39,0,0) && hasCloseYDistance(stroke1, stroke2, 80,0,4,0,4))
				return "=";
				
		// >=
		if (checkStrokes(stroke1,">=H1",stroke2,">=H2"))
			if(hasYDistance(stroke1, stroke2, 20,20,18,19,0,1) && hasCloseXDistance(stroke1, stroke2, 30,18,19,0,1))
				return "\\u2265";
		
		if (checkStrokes(stroke1,">=H1",stroke2,">=H2+"))
			if(hasYDistance(stroke1, stroke2, 20,20,18,19,18,19) && hasCloseXDistance(stroke1, stroke2, 30,18,19,0,1))
				return "\\u2265";
		
		// <=
		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2"))
			if(hasYDistance(stroke1, stroke2, 20,20,18,19,18,19) && hasCloseXDistance(stroke1, stroke2, 30,18,19,18,19))
				return "\\u2264";
				
		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2+"))
			if(hasYDistance(stroke1, stroke2, 20,20,18,19,0,1) &&hasCloseXDistance(stroke1, stroke2, 30,18,19,0,1))
				return "\\u2264";
		
		// f met extra streepje
		if (checkStrokes(stroke1,"fH1",stroke2,"fH2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,5,16,7,13))
				return "f";	
				
		// f met extra streepje back
		if (checkStrokes(stroke1,"fH1",stroke2,"fH2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,5,16,7,13))
				return "f";	
		
		// t met extra streepje
		if (checkStrokes(stroke1,"tH1",stroke2,"tH2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,5,16,7,13))
				return "t";	
						
		// t met extra streepje back
			if (checkStrokes(stroke1,"tH1",stroke2,"tH2+"))
				if(hasCloseDistance(stroke1, stroke2, 10,5,16,7,13))
					return "t";
			
		// A 
		if (checkStrokes(stroke1,"AH1",stroke2,"AH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,3,7,0,1) && hasCloseDistance(stroke1, stroke2, 20,13,17,19,20))
				return "A";	
		
		// B 
		if (checkStrokes(stroke1,"BH1",stroke2,"BH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,1,0,3) && hasCloseDistance(stroke1, stroke2, 30,19,20,17,20))
				return "B";	
		
		// B 
		if (checkStrokes(stroke1,"DH1",stroke2,"DH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,1,0,3) && hasCloseDistance(stroke1, stroke2, 30,19,20,17,20))
				return "D";
				
		// j 
		if (checkStrokes(stroke1,"jH1",stroke2,"jH2"))
			if(hasYDistance(stroke1, stroke2, -30,25,0,1,0,1) && hasCloseXDistance(stroke1, stroke2, 20,0,1,0,1))
				return "j";	
		
		// i
		if (checkStrokes(stroke1,"iH1",stroke2,"iH2"))
			if(hasYDistance(stroke1, stroke2, -30,25,0,1,0,1) && hasCloseXDistance(stroke1, stroke2, 20,0,1,0,1))
				return "i";
		
		// k
		if (checkStrokes(stroke1,"kH1",stroke2,"kH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,7,16,7,13) && hasYDistance(stroke1, stroke2, 35, 20,  0, 1, 0, 1))
				return "k";
		
		// K
		if (checkStrokes(stroke1,"KH1",stroke2,"KH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,7,14,7,13) &&hasYDistance(stroke1, stroke2, 0, 15,  0, 1, 0, 1))
				return "K";
		
		return null;
	}
	
	public static void setAverageHeight(double height) {
		averageHeight = height;
	}
	
	private static boolean checkStrokes(Stroke stroke1, String teken1, Stroke stroke2, String teken2) {
		return TwoStrokeDirFilter.checkDir(stroke1,teken1) && TwoStrokeDirFilter.checkDir(stroke2,teken2);
	}
		
	public static boolean hasCloseDistance(Stroke stroke1, Stroke stroke2, int distMin, int min1, int max1, int min2, int max2) {
		double dMin = distMin*averageHeight/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double dx = stroke1.getParsePoints().get(i).getX() - stroke2.getParsePoints().get(j).getX();
				double dy = stroke1.getParsePoints().get(i).getY() - stroke2.getParsePoints().get(j).getY();
				double d = Math.sqrt(dx*dx + dy*dy);
				distance = Math.min(distance, d);
			}
		}
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public static boolean hasCloseXDistance(Stroke stroke1, Stroke stroke2, int distMin,int min1, int max1, int min2, int max2) {
		double dMin = distMin*averageHeight/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d = Math.abs(stroke1.getParsePoints().get(i).getX() - stroke2.getParsePoints().get(j).getX());
				distance = Math.min(distance, d);
			}
		}
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public static boolean hasCloseYDistance(Stroke stroke1, Stroke stroke2, int distMin, int min1, int max1, int min2, int max2) {
		double dMin = distMin*averageHeight/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d = Math.abs(stroke1.getParsePoints().get(i).getY() - stroke2.getParsePoints().get(j).getY());
				distance = Math.min(distance, d);
			}
		}
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public static boolean hasYDistance(Stroke stroke1, Stroke stroke2, int dist, int distMin, int min1, int max1, int min2, int max2) {
		double dMin = distMin*averageHeight/100;
		dist = (int)(dist*averageHeight/100);
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d =   stroke2.getParsePoints().get(j).getY() - stroke1.getParsePoints().get(i).getY();
				distance = Math.min(distance, d);
			}
		}
		if(Math.abs(distance-dist) < dMin)
			return true;
		
		return false;
	}
}
