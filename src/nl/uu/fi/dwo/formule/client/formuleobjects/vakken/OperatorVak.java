package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d;

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
	
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		ctx.translate(teken.x, teken.y);
		teken.paintComponent(ctx);
		ctx.translate(-teken.x, -teken.y);
	}

	@Override
	public void zetMaat() {
		final FormuleRegel a = getChild(0);
		final FormuleRegel b = getChild(1);

		teken.validate();

		int width = 0;
		if(!(this instanceof AftrekVak && a.toString().equals("0")))
		{	
			width = a.width;
		}
		int as;
// at width,0
		teken.setX(width);
		width += teken.width;
// at width+width, 0
		b.setX(width);
		width += b.width;
		as = Math.max(a.getAsHoogte(), Math.max(b.getAsHoogte(), teken.getAsHoogte()));
		int height = as + Math.max(a.height-a.getAsHoogte(), Math.max( teken.height-teken.getAsHoogte(), b.height-b.getAsHoogte()));
		setSize(width, height);
		setAsHoogte(as);
		a.setY(as-a.getAsHoogte());
		b.setY(as-b.getAsHoogte());
		teken.setY(as-teken.getAsHoogte());
		super.zetMaat();
	}

	public void paintObject() {
// at 0,0
		final FormuleRegel a = getChild(0);
		final FormuleRegel b = getChild(1);

		b.paint();

		if(!(this instanceof AftrekVak && a.toString().equals("0")))
		{	a.paint();
		}
		
		zetMaat();
		paintComponent(ctx);

		b.draw(ctx);
		if(!(this instanceof AftrekVak && a.toString().equals("0")))
			a.draw(ctx);
		
		this.drawCursor();

	}
	
}
