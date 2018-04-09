package fi.writemathgwt.client.engine;

import java.util.ArrayList;

import fi.writemathgwt.client.Rectangle;
import fi.writemathgwt.client.WriteObject;

public class StrokeContainer {
	
	private ArrayList<Stroke> strokes;
	private ArrayList<WMObject> wmObjects;
	private String formulaString;
	
	
	public StrokeContainer() {
		strokes = new ArrayList<Stroke>();
		wmObjects = new ArrayList<WMObject>();
	}
	
	public void addStroke(Stroke stroke) {
		strokes.add(0,stroke);
		parseStrokes();
	}
	
	private void parseStrokes() {
		String s = null;
		if(strokes.size()>2 && wmObjects.get(1).isOneStroke() && wmObjects.get(2).isOneStroke()) {
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
		if(s==null && strokes.size()>1 && wmObjects.get(1).isOneStroke()) {
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
			wmObjects.add(0, new WMObject(strokes.get(0), s));
		}
		formulaString = formulaString + s;
	}
}
