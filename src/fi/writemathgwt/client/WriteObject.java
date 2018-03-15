package fi.writemathgwt.client;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;



public class WriteObject {
	private static Logger logger = Logger.getLogger("WriteObject");

	private final static boolean cNewStrokmatcher = true;
	static int newTekenSet = 0;
	public static HashMap<String, int[]>  samples;
	
	
 	
	//OK
	public static void initSamples(int tekenSet) {
//		logger.info("tekenSet = "+tekenSet);		
		newTekenSet = tekenSet;
		samples = Samples20.init(tekenSet);
	}
	
	public boolean newMatch = false;
	public boolean oldMatch = false;
	public boolean newMatchWrong = false;
	public boolean oldMatchWrong = false;
	private boolean isTwoStrokeObject;
	private int twoStrokeGap;
	private boolean isThreeStrokeObject;
	private int threeStrokeGap1;
	private int threeStrokeGap2;
	private ArrayList<DoublePoint> doublePoints;
	private ArrayList<DoublePoint> parsePoints;
	private ArrayList<DoublePoint> rawPoints;
	private Rectangle box;
	private int boxDiagonal;
	int boxOffset = 2;
	private String teken;
	
	private int maxWidth = 1000;
	private int maxHeigth = 500;
	private int standardizeUnit = 20;
	private int standardizeLengthNumber = 20;
	
	// spatial parsing
	WriteObject isTellerVan = null;
	WriteObject isNoemerVan = null;
	WriteObject isMachtVan = null;
	WriteObject isOnderWortel = null;	
	boolean isVerwerkt = false;
	boolean isBreuk = false;
	Rectangle tellerBox = null;
	Rectangle noemerBox = null;
	Rectangle wortelBox = null;

	String teken1 = "null";
	String teken2 = "null";
	String teken3 = "null";
	String teken4 = "null";
	String teken5 = "null";
	String teken6 = "null";
	
	StrokeMatcherWrapper newStrokeMatcher;
	public ArrayList<Point> points;
	
	public WriteObject(boolean fromRef, ArrayList<Point> points) {
		isTwoStrokeObject = false;
		isThreeStrokeObject = false;
		twoStrokeGap = 0;
		threeStrokeGap1 = 0;
		threeStrokeGap2 = 0;
		
		if ( cNewStrokmatcher ) {
			newStrokeMatcher = new StrokeMatcherWrapper(newTekenSet);
		}
		
		this.points = new ArrayList<Point>();
		
		doublePoints = new ArrayList<DoublePoint>();
		rawPoints = new ArrayList<DoublePoint>();
		double size = 0;
		for(int i = 0 ; i < points.size() ; i++) 
		{
			this.points.add(new Point(points.get(i).x, points.get(i).y));
			doublePoints.add(new DoublePoint(points.get(i).x, points.get(i).y));
			rawPoints.add(new DoublePoint(points.get(i).x, points.get(i).y));
			size = Math.max(size, distance(points.get(0), points.get(i)));
		}
		//doublePoints = averageSmooth(doublePoints);
		makeBox(points);
		
		// try to standarize
		if (doublePoints.size() >= 20) {	
			ArrayList<DoublePoint> tempDoublePoints = standardize(doublePoints);
			if (tempDoublePoints.size() >= 20)
				doublePoints = tempDoublePoints;
		}
		
		int dpSize = doublePoints.size();
		while (dpSize < 20)
		{	
			doublePoints = insertPoint(doublePoints);
			dpSize = doublePoints.size();
		}
	}
	
