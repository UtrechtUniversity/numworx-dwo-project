package fi.writemathgwt.client.engine.filters;

import java.util.ArrayList;
import java.util.logging.Logger;

import fi.writemathgwt.client.engine.ReferenceSamples;
import fi.writemathgwt.client.engine.ReferenceTwoStrokeSamples;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;
import fi.writemathgwt.client.engine.StrokeMatcher;

public class TwoStrokeProcessor {

	private static Logger logger = Logger.getLogger("TwoStrokeProcessor");

	private static double averageHeight = 50;
	
	public static String findTwoStrokeTeken(StrokeContainer strokeContainer, Stroke stroke1, Stroke stroke2) {
		ArrayList<String> foundTekens = findTwoStrokeTekens( strokeContainer,  stroke1,  stroke2);
		logger.info("foundTekens:"+foundTekens );
		if(foundTekens.size()>0)
		return foundTekens.get(0);
		return null;
	}
	
	public static void addAndSort(boolean findScore, String teken, ArrayList<String> gevondenTekens, ArrayList<Double> scores, Stroke stroke1, String teken1, Stroke stroke2, String teken2) {
		if(!findScore) {
			gevondenTekens.add(teken);
			scores.add(0.0);
			return;
		}
		double score1 = Math.min(getMatchScoreDir(stroke1, teken1,0) , getMatchScoreDir(stroke1, teken1 ,5));
		double score2 = Math.min(getMatchScoreDir(stroke2, teken2,0) , getMatchScoreDir(stroke2, teken2 ,5));
		double score = score1 + score2;
		if(gevondenTekens.size()==0) {
			gevondenTekens.add(0, teken);
			scores.add(0,score);
		}
		else {
			for(int j=0 ; j<gevondenTekens.size(); j++) {
				if(score < scores.get(j)) {
					gevondenTekens.add(j, teken);
					scores.add(j,score);
					break;					
				}
				else if (j==gevondenTekens.size()-1) {
					gevondenTekens.add(teken);
					scores.add(score);
					break;
				}
			}
		}
	}
	
	public static void addAndSort(String teken, ArrayList<String> gevondenTekens, ArrayList<Double> scores, Stroke stroke1, String teken1, Stroke stroke2, String teken2) {
		addAndSort(true, teken, gevondenTekens, scores, stroke1,  teken1,  stroke2,  teken2);
	}
	
