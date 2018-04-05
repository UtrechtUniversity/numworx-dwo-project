package fi.writemathgwt.client.engine;

import java.util.HashMap;

public class StrokeLocDAngleFilter {

private static HashMap<String,int[][]> cachedLocDAngleData = new HashMap<String,int[][]>();
	
	public static void setCachedLocDAngleData(String key, int[][] locDAngleData) {
		cachedLocDAngleData.put(key, locDAngleData);
	}
	
	public static boolean checkLocDAngle(Stroke wo, String teken) {
		boolean[] locDAngleBooleans = getLocDAngleBooleans(wo,teken);
		boolean check = true;
		for(int i=0 ; i<locDAngleBooleans.length ; i++) {
			check = check && locDAngleBooleans[i];
		}
		return check;
	}
	
	public static boolean[] getLocDAngleBooleans(Stroke wo, String teken) {
		int[][] locDAngleData = getLocDAngleData(teken);
		boolean[] locDAngleBooleans = new boolean[locDAngleData.length];
		for(int i=0 ; i<locDAngleData.length ; i++) {
			locDAngleBooleans[i] = wo.hasLocDAngle(locDAngleData[i][0], locDAngleData[i][1], locDAngleData[i][2], locDAngleData[i][3]);
		}
		return locDAngleBooleans;
	}
	
	public static int[][] getLocDAngleData(String teken) {
		if(cachedLocDAngleData.containsKey(teken))
			return cachedLocDAngleData.get(teken);
		
		if("2".equals(teken)) {int[][] data = {{-300,-50,-100,20,6,17}};return data;}

		if("b".equals(teken)) {int[][] data = {{180,30,10,22}};return data;}
		if("h_1".equals(teken)) {int[][] data = {{150,40,16,32}};return data;}
		if("k".equals(teken)) {int[][] data = {{120,60,29,38}};return data;}
		if("k_1".equals(teken)) {int[][] data = {{120,60,32,39}};return data;}
		if("r".equals(teken)) {int[][] data = {{180,40,10,22}};return data;}
		if("z".equals(teken)) {int[][] data = {{-130,30,5,20}};return data;}

		if("B".equals(teken)) {int[][] data = {{120,60,20,35}};return data;}
		
		if(">".equals(teken)) {int[][] data = {{-120,30,10,30}};return data;}



		return new int[0][0];
	}
}
