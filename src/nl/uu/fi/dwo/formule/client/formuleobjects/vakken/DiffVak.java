package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d;

import fi.wiskopdr.Letter;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class DiffVak extends FormuleElementWithChildren

{		
	private boolean diffBreuk;
	private double asc;
	private double desc;
	
	public DiffVak(FormuleElement editor)
	{
		super(editor, 2);
		//hier op één of andere manier x in tweede kind stoppen. 
		//Maar wel zo dat die x ook weer verdwijnt als er iets anders (bijvoorbeeld het opgeslagen kind, dat ook x kan zijn) in wordt gezet.
		getChild(1).insert("x");
	
		diffBreuk = getChild(0).toString().length()==1 && Letter.isLetter(getChild(0).toString().charAt(0));
        
		width = fm.getAscent()/8+fm.getAscent()+fm.getAscent()/3+getChild(0).width+fm.getAscent()/3+fm.getAscent()/4;
        height = Math.max(getChild(0).height, 2*(fm.getAscent()+fm.getDescent())+fm.getAscent()/4);
        if(diffBreuk) width = fm.getAscent()/8+getChild(0).width+fm.getAscent()/3+fm.getAscent()/4;
        getChild(0).setPosition(fm.getAscent()/8 + fm.getAscent() + fm.getAscent()/3 + 1, (height - getChild(0).height)/2 - 1);
        if(diffBreuk) 
        {	getChild(0).setPosition(fm.getAscent()/2 -1, 0);
        
        }
        
        getChild(1).setPosition(fm.getAscent()/8 + fm.getAscent()/2 - 2, 
        		(height - (2*(fm.getAscent() + fm.getDescent()) + fm.getAscent()/4))/2 + fm.getAscent() + fm.getDescent() + fm.getAscent()/8);
        setAsHoogte(getChild(1).y + 3*fm.getAscent()/8);
        if(diffBreuk)
        {	setAsHoogte(getChild(0).height + 3* fm.getAscent()/8);
        }
        
        
        setSize(width, height);
    }
	
//	public int getAsHoogte()
//	{
//		
//		if(diffBreuk)
//        	//return getChild(0).height - fm.getAscent()/8 - 1;
//			return getChild(0).height + 3 * fm.getAscent()/8;
//		else
//			//return getChild(1).y - fm.getAscent()/8 - 2;
//			return getChild(1).y - fm.getAscent()/8 - 1;
//        
//	}
	
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
	
	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren#paintComponent(com.google.gwt.canvas.dom.client.Context2d)
	 */
	@Override
	public void paintComponent(Context2d ctx) {
		super.paintComponent(ctx);
	
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		String dString = "d";
		fm.setItalic(false);
		ctx.setFont(fm.getFontStyle());
		
		ctx.beginPath();
		ctx.moveTo(fm.getAscent()/8, getAsHoogte() - 3*asc/8 + 1);
		ctx.lineTo(fm.getAscent()/8+fm.getAscent(), getAsHoogte() - 3*asc/8 + 1);
				
		ctx.stroke();
		ctx.fillText(dString, fm.getAscent()/8+ (diffBreuk?0:fm.getAscent()/4), getAsHoogte() - 3*asc/8 - desc);
		int k2y = getChild(2).getY();
		ctx.fillText(dString, fm.getAscent()/8, k2y + asc);
		fm.setItalic(true);
		
		
		int hoogte = getChild(0).height;
		int breedte = width;
		int h =3*fm.getAscent()/2;
		int hh = h/2;
		int b = h/6;
		int bb = b/2;
		
		int c = fm.getAscent()/6;
		int d = fm.getAscent()/8;
		
		int locx = fm.getAscent()/8+fm.getAscent();
		int locy = (height-getChild(0).height)/2;
		
		if(!diffBreuk)
		{	ctx.beginPath();
			ctx.moveTo(locx+c+b, locy+d);
			ctx.lineTo(locx+c+b-bb, locy+d+bb);
			ctx.lineTo(locx+c, locy+d+hh-b);
			ctx.lineTo(locx+c, locy+hoogte-hh+b-d);
			ctx.lineTo(locx+c+b-bb, locy+hoogte-bb-d);
			ctx.lineTo(locx+c+b, locy+hoogte-d);
//			ctx.stroke();
//			
//			ctx.beginPath();
			ctx.moveTo(breedte-b-1-c, locy+d);
			ctx.lineTo(breedte-b+bb-1-c, locy+d+bb);
			ctx.lineTo(breedte-1-c, locy+d+hh-b);
			ctx.lineTo(breedte-1-c, locy+hoogte-hh+b-d);
			ctx.lineTo(breedte-b+bb-1-c, locy+hoogte-bb-d);
			ctx.lineTo(breedte-b-1-c, locy+hoogte-d);
			ctx.stroke();
		}

	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement#zetMaat()
	 */
	@Override
	public void zetMaat() {
		diffBreuk = getChild(0).toString().length()==1 && Character.isLetter(getChild(0).toString().charAt(0));
        asc = fm.getAscent();
		desc = fm.getDescent();
		width = (int) (asc/8+asc+asc/3+getChild(0).width+asc/3+asc/4);
		//int h1 = getChild(0).height;
		//int h2 = (int) (getChild(1).height + asc + desc + asc/4);
		height = (int) (Math.max(getChild(0).height, asc + desc + getChild(1).height + asc/4));
        if(diffBreuk) 
        	width = (int) (asc/8+getChild(0).width+asc/3+asc/4);
        int k1x = (int) (asc/8+asc+asc/3+2);
        int k1y = (int) ((height-getChild(0).height)/2);// + 4*asc/8 + 1);//was zonder - 1
        if(diffBreuk) 
        {	k1x = (int) (asc/2) - 1;
        	k1y = -1;
        }
        int k2x = (int) (asc/8+asc/2 - 1)-2;//was zonder -2;
        setAsHoogte((int) (getChild(0).getAsHoogte() + k1y));// - 4* asc/8 - 1));
        if(diffBreuk) 
        	setAsHoogte((int)(asc + desc + 3* asc/8));
        
        int k2y = (int) (getAsHoogte() + asc/8 - asc/2 + asc/8);
        setSize(width, height);
        getChild(0).setPosition(k1x, k1y);
        getChild(1).setPosition(k2x, k2y);
		super.zetMaat();
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
	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		//getChild().setPosition(5 * fm.getAscent() / 7 - 1, fm.getAscent() / 4);
		return true;
	}
	
	@Override
	public String toString()
	{
		return "$d" + getChild(0).toString() + "$n" + getChild(1).toString() + "@@";
	}

	public String toMathML() 
	{
		return "<mfrac><mrow><mo>d</mo>" + getChild(0).toMathML() + "</mrow><mrow><mo>d</mo>" + getChild(1).toMathML() + "</mrow></mfrac>";
	}

}


