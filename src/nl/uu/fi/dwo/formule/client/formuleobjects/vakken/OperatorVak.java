package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

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
	
	public boolean setFont(FormuleFont f)
	{
		teken.setFont(f);
		return super.setFont(f);
	}

	public String toString() {
		return "$" + code + getChild(0) + "$n" + getChild(1) + "@@";
	}
	
	public void paintObject() {
// at 0,0
		final FormuleRegel a = getChild(0);
		a.paint();
		int width =  a.width;
		int as;
// at width,0
		teken.setPosition(width, 0);
		teken.paint();
		width += teken.width;
// at width+width, 0
		final FormuleRegel b = getChild(1);
		b.setPosition(width, 0);
		b.paint();
		width += b.width;
		as = Math.max(a.getAsHoogte(), Math.max(b.getAsHoogte(), teken.getAsHoogte()));
		height = as + Math.max(a.height-a.getAsHoogte(), Math.max( teken.height-teken.getAsHoogte(), b.height-b.getAsHoogte()));
		setSize(width, height);
		setAsHoogte(as);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		//ctx.setStrokeStyle("#000");
		//ctx.setFillStyle("#000");
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		//}

		//ctx.setLineWidth(fm.getStrokeWidth());

		//this.drawline(ctx, fm.getAscent() / 3, a.height + fm.getAscent() / 8, this.width - (fm.getAscent() / 3), a.height + fm.getAscent() / 8);

		b.draw(ctx, b.x, as-b.getAsHoogte());
		a.draw(ctx, a.x, as-a.getAsHoogte());
		teken.draw(ctx, teken.x, as-teken.getAsHoogte());
		this.drawCursor();

	}
	
}
