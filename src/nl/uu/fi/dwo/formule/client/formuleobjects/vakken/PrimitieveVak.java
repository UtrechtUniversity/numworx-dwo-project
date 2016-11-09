package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class PrimitieveVak extends FormuleElementWithChildren
{
	public PrimitieveVak(FormuleElement holder){
		
		super(holder, 2);
		getChild(1).insert("x");
	}

	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		zetMaat();

		int asc = fm.getAscent();

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		int tx = 1;
		int ty = 0;
		int tb = 2*asc/3;
		int th = getChild(0).height + tb;
		
		ctx.beginPath();
		
		ctx.beginPath();
		ctx.arc(tx + asc / 2, ty + asc / 6, asc / 6, 0, Math.PI, true);
		ctx.lineTo(tx + asc/3, ty + th - asc/6);
        ctx.arc(tx + asc / 6, ty + th - asc / 6, asc / 6, 0, Math.PI, false);
		ctx.stroke();
		
        //this.drawline(ctx, tx+asc/3,ty+asc/6,tx+asc/3,ty+th-asc/6);
        
		//ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		fm.setItalic(false);
		ctx.setFont(fm.getFontStyle());
		//ctx.fillText("d", tx+asc+getChild(0).width+asc/5-2,getAsHoogte()+(asc-1)/2+1);

		ctx.fillText("d", tx+asc+getChild(0).width+asc/5-2,getAsHoogte());
		fm.setItalic(true);
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		
		this.drawCursor();
	}

	public void zetMaat()
	{
		//int asc = fm.getAscent();
		int tx = 1;
		int ty = 0;
		int tb = 2*fm.getAscent()/3;
		int th = getChild(0).height + tb;
		
		int k1x = tx+ fm.getAscent() -2;
		int k1y = fm.getAscent()/3;
		
		setAsHoogte(k1y + getChild(0).getAsHoogte());
		
		int k2x = k1x+ getChild(0).width + tb - 2;
		int k2y = getAsHoogte()- getChild(1).getAsHoogte();
		
		width = 1 + tx + getChild(0).width + fm.getAscent() + getChild(1).width + tb;
		height = th+1;
		
		setSize(width,height);
		setAsHoogte(k1y + getChild(0).getAsHoogte());
		getChild(0).setPosition(k1x,k1y);
		getChild(1).setPosition(k2x,k2y);

	}
	
	public int getAsHoogte()
	{
		return fm.getAscent()/3 + getChild(0).getAsHoogte();
	}

	public String toString()
	{
		return "$P" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@"; 
	}
	public String toMathML() {
		return "<mrow><mo>\u222b</mo>"+ getChild(0).toMathML() + "<mo>d<mo>" + getChild(1).toMathML() + "</mrow>";
	}

}
