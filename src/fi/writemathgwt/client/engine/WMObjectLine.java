package fi.writemathgwt.client.engine;

import java.util.ArrayList;

public class WMObjectLine {

	private WMObject parent;
	private ArrayList<WMObject> wmObjects = new ArrayList<WMObject>();
	
	private double averageHeight;
	public static final double DEFAULTAVERAGEHEIGHT = 40;
	
	private double averageBaseLine;
	
	private boolean averageHeightSet;
	private boolean averageBaseLineSet;
	
	public WMObjectLine() {
	}
	
	public WMObjectLine(WMObject parent) {
		this.parent = parent;
	}
	
	public WMObjectLine(ArrayList<WMObject> objects) {
		for (int i = 0; i < objects.size(); i++) {
			WMObject wo = objects.get(i);
			wo.setWMObjectChildLine1(null);
			wo.setWMObjectChildLineExponent(null);
			wo.setWMObjectChildLineSubscript(null);
			wo.setWMObjectChildLine2(null);
			wo.setWMObjectParentLine(null);
		}
		fillLine(objects);
	}
	
	public void fillLine(ArrayList<WMObject> objects) {
		ArrayList<WMObject> wmObjectsToDo = new ArrayList<WMObject>();
		wmObjectsToDo.addAll(objects);
		for(int i=0 ; i<wmObjectsToDo.size() ; i++) {
			WMObject wo = wmObjectsToDo.get(i);
			if(wo.isTellerVan()==null 
					&& wo.isNoemerVan()==null  
					&& wo.isOnderWortel()==null 
					&& wo.isNdeVanWortel()==null
					&& wo.isExponentVan()==null 
					&& wo.isSubscriptVan()==null 
					|| wo.getWMObjectParentLine()==this) {
				addWMObject(wo);
				wmObjectsToDo.remove(wo);
				i--;
				wo.setWMObjectParentLine(this);
				sortWMObjects();
			}
		}
		for(int i=0 ; i<wmObjectsToDo.size() ; i++) {
			WMObject wo = wmObjectsToDo.get(i);
			if(wo.isExponentVan()!=null && wmObjects.contains(wo.isExponentVan())) {
				WMObjectLine woLine = wo.isExponentVan().getWMObjectChildLineExponent();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isExponentVan());
					wo.isExponentVan().setWMObjectChildLineExponent(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
			else if(wo.isSubscriptVan()!=null && wmObjects.contains(wo.isSubscriptVan())) {
				WMObjectLine woLine = wo.isSubscriptVan().getWMObjectChildLineSubscript();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isSubscriptVan());
					wo.isSubscriptVan().setWMObjectChildLineSubscript(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
			else if(wo.isTellerVan()!=null && wmObjects.contains(wo.isTellerVan())) {
				WMObjectLine woLine = wo.isTellerVan().getWMObjectChildLine1();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isTellerVan());
					wo.isTellerVan().setWMObjectChildLine1(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
			else if(wo.isNoemerVan()!=null && wmObjects.contains(wo.isNoemerVan())) {
				WMObjectLine woLine = wo.isNoemerVan().getWMObjectChildLine2();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isNoemerVan());
					wo.isNoemerVan().setWMObjectChildLine2(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
			
			else if(wo.isOnderWortel()!=null  && wmObjects.contains(wo.isOnderWortel())) {
				WMObjectLine woLine = wo.isOnderWortel().getWMObjectChildLine1();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isOnderWortel());
					wo.isOnderWortel().setWMObjectChildLine1(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
			else if(wo.isNdeVanWortel()!=null  && wmObjects.contains(wo.isNdeVanWortel())) {
				WMObjectLine woLine = wo.isNdeVanWortel().getWMObjectChildLine2();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isNdeVanWortel());
					wo.isNdeVanWortel().setWMObjectChildLine2(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
		}
		for(int i=0 ; i<wmObjects.size() ; i++) {
			WMObject wo = wmObjects.get(i);
			if(wo.getWMObjectChildLineExponent()!=null) {
				wo.getWMObjectChildLineExponent().fillLine(wmObjectsToDo);
			}
			if(wo.getWMObjectChildLineSubscript()!=null) {
				wo.getWMObjectChildLineSubscript().fillLine(wmObjectsToDo);
			}
			if(wo.getWMObjectChildLine1()!=null) {
				wo.getWMObjectChildLine1().fillLine(wmObjectsToDo);
			}
			if(wo.getWMObjectChildLine2()!=null) {
				wo.getWMObjectChildLine2().fillLine(wmObjectsToDo);
			}
			
		}
	}
	
	public void addWMObject(WMObject wo) {
		wmObjects.add(wo);
		updateAverageHeight();
		updateAverageBaseLine();
	}
	
	private void scaleAndPlace() {
//		boolean swapped = true;
//		while (swapped)	{	
//			swapped = false;
//			for (int i = 1; i < wmObjects.size(); i++) {	
//				WMObject wo1 = wmObjects.get(i-1);
//				WMObject wo2 = wmObjects.get(i);
//				if (wo1.getTimeStamp() > wo2.getTimeStamp()) {	
//					wmObjects.set(i-1, wo2);
//					wmObjects.set(i, wo1);
//					swapped = true;
//				}
//			}
//		}
		boolean scaled = false;
		for(int i=wmObjects.size()-1 ; i>0; i--) {
			if(i<wmObjects.size()-1 && !scaled) {
				scaleAndPlace(wmObjects.get(i-1));
				scaled = true;
			}
		}
	}
	
	private void scaleAndPlace(WMObject wo) {
		if(wo.getBox().getDiagonal()<10)
			return;
		if(wo.isBreuk())
			return;
		if(wo.isWortel())
			return;
		if(averageHeightSet) {
			double woHeight = wo.getXBox().height;
			double woX = wo.getXBox().x;
			double factor = averageHeight/woHeight;
			wo.scale(woX, 0, factor);
			ArrayList<Stroke> strokes = wo.getStrokes();
			for(int i=0 ; i<strokes.size() ; i++) {
				strokes.get(i).scale(woX, 0, factor);
			}
		}
		if(averageBaseLineSet) {
			double woBase = wo.getXBox().y + wo.getXBox().height;
			double ty = averageBaseLine - woBase;
			wo.translate(0, ty);
			ArrayList<Stroke> strokes = wo.getStrokes();
			for(int i=0 ; i<strokes.size() ; i++) {
				strokes.get(i).translate(0, ty);
			}
		}		
	}
		
	public void removeAll() {
		wmObjects.clear();
	}
		
	public void sortWMObjects() {
		boolean swapped = true;
		while (swapped)	{	
			swapped = false;
			for (int i = 1; i < wmObjects.size(); i++) {	
				WMObject wo1 = wmObjects.get(i-1);
				WMObject wo2 = wmObjects.get(i);
				if (wo1.getBox().x > wo2.getBox().x) {	
					wmObjects.set(i-1, wo2);
					wmObjects.set(i, wo1);
					swapped = true;
				}
			}
		}
	}
	
	public DoubleRectangle getGrondtalBox(WMObject wo) {
		DoubleRectangle woBox = null;
		if(wo.getWMObjectChildLine1()==null) {
			woBox = new DoubleRectangle(wo.getBox());
		}
		else if(wo.getWMObjectChildLine2()==null) { 
			DoubleRectangle box0 = new DoubleRectangle(wo.getBox());
			DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
			woBox = new DoubleRectangle(box0, box1);
		}
		else {
			DoubleRectangle box0 = new DoubleRectangle(wo.getBox().x, wo.getBox().y, wo.getBox().width, wo.getBox().height);
			DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
			DoubleRectangle box2 = wo.getWMObjectChildLine2().getBox();
			woBox = new DoubleRectangle(box0, box1, box2);
		}
		return woBox;
	}
	
	public DoubleRectangle getBox() {
		double xLeft = 10000;
		double yTop = 10000;
		double xRight = -10000;
		double yBottom = -10000;
		for (int i=0 ; i<wmObjects.size() ; i++) {
			DoubleRectangle woBox = null;
			WMObject wo = wmObjects.get(i);
			if(wo.getWMObjectChildLineExponent()!=null) {
				DoubleRectangle box0 = getGrondtalBox(wo);
				DoubleRectangle box1 = wo.getWMObjectChildLineExponent().getBox();
				woBox = new DoubleRectangle(box0, box1);
			}
			else if(wo.getWMObjectChildLineSubscript()!=null) {
				DoubleRectangle box0 = getGrondtalBox(wo);
				DoubleRectangle box1 = wo.getWMObjectChildLineSubscript().getBox();
				woBox = new DoubleRectangle(box0, box1);
			}
			else if(wo.getWMObjectChildLine1()==null) {
				woBox = new DoubleRectangle(wo.getBox());
			}
			else if(wo.getWMObjectChildLine2()==null) { 
				DoubleRectangle box0 = new DoubleRectangle(wo.getBox());
				DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
				woBox = new DoubleRectangle(box0, box1);
			}
			else {
				DoubleRectangle box0 = new DoubleRectangle(wo.getBox().x, wo.getBox().y, wo.getBox().width, wo.getBox().height);
				DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
				DoubleRectangle box2 = wo.getWMObjectChildLine2().getBox();
				woBox = new DoubleRectangle(box0, box1, box2);
			}
			xLeft = Math.min(xLeft, woBox.x);
			yTop = Math.min(yTop, woBox.y);
			xRight = Math.max(xRight, woBox.x+woBox.width);
			yBottom = Math.max(yBottom, woBox.y+woBox.height);
		}
		return new DoubleRectangle(xLeft,yTop,xRight-xLeft,yBottom-yTop);
	}
	
	public ArrayList<DoubleRectangle> getBoxes() {
		ArrayList<DoubleRectangle> boxes = new ArrayList<DoubleRectangle>();
		for (int i=0 ; i<wmObjects.size() ; i++) {
			ArrayList<DoubleRectangle> r = null;
			if(wmObjects.get(i).getWMObjectChildLine1()!=null) {
				r = wmObjects.get(i).getWMObjectChildLine1().getBoxes();
				boxes.addAll(r);
			}
			if(wmObjects.get(i).getWMObjectChildLine2()!=null) {
				r = wmObjects.get(i).getWMObjectChildLine2().getBoxes();
				boxes.addAll(r);
			}
			if(wmObjects.get(i).getWMObjectChildLineExponent()!=null) {
				r = wmObjects.get(i).getWMObjectChildLineExponent().getBoxes();
				boxes.addAll(r);
			}
			if(wmObjects.get(i).getWMObjectChildLineSubscript()!=null) {
				r = wmObjects.get(i).getWMObjectChildLineSubscript().getBoxes();
				boxes.addAll(r);
			}
		}
		boxes.add(this.getBox());
		return boxes;
	}
	
	public ArrayList<WMObjectLine> getLines() {
		ArrayList<WMObjectLine> lines = new ArrayList<WMObjectLine>();
		for (int i=0 ; i<wmObjects.size() ; i++) {
			ArrayList<DoubleRectangle> r = null;
			if(wmObjects.get(i).getWMObjectChildLine1()!=null) {
				lines.addAll(wmObjects.get(i).getWMObjectChildLine1().getLines());
			}
			if(wmObjects.get(i).getWMObjectChildLine2()!=null) {
				lines.addAll(wmObjects.get(i).getWMObjectChildLine2().getLines());
			}
			if(wmObjects.get(i).getWMObjectChildLineExponent()!=null) {
				lines.addAll(wmObjects.get(i).getWMObjectChildLineExponent().getLines());
			}
			if(wmObjects.get(i).getWMObjectChildLineSubscript()!=null) {
				lines.addAll(wmObjects.get(i).getWMObjectChildLineSubscript().getLines());
			}
		}
		lines.add(this);
		return lines;
	}
	
	public ArrayList<WMObject> getWMObjects() {
		return wmObjects;
	}
	
	public void setInContext() {
		for(int i=0 ; i<wmObjects.size(); i++) {
			wmObjects.get(i).setInContext();
		}
	}
	
	public String getFormula() {
		String formula = "";
		for(int i=0 ; i<wmObjects.size(); i++) {
			formula = formula + wmObjects.get(i).getFormulaString();
		}
		//scaleAndPlace();
		return formula;
	}
	
	public double getAverageHeight() {
		return averageHeight;
	}
	
	public double getAverageBaseLine() {
		return averageBaseLine;
	}
	
	private void updateAverageHeight() {
		double heightSum = 0;
		int cnt = 0;
		averageHeightSet = false;
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
				cnt++;
				averageHeightSet = true;
			}
		}
		if(cnt>0)
			averageHeight = heightSum/cnt;
		else 
			averageHeight = DEFAULTAVERAGEHEIGHT;
	}
	
	private void updateAverageBaseLine() {
		double baseLineSum = 0;
		int cnt = 0;
		averageBaseLineSet = false;
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
				baseLineSum += wmObjects.get(i).getXBox().y + wmObjects.get(i).getXBox().height;
				cnt++;
				averageBaseLineSet = true;
			}
		}
		if(cnt>0)
			averageBaseLine = baseLineSum/cnt;
		else {
			for (int i = 0; i < wmObjects.size(); i++) {
				averageBaseLine += wmObjects.get(i).getXBox().y + wmObjects.get(i).getXBox().height/2 + DEFAULTAVERAGEHEIGHT/2;
			}
		}
		
	}
}
