package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class NdeWortelVak extends FormuleElementWithChildren
{
	public NdeWortelVak(FormuleElement editor)
	{
		super(editor, 2);

		//this.paint();
		this.setChanged(true);
		this.setAsHoogte(3 * fm.getAscent() / 4);

		setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());
		setAsHoogte(3 * fm.getAscent() / 4);

		getChild(0).setPosition(5 * fm.getAscent() / 7 + 5, fm.getAscent() / 4);

		getChild(1).setPosition(fm.getAscent() / 4, 0);
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

		setSize(5 * fm.getAscent() / 6 + getChild(0).width + 5, fm.getAscent() / 4 + getChild(0).height);
		setAsHoogte(getChild(0).getAsHoogte() + fm.getAscent() / 4);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		//ctx.setStrokeStyle("#000");
		//ctx.setFillStyle("#000");
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		

		ctx.setLineWidth(fm.getStrokeWidth());

		ctx.beginPath();
		ctx.moveTo(6, 2 * height / 3);
		ctx.lineTo(fm.getAscent() / 3 + 6, height);
		ctx.moveTo(5, 2 * height / 3);
		ctx.lineTo(fm.getAscent() / 3 + 5, height);
		ctx.lineTo(2 * fm.getAscent() / 3 + 4, fm.getAscent() / 8);
		ctx.moveTo(2 * fm.getAscent() / 3 + 5, fm.getAscent() / 8);
		ctx.lineTo(width + 5, fm.getAscent() / 8);
		ctx.stroke();
		
		//this.drawline(ctx, 5, 2 * height / 3, fm.getAscent() / 3 + 5, height);
		//this.drawline(ctx, 6, 2 * height / 3, fm.getAscent() / 3 + 6, height);
		//this.drawline(ctx, fm.getAscent() / 3 + 5, height, 2 * fm.getAscent() / 3 + 4, fm.getAscent() / 8);
		//this.drawline(ctx, 2 * fm.getAscent() / 3 + 5, fm.getAscent() / 8, width + 5, fm.getAscent() / 8);

		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.drawCursor();
	}

	public int getAsHoogte()
	{
		return getChild(0).getAsHoogte() + fm.getAscent() / 4;
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
		//getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		return true;
	}

	@Override
	public String toString()
	{
		return "$W" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}
}
