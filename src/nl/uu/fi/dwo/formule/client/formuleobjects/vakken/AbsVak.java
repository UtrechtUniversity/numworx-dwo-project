package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class AbsVak extends FormuleElementWithChildren
{
	public AbsVak(FormuleElement holder)
	{
		super(holder ,1);
	}
	
	public void paintObject()
	{
		getChild().paint();
		zetMaat();
		
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		ctx.beginPath();
		ctx.moveTo(c,  d);
		ctx.lineTo(c, height-d);
		ctx.stroke();
		ctx.beginPath();
		ctx.moveTo(width - 1 - c, d);
		ctx.lineTo(width - 1- c, height - d);
		ctx.stroke();
		
		this.getChild().draw(ctx);
		this.drawCursor();
	}
	
	public void zetMaat()
	{
		setSize(5*fm.getAscent()/6 + getChild().width,  getChild().height);
		getChild().setPosition(5*fm.getAscent()/12,fm.getAscent()/12-1);
		setAsHoogte(getChild().getAsHoogte() + fm.getAscent()/12);
	}
	
	public int getAsHoogte()
	{
		return getChild().getAsHoogte() + fm.getAscent()/12;
	}
	
	public String toString()
	{	return "$r" + getChild().toString() + "@";
	}
}
