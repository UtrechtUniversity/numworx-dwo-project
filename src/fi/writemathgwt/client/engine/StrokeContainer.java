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
import fi.writemathgwt.client.engine.filters.TwoStrokeProcessor;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class StrokeContainer {
	
	private static Logger logger = Logger.getLogger("StrokeContainer");
	
	private ArrayList<Stroke> strokes;
	private ArrayList<WMObject> wmObjects;
	private String formulaString = "";
	public double defaultAverageHeight = 50;
	public double averageHeight = defaultAverageHeight;
	private DoubleRectangle parseArea;
	private boolean parseable = true;
	
	
	public StrokeContainer() {
		strokes = new ArrayList<Stroke>();
		wmObjects = new ArrayList<WMObject>();
		
	}
	
	public boolean addStroke(Stroke stroke) {
		if(isOutOfLine(stroke))
			return true;
		strokes.add(stroke);
		updateParseArea(stroke);
		//if(stroke.isParseable() && checkStrokeParseable(stroke)) {
			parseStrokes();
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
		String s = null;
		if(strokes.size()>3) {
			Stroke stroke1 = strokes.get(strokes.size()-4);
			Stroke stroke2 = strokes.get(strokes.size()-3);
			Stroke stroke3 = strokes.get(strokes.size()-2);
			Stroke stroke4 = strokes.get(strokes.size()-1);
			s = FourStrokeProcessor.findFourStrokeTeken(stroke1, stroke2, stroke3, stroke4);
			if(s!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, stroke3, stroke4, s);
				int teller = 3;
				while(teller>0) {
					teller -= wmObjects.get(wmObjects.size()-1).getStrokes().size();
					wmObjects.remove(wmObjects.size()-1); 
				}
				wmObjects.add(wo);
				//updateAverageHeight();
			}
		}
		if(strokes.size()>2 && wmObjects.size()>1 && wmObjects.get(wmObjects.size()-1).isOneStroke() && wmObjects.get(wmObjects.size()-2).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-3);
			Stroke stroke2 = strokes.get(strokes.size()-2);
			Stroke stroke3 = strokes.get(strokes.size()-1);
			s = ThreeStrokeProcessor.findThreeStrokeTeken(stroke1, stroke2, stroke3);
			if(s!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, stroke3, s);
				wmObjects.remove(wmObjects.size()-1);
				wmObjects.remove(wmObjects.size()-1);
				wmObjects.add(wo);
				//updateAverageHeight();
			}
		}
		if(s==null && strokes.size()>1 &&wmObjects.size()>0 && wmObjects.get(wmObjects.size()-1).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-2);
			Stroke stroke2 = strokes.get(strokes.size()-1);
			s = TwoStrokeProcessor.findTwoStrokeTeken(this, stroke1, stroke2);
			if(s!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, s);
				wmObjects.remove(wmObjects.size()-1);
				wmObjects.add(wo);
				//updateAverageHeight();
			}
		}
		if(s==null && strokes.size()>0) {
			if(tryAsStrokeExtension(strokes.get(strokes.size()-1))) 
				strokes.remove(strokes.get(strokes.size()-1));
			else if(tryDelayedTwoStroke(strokes.get(strokes.size()-1))){
			}
			else {
				s = StrokeMatcher.findTekenRaw(this,strokes.get(strokes.size()-1));
				WMObject wo = new WMObject(strokes.get(strokes.size()-1), s);
				if("back".equals(s)) 
					doBack(wo);
				else {
					wmObjects.add(wo);
					//updateAverageHeight();
				}
			}
		}
		formulaString = FormulaProcessor.parseFormule(this, parseArea);
		updateAverageHeight();
		
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
	
	private void makeParseArea() {
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
		for(int i=0 ; i<wmObjects.size() ; i++) {
			Stroke stroke1 = null;
			if(wmObjects.get(i).isOneStroke()) {
				stroke1 = wmObjects.get(i).getStrokes().get(0);
				
				String s = TwoStrokeProcessor.findTwoStrokeTeken(this, stroke1, stroke2);
				if(s!=null) {
					wmObjects.remove(wmObjects.get(i));
					WMObject wo = new WMObject(stroke1, stroke2, s);
					wmObjects.add(wo);
					updateAverageHeight();
					return true;
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
	
	public boolean isOutOfLine(Stroke stroke) {
		if(strokes.size()==0)
			return false;
		if(stroke.getParsePointsbox().x - (parseArea.x+parseArea.width) > 2.5*averageHeight
				|| parseArea.x - (stroke.getParsePointsbox().x+stroke.getParsePointsbox().width)  > 5*averageHeight) 
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
