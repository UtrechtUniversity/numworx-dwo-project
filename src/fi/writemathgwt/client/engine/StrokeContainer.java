package fi.writemathgwt.client.engine;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;

import fi.writemathgwt.client.engine.filters.FourStrokeProcessor;
import fi.writemathgwt.client.engine.filters.ThreeStrokeProcessor;
import fi.writemathgwt.client.engine.filters.TwoStrokeProcessor;


public class StrokeContainer {
	
	private static Logger logger = Logger.getLogger("StrokeContainer");
	
	private ArrayList<Stroke> strokes;
	private ArrayList<WMObject> wmObjects;
	private String formulaString = "";
	public static double averageHeight = 50;
	private DoubleRectangle parseArea;
	
	
	public StrokeContainer() {
		strokes = new ArrayList<Stroke>();
		wmObjects = new ArrayList<WMObject>();
	}
	
	public void addStroke(Stroke stroke) {
		strokes.add(stroke);
		updateParseArea(stroke);
		parseStrokes();
	}
	
	public String getFormulaString() {
		return formulaString;
	}
	
	public ArrayList<Stroke> getStrokes() {
		return strokes;
	}
	
	public void wis() {
		strokes.clear();
		wmObjects.clear();
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
				updateAverageHeight(wo);
				wmObjects.add(wo);
			}
		}if(strokes.size()>2 && wmObjects.size()>1 && wmObjects.get(wmObjects.size()-1).isOneStroke() && wmObjects.get(wmObjects.size()-2).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-3);
			Stroke stroke2 = strokes.get(strokes.size()-2);
			Stroke stroke3 = strokes.get(strokes.size()-1);
			s = ThreeStrokeProcessor.findThreeStrokeTeken(stroke1, stroke2, stroke3);
			if(s!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, stroke3, s);
				wmObjects.remove(wmObjects.size()-1);
				wmObjects.remove(wmObjects.size()-1);
				updateAverageHeight(wo);
				wmObjects.add(wo);
			}
		}
		if(s==null && strokes.size()>1 &&wmObjects.size()>0 && wmObjects.get(wmObjects.size()-1).isOneStroke()) {
			Stroke stroke1 = strokes.get(strokes.size()-2);
			Stroke stroke2 = strokes.get(strokes.size()-1);
			s = TwoStrokeProcessor.findTwoStrokeTeken(stroke1, stroke2);
			if(s!=null) {
				WMObject wo = new WMObject(stroke1, stroke2, s);
				wmObjects.remove(wmObjects.size()-1);
				updateAverageHeight(wo);
				wmObjects.add(wo);
			}
		}
		if(s==null && strokes.size()>0) {
			if(tryAsStrokeExtension(strokes.get(strokes.size()-1))) 
				strokes.remove(strokes.get(strokes.size()-1));
			else if(tryDelayedTwoStroke(strokes.get(strokes.size()-1))){
			}
			else {
				s = StrokeMatcher.findTekenRaw(strokes.get(strokes.size()-1));
				WMObject wo = new WMObject(strokes.get(strokes.size()-1), s);
				if("back".equals(s)) 
					doBack(wo);
				else {
					updateAverageHeight(wo);
					wmObjects.add(wo);
				}
			}
		}
		formulaString = FormulaProcessor.parseFormule(wmObjects, parseArea);
		
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
			updateAverageHeight(wo);
			wmObjects.add(wo);
		}
		else
			strokes.remove(strokes.size()-1);
	}
	
	private void updateAverageHeight(WMObject wo) {
		if ("-".equals(wo.getTeken())|| 
			".".equals(wo.getTeken())|| 
			"sqrt".equals(wo.getTeken()) || 
			"=".equals(wo.getTeken())) 
			return;
		averageHeight = (wmObjects.size()*averageHeight + wo.getBox().height)/(wmObjects.size()+1);	
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
	
	private boolean tryAsStrokeExtension(Stroke extension) {
		for(int i=0 ; i<wmObjects.size() ; i++) {
			Stroke stroke = null;
			if(wmObjects.get(i).isOneStroke() && (wmObjects.get(i).getTeken().equals("-") || wmObjects.get(i).getTeken().equals("sqrt"))) {
				stroke = wmObjects.get(i).getStrokes().get(0);
				if(TwoStrokeProcessor.hasCloseDistance(stroke, extension, 10, 37, 39, 0, 1)) {
					stroke.extendRight(extension);
					wmObjects.remove(wmObjects.get(i));
					String tekenNew = StrokeMatcher.findTekenRaw(stroke);
					if(tekenNew.equals("back"))
						tekenNew = "-";
					WMObject wmObjectNew = new WMObject(stroke, tekenNew);
					wmObjects.add(i, wmObjectNew);
					return true;
				}
				if(TwoStrokeProcessor.hasCloseDistance(stroke, extension, 10, 0, 2, 0, 1)) {
					stroke.extendLeft(extension);
					wmObjects.remove(wmObjects.get(i));
					String tekenNew = StrokeMatcher.findTekenRaw(stroke);
					if(tekenNew.equals("back"))
						tekenNew = "-";
					WMObject wmObjectNew = new WMObject(stroke, tekenNew);
					wmObjects.add(i, wmObjectNew);
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
				
				String s = TwoStrokeProcessor.findTwoStrokeTeken(stroke1, stroke2);
				if(s!=null) {
					wmObjects.remove(wmObjects.get(i));
					WMObject wo = new WMObject(stroke1, stroke2, s);
					updateAverageHeight(wo);
					wmObjects.add(i,wo);
					return true;
				}
			}
		}
		return false;
	}
	
	
}
