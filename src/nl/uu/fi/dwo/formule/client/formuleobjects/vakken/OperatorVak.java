package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;

public class OperatorVak extends FormuleElementWithChildren {

	protected char code, operator;
	protected FormuleTeken teken;
	
	public OperatorVak(FormuleElement holder, char code, char operator ) {
		super(holder,2);
		this.code = code;
		this.operator = operator;
		teken = new FormuleTeken(holder, operator);
		setChanged(true);
		getChild(0).setPosition(0, 0);
		
	}

	public String toString() {
		return "$" + code + getChild(0) + "$n" + getChild(1) + "@@";
	}
	
	public void paintObject() {
// at 0,0
		getChild(0).paint();
		int width =  getChild(0).width;
		int height = getChild(0).height;
// at width,0
		teken.setPosition(width, 0);
		teken.paint();
		width += teken.width;
		height  = Math.max(height, teken.height);
// at width+width, 0
		getChild(1).setPosition(width, 0);
		getChild(1).paint();
		width += getChild(1).width;
		height = Math.max(height, getChild(1).height);
		int ashoogte = getChild(0).getAsHoogte();
		setSize(width, height);
		setAsHoogte(ashoogte);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		ctx.setStrokeStyle("#000");
		ctx.setFillStyle("#000");
		//}

		ctx.setLineWidth(fm.getStrokeWidth());

		this.drawline(ctx, fm.getAscent() / 3, getChild(0).height + fm.getAscent() / 8, this.width - (fm.getAscent() / 3), getChild(0).height + fm.getAscent() / 8);

		this.getChild(1).draw(ctx);
		this.getChild(0).draw(ctx);
		teken.draw(ctx);
		this.drawCursor();

	}
	
}
