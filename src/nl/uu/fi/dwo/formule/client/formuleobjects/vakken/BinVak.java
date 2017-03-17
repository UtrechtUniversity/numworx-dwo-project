package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class BinVak extends FormuleElementWithChildren
{
	public BinVak(FormuleElement holder)
	{
		super(holder, 2);
	}
	
	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		int h =3*fm.getAscent()/2;
		int hh = h/2;
		int b = h/6;
		int bb = b/2;
		
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
				
		ctx.setStrokeStyle(color);
//		ctx.setFillStyle(color);
		
		ctx.beginPath();
		ctx.moveTo(c+b, d);
		ctx.lineTo(c+b-bb, d+bb);
		ctx.lineTo(c, d+hh-b);
		ctx.lineTo(c, height - hh + b - d);
		ctx.lineTo(c+b-bb, height-bb-d);
		ctx.lineTo(c+b, height-d);
//		ctx.stroke();
//		
//		ctx.beginPath();
		ctx.moveTo(width-b-1-c, d);
		ctx.lineTo(width-1-c, d+hh-b);
		ctx.lineTo(width-1-c, height-hh+b-d);
		ctx.lineTo(width-b+bb-1-c, height-bb-d);
		ctx.lineTo(width-b-1-c, height-d);
		ctx.stroke();		
	}

	public void paintObject()
	{
		getChild(0).paint();
		getChild(1).paint();
		zetMaat();
		paintComponent(ctx);
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.drawCursor();
	}
	
	public void zetMaat()
	{
		int maxWidth = Math.max(getChild(0).width, getChild(1).width);
		getChild(0).setPosition(5*fm.getAscent()/12 + (maxWidth-getChild(0).width)/2, fm.getAscent()/12);
		getChild(1).setPosition(5*fm.getAscent()/12 + (maxWidth-getChild(1).width)/2, fm.getAscent()/6 + getChild(0).height);
		setSize(5*fm.getAscent()/6 + maxWidth, 3*fm.getAscent()/12 + getChild(0).height + getChild(1).height);
		//setAsHoogte(-fm.getAscent()/8 + getChild(0).height);
		setAsHoogte(3*fm.getAscent()/8 + getChild(0).height);
	}
	
	public int getAsHoogte()
	{
		return //-fm.getAscent()/8 + getChild(0).height;
				3*fm.getAscent()/8 + getChild(0).height;
	}
	
	public String toString()
	{
		return "$y" + getChild(0).toString() + "$n"+ getChild(1).toString() + "@@";
	}
	public String toMathML() {
		return "<mfrac linethickness='0'>" + getChild(0).toMathML() + getChild(1).toMathML() + "</mfrac>";
	}

}
