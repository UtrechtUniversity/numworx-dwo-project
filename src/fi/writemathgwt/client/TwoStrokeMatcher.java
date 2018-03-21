package fi.writemathgwt.client;

public class TwoStrokeMatcher {
	
	private static boolean checkStrokes(WriteObject woLast, String tekenLast, WriteObject wo, String teken) {
		return TwoStrokeChecker.check(woLast,tekenLast) && TwoStrokeChecker.check(wo,teken);
	}
	
	public static WriteObject findTwoStroke(WriteObject woLast, WriteObject wo) {

		if(".".equals(woLast.getTeken()))
			return null;
		
		//if(".".equals(wo.getTeken())) {
			
			//if (StrokeChecker.check(woLast,"jH1")) 
			//	if(woLast.hasCloseDistance(10,wo,0,1,0,1))
			//		return new WriteObject("j", woLast, wo);
		//	return null;
		//}
			
		
		//y = \ (klein) + /
		if (checkStrokes(woLast,"yH1",wo,"yH2"))
			if(woLast.hasCloseDistance(20,wo,17,19,7,12) && woLast.hasCloseYDistance(30,wo,0,1,0,1))
				return new WriteObject("y", woLast, wo);
				
		//x
			// x = ) + (
		if (checkStrokes(woLast,"x1H1",wo,"x1H2"))
			if(woLast.hasCloseDistance(20,wo,7,13,7,13))
				return new WriteObject("x", woLast, wo);
			
		// x = / + \
		if (checkStrokes(woLast,"x2H1",wo,"x2H2"))
			if(woLast.hasCloseDistance(10,wo,5,15,5,15))
				return new WriteObject("x", woLast, wo);
			
		// x = \ + /
		if (checkStrokes(woLast,"x2H2",wo,"x2H1"))
			if(woLast.hasCloseDistance(10,wo,8,12,8,12))
				return new WriteObject("x", woLast, wo);
				
		// 5 = 5 + -
		if (checkStrokes(woLast,"5H1",wo,"5H2"))
			if(woLast.hasCloseDistance(20,wo,0,2,0,2))
				return new WriteObject("5", woLast, wo);
		
		// 5 = 5 + back
		if (checkStrokes(woLast,"5H1",wo,"5H2+"))
			if(woLast.hasCloseDistance(20,wo,0,2,18,20))
				return new WriteObject("5", woLast, wo);
		
		//9				  
		if (checkStrokes(woLast,"9H1",wo,"9H2"))
			if(woLast.hasCloseDistance(20,wo,0,1,0,1) && woLast.hasCloseDistance(20,wo,19,20,5,10))
				return new WriteObject("9", woLast, wo);
				
		//4				  
		if (checkStrokes(woLast,"4H1",wo,"4H2"))
			if(woLast.hasCloseDistance(20,wo,15,19,5,15))
				return new WriteObject("4", woLast, wo);
				
		// 7 met extra streepje
		if (checkStrokes(woLast,"7H1",wo,"7H2")) 
			if(woLast.hasCloseDistance(10,wo,10,16,7,13))
				return new WriteObject("7", woLast, wo);	
		
		// 7 met extra streepje back
		if (checkStrokes(woLast,"7H1",wo,"7H2+"))
			if(woLast.hasCloseDistance(10,wo,10,16,8,12))
				return new WriteObject("7", woLast, wo);	
			
		//+
		if (checkStrokes(woLast,"+H1",wo,"+H2"))
			if(woLast.hasCloseDistance(10,wo,6,14,6,14))
				return new WriteObject("+", woLast, wo);
		
		if (checkStrokes(woLast,"+H1",wo,"+H2+"))
			if(woLast.hasCloseDistance(10,wo,6,14,6,14))
				return new WriteObject("+", woLast, wo);
		
		if (checkStrokes(woLast,"+H2",wo,"+H1"))
			if(woLast.hasCloseDistance(10,wo,6,14,6,14))
				return new WriteObject("+", woLast, wo);
		
		if (checkStrokes(woLast,"+H2+",wo,"+H1"))
			if(woLast.hasCloseDistance(10,wo,6,14,6,14))
				return new WriteObject("+", woLast, wo);
		
		// =
		if (checkStrokes(woLast,"=H1",wo,"=H2") || checkStrokes(woLast,"=H1+",wo,"=H2+"))
			if(woLast.hasCloseXDistance(80,wo,0,2,0,2) && woLast.hasCloseXDistance(80,wo,18,19,18,19) && woLast.hasCloseYDistance(120,wo,0,2,0,2))
				return new WriteObject("=", woLast, wo);
		
		if (checkStrokes(woLast,"=H1",wo,"=H2+") || checkStrokes(woLast,"=H1+",wo,"=H2"))
			if(woLast.hasCloseXDistance(40,wo,0,2,18,19) && woLast.hasCloseXDistance(40,wo,18,19,0,0) && woLast.hasCloseYDistance(80,wo,0,2,0,2))
				return new WriteObject("=", woLast, wo);
				
		// >=
		if (checkStrokes(woLast,">=H1",wo,">=H2"))
			if(woLast.hasYDistance(20,20,wo,18,19,0,1) && woLast.hasCloseXDistance(30,wo,18,19,0,1))
				return new WriteObject("\\u2265", woLast, wo);
		
		if (checkStrokes(woLast,">=H1",wo,">=H2+"))
			if(woLast.hasYDistance(20,20,wo,18,19,18,19) && woLast.hasCloseXDistance(30,wo,18,19,0,1))
				return new WriteObject("\\u2265", woLast, wo);
		
		// <=
		if (checkStrokes(woLast,"<=H1",wo,"<=H2"))
			if(woLast.hasYDistance(20,20,wo,18,19,18,19) && woLast.hasCloseXDistance(30,wo,18,19,18,19))
				return new WriteObject("\\u2264", woLast, wo);
				
		if (checkStrokes(woLast,"<=H1",wo,"<=H2+"))
			if(woLast.hasYDistance(20,20,wo,18,19,0,1) && woLast.hasCloseXDistance(30,wo,18,19,0,1))
				return new WriteObject("\\u2264", woLast, wo);
		
		// f met extra streepje
		if (checkStrokes(woLast,"fH1",wo,"fH2")) 
			if(woLast.hasCloseDistance(10,wo,5,16,7,13))
				return new WriteObject("f", woLast, wo);	
				
		// f met extra streepje back
		if (checkStrokes(woLast,"fH1",wo,"fH2+"))
			if(woLast.hasCloseDistance(10,wo,5,16,7,13))
				return new WriteObject("f", woLast, wo);	
		
		// t met extra streepje
		if (checkStrokes(woLast,"tH1",wo,"tH2")) 
			if(woLast.hasCloseDistance(10,wo,5,16,7,13))
				return new WriteObject("t", woLast, wo);	
						
		// t met extra streepje back
			if (checkStrokes(woLast,"tH1",wo,"tH2+"))
				if(woLast.hasCloseDistance(10,wo,5,16,7,13))
					return new WriteObject("t", woLast, wo);
			
		// A 
		if (checkStrokes(woLast,"AH1",wo,"AH2"))
			if(woLast.hasCloseDistance(20,wo,3,7,0,1) && woLast.hasCloseDistance(20,wo,13,17,19,20))
				return new WriteObject("A", woLast, wo);	
		
		// B 
		if (checkStrokes(woLast,"BH1",wo,"BH2"))
			if(woLast.hasCloseDistance(20,wo,0,1,0,3) && woLast.hasCloseDistance(30,wo,19,20,17,20))
				return new WriteObject("B", woLast, wo);	
		
		// B 
		if (checkStrokes(woLast,"DH1",wo,"DH2"))
			if(woLast.hasCloseDistance(20,wo,0,1,0,3) && woLast.hasCloseDistance(30,wo,19,20,17,20))
				return new WriteObject("D", woLast, wo);
				
		// j 
		if (checkStrokes(woLast,"jH1",wo,"jH2"))
			if(woLast.hasYDistance(-30,25,wo,0,1,0,1) && woLast.hasCloseXDistance(20,wo,0,1,0,1))
				return new WriteObject("j", woLast, wo);	
		
		// i
		if (checkStrokes(woLast,"iH1",wo,"iH2"))
			if(woLast.hasYDistance(-30,25,wo,0,1,0,1) && woLast.hasCloseXDistance(20,wo,0,1,0,1))
				return new WriteObject("i", woLast, wo);
		
		// k
		if (checkStrokes(woLast,"kH1",wo,"kH2"))
			if(woLast.hasCloseDistance(20,wo,7,16,7,13) && woLast.hasYDistance(35, 20, wo, 0, 1, 0, 1))
				return new WriteObject("k", woLast, wo);
		
		// K
		if (checkStrokes(woLast,"KH1",wo,"KH2"))
			if(woLast.hasCloseDistance(20,wo,7,14,7,13) && woLast.hasYDistance(0, 15, wo, 0, 1, 0, 1))
				return new WriteObject("K", woLast, wo);
		
		return null;
	}
	
	
	
	

}
