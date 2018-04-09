package fi.writemathgwt.client.engine;

import java.awt.Graphics;
import java.util.ArrayList;

import fi.writemathgwt.client.Rectangle;
import fi.writemathgwt.client.WriteObject;


public class StrokeContainer {
	
	private ArrayList<Stroke> strokes;
	private ArrayList<WMObject> wmObjects;
	private String formulaString = "";
	private int averageHeight = 30;
	
	
	public StrokeContainer() {
		strokes = new ArrayList<Stroke>();
		wmObjects = new ArrayList<WMObject>();
	}
	
	public void addStroke(Stroke stroke) {
		strokes.add(0,stroke);
		parseStrokes();
	}
	
	public void draw(Graphics g) {
		for(int i=0 ; i<strokes.size() ; i++)
			strokes.get(i).draw(g);
	}
	
	public String getFormulaString() {
		return formulaString;
	}
	
	private void parseStrokes() {
		String s = null;
		if(strokes.size()>2 && wmObjects.get(0).isOneStroke() && wmObjects.get(1).isOneStroke()) {
			Stroke stroke1 = strokes.get(2);
			Stroke stroke2 = strokes.get(1);
			Stroke stroke3 = strokes.get(0);
			s = ThreeStrokeProcessor.findThreeStrokeTeken(stroke1, stroke2, stroke3);
			if(s!=null) {
				wmObjects.remove(0);
				wmObjects.remove(0);
				wmObjects.add(0, new WMObject(stroke1, stroke2, stroke3, s));
			}
			
		}
		if(s==null && strokes.size()>1 && wmObjects.get(0).isOneStroke()) {
			Stroke stroke1 = strokes.get(1);
			Stroke stroke2 = strokes.get(0);
			s = TwoStrokeProcessor.findTwoStrokeTeken(stroke1, stroke2);
			if(s!=null) {
				wmObjects.remove(0);
				wmObjects.add(0, new WMObject(stroke1, stroke2, s));
			}
		}
		if(s==null && strokes.size()>0) {
			s = StrokeMatcher.findTekenRaw(strokes.get(0));
			WMObject wo = new WMObject(strokes.get(0), s);
			if("back".equals(s)) 
				doBack(wo);
			else 
				wmObjects.add(0, wo);
		}
		formulaString = FormulaProcessor.parseFormule(wmObjects);
		
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
			wo = new WMObject(strokes.get(0),"-");
			wmObjects.add(0, wo);
		}
		else
			strokes.remove(0);
	}
}
