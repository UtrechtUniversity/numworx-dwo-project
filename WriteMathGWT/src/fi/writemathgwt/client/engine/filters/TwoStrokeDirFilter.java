package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class TwoStrokeDirFilter {
	
	private static HashMap<String,int[][]> cachedDirData = new HashMap<String,int[][]>();
	
	public static void setCachedDirData(String key, int[][] dirData) {
		cachedDirData.put(key, dirData);
	}
	
	public static boolean checkDir(Stroke stroke, String teken) {
		if(!stroke.isParseable())
			return false;
		boolean[] dirBooleans = getDirBooleans(stroke,teken);
		boolean check = true;
		for(int i=0 ; i<dirBooleans.length ; i++) {
			check = check && dirBooleans[i];
		}
		return check;
	}
	
	public static boolean[] getDirBooleans(Stroke stroke, String teken) {
		int[][] dirData = getDirData(teken);
		
		for(int i=0 ; i<dirData.length ; i++) {
			System.out.println("dirData"+i+": "+dirData[i][0]+","+dirData[i][1]+","+dirData[i][2]+","+dirData[i][3]+","+dirData[i][4]);
		}
		boolean[] dirBooleans = new boolean[dirData.length];
		for(int i=0 ; i<dirData.length ; i++) {
			dirBooleans[i] = stroke.hasDirection(dirData[i][0], dirData[i][1], dirData[i][2], dirData[i][3], dirData[i][4]);
		}
		return dirBooleans;
	}
	
	public static int[][] getDirData(String teken) {
		if(cachedDirData.containsKey(teken))
			return cachedDirData.get(teken);
			
		if("AH1".equals(teken)) {int[][] data = {{70, 20, 1, 20, 12},{290, 30, 20, 39, 12}};return data;}
		if("AH2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("BH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("BH2".equals(teken)) {int[][] data = {{0, 60, 1, 10, 6},{180, 90, 10, 20, 4},{0, 90, 16, 30, 4},{180, 90, 26, 39, 6},{270, 90, 1, 39, 20},{270, 90, 14, 26, 6}};return data;}
		if("DH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("DH2".equals(teken)) {int[][] data = {{320, 30, 1, 14, 4},{300, 30, 6, 18, 4},{270, 40, 10, 30, 6},{240, 30, 22, 34, 4},{220, 30, 26, 39, 4}};return data;}
		//DH2 checkers.add(wo.hasDecreasingAngle(10,2,19));
		if("PH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("PH2".equals(teken)) {int[][] data = {{0, 30, 1, 14, 4},{300, 30, 6, 18, 2},{270, 40, 10, 30, 6},{240, 30, 22, 34, 2},{200, 30, 26, 39, 4}};return data;}
		if("bH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("bH2".equals(teken)) {int[][] data = {{0, 30, 1, 14, 4},{300, 30, 6, 18, 2},{270, 40, 10, 30, 6},{240, 30, 22, 34, 2},{200, 30, 26, 39, 4}};return data;}
		if("yH1".equals(teken)) {int[][] data = {{280, 30, 1, 30, 16},{300, 40, 20, 36, 8}};return data;}
		if("yH2".equals(teken)) {int[][] data = {{240, 30, 1, 30, 16},{230, 40, 20, 39, 8}};return data;}
		if("x1H1".equals(teken)) {int[][] data = {{330, 50, 1, 18, 8},{270, 90, 10, 30, 12},{210, 50, 22, 39, 8}};return data;}
		if("x1H2".equals(teken)) {int[][] data = {{220, 50, 1, 18, 8},{270, 90, 10, 30, 12},{320, 50, 22, 39, 8}};return data;}
		
		if("x2H1".equals(teken)) {int[][] data = {{310, 30, 1, 39, 20},{310, 90, 1, 39, 32}};return data;}	
		if("x2H2".equals(teken)) {int[][] data = {{240, 30, 1, 39, 20},{240, 90, 1, 39, 32}};return data;}	
		if("x2H2+".equals(teken)) {int[][] data = {{60, 30, 1, 39, 20},{60, 90, 1, 39, 32}};return data;}	
		
		if("labdaH1".equals(teken)) {int[][] data = {{310, 40, 1, 39, 20},{310, 90, 1, 39, 32}};return data;}	
		if("labdaH2".equals(teken)) {int[][] data = {{240, 40, 1, 39, 20},{240, 90, 1, 39, 32}};return data;}	
		if("5H1".equals(teken)) {int[][] data = {{240, 60, 1, 8, 4},{0, 60, 10, 24, 6},{270, 90, 20, 39, 4},{180, 45, 30, 39, 4}};return data;}
		if("5H2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 26}};return data;}	
		if("5H2+".equals(teken)) {int[][] data = {{180, 30, 1, 39, 32}};return data;}	
		if("9H1".equals(teken)) {int[][] data = {{220, 50, 1, 18, 8},{270, 90, 10, 30, 12},{320, 50, 22, 39, 8}};return data;}
		if("9H2".equals(teken)) {int[][] data = {{270, 30, 1, 39, 16},{180, 70, 24, 39, 4}};return data;}
		if("4H1".equals(teken)) {int[][] data = {{240, 40, 1, 20, 10},{0, 30, 22, 39, 6}};return data;}
		if("4H2".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		
		if("+H1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 32}};return data;}
		if("+H2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("+H2+".equals(teken)) {int[][] data = {{180, 20, 1, 39, 32}};return data;}
		if("7H1".equals(teken)) {int[][] data = {{0, 45, 1, 14, 6},{250, 20, 14, 39, 16}};return data;}
		if("7H2".equals(teken)) {int[][] data = {{0, 35, 1, 39, 24}};return data;}
		if("7H2+".equals(teken)) {int[][] data = {{180, 35, 1, 39, 24}};return data;}
		if(">=H1".equals(teken)) {int[][] data = {{340, 25, 1, 20, 12},{200, 25, 20, 39, 12}};return data;}
		if(">=H2".equals(teken)) {int[][] data = {{0, 20, 1, 39, 28}};return data;}
		if(">=H2+".equals(teken)) {int[][] data = {{180, 20, 1, 39, 32}};return data;}
		
		if("<=H1".equals(teken)) {int[][] data = {{200, 25, 1, 20, 14},{340, 25, 20, 39, 14}};return data;}
		if("<=H2".equals(teken)) {int[][] data = {{0, 20, 1, 39, 28}};return data;}
		if("<=H2+".equals(teken)) {int[][] data = {{180, 20, 1, 39, 32}};return data;}
		if("=H1".equals(teken)) {int[][] data = {{0, 30, 1, 39, 30}};return data;}
		if("=H1+".equals(teken)) {int[][] data = {{180, 30, 1, 39, 30}};return data;}
		if("=H2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 30}};return data;}
		if("=H2+".equals(teken)) {int[][] data = {{180, 30, 1, 39, 30}};return data;}
		if("fH1".equals(teken)) {int[][] data = {{180, 55, 1, 10, 3},{260, 35, 10, 39, 20}};return data;}
		if("fH2".equals(teken)) {int[][] data = {{0, 20, 1, 39, 28}};return data;}
		if("fH2+".equals(teken)) {int[][] data = {{180, 20, 1, 39, 32}};return data;}
		
		if("tH1".equals(teken)) {int[][] data = {{260, 25, 1, 30, 20},{0, 60, 30, 39, 3}};return data;}
		if("tH2".equals(teken)) {int[][] data = {{0, 20, 1, 39, 28}};return data;}
		if("tH2+".equals(teken)) {int[][] data = {{180, 20, 1, 39, 32}};return data;}
		if("jH1".equals(teken)) {int[][] data = {{260, 40, 1, 30, 16},{180, 55, 30, 39, 4}};return data;}
		// checkers.add(".".equals(wo.getTeken())); if("jH2".equals(teken)) {int[][] data = {{}};return data;}
		if("iH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 32}};return data;}
		if("iH1+".equals(teken)) {int[][] data = {{260, 40, 1, 30, 24},{0, 90, 30, 39, 4}};return data;}
		//	checkers.add(".".equals(wo.getTeken())); if("iH2".equals(teken)) {int[][] data = {{}};return data;}
		if("kH1".equals(teken)) {int[][] data = {{260, 40, 1, 30, 26}};return data;}
		if("kH2".equals(teken)) {int[][] data = {{220, 35, 1, 20, 12},{320, 35, 20, 39, 12}};return data;}
		if("KH1".equals(teken)) {int[][] data = {{260, 40, 1, 39, 26}};return data;}
		if("KH2".equals(teken)) {int[][] data = {{220, 35, 1, 20, 12},{320, 35, 20, 39, 12}};return data;}
		if("TH1".equals(teken)) {int[][] data = {{260, 40, 1, 39, 26}};return data;}
		if("TH2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("QH2".equals(teken)) {int[][] data = {{315, 40, 1, 39, 24}};return data;}
		if("RH1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("RH2".equals(teken)) {int[][] data =  {{0, 30, 1, 8, 2},{300, 30, 4, 12, 2},{270, 40, 7, 20, 4},{240, 30, 14, 22, 2},{200, 30, 16, 26, 3},{315, 30, 26, 39, 8}};return data;}
		
		
		return new int[0][0];
	}
	
	
	
}

