package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;



public class NdeLogVak extends FormuleElementWithChildren
{
	public NdeLogVak(FormuleElement editor)
	{
		super(editor, 2);

		//this.paint();
		this.setChanged(true);
		this.setAsHoogte(3 * fm.getAscent() / 4);
		//ctx.setFont(fm.getFontStyle());
		//ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		//ctx.setFont(fm.getFontStyle());
		//System.out.println("this.getFontChanges.toString()" + this.font.toString());
		int fStr = (int) Math.round(ctx.measureText("log").getWidth());
		
		setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());
		setAsHoogte(getChild(0).getAsHoogte() + getChild(1).height/2);
		//if(WiskOpdr.language.toString().equals("en"))ashoogte = kind1.ashoogte;
		//ashoogte = kind1.ashoogte+ k2h/2;
        
		//getChild(0).setPosition(5 * fm.getAscent() / 7 + 5, fm.getAscent() / 4);
		//getChild(0).setPosition(getChild(1).width + fStr + 3*fm.getAscent()/4, getChild(1).height/2);
		getChild(0).setPosition(getChild(1).width + fStr + fm.getAscent()/3, fm.getAscent() / 4);// getChild(1).height/2);
        getChild(1).setPosition(fm.getAscent()/3, getAsHoogte()-(this.getChild(1).height/2 + fm.getAscent()/2));
        
        /*
        if(WiskOpdr.language.toString().equals("en"))
        {
        	kind1.setLocation(k2w + fStr + 3*asc/4, 0);
        	kind2.setLocation(5+fStr, ashoogte + (-kind2.ashoogte + 2*asc/3));
        }
        */
		
		
		//getChild(1).setPosition(fm.getAscent() / 4, 0);
		getChild(1).setEditable(false);

		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);

		getChild(1).setFontChanges(changes);
	}

	@Override
	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		//ctx.setFont(fm.getFontStyle());
		ctx.setTextAlign(TextAlign.CENTER);
		ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());
		
		int fStr = (int) Math.round(ctx.measureText("log").getWidth());
		setSize(2*fm.getAscent()/3+getChild(1).width + fStr + getChild(0).width + fm.getAscent()/2, getChild(1).height/2 + getChild(0).height);
		setAsHoogte(getChild(0).getAsHoogte() + getChild(1).getHeight()/2);

		getChild(0).setPosition(getChild(1).width + fStr + 3*fm.getAscent()/4, getChild(1).height/2);
        getChild(1).setPosition(fm.getAscent()/3, getAsHoogte()-(this.getChild(1).height/2 + fm.getAscent()/2));
        
        if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		//ctx.setStrokeStyle("#000");
		//ctx.setFillStyle("#000");
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		

		//ctx.setLineWidth(fm.getStrokeWidth());

		//this.drawline(ctx, 5, 2 * height / 3, fm.getAscent() / 3 + 5, height);
		//this.drawline(ctx, 6, 2 * height / 3, fm.getAscent() / 3 + 6, height);
		//this.drawline(ctx, fm.getAscent() / 3 + 5, height, 2 * fm.getAscent() / 3 + 4, fm.getAscent() / 8);
		//this.drawline(ctx, 2 * fm.getAscent() / 3 + 5, fm.getAscent() / 8, width + 5, fm.getAscent() / 8);
		
		//if(language.toString().equals("en"))g.drawString("log", 3 ,ashoogte + asc/2 + asc/12);
		//else g.drawString("log", 5+k2w ,ashoogte + asc/2 + asc/12);
		//ctx.setTextAlign(TextAlign.CENTER);
		ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());
		ctx.fillText("log", 5 + getChild(1).width, getAsHoogte() + fm.getAscent()/2 + fm.getAscent()/12);
		
		int hoogte = getChild(0).height;
		int breedte = width;
		int h =3*fm.getAscent()/2;
		int hh = h/2;
		int b = h/6;
		int bb = b/2;
		
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
		
		int locx =	fm.getAscent()/3+getChild(1).width + fStr;
		int locy = getChild(1).height/2;
		//if(WiskOpdr.language.toString().equals("en"))locy = 0;
		
		ctx.beginPath();
		ctx.moveTo(locx+c+b, locy+d);
		ctx.lineTo(locx+c+b-bb, locy+d+bb);
		ctx.lineTo(locx+c, locy+d+hh-b);
		ctx.lineTo(locx+c, locy+hoogte-hh+b-d);
		ctx.lineTo(locx+c+b-bb, locy+hoogte-bb-d);
		ctx.lineTo(locx+c+b, locy+hoogte-d);
		ctx.stroke();
		
		ctx.beginPath();
		ctx.moveTo(breedte-b-1-c, locy+d);
		ctx.lineTo(breedte-b+bb-1-c, locy+d+bb);
		ctx.lineTo(breedte-1-c, locy+d+hh-b);
		ctx.lineTo(breedte-1-c, locy+hoogte-hh+b-d);
		ctx.lineTo(breedte-b+bb-1-c, locy+hoogte-bb-d);
		ctx.lineTo(breedte-b-1-c, locy+hoogte-d);
		ctx.stroke();
		
		
		/*
		this.drawline(ctx, locx+c+b, locy+d, locx+c+b-bb, locy+d+bb);
		this.drawline(ctx, locx+c+b-bb, locy+d+bb, locx+c, locy+d+hh-b);
		this.drawline(ctx, locx+c, locy+d+hh-b, locx+c, locy+hoogte-hh+b-d);		
		this.drawline(ctx, locx+c+b-bb, locy+hoogte-bb-d, locx+c, locy+hoogte-hh+b-d);
		this.drawline(ctx, locx+c+b, locy+hoogte-d, locx+c+b-bb, locy+hoogte-bb-d);
		
		this.drawline(ctx, breedte-b-1-c, locy+d, breedte-b+bb-1-c, locy+d+bb);
		this.drawline(ctx, breedte-b+bb-1-c, locy+d+bb, breedte-1-c, locy+d+hh-b);
		this.drawline(ctx, breedte-1-c, locy+d+hh-b, breedte-1-c, locy+hoogte-hh+b-d);		
		this.drawline(ctx, breedte-b+bb-1-c, locy+hoogte-bb-d, breedte-1-c, locy+hoogte-hh+b-d);
		this.drawline(ctx, breedte-b-1-c, locy+hoogte-d, breedte-b+bb-1-c, locy+hoogte-bb-d);
		*/
		
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.drawCursor();
	}

	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (x > getChild().x && x < getChild().x + getChild().width)
			return getChild().setCurrentElementAt(x - getChild().x, y - getChild().y);
		if (x <= getChild().x)
		{
			if (x > getChild(1).x && x < getChild(1).x + getChild(1).width)
				return getChild(1).setCurrentElementAt(x - getChild(1).x, y - getChild(1).y);
			//if x < 1/2 of the "v" the cursor should be placed before this object
			if (x < fm.getAscent() / 3)
				return null;

			this.getChild().setIndexAt(-1);
			holder.setCurrentElement(this.getChild());
			return this.getChild();
		}

		holder.setCurrentElement(this);
		return this;
	}

	@Override
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "$L" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}
}

