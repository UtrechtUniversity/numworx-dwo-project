package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class StrokeLocFilter {

	private static HashMap<String,int[][]> cachedLocData = new HashMap<String,int[][]>();
	
	public static void setCachedLocData(String key, int[][] locData) {
		cachedLocData.put(key, locData);
	}
	
	public static boolean checkLoc(Stroke wo, String teken) {
		boolean[] locBooleans = getLocBooleans(wo,teken);
		boolean check = true;
		for(int i=0 ; i<locBooleans.length ; i++) {
			check = check && locBooleans[i];
		}
		return check;
	}
	
	public static boolean[] getLocBooleans(Stroke wo, String teken) {
		int[][] locData = getLocData(teken);
		
		
		boolean[] locBooleans = new boolean[locData.length];
		for(int i=0 ; i<locData.length ; i++) {
			locBooleans[i] = wo.hasLocation(locData[i][0], locData[i][1], locData[i][2], locData[i][3], locData[i][4], locData[i][5], locData[i][6]);
		}
		return locBooleans;
	}
	
	public static int[][] getLocData(String teken) {
		if(cachedLocData.containsKey(teken))
			return cachedLocData.get(teken);
			
		if("0".equals(teken)) {int[][] data = {{0,100,0,40,0,0,1},{0,100,0,40,39,39,1}};return data;}
		if("1".equals(teken)) {int[][] data = {};return data;}
		if("1_1".equals(teken)) {int[][] data = {};return data;}
		if("2".equals(teken)) {int[][] data = {};return data;}
		if("3".equals(teken)) {int[][] data = {{0,100,0,20,0,15,5},{0,70,70,100,39,39,1}};return data;}
		if("4".equals(teken)) {int[][] data = {{0,100,90,100,39,39,1},{0,100,0,90,0,30,30}};return data;}
		if("6".equals(teken)) {int[][] data = {{0,100,0,20,0,0,1},{0,100,90,100,16,33,5},{0,100,30,100,20,39,20}};return data;}
		if("7".equals(teken)) {int[][] data = {{0,100,0,25,4,15,11}};return data;}
		if("8".equals(teken)) {int[][] data = {{50,100,40,100,10,20,5}};return data;}
		if("9".equals(teken)) {int[][] data = {{0,100,0,55,0,20,21},{5,100,0,30,15,30,1},{0,100,0,15,0,10,4}};return data;}

		if("a".equals(teken)) {int[][] data = {{0,100,70,100,8,20,4},{0,15,40,100,5,20,5},{50,100,60,100,39,39,1}};return data;}
		if("b".equals(teken)) {int[][] data = {{0,100,0,10,0,2,1},{0,100,30,100,10,39,27},{0,50,70,100,9,20,1}};return data;}
		if("b_1".equals(teken)) {int[][] data = {};return data;}
		if("c".equals(teken)) {int[][] data = {{35,100,0,30,0,0,1},{40,100,60,100,39,39,1},{0,10,0,100,10,30,7}};return data;}
		if("d".equals(teken)) {int[][] data = {{0,100,30,100,0,15,16},{0,100,80,100,34,39,1}};return data;}
		if("e".equals(teken)) {int[][] data = {};return data;}
		if("f".equals(teken)) {int[][] data = {{30,100,20,70,39,39,1}};return data;}
		if("g".equals(teken)) {int[][] data = {{0,100,0,60,0,20,21}};return data;}
		if("h".equals(teken)) {int[][] data = {{0,100,0,20,0,0,1},{0,100,30,100,20,39,20},{50,100,80,100,39,39,1}};return data;}
		if("h_1".equals(teken)) {int[][] data = {{70,100,80,100,38,39,1},{0,60,30,100,0,0,1}};return data;}
		if("k".equals(teken)) {int[][] data = {{0,100,35,100,8,30,17},{0,60,60,100,30,35,1}};return data;}
		if("k_1".equals(teken)) {int[][] data = {};return data;}
		if("l".equals(teken)) {int[][] data = {};return data;}
		if("l_1".equals(teken)) {int[][] data = {{0,60,30,100,0,0,1},{0,100,80,100,39,39,1}};return data;}
		if("m".equals(teken)) {int[][] data = {{0,50,70,100,5,15,1},{25,75,50,100,20,25,1},{50,100,70,100,34,39,1}};return data;}
		if("n".equals(teken)) {int[][] data = {{50,100,0,100,25,39,8},{0,50,60,100,5,18,1},{60,100,60,100,30,39,1}};return data;}
		if("o".equals(teken)) {int[][] data = {{75,100,0,25,39,39,1},{0,100,0,30,0,10,1}};return data;}
		if("p".equals(teken)) {int[][] data = {{0,100,0,65,22,39,16},{0,40,90,100,10,20,1}};return data;}
		if("q".equals(teken)) {int[][] data = {};return data;}
		if("r".equals(teken)) {int[][] data = {{0,60,0,50,0,0,1},{90,100,0,30,39,39,1}};return data;}
		if("s".equals(teken)) {int[][] data = {{0,100,85,100,28,39,5}};return data;}
		if("u".equals(teken)) {int[][] data = {{0,100,65,100,8,20,5},{60,100,0,35,20,30,1},{0,60,0,20,0,0,1},{60,100,80,100,37,39,1}};return data;}
		if("v".equals(teken)) {int[][] data = {{0,60,0,35,0,0,1},{90,100,0,30,39,39,1}};return data;}
		if("v_1".equals(teken)) {int[][] data = {{90,100,0,20,39,39,1}};return data;}
		if("w".equals(teken)) {int[][] data = {{0,30,0,40,0,0,1},{80,100,0,40,39,39,1},{35,100,75,100,20,39,1}};return data;}
		if("x".equals(teken)) {int[][] data = {{0,60,0,60,0,0,1}};return data;}
		if("y".equals(teken)) {int[][] data = {{0,50,0,20,0,0,1},{0,100,25,100,2,10,1}};return data;}
		if("y_1".equals(teken)) {int[][] data = {{0,100,0,60,0,15,15}};return data;}
		if("z".equals(teken)) {int[][] data = {{0,100,75,100,30,39,10}};return data;}
		
		if("B".equals(teken)) {int[][] data = {{0,100,0,25,15,25,4},{0,30,0,100,1,20,10}};return data;}
		if("D".equals(teken)) {int[][] data = {{0,100,80,100,30,39,5},{0,100,0,20,14,27,5},{80,100,0,100,24,33,5}};return data;}
		if("G".equals(teken)) {int[][] data = {{60,100,40,80,39,39,1},{0,29,0,100,10,25,10},{1,100,80,100,20,30,5},{1,100,90,100,15,30,5},{1,100,40,100,25,39,15}};return data;}
		if("L".equals(teken)) {int[][] data = {{0,40,0,100,0,25,10},{0,100,80,100,20,39,10}};return data;}
		if("M".equals(teken)) {int[][] data = {{0,50,0,20,8,20,2},{50,100,0,20,20,30,2},{30,70,30,100,15,25,1},{0,20,70,100,0,0,1},{80,100,70,100,39,39,1}};return data;}
		if("R".equals(teken)) {int[][] data = {{0,100,0,10,20,30,2},{0,20,70,100,5,15,1},{0,60,30,70,25,35,1}};return data;}
		if("N".equals(teken)) {int[][] data = {{70,100,0,30,39,39,1},{0,50,1,30,8,18,1},{0,30,70,100,0,0,1}};return data;}

		if("δ".equals(teken)) {int[][] data = {{0,100,40,100,0,25,26}};return data;}
		if("φ".equals(teken)) {int[][] data = {{0,75,20,100,25,39,10}};return data;}
		if("μ".equals(teken)) {int[][] data = {{0,100,0,60,20,39,20}};return data;}
		if("μ_1".equals(teken)) {int[][] data = {{0,100,0,60,10,39,27}};return data;}
		if("ɛ".equals(teken)) {int[][] data = {{0,100,0,10,0,10,5},{0,100,10,100,30,39,5},{0,30,0,50,5,20,5},{0,30,50,100,20,34,5}};return data;}



		
		if("-".equals(teken)) {int[][] data = {{0,10,0,100,0,0,1},{90,100,0,100,39,39,1}};return data;}
		if("back".equals(teken)) {int[][] data = {};return data;}
		if("(".equals(teken)) {int[][] data = {{20,100,0,10,0,0,1},{10,100,90,100,39,39,1}};return data;}
		if(")".equals(teken)) {int[][] data = {{0,80,0,10,0,0,1},{0,80,90,100,39,39,1}};return data;}
		if(">".equals(teken)) {int[][] data = {{0,30,0,10,0,0,1},{0,30,90,100,39,39,1}};return data;}
		if("<".equals(teken)) {int[][] data = {{60,100,0,10,0,0,1},{60,100,90,100,39,39,1}};return data;}
		if("/".equals(teken)) {int[][] data = {};return data;}
		if("\\".equals(teken)) {int[][] data = {};return data;}
		if("sqrt".equals(teken)) {int[][] data = {{0,10,0,100,0,0,1},{0,100,0,20,30,39,10},{90,100,0,20,39,39,1}};return data;}
		if("sqrt_1".equals(teken)) {int[][] data = {{0,10,0,100,0,0,1},{0,100,0,20,30,39,10},{90,100,0,20,39,39,1}};return data;}
		if("of".equals(teken)) {int[][] data = {{0,10,0,30,0,0,1},{90,100,0,30,39,39,1},{30,70,90,100,15,25,1}};return data;}
		if("of_1".equals(teken)) {int[][] data = {{0,100,20,80,0,10,8},{50,100,30,70,39,39,1}};return data;}
		if("[".equals(teken)) {int[][] data = {{0,100,0,10,0,5,6},{0,100,90,100,34,39,6}};return data;}
		if("]".equals(teken)) {int[][] data = {{0,100,0,10,0,5,6},{0,100,90,100,34,39,6}};return data;}
		if("\u27e8".equals(teken)) {int[][] data = {{70,100,0,10,0,1,1},{70,100,90,100,38,39,1},{0,10,30,70,15,25,1}};return data;}
		if("\u27e9".equals(teken)) {int[][] data = {{0,30,0,10,0,1,1},{0,30,90,100,38,39,1},{90,100,30,70,15,25,1}};return data;}


		return new int[0][0];
	}
	
}

