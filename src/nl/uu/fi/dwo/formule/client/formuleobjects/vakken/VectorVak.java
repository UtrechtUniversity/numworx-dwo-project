package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import java.util.Iterator;
import java.util.Vector;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class VectorVak extends FormuleElementWithChildren
{
	public VectorVak(FormuleElement holder)
	{
		super(holder, 2);
		this.setChanged(true);
	}

	public VectorVak(FormuleElement holder, int aantalRijen)
	{
		super(holder, aantalRijen);
		this.setChanged(true);
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

	@Override
	protected void paintComponent(OMSVGElement svg)
	{
		super.paintComponent(svg);
		SvgBuilder builder = new SvgBuilder(svg, x, y);
		build(builder);
	}

	@Override
	public void draw(OMSVGElement svg)
	{
		paintComponent(svg);
		OMSVGGElement g = new OMSVGGElement();
		svg.appendChild(g);
		if (x != 0 || y != 0)
		{
			OMSVGTransform transform = getSVGSVGElement(svg).createSVGTransform();
			transform.setTranslate(x, y);
			g.getTransform().getBaseVal().appendItem(transform);
		}
		
		for (int i = 0; i < getChildrenSize(); i++)
		{
			getChild(i).draw(g);
		}
		
		drawCursor(svg);
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
		
//		ctx.beginPath();
//
//		// haak ervoor
//		ctx.arc(10, 6, 5, Math.PI, 1.5 * Math.PI, false); // eerste bochtje
//		ctx.moveTo(5, 6);
//		ctx.lineTo(5, height - 6); // 1 lange lijn
//
//		ctx.arc(10, height - 7, 5, Math.PI, Math.PI / 2, true); // laatste bochtje 
//		
//		ctx.stroke();
//		ctx.beginPath();
//
//		// haak erna
//		ctx.arc(width - 7, 6, 5, 0, 1.5 * Math.PI, true); // eerste bochtje
//		ctx.moveTo(width - 2, 6);
//		ctx.lineTo(width - 2, height - 6); // 1 lange lijn
//		ctx.arc(width - 7, height - 7, 5, 0, Math.PI / 2, false); // laatste bochtje 
		
//		ctx.stroke();
		
		int h = 4 * fm.getAscent() / 3;
		int b = h / 4;
		int c = fm.getAscent() / 6;
		int d = fm.getAscent() / 8;
		
		float bx = (float)(b*Math.sqrt(2)/(Math.sqrt(2)-1));
		float by = (float)(b/(Math.sqrt(2)-1));
		ctx.beginPath();
		ctx.arc(c+bx+3, d+by+2, bx, Math.PI, 5*Math.PI/4, false);
		ctx.moveTo(c+3, d + by+2);
		ctx.lineTo(c+3, height-d-by);
		ctx.arc(c+bx+3, height-d-by, bx, 3*Math.PI/4, Math.PI, false);
		
		ctx.arc(width-c-bx, d+by+2, bx, 0, -Math.PI/4, true);
		ctx.moveTo(width-c, d+2 + by);
		ctx.lineTo(width-c, height-d-by);
		ctx.arc(width-c-bx, height-d-by, bx,  0, Math.PI/4,false);
		ctx.stroke();

		
	}

	private void maakMaat()
	{
		super.zetMaat();

		height = 5;
		width = 10;
		
		for (int i = 0; i < children.size(); i++)
		{
			height += getChild(i).height + 5;
			width = Math.max(width, 10 + getChild(i).width);
		}
		
		// extra breedte voor afsluitende haak
		width = width + 10;

		setSize(width, height);
		setAsHoogte(height / 2 + fm.getAscent()/2 - fm.getDescent()/2);
		//System.out.println("VectorVak.maakMaat(): setSize(" + width + ", " + height + "), ashoogte = " + getAsHoogte());

		int kindHoogte = 5;
		for (int i = 0; i < children.size(); i++)
		{
			getChild(i).setPosition((int) (0.5 * width - 0.5 * getChild(i).width + 2), kindHoogte);
			kindHoogte += getChild(i).height + 5;
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
		FormuleRegel kind = getChild(kindMetFocus);
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
			getChild(i).setEditable(b);
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
					getChild(children.size() - 1).insert(s.substring(2, eind));
					s = s.substring(eind);
				}
			}
		}
		for (int i = 0; i < children.size(); i++)
		{
			getChild(i).zetMaat();
		}
	}

//	/**
//	 * Verwijder alle kinderen.
//	 */
//	private void removeKinderen()
//	{
//		Iterator i = children.iterator();
//		
//	    while (i.hasNext())
//	    {
//	    	FormuleRegel kind = (FormuleRegel) i.next();
//	    	children.remove(kind);
//	    }
//	}

	public String toString()
	{
		String string = "$Y";
		if (children.size() > 0)
		{
			for (int i = 0; i < children.size(); i++)
				string = string + "$n" + getChild(i).toString() + "@";
		}
		
		string = string + "@";

//		System.out.println("VectorVak.toString(): " + string);
		
		return string;
	}

}
