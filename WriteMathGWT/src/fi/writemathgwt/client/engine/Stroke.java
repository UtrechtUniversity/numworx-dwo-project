package fi.writemathgwt.client.engine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.writemathgwt.client.engine.DoublePoint;
import fi.writemathgwt.client.engine.DoubleRectangle;
import fi.writemathgwt.client.engine.Point;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class Stroke {
	
	private static Logger logger = Logger.getLogger("Stroke");
	
	protected ArrayList<DoublePoint> parsePoints;
	protected int standardizeLengthNumber = 40;
	protected DoubleRectangle parsePointsBox;
	protected double[] angles;
	protected double[] dAngles;
	private long timeStamp;
	private boolean parseable = true;
	private double length;
	private String oneStrokeTeken = null;
	
	public static Stroke setState(Map<String, Object> map) {
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		List<Double> parsePointsX = null;
		List<Double> parsePointsY = null;
		
		List<Integer> parsePointsIntX = null;
		List<Integer> parsePointsIntY = null;
		
		if(h.containsKey("parsePointsX")) 
			parsePointsX = h.getDoubleList("parsePointsX");
		if(h.containsKey("parsePointsY")) 
			parsePointsY = h.getDoubleList("parsePointsY");
		
		if(h.containsKey("parsePointsIntX")) 
			parsePointsIntX = h.getIntegerList("parsePointsIntX");
		if(h.containsKey("parsePointsIntY")) 
			parsePointsIntY = h.getIntegerList("parsePointsIntY");
		
		if(parsePointsIntX==null && parsePointsX==null)
			return null;
		
		ArrayList<DoublePoint> parsePoints = new ArrayList<DoublePoint>();
		if(parsePointsX!=null) {
			for (int i = 0; i < parsePointsX.size(); i++) {
				double x = ((Number) parsePointsX.get(i)).doubleValue();
				double y = ((Number) parsePointsY.get(i)).doubleValue();
				parsePoints.add(new DoublePoint(x,y));
			}
		}
		else if(parsePointsIntX!=null) {
			for (int i = 0; i < parsePointsIntX.size(); i++) {
				double x = ((Number)((double)parsePointsIntX.get(i)/100)).doubleValue();
				double y = ((Number)((double)parsePointsIntY.get(i)/100)).doubleValue();
				parsePoints.add(new DoublePoint(x,y));
			}
		}
		return new Stroke(parsePoints, true);
	}
	
	public Stroke() {
		
	} 
	
	public Stroke(double[] x, double[] y) {
		ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i < x.length ; i++) {
			doublePoints.add(new DoublePoint(x[i], y[i]));
		}
		while (doublePoints.size() < standardizeLengthNumber+5)	{	
			doublePoints = insertPoints(doublePoints);
		}
		length = getLength(doublePoints);
//		if(length>250) {
//			doublePoints = averageSmooth(doublePoints);
//			parsePoints = standardizeToLength((int)(10*length/40),doublePoints);
//			parsePointsBox = makeParsingBox(parsePoints);
//			parseable = false;
//			return;
//		}
		doublePoints = averageSmooth(doublePoints);
		parsePoints = standardizeToLength(40,doublePoints);
		parsePointsBox = makeParsingBox(parsePoints);
		angles = new double[parsePoints.size()-1];
		makeAngles();
		makeDAngles();
		timeStamp = System.currentTimeMillis();
	}
	
	public Stroke(ArrayList<Point> points) {
		ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i < points.size() ; i++) {
			doublePoints.add(new DoublePoint(points.get(i).getX(), points.get(i).getY()));
		}
		while (doublePoints.size() < standardizeLengthNumber+5)	{	
			doublePoints = insertPoints(doublePoints);
		}
		length = getLength(doublePoints);
