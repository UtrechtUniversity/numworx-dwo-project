package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class Breukvak extends FormuleElementWithChildren
{
	public Breukvak(FormuleElement holder)
	{
		super(holder);

		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.FALSE);
		this.setFontChanges(changes);

		this.createChildren(2);

		//new formuleRegel

		setSize(3 * fm.getAscent() / 4, 5 * fm.getAscent() / 2 + 2 * fm.getDescent());

		getChild(0).setPosition(fm.getAscent() / 3, 0);
		getChild(1).setPosition(fm.getAscent() / 3, 6 * fm.getAscent() / 4 + fm.getDescent());

		this.setAsHoogte(getChild(0).height - fm.getAscent() / 8);
		this.setChanged(true);
	}

	@Override
	public void paintObject()
	{
		if (getChild(0).isNumber() && getChild(1).isNumber())
		{
			getChild(0).setSmallText(true);
			getChild(1).setSmallText(true);
		}
		else
		{
			getChild(0).setSmallText(false);
			getChild(1).setSmallText(false);
		}
		this.getChild(0).paint();
		this.getChild(1).paint();
		
		//width = 2*fm.getAscent()/3 + ((getChild(1).width > getChild(0).width) ? getChild(1).width : getChild(0).width);
		width = fm.getAscent()/4 + Math.max(getChild(0).width, getChild(1).width);
		//height = getChild(1).height + getChild(0).height + 2 * fm.getDescent();
		height = getChild(0).height + getChild(1).height + fm.getAscent()/4;
		
		this.setSize(width, height);
		
		getChild(0).y = 0;
		//getChild(1).y = getChild(0).height + 2 * fm.getDescent();
		getChild(1).y = getChild(0).height + fm.getAscent()/4;

		/*
		if (getChild(0).width > getChild(1).width)
		{
			getChild(1).x = getChild(0).width / 2 - getChild(1).width / 2 + fm.getAscent() / 3;
			getChild(0).x = fm.getAscent() / 3;
		}
		else
		{
			getChild(0).x = getChild(1).width / 2 - getChild(0).width / 2 + fm.getAscent() / 3;
			getChild(1).x = fm.getAscent() / 3;
		}
		*/
		getChild(0).x = (width - getChild(0).width)/2;
		getChild(1).x = (width - getChild(1).width)/2;

		this.setAsHoogte(getChild(0).height + fm.getAscent() / 8);
		//this.setAsHoogte(getChild(0).height - fm.getAscent() / 8);
		//this.setAsHoogte(this.height / 2);

		
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		//ctx.setStrokeStyle("#000");
		//ctx.setFillStyle("#000");
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		//}

		ctx.setLineWidth(fm.getStrokeWidth());

		ctx.beginPath();
		//ctx.moveTo(fm.getAscent() / 8, getChild(0).height + fm.getAscent() / 8);
		//ctx.lineTo(this.width - (fm.getAscent() / 8), getChild(0).height + fm.getAscent() / 8);
		ctx.moveTo(fm.getAscent() / 8, this.getAsHoogte());
		ctx.lineTo(this.width - (fm.getAscent() / 8), this.getAsHoogte());
		ctx.stroke();
		//this.drawline(ctx, fm.getAscent() / 3, getChild(0).height + fm.getAscent() / 8, this.width - (fm.getAscent() / 3), getChild(0).height + fm.getAscent() / 8);

		//g.drawLine(fm.getAscent()/8,kind1.getSize().height + fm.getAscent()/8,getSize().width - fm.getAscent()/8,kind1.getSize().height + fm.getAscent()/8);
		
		
		
		this.getChild(1).draw(ctx);
		this.getChild(0).draw(ctx);

		this.drawCursor();
	}

	@Override
	@Deprecated
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;
		//check if one of the children is pressed
		if (x > fm.getAscent() / 2 && x < this.width - fm.getAscent() / 2)
			if (y > getChild(1).height + 2 * fm.getDescent())
				return getChild(0).setCurrentElementAt(x - getChild(0).x, y - getChild(0).y);
			else
				return getChild(1).setCurrentElementAt(x - getChild(1).x, y - getChild(1).y);
		holder.setCurrentElement(this);
		return this;
	}

	@Override
	public FormuleElement getCurrentOnNew()
	{
		//the current element with a new instance is the first child
		return this.getChild(0);
	}

	@Override
	public FormuleElement getCurrentOnNewOnSelection()
	{
		return getChild(1);
	}

	@Override
	public String toString()
	{
		return "$b" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}
}
