package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.core.client.GWT;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class IntegraalVak extends FormuleElementWithChildren
{
	public IntegraalVak(FormuleElement holder)
	{
		super(holder, 4);

		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);

		getChild(1).setFontChanges(changes);
		getChild(2).setFontChanges(changes);

	}
	
	public IntegraalVak(FormuleElement holder, String var)
	{
		this(holder);
		
		getChild(3).insert(var);
	}

	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		this.getChild(2).paint();
		this.getChild(3).paint();
		
		zetMaat();

		int asc = fm.getAscent();

		int tx = Math.max(1, getChild(1).width - asc / 2);
		int ty = getChild(2).height + 1;
		int tb = 2 * asc / 3;
		int th = getChild(0).height + tb;
		//int ashoogte = this.getAsHoogte();
		int ashoogte = getAsHoogte();
		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		
		//g.drawArc(tx+asc/3,ty,asc/3,asc/3,0,180);
        //g.drawArc(tx,ty+th-asc/3,asc/3,asc/3,180,180);
        //g.drawLine(tx+asc/3,ty+asc/6,tx+asc/3,ty+th-asc/6);
        //g.drawString("d",tx+asc+k1w+asc/5-2,ashoogte+(asc-1)/2+1);//(int)Math.rint(4.0*asc/8));
		
        /*
		ctx.beginPath();
		ctx.arc(tx + (asc / 3) + (asc / 3 / 2), ty + asc / 6, asc / 3 / 2, 0, Math.PI, true);
		ctx.arc(tx + (asc / 3 / 2), ty + th - asc / 6, asc / 3 / 2, 0, Math.PI, false);
		ctx.stroke();
*/
		ctx.beginPath();
        ctx.arc(tx + asc/2, ty + asc/6,  asc/6, 0, Math.PI, true);
        ctx.lineTo(tx + asc/3, ty + th - asc/6);
        ctx.arc(tx + asc/6, ty + th - asc/6, asc/6, 0, Math.PI, false);
        ctx.stroke();
        
        //ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		fm.setItalic(false);
		ctx.setFont(fm.getFontStyle());
		ctx.fillText("d", tx + asc + getChild(0).width + asc / 5 - 2, ashoogte);
		
		fm.setItalic(true);
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.getChild(2).draw(ctx);
		this.getChild(3).draw(ctx);

		for (int i = 0; i < this.getChildrenSize(); i++)
			GWT.log("Child " + i + " is in loc " + getChild(i).x + " x,y: " + getChild(i).y + " size " + getChild(i).width + " w,h: " + getChild(i).height);
		this.drawCursor();
	}

	public void zetMaat()
	{
		int asc = fm.getAscent();

		int k1h = getChild(0).height;
		int k1w = getChild(0).width;
		int k1a = getChild(0).getAsHoogte();

		int k2w = getChild(1).width;
		int k2h = getChild(1).height;

		int k3h = getChild(2).height;

		int k4w = getChild(3).width;
		int k4a = getChild(3).getAsHoogte();
		
		int tx = Math.max(1, k2w - asc / 2);
		int ty = k3h + 1;
		int tb = 2 * asc / 3;
		int th = k1h + tb;

		int k3x = tx + asc / 2;
		int k3y = 0;

		int k1x = tx + asc - 2;
		int k1y = ty + tb / 2;

		int ashoogte = k1y + k1a;

		int k4x = k1x + k1w + tb - 2;
		int k4y = ashoogte - k4a;

		int k2x = 1;
		int k2y = k3h + th + 2;

		width = 1 + tx + k1w + asc + k4w + tb;
		height = 2 + k2h + k3h + th;

		setSize(width, height);
		ashoogte = k1y + k1a;
		getChild(0).setPosition(k1x, k1y);
		getChild(1).setPosition(k2x, k2y);
		getChild(2).setPosition(k3x, k3y);
		getChild(3).setPosition(k4x, k4y);
		this.setAsHoogte(ashoogte);
	}
	
	public int getAsHoogte()
	{
		return getChild(2).height + 1 + fm.getAscent() / 3 + getChild(0).getAsHoogte();
	}

	public String toString()
	{
		return "$i" + getChild(0).toString() + "$n" + getChild(1).toString() + "$k" + getChild(2).toString() + "$l" + getChild(3).toString() + "@@@@";//"$n" + kind3.toString() + 
	}
}
