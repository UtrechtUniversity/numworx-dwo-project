package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class PrvVak extends FormuleElementWithChildren
{
	public PrvVak(FormuleElement holder)
	{
		super(holder, 4);
		
		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
		
		getChild(1).setFontChanges(changes);
		getChild(2).setFontChanges(changes);
	}
	
	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		this.getChild(2).paint();
		//this.getChild(3).paint();
		zetMaat();
		
		int a = fm.getAscent();
		int kh = getChild(0).height;
		int kb = getChild(0).width;
		int k2h = getChild(1).height;
		int k3h = getChild(2).height;
		
		int t1x = 0;
		int t1y = 0;
		int t1b = a/3;
		int t1h = 2*a/3+Math.max(kh+2*t1b,k2h+k3h);
		
		int t2x = t1x+t1b+kb;
		int t2y = 0;
		int t2b = a/3;
		int t2h = t1h;
		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		ctx.beginPath();
		ctx.moveTo(t1x + t1b, t1y + a/3);
		ctx.lineTo(t1x, t1y + a/3);
		ctx.lineTo(t1x,  t1y + t1h - a/3);
		ctx.lineTo(t1x + t1b, t1y + t1h - a/3);
		ctx.stroke();
		
		ctx.beginPath();
		ctx.moveTo(t2x,t2y+a/3);
		ctx.lineTo(t2x+t2b,t2y+a/3);
		//ctx.moveTo(t2x+t2b,t2y+a/3);
		ctx.lineTo(t2x+t2b,t2y+t2h-a/3);
		ctx.lineTo(t2x,t2y+t2h-a/3);
		//ctx.lineTo(t2x+t2b,t2y+t2h-a/3);
		ctx.stroke();
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.getChild(2).draw(ctx);
		//this.getChild(3).draw(ctx);
		this.drawCursor();
	}
	
	public void zetMaat()
	{
		int a = fm.getAscent();
		int kh = getChild(0).height;
		int kb = getChild(0).width;
		int k2b = getChild(1).width;
		int k2h = getChild(1).height;
		int k3b = getChild(2).width;
		int k3h = getChild(2).height;
		
		int t1x = 0;
		int t1y = 0;
		int t1b = a/3;
		int t1h = 2*a/3+Math.max(kh+2*t1b,k2h+k3h);
		
		int t2x = t1x+t1b+kb;
		int t2y = 0;
		int t2b = a/3;
		int t2h = t1h;
		
		int k3x = t2x+t2b+2;
		int k3y = 0;
		
		int kx = t1x+t1b;
		int ky = t1y+t1h/2-getChild(0).getAsHoogte() + a/2;
		
		setAsHoogte(ky + getChild(0).getAsHoogte());
		
		int k2x = t2x+t2b+2;
		int k2y = t2h-k2h;
		
		int b = t2x+t2b+Math.max(k2b, k3b)+4;
		int h = t2h+1;
		
		setSize(b,h);
		getChild(0).setPosition(kx,ky);
		getChild(1).setPosition(k2x,k2y);
		getChild(2).setPosition(k3x,k3y);
		
	}
	
	public int getAsHoogte()
	{
		return fm.getAscent()/3 + fm.getAscent()/2 + Math.max(getChild(0).height + 2*fm.getAscent()/3, getChild(1).height + getChild(2).height)/2;
				
	}
	
	public String toString()
	{	
	    return "$q" + getChild(0).toString() + "$n" + getChild(1).toString() + "$k" + getChild(2).toString() + "$l" + getChild(3).toString() + "@@@@";//"$n" + kind3.toString() + 
	}

}