	public ArrayList<DoublePoint> averageSmooth(ArrayList<DoublePoint> doublePoints)
	{
		if (doublePoints.size() < 5) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		pointsNew.add(doublePoints.get(0));		
		pointsNew.add(doublePoints.get(1));
		for (int i = 2; i < doublePoints.size() - 2; i++)
		{
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
	//OK
	public WriteObject(ArrayList<Point> points) {
		isTwoStrokeObject = false;
		isThreeStrokeObject = false;
		twoStrokeGap = 0;
		threeStrokeGap1 = 0;
		threeStrokeGap2 = 0;
		if ( cNewStrokmatcher ) {
			newStrokeMatcher = new StrokeMatcherWrapper(newTekenSet);
		}
		this.points = new ArrayList<Point>();
		
		doublePoints = new ArrayList<DoublePoint>();
		rawPoints = new ArrayList<DoublePoint>();
		double size = 0;
		for(int i = 0 ; i < points.size() ; i++) 
		{
			this.points.add(new Point(points.get(i).x, points.get(i).y));
			doublePoints.add(new DoublePoint(points.get(i).x, points.get(i).y));
			rawPoints.add(new DoublePoint(points.get(i).x, points.get(i).y));
			size = Math.max(size, distance(points.get(0), points.get(i)));
		}
		//doublePoints = averageSmooth(doublePoints);
		makeBox(points);
		
		if (size < 3) 
		{
			teken = ".";
			parsePoints = new ArrayList<DoublePoint>();
			for(int i = 0 ; i < standardizeLengthNumber ; i++) 
			{
				parsePoints.add(new DoublePoint(points.get(0).x, points.get(0).y));
			}
			return;
		}
		
		if (cNewStrokmatcher) { // if new parse before standardize
			teken = parse(doublePoints);
		} 
			
		// try to standarize
		if (doublePoints.size() >= 20) {	
			ArrayList<DoublePoint> tempDoublePoints = standardize(doublePoints);
			if (tempDoublePoints.size() >= 20)
				doublePoints = tempDoublePoints;
		}
		
		int dpSize = doublePoints.size();
		while (dpSize < 20)
		{	
			doublePoints = insertPoint(doublePoints);
			dpSize = doublePoints.size();
		}
			
		if (!cNewStrokmatcher) { // if old parse after standardize
			teken = parse(doublePoints);
		} 
				
		
//System.out.println("WriteObject: " + teken);
	}

	//OK
	public WriteObject(String teken, ArrayList<Point> points){
		isTwoStrokeObject = false;
		isThreeStrokeObject = false;
		
//		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		//logger.info("New Object, teken = " + teken);
//		logger.info("Stacktrace = "+ stackTraceElements);
		
		makeBox(points);
		doublePoints = new ArrayList<DoublePoint>();
		parsePoints = new ArrayList<DoublePoint>();
		rawPoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <points.size() ; i++) {
//			logger.info("new Point added -" +i +"- X="+points.get(i).getDoublePoint().getX() +
//					",Y="+points.get(i).getDoublePoint().getY());
			doublePoints.add(points.get(i).getDoublePoint());
			rawPoints.add(points.get(i).getDoublePoint());
			parsePoints.add(points.get(i).getDoublePoint());
		}
		
		parsePoints = standardizeToLength(parsePoints);
		this.teken = teken;
	}
	
	public WriteObject(boolean fromSample, String teken, ArrayList<Point> points){
		isTwoStrokeObject = false;
		isThreeStrokeObject = false;
		
		makeBox(points);
		doublePoints = new ArrayList<DoublePoint>();
		parsePoints = new ArrayList<DoublePoint>();
		rawPoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <points.size() ; i++) {
//			logger.info("new Point added -" +i +"- X="+points.get(i).getDoublePoint().getX() +
//					",Y="+points.get(i).getDoublePoint().getY());
			doublePoints.add(points.get(i).getDoublePoint());
			rawPoints.add(points.get(i).getDoublePoint());
			parsePoints.add(points.get(i).getDoublePoint());
		}
		
		this.teken = teken;
	}
	
	//OK
	public WriteObject(String teken, WriteObject wo1, WriteObject wo2){
		isTwoStrokeObject = true;
		
		ArrayList<Point> wo1Points = wo1.getPoints();
		ArrayList<Point> wo2Points = wo2.getPoints();
		ArrayList<Point> wo1RawPoints = wo1.getRawPoints();
		ArrayList<Point> wo2RawPoints = wo2.getRawPoints();
		twoStrokeGap = wo1RawPoints.size();

		doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <wo1Points.size() ; i++) {
			doublePoints.add(wo1Points.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo2Points.size() ; i++) {
			doublePoints.add(wo2Points.get(i).getDoublePoint());
		}
		makeBoxDouble(doublePoints);
		
		rawPoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <wo1RawPoints.size() ; i++) {
			rawPoints.add(wo1RawPoints.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo2RawPoints.size() ; i++) {
			rawPoints.add(wo2RawPoints.get(i).getDoublePoint());
		}

		this.teken = teken;
	}

