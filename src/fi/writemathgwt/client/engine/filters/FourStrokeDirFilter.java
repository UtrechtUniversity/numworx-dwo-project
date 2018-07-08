package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class FourStrokeDirFilter {
	
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
			
		if("EQ1".equals(teken)) {int[][] data = {{260, 30, 1, 39, 20}};return data;}
		if("EQ2".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("EQ3".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
		if("EQ4".equals(teken)) {int[][] data = {{0, 30, 1, 39, 24}};return data;}
				
		return new int[0][0];
	}
	
	
	
}

