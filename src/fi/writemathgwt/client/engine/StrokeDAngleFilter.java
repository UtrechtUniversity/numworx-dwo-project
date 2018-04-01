package fi.writemathgwt.client.engine;

import java.util.HashMap;

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
		int [][] dirData = null;
		if(cachedDAngleData.containsKey(teken))
			return cachedDAngleData.get(teken);
			
		if("-".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if(">".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("<".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		
		if("(".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if(")".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("0".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("1".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("1_1".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("2".equals(teken)) {
			int[][] data = {
					{ -300, -70, -80, 20, 2, 17 },	
			};
			return data;
		}
		if("3".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("4".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("6".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("7".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		
		if("8".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("9".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("a".equals(teken)) {
			int[][] data = {
					{90, 460, -10, 90,2,20}
			};
			return data;
		}
		if("b".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("b_1".equals(teken)) {
			int[][] data = {
					{  90, 240, -10, 120, 23, 33}
			};
			return data;
		}
		if("c".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("d".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("e".equals(teken)) {
			int[][] data = {
					{270, 460, -10, 90,2,38}
			};
			return data;
		}
		
		if("g".equals(teken)) {
			int[][] data = {
					{50, 300, -10, 90,2,10}
			};
			return data;
		}
		if("h".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("h_1".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("k".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("k_1".equals(teken)) {
			int[][] data = {
					{-240, -60, -180, 20,30,35}
			};
			return data;
		}
		if("l".equals(teken)) {
			int[][] data = {
					{  -25, 25, -10, 10, 4, 25}
			};
			return data;
		}
		if("m".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("n".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("o".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("p".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("q".equals(teken)) {
			int[][] data = {
					{90, 460, -10, 90,2,15}
			};
			return data;
		}
		if("r".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("s".equals(teken)) {
			int[][] data = {
				
			};
			return data;
		}
		if("u".equals(teken)) {
			int[][] data = {
					{-30, 30, -30, 30,2,8}
			};
			return data;
		}
		if("v".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("w".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("x".equals(teken)) {
			int[][] data = {
					{-180, -30, -45, 5,2,10}	
			};
			return data;
		}
		if("y".equals(teken)) {
			int[][] data = {
					{  -40, 40, -20, 20, 25, 35}
			};
			return data;
		}
		if("z".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("of".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("sqrt".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("sqrt_1".equals(teken)) {
			int[][] data = {
					
			};
			return data;
		}
		if("/".equals(teken)) { 
			int[][] data = {
					
			};
			return data;
		}
		return new int[0][0];
	}
	
}