	public WriteObject(String teken, WriteObject wo1, WriteObject wo2,  WriteObject wo3){
		isThreeStrokeObject = true;
		
		ArrayList<Point> wo1Points = wo1.getPoints();
		ArrayList<Point> wo2Points = wo2.getPoints();
		ArrayList<Point> wo3Points = wo3.getPoints();
		ArrayList<Point> wo1RawPoints = wo1.getRawPoints();
		ArrayList<Point> wo2RawPoints = wo2.getRawPoints();
		ArrayList<Point> wo3RawPoints = wo3.getRawPoints();
		threeStrokeGap1 = wo1RawPoints.size();
		threeStrokeGap2 = threeStrokeGap1 + wo2RawPoints.size();

		doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <wo1Points.size() ; i++) {
			doublePoints.add(wo1Points.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo2Points.size() ; i++) {
			doublePoints.add(wo2Points.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo3Points.size() ; i++) {
			doublePoints.add(wo3Points.get(i).getDoublePoint());
		}
		makeBoxDouble(doublePoints);
		
		rawPoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <wo1RawPoints.size() ; i++) {
			rawPoints.add(wo1RawPoints.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo2RawPoints.size() ; i++) {
			rawPoints.add(wo2RawPoints.get(i).getDoublePoint());
		}
		for(int i = 0 ; i <wo3RawPoints.size() ; i++) {
			rawPoints.add(wo3RawPoints.get(i).getDoublePoint());
		}

		this.teken = teken;
	}

	// compact deep copy
	public WriteObject(WriteObject wo)
	{	box = new Rectangle(wo.box.x, wo.box.y, wo.box.width, wo.box.height);
		teken = new String(wo.teken);
	}
	
	//OK
	public ArrayList<Point> getPoints() 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int i = 0 ; i < doublePoints.size() ; i++) {
			points.add(doublePoints.get(i).getPoint());
		}
		return points;
	}
	
	public ArrayList<Point> getParsePoints() 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int i = 0 ; i < parsePoints.size() ; i++) {
			points.add(parsePoints.get(i).getPoint());
		}
		return points;
	}
	
	public ArrayList<Point> getRawPoints() 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(int i = 0 ; i < rawPoints.size() ; i++) {
			points.add(rawPoints.get(i).getPoint());
		}
		return points;
	}

	
	//OK
	public Rectangle getBox() 
	{
		if (teken.equals("-") || teken.equals("back"))
		{	
			int height = 2;
			if (box.height < height)
				return new Rectangle(box.x - height, box.y - height / 2, box.width + 2 * height, height);
			else
				return new Rectangle(box.x - box.height, box.y, box.width + 2 * box.height, box.height);
		}
		return box;
	}
	
	public Point getBoxMid() 
	{
		if (hasAscent())
			return new Point(box.x + box.width / 2, box.y + 2 * box.height / 3);
		else if (hasDescent())
			return new Point(box.x + box.width / 2, box.y + box.height / 3);	
		else
			return new Point(box.x + box.width / 2, box.y + box.height / 2);
	}
	
	public boolean hasAscent()
	{	
		if (getTeken().equals("b") || getTeken().equals("d") || getTeken().equals("h") || getTeken().equals("k") ||
			getTeken().equals("l") || getTeken().equals("t") || getTeken().equals("6") || getTeken().equals("8"))
		{	return true;
		}
		else 
			return false;
	}
	
	public boolean hasDescent()
	{	
		if (getTeken().equals("f") || getTeken().equals("g") || getTeken().equals("j") || getTeken().equals("p") ||
			getTeken().equals("q") || getTeken().equals("y") || getTeken().equals("7") || getTeken().equals("9"))
		{	return true;
		}
		else
			return false;
	}
	//OK
	public String getTekenRaw() 
	{
		return teken;
	}
	//OK
	public String getTeken() 
	{	
		return getTeken(teken);
	}
	//OK
	public String getTeken(String s) 
	{
		if (s != null && s.indexOf("_") > 0)
			s = s.substring(0, s.indexOf("_"));
		return s;
	}

	public String zetTeken(String s) 
	{	
		return teken = new String(s);
	}

	public String getTeken1() 
	{	
		return getTeken(teken1);
	}
	public String getTeken2() 
	{	
		return getTeken(teken2);
	}
	public String getTeken3() 
	{	
		return getTeken(teken3);
	}
	public String getTeken4() 
	{	
		return getTeken(teken4);
	}
	public String getTeken5() 
	{	
		return getTeken(teken5);
	}
	public String getTeken6() 
	{	
		return getTeken(teken6);
	}

	//OK
