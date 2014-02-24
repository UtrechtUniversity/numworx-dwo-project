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
		int superscript; 
		FormuleRegel b = getChild(0);
		b.paint();
		int width =  b.width;
		int height = b.height;
// at width,0
		FormuleRegel p = getChild(1);
		p.setSmallText(true);
		p.setPosition(width, 0);
		p.paint();
		width += p.width;	

		superscript = p.height - 2 * p.getFont().getAscent() / 3 - p.getFont().getDescent();
		height += superscript;
		b.setPosition(0, superscript);
		setAsHoogte(b.getAsHoogte()+superscript);
		setSize(width, height);
		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		

		p.draw(ctx);
		b.draw(ctx);
		this.drawCursor();

	}

}
