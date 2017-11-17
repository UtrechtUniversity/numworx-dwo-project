package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;

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
	}

	@Override
	public void paintObject()
	{
		this.getChild().paint();

		zetMaat();

		paintComponent(ctx);
		
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
		super.zetMaat();
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

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement#draw(org.vectomatic.dom.svg.OMSVGElement, com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void draw(OMSVGElement svg, Context2d ctx2) {		
		OMSVGGElement g = new OMSVGGElement();
		getChild().draw(g, ctx2);		
		int n = g.getChildNodes().getLength();
		if(n > 0)
		{ 	OMSVGTransform transform = svg.getOwnerSVGElement().createSVGTransform();
			transform.setTranslate(x, y);
			g.getTransform().getBaseVal().appendItem(transform);
			svg.appendChild(g);
		} else
			super.draw(ctx2);
	}

	
	
	
}
