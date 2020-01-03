package fi.writemathgwt.client.engine;

import java.util.ArrayList;

import fi.writemathgwt.client.engine.filters.StrokeDAngleFilter;
import fi.writemathgwt.client.engine.filters.StrokeDirFilter;
import fi.writemathgwt.client.engine.filters.StrokeLocDAngleFilter;
import fi.writemathgwt.client.engine.filters.StrokeLocFilter;

public class StrokeMatcher {
	
	protected static String checkLogString;
	protected static ArrayList<Double> scores;
	protected static String[] tekens = ReferenceSamples.getTekens();
	
	public static boolean match(Stroke wo, String key, boolean filter) {
		if(key.equals(findMatches(wo, filter)[0]))
			return true;
		return false;
	}
	
	public static String[] findMatches(Stroke wo) {
		return findMatches(wo, true);
	}
	
	public static String[] findMatches(Stroke wo, boolean filter) {
		ArrayList<String> gevondenTekens = new ArrayList<String>();
		scores = new ArrayList<Double>();
		for(int i = 0 ; i<tekens.length ; i++) {
			double score = Math.min(getMatchScoreDir(wo, tekens[i],0) , getMatchScoreDir(wo, tekens[i],5));
			if(gevondenTekens.size()==0) {
				gevondenTekens.add(0, tekens[i]);
				scores.add(0,score);
			}
			else {
				for(int j=0 ; j<gevondenTekens.size(); j++) {
					if(score < scores.get(j)) {
						gevondenTekens.add(j, tekens[i]);
						scores.add(j,score);
						break;					
					}
					else if (j==gevondenTekens.size()-1) {
						gevondenTekens.add(tekens[i]);
						scores.add(score);
						break;
					}
				}
			}
		}
		//StrokeContainer.logger.info("3a. "+System.currentTimeMillis());
		if(filter) {
			String bestUnfiltered = gevondenTekens.get(0);
			for(int i = 0 ; i<gevondenTekens.size()  ; i++) {
				if(scores.get(0)>2000) {
					gevondenTekens.add(0,bestUnfiltered);
					return gevondenTekens.toArray(new String[20]);
				}
				if(!StrokeLocFilter.checkLoc(wo, gevondenTekens.get(i))) {
					gevondenTekens.remove(i);
					scores.remove(i);
					i--;
				}
				else if(!StrokeDAngleFilter.checkDAngle(wo, gevondenTekens.get(i))) {
					gevondenTekens.remove(i);
					scores.remove(i);
					i--;
				}
				else if(!StrokeDirFilter.checkDir(wo, gevondenTekens.get(i))) {
					gevondenTekens.remove(i);
					scores.remove(i);
					i--;
				}
				else if(!StrokeLocDAngleFilter.checkLocDAngle(wo, gevondenTekens.get(i))) {
					gevondenTekens.remove(i);
					scores.remove(i);
					i--;
				}
				else break;
			}
		}
		return gevondenTekens.toArray(new String[20]);		
	}
	
	public static String findTeken(StrokeContainer strokeContainer, Stroke stroke) {
		String teken = stroke.getOneStrokeTeken();
		if(teken==null) {
			teken = findMatches(stroke)[0];
			stroke.setOneStrokeTeken(teken);
		}
		if(teken==null)
			return null;
		if(stroke.getParsePointsbox().width<5 && stroke.getParsePointsbox().height<5)
			teken = ".";
		if("sqrt_1".equals(teken) && stroke.getParsePointsbox().height<15)
			teken = "-";
		int index = teken.indexOf("_");
		if(index>0)
			teken = teken.substring(0,index);
		if("of".equals(teken))
			teken = " of ";
		teken = filterOpMaat(strokeContainer, stroke, teken);
		return teken;
	}
	
	public static String findTekenRaw(StrokeContainer strokeContainer, Stroke stroke) {
		String teken = stroke.getOneStrokeTeken();
		if(teken==null) {
			teken = findMatches(stroke)[0];
			stroke.setOneStrokeTeken(teken);
		}
		if(stroke.getParsePointsbox().width<5 && stroke.getParsePointsbox().height<5)
			teken = ".";
		if("sqrt_1".equals(teken) && stroke.getParsePointsbox().height<15)
			teken = "-";
		//teken = filterOpMaat(strokeContainer, stroke, teken);
		return teken;
	}
	
	public static String filterOpMaat(StrokeContainer strokeContainer, Stroke stroke, String teken) {
		if("/".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight)
			teken = "1";
		if("\\".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight)
			teken = "1";
		if(")".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight/2)
			teken = ",";
		if("\u27e9".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight/2)
			teken = ",";
		if(")".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight && 3*stroke.getParsePointsbox().width<stroke.getParsePointsbox().height)
			teken = "1";
		if("(".equals(teken) && stroke.getParsePointsbox().height<strokeContainer.averageHeight && 3*stroke.getParsePointsbox().width<stroke.getParsePointsbox().height)
			teken = "1";
		if("l".equals(teken) && stroke.getParsePointsbox().height<1.2*strokeContainer.averageHeight && 3*stroke.getParsePointsbox().width<stroke.getParsePointsbox().height)
			teken = "1";
		if("c".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "C";
		if("o".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "O";
		if("s".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "S";
		if("u".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "U";
		if("v".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "V";
		if("w".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "W";
//		if("x".equals(teken) && stroke.getParsePointsbox().height>1.3*strokeContainer.averageHeight)
//			teken = "X";
//		if("y".equals(teken) && stroke.getParsePointsbox().height>1.3*strokeContainer.averageHeight)
//			teken = "Y";
		if("z".equals(teken) && stroke.getParsePointsbox().height>1.2*strokeContainer.averageHeight)
			teken = "Z";
		return teken;
	}
	
	public static double getMatchScoreDir(Stroke stroke, String teken, double rot) {
		Stroke sample = ReferenceSamples.getReferenceStroke(teken);//ts.getTestStroke(0, teken);//
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
}