//	public void draw(Context2d g)
//	{	g.setStrokeStyle(CssColor.make(0, 0, 0));
//		
//		if (doublePoints.size() > 0) 
//		{
//			
//			if (".".equals(teken))
//			{
//				g.setFillStyle(CssColor.make(0, 0, 0));
//				g.fillRect(doublePoints.get(0).getX(), doublePoints.get(0).getY(), 3, 3);
//				g.setFillStyle(CssColor.make(255, 255, 255));
//				return;
//			}
//			
//			boolean drawContinuous = "-".equals(teken) || "sqrt".equals(teken);			
//			g.beginPath();
//			g.moveTo(doublePoints.get(0).getX(), doublePoints.get(0).getY());
//			
//			for (int j = 1; j <doublePoints.size(); j++) 
//			{	if (drawContinuous)
//				{
//					g.lineTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
//				}
//				else // skip gaps for two-strokes
//				{	
//					if (distance(doublePoints.get(j-1), doublePoints.get(j)) < 3 * getStandardLength(doublePoints))
//						g.lineTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
//					else 
//						g.moveTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
//				}
//			}
//			g.stroke();
//		}
//		
//	}
	
	public void draw(Context2d g, int shiftX, int shiftY) {	
		g.setStrokeStyle(CssColor.make(0, 0, 0));
//		logger.info("Teken object : "+ teken);
		if (rawPoints.size() > 0) {
			if ( (".".equals(teken)) || ("*".equals(teken) ) ) {
				g.setFillStyle(CssColor.make(0, 0, 0));
				if(newMatch)
					g.setStrokeStyle(CssColor.make(0, 200, 0));
				if(newMatchWrong)
					g.setStrokeStyle(CssColor.make(200, 0, 0));
				if(oldMatch)
					g.setStrokeStyle(CssColor.make(0, 0, 200));
				if(oldMatchWrong)
					g.setStrokeStyle(CssColor.make(200, 0, 200));
//				g.fillRect(rawPoints.get(0).getX()+shiftX, rawPoints.get(0).getY()+shiftY, 3, 3);
				g.beginPath();
				g.arc(rawPoints.get(0).getX()+shiftX, rawPoints.get(0).getY()+shiftY, 2, 0, 2* Math.PI);
				g.closePath();
				g.fill();
				g.stroke();
				g.setFillStyle(CssColor.make(255, 255, 255));
				return;
			}
			
//			boolean drawContinuous = "-".equals(teken) || "sqrt".equals(teken);			
			boolean drawContinuous = true;
			
			for (int j = 0; parsePoints!=null && j <parsePoints.size(); j++) {	
				try {
					if(newMatch)
						g.setStrokeStyle(CssColor.make(0, 200, 0));
					if(newMatchWrong)
						g.setStrokeStyle(CssColor.make(200, 0, 0));
					if(oldMatch)
						g.setStrokeStyle(CssColor.make(0, 0, 200));
					if(oldMatchWrong)
						g.setStrokeStyle(CssColor.make(200, 0, 200));
					g.beginPath();
					g.arc(parsePoints.get(j).getX()+shiftX, parsePoints.get(j).getY()+shiftY, 2, 0, 2* Math.PI);
					g.closePath();
					g.fill();
					g.stroke();
				}
				catch (Exception e) {}
			}
			
			if(newMatch)
				g.setStrokeStyle(CssColor.make(0, 200, 0));
			if(newMatchWrong)
				g.setStrokeStyle(CssColor.make(200, 0, 0));
			if(oldMatch)
				g.setStrokeStyle(CssColor.make(0, 0, 200));
			if(oldMatchWrong)
				g.setStrokeStyle(CssColor.make(200, 0, 200));
			g.beginPath();
			g.moveTo(rawPoints.get(0).getX()+shiftX, rawPoints.get(0).getY()+shiftY);
			
			for (int j = 1; j <rawPoints.size(); j++) {	
				if ((!isTwoStrokeObject() || isTwoStrokeObject() && twoStrokeGap!= j)
						&& (!isThreeStrokeObject() || isThreeStrokeObject() && threeStrokeGap1!= j && threeStrokeGap2!= j)) {
					g.lineTo(rawPoints.get(j).getX()+shiftX, rawPoints.get(j).getY()+shiftY);
				} else { // skip gaps for two-strokes
					g.moveTo(rawPoints.get(j).getX()+shiftX, rawPoints.get(j).getY()+shiftY);
					if(teken.equals("i") || teken.equals("j")) {
						g.stroke();
						g.beginPath();
						g.arc(rawPoints.get(j).getX()+shiftX, rawPoints.get(j).getY()+shiftY, 2, 0, 2* Math.PI);
						g.closePath();
						g.fill();
						g.stroke();
					}
				}
			}
			g.stroke();
		}
		
	}
	
