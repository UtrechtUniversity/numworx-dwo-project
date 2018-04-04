package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;

public class AbsVak extends FormuleElementWithChildren
{
	public AbsVak(FormuleElement holder)
	{
		super(holder ,1);
	}
	
	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
				
		ctx.setStrokeStyle(color);
//		ctx.setFillStyle(color);
		
		ctx.beginPath();
		ctx.moveTo(c,  d);
		ctx.lineTo(c, height-d);
//		ctx.stroke();
//		ctx.beginPath();
		ctx.moveTo(width - 1 - c, d);
		ctx.lineTo(width - 1 - c, height - d);
		ctx.stroke();
	}
	
	
	protected void paintComponent(OMSVGElement svg) {
		super.paintComponent(svg);
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
		float x1 = c, y1 = d;
		float x2 = c, y2 = height-d;
		OMSVGLineElement line = new OMSVGLineElement(x1+x,y1+y,x2+x,y2+y);
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
		svg.appendChild(line);
		x1 = x2 = width-1-c;
		line = new OMSVGLineElement(x1+x,y1+y,x2+x,y2+y);
		line.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, color);
		svg.appendChild(line);
	}

	public void paintObject()
	{
		getChild().paint();
		zetMaat();		
		paintComponent(ctx);
		this.getChild().draw(ctx);
		this.drawCursor();
	}
	
	public void zetMaat()
	{
		setSize(5*fm.getAscent()/6 + getChild().width,  getChild().height);
		getChild().setPosition(5*fm.getAscent()/12, fm.getAscent()/12-1);
		setAsHoogte(getChild().getAsHoogte() + fm.getAscent()/12);
		super.zetMaat();
	}
	
	public int getAsHoogte()
	{
		return getChild().getAsHoogte() + fm.getAscent()/12;
	}
	
	public String toString()
	{	return "$r" + getChild().toString() + "@";
	}
	
	public String toMathML() 
	{
		return "<mfenced open='|' close='|'>" + getChild().toMathML() + "</mfenced>";
	}

	@Override
	public void draw(OMSVGElement svg) {
		paintComponent(svg);
		OMSVGGElement g = new OMSVGGElement();
		svg.appendChild(g);
		if(x != 0 || y != 0) {
			OMSVGTransform transform = getSVGSVGElement(svg).createSVGTransform();
			transform.setTranslate(x, y);
			g.getTransform().getBaseVal().appendItem(transform);
		}
		getChild().draw(g);
		drawCursor(svg);
	}

}
