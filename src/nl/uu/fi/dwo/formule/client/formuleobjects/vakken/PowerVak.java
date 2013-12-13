package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

public class PowerVak extends FormuleElementWithChildren {

	public PowerVak(FormuleElement holder) {
		super(holder,2);
	}
	
	public String toString() {
		return "$p" + getChild(0) + "$n" + getChild(1) + "@@";
	}
	public void paintObject() {
// at 0,0
		FormuleRegel b = getChild(0);
		b.paint();
		int width =  b.width;
		int height = b.height;
// at width,0
		FormuleRegel p = getChild(1);
		p.setSmallText(true);
		p.setPosition(width, 0);
		p.paint();
		width += getChild(1).width;
		height = Math.max(height, getChild(1).height);		
		setSize(width, height);
		setAsHoogte(b.getAsHoogte());
		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		p.draw(ctx);
		b.draw(ctx);
		this.drawCursor();

	}

}
