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

		zetMaat();

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

//		ctx.setStrokeStyle(color);
//		ctx.setFillStyle(color);
		
		
		this.getChild().draw(ctx);

		this.drawCursor();
	}
	
	public void zetMaat()
	{
		FormuleRegel parentRegel = getRegelParent();
	
		//previous object in the line
		FormuleElement prev = parentRegel.getPrevious(this);
		int vgh = parentRegel.getFont().getHeight();
		int vgah = parentRegel.getFont().getAscent();
		if (prev != null)
		{
			vgh = prev.getHeight();
			vgah = prev.getAsHoogte();
		}
	
		width = getChild().width;
	
		height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;
	
		
		this.setAsHoogte(getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgah);
		
		this.setSize(width, height);
		
	}
	
	public int getAsHoogte()
	{	FormuleRegel parentRegel = getRegelParent();
		FormuleElement prev = parentRegel.getPrevious(this);
		int vgah = parentRegel.getFont().getAscent();
		if (prev != null)
		{
			vgah = prev.getAsHoogte();
		}
		return getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgah;
		
	}

	@Override
	public String toString()
	{
		return "$m" + getChild().toString() + "@";
	}

	public String toMathML() {
		return getChild().toMathML();
	}

}
