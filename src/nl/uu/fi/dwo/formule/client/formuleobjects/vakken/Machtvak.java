package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

/**
 * Macht vak
 * 
 * @author Danny Hendrix
 * 
 */
public class Machtvak extends FormuleElementWithChildren
{
	public Machtvak(FormuleElement holder)
	{
		super(holder, 1);

		FormuleFontChanges fc = new FormuleFontChanges();

		fc.setSmallText(FormuleFontChanges.TRUE);

		this.setFontChanges(fc);

		getChild().setPosition(0, 0);

		this.setChanged(true);
	}

	@Override
	public void paintObject()
	{
		this.getChild().paint();

		FormuleRegel parentRegel = getRegelParent();

		//previous object in the line
		FormuleElement prev = parentRegel.getPrevious(this);

		int vgh = parentRegel.getFont().getHeight();
		int vgah = parentRegel.getFont().getHeight() / 2;
		if (prev != null)
		{
			vgh = prev.getHeight();
			vgah = prev.getAsHoogte();
		}

		width = getChild().width;

		height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;

		this.setAsHoogte(getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgah);

		this.setSize(width, height);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

		this.getChild().draw(ctx);

		this.drawCursor();
	}

	@Override
	public String toString()
	{
		return "$m" + getChild().toString() + "@";
	}
}
