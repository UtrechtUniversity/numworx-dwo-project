package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class StrokeDirFilter {
	
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
			
		if("0".equals(teken)) {int[][] data = {};return data;}
		if("1".equals(teken)) {int[][] data = {{265,20,1,39,24}};return data;}
		if("1_1".equals(teken)) {int[][] data = {{45,45,1,8,4},{265,30,1,39,26}};return data;}
		if("2".equals(teken)) {int[][] data = {{0,60,33,39,2}};return data;}
		if("3".equals(teken)) {int[][] data = {{180,90,7,20,2},{0,90,15,25,2},{0,90,1,10,4},{180,80,30,39,5}};return data;}
		if("4".equals(teken)) {int[][] data = {{250,30,1,12,8},{0,60,8,30,6},{90,90,16,32,2},{260,30,20,39,6}};return data;}
		if("5_1".equals(teken)) {int[][] data = {{270,60,20,35,5},{180,45,1,8,5}};return data;}
		if("6".equals(teken)) {int[][] data = {{240,50,1,24,14},{0,90,16,34,6},{90,90,24,39,4},{180,90,30,39,2}};return data;}
		if("7".equals(teken)) {int[][] data = {{0,40,1,11,3},{250,30,15,39,20}};return data;}
		if("8".equals(teken)) {int[][] data = {};return data;}
		if("9".equals(teken)) {int[][] data = {{180,80,2,10,3},{270,90,30,39,8}};return data;}

		if("a".equals(teken)) {int[][] data = {{180,60,1,15,3}};return data;}
		if("b".equals(teken)) {int[][] data = {};return data;}
		if("b_1".equals(teken)) {int[][] data = {{  45, 45, 2, 6, 4}};return data;}
		if("c".equals(teken)) {int[][] data = {{180,40,1,15,5},{0,40,25,39,10},{180,60,1,15,10},{0,30,25,39,6}};return data;}
		if("d".equals(teken)) {int[][] data = {};return data;}
		if("e".equals(teken)) {int[][] data = {};return data;}
		if("g".equals(teken)) {int[][] data = {{180,60,2,10,3},{180,70,20,39,3}};return data;}
		if("h".equals(teken)) {int[][] data = {{ 320, 90, 33, 39, 7}};return data;}
		if("h_1".equals(teken)) {int[][] data = {{  0, 130, 34, 39, 5},{  80, 60, 25, 33, 3}};return data;}
		if("k".equals(teken)) {int[][] data = {{210,40,26,39,3}};return data;}
		if("k_1".equals(teken)) {int[][] data = {{ 230, 30, 30, 39, 2}};return data;}
		if("l".equals(teken)) {int[][] data = {{ 260, 20, 2, 30, 20},{ 0, 50, 30, 39, 2}};return data;}
		if("l_1".equals(teken)) {int[][] data = {{ 0, 50, 30, 39, 2}};return data;}
		if("m".equals(teken)) {int[][] data = {{270,70,1,15,4},{270,70,15,25,4},{270,70,30,39,4}};return data;}
		if("n".equals(teken)) {int[][] data = {{90,70,16,22,5}};return data;}
		if("o".equals(teken)) {int[][] data = {{ 180, 90, 1, 10, 4}};return data;}
		if("p".equals(teken)) {int[][] data = {{180,60,30,39,3}};return data;}
		if("q".equals(teken)) {int[][] data = {{ 50, 20, 32, 39, 2},{ 350, 120, 25, 39, 14}};return data;}
		if("r".equals(teken)) {int[][] data = {};return data;}
		if("s".equals(teken)) {int[][] data = {{180,70,1,10,5},{0,70,10,30,5},{180,70,30,39,5},{270,100,10,30,20}};return data;}
		if("u".equals(teken)) {int[][] data = {};return data;}
		if("v".equals(teken)) {int[][] data = {};return data;}
		if("v_1".equals(teken)) {int[][] data = {{ 0, 60, 1, 5, 2}};return data;}
		if("w".equals(teken)) {int[][] data = {};return data;}
		if("x".equals(teken)) {int[][] data = {};return data;}
		if("y".equals(teken)) {int[][] data = {{70,30,8,20,4},{180,90,25,39,10},{255,30,24,34,11}};return data;}
		if("y_1".equals(teken)) {int[][] data = {{270,30,1,6,4},{260,30,15,30,8},{90,60,1,20,4}};return data;}

		if("z".equals(teken)) {int[][] data = {};return data;}
		if("of".equals(teken)) {int[][] data = {};return data;}
		if("sqrt".equals(teken)) {int[][] data = {};return data;}
		if("sqrt_1".equals(teken)) {int[][] data = {};return data;}
		
		if("B".equals(teken)) {int[][] data = {{210,60,20,28,3},{180,60,30,39,3}};return data;}
		if("G".equals(teken)) {int[][] data = {{  0, 30, 36, 39, 2}};return data;}
		if("L".equals(teken)) {int[][] data = {{260,20,3,25,15},{0,20,25,39,10}};return data;}
		if("N".equals(teken)) {int[][] data = {{90,30,2,15,8},{300,30,15,25,7},{90,30,25,39,8}};return data;}
		if("R".equals(teken)) {int[][] data = {{  180, 70, 25, 35, 3}};return data;}
		
		if("β".equals(teken)) {int[][] data = {{180,90,35,39,3},{90,60,3,15,10}};return data;}
		if("μ".equals(teken)) {int[][] data = {{260,30,1,13,9}};return data;}
		if("μ_1".equals(teken)) {int[][] data = {{80,30,1,15,9}};return data;}



		if("-".equals(teken)) {int[][] data = {{0,30,1,39,24}};return data;}
		if("back".equals(teken)){int[][] data = {{ 180,35,1,39,24}};return data;}
		if(">".equals(teken)) {int[][] data = {};return data;}
		if("<".equals(teken)) {int[][] data = {};return data;}
		if("(".equals(teken)) {int[][] data = {};return data;}
		if(")".equals(teken)) {int[][] data = {};return data;}
		if("\\".equals(teken)) {int[][] data = {{310,20,1,39,25}};return data;}
		if("/".equals(teken)) {int[][] data = {{ 230, 20, 1, 39, 32}};return data;}
		
		if("→".equals(teken)) {int[][] data = {{0,20,1,20,16},{135,30,20,39,3},{225,30,20,39,3}};return data;}
		if("←".equals(teken)) {int[][] data = {{180,20,1,20,16},{45,30,20,39,3},{315,30,20,39,3}};return data;}



		
		
		return new int[0][0];
	}
	
	
	
}

