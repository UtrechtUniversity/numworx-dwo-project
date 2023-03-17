package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class VectorNotatieVak extends FormuleElementWithChildren
{
	public VectorNotatieVak(FormuleElement holder)
	{
		super(holder, 1);
		this.setChanged(true);
		getChild().setPosition(0, 2);

//		setSize(5 * fm.getAscent() / 5, 4 * fm.getAscent() / 3 + fm.getDescent());
//		setAsHoogte(fm.getAscent() / 2);
	}

	protected void build(PathBuilder ctx)
	{
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());

		ctx.beginPath();

		ctx.moveTo(2, 2);
		ctx.lineTo(width - 2, 2);
		ctx.moveTo(width - 4, 0);
		ctx.lineTo(width - 2, 2);
		ctx.moveTo(width - 4, 4);
		ctx.lineTo(width - 2, 2);
		
		ctx.stroke();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx)
	{
		super.paintComponent(ctx);
		paintSymbol(ctx);		
	}

	private void paintSymbol(Context2d ctx) {
		build(new CanvasBuilder(ctx));
	}
	
	
	protected void paintComponent(OMSVGElement svg)
	{
		super.paintComponent(svg);
		paintSymbol(svg);
	}

	private void paintSymbol(OMSVGElement svg) {
		SvgBuilder builder = new SvgBuilder(svg, x, y);
		build(builder);
	}

	public void paintObject()
	{
		getChild().paint();
		zetMaat();		
		paintComponent(ctx);
		getChild().draw(ctx);
		paintSymbol(ctx);
		this.drawCursor();
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
		getChild().draw(g);
		paintSymbol(svg);
		drawCursor(svg);
	}

	public int getAsHoogte()
	{
		return getChild().getAsHoogte() + 2;
	}
	
	public void zetMaat()
	{
		setSize(getChild(0).width, getChild(0).height + 2);
		setAsHoogte(getChild(0).getAsHoogte() + 2);
		super.zetMaat();	
//		if (getParent() instanceof FormuleElement)
//			((FormuleElement) getParent()).zetMaat();
	}

	public String toString()
	{
		return "$z" + getChild(0).toString() + "@";
	}

	@Override
	public String toMathML()
	{
		/* geen idee welke van de twee de meest gesupporte is */
		// return "<menclose notation='top' >" + children.get(0).toMathML() +
		// "</menclose>";
		return "<mover>" + getChild(0).toMathML() + "<mo>\u2192</mo></mover>"; // UNICODE
																			// MACRON
	}
}
