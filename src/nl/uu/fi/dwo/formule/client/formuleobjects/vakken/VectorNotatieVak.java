package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class VectorNotatieVak extends RegelVak
{
	public VectorNotatieVak(FormuleVak fv)
	{
		formuleVak = fv;

		setLayout(null);

		super.setFont(fv.getFont());
		fm = getFontMetrics(getFont());

		setSize(5 * fm.getAscent() / 5, 4 * fm.getAscent() / 3 + fm.getDescent());
		ashoogte = fm.getAscent() / 2;

		kind1 = new FormuleRegel(formuleVak);
		kind1.setLocation(0, 6);
		add(kind1);
		setOpaque(false);
	}

	public void setFont(Font f)
	{
		super.setFont(f);
		fm = getFontMetrics(getFont());
		if (kind1 == null)
			return;

		setSize(kind1.getSize().width, kind1.getSize().height + 2);
		ashoogte = kind1.ashoogte + 2;
		kind1.setLocation(0, 2);
		kind1.setFont(f);
	}

	public void paint(Graphics g)
	{
		if (selected)
		{
			g.setColor(Color.black);
			g.fillRect(0, 0, getSize().width, getSize().height);
		}
		
		if (selected)
			g.setColor(Color.white);
		else
			g.setColor(fgColor);

		int hoogte = getSize().height;
		int breedte = getSize().width;

		super.paint(g);

		if (selected)
			g.setColor(Color.white);
		else
			g.setColor(fgColor);
		g.drawLine(2, 2, breedte - 2, 2);
		g.drawLine(breedte - 4, 0, breedte - 2, 2);
		g.drawLine(breedte - 4, 4, breedte - 2, 2);
	}

	public void zetMaat()
	{
		setSize(kind1.getSize().width, kind1.getSize().height + 2);
		ashoogte = kind1.ashoogte + 2;
		
		if (getParent() instanceof FormuleElement)
			((FormuleElement) getParent()).zetMaat();
	}

	public String toString()
	{
		return "$z" + kind1.toString() + "@";
	}

	@Override
	public String toMathML()
	{
		/* geen idee welke van de twee de meest gesupporte is */
		// return "<menclose notation='top' >" + kind1.toMathML() +
		// "</menclose>";
		return "<mover>" + kind1.toMathML() + "<mo>\u00AF</mo></mover>"; // UNICODE
																			// MACRON
	}
}