	public static ArrayList<String> findTwoStrokeTekens(StrokeContainer strokeContainer, Stroke stroke1, Stroke stroke2) {
		ArrayList<String> foundTekens = new ArrayList<String>();
		ArrayList<Double> scores = new ArrayList<Double>();
		
		averageHeight = strokeContainer.averageHeight;
		
		String teken1 = StrokeMatcher.findTekenRaw(strokeContainer,stroke1);
		String teken2 = StrokeMatcher.findTekenRaw(strokeContainer,stroke2);
		
		//y = \ (klein) + /
		if (checkStrokes(stroke1,"yH1",stroke2,"yH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,34,39,14,24) && hasCloseYDistance(stroke1, stroke2, 30,0,1,0,1))
				addAndSort("y", foundTekens, scores, stroke1,"yH1",stroke2,"yH2");
				
		//x
			// x = ) + (
		if (checkStrokes(stroke1,"x1H1",stroke2,"x1H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,14,26,14,26))
				addAndSort("x", foundTekens, scores, stroke1,"x1H1",stroke2,"x1H2");
			
		// x = / + \
		if (checkStrokes(stroke1,"x2H1",stroke2,"x2H2"))
			if(hasCloseDistance(stroke1, stroke2, 10,14,26,14,26))
				addAndSort("x", foundTekens, scores, stroke1,"x2H1",stroke2,"x2H2");
				
		// x = \ + /
		if (checkStrokes(stroke1,"x2H2",stroke2,"x2H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,16,24,16,24))
				addAndSort("x", foundTekens, scores, stroke1,"x2H2",stroke2,"x2H1");
		
		// x = \ + /(2e stroke start onder
				if (checkStrokes(stroke1,"x2H1",stroke2,"x2H2+"))
					if(hasCloseDistance(stroke1, stroke2, 10,16,24,16,24))
						addAndSort("x", foundTekens, scores, stroke1,"x2H1",stroke2,"x2H2+");
			
		// x = \ + /
		if (checkStrokes(stroke1,"labdaH1",stroke2,"labdaH2"))
			if(hasCloseDistance(stroke1, stroke2, 10,16,28,0,2))
				addAndSort("λ", foundTekens, scores, stroke1,"labdaH1",stroke2,"labdaH2");
				
		// 5 = 5 + -
		if (checkStrokes(stroke1,"5H1",stroke2,"5H2"))
			if(hasCloseDistance(stroke1, stroke2, 30,0,4,0,4) && stroke2.width()>stroke1.width()/3)
				addAndSort("5", foundTekens, scores, stroke1,"5H1",stroke2,"5H2");
			
		// 5 = 5 + back
		if (checkStrokes(stroke1,"5H1",stroke2,"5H2+"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,4,36,39))
				addAndSort("5", foundTekens, scores, stroke1,"5H1",stroke2,"5H2+");
			
		//9				  
		if (checkStrokes(stroke1,"9H1",stroke2,"9H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,2) && hasCloseDistance(stroke1, stroke2, 20,38,39,38,39))
				addAndSort("9", foundTekens, scores, stroke1,"9H1",stroke2,"9H2");
				
		//4				  
		if (checkStrokes(stroke1,"4H1",stroke2,"4H2"))
			if(hasCloseDistance(stroke1, stroke2, 20,30,39,10,30))
				addAndSort("4", foundTekens, scores, stroke1,"4H1",stroke2,"4H2");
				
		// 7 met extra streepje
		if (checkStrokes(stroke1,"7H1",stroke2,"7H2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,20,32,14,26))
				addAndSort("7", foundTekens, scores, stroke1,"7H1",stroke2,"7H2");
			
		// 7 met extra streepje back
		if (checkStrokes(stroke1,"7H1",stroke2,"7H2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,20,32,16,24))
				addAndSort("7", foundTekens, scores, stroke1,"7H1",stroke2,"7H2+");
			
		//+
		if (checkStrokes(stroke1,"+H1",stroke2,"+H2"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				if(stroke2.getParsePointsbox().width<1.8*stroke1.getParsePointsbox().height)
					addAndSort("+", foundTekens, scores, stroke1,"+H1",stroke2,"+H2");
				
		if (checkStrokes(stroke1,"+H1",stroke2,"+H2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				if(stroke2.getParsePointsbox().width<1.8*stroke1.getParsePointsbox().height)
					addAndSort("+", foundTekens, scores, stroke1,"+H1",stroke2,"+H2+");
				
		if (checkStrokes(stroke1,"+H2",stroke2,"+H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				addAndSort("+", foundTekens, scores, stroke1,"+H2",stroke2,"+H1");
			
		if (checkStrokes(stroke1,"+H2+",stroke2,"+H1"))
			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
				addAndSort("+", foundTekens, scores, stroke1,"+H2+",stroke2,"+H1");
			
		// =
		if (checkStrokes(stroke1,"=H1",stroke2,"=H2") || checkStrokes(stroke1,"=H1+",stroke2,"=H2+"))
			if(hasCloseXDistance(stroke1, stroke2, 80,0,4,0,4) && hasCloseXDistance(stroke1, stroke2, 80,36,39,36,39) && hasCloseYDistance(stroke1, stroke2, 120,0,4,0,4))
				//if(hasCloseXDistance(stroke1, stroke2, 80,0,4,0,4) && hasCloseXDistance(stroke1, stroke2, 80,36,39,36,39) && hasCloseYDistance(stroke1, stroke2, 120,0,4,0,4))
					addAndSort("=", foundTekens, scores, stroke1,"=H1",stroke2,"=H2");
			
		if (checkStrokes(stroke1,"=H1",stroke2,"=H2+") || checkStrokes(stroke1,"=H1+",stroke2,"=H2"))
			if(hasCloseXDistance(stroke1, stroke2, 40,0,4,36,39) && hasCloseXDistance(stroke1, stroke2, 40,39,39,0,0) && hasCloseYDistance(stroke1, stroke2, 80,0,4,0,4))
				addAndSort("=", foundTekens, scores, stroke1,"=H1",stroke2,"=H2+");
				
		// >=
		if (checkStrokes(stroke1,">=H1",stroke2,">=H2"))
			if(hasYDistance(stroke1, stroke2, 20,20,36,39,0,2) && hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
				addAndSort(">=", foundTekens, scores, stroke1,">=H1",stroke2,">=H2");
			
		if (checkStrokes(stroke1,">=H1",stroke2,">=H2+"))
			if(hasYDistance(stroke1, stroke2, 20,20,36,39,36,39) && hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
				addAndSort(">=", foundTekens, scores, stroke1,">=H1",stroke2,">=H2+");
			
		// <=
		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2"))
			if(hasYDistance(stroke1, stroke2, 20,20,36,39,36,39) && hasCloseXDistance(stroke1, stroke2, 30,36,39,36,39))
				addAndSort("<=", foundTekens, scores, stroke1,"<=H1",stroke2,"<=H2");
				
		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2+"))
			if(hasYDistance(stroke1, stroke2, 20,20,36,39,0,2) &&hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
				addAndSort("<=", foundTekens, scores, stroke1,"<=H1",stroke2,"<=H2+");
			
		// f met extra streepje
		if (checkStrokes(stroke1,"fH1",stroke2,"fH2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,10,32,0,26))
				if(stroke2.getParsePointsbox().width<stroke1.getParsePointsbox().height)
					addAndSort("f", foundTekens, scores, stroke1,"fH1",stroke2,"fH2");
				
		// f met extra streepje back
		if (checkStrokes(stroke1,"fH1",stroke2,"fH2+"))
			if(hasCloseDistance(stroke1, stroke2, 10,10,32,14,39))
				if(stroke2.getParsePointsbox().width<stroke1.getParsePointsbox().height)
					addAndSort("f", foundTekens, scores, stroke1,"fH1",stroke2,"fH2+");
				
		// t met extra streepje
		if (checkStrokes(stroke1,"tH1",stroke2,"tH2")) 
			if(hasCloseDistance(stroke1, stroke2, 10,10,32,0,26))
				if(stroke2.width()<stroke1.getParsePointsbox().height)
					addAndSort("t", foundTekens, scores, stroke1,"tH1",stroke2,"tH2");
						
		// t met extra streepje back
			if (checkStrokes(stroke1,"tH1",stroke2,"tH2+"))
				if(hasCloseDistance(stroke1, stroke2, 10,10,32,14,39))
					if(stroke2.getParsePointsbox().width<stroke1.getParsePointsbox().height)
						addAndSort("t", foundTekens, scores, stroke1,"tH1",stroke2,"tH2+");
					
		// A 
		if (checkStrokes(stroke1,"AH1",stroke2,"AH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,6,14,0,2) && hasCloseDistance(stroke1, stroke2, 20,26,34,38,39))
				addAndSort("A", foundTekens, scores, stroke1,"AH1",stroke2,"AH2");
			
		// B 
		if (checkStrokes(stroke1,"BH1",stroke2,"BH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,38,39,34,39))
				addAndSort("B", foundTekens, scores, stroke1,"BH1",stroke2,"BH2");
			
		// B 
		if (checkStrokes(stroke1,"DH1",stroke2,"DH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,38,39,34,39))
				addAndSort("D", foundTekens, scores, stroke1,"DH1",stroke2,"DH2");
				
		// P 
		if (checkStrokes(stroke1,"PH1",stroke2,"PH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,15,25,34,39))
				addAndSort("P", foundTekens, scores, stroke1,"PH1",stroke2,"PH2");
			
		// b 
			if (checkStrokes(stroke1,"bH1",stroke2,"bH2"))
				if(hasCloseDistance(stroke1, stroke2, 20,15,25,0,6) && hasCloseDistance(stroke1, stroke2, 30,34,39,31,39))
					addAndSort("b", foundTekens, scores, stroke1,"bH1",stroke2,"bH2");
				
		// j 
		if (checkStrokes(stroke1,"jH1",stroke2,"jH2"))
			if(hasYDistance(stroke1, stroke2, -30,50,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 30,0,2,0,2)
				&& stroke2.width()<4 && stroke2.height()<4)
				addAndSort("j", foundTekens, scores, stroke1,"jH1",stroke2,"jH2");
			
		// i
		if (checkStrokes(stroke1,"iH1",stroke2,"iH2"))
			if(hasYDistance(stroke1, stroke2, -40,50,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 30,0,2,0,2)
				&& stroke2.width()<4 && stroke2.height()<4)
				addAndSort("i", foundTekens, scores, stroke1,"iH1",stroke2,"iH2");
			
		if (checkStrokes(stroke1,"iH1+",stroke2,"iH2"))
			if(hasYDistance(stroke1, stroke2, -40,25,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 30,0,2,0,2)
					&& stroke2.getParsePointsbox().width<4 && stroke2.getParsePointsbox().height<4)
				addAndSort("i", foundTekens, scores, stroke1,"iH1+",stroke2,"iH2");
		// k
		if (checkStrokes(stroke1,"kH1",stroke2,"kH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,14,32,14,26) && hasYDistance(stroke1, stroke2, 35, 20,  0, 2, 0, 2))
				addAndSort("k", foundTekens, scores, stroke1,"kH1",stroke2,"kH2");
			
		// K
		if (checkStrokes(stroke1,"KH1",stroke2,"KH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,14,28,14,26) && hasYDistance(stroke1, stroke2, 0, 15,  0, 2, 0, 2))
				addAndSort("K", foundTekens, scores, stroke1,"KH1",stroke2,"KH2");
			
		// R
		if (checkStrokes(stroke1,"RH1",stroke2,"RH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,15,25,20,30) && hasYDistance(stroke1, stroke2, 0, 25,  38, 39, 38, 39))
				addAndSort("R", foundTekens, scores, stroke1,"RH1",stroke2,"RH2");
			
		// T
		if (checkStrokes(stroke1,"TH1",stroke2,"TH2"))
			if(hasCloseDistance(stroke1, stroke2, 20,0,2,14,26))
				if(stroke2.width() > stroke1.height()/2)
					addAndSort("T", foundTekens, scores, stroke1,"TH1",stroke2,"TH2");
			
		// Q
		if ("0".equals(teken1) && TwoStrokeDirFilter.checkDir(stroke2,"QH2") )
			if(hasCloseDistance(stroke1, stroke2, 20,14,28,10,30))
				if(stroke2.getParsePointsbox().width<stroke1.getParsePointsbox().width/2 && stroke2.getParsePointsbox().height<stroke1.getParsePointsbox().width/2)
					addAndSort(false,"Q", foundTekens, scores, stroke1,"0",stroke2,"QH2");
				
		
		// theta
		if ("0".equals(teken1) && "-".equals(teken2))
			if(hasCloseDistance(stroke1, stroke2, 20,5,15,0,2) &&hasCloseDistance(stroke1, stroke2, 20, 25,  35, 38, 39))
				addAndSort(false,"θ", foundTekens, scores, stroke1,"0",stroke2,"-");
			
		if ("0".equals(teken1) && "back".equals(teken2))
			if(hasCloseDistance(stroke1, stroke2, 20,25,35,0,2) &&hasCloseDistance(stroke1, stroke2, 20, 5,  15, 38, 39))
				addAndSort(false, "θ", foundTekens, scores, stroke1,"0",stroke2,"back");
		
		
		// <- pijltje links
		if ("-".equals(teken1) && ("<".equals(teken2) || "\u27e8".equals(teken2)))
			if(hasCloseDistance(stroke1, stroke2, 10,0,1,15,25))
				addAndSort(false,"\\u2190", foundTekens, scores, stroke1,"-",stroke2,"<");
			
		if ("back".equals(teken1) && ("<".equals(teken2) || "\u27e8".equals(teken2)))
			if(hasCloseDistance(stroke1, stroke2, 10,38,39,15,25))
				addAndSort(false,"\\u2190", foundTekens, scores, stroke1,"back",stroke2,"<");
			
		
		// ;
			if (".".equals(teken1) && ",".equals(teken2))
				if(hasCloseXDistance(stroke1, stroke2, 10,0,1,0,1) && hasYDistance(stroke1, stroke2, (int)averageHeight, 30,  0, 2, 0, 2))
					addAndSort(false, ";", foundTekens, scores, stroke1,".",stroke2,",");
		
			return	foundTekens;
	}
	
//	public static void setAverageHeight(double height) {
//		averageHeight = height;
//	}
	
	private static boolean checkStrokes(Stroke stroke1, String teken1, Stroke stroke2, String teken2) {
		return TwoStrokeDirFilter.checkDir(stroke1,teken1) && TwoStrokeDirFilter.checkDir(stroke2,teken2);
	}
		
	public static boolean hasCloseDistance(Stroke stroke1, Stroke stroke2, int distMin, int min1, int max1, int min2, int max2) {
		double relHeight = Math.max(stroke1.height(), stroke2.height());
		double dMin = distMin*relHeight/100;
		double distance = 1000;
		for(int i=min1 ; i<max1+1 ; i++) {
			for(int j=min2 ; j<max2+1 ; j++) {
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
	
	private static boolean hasCloseXDistance(Stroke stroke1, Stroke stroke2, int distMin,int min1, int max1, int min2, int max2) {
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
	
	private static boolean hasCloseYDistance(Stroke stroke1, Stroke stroke2, int distMin, int min1, int max1, int min2, int max2) {
		
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
	
	private static boolean hasYDistance(Stroke stroke1, Stroke stroke2, int dist, int distMin, int min1, int max1, int min2, int max2) {
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
	
	public static double getMatchScoreDir(Stroke stroke, String teken, double rot) {
		Stroke sample = ReferenceTwoStrokeSamples.getReferenceStroke(teken);//ts.getTestStroke(0, teken);//
		if(sample==null || stroke==null)
			return 2000;
		int margin = 3;
		double matchScore = 0;
		for(int i=1 ; i<39 ; i++) {
			double minDifAngle = 360;
			
			double angleStroke = stroke.getAngles()[i]-rot;
			int jMin = 0;
			for(int j=Math.max(i-margin,0) ; j<i+margin+1 && j<39; j++) {
				double angleSample = sample.getAngles()[j];
				if(angleStroke-angleSample>=180)
					angleStroke -= 360;
				if(angleStroke-angleSample<-180)
					angleStroke += 360;
				double difAngle = Math.abs(angleStroke-angleSample);
				if(difAngle<minDifAngle) {
					minDifAngle = Math.min(minDifAngle, difAngle);
					jMin = j;
				}
			}
			matchScore	+= minDifAngle;
		}
		return matchScore;
	}
	
//	public static String findTwoStrokeTeken(StrokeContainer strokeContainer, Stroke stroke1, Stroke stroke2) {
//		averageHeight = strokeContainer.averageHeight;
//		
//		String teken1 = StrokeMatcher.findTekenRaw(strokeContainer,stroke1);
//		String teken2 = StrokeMatcher.findTekenRaw(strokeContainer,stroke2);
//		
//		//y = \ (klein) + /
//		if (checkStrokes(stroke1,"yH1",stroke2,"yH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,34,39,14,24) && hasCloseYDistance(stroke1, stroke2, 30,0,1,0,1))
//				return "y";addAndSort()
//				
//		//x
//			// x = ) + (
//		if (checkStrokes(stroke1,"x1H1",stroke2,"x1H2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,14,26,14,26))
//				return "x";
//			
//		// x = / + \
//		if (checkStrokes(stroke1,"x2H1",stroke2,"x2H2"))
//			if(hasCloseDistance(stroke1, stroke2, 10,14,26,14,26))
//				return "x";
//			
//		// x = \ + /
//		if (checkStrokes(stroke1,"x2H2",stroke2,"x2H1"))
//			if(hasCloseDistance(stroke1, stroke2, 10,16,24,16,24))
//				return "x";
//		
//		// x = \ + /
//		if (checkStrokes(stroke1,"labdaH1",stroke2,"labdaH2"))
//			if(hasCloseDistance(stroke1, stroke2, 10,16,28,0,2))
//				return "λ";
//				
//		// 5 = 5 + -
//		if (checkStrokes(stroke1,"5H1",stroke2,"5H2"))
//			if(hasCloseDistance(stroke1, stroke2, 30,0,4,0,4))
//				return "5";
//		
//		// 5 = 5 + back
//		if (checkStrokes(stroke1,"5H1",stroke2,"5H2+"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,4,36,39))
//				return "5";
//		
//		//9				  
//		if (checkStrokes(stroke1,"9H1",stroke2,"9H2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,2) && hasCloseDistance(stroke1, stroke2, 20,38,39,38,39))
//				return "9";
//				
//		//4				  
//		if (checkStrokes(stroke1,"4H1",stroke2,"4H2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,30,39,10,30))
//				return "4";
//				
//		// 7 met extra streepje
//		if (checkStrokes(stroke1,"7H1",stroke2,"7H2")) 
//			if(hasCloseDistance(stroke1, stroke2, 10,20,32,14,26))
//				return "7";	
//		
//		// 7 met extra streepje back
//		if (checkStrokes(stroke1,"7H1",stroke2,"7H2+"))
//			if(hasCloseDistance(stroke1, stroke2, 10,20,32,16,24))
//				return "7";	
//			
//		//+
//		if (checkStrokes(stroke1,"+H1",stroke2,"+H2"))
//			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
//				if(stroke2.getParsePointsbox().width<1.5*averageHeight)
//				return "+";
//		
//		if (checkStrokes(stroke1,"+H1",stroke2,"+H2+"))
//			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
//				if(stroke2.getParsePointsbox().width<1.5*averageHeight)
//				return "+";
//		
//		if (checkStrokes(stroke1,"+H2",stroke2,"+H1"))
//			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
//				return "+";
//		
//		if (checkStrokes(stroke1,"+H2+",stroke2,"+H1"))
//			if(hasCloseDistance(stroke1, stroke2, 10,12,28,12,28))
//				return "+";
//		
//		// =
//		if (checkStrokes(stroke1,"=H1",stroke2,"=H2") || checkStrokes(stroke1,"=H1+",stroke2,"=H2+"))
//			if(hasCloseXDistance(stroke1, stroke2, 80,0,4,0,4) && hasCloseXDistance(stroke1, stroke2, 80,36,39,36,39) && hasCloseYDistance(stroke1, stroke2, 120,0,4,0,4))
//				return "=";
//		
//		if (checkStrokes(stroke1,"=H1",stroke2,"=H2+") || checkStrokes(stroke1,"=H1+",stroke2,"=H2"))
//			if(hasCloseXDistance(stroke1, stroke2, 40,0,4,36,39) && hasCloseXDistance(stroke1, stroke2, 40,39,39,0,0) && hasCloseYDistance(stroke1, stroke2, 80,0,4,0,4))
//				return "=";
//				
//		// >=
//		if (checkStrokes(stroke1,">=H1",stroke2,">=H2"))
//			if(hasYDistance(stroke1, stroke2, 20,20,36,39,0,2) && hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
//				return ">=";
//		
//		if (checkStrokes(stroke1,">=H1",stroke2,">=H2+"))
//			if(hasYDistance(stroke1, stroke2, 20,20,36,39,36,39) && hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
//				return ">=";
//		
//		// <=
//		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2"))
//			if(hasYDistance(stroke1, stroke2, 20,20,36,39,36,39) && hasCloseXDistance(stroke1, stroke2, 30,36,39,36,39))
//				return "<=";
//				
//		if (checkStrokes(stroke1,"<=H1",stroke2,"<=H2+"))
//			if(hasYDistance(stroke1, stroke2, 20,20,36,39,0,2) &&hasCloseXDistance(stroke1, stroke2, 30,36,39,0,2))
//				return "<=";
//		
//		// f met extra streepje
//		if (checkStrokes(stroke1,"fH1",stroke2,"fH2")) 
//			if(hasCloseDistance(stroke1, stroke2, 10,10,32,0,26))
//				if(stroke2.getParsePointsbox().width<averageHeight)
//					return "f";	
//				
//		// f met extra streepje back
//		if (checkStrokes(stroke1,"fH1",stroke2,"fH2+"))
//			if(hasCloseDistance(stroke1, stroke2, 10,10,32,14,39))
//				if(stroke2.getParsePointsbox().width<averageHeight)
//					return "f";	
//		
//		// t met extra streepje
//		if (checkStrokes(stroke1,"tH1",stroke2,"tH2")) 
//			if(hasCloseDistance(stroke1, stroke2, 10,10,32,0,26))
//				if(stroke2.getParsePointsbox().width<averageHeight)
//					return "t";	
//						
//		// t met extra streepje back
//			if (checkStrokes(stroke1,"tH1",stroke2,"tH2+"))
//				if(hasCloseDistance(stroke1, stroke2, 10,10,32,14,39))
//					if(stroke2.getParsePointsbox().width<averageHeight)
//						return "t";
//			
//		// A 
//		if (checkStrokes(stroke1,"AH1",stroke2,"AH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,6,14,0,2) && hasCloseDistance(stroke1, stroke2, 20,26,34,38,39))
//				return "A";	
//		
//		// B 
//		if (checkStrokes(stroke1,"BH1",stroke2,"BH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,38,39,34,39))
//				return "B";	
//		
//		// B 
//		if (checkStrokes(stroke1,"DH1",stroke2,"DH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,38,39,34,39))
//				return "D";
//				
//		// P 
//		if (checkStrokes(stroke1,"PH1",stroke2,"PH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,15,25,34,39))
//				return "P";
//		
//		// b 
//			if (checkStrokes(stroke1,"bH1",stroke2,"bH2"))
//				if(hasCloseDistance(stroke1, stroke2, 20,15,25,0,6) && hasCloseDistance(stroke1, stroke2, 30,34,39,31,39))
//					return "b";
//				
//		// j 
//		if (checkStrokes(stroke1,"jH1",stroke2,"jH2"))
//			if(hasYDistance(stroke1, stroke2, -30,25,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 20,0,2,0,2))
//				return "j";	
//		
//		// i
//		if (checkStrokes(stroke1,"iH1",stroke2,"iH2"))
//			if(hasYDistance(stroke1, stroke2, -40,25,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 30,0,2,0,2)
//				&& stroke2.getParsePointsbox().width<4 && stroke2.getParsePointsbox().height<4)
//				return "i";
//		
//		if (checkStrokes(stroke1,"iH1+",stroke2,"iH2"))
//			if(hasYDistance(stroke1, stroke2, -40,25,0,2,0,2) && hasCloseXDistance(stroke1, stroke2, 30,0,2,0,2)
//					&& stroke2.getParsePointsbox().width<4 && stroke2.getParsePointsbox().height<4)
//				return "i";
//		// k
//		if (checkStrokes(stroke1,"kH1",stroke2,"kH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,14,32,14,26) && hasYDistance(stroke1, stroke2, 35, 20,  0, 2, 0, 2))
//				return "k";
//		
//		// K
//		if (checkStrokes(stroke1,"KH1",stroke2,"KH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,14,28,14,26) && hasYDistance(stroke1, stroke2, 0, 15,  0, 2, 0, 2))
//				return "K";
//		
//		// R
//		if (checkStrokes(stroke1,"RH1",stroke2,"RH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,0,6) && hasCloseDistance(stroke1, stroke2, 30,15,25,20,30) && hasYDistance(stroke1, stroke2, 0, 25,  38, 39, 38, 39))
//				return "R";
//		
//		// T
//		if (checkStrokes(stroke1,"TH1",stroke2,"TH2"))
//			if(hasCloseDistance(stroke1, stroke2, 20,0,2,14,26))
//				return "T";
//		
//		// Q
//		if ("0".equals(teken1) && TwoStrokeDirFilter.checkDir(stroke2,"QH2") )
//			if(hasCloseDistance(stroke1, stroke2, 20,14,28,10,30))
//				if(stroke2.getParsePointsbox().width<averageHeight/2 && stroke2.getParsePointsbox().height<averageHeight/2)
//					return "Q";
//		
//		// theta
//		if ("0".equals(teken1) && "-".equals(teken2))
//			if(hasCloseDistance(stroke1, stroke2, 20,5,15,0,2) &&hasCloseDistance(stroke1, stroke2, 20, 25,  35, 38, 39))
//				return "θ";
//		if ("0".equals(teken1) && "back".equals(teken2))
//			if(hasCloseDistance(stroke1, stroke2, 20,25,35,0,2) &&hasCloseDistance(stroke1, stroke2, 20, 5,  15, 38, 39))
//				return "θ";
//		
//		
//		
//		// <- pijltje links
//		if ("-".equals(teken1) && ("<".equals(teken2) || "\u27e8".equals(teken2)))
//			if(hasCloseDistance(stroke1, stroke2, 10,0,1,15,25))
//				return "\u2190";
//		if ("back".equals(teken1) && ("<".equals(teken2) || "\u27e8".equals(teken2)))
//			if(hasCloseDistance(stroke1, stroke2, 10,38,39,15,25))
//				return "\u2190";
//		
//		// ;
//			if (".".equals(teken1) && ",".equals(teken2))
//				if(hasCloseXDistance(stroke1, stroke2, 10,0,1,0,1) && hasYDistance(stroke1, stroke2, (int)averageHeight, 30,  0, 2, 0, 2))
//					return ";";
//		
//			
//		return null;
//	}
}
