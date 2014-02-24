package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class PrimitieveVak extends FormuleElementWithChildren
{
	public PrimitieveVak(FormuleElement holder){
		
		super(holder, 2);
	}

	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		initSize();

		int asc = fm.getAscent();

		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		int tx = 1;
		int ty = 0;
		int tb = 2*asc/3;
		int th = getChild(0).height + tb;
		
		ctx.beginPath();
		
		ctx.beginPath();
		ctx.arc(tx + (asc / 3) + (asc / 3 / 2), ty + asc / 6, asc / 3 / 2, 0, Math.PI, true);
		ctx.arc(tx + (asc / 3 / 2), ty + th - asc / 6, asc / 3 / 2, 0, Math.PI, false);
		ctx.stroke();
		
        this.drawline(ctx, tx+asc/3,ty+asc/6,tx+asc/3,ty+th-asc/6);
        
		ctx.setTextAlign(TextAlign.CENTER);
		ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());
		ctx.fillText("d", tx+asc+getChild(0).width+asc/5-2,getAsHoogte()+(asc-1)/2+1);

		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		
		this.drawCursor();
	}

	public void initSize()
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

	public String toString()
	{
		return "$P" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@"; 
	}
}
