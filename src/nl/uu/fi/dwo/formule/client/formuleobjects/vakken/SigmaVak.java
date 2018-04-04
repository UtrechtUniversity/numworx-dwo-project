package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class SigmaVak extends FormuleElementWithChildren{
	
	public SigmaVak(FormuleElement holder)
	{
		super(holder, 4);
	
		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);
	
		getChild(1).setFontChanges(changes);
		getChild(2).setFontChanges(changes);
		getChild(3).setFontChanges(changes);
		
		getChild(1).insert("i");
		getChild(2).insert("0");
		
		this.setChanged(true);
		
	}
	
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
		build(new CanvasBuilder(ctx));
	}

	protected void build(PathBuilder ctx) {
		FormuleFontChanges changes0 = new FormuleFontChanges();
		changes0.setItalic(FormuleFontChanges.FALSE);
		changes0.setRelativeSize(150);
		FormuleFont f0 = FormuleFont.createFromChanges(fm, changes0);
				
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		ctx.setFont(f0);
		
		int sigmaW = (int) ctx.measureText("\u03A3").getWidth();//fm0.stringWidth("\u03A3");
		ctx.fillText("\u03A3", this.getChild(3).x +(this.getChild(3).width-sigmaW)/2,Math.min(this.getChild(1).y, this.getChild(2).y));
		
		FormuleFontChanges changes1 = new FormuleFontChanges();
		changes1.setSmallText(FormuleFontChanges.TRUE);
		changes1.setItalic(FormuleFontChanges.FALSE);
		FormuleFont f1 = FormuleFont.createFromChanges(fm, changes1);

		ctx.setFont(f1);
		ctx.fillText("=", getChild(1).x + getChild(1).width, getChild(1).y + getChild(1).getAsHoogte());
	}

	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		this.getChild(2).paint();
		this.getChild(3).paint();
		zetMaat();
		paintComponent(ctx);

		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.getChild(2).draw(ctx);
		this.getChild(3).draw(ctx);

		this.drawCursor();
	
	}
	
	public void zetMaat()
	{
		int asc = fm.getAscent();
		int k4h = getChild(3).height;
		int k2w = getChild(1).width;
		int k2h = getChild(1).height;
		int k3w = getChild(2).width;
		int k1a = getChild(0).getAsHoogte();
		int k1w = getChild(0).width;
		int k1h = getChild(0).height;
		int k4w = getChild(3).width;
		
		
		int k4y = Math.max(0, k1a + 1 - k4h - asc);
		int k2x = 0;
		int k2y = k4y+k4h+asc;
		int k3y = k4y+k4h+asc;
		int k3x = k2w+asc/2;
		
		int k1x = k3x+k3w;
		int k1y = Math.max(0, k4y+k4h+asc-k1a-1);
		int w2plus3 = k2w+asc/2+k3w;
		int k4x = (w2plus3-k4w)/2; //is deze keuze logisch? k4w was k2w. Dit lijkt me beter.
		width = w2plus3 + k1w;
		height = Math.max(k4h+asc/2, k1a+1) + Math.max(asc/2+k2h, k1h-k1a-1);
		setSize(width, height);
		setAsHoogte(k1y+k1a);
		
		this.getChild(0).setPosition(k1x,k1y);
		this.getChild(1).setPosition(k2x,k2y);
		this.getChild(2).setPosition(k3x,k3y);
		this.getChild(3).setPosition(k4x,k4y);
	}
	
	public String toString()
	{	return "$S" + getChild(0).toString() + "$n"+ getChild(1).toString() + "$k" + getChild(2).toString() + "$l" + getChild(3).toString() + "@@@@";
	}

	public String toMathML() {
		return "<mrow><munderover><mo>\u03a3</mo><mrow>"+ getChild(1).toMathML()+ "<mo>=</mo>"+ getChild(2).toMathML() + "</mrow>"+ getChild(3).toMathML() + "</munderover>" + getChild(0).toMathML() +"</mrow>" ;
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
		getChild(0).draw(g);
		getChild(1).draw(g);
		getChild(2).draw(g);
		getChild(3).draw(g);
		drawCursor(svg);
	}

}
