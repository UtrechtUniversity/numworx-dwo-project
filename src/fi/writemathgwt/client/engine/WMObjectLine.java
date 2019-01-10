package fi.writemathgwt.client.engine;

import java.util.ArrayList;

public class WMObjectLine {

	WMObject parent;
	ArrayList<WMObject> wmObjects = new ArrayList<WMObject>();
	
	public WMObjectLine() {
	}
	
	public WMObjectLine(WMObject parent) {
		this.parent = parent;
	}
	
	public WMObjectLine(ArrayList<WMObject> objects) {
		for (int i = 0; i < objects.size(); i++) {
			WMObject wo = objects.get(i);
			wo.setWMObjectChildLine1(null);
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
			if(wo.isTellerVan()==null && wo.isNoemerVan()==null && wo.isMachtVan()==null && wo.isOnderWortel()==null || wo.getWMObjectParentLine()==this) {
				wmObjects.add(wo);
				wmObjectsToDo.remove(wo);
				i--;
				wo.setWMObjectParentLine(this);
				sortWMObjects();
			}
		}
		for(int i=0 ; i<wmObjectsToDo.size() ; i++) {
			WMObject wo = wmObjectsToDo.get(i);
			 if(wo.isMachtVan()!=null && wmObjects.contains(wo.isMachtVan())) {
				WMObjectLine woLine = wo.isMachtVan().getWMObjectChildLine1();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isMachtVan());
					wo.isMachtVan().setWMObjectChildLine1(woLine);
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
			
			else if(wo.isOnderWortel()!=null && wmObjects.contains(wo.isOnderWortel())) {
				WMObjectLine woLine = wo.isOnderWortel().getWMObjectChildLine1();
				if(woLine==null) {
					woLine = new WMObjectLine(wo.isOnderWortel());
					wo.isOnderWortel().setWMObjectChildLine1(woLine);
				}
				wo.setWMObjectParentLine(woLine);
			}
		}
		for(int i=0 ; i<wmObjects.size() ; i++) {
			WMObject wo = wmObjects.get(i);
			if(wo.getWMObjectChildLine1()!=null) {
				wo.getWMObjectChildLine1().fillLine(wmObjectsToDo);
			}
			if(wo.getWMObjectChildLine2()!=null) {
				wo.getWMObjectChildLine2().fillLine(wmObjectsToDo);
			}
		}
	}
	
	
	public void removeAll() {
		wmObjects.clear();
	}
	
	
//	public void addChildWMObject(WMObject wo) {
//		if(wmObjects.contains(wo))
//			return;
//		else { 
//			wo.setWMObjectParentLine(this);
//			wmObjects.add(wo);
//			sortWMObjects();
//		}
//	}
	
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
	
	public DoubleRectangle getBox() {
		double xLeft = 10000;
		double yTop = 10000;
		double xRight = -10000;
		double yBottom = -10000;
		for (int i=0 ; i<wmObjects.size() ; i++) {
			DoubleRectangle woBox = null;
			WMObject wo = wmObjects.get(i);
			if(wo.getWMObjectChildLine1()==null) {
				woBox = new DoubleRectangle(wo.getBox().x, wo.getBox().y, wo.getBox().width, wo.getBox().height);
			}
			else if(wo.getWMObjectChildLine2()==null) { 
				DoubleRectangle box0 = new DoubleRectangle(wo.getBox().x, wo.getBox().y, wo.getBox().width, wo.getBox().height);
				DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
				double xxLeft = Math.min(box0.x, box1.x);
				double yyTop = Math.min(box0.y, box1.y);
				double xxRight = Math.max(box0.x+box0.width, box1.x+box1.width);
				double yyBottom = Math.max(box0.y+box0.height,box1.y+box1.height);
				woBox = new DoubleRectangle(xxLeft,yyTop,xxRight-xxLeft,yyBottom-yyTop);
			}
			else {
				DoubleRectangle box0 = new DoubleRectangle(wo.getBox().x, wo.getBox().y, wo.getBox().width, wo.getBox().height);
				DoubleRectangle box1 = wo.getWMObjectChildLine1().getBox();
				DoubleRectangle box2 = wo.getWMObjectChildLine2().getBox();
				double xxLeft = Math.min(box0.x, Math.min(box1.x, box2.x));
				double yyTop = Math.min(box0.y, Math.min(box1.y, box2.y));
				double xxRight = Math.max(box0.x+box0.width, Math.max(box1.x+box1.width, box2.x+box2.width));
				double yyBottom = Math.max(box0.y+box0.height, Math.max(box1.y+box1.height, box2.y+box2.height));
				woBox = new DoubleRectangle(xxLeft,yyTop,xxRight-xxLeft,yyBottom-yyTop);
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
		}
		boxes.add(this.getBox());
		return boxes;
	}
	
	public ArrayList<WMObject> getWMObjects() {
		return wmObjects;
	}
	
	public String getFormula() {
		String formula = "";
		for(int i=0 ; i<wmObjects.size(); i++) 
			formula = formula + wmObjects.get(i).getFormulaString();
		return formula;
	}
}
