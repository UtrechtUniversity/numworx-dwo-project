package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;



import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class ConjugVak extends FormuleElementWithChildren
{
	public ConjugVak(FormuleElement holder)
	{
		super(holder, 1);
		//getChild().setPosition(0, 0);
		this.setChanged(true);
	}
	
	@Override
	public void paintObject()
	{
		this.getChild().paint();
		zetMaat();
		paintComponent(ctx);
		this.getChild().draw(ctx);
		paintSymbol(ctx);
		this.drawCursor();
	}	

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		paintSymbol(ctx);
	}

	public void paintSymbol(Context2d ctx) {
		build(new CanvasBuilder(ctx));
	}

	protected void build(PathBuilder ctx) {
		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());

		ctx.beginPath();
		ctx.moveTo(2, 1);
		ctx.lineTo(width - 1, 1);
		ctx.stroke();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement#zetMaat()
	 */
	@Override
	public void zetMaat() {
		width =  getChild().width;
		height = getChild().height;
		this.setSize(width, height);
		super.zetMaat();
	}

	public int getAsHoogte()
	{
		return getChild().getAsHoogte();//+2;
	}
	
	public String toString()
	{	return "$c" + getChild().toString() + "@";
	}

	public String toMathML() {
/* geen idee welke van de twee de meest gesupporte is */
		//return "<menclose notation='top' >" + getChild().toMathML() + "</menclose>";
		return "<mover>" + getChild().toMathML() + "<mo>\u00AF</mo></mover>";
	}

	@Override
	protected void paintComponent(OMSVGElement svg) {
		super.paintComponent(svg);
		paintSymbol(svg);
	}

	public void paintSymbol(OMSVGElement svg) {
		SvgBuilder builder = new SvgBuilder(svg, x, y);
		build(builder);
	}

	@Override
	public void draw(OMSVGElement svg) {
		OMSVGGElement g = new OMSVGGElement();
		paintComponent(svg);
		svg.appendChild(g);
		if(x != 0 || y != 0) {
			OMSVGTransform transform = getSVGSVGElement(svg).createSVGTransform();
			transform.setTranslate(x, y);
			g.getTransform().getBaseVal().appendItem(transform);
		}
		getChild().draw(g);
		paintSymbol(svg);
		drawCursor(svg);
	}

}
