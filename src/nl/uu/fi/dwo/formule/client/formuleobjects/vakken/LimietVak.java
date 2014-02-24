package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;


import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class LimietVak extends FormuleElementWithChildren{

	public LimietVak(FormuleElement holder)
	{
		super(holder, 4);
	
		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
	
		getChild(1).setFontChanges(changes);
		getChild(2).setFontChanges(changes);
		//getChild(3).setFontChanges(changes);
	}
	
	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		this.getChild(2).paint();
		//this.getChild(3).paint();
		maakMaat();
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		//ctx.setTextAlign(TextAlign.CENTER);
		ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());//niet italic maken.
		ctx.fillText("lim", getChild(1).x+getChild(1).width/2,getChild(2).y);
		
		if(getChild(3)!=null && getChild(3).toString().equals("0"))
		{	
			int pijlX = getChild(1).x + getChild(1).width + getChild(1).getFont().getAscent()/4;
			int pijlY = getChild(2).y + getChild(2).getAsHoogte() + getChild(1).getFont().getAscent()/4;
			int pijlW = 3*fm.getAscent()/8;
			this.drawline(ctx, pijlX,pijlY,pijlX+pijlW,pijlY);
			this.drawline(ctx, pijlX+pijlW-1,pijlY-1,pijlX+pijlW-1,pijlY);
			this.drawline(ctx, pijlX+pijlW-1,pijlY+1,pijlX+pijlW,pijlY);
		}
		if(getChild(3)!=null && getChild(3).toString().equals("1"))
		{	
			int pijlX = 6*fm.getAscent()/8;
			int pijlY = getChild(2).y + getChild(2).getAsHoogte()+getChild(1).getFont().getAscent()/4-fm.getAscent()/3;
			int pijlH = 4*fm.getAscent()/8;
			this.drawline(ctx, pijlX,pijlY,pijlX,pijlY+pijlH);
			this.drawline(ctx, pijlX-1,pijlY+pijlH-1,pijlX,pijlY+pijlH);
			this.drawline(ctx, pijlX+1,pijlY+pijlH-1,pijlX,pijlY+pijlH);
		}
		if(getChild(3)!=null && getChild(3).toString().equals("2"))
		{	int pijlX = 6*fm.getAscent()/8;
			int pijlY = getChild(2).y + getChild(2).getAsHoogte()+getChild(1).getFont().getAscent()/4-fm.getAscent()/3;
			int pijlH = 4*fm.getAscent()/8;
			this.drawline(ctx, pijlX,pijlY,pijlX,pijlY+pijlH);
			this.drawline(ctx, pijlX-1,pijlY+1,pijlX,pijlY);
			this.drawline(ctx, pijlX+1,pijlY+1,pijlX,pijlY);
		}
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.getChild(2).draw(ctx);
		this.getChild(3).draw(ctx);
		
		this.drawCursor();
	}
	
	private void maakMaat()
	{	
		setAsHoogte(getChild(0).getAsHoogte()); 
		
		//boolean breuk = kind1!=null && kind1.getComponentCount()>0 && (kind1.getComponent(0) instanceof BreukVak);
		boolean breuk = getChild(0)!=null && (getChild(0).getElementAt(0) instanceof Breukvak);
		int corr = breuk ? fm.getAscent()/3 : 0;
		
		getChild(2).x = getChild(1).width + 3 * fm.getAscent() / 4;
		getChild(2).y = getAsHoogte() + (fm.getAscent() + fm.getDescent())/2 - 1 - corr;
		getChild(1).x = fm.getAscent()/8;
		getChild(1).y = getChild(2).y + getChild(2).getAsHoogte() - getChild(1).getAsHoogte();
		getChild(0).x = getChild(2).x + getChild(2).width + fm.getAscent()/4;
		getChild(0).y = 0;
		/*
		k3x = k2w+3*asc/4;
		k3y = ashoogte+(asc+desc)/2-1 - corr;
		k2x = asc/8;
		k2y = k3y+k3a-k2a;//(height-(2*(asc+desc)+asc/4))/2+asc+3*desc;
		k1x = k3x+k3w+asc/4;
		k1y = 0;
		*/
		width = getChild(2).x + getChild(2).width + fm.getAscent()/4 + getChild(0).width + fm.getAscent()/4;
		height = Math.max(getChild(2).y + getChild(2).height, getChild(0).height);
		
		setSize(width, height);
		//kind1.setLocation(k1x,k1y);
		//kind2.setLocation(k2x,k2y);
		//kind3.setLocation(k3x,k3y);
	}
	
	
	public String toString()
	{
		return "$T" + getChild(0).toString() + "$n"+ getChild(1).toString() + "$k" + getChild(2).toString() + "$l" + getChild(3).toString() + "@@@@";
	}

}