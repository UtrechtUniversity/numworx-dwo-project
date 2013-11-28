package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleFont;
import nl.uu.fi.dwo.mobile.DWOplayer;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class WortelVak extends FormuleElementWithChildren
{
	public WortelVak(FormuleElement editor)
	{
		super(editor, 1);
		//new formuleRegel

		//setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());

		//ind1.setLocation(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);

		getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		//this.paint();
		this.setChanged(true);
		this.setAsHoogte(3 * fm.getAscent() / 4);
	}

	@Override
	public void paint()
	{
		if (this.isChanged() == false)
			return;

		this.getChild().paint();

		width = 5 * fm.getAscent() / 6 + getChild().width;
		height = fm.getAscent() / 4 + getChild().height;

		this.setAsHoogte(getChild().getAsHoogte() + fm.getAscent() / 4);
		this.setSize(width, height);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		ctx.setStrokeStyle("#000");
		ctx.setFillStyle("#000");

		ctx.setLineWidth(fm.getStrokeWidth());

		this.drawline(ctx, 0, 2 * height / 3, fm.getAscent() / 3, height);

		ctx.beginPath();
		ctx.moveTo(1, 2 * height / 3);
		ctx.lineTo(fm.getAscent() / 3 + 1, height);
		ctx.lineTo(2 * fm.getAscent() / 3 - 1, fm.getAscent() / 8);
		ctx.lineTo(width, fm.getAscent() / 8);
		ctx.stroke();

		this.getChild().draw(ctx);
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
		getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		return true;
	}

	@Override
	public String toString()
	{
		return "$w" + getChild().toString() + "@";
	}
}
