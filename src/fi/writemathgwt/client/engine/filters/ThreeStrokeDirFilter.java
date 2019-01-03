package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class ThreeStrokeDirFilter {
	
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
			
		if("AT1".equals(teken)) {int[][] data = {{240, 30, 1, 39, 20}};return data;}
		if("AT2".equals(teken)) {int[][] data = {{290, 30, 1, 39, 20}};return data;}
		if("AT3".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		//checkers.add(!wo.hasDirection(300, 25, 1, 6, 2)); // ivm lang wortelteken
		//checkers.add(!wo.hasDirection(70, 15, 4, 14, 4));
		if("FT1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 20}};return data;}
		if("FT2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("FT3".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("piT1".equals(teken)) {int[][] data = {{265, 30, 4, 39, 26}};return data;}
		if("piT2".equals(teken)) {int[][] data = {{265, 30, 4, 39, 26}};return data;}
		if("piT3".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("HT1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 20}};return data;}
		if("HT2".equals(teken)) {int[][] data = {{260, 30, 1, 39, 20}};return data;}
		if("HT3".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("RT1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 24}};return data;}
		if("RT2".equals(teken)) {int[][] data = {{0, 30, 1, 14, 4},{300, 30, 6, 18, 2},{270, 40, 10, 30, 6},{240, 30, 22, 34, 2},{200, 30, 26, 39, 4}};return data;}
		if("RT3".equals(teken)) {int[][] data = {{315, 40, 1, 39, 24}};return data;}
				
		return new int[0][0];
	}
	
	
	
}

