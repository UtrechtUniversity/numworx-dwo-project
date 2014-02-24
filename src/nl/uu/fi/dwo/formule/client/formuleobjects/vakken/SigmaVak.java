package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class SigmaVak extends FormuleElementWithChildren{
	
	public SigmaVak(FormuleElement holder)
	{
		super(holder, 4);
	
		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
	
		getChild(1).setFontChanges(changes);
		getChild(2).setFontChanges(changes);
		getChild(3).setFontChanges(changes);
	}
	
	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		this.getChild(2).paint();
		this.getChild(3).paint();
		initSize();
		
		FormuleFont f = fm.createCopy();
		FormuleFontChanges changes0 = new FormuleFontChanges();
		changes0.setRelativeSize(3 / 2);
		setFontChanges(changes0);
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		//Font font0 = new Font(f.getName(), f.getStyle(), f.getSize()*3/2);
		//g.setFont(font0);
		//FontMetrics fm0 = getFontMetrics(font0);
		int sigmaW = (int) ctx.measureText("\u03A3").getWidth();//fm0.stringWidth("\u03A3");
		ctx.fillText("\u03A3", getChild(3).x+(getChild(3).width-sigmaW)/2,Math.min(getChild(1).y, getChild(2).y));
		
		FormuleFontChanges changes1 = new FormuleFontChanges();
		changes1.setSmallText(FormuleFontChanges.TRUE);
		setFontChanges(changes1);
		//f = fm2.getFont();
		//g.setFont(f);
		ctx.fillText("=", getChild(1).x + getChild(1).width, getChild(2).y + 2*getChild(2).getAsHoogte());
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.getChild(2).draw(ctx);
		this.getChild(3).draw(ctx);

		this.drawCursor();
	
	}
	
	public void initSize()
	{
		getChild(3).y = Math.max(0, getChild(0).getAsHoogte() + 1 - getChild(3).height - fm.getAscent()/2);
		//k4y = Math.max(0,k1a+1-k4h-asc/2);
		getChild(1).x = 0;
		//k2x = 0;
		getChild(1).y = getChild(3).y + getChild(3).height + fm.getAscent();
		//k2y = k4y+k4h+asc;
		getChild(2).y = getChild(3).y + getChild(3).height + fm.getAscent();
		//k3y = k4y+k4h+asc;
		getChild(2).x = getChild(1).width + fm.getAscent()/2;
		//k3x = k2w+asc/2;
		getChild(0).x = getChild(2).x + getChild(2).width;
		//k1x = k3x+k3w;
		getChild(0).y = Math.max(0, getChild(3).y + getChild(3).height + fm.getAscent()/2 - getChild().getAsHoogte() - 1);
		//k1y = Math.max(0, k4y+k4h+asc/2-k1a-1);
		int w1plus2 = getChild(1).width + fm.getAscent()/2 + getChild(2).width;
		//int w2plus3 = k2w+asc/2+k3w;
		getChild(3).x = (w1plus2 - getChild(1).width)/2;
		//k4x = (w2plus3-k2w)/2;
		width = w1plus2 + getChild(0).width;
		//width = w2plus3 + k1w;
		height = Math.max(getChild(3).height + fm.getAscent()/2, getChild(0).getAsHoogte() + 1) + 
				Math.max(fm.getAscent()/2 + getChild(1).height, getChild(0).height - getChild(0).getAsHoogte() - 1);
		//height = Math.max(k4h+asc/2, k1a+1) + Math.max(asc/2+k2h, k1h-k1a-1);
		setSize(width, height);
		setAsHoogte(getChild(0).y + getChild(0).getAsHoogte());
		//ashoogte = k1y+k1a;
		
		//kind1.setLocation(k1x,k1y);
		//kind2.setLocation(k2x,k2y);
		//kind3.setLocation(k3x,k3y);
		//kind4.setLocation(k4x,k4y);
	}

	public String toString()
	{	return "$S" + getChild(0).toString() + "$n"+ getChild(1).toString() + "$k" + getChild(2).toString() + "$l" + getChild(3).toString() + "@@@@";
	}
}
