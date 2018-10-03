package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

public class VectorVak extends FormuleElementWithChildren
{
	public VectorVak(FormuleElement holder)
	{
		super(holder, 2);
		this.setChanged(true);
		
//		formuleVak = fv;
//		kinderen = new Vector<FormuleRegel>();
//		setLayout(null);
//
//		super.setFont(fv.getFont());
//		fm = getFontMetrics(getFont());
//
//		kind1 = new FormuleRegel(formuleVak);
//		kind1.setLocation(10, 5);// iets met fm.getAscent en fm.getDescent
//		kinderen.add(kind1);
//		add(kind1);
//		// default 2 kinderen. In vulVak() worden deze weeggehaald.
//		maakNieuwKind();
//		maakMaat();
//
//		setOpaque(false);
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx)
	{
		super.paintComponent(ctx);
		build(new CanvasBuilder(ctx));		
	}

	public void paintObject()
	{
		for (int i = 0; i < getChildrenSize(); i++)
		{
			getChild(i).paint();
		}

		zetMaat();
		paintComponent(ctx);
		
		for (int i = 0; i < getChildrenSize(); i++)
		{
			getChild(i).draw(ctx);
		}
		
		this.drawCursor();
	}
	
	protected void build(PathBuilder ctx)
	{
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());
		
		ctx.beginPath();

		// haak ervoor
		ctx.arc(5, 1, 5, 90, 180, true); // eerste bochtje
//		g.drawArc(5, 1, 10, 10, 90, 90); // eerste bochtje
		ctx.moveTo(5, 6);
		ctx.lineTo(5, height - 6); // 1 lange lijn
//		g.drawLine(5, 6, 5, getSize().height - 6); // 1 lange lijn
		ctx.arc(5, height - 12, 5, 180, 270, true); // laatste bochtje 
//		g.drawArc(5, getSize().height - 12, 10, 10, 180, 90); // laatste bochtje 
		
		// haak erna
		ctx.arc(width - 12, 1, 5, 90, 180, false); // eerste bochtje
//		g.drawArc(getSize().width - 12, 1, 10, 10, 90, -90); // eerste bochtje
		ctx.moveTo(width - 2, 6);
		ctx.lineTo(width - 2, height - 6); // 1 lange lijn
//		g.drawLine(getSize().width - 2, 6, getSize().width - 2, getSize().height - 6); // 1 lange lijn
		ctx.arc(width - 12, height - 12, 5, 0, 90, false); // laatste bochtje 
//		g.drawArc(getSize().width - 12, getSize().height - 12, 10, 10, 0, -90); // laatste bochtje 

		ctx.stroke();
	}

	private void maakMaat()
	{
		super.zetMaat();

		height = 5;
		width = 10;
		for (int i = 0; i < children.size(); i++)
		{
			height += children.get(i).height + 5;
			width = Math.max(width, 10 + children.get(i).width);
		}
		
		// extra breedte voor afsluitende haak
		width = width + 10;

		setSize(width, height);
		setAsHoogte(height / 2 - fm.getDescent());

		int kindHoogte = 5;
		for (int i = 0; i < children.size(); i++)
		{
			children.get(i).setPosition(10, kindHoogte);
			kindHoogte += children.get(i).height + 5;
		}
	}

	public int bepaalKindMetFocus()
	{
		int kindMetFocus = 0;
		for (int i = 0; i < children.size(); i++)
		{
//			if (children.get(i).hasFocus())
//			{
//				kindMetFocus = i;
//				break;
//			}
		}
		return kindMetFocus;
	}

	public void focusKindOmhoog()
	{
		int kindMetFocus = bepaalKindMetFocus();
//		if (kindMetFocus > 0)
//			children.get(kindMetFocus - 1).neemFocus("rechts");
	}

	public void focusKindOmlaag()
	{
		int kindMetFocus = bepaalKindMetFocus();
		if (kindMetFocus == children.size() - 1)
		{
			maakNieuwKind();
		}
//		children.get(kindMetFocus + 1).neemFocus("rechts");
		zetMaat();
		paint();
	}

	public void maakNieuwKind()
	{
		FormuleRegel kind = new FormuleRegel(this);
		kind.setFont(getFont());
		children.add(kind);
	}

	public void deleteKind()
	{
		int kindMetFocus = bepaalKindMetFocus();
		FormuleRegel kind = children.get(kindMetFocus);
		// testen of kind leeg is.
		// Als het kind leeg is: kind verwijderen.
		if (kind.toString().length() > 0)
			return;
		children.remove(kindMetFocus);
//		if (kindMetFocus < children.size())
//			children.get(kindMetFocus).neemFocus("rechts");
//		else
//			children.get(kindMetFocus - 1).neemFocus("rechts");
		zetMaat();
		paint();
	}

	public void zetMaat()
	{
		maakMaat();
		if (getParent() instanceof FormuleElement)
			((FormuleElement) getParent()).zetMaat();
	}

	public void setEditable(boolean b)
	{
		for (int i = 0; i < children.size(); i++)
			children.get(i).setEditable(b);
	}

	public void vulVak(String s)
	{
		// hier komt altijd een string in waarin de elementen zijn gescheiden
		// door $n.
		
		children = new Vector<FormuleRegel>();

		while (s.length() > 0)
		{
			char ch0 = s.charAt(0);
			if (ch0 == '@')
			{
				break;
			}
			else if (ch0 == '$')
			{
				int niv = 1;
				int eind = 0;
				String sz = s.substring(2);
				while (niv > 0)
				{
					int eindB = sz.indexOf("$");
					int eindE = sz.indexOf("@");
					if (eindB < eindE && eindB != -1)
					{
						eind = eindB;
						niv++;
					}
					else
					{
						eind = eindE;
						niv--;
					}
					sz = sz.substring(eind + 1);
				}
				eind = s.length() - sz.length();
				char ch1 = s.charAt(1);
				if (ch1 == 'n')
				{
					maakNieuwKind();
					children.get(children.size() - 1).insert(s.substring(2, eind));
					s = s.substring(eind);
				}
			}
		}
		for (int i = 0; i < children.size(); i++)
		{
			children.get(i).zetMaat();
		}
	}

	/**
	 * Verwijder alle kinderen.
	 */
	private void removeKinderen()
	{
		Iterator i = children.iterator();
		
	    while (i.hasNext())
	    {
	    	FormuleRegel kind = (FormuleRegel) i.next();
	    	children.remove(kind);
	    }
	}

	public String toString()
	{
		String string = "$Y";
		if (children.size() > 0)
		{
			for (int i = 0; i < children.size(); i++)
				string = string + "$n" + children.get(i).toString() + "@";
		}
		
		string = string + "@";

//		System.out.println("VectorVak.toString(): " + string);
		
		return string;
	}
}
