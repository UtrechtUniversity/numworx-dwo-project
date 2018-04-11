package fi.writemathgwt.client.engine;

import java.awt.Graphics;
import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.Context2d;


public class StrokeContainer {
	
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
	
	private void parseStrokes() {
		String s = null;
		if(strokes.size()>2 && wmObjects.size()>1 && wmObjects.get(wmObjects.size()-1).isOneStroke() && wmObjects.get(wmObjects.size()-2).isOneStroke()) {
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
			s = StrokeMatcher.findTekenRaw(strokes.get(strokes.size()-1));
			WMObject wo = new WMObject(strokes.get(strokes.size()-1), s);
			if("back".equals(s)) 
				doBack(wo);
			else {
				updateAverageHeight(wo);
				wmObjects.add(wo);
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
}