//	public void draw(Context2d g, int shiftX, int shiftY) {	
//		g.setStrokeStyle(CssColor.make(0, 0, 0));
//		
//		if (doublePoints.size() > 0) {
//			if (".".equals(teken)) {
//				g.setFillStyle(CssColor.make(0, 0, 0));
//				g.fillRect(doublePoints.get(0).getX(), doublePoints.get(0).getY(), 3, 3);
//				g.setFillStyle(CssColor.make(255, 255, 255));
//				return;
//			}
//			
//			boolean drawContinuous = "-".equals(teken) || "sqrt".equals(teken);			
//			g.beginPath();
//			g.moveTo(doublePoints.get(0).getX()+shiftX, doublePoints.get(0).getY()+shiftY);
//			
//			for (int j = 1; j <doublePoints.size(); j++) 
//			{	if (drawContinuous)
//				{
//					g.lineTo(doublePoints.get(j).getX()+shiftX, doublePoints.get(j).getY()+shiftY);
//				}
//				else // skip gaps for two-strokes
//				{	
//					if (distance(doublePoints.get(j-1), doublePoints.get(j)) < 3 * getStandardLength(doublePoints))
//						g.lineTo(doublePoints.get(j).getX()+shiftX, doublePoints.get(j).getY()+shiftY);
//					else 
//						g.moveTo(doublePoints.get(j).getX()+shiftX, doublePoints.get(j).getY()+shiftY);
//				}
//			}
//			g.stroke();
//		}
//		
//	}

	
	public void draw(Context2d g){
		draw(g, 0, 0);
	}


	
	public boolean isTwoStrokeObject() {
		return isTwoStrokeObject;
	}
	public boolean isThreeStrokeObject() {
		return isThreeStrokeObject;
	}
	
	//OK
	private void makeBox(ArrayList<Point> points) 
	{
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
		boxDiagonal = (int)Math.sqrt(box.width*box.width+box.height*box.height);
	}
	
	private void makeBoxDouble(ArrayList<DoublePoint> doublePoints) 
	{
		int xMin = maxWidth;
		int xMax = 0;
		int yMin = maxHeigth;
		int yMax = 0;
		for(int i = 0 ; i < doublePoints.size() ; i++) { 
			xMin = Math.min(xMin, (int) doublePoints.get(i).getX());
			yMin = Math.min(yMin, (int) doublePoints.get(i).getY());
			xMax = Math.max(xMax, (int) doublePoints.get(i).getX());
			yMax = Math.max(yMax, (int) doublePoints.get(i).getY());
		}
		box = new Rectangle(xMin, yMin, xMax-xMin+1, yMax-yMin+1);
		boxDiagonal = (int)Math.sqrt(box.width*box.width+box.height*box.height);
	}
	
	public int getDiagonal()
	{
		return boxDiagonal;
	}

	
	private ArrayList<DoublePoint> deepCopy(ArrayList<DoublePoint> toCopy)
	{
		ArrayList<DoublePoint> deepCopy = new ArrayList<DoublePoint>();
		for (int j = 0; j < toCopy.size(); j++)
		{
			deepCopy.add(new DoublePoint(toCopy.get(j).getX(), toCopy.get(j).getY()));
		}
		return deepCopy;
	}
	
	public static ArrayList<Point> intConvertSample(int[] data) 
	{
		ArrayList<Point> newPoints = new ArrayList<Point>();
		for (int i = 0; i < data.length - 1; i += 2) 
		{ 
			newPoints.add(new Point(data[i], data[i+1]));
		}
		return newPoints;
	} 

	public String parse(String key) {
		if (doublePoints.size() > 1) {
			//doublePoints = scaleToSquare(doublePoints);
			// hier nog een smoother?			
			doublePoints = standardizeToLength(doublePoints);
		}
		parsePoints = deepCopy(doublePoints);
		
		String gevondenTeken = OneStrokeMatcher.findTeken(this);
		if(gevondenTeken != null) {
			newMatch = true;
			if(!gevondenTeken.equals(key))
					newMatchWrong = true;
		}
		else {
			gevondenTeken = newStrokeMatcher.findTeken(this.doublePoints);
			if(gevondenTeken != null) {
				oldMatch = true;
				if(!gevondenTeken.equals(key))
						oldMatchWrong = true;
			}
		}
		return gevondenTeken;
	}
	//OK
	private String parse(ArrayList<DoublePoint> doublePoints) {
		
		if (doublePoints.size() > 1) {
			//doublePoints = scaleToSquare(doublePoints);
			// hier nog een smoother?			
			doublePoints = standardizeToLength(doublePoints);
		}
		parsePoints = deepCopy(doublePoints);
		
		String gevondenTeken = OneStrokeMatcher.findTeken(this);
		if(gevondenTeken != null) {
			newMatch = true;
		
			return gevondenTeken;
		}
		
		gevondenTeken = "";

		if (cNewStrokmatcher) {
			gevondenTeken = newStrokeMatcher.findTeken(doublePoints);
			teken1 = newStrokeMatcher.getTeken1();
			teken2 = newStrokeMatcher.getTeken2();
			teken3 = newStrokeMatcher.getTeken3();
			teken4 = newStrokeMatcher.getTeken4();
			teken5 = newStrokeMatcher.getTeken5();
			teken6 = newStrokeMatcher.getTeken6();
//			logger.info("parsing :: match = " + teken1 + "("+ newStrokeMatcher.getTekenId(0) + ")");
//			logger.info("parsing :: teken2 = " + teken2);
//			logger.info("parsing :: teken3 = " + teken3);
//			logger.info("parsing :: teken4 = " + teken4);
			
		} 
			
		
		
		//StrokeChecker.parse(this);
		
		if (!cNewStrokmatcher) {
			gevondenTeken = findTeken(doublePoints);
		}
		return gevondenTeken;
	}
	
	//OK
	private double distance(Point p1, Point p2) {
		return Math.sqrt(1.0*(p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
	}
	
	//OK
	private double distance(DoublePoint p1, DoublePoint p2) 
	{
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()));
	}
	
	//OK
	private ArrayList<DoublePoint> standardize(ArrayList<DoublePoint> doublePoints) 
	{
		if (doublePoints.size() < 1) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>(); 
		DoublePoint  lastPoint = doublePoints.get(0);
		pointsNew.add(lastPoint);
		for(int i = 1 ; i < doublePoints.size() ; i++) 
		{ 
			double unit = Math.max(box.width,box.height) / standardizeUnit;	
			if (distance(doublePoints.get(i),lastPoint) > unit || i == doublePoints.size() - 1) 
			{
				lastPoint = doublePoints.get(i);
				pointsNew.add(lastPoint);
			}
		}
		return pointsNew;
	}

	//OK
	private ArrayList<DoublePoint> insertPoint(ArrayList<DoublePoint> doublePoints)
	{
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		int maxIndex = 0;
		double maxSegment = -10;
		for (int i = 0; i < doublePoints.size() - 1; i++)
		{	DoublePoint beginPoint = doublePoints.get(i);
			DoublePoint endPoint = doublePoints.get(i+1);
			double segment = distance(beginPoint, endPoint);
			if (segment > maxSegment)
			{	maxSegment = segment;
				maxIndex = i;
			}	
		}
		for (int i = 0; i < doublePoints.size() - 1; i++)
		{	pointsNew.add(doublePoints.get(i));
			if (i == maxIndex)
			{	// interpolate
				DoublePoint beginPoint = doublePoints.get(i);
				DoublePoint endPoint = doublePoints.get(i+1);
				pointsNew.add(new DoublePoint((beginPoint.getX() + endPoint.getX()) / 2, (beginPoint.getY() + endPoint.getY()) / 2));
			}	
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		
		return pointsNew;
	}

	//OK
	private ArrayList<DoublePoint> standardizeToLength(ArrayList<DoublePoint> doublePoints) 
	{
		if (doublePoints.size() < 1) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>(); 
		double s = getStandardLength(doublePoints);
		double lengthRest = 0;
		DoublePoint  lastPoint = doublePoints.get(0);
		pointsNew.add(lastPoint);
		
		for (int i = 1; i < doublePoints.size() - 1; i++) 
		{
			DoublePoint pOld0 = doublePoints.get(i-1);
			DoublePoint pOld1 = doublePoints.get(i);
			double lengthOld = distance(pOld0, pOld1);
			
			if (s < lengthOld + lengthRest) 
			{
				double xNew = pOld0.getX() + (s-lengthRest)/lengthOld*(pOld1.getX()-pOld0.getX());
				double yNew = pOld0.getY() + (s-lengthRest)/lengthOld*(pOld1.getY()-pOld0.getY());
				DoublePoint pointNew = new DoublePoint(xNew, yNew);
				pointsNew.add(pointNew);
				lengthRest = distance(pointNew,pOld1);
				int teller = 0;
				while (s < lengthRest - 0.0000001) 
				{
					xNew = pointNew.getX() + s/lengthRest*(pOld1.getX()-pointNew.getX());
					yNew = pointNew.getY() + s/lengthRest*(pOld1.getY()-pointNew.getY());
					pointNew = new DoublePoint(xNew, yNew);
					pointsNew.add(pointNew);
					lengthRest = distance(pointNew,pOld1);
					teller++;
				}
			}
			else 
				lengthRest += lengthOld;
		}
		pointsNew.add(doublePoints.get(doublePoints.size()-1));
		return pointsNew;
	}
	
	//OK
	private double getStandardLength(ArrayList<DoublePoint> object) 
	{
		double length = 0;
		for(int j = 1 ; j < object.size() ; j++) {
			length += distance(object.get(j-1), object.get(j));
		}
		return length/(standardizeLengthNumber-1);
	}
	
	//OK
	private double getStandardLength(int[] sample) 
	{
		double length = 0;
		for(int j = 0 ; j < sample.length-3 ; j+=2) {
			length += distance(new Point(sample[j], sample[j+1]), new Point(sample[j+2], sample[j+3]));
		}
		return length/(standardizeLengthNumber-1);
	}
	
	//OK
	private double getDistance(ArrayList<DoublePoint> doublePoints, int[] sample) 
	{
		double distance = 0;
		for(int i = 0 ; i < doublePoints.size() ; i++) {
			if (i < sample.length / 2)
			{
				DoublePoint p = new DoublePoint(sample[2*i], sample[2*i+1]);
				distance += distance(doublePoints.get(i), p);
			}
		}
		distance = distance/(standardizeLengthNumber);
		return distance;
	}
	
	static WriteObject getWriteObjectFromSample(String key, int[] sample) {
		ArrayList<Point> woParsePoints = new ArrayList<Point>();
		for(int i = 0 ; i < sample.length/2 ; i++) {
				Point p = new Point(sample[2*i], sample[2*i+1]);
				woParsePoints.add(p);
		}
		return new WriteObject(key,woParsePoints);
	}
	
	static WriteObject getWriteObjectFromRefSample(int[] sample) {
		ArrayList<Point> woPoints = new ArrayList<Point>();
		for(int i = 0 ; i < sample.length/2 ; i++) {
				Point p = new Point(sample[2*i], sample[2*i+1]);
				woPoints.add(p);
		}
		return new WriteObject(true,woPoints);
	}
	
	//OK
	private double getDistanceCurving(ArrayList<DoublePoint> doublePoints, int[] sample) 
	{
		double distance = 0;
		for (int i = 1; i < doublePoints.size() - 1; i++) 
		{
			if (i < sample.length / 2 - 1)
			{
				DoublePoint p0 = new DoublePoint(sample[2*i-2], sample[2*i-1]);
				DoublePoint p1 = new DoublePoint(sample[2*i], sample[2*i+1]);
				DoublePoint p2 = new DoublePoint(sample[2*i+2], sample[2*i+3]);
				DoublePoint p01 = new DoublePoint(p1.getX()-p0.getX(), p1.getY()-p0.getY());
				DoublePoint p12 = new DoublePoint(p2.getX()-p1.getX(), p2.getY()-p1.getY());
				double detSample = (p01.getX()*p12.getY()-p01.getY()*p12.getX())/(distance(p0,p1)*distance(p1,p2));
				
				DoublePoint q0 = doublePoints.get(i-1);
				DoublePoint q1 = doublePoints.get(i);
				DoublePoint q2 = doublePoints.get(i+1);
				DoublePoint q01 = new DoublePoint(q1.getX()-q0.getX(), q1.getY()-q0.getY());
				DoublePoint q12 = new DoublePoint(q2.getX()-q1.getX(), q2.getY()-q1.getY());
				double detStroke = (q01.getX()*q12.getY()-q01.getY()*q12.getX())/(distance(q0,q1)*distance(q1,q2));
				
				distance +=  Math.abs(detSample - detStroke);
			}
		}
		return distance;
	}
	
	//OK
	private ArrayList<DoublePoint> scaleToSquare(ArrayList<DoublePoint> doublePoints) 
	{
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		double scaleX = 200.0 / box.width;
		double scaleY = 200.0 / box.height;
		if (scaleX > 4 * scaleY)
			scaleX = 1;
		else if (scaleY > 4 * scaleX)
			scaleY = 1;
		int startX = box.x;
		int startY = box.y;
		
//		System.out.print("Sample: ");
		
		for(int i = 0 ; i < doublePoints.size() ; i++) 
		{
			DoublePoint p = doublePoints.get(i);
			double xNew = scaleX*(p.getX()-startX);
			double yNew = scaleY*(p.getY()-startY);
			pointsNew.add(new DoublePoint(xNew,yNew));
			
//			System.out.print(""+pointsNew.get(i).getPoint().x + "," + pointsNew.get(i).getPoint().y + ",");
		}
		return pointsNew;
	}
	
	//OK
	public String findTeken(ArrayList<DoublePoint> doublePoints) 
	{
		teken1 = "null";
		teken2 = "null";
		teken3 = "null";
		teken4 = "null";
		teken5 = "null";
		teken6 = "null";
		double min1 = 1000;
		double min2 = 1000;
		double min3 = 1000;
		double min4 = 1000;
		double min5 = 1000;
		double min6 = 1000;
		
		Set keys = samples.keySet();
		Iterator<String> iterator = (Iterator<String>)keys.iterator();
	    while(iterator.hasNext()) 
	    {
	    	
	        String key = iterator.next();
	        int[] sample = samples.get(key);
	        double distanceMin = getDistance(doublePoints, sample);
	        
	       // if(key.equals("5H"))
	        	//System.out.println("Afstand tot 5H = " + distanceMin);
	       // logger.info("Afstand tot "+key+ " = " + distanceMin);
	        
	        if(distanceMin < min1)
	        {
	            teken1 = key;
				min1 = distanceMin;
			}
        	else if (distanceMin < min2)
        	{
        		teken2 = key;
        		min2 = distanceMin;
        	}
        	else if (distanceMin < min3)
        	{
        		teken3 = key;
        		min3 = distanceMin;
        	}
        	else if (distanceMin < min4)
        	{
        		teken4 = key;
        		min4 = distanceMin;
        	}
        	else if (distanceMin < min5)
        	{
        		teken5 = key;
        		min5 = distanceMin;
        	}
        	else if (distanceMin < min6)
        	{
        		teken6 = key;
        		min6 = distanceMin;
        	}

	        
	    }
	    return teken1;
	}
	
	public boolean isCloseTo(String input) {
		if(input.contentEquals(teken1)
			|| input.contentEquals(teken2)
			|| input.contentEquals(teken3)
			|| input.contentEquals(teken4))
			return true;
		return false;
	}
	
	public boolean hasCloseDistance(int distMin, WriteObject wo, int min1, int max1, int min2, int max2) {
		double dMin = distMin*boxDiagonal/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double dx = parsePoints.get(i).getX() - wo.getParsePoints().get(j).getX();
				double dy = parsePoints.get(i).getY() - wo.getParsePoints().get(j).getY();
				double d = Math.sqrt(dx*dx + dy*dy);
				distance = Math.min(distance, d);
			}
		}
		//logger.info("distance = " + distance);
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public boolean hasCloseXDistance(int distMin, WriteObject wo, int min1, int max1, int min2, int max2) {
		
		double dMin = distMin*boxDiagonal/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d = Math.abs(parsePoints.get(i).getX() - wo.getParsePoints().get(j).getX());
				distance = Math.min(distance, d);
			}
		}
		//logger.info("xDistance = " + distance);
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public boolean hasDirection (int angle, double tolerance, int pointNrStart, int pointNrEnd, int minSteps) {
		int counter = 0;
		for(int i=pointNrStart ; i<pointNrEnd+1 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i-1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i-1).getY();
			
			int angleStep = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
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
	
	public boolean hasCloseYDistance(int distMin, WriteObject wo, int min1, int max1, int min2, int max2) {
		double dMin = distMin*boxDiagonal/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d = Math.abs(parsePoints.get(i).getY() - wo.getParsePoints().get(j).getY());
				distance = Math.min(distance, d);
			}
		}
		if(distance < dMin)
			return true;
		
		return false;
	}
	
	public boolean hasYDistance(int dist, int distMin, WriteObject wo, int min1, int max1, int min2, int max2) {
		double dMin = distMin*boxDiagonal/100;
		dist = dist*boxDiagonal/100;
		double distance = 1000;
		for(int i=min1 ; i<max1 ; i++) {
			for(int j=min2 ; j<max2 ; j++) {
				double d =   wo.getParsePoints().get(j).getY() - parsePoints.get(i).getY();
				//d = d*boxDiagonal/200;
				distance = Math.min(distance, d);
			}
		}
		//logger.info("Ydistance = "+distance);
		if(Math.abs(distance-dist) < dMin)
			return true;
		
		return false;
	}
	
	public boolean hasSharpAngle(int angle, int tolerance, int pointNrStart, int pointNrEnd) {
		for(int i=pointNrStart ; i<pointNrEnd-3 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i+1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i+1).getY();
			double ddx = parsePoints.get(i+2).getX() - parsePoints.get(i+3).getX();
			double ddy = parsePoints.get(i+2).getY() - parsePoints.get(i+3).getY();
			
			int angleStep1 = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
			int angleStep2 = (int)(180.0*(Math.atan2(-ddy, ddx)/Math.PI));
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
	
	public boolean hasIncreasingAngle(int tolerance, int pointNrStart, int pointNrEnd) {
		boolean increasing = true;
		int teller = 0;
		int totalIncrease = 0;
		for(int i=pointNrStart ; i<pointNrEnd-3 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i+1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i+1).getY();
			double ddx = parsePoints.get(i+1).getX() - parsePoints.get(i+2).getX();
			double ddy = parsePoints.get(i+1).getY() - parsePoints.get(i+2).getY();
			
			int angleStep1 = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
			int angleStep2 = (int)(180.0*(Math.atan2(-ddy, ddx)/Math.PI));
			if(angleStep2-angleStep1>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1<-180)
				angleStep2 += 360;
			increasing = increasing && angleStep2-angleStep1 >= -tolerance;
			totalIncrease += angleStep2-angleStep1;
			teller++;	
		}
		int averageIncrease = totalIncrease/teller;
		return increasing && averageIncrease>0;
	}
	
	public boolean hasDecreasingAngle(int tolerance, int pointNrStart, int pointNrEnd) {
		boolean decreasing = true;
		int teller = 0;
		int totalDecrease = 0;
		for(int i=pointNrStart ; i<pointNrEnd-3 ; i++) {
			double dx = parsePoints.get(i).getX() - parsePoints.get(i+1).getX();
			double dy = parsePoints.get(i).getY() - parsePoints.get(i+1).getY();
			double ddx = parsePoints.get(i+1).getX() - parsePoints.get(i+2).getX();
			double ddy = parsePoints.get(i+1).getY() - parsePoints.get(i+2).getY();
			
			int angleStep1 = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
			int angleStep2 = (int)(180.0*(Math.atan2(-ddy, ddx)/Math.PI));
			if(angleStep2-angleStep1>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1<-180)
				angleStep2 += 360;
			//logger.info("angle"+(angleStep2-angleStep1));
			decreasing = decreasing && angleStep2-angleStep1 <= tolerance;
			totalDecrease += angleStep2-angleStep1;
			teller++;
				
		}
		int averageDecrease = totalDecrease/teller;
		return decreasing && averageDecrease<0;
		
	}
	
	
	public String printStroke()
	{	
		String result = "sample_X = {";
	
		for (int i = 0; i < parsePoints.size(); i++)
		{	
			DoublePoint p = parsePoints.get(i);
	
			if (i < parsePoints.size() - 1)
			{	//System.out.print("" + (int) Math.round(p.x) + "," + (int) Math.round(p.y) + ",");
				result += (int) Math.round(p.getX()) + "," + (int) Math.round(p.getY()) + ",";
			
			}
			else
			{	result += (int) Math.round(p.getX()) + "," + (int) Math.round(p.getY()) + "};";
				//System.out.print("" + (int) Math.round(p.x) + "," + (int) Math.round(p.y) + "};");
			
			}
		
			
		}
		//System.out.println();
		return result;
	}
	
}
