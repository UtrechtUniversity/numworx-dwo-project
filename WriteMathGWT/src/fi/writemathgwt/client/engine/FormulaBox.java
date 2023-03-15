package fi.writemathgwt.client.engine;

import java.util.ArrayList;

public class FormulaBox {
	
//	private double x;
//	private double y;
//	private double width;
//	private double height;
	private DoubleRectangle box;
	
	FormulaBox mainBox;
	FormulaBox exponentBox;
	FormulaBox subscriptBox;
	
	ArrayList<WMObject> wmObjects;
	
	public void addWriteObject(WMObject wo) {
		if(exponentBox.contains(wo.getXBox()))
			exponentBox.addWriteObject(wo);
		else if(subscriptBox.contains(wo.getXBox()))
			subscriptBox.addWriteObject(wo);
		else if(mainBox.contains(wo.getXBox())) {
			wmObjects.add(wo);
			updateBoxSizes();
		}
	}
	
	public void updateBoxSizes() {
		
	}
	
	public boolean contains(DoubleRectangle r) {
		return box.contains(r);
	}
	
	public DoubleRectangle getBox() {
		return box;
	}
}
