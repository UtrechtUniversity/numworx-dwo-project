package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class Haakjesvak extends FormuleElementWithChildren
{

	public Haakjesvak(FormuleElement holder)
	{
		super(holder, 1);

		this.setChanged(true);

		setSize(5 * fm.getAscent() / 5 + getChild().width, 4 * fm.getAscent() / 3 + fm.getDescent());
		getChild().setPosition(5 * fm.getAscent() / 12, fm.getAscent() / 12);
	}

	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//check if one of the children is pressed
		return getChild().setCurrentElementAt(x - getChild().x, y - getChild().y);
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		CanvasBuilder builder = new CanvasBuilder(ctx);
		build(builder);
	}

	protected void build(PathBuilder ctx) {
		float h = 4 * fm.getAscent() / 3;
		//int hh = h / 2;
		float b = h / 4;
		//int bb = 2*b / 3;

		int c = fm.getAscent() / 6;
		int d = fm.getAscent() / 8;

		ctx.setStrokeStyle(color);
		ctx.setLineWidth(fm.getStrokeWidth());
		
//		ctx.beginPath();
//		ctx.arc(c+2*b+1, d+2*b, 2*b, Math.PI, 4*Math.PI/3, false);
//		ctx.moveTo(c+1, d + b*(float)Math.sqrt(3));
//		ctx.lineTo(c+1, height-d-b*(float)Math.sqrt(3));
//		ctx.arc(c+2*b+1, height-d-2*b, 2*b, 2*Math.PI/3, Math.PI, false);
//		
//		ctx.arc(width-c-2*b, d+2*b, 2*b, 0, -Math.PI/3, true);
//		ctx.moveTo(width-c, d + b*(float)Math.sqrt(3));
//		ctx.lineTo(width-c, height-d-b*(float)Math.sqrt(3));
//		ctx.arc(width-c-2*b, height-d-2*b, 2*b,  0, Math.PI/3,false);
//		ctx.stroke();
		if((float)(2*b/(Math.sqrt(2)-1))+3>height)
			b = h/6;
		float bx = (float)(b*Math.sqrt(2)/(Math.sqrt(2)-1));
		float by = (float)(b/(Math.sqrt(2)-1));
		ctx.beginPath();
		ctx.arc(c+bx, d+by+2, bx, Math.PI, 5*Math.PI/4, false);
		ctx.moveTo(c, d + by+2);
		ctx.lineTo(c, height-d-by);
		ctx.arc(c+bx, height-d-by, bx, 3*Math.PI/4, Math.PI, false);
		
		ctx.arc(width-c-bx, d+by+2, bx, 0, -Math.PI/4, true);
		ctx.moveTo(width-c, d + by+2);
		ctx.lineTo(width-c, height-d-by);
		ctx.arc(width-c-bx, height-d-by, bx,  0, Math.PI/4,false);
		ctx.stroke();
		
		//oud
//		ctx.beginPath();
//		ctx.moveTo(c+b, d);
//		ctx.lineTo(c+b-bb, d+bb);
//		ctx.lineTo(c, d+hh-b);
//		ctx.lineTo(c, height - hh + b - d);
//		ctx.lineTo(c+b-bb, height-bb-d);
//		ctx.lineTo(c+b, height-d);
////		ctx.stroke();
////		
////		ctx.beginPath();
//		ctx.moveTo(width-b-1-c, d);
//		ctx.lineTo(width-b+bb-1-c, d+bb);
//		ctx.lineTo(width-1-c, d+hh-b);
//		ctx.lineTo(width-1-c, height-hh+b-d);
//		ctx.lineTo(width-b+bb-1-c, height-bb-d);
//		ctx.lineTo(width-b-1-c, height-d);
//		ctx.stroke();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement#zetMaat()
	 */
	@Override
	public void zetMaat() {
		//width = getChild().width;
		//height = getChild().height - 2 * fm.getAscent() / 3 - fm.getDescent() + vgh;

		setAsHoogte(getChild().getAsHoogte() + fm.getAscent() / 12);
		setSize(5 * fm.getAscent() / 6 + getChild().width, fm.getAscent() / 6 + getChild().height);
		//this.setSize(width, height);

		super.zetMaat();
	}

	@Override
	public void paintObject()
	{
		try
		{
			getChild().paint();
			zetMaat();
			paintComponent(ctx);
			getChild().draw(ctx);
			drawCursor();
		}
		catch (Exception e)
		{
			Window.alert(e.getMessage());
		}
	}
	
	public int getAsHoogte()
	{
		return getChild().getAsHoogte() + fm.getAscent() / 12;
	}

	@Override
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		getChild().setPosition(5 * fm.getAscent() / 12, fm.getAscent() / 12);
		return true;
	}

	@Override
	public String toString()
	{
		return "$h" + getChild().toString() + "@";
	}

	public String toMathML() 
	{
		return "<mfenced>" + getChild().toMathML() + "</mfenced>";
	}
	
	@Override
	protected void paintComponent(OMSVGElement svg) {
		super.paintComponent(svg);
		SvgBuilder builder = new SvgBuilder(svg, x, y);
		build(builder);
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
