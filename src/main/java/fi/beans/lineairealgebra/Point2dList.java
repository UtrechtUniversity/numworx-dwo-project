package fi.beans.lineairealgebra;

import java.util.ArrayList;

public class Point2dList extends ArrayList<Point2d>{
	
	private static final long serialVersionUID = -4415880322459290L;

	@Override
	public String toString() {
		String retString = "{";
		
		for (int i=0; i<this.size(); i++) {
			retString += this.get(i).toString();
		}
		
		retString = retString + "}";
		return retString;
	}
	
	double[] boundingBox() { // xmin, ymin, xmax, ymax
		double[] bb = null;
		if (size() > 0) {
			bb = new double[4];
			
			// initialization
			bb[0] = get(0).getX(); 
			bb[1] = get(0).getY();
			bb[2] = bb[0];
			bb[3] = bb[1];
			
			// loop over remaining points
			for (int i=1; i<size(); i++) {
				Point2d p = get(i);
				bb[0] = Math.min(bb[0], p.getX());
				bb[1] = Math.min(bb[1], p.getY());
				bb[2] = Math.max(bb[2], p.getX());
				bb[3] = Math.max(bb[3], p.getY());
			}
		}
		return bb; // in case path is empty, bb is also empty (size 0)
	}


}