//		if(length>250) {
//			doublePoints = averageSmooth(doublePoints);
//			parsePoints = standardizeToLength((int)(10*length/40),doublePoints);
//			parsePointsBox = makeParsingBox(parsePoints);
//			parseable = false;
//			return;
//		}
		doublePoints = averageSmooth(doublePoints);
		parsePoints = standardizeToLength(40,doublePoints);
		parsePointsBox = makeParsingBox(parsePoints);
		angles = new double[parsePoints.size()-1];
		makeAngles();
		makeDAngles();
		timeStamp = System.currentTimeMillis();
	}
	
	public Stroke(ArrayList<Point> points, String teken) {
		ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i < points.size() ; i++) {
			doublePoints.add(new DoublePoint(points.get(i).getX(), points.get(i).getY()));
		}
		while (doublePoints.size() < standardizeLengthNumber+5)	{	
			doublePoints = insertPoints(doublePoints);
		}
		length = getLength(doublePoints);
		//if(length>250) {
			doublePoints = averageSmooth(doublePoints);
			parsePoints = standardizeToLength((int)(length/2),doublePoints);
			parsePointsBox = makeParsingBox(parsePoints);
			parseable = false;
		//	return;
		//}
			timeStamp = System.currentTimeMillis();
	}
	
	public Stroke(ArrayList<DoublePoint> points, boolean fromParsePoints) {
		parsePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i < points.size() ; i++) {
			parsePoints.add(new DoublePoint(points.get(i).getX(), points.get(i).getY()));
		}
		length = 2.5*getLength(parsePoints);
		parsePointsBox = makeParsingBox(parsePoints);
		angles = new double[parsePoints.size()-1];
		makeAngles();
		makeDAngles();
		timeStamp = System.currentTimeMillis();
	}
	
	public void setOneStrokeTeken(String s) {
		oneStrokeTeken = s;
	}
	
	public String getOneStrokeTeken() {
		return oneStrokeTeken;
	}
	
	public HashMap<String,Object> getState() {
		HashMap<String, Object> h = new HashMap<String, Object>();
//		ArrayList<Double> parsePointsX = new ArrayList<Double>();
//		ArrayList<Double> parsePointsY = new ArrayList<Double>();
		
		ArrayList<Integer> parsePointsIntX = new ArrayList<Integer>();
		ArrayList<Integer> parsePointsIntY = new ArrayList<Integer>();
		
//		for(int i = 0 ; i < parsePoints.size() ; i++) {
//		parsePointsX.add(parsePoints.get(i).x);
//		parsePointsY.add(parsePoints.get(i).y);
//		}
	
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			parsePointsIntX.add((int)(parsePoints.get(i).x*100));
			parsePointsIntY.add((int)(parsePoints.get(i).y*100));
		}
