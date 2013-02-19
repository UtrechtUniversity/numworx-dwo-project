package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class WriteObject {

	public static int[] sample_1 = {30,0,30,11,30,22,26,33,22,44,19,56,19,67,19,78,19,89,19,100,19,111,19,122,15,133,15,144,15,156,7,163,7,174,4,185,4,196,0,200};
	public static int[] sample_2 = {15,36,44,21,81,10,119,0,163,0,193,15,193,46,185,72,156,97,133,123,104,144,81,164,44,185,7,195,22,185,59,174,104,169,141,185,163,200,200,200};
	public static int[] sample_3 = {18,19,51,6,91,3,131,0,167,11,196,31,189,56,156,75,120,89,84,100,87,103,127,106,164,117,189,136,189,167,156,186,120,194,80,200,40,200,0,189};
	public static int[] sample_4 = {194,0,181,16,156,35,125,52,94,68,75,87,44,103,19,123,6,139,50,145,94,145,138,142,181,139,188,123,200,103,188,116,181,139,181,161,188,184,200,200};
	public static int[] sample_5 = {184,0,155,8,114,8,78,11,53,25,53,52,53,79,45,93,82,88,118,85,155,96,188,110,200,132,196,156,176,175,147,192,110,200,73,197,41,186,4,175,0,173};
	public static int[] sample_6 = {200,0,157,5,119,19,86,36,59,52,38,71,16,90,0,112,0,134,5,156,22,178,54,192,97,200,130,189,151,170,157,148,146,126,119,110,81,101,43,101,38,101};
	public static int[] sample_7 = {0,4,28,0,55,4,83,6,111,6,138,8,166,8,194,6,178,12,157,26,142,42,129,58,117,74,108,92,98,110,86,126,77,144,68,162,55,180,52,200};
	public static int[] sample_8 = {133,86,84,91,40,114,4,139,4,172,49,192,107,200,164,195,200,170,191,137,160,109,116,86,67,66,18,46,31,20,80,3,129,10,160,38,156,71,116,86};
	public static int[] sample_9 = {200,9,151,2,92,4,43,20,16,41,22,67,76,76,130,65,168,46,184,22,178,26,168,50,162,76,168,102,168,128,173,154,162,178,124,196,65,200,0,198,0,198};
	public static int[] sample_0 = {85,0,56,8,30,26,10,53,3,83,0,117,13,147,30,177,56,192,85,200,115,196,141,189,164,166,184,140,193,109,193,79,180,49,157,30,131,15,105,8};
	
	public static int[] sample_x = {30,4,72,21,98,58,102,104,91,150,60,183,19,200,4,171,42,146,79,125,117,100,151,71,158,25,128,0,102,29,87,75,83,121,117,146,158,154,200,154};
	public static int[] sample_y = {38,22,48,41,62,59,76,76,90,93,105,107,138,93,152,76,162,57,176,39,181,30,167,48,152,65,133,83,119,100,105,117,86,135,62,152,19,189};
	public static int[] sample_a = {189,30,161,12,122,18,83,42,50,67,22,103,0,145,6,188,44,200,83,182,117,152,144,121,167,79,183,36,189,6,167,36,156,79,150,127,156,170,200,170,200,170};
	public static int[] sample_b = {150,11,139,8,117,35,106,62,89,89,78,116,56,143,33,170,17,197,11,184,33,159,50,132,78,111,133,108,183,119,200,143,189,170,150,189,100,200,56,200};
	
	public static int[] sample_hOpen = {148,9,130,16,113,23,96,32,78,41,61,50,43,58,30,67,17,76,4,87,4,97,0,108,0,119,0,129,4,140,13,150,22,161,30,172,70,195};
	public static int[] sample_hClose = {50,10,69,18,88,25,106,33,125,40,144,48,156,58,175,68,188,78,194,88,200,98,200,108,200,118,194,128,188,138,175,148,156,155,138,163,88,188};
	
	public static int[] sample_plus = {82,9,85,38,85,67,85,96,85,125,82,154,82,183,82,191,73,165,64,139,55,113,33,93,6,87,24,90,55,90,82,84,112,84,139,81,188,75};
	public static int[] sample_min = {0,0,10,0,20,0,30,0,41,0,51,0,61,0,71,0,81,0,91,0,101,3,111,3,122,3,132,3,142,3,152,3,162,3,190,3};
	public static int[] sample_slash = {200,0,185,10,170,20,158,29,143,39,132,49,125,59,113,68,102,78,94,88,87,98,79,107,68,117,60,127,49,137,42,146,34,159,23,168,8,193};
	public static int[] sample_slash_1 = {0,200,6,188,19,178,29,166,38,154,48,144,60,134,70,122,76,110,86,100,95,90,108,80,121,71,130,59,143,49,156,39,165,29,178,20,187,10,200,0};
	public static int[] sample_backslash = {2,0,12,10,22,21,32,31,41,42,51,52,61,65,71,78,80,91,90,101,102,109,115,119,127,130,139,140,151,148,161,158,171,169,183,179,198,195};
	public static int[] sample_sqrt = {0,152,11,171,20,189,31,192,31,171,31,149,31,128,31,107,31,85,31,64,29,43,23,24,31,11,54,16,77,13,100,8,123,5,146,3,169,3,194,0};
	
	public static int[] sample_back = {200,0,190,2,179,2,169,2,158,2,148,3,137,3,127,3,117,5,106,5,96,5,85,5,75,5,64,3,54,3,43,3,33,3,23,3,12,3,3,5};
	
	public static int[] sample_of = {0,13,0,36,16,53,24,76,40,93,48,116,48,138,56,156,72,173,88,191,104,182,120,164,128,142,144,124,152,102,168,84,176,62,184,44,192,27,192,4};	
	
	public static HashMap<String, int[]>  samples;
	
	public static void initSamples() {
		samples = new HashMap<String, int[]>();
		samples.put("1",sample_1);
		samples.put("2",sample_2);
		samples.put("3",sample_3);
		samples.put("4",sample_4);
		samples.put("5",sample_5);
		samples.put("6",sample_6);
		samples.put("7",sample_7);
		samples.put("8",sample_8);
		samples.put("9",sample_9);
		samples.put("0",sample_0);
		samples.put("x",sample_x);
		samples.put("y",sample_y);
		samples.put("a",sample_a);
		samples.put("b",sample_b);
		samples.put("(",sample_hOpen);
		samples.put(")",sample_hClose);
		samples.put("+",sample_plus);
		samples.put("-",sample_min);
		samples.put("/",sample_slash);
		samples.put("/_1",sample_slash_1);
		samples.put("\\",sample_backslash);
		samples.put("sqrt",sample_sqrt);
		samples.put("back",sample_back);
		samples.put("of",sample_of);
	}
	
	private ArrayList<DoublePoint> doublePoints;
	private Rectangle box;
	private String teken;
	private int maxWidth = 1000;
	private int maxHeigth = 500;
	private int standardizeUnit = 20;
	private int standardizeLengthNumber = 20;
	
	
	
	public WriteObject(ArrayList<Point> points){
		doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <points.size() ; i++) {
			doublePoints.add(points.get(i).getDoublePoint());
		}
		makeBox(points);
		if(doublePoints.size()>1)
			doublePoints = standardize(doublePoints);
		teken = parse(doublePoints);
		
		System.out.println("WriteObject: "+teken);
		System.out.println("WriteObject: "+teken);
	}
	
	public WriteObject(String teken, ArrayList<Point> points){
		makeBox(points);
		doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <points.size() ; i++) {
			doublePoints.add(points.get(i).getDoublePoint());
		}
		this.teken = teken;
	}
	
	public ArrayList<Point> getPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		for(int i = 0 ; i <doublePoints.size() ; i++) {
			points.add(doublePoints.get(i).getPoint());
		}
		return points;
	}
	
	public Rectangle getBox() {
		if(teken.equals("-"))
			return new Rectangle(box.x-box.height, box.y, box.width+2*box.height, box.height);
		return box;
	}
	
	public Point getBoxMid() {
		return new Point(box.x + box.width/2, box.y + box.height/2);
	}
	
	public String getTeken() {
		return teken;
	}
	
	public void draw(Context2d g)
	{	g.setStrokeStyle(CssColor.make(0, 0, 0));
		
		if(doublePoints.size() > 0) {
			g.beginPath();
			g.moveTo(doublePoints.get(0).x, doublePoints.get(0).y);
			for(int j = 1 ; j <doublePoints.size() ; j++) {
				if(distance(doublePoints.get(j-1), doublePoints.get(j))<4*getStandardLength(doublePoints))
					g.lineTo(doublePoints.get(j).x, doublePoints.get(j).y);
				else 
					g.moveTo(doublePoints.get(j).x, doublePoints.get(j).y);
			}
			g.stroke();
		}
		
	}
	
	
	private void makeBox(ArrayList<Point> points) {
		int xMin = maxWidth;
		int xMax = 0;
		int yMin = maxHeigth;
		int yMax = 0;
		for(int i = 0 ; i < points.size() ; i++) { 
			xMin = Math.min(xMin, points.get(i).x);
			yMin = Math.min(yMin, points.get(i).y);
			xMax = Math.max(xMax, points.get(i).x);
			yMax = Math.max(yMax, points.get(i).y);
		}
		box = new Rectangle(xMin, yMin, xMax-xMin+1, yMax-yMin+1);
	}
		
	private String parse(ArrayList<DoublePoint> doublePoints) {
		if(doublePoints.size()>1) {
			this.doublePoints = standardizeToLength(doublePoints);
			doublePoints = scaleToSquare(this.doublePoints);
		}
		return findTeken(doublePoints);
	}
	
	private double distance(Point p1, Point p2) {
		return Math.sqrt(1.0*(p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}
	
	private double distance(DoublePoint p1, DoublePoint p2) {
		return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}
	
	private ArrayList<DoublePoint> standardize(ArrayList<DoublePoint> doublePoints) {
		if(doublePoints.size()<1) return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>(); 
		DoublePoint  lastPoint = doublePoints.get(0);
		pointsNew.add(lastPoint);
		for(int i = 1 ; i < doublePoints.size() ; i++) { 
			double unit = Math.max(box.width,box.height)/standardizeUnit;	
			if(distance(doublePoints.get(i),lastPoint)>unit || i==doublePoints.size()-1) {
				lastPoint = doublePoints.get(i);
				pointsNew.add(lastPoint);
			}
		}
		return pointsNew;
	}
		
	private ArrayList<DoublePoint> standardizeToLength(ArrayList<DoublePoint> doublePoints) {
		if(doublePoints.size()<1) return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>(); 
		double s = getStandardLength(doublePoints);
		double lengthRest = 0;
		DoublePoint  lastPoint = doublePoints.get(0);
		pointsNew.add(lastPoint);
		
		for(int i = 1 ; i < doublePoints.size()-1 ; i++) {
			DoublePoint pOld0 = doublePoints.get(i-1);
			DoublePoint pOld1 = doublePoints.get(i);
			double lengthOld = distance(pOld0, pOld1);
			if(s<lengthOld + lengthRest) {
				double xNew = pOld0.x + (s-lengthRest)/lengthOld*(pOld1.x-pOld0.x);
				double yNew = pOld0.y + (s-lengthRest)/lengthOld*(pOld1.y-pOld0.y);
				DoublePoint pointNew = new DoublePoint(xNew, yNew);
				pointsNew.add(pointNew);
				lengthRest = distance(pointNew,pOld1);
				int teller = 0;
				while(s<lengthRest-0.0000001) {
					xNew = pointNew.x + s/lengthRest*(pOld1.x-pointNew.x);
					yNew = pointNew.y + s/lengthRest*(pOld1.y-pointNew.y);
					pointNew = new DoublePoint(xNew, yNew);
					pointsNew.add(pointNew);
					lengthRest = distance(pointNew,pOld1);
					teller++;
				}
			}
			else lengthRest += lengthOld;
		}
		pointsNew.add(doublePoints.get(doublePoints.size()-1));
		return pointsNew;
	}
	
	private double getStandardLength(ArrayList<DoublePoint> object) {
		double length = 0;
		for(int j = 1 ; j < object.size() ; j++) {
			length += distance(object.get(j-1), object.get(j));
		}
		return length/(standardizeLengthNumber-1);
	}
	
	private double getDistance(ArrayList<DoublePoint> doublePoints, int[] sample) {
		double distance = 0;
		for(int i = 0 ; i < doublePoints.size() ; i++) {
			if(i<sample.length/2){
				DoublePoint p = new DoublePoint(sample[2*i], sample[2*i+1]);
				distance +=  distance(doublePoints.get(i), p);
			}
		}
		distance = distance/standardizeLengthNumber;
		return distance;
	}
	
	private ArrayList<DoublePoint> scaleToSquare(ArrayList<DoublePoint> doublePoints) {
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		double scaleX = 200.0 / box.width;
		double scaleY = 200.0 / box.height;
		if(scaleX>4*scaleY)scaleX=1;
		else if(scaleY>4*scaleX)scaleY=1;
		int startX = box.x;
		int startY = box.y;
		System.out.print("Sample: ");
		for(int i = 0 ; i < doublePoints.size() ; i++) {
			DoublePoint p = doublePoints.get(i);
			double xNew = scaleX*(p.x-startX);
			double yNew = scaleY*(p.y-startY);
			pointsNew.add(new DoublePoint(xNew,yNew));
			System.out.print(""+pointsNew.get(i).getPoint().x + "," + pointsNew.get(i).getPoint().y + ",");
		}
		return pointsNew;
	}
	
	
	public String findTeken(ArrayList<DoublePoint> doublePoints) {
		String teken = null;
		double min = 100;
		Set keys = samples.keySet();
		Iterator<String> iterator = (Iterator<String>)keys.iterator();
	    while(iterator.hasNext()) {
	        String key = iterator.next();
	        int[] sample = samples.get(key);
	        double distanceMin = getDistance(doublePoints, sample);
	        if(distanceMin < min){
			    teken = key;
				min = distanceMin;
			}
	    }
	    return teken;
	}
}
