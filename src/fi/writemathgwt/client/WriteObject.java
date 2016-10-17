package fi.writemathgwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.writemathgwt.strokematcher.StrokeMatcherWrapper;


public class WriteObject 
{
	private final static boolean cNewStrokmatcher = false;
	public static HashMap<String, int[]>  samples;
 	
	//OK
	public static void initSamples(int tekenSet) 
	{
		samples = Samples20.init(tekenSet);
	}
	
	private ArrayList<DoublePoint> doublePoints;
	private ArrayList<DoublePoint> parsePoints;
	private Rectangle box;
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
	
//	StrokeMatcherWrapper newStrokeMatcher;
	
	//OK
	public WriteObject(ArrayList<Point> points) {
//		newStrokeMatcher = new StrokeMatcherWrapper();
		
		doublePoints = new ArrayList<DoublePoint>();
		double size = 0;
		for(int i = 0 ; i < points.size() ; i++) 
		{
			doublePoints.add(new DoublePoint(points.get(i).x, points.get(i).y));
			size = Math.max(size, distance(points.get(0), points.get(i)));
		}
		makeBox(points);
		
		if (size < 3) 
		{
			teken = ".";
			return;
		}
		
		if (!cNewStrokmatcher) {
			// try to standarize
			if (doublePoints.size() >= 20)
			{	ArrayList<DoublePoint> tempDoublePoints = standardize(doublePoints);
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
		
		teken = parse(doublePoints);
		
//System.out.println("WriteObject: " + teken);
	}

	//OK
	public WriteObject(String teken, ArrayList<Point> points){
		makeBox(points);
		doublePoints = new ArrayList<DoublePoint>();
		for(int i = 0 ; i <points.size() ; i++) {
			doublePoints.add(points.get(i).getDoublePoint());
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

	//OK
	public void draw(Context2d g)
	{	g.setStrokeStyle(CssColor.make(0, 0, 0));
		
		if (doublePoints.size() > 0) 
		{
			
			if (".".equals(teken))
			{
				g.setFillStyle(CssColor.make(0, 0, 0));
				g.fillRect(doublePoints.get(0).getX(), doublePoints.get(0).getY(), 3, 3);
				g.setFillStyle(CssColor.make(255, 255, 255));
				return;
			}
			
			boolean drawContinuous = "-".equals(teken) || "sqrt".equals(teken);			
			g.beginPath();
			g.moveTo(doublePoints.get(0).getX(), doublePoints.get(0).getY());
			
			for (int j = 1; j <doublePoints.size(); j++) 
			{	if (drawContinuous)
				{
					g.lineTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
				}
				else // skip gaps for two-strokes
				{	
					if (distance(doublePoints.get(j-1), doublePoints.get(j)) < 3 * getStandardLength(doublePoints))
						g.lineTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
					else 
						g.moveTo(doublePoints.get(j).getX(), doublePoints.get(j).getY());
				}
			}
			g.stroke();
		}
		
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

	//OK
	private String parse(ArrayList<DoublePoint> doublePoints) 
	{
		if (cNewStrokmatcher) {
			String gevondenTeken = "";
//			String gevondenTeken = newStrokeMatcher.findTeken(doublePoints);
//			teken1 = newStrokeMatcher.getTeken1();
//			teken2 = newStrokeMatcher.getTeken2();
//			teken3 = newStrokeMatcher.getTeken3();
//			teken4 = newStrokeMatcher.getTeken4();
			
			return gevondenTeken;
		} else {
			if (doublePoints.size() > 1) {
				doublePoints = scaleToSquare(doublePoints);
				// hier nog een smoother?			
				doublePoints = standardizeToLength(doublePoints);
			}
			parsePoints = deepCopy(doublePoints);
		
			return findTeken(doublePoints);
		}
	}
	
	//OK
	private double distance(Point p1, Point p2) 
	{
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
		double min1 = 1000;
		double min2 = 1000;
		double min3 = 1000;
		double min4 = 1000;
		
		Set keys = samples.keySet();
		Iterator<String> iterator = (Iterator<String>)keys.iterator();
	    while(iterator.hasNext()) 
	    {
	        String key = iterator.next();
	        int[] sample = samples.get(key);
	        double distanceMin = getDistance(doublePoints, sample);
	        
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

	        
	    }
	    return teken1;
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
