package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGTransform;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.i18n.NdeLog;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;



public class NdeLogVak extends FormuleElementWithChildren
{
	public NdeLogVak(FormuleElement editor)
	{
		super(editor, 2);

		variant = GWT.create(NdeLog.class);
		//this.paint();
		//this.setChanged(true);
		this.setAsHoogte(3 * fm.getAscent() / 4);
		//ctx.setFont(fm.getFontStyle());
		//ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		//ctx.setFont(fm.getFontStyle());
		//System.out.println("this.getFontChanges.toString()" + this.font.toString());
		fStr = (int) holder.measureWidth(this, fm, "log");
		setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());
		setAsHoogte(getChild(0).getAsHoogte() + getChild(1).height/2);

		//if(WiskOpdr.language.toString().equals("en"))ashoogte = kind1.ashoogte;
		//ashoogte = kind1.ashoogte+ k2h/2;
        
		//getChild(0).setPosition(5 * fm.getAscent() / 7 + 5, fm.getAscent() / 4);
		//getChild(0).setPosition(getChild(1).width + fStr + 3*fm.getAscent()/4, getChild(1).height/2);
		getChild(0).setPosition(getChild(1).width + fStr + fm.getAscent()/3, fm.getAscent() / 4);// getChild(1).height/2);
        getChild(1).setPosition(fm.getAscent()/3, getAsHoogte()-(this.getChild(1).height/2 + fm.getAscent()/2));
        
        /* FIXME
        if(WiskOpdr.language.toString().equals("en"))
        {
        	kind1.setLocation(k2w + fStr + 3*asc/4, 0);
        	kind2.setLocation(5+fStr, ashoogte + (-kind2.ashoogte + 2*asc/3));
        }
        */
		
		
		//getChild(1).setPosition(fm.getAscent() / 4, 0);
		//getChild(1).setEditable(false);

		FormuleFontChanges changes = new FormuleFontChanges();
		changes.setSmallText(FormuleFontChanges.TRUE);

		getChild(1).setFontChanges(changes);
		
		variant.position(this);
	}
	
	public int getAsHoogte()
	{
		return variant.getAsHoogte(this);
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		// TODO Auto-generated method stub
		super.paintComponent(ctx);

		build(new CanvasBuilder(ctx));
	}

	protected void build(PathBuilder ctx) {
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		

		// FIXME if(language.toString().equals("en"))g.drawString("log", 3 ,ashoogte + asc/2 + asc/12);
		//else g.drawString("log", 5+k2w ,ashoogte + asc/2 + asc/12);
		//ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		FormuleFont fmLog = fm.createCopy();
		fmLog.setItalic(false);
		
		ctx.setFont(fmLog);
		ctx.fillText("log", variant.getLogX(this), getAsHoogte());// + fm.getAscent()/2 + fm.getAscent()/12);
		ctx.setFont(fm);
		int hoogte = getChild(0).height;
		int breedte = width;
		int h =3*fm.getAscent()/2;
		int hh = h/2;
		int b = h/6;
		int bb = b/2;
		
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
		
		int locx =	fm.getAscent()/3+getChild(1).width + fStr;
		int locy = variant.getLogY(this);
		//if(WiskOpdr.language.toString().equals("en"))locy = 0;
		
		ctx.beginPath();
		ctx.moveTo(locx+c+b, locy+d);
		ctx.lineTo(locx+c+b-bb, locy+d+bb);
		ctx.lineTo(locx+c, locy+d+hh-b);
		ctx.lineTo(locx+c, locy+hoogte-hh+b-d);
		ctx.lineTo(locx+c+b-bb, locy+hoogte-bb-d);
		ctx.lineTo(locx+c+b, locy+hoogte-d);
//		ctx.stroke();
//		
//		ctx.beginPath();
		ctx.moveTo(breedte-b-1-c, locy+d);
		ctx.lineTo(breedte-b+bb-1-c, locy+d+bb);
		ctx.lineTo(breedte-1-c, locy+d+hh-b);
		ctx.lineTo(breedte-1-c, locy+hoogte-hh+b-d);
		ctx.lineTo(breedte-b+bb-1-c, locy+hoogte-bb-d);
		ctx.lineTo(breedte-b-1-c, locy+hoogte-d);
		ctx.stroke();
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement#zetMaat()
	 */
	@Override
	public void zetMaat() {
		
		fStr = (int) holder.measureWidth(this, fm, "log");
		variant.position(this);
		super.zetMaat();
	}

	@Override
	public void paintObject()
	{
		this.getChild(0).paint();
		this.getChild(1).paint();
		
		zetMaat();
		paintComponent(ctx);
				
		this.getChild(0).draw(ctx);
		this.getChild(1).draw(ctx);
		this.drawCursor();
	}

	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;
		if (x > getChild().x && x < getChild().x + getChild().width)
			return getChild().setCurrentElementAt(x - getChild().x, y - getChild().y);
		if (x <= getChild().x)
		{
			if (x > getChild(1).x && x < getChild(1).x + getChild(1).width)
				return getChild(1).setCurrentElementAt(x - getChild(1).x, y - getChild(1).y);
			//if x < 1/2 of the "v" the cursor should be placed before this object
			if (x < fm.getAscent() / 3)
				return null;

			this.getChild().setIndexAt(-1);
			holder.setCurrentElement(this.getChild());
			return this.getChild();
		}

		holder.setCurrentElement(this);
		return this;
	}

	@Override
	public String toString()
	{
		return "$L" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}

	private int fStr;
	private final NdeLog variant;

	public String toMathML() 
	{
		return variant.toMathML(this);
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
		drawCursor(svg);
	}

	public int getfStr() {
		return fStr;
	}

	public void setfStr(int fStr) {
		this.fStr = fStr;
	}

}

