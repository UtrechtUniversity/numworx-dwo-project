package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class PowerVak extends FormuleElementWithChildren {

	public PowerVak(FormuleElement holder) {
		super(holder,2);

		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
		getChild(1).setFontChanges(changes);
	}
	
	public String toString() {
		return "$p" + getChild(0) + "$n" + getChild(1) + "@@";
	}
	public String toMathML() 
	{
		return "<msup>" + getChild(0).toMathML() + getChild(1).toMathML() + "</msup>";
	}

	@Override
	public void zetMaat() {
		FormuleRegel b = getChild(0);
		int width =  b.width;
		int height = b.height;
		FormuleRegel p = getChild(1);
		p.setPosition(width, 0);
		width += p.width;	

		int superscript = p.height - 2 * p.getFont().getAscent() / 3 - p.getFont().getDescent();
		height += superscript;
		b.setPosition(0, superscript);
		setAsHoogte(b.getAsHoogte()+superscript);
		setSize(width, height);
		super.zetMaat();
	}

	public void paintObject() {
		FormuleRegel b = getChild(0);
		b.paint();
		FormuleRegel p = getChild(1);
		p.paint();

		zetMaat();
		paintComponent(ctx);
		
		p.draw(ctx);
		b.draw(ctx);
		this.drawCursor();

	}

}
