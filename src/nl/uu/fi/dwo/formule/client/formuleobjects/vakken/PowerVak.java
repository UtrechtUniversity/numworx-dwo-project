package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class PowerVak extends FormuleElementWithChildren {

	public PowerVak(FormuleElement holder) {
		super(holder,2);
	}
	
	public String toString() {
		return "$p" + getChild(0) + "$n" + getChild(1) + "@@";
	}
	public void paint() {
		if(!isChanged()) 
			return;
// at 0,0
		getChild(0).paint();
		int width =  getChild(0).width;
		int height = getChild(0).height;
// at width,0
		getChild(1).setPosition(width, 0);
		getChild(1).paint();
		width += getChild(1).width;
		height = Math.max(height, getChild(1).height);		
		setSize(width, height);
	}

}
