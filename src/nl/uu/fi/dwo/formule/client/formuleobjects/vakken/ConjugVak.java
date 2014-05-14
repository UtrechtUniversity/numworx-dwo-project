package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;



import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class ConjugVak extends FormuleElementWithChildren
{
	public ConjugVak(FormuleElement holder)
	{
		super(holder, 1);
		getChild().setPosition(0, 2);
		this.setChanged(true);
	}
	
	
	
	/*
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		if(getChild() == null) 
			return true;
		setSize(getChild().width, getChild().height + 2);
		setAsHoogte(getChild().getAsHoogte() + 2);
		getChild().setPosition(0, 2);
		getChild().setFont(fm);//niet nodig, denk ik.
		
		//getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		return true;
	}
	*/
	public void paintObject()
	{
		getChild().paint();
		zetMaat();
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		ctx.beginPath();
		ctx.moveTo(2, 0);
		ctx.lineTo(width - 2, 0);
		ctx.stroke();
		
		this.getChild().draw(ctx);
		this.drawCursor();
	}
	
	public void zetMaat()
	{
		setSize(getChild().width  + 2,  getChild().height+2);
		getChild().setPosition(1, 2);
		setAsHoogte(getChild().getAsHoogte()+2);
	}
	
	public int getAsHoogte()
	{
		zetMaat();
		return super.getAsHoogte();
	}
	
	public String toString()
	{	return "$c" + getChild().toString() + "@";
	}
}
