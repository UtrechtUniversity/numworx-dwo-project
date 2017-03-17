package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class SubscriptVak extends FormuleElementWithChildren
{
	public SubscriptVak(FormuleElement holder)
	{
		super(holder, 1);
		
		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
		
		this.setFontChanges(changes);

	}
	
	public void paintObject()
	{
		getChild(0).paint();
		
		FormuleRegel parentRegel = getRegelParent();

		//previous object in the line
		FormuleElement prev = parentRegel.getPrevious(this);

		int vgh = parentRegel.getFont().getHeight();
		int vgah = parentRegel.getFont().getHeight() - 2;
		if (prev != null)
		{
			vgh = prev.getHeight();
			vgah = prev.getAsHoogte();
		}
		
		width = getChild().width;

		height = getChild().height - 2 * fm.getAscent() / 3 + vgh;

		this.setAsHoogte(vgah);

		this.setSize(width, height);
		
		getChild().setPosition(0, vgh - 2*fm.getAscent()/3);

		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}

//		ctx.setStrokeStyle(color);
//		ctx.setFillStyle(color);
//		
		
		this.getChild().draw(ctx);

		this.drawCursor();
	}
	
//	public boolean setFont(FormuleFont fm)
//	{
//		if (super.setFont(fm) == false)
//			return false;
//		return true;
//	}
	
	public void zetMaat()
	{	//int vgh = ((FormuleRegel)getParent()).geefVoorgangerHoogte(this);
		//int vgah = ((FormuleRegel)getParent()).geefVoorgangerAsHoogte(this);
		
        setSize(getChild(0).width, getChild(0).height + fm.getAscent());
        getChild(0).setPosition(0, fm.getAscent());
		//setAsashoogte = vgah;
		//if(getParent()instanceof FormuleElement)((FormuleElement)getParent()).zetMaat();
	}
	
	public String toString()
	{	return "$s" + getChild(0).toString() + "@";
	}
	
	public String toMathML() {
		return getChild().toMathML();
	}
}
