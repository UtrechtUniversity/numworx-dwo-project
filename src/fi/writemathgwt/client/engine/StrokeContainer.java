package fi.writemathgwt.client.engine;

import java.awt.Graphics;
import java.util.List;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;

import fi.writemathgwt.client.engine.filters.FourStrokeProcessor;
import fi.writemathgwt.client.engine.filters.ThreeStrokeProcessor;
import fi.writemathgwt.client.engine.filters.TwoStrokeDirFilter;
import fi.writemathgwt.client.engine.filters.TwoStrokeProcessor;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class StrokeContainer {
	
	protected static Logger logger = Logger.getLogger("StrokeContainer");
	
	private ArrayList<Stroke> strokes;
	private ArrayList<WMObject> wmObjects;
	private String formulaString = "";
	public double defaultAverageHeight = 50;
	public double averageHeight = defaultAverageHeight;
	public double averageBaseLine = 0;
	private DoubleRectangle parseArea;
	private boolean parseable = true;
	
	private WMObjectLine mainLine = new WMObjectLine();
	
	
	public StrokeContainer() {
		strokes = new ArrayList<Stroke>();
		wmObjects = new ArrayList<WMObject>();
		
	}
	
	public boolean addStroke(Stroke stroke) {
		return addStroke(stroke, true);
	}
	
	public boolean addStroke(Stroke stroke, boolean parse) {
		if(isOutOfLine(stroke))
			return true;
		strokes.add(stroke);
		updateParseArea(stroke);
		if(parse) {
			parseStrokes();
			
			mainLine = new WMObjectLine(wmObjects);
			return true;
		}
		else
			return true;
//		}
//		else {
//			if(!stroke.isParseable()){
//				wmObjects.add(new WMObject(stroke, ""));
//				parseable = false;
//				return false;
//			}
//			else {
//				strokes.remove(stroke);
//				makeParseArea();
//				return false;
//			}
//		}
		
	}
	
	private boolean checkStrokeParseable(Stroke stroke) {
		double length = stroke.getLength();
		boolean magLang = "back".equals(StrokeMatcher.findTeken(this,stroke)) || "-".equals(StrokeMatcher.findTeken(this,stroke)) || "sqrt".equals(StrokeMatcher.findTeken(this,stroke));
		if(length/averageHeight>9 && !magLang)
			return false;
		return true;
	}
	
	public String getFormulaString() {
		return formulaString;
	}
	
	public ArrayList<Stroke> getStrokes() {
		return strokes;
	}
	
	public ArrayList<WMObject> getWMObjects() {
		return wmObjects;
	}

	
	public DoubleRectangle getBoundingBox() {
		return parseArea;
	}
	
	public void updateParseable() {
		parseable = true;
		for (int i = 0; i < strokes.size(); i++) {
			if(!strokes.get(i).isParseable()) {
				parseable = false;
				return;
			}
		}
	}
	
	public boolean isParseable() {
		return parseable;
	}
	
	public void wis() {
		strokes.clear();
		wmObjects.clear();
		formulaString="";
	}
	
	public void addWriteObject(String teken, Stroke stroke) {
		strokes.add(stroke);
		wmObjects.add(new WMObject(stroke, teken));
	}
	
	public void addWriteObject(String teken, Stroke stroke1, Stroke strokes2) {
		
	}
	
	public void addWriteObject(String teken, ArrayList<Point> points) {
		Stroke stroke = new Stroke(points);
		strokes.add(stroke);
		wmObjects.add(new WMObject(stroke, teken));
	}
	
	public void addWriteObject(String teken, ArrayList<Point> points1, ArrayList<Point> points2) {
		Stroke stroke1 = new Stroke(points1);
		Stroke stroke2 = new Stroke(points2);
		strokes.add(stroke1);
		strokes.add(stroke2);
		wmObjects.add(new WMObject(stroke1, stroke2, teken));
	}
	
	private void parseStrokes() {
		//logger.info("0. "+System.currentTimeMillis());
		String s1 = null;
		String s2 = null;
		String s3 = null;
		String s4 = null;
		if(strokes.size()>3) {
			Stroke stroke1 = strokes.get(strokes.size()-4);
			Stroke stroke2 = strokes.get(strokes.size()-3);
			Stroke stroke3 = strokes.get(strokes.size()-2);
			Stroke stroke4 = strokes.get(strokes.size()-1);
			s4 = FourStrokeProcessor.findFourStrokeTeken(stroke1, stroke2, stroke3, stroke4);
			if(s4!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, stroke3, stroke4, s4);
				int teller = 3;
				while(teller>0) {
					teller -= wmObjects.get(wmObjects.size()-1).getStrokes().size();
					wmObjects.remove(wmObjects.size()-1); 
				}
				wmObjects.add(wo);
			}
			//logger.info("1. "+System.currentTimeMillis());
		}
		if(s4==null &&strokes.size()>2) {// && wmObjects.size()>1 && wmObjects.get(wmObjects.size()-1).isOneStroke() && wmObjects.get(wmObjects.size()-2).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-3);
			Stroke stroke2 = strokes.get(strokes.size()-2);
			Stroke stroke3 = strokes.get(strokes.size()-1);
			s3 = ThreeStrokeProcessor.findThreeStrokeTeken(stroke1, stroke2, stroke3);
			if(s3!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, stroke3, s3);
				int teller = 2;
				while(teller>0) {
					teller -= wmObjects.get(wmObjects.size()-1).getStrokes().size();
					wmObjects.remove(wmObjects.size()-1); 
				}
				wmObjects.add(wo);
			}
			//logger.info("2. "+System.currentTimeMillis());
		}
		if(s4==null && s3==null && strokes.size()>1 &&wmObjects.size()>0 && wmObjects.get(wmObjects.size()-1).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-2);
			Stroke stroke2 = strokes.get(strokes.size()-1);
			s2 = TwoStrokeProcessor.findTwoStrokeTeken(this, stroke1, stroke2);
			if(s2!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, s2);
				wmObjects.remove(wmObjects.size()-1);
				wmObjects.add(wo);
			}
			//logger.info("3. "+System.currentTimeMillis());
		}
		if(s4==null && s3==null && s2==null && strokes.size()>0) {
			boolean b = tryAsStrokeExtension(strokes.get(strokes.size()-1));
			//logger.info("3b. "+System.currentTimeMillis());
			if(b) {
				strokes.remove(strokes.get(strokes.size()-1));
			}
			else {
				boolean bb = tryDelayedTwoStroke(strokes.get(strokes.size()-1));
				//logger.info("3c. "+System.currentTimeMillis());
				if(bb){
				
				}
				else 
				{
					s1 = StrokeMatcher.findTekenRaw(this,strokes.get(strokes.size()-1));
					//logger.info("3d. "+System.currentTimeMillis());
					WMObject wo = new WMObject(strokes.get(strokes.size()-1), s1);
					//logger.info("3e. "+System.currentTimeMillis());
					if("back".equals(s1)) 
						doBack(wo);
					else {
						wmObjects.add(wo);
						//updateAverageHeight();
					}
				}
				//logger.info("4. "+System.currentTimeMillis());
			}
		}
		formulaString = FormulaProcessor.parseFormuleNew(this, parseArea);
		//logger.info("5. "+System.currentTimeMillis());
		updateAverageHeight();
		//updateAverageBaseLine();
		
	}
	
	public boolean isNearStroke(int x, int y, int distance) {
		DoubleRectangle r = new DoubleRectangle(x-distance/2,y-distance/2,distance,distance);
		for (int i = 0; i < strokes.size(); i++) {
			Stroke stroke = strokes.get(i);
			for(int j=0 ; j<stroke.getParsePoints().size() ; j++) {
				if(r.contains(stroke.getParsePoints().get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeStrokes(int x, int y) {
		DoubleRectangle r = new DoubleRectangle(x-10,y-10,20,20);
	
		for (int i = 0; i < wmObjects.size(); i++) {
			ArrayList<Stroke> wmStrokes = wmObjects.get(i).getStrokes();
			for (int j = 0; j < wmStrokes.size(); j++) {
				Stroke stroke = wmStrokes.get(j);
				for(int k=0 ; k<stroke.getParsePoints().size() ; k++) {
					if(r.contains(stroke.getParsePoints().get(k))) {
						wmObjects.remove(wmObjects.get(i));
						strokes.remove(stroke);
						break;
					}
				}
			}
		}
		for (int i = 0; i < strokes.size(); i++) {
			Stroke stroke = strokes.get(i);
			for(int j=0 ; j<stroke.getParsePoints().size() ; j++) {
				if(r.contains(stroke.getParsePoints().get(j))) {
					strokes.remove(stroke);
					break;
				}
			}
		}
		makeParseArea();
	}
	
	public void removeWmObjects(int x, int y) {
		for (int i = 0; i < wmObjects.size(); i++) {
			if(wmObjects.get(i).getBox().contains(new DoubleRectangle(x,y,0,0),-4,-4)) {
				wmObjects.remove(wmObjects.get(i));
				break;
			}
		}
		makeParseArea();
	}
	
	public void removeWmObjects() {
		wmObjects.clear();
	}
	
	public void removeStrokes() {
		strokes.clear();
	}
	
	private  ArrayList<WMObject> removeAllInBox(ArrayList<WMObject> wo, DoubleRectangle box) {
		ArrayList<WMObject> woNew = new ArrayList<WMObject>();
		for (int i = 0; i < wo.size(); i++) {
			WMObject awo = wo.get(i);
			boolean wortelLatenStaan = (wo.size() > 1) && awo.getTeken().equals("sqrt") && (awo.getBox().width > box.width);	
			if (!box.contains(awo.getBoxMid()) || wortelLatenStaan)
				woNew.add(awo);
			else
				for (int j = 0; j < awo.getStrokes().size(); j++) 
					strokes.remove(awo.getStrokes().get(j));
		}
		
		return woNew;
	}
	
	private void doBack(WMObject wo) {
		double x = wo.getBox().x;
		double y = wo.getBoxMid().y - averageHeight ;
		double w = wo.getBox().width;
		double h = 2*averageHeight;
		DoubleRectangle box = new DoubleRectangle(x, y, w, h);
		int objectsBefore = wmObjects.size();
		wmObjects = removeAllInBox(wmObjects,box);
		int objectsAfter = wmObjects.size();
		if (objectsBefore == objectsAfter) {	
			wo = new WMObject(strokes.get(strokes.size()-1),"-");
			wmObjects.add(wo);
			updateAverageHeight();
			//updateAverageBaseLine();
		}
		else {
			strokes.remove(strokes.size()-1);
			makeParseArea();
			updateParseable();
		}
	}
	
	private void updateAverageHeight() {
//		if ("-".equals(wo.getTeken())|| 
//			".".equals(wo.getTeken())|| 
//			"sqrt".equals(wo.getTeken()) || 
//			"=".equals(wo.getTeken())) 
//			return;
//		averageHeight = (wmObjects.size()*averageHeight + wo.getBox().height)/(wmObjects.size()+1);	
		
		double heightSum = 0;
		int cnt = 0;
		for (int i = 0; i < wmObjects.size(); i++) {
			if ("-".equals(wmObjects.get(i).getTeken())
					|| ".".equals(wmObjects.get(i).getTeken())
					|| ",".equals(wmObjects.get(i).getTeken())
					|| "sqrt".equals(wmObjects.get(i).getTeken()) 
					|| "=".equals(wmObjects.get(i).getTeken())
					|| "\u2190".equals(wmObjects.get(i).getTeken())
					|| "\u2192".equals(wmObjects.get(i).getTeken())
					)
				;
			else {
				heightSum += wmObjects.get(i).getXHeight();
				//logger.info("Teken: "+wmObjects.get(i).getTeken());
				//logger.info("isMacht: "+wmObjects.get(i).isMachtVan());
				//logger.info("isAsc: "+wmObjects.get(i).hasAscent());
				//logger.info("isDesc: "+wmObjects.get(i).hasDescent());
				cnt++;
			}
		}
		if(cnt>0)
			averageHeight = heightSum/cnt;
		else 
			averageHeight = defaultAverageHeight;
	}
	
//	private void updateAverageBaseLine() {
//		double baseLineSum = 0;
//		int cnt = 0;
//		for (int i = 0; i < wmObjects.size(); i++) {
//			if ("-".equals(wmObjects.get(i).getTeken())
//					|| "*".equals(wmObjects.get(i).getTeken())
//					|| ",".equals(wmObjects.get(i).getTeken())
//					|| "sqrt".equals(wmObjects.get(i).getTeken()) 
//					|| "=".equals(wmObjects.get(i).getTeken())
//					|| "\u2190".equals(wmObjects.get(i).getTeken())
//					|| "\u2192".equals(wmObjects.get(i).getTeken())
//					) {
//				baseLineSum += wmObjects.get(i).getBoxMid().y + averageHeight/2;
//				cnt++;
//			}
//			else if(wmObjects.get(i).isMachtVan() == null){
//				baseLineSum += wmObjects.get(i).getXBox().y + wmObjects.get(i).getXBox().height;
//				//logger.info("Teken: "+wmObjects.get(i).getTeken());
//				//logger.info("isMacht: "+wmObjects.get(i).isMachtVan());
//				//logger.info("isAsc: "+wmObjects.get(i).hasAscent());
//				//logger.info("isDesc: "+wmObjects.get(i).hasDescent());
//				cnt++;
//			}
//		}
//		if(cnt>0)
//			averageBaseLine = baseLineSum/cnt;
//		else 
//			averageBaseLine = 0;
//	}
	
	private void updateParseArea(Stroke stroke) {
		if(strokes.size()==1) 
			parseArea = new DoubleRectangle(stroke.getParsePointsbox().x, stroke.getParsePointsbox().y, stroke.getParsePointsbox().width, stroke.getParsePointsbox().height);
		else if(strokes.size()>1) {
			double xmin = Math.min(parseArea.x, stroke.getParsePointsbox().x);
			double ymin = Math.min(parseArea.y, stroke.getParsePointsbox().y);
			double xmax = Math.max(parseArea.x + parseArea.width, stroke.getParsePointsbox().x + stroke.getParsePointsbox().width);
			double ymax = Math.max(parseArea.y + parseArea.height, stroke.getParsePointsbox().y + stroke.getParsePointsbox().height);
			parseArea = new DoubleRectangle(xmin, ymin, xmax-xmin, ymax-ymin);
		}
	}
	
	public void makeParseArea() {
		if(strokes.size()==0)
			parseArea = null;
		double xMin = 10000;
		double xMax = -10000;
		double yMin = 10000;
		double yMax = -10000;
		for (int i = 0; i < strokes.size(); i++) {
			xMin = Math.min((int)xMin,  strokes.get(i).getParsePointsbox().x);
			yMin = Math.min((int)yMin,  strokes.get(i).getParsePointsbox().y);
			xMax = Math.max((int)xMax,  strokes.get(i).getParsePointsbox().x + strokes.get(i).getParsePointsbox().width);
			yMax = Math.max((int)yMax,  strokes.get(i).getParsePointsbox().y + strokes.get(i).getParsePointsbox().height);
		}
		parseArea = new DoubleRectangle(xMin, yMin, xMax-xMin, yMax-yMin);
	}
	
	private boolean tryAsStrokeExtension(Stroke extension) {
		for(int i=0 ; i<wmObjects.size() ; i++) {
			Stroke stroke = null;
			if(wmObjects.get(i).isOneStroke() && (wmObjects.get(i).getTeken().equals("-") || wmObjects.get(i).getTeken().equals("sqrt"))) {
				stroke = wmObjects.get(i).getStrokes().get(0);
				if(TwoStrokeProcessor.hasCloseDistance(stroke, extension, 10, 37, 39, 0, 1)) {
					stroke.extendRight(extension);
					wmObjects.remove(wmObjects.get(i));
					String tekenNew = StrokeMatcher.findTekenRaw(this,stroke);
					if(tekenNew.equals("back"))
						tekenNew = "-";
					WMObject wmObjectNew = new WMObject(stroke, tekenNew);
					wmObjects.add(i, wmObjectNew);
					return true;
				}
				if(TwoStrokeProcessor.hasCloseDistance(stroke, extension, 10, 0, 2, 0, 1)) {
					stroke.extendLeft(extension);
					wmObjects.remove(wmObjects.get(i));
					String tekenNew = StrokeMatcher.findTekenRaw(this,stroke);
					if(tekenNew.equals("back"))
						tekenNew = "-";
					WMObject wmObjectNew = new WMObject(stroke, tekenNew);
					wmObjects.add(wmObjectNew);
					return true;
				}
			}
		}
		return false;
	}

	private boolean tryDelayedTwoStroke(Stroke stroke2) {
		boolean b1 = TwoStrokeDirFilter.checkDir(stroke2, "+H1");
		boolean b2 = TwoStrokeDirFilter.checkDir(stroke2, "+H2");
		boolean b3 = TwoStrokeDirFilter.checkDir(stroke2, "+H2+");
		boolean b4 = ".".equals(StrokeMatcher.findTekenRaw(this,stroke2));
		if(!b1 && !b2 && !b3 && !b4) {
			//logger.info("geen juistestroke");
			return false;
		}
		for(int i=0 ; i<wmObjects.size() ; i++) {
			Stroke stroke1 = null;
			if(wmObjects.get(i).isOneStroke()) {
				stroke1 = wmObjects.get(i).getStrokes().get(0);
				double midx1 = stroke1.getParsePointsbox().x+stroke1.getParsePointsbox().width/2;
				double midx2 = stroke2.getParsePointsbox().x+stroke2.getParsePointsbox().width/2;
				if(Math.abs(midx1-midx2)<stroke1.getParsePointsbox().getDiagonal()) {
					String s = TwoStrokeProcessor.findTwoStrokeTeken(this, stroke1, stroke2);
					if(s!=null) {
						wmObjects.remove(wmObjects.get(i));
						WMObject wo = new WMObject(stroke1, stroke2, s);
						wmObjects.add(wo);
						updateAverageHeight();
						//updateAverageBaseLine();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean isDelayedPointStroke(Stroke stroke2) {
		
		boolean b4 = ".".equals(StrokeMatcher.findTekenRaw(this,stroke2));
		if(!b4) {
			//logger.info("geen juistestroke");
			return false;
		}
		for(int i=0 ; i<wmObjects.size() ; i++) {
			Stroke stroke1 = null;
			if(wmObjects.get(i).isOneStroke()) {
				stroke1 = wmObjects.get(i).getStrokes().get(0);
				double midx1 = stroke1.getParsePointsbox().x+stroke1.getParsePointsbox().width/2;
				double midx2 = stroke2.getParsePointsbox().x+stroke2.getParsePointsbox().width/2;
				if(Math.abs(midx1-midx2)<stroke1.getParsePointsbox().getDiagonal()) {
					String s = TwoStrokeProcessor.findTwoStrokeTeken(this, stroke1, stroke2);
					if(s!=null) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void translate(int dx, int dy) {
		for (int i = 0; i < strokes.size(); i++) {
			strokes.get(i).translate(dx, dy);
		}
		for (int i = 0; i < wmObjects.size(); i++) {
			wmObjects.get(i).translate(dx, dy);
		}
		if(parseArea!=null)
			parseArea.translate(dx, dy);
	}
	
	public void scale(double factor) {
		if(parseArea!=null)
			scale(parseArea.x, parseArea.y, factor);
	}
	
	public void scale(double cx, double cy,double factor) {
		for (int i = 0; i < strokes.size(); i++) {
			strokes.get(i).scale(cx, cy, factor);
		}
		for (int i = 0; i < wmObjects.size(); i++) {
			wmObjects.get(i).scale(cx, cy, factor);
		}
		if(parseArea!=null)
			parseArea.scale(cx, cy, factor);
		averageHeight *= factor;
	}
	
	public boolean contains(int x, int y) {
		if(parseArea==null)
			return false;
		return parseArea.contains(x, y);
	}
	
	public double getDiagonal() {
		if(parseArea!=null)
			return parseArea.getDiagonal();
		return 0;
	}
	
	public WMObjectLine getMainLine() {
		return mainLine;
	}
	
	public boolean isOutOfLine(Stroke stroke) {
		if(strokes.size()==0)
			return false;
		if(stroke.getParsePointsbox().x - (parseArea.x+parseArea.width) > 2.5*averageHeight
				|| parseArea.x - (stroke.getParsePointsbox().x+stroke.getParsePointsbox().width)  > 5*averageHeight) 
			return true;
		if(stroke.getParsePointsbox().getDiagonal()<5 && 
				(stroke.getParsePointsbox().x - (parseArea.x+parseArea.width) >1.0*averageHeight
				|| parseArea.x - (stroke.getParsePointsbox().x+stroke.getParsePointsbox().width)  > 0.5*averageHeight)) 
			return true;
		if(stroke.getParsePointsbox().getDiagonal()<5 && 
				(stroke.getParsePointsbox().y - (parseArea.y+parseArea.height) > 0.25*averageHeight
				|| parseArea.y - (stroke.getParsePointsbox().y+stroke.getParsePointsbox().height)  > averageHeight)) 
			return true;
		if(stroke.getParsePointsbox().getDiagonal()<5 && !isDelayedPointStroke(stroke) && 
				(parseArea.y - (stroke.getParsePointsbox().y+stroke.getParsePointsbox().height)  > 0)) 
			return true;
		return false;
	}
	
	public HashMap<String,Object> getState() {
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		ArrayList<Map<String,Object>> strokeList = new ArrayList<Map<String,Object>>();
		ArrayList<Object> wmStrokeIndicesList = new ArrayList<Object>();
		ArrayList<String> wmStrokeTekenList = new ArrayList<String>();
		for (int i = 0; i < strokes.size(); i++) {
			strokeList.add(strokes.get(i).getState());
		}
		for (int i = 0; i < wmObjects.size(); i++) {
			ArrayList<Stroke> wmStrokes = wmObjects.get(i).getStrokes();
			ArrayList<Integer> wmStrokeIndices = new ArrayList<Integer>();
			for (int j = 0; j < wmStrokes.size(); j++) {
				wmStrokeIndices.add(strokes.indexOf(wmStrokes.get(j)));
			}
			wmStrokeIndicesList.add(wmStrokeIndices);
			wmStrokeTekenList.add(wmObjects.get(i).getTekenRaw());
		}
		
		h.put("strokeList", strokeList);
		h.put("wmStrokeIndicesList", wmStrokeIndicesList);
		h.put("wmStrokeTekenList", wmStrokeTekenList);
		h.put("formulaString", formulaString);
		h.put("averageHeight", new Double(averageHeight));
		h.put("parseable", new Boolean(parseable));
		return h;
	}
	
	public void setState(Map<String,Object> map) {
		if(map == null || map.isEmpty())
			return;
		wis();
		
		//logger.info(map.toString());
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		List<Map<String,Object>> strokeList = new ArrayList<Map<String,Object>>();
		List<Object> wmStrokeIndicesList = new ArrayList<Object>();
		List<String> wmStrokeTekenList = new ArrayList<String>();
		
		if (launchState.containsKey("strokeList"))
			strokeList = launchState.getMapList("strokeList");
		if (launchState.containsKey("wmStrokeIndicesList"))
			wmStrokeIndicesList =launchState. getList("wmStrokeIndicesList");
		if (launchState.containsKey("wmStrokeTekenList"))
			wmStrokeTekenList = launchState.getStringList("wmStrokeTekenList");
		if (launchState.containsKey("formulaString"))
			formulaString = launchState.getString("formulaString");
		if (launchState.containsKey("averageHeight"))
			averageHeight = launchState.getDouble("averageHeight");
		if (launchState.containsKey("parseable"))
			parseable = launchState.getBoolean("parseable");
		
		
		for (int i = 0; i < strokeList.size(); i++)	{	
			Stroke stroke = Stroke.setState(strokeList.get(i));
			if(stroke!=null)
				strokes.add(stroke);
		}
		for (int i = 0; i < wmStrokeIndicesList.size(); i++)	{	
			List<Object> wmStrokeIndices = JSONUtilities.toArrayList(wmStrokeIndicesList.get(i));
			
			ArrayList<Object> indices = new ArrayList<Object>(wmStrokeIndices);
			ArrayList<Stroke> wmStrokes = new ArrayList<Stroke>();
			for (int j = 0; j < indices.size(); j++) {
				int index = ((Number)indices.get(j)).intValue();
				wmStrokes.add(strokes.get(index));
			}
			WMObject wmObject = new WMObject(wmStrokes,wmStrokeTekenList.get(i));
			//logger.info("WMObject nr:"+i+" Teken:"+wmStrokeTekenList.get(i));
			wmObjects.add(wmObject);
		}
		
		makeParseArea();
		
	}
	
	
}