//		h.put("parsePointsX", parsePointsX);
//		h.put("parsePointsY", parsePointsY);
		
		h.put("parsePointsIntX", parsePointsIntX);
		h.put("parsePointsIntY", parsePointsIntY);
		
		return h;
	}
	
	public void extendRight(Stroke extension) {
		ArrayList<DoublePoint> doublePoints = parsePoints;
		for(int i = 0 ; i < extension.parsePoints.size() ; i++) {
			doublePoints.add(extension.parsePoints.get(i));
		}
		doublePoints = averageSmooth(doublePoints);
		parsePoints = standardizeToLength(40,doublePoints);
		parsePointsBox = makeParsingBox(parsePoints);
		angles = new double[parsePoints.size()-1];
		makeAngles();
		makeDAngles();
		timeStamp = System.currentTimeMillis();
	}
	
	public void extendLeft(Stroke extension) {
		ArrayList<DoublePoint> doublePoints = new ArrayList<DoublePoint>();
		for(int i = extension.parsePoints.size()-1 ; i > -1 ; i--) {
			doublePoints.add(extension.parsePoints.get(i));
		}
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			doublePoints.add(parsePoints.get(i));
		}
		doublePoints = averageSmooth(doublePoints);
		parsePoints = standardizeToLength(40,doublePoints);
		parsePointsBox = makeParsingBox(parsePoints);
		angles = new double[parsePoints.size()-1];
		makeAngles();
		makeDAngles();
		timeStamp = System.currentTimeMillis();
	}
	
	public ArrayList<DoublePoint> getParsePoints() {
		return parsePoints;
	}
	
	public String getTestCode() {
		String text = "{";
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			if(i>0)
				text = text+",";
			text = text + (int)Math.rint(100*(parsePoints.get(i).getX()-parsePointsBox.x));
			text = text+",";
			text = text + (int)Math.rint(100*(parsePoints.get(i).getY()-parsePointsBox.y));
		}
		text = text+"}";
		
		String samplesText = "int[] sample_="+text+";samples.add(sample_);";
		return samplesText;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	
	public double[] getAngles() {
		return angles;
	}
	
	public double[] getDAngles() {
		return dAngles;
	}
	
	public boolean isParseable() {
		return parseable;
	}
	
	public DoubleRectangle getParsePointsbox() {
		return parsePointsBox;
	}
	
	public double width() {
		return parsePointsBox.width;
	}
	
	public double height() {
		return parsePointsBox.height;
	}
	
	public double x() {
		return parsePointsBox.x;
	}
	
	public double y() {
		return parsePointsBox.y;
	}
	
	public String getParsePointsText() {
		String text = "{";
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			if(i>0)
				text = text+",";
			text = text + Math.rint(100*(parsePoints.get(i).getX()-parsePointsBox.x));
			text = text+",";
			text = text + Math.rint(100*(parsePoints.get(i).getY()-parsePointsBox.y));
		}
		text = text+"}";
		return text;
	}
	
	private ArrayList<DoublePoint> insertPoints(ArrayList<DoublePoint> doublePoints) {	
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		if(doublePoints.size()==1)
			doublePoints.add(new DoublePoint(doublePoints.get(0).x+0.00001,doublePoints.get(0).y));
		for (int i = 0; i < doublePoints.size() - 1; i++) {	
			pointsNew.add(doublePoints.get(i));
			DoublePoint beginPoint = doublePoints.get(i);
			DoublePoint endPoint = doublePoints.get(i+1);
			pointsNew.add(new DoublePoint((beginPoint.getX() + endPoint.getX()) / 2, (beginPoint.getY() + endPoint.getY()) / 2));
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		
		return pointsNew;
	}
	
	private double distance(DoublePoint p1, DoublePoint p2) {
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()));
	}
	
	public ArrayList<DoublePoint> averageSmooth(ArrayList<DoublePoint> doublePoints) {	
		if (doublePoints.size() < 5) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		pointsNew.add(doublePoints.get(0));		
		pointsNew.add(doublePoints.get(1));
		for (int i = 2; i < doublePoints.size() - 2; i++) {
			DoublePoint pOld0 = doublePoints.get(i-2);
			DoublePoint pOld1 = doublePoints.get(i-1);
			DoublePoint pOld2 = doublePoints.get(i);
			DoublePoint pOld3 = doublePoints.get(i+1);
			DoublePoint pOld4 = doublePoints.get(i+2);
			
			DoublePoint smoothedPoint = new DoublePoint(pOld0.getX()/5 + pOld1.getX()/5 + pOld2.getX()/5 + pOld3.getX()/5 + pOld4.getX()/5,
														pOld0.getY()/5 + pOld1.getY()/5 + pOld2.getY()/5 + pOld3.getY()/5 + pOld4.getY()/5);
			pointsNew.add(smoothedPoint);
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		return pointsNew;
	}
	
	private ArrayList<DoublePoint> standardizeToLength(int pointCount, ArrayList<DoublePoint> doublePoints) {
		if (doublePoints.size() < 1) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>(); 
		double s = getStandardLength(pointCount, doublePoints);
		double lengthRest = 0;
		DoublePoint  lastPoint = doublePoints.get(0);
		pointsNew.add(lastPoint);
		
		for (int i = 1; i < doublePoints.size() - 1; i++) {
			DoublePoint pOld0 = doublePoints.get(i-1);
			DoublePoint pOld1 = doublePoints.get(i);
			double lengthOld = distance(pOld0, pOld1);
			
			if (s < lengthOld + lengthRest)	{
				double xNew = pOld0.getX() + (s-lengthRest)/lengthOld*(pOld1.getX()-pOld0.getX());
				double yNew = pOld0.getY() + (s-lengthRest)/lengthOld*(pOld1.getY()-pOld0.getY());
				DoublePoint pointNew = new DoublePoint(xNew, yNew);
				pointsNew.add(pointNew);
				lengthRest = distance(pointNew,pOld1);
				while (s < lengthRest - 0.0000001) {
					xNew = pointNew.getX() + s/lengthRest*(pOld1.getX()-pointNew.getX());
					yNew = pointNew.getY() + s/lengthRest*(pOld1.getY()-pointNew.getY());
					pointNew = new DoublePoint(xNew, yNew);
					pointsNew.add(pointNew);
					lengthRest = distance(pointNew,pOld1);
				}
			}
			else 
				lengthRest += lengthOld;
		}
		while(pointsNew.size()<pointCount-1) {
			double x = (pointsNew.get(pointsNew.size()-1).getX() + doublePoints.get(doublePoints.size()-1).getX())/2;
			double y = (pointsNew.get(pointsNew.size()-1).getY() + doublePoints.get(doublePoints.size()-1).getY())/2;
			pointsNew.add(new DoublePoint(x,y));
		}
			
		pointsNew.add(doublePoints.get(doublePoints.size()-1));
		return pointsNew;
	}
	
	private double getStandardLength(int pointCount, ArrayList<DoublePoint> object) {
		double length = 0;
		for(int j = 1 ; j < object.size() ; j++) {
			length += distance(object.get(j-1), object.get(j));
		}
		return length/(pointCount-1);
	}
	
	private double getLength(ArrayList<DoublePoint> object) {
		double length = 0;
		for(int j = 1 ; j < object.size() ; j++) {
			length += distance(object.get(j-1), object.get(j));
		}
		return length;
	}
	
	public double getLength() {
		return length;
	}

	private DoubleRectangle makeParsingBox(ArrayList<DoublePoint> points) {
		double xMin = 1000;
		double xMax = 0;
		double yMin = 1000;
		double yMax = 0;
		for(int i = 0 ; i < points.size() ; i++) { 
			xMin = Math.min(xMin, points.get(i).getX());
			yMin = Math.min(yMin, points.get(i).getY());
			xMax = Math.max(xMax, points.get(i).getX());
			yMax = Math.max(yMax, points.get(i).getY());
		}
		return new DoubleRectangle(xMin, yMin, xMax-xMin+1, yMax-yMin+1);
	}
	
	private void makeAngles() {
		for(int i=1 ; i<parsePoints.size() ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i-1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i-1).getY();
			
			angles[i-1] = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
		}
	}
	
	private void makeDAngles() {
		dAngles = new double[40];
		dAngles[0] = 0.0;
		for(int i=1 ; i<standardizeLengthNumber-1 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i-1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i-1).getY();
			double ddx = parsePoints.get(i+1).getX() - parsePoints.get(i).getX();
			double ddy = parsePoints.get(i+1).getY() - parsePoints.get(i).getY();
			
			
			double angleStep1 = 180.0*(Math.atan2(-dy, dx)/Math.PI);
			double angleStep2 = 180.0*(Math.atan2(-ddy, ddx)/Math.PI);
			
			
			if(angleStep2-angleStep1>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1<-180)
				angleStep2 += 360;
			
			double dAngle = angleStep2-angleStep1;
			dAngles[i] = dAngle;
		}
		dAngles[39] = 0.0;
	}
	
	
	public boolean hasDirection (int angle, double tolerance, int pointNrStart, int pointNrEnd, int minSteps) {
		int counter = 0;
		for(int i=pointNrStart ; i<pointNrEnd+1 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i-1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i-1).getY();
			
			int angleStep = (int)angles[i];//(180.0*(Math.atan2(-dy, dx)/Math.PI));
			if(angleStep-angle>180)
				angleStep -= 360;
			if(angleStep-angle<-180)
				angleStep += 360;
				
			if(Math.abs(angleStep - angle) < tolerance)
				counter++;
		}
		if(counter >= minSteps)
			return true;
		return false;
	}
	
	public boolean hasLocation(double minx, double maxx, double miny, double maxy, int firstPoint, int lastPoint, int minCount) {
		double d = 0.0000001;
		double intervalXMin = parsePointsBox.x + minx/100*parsePointsBox.width;
		double intervalXMax = parsePointsBox.x + maxx/100*parsePointsBox.width;
		double intervalYMin = parsePointsBox.y + miny/100*parsePointsBox.height;
		double intervalYMax = parsePointsBox.y + maxy/100*parsePointsBox.height;
		
		int counter = 0;
		for(int i=firstPoint ; i<lastPoint+1 ; i++) {
			boolean boolX = parsePoints.get(i).getX() > intervalXMin-d && parsePoints.get(i).getX() < intervalXMax+d;
			boolean boolY = parsePoints.get(i).getY() > intervalYMin-d && parsePoints.get(i).getY() < intervalYMax+d;
			if(boolX && boolY)
				counter++;
		}
		if(counter>=minCount)
			return true;
		else			
			return false;
	}
	
	public boolean hasDAngle(double dAngleTotalMin, double dAngleTotalMax, double dAngleStepMin, double dAngleStepMax, int firstStep, int lastStep) {
		boolean hasTotalDAngle = true;
		boolean hasStepDAngle = true;
		double dAngleTotal = 0;
		for(int i=firstStep ; i<lastStep-1 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i-1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i-1).getY();
			double ddx = parsePoints.get(i+1).getX() - parsePoints.get(i).getX();
			double ddy = parsePoints.get(i+1).getY() - parsePoints.get(i).getY();
			
			double angleStep1 = 180.0*(Math.atan2(-dy, dx)/Math.PI);
			double angleStep2 = 180.0*(Math.atan2(-ddy, ddx)/Math.PI);
			
			if(angleStep2-angleStep1>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1<-180)
				angleStep2 += 360;
			
			hasStepDAngle = hasStepDAngle && angleStep2-angleStep1 > dAngleStepMin && angleStep2-angleStep1 < dAngleStepMax;
			dAngleTotal += angleStep2-angleStep1;
		}
		hasTotalDAngle = dAngleTotal > dAngleTotalMin &&  dAngleTotal < dAngleTotalMax;
		return hasStepDAngle && hasTotalDAngle;
	}
	
	public boolean hasLocDAngle(int angle, int tolerance, int pointNrStart, int pointNrEnd) {
		for(int i=pointNrStart ; i<pointNrEnd-3 ; i++) {
			double angleStep1 = angles[i];
			double angleStep2 = angles[i+2];
			if(angleStep2-angleStep1-angle>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1-angle<-180)
				angleStep2 += 360;
			
			if(Math.abs(angleStep2-angleStep1 - angle) < tolerance) {
				//logger.info("angle: "+(angleStep2-angleStep1));	
				return true;
			}
				
		}
		return false;
	}
	
	private double calculateDAngle(double h1, double h2) {
		if(h1-h2>180)
			h1 -= 360;
		if(h1-h2<-180)
			h1 += 360;
		return h1-h2;
	}
	
	public void translate(double dx, double dy) {
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			parsePoints.get(i).translate(dx, dy);
		}
		parsePointsBox.translate(dx, dy);
	}
	
	public void scale(double cx, double cy, double factor) {
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			parsePoints.get(i).scale(cx, cy, factor);
		}
		parsePointsBox.scale(cx, cy, factor);
	}
}

