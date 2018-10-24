package fi.writemathgwt.client.engine.filters;

import java.util.HashMap;

import fi.writemathgwt.client.engine.Stroke;

public class StrokeDAngleFilter {

	private static HashMap<String,int[][]> cachedDAngleData = new HashMap<String,int[][]>();
	
	public static void setCachedDAngleData(String key, int[][] dAngleData) {
		cachedDAngleData.put(key, dAngleData);
	}
	
	public static boolean checkDAngle(Stroke wo, String teken) {
		boolean[] dAngleBooleans = getDAngleBooleans(wo,teken);
		boolean check = true;
		for(int i=0 ; i<dAngleBooleans.length ; i++) {
			check = check && dAngleBooleans[i];
		}
		return check;
	}
	
	public static boolean[] getDAngleBooleans(Stroke wo, String teken) {
		int[][] dAngleData = getDAngleData(teken);
		
		
		boolean[] dAngleBooleans = new boolean[dAngleData.length];
		for(int i=0 ; i<dAngleData.length ; i++) {
			dAngleBooleans[i] = wo.hasDAngle(dAngleData[i][0], dAngleData[i][1], dAngleData[i][2], dAngleData[i][3], dAngleData[i][4], dAngleData[i][5]);
		}
		return dAngleBooleans;
	}
	
	public static int[][] getDAngleData(String teken) {
		if(cachedDAngleData.containsKey(teken))
			return cachedDAngleData.get(teken);
			
		if("0".equals(teken)) {int[][] data = {};return data;}
		if("1".equals(teken)) {int[][] data = {};return data;}
		if("1_1".equals(teken)) {int[][] data = {};return data;}
		if("2".equals(teken)) {int[][] data = {{-300,-50,-100,20,6,17}};return data;}
		if("3".equals(teken)) {int[][] data = {};return data;}
		if("4".equals(teken)) {int[][] data = {};return data;}
		if("6".equals(teken)) {int[][] data = {};return data;}
		if("7".equals(teken)) {int[][] data = {{-30,30,-20,20,25,38}};return data;}
		if("8".equals(teken)) {int[][] data = {};return data;}
		if("9".equals(teken)) {int[][] data = {{20,240,0,90,5,12}};return data;}

		if("a".equals(teken)) {int[][] data = {{90,460,-10,120,2,20}};return data;}
		if("b".equals(teken)) {int[][] data = {};return data;}
		if("b_1".equals(teken)) {int[][] data = {{90,240,-10,120,23,33},{150,270,-20,180,3,20}};return data;}
		if("c".equals(teken)) {int[][] data = {};return data;}
		if("d".equals(teken)) {int[][] data = {};return data;}
		if("e".equals(teken)) {int[][] data = {{270,460,-10,90,2,38}};return data;}
		if("f".equals(teken)) {int[][] data = {{150,270,-20,180,3,20}};return data;}
		if("g".equals(teken)) {int[][] data = {{50,300,-10,90,2,10}};return data;}
		if("h".equals(teken)) {int[][] data = {};return data;}
		if("h_1".equals(teken)) {int[][] data = {{150,270,-20,180,3,20},{-240,-90,-90,30,30,37}};return data;}
		if("k".equals(teken)) {int[][] data = {};return data;}
		if("k_1".equals(teken)) {int[][] data = {{-240,-60,-180,20,30,35},{150,270,-20,180,3,20}};return data;}
		if("l".equals(teken)) {int[][] data = {{-25,25,-10,10,4,25}};return data;}
		if("l_1".equals(teken)) {int[][] data = {{150,270,-20,180,3,20}};return data;}
		if("m".equals(teken)) {int[][] data = {};return data;}
		if("n".equals(teken)) {int[][] data = {};return data;}
		if("o".equals(teken)) {int[][] data = {};return data;}
		if("p".equals(teken)) {int[][] data = {};return data;}
		if("q".equals(teken)) {int[][] data = {{90,460,-10,90,2,15}};return data;}
		if("r".equals(teken)) {int[][] data = {};return data;}
		if("s".equals(teken)) {int[][] data = {};return data;}
		if("u".equals(teken)) {int[][] data = {{-30,30,-30,30,2,8}};return data;}
		if("v".equals(teken)) {int[][] data = {};return data;}
		if("v_1".equals(teken)) {int[][] data = {};return data;}
		if("w".equals(teken)) {int[][] data = {};return data;}
		if("x".equals(teken)) {int[][] data = {{-300,-20,-120,5,2,10},{30,160,-10,90,30,39}};return data;}
		if("y".equals(teken)) {int[][] data = {{-240,40,-100,20,25,35}};return data;}
		if("z".equals(teken)) {int[][] data = {};return data;}
		
		if("R".equals(teken)) {int[][] data = {{-300,-120,-90,10,15,27}};return data;}
		if("G".equals(teken)) {int[][] data = {};return data;}
		if("B".equals(teken)) {int[][] data = {};return data;}
		if("D".equals(teken)) {int[][] data = {{-300,-90,-50,10,22,38}};return data;}
		if("M".equals(teken)) {int[][] data = {};return data;}
		if("L".equals(teken)) {int[][] data = {{-20,20,-20,20,3,20}};return data;}
		if("N".equals(teken)) {int[][] data = {};return data;}
		
		if("α".equals(teken)) {int[][] data = {{-330,-240,-120,20,5,30}};return data;}
		if("γ".equals(teken)) {int[][] data = {{-360,-200,-150,20,3,38}};return data;}
		if("φ".equals(teken)) {int[][] data = {{360,500,-30,120,2,38}};return data;}
		
		if("-".equals(teken)) {int[][] data = {};return data;}
		if("(".equals(teken)) {int[][] data = {};return data;}
		if(")".equals(teken)) {int[][] data = {};return data;}
		if(">".equals(teken)) {int[][] data = {};return data;}
		if("<".equals(teken)) {int[][] data = {};return data;}
		if("/".equals(teken)) {int[][] data = {};return data;}
		if("\\".equals(teken)) {int[][] data = {};return data;}
		if("back".equals(teken)) {int[][] data = {};return data;}
		if("sqrt".equals(teken)) {int[][] data = {};return data;}
		if("sqrt_1".equals(teken)) {int[][] data = {};return data;}
		if("of".equals(teken)) {int[][] data = {};return data;}
		if("of_1".equals(teken)) {int[][] data = {};return data;}
		if("[".equals(teken)) {int[][] data = {{-20,20,-20,20,12,28}};return data;}
		if("]".equals(teken)) {int[][] data = {{-20,20,-20,20,12,28}};return data;}

		
		return new int[0][0];
	}
	
}

