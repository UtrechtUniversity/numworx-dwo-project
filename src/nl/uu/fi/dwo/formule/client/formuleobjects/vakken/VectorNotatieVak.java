package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Vector;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class VectorNotatieVak extends FormuleElementWithChildren
{
	public VectorNotatieVak(FormuleElement holder)
	{
		super(holder, 1);
		this.setChanged(true);

//		setSize(5 * fm.getAscent() / 5, 4 * fm.getAscent() / 3 + fm.getDescent());
//		setAsHoogte(fm.getAscent() / 2);
	}

	protected void build(PathBuilder ctx)
	{
		ctx.beginPath();

		ctx.moveTo(2, 2);
		ctx.lineTo(width - 2, 2);
		ctx.moveTo(width - 4, 0);
		ctx.lineTo(width - 2, 2);
		ctx.moveTo(width - 4, 4);
		ctx.lineTo(width - 2, 2);
		
		ctx.stroke();
	}

	public void zetMaat()
	{
		setSize(children.get(0).width, children.get(0).height + 2);
		setAsHoogte(children.get(0).getAsHoogte() + 2);
		
		if (getParent() instanceof FormuleElement)
			((FormuleElement) getParent()).zetMaat();
	}

	public String toString()
	{
		return "$z" + children.get(0).toString() + "@";
	}

	@Override
	public String toMathML()
	{
		/* geen idee welke van de twee de meest gesupporte is */
		// return "<menclose notation='top' >" + children.get(0).toMathML() +
		// "</menclose>";
		return "<mover>" + children.get(0).toMathML() + "<mo>\u00AF</mo></mover>"; // UNICODE
																			// MACRON
	}
}
