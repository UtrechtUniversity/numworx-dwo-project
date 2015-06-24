package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElementWithChildren;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleFontChanges;

public class DiffVak extends FormuleElementWithChildren

{		
	private boolean diffBreuk;
	
	public DiffVak(FormuleElement editor)
	{
		super(editor, 2);
	
		//this.paint();
		//this.setChanged(true);
		//this.setAsHoogte(3 * fm.getAscent() / 4);
	
		//setSize(4 * fm.getAscent() / 3, 5 * fm.getAscent() / 4 + fm.getDescent());
		//setAsHoogte(3 * fm.getAscent() / 4);
	
		diffBreuk = getChild(0).toString().length()==1 && Character.isLetter(getChild(0).toString().charAt(0));
        
		width = fm.getAscent()/8+fm.getAscent()+fm.getAscent()/3+getChild(0).width+fm.getAscent()/3+fm.getAscent()/4;
        height = Math.max(getChild(0).height, 2*(fm.getAscent()+fm.getDescent())+fm.getAscent()/4);
        if(diffBreuk) width = fm.getAscent()/8+getChild(0).width+fm.getAscent()/3+fm.getAscent()/4;
        getChild(0).setPosition(fm.getAscent()/8 + fm.getAscent() + fm.getAscent()/3 + 1, (height - getChild(0).height)/2 - 1);
        if(diffBreuk) 
        {	getChild(0).setPosition(fm.getAscent()/2 -1, 0);
        
        }
        getChild(1).setPosition(fm.getAscent()/8 + fm.getAscent()/2 - 2, 
        		(height - (2*(fm.getAscent() + fm.getDescent()) + fm.getAscent()/4))/2 + fm.getAscent() + fm.getDescent() + fm.getAscent()/8);
        
        //setAsHoogte(getChild(1).y - fm.getAscent()/8 - 2);
        setAsHoogte(getChild(1).y + 3*fm.getAscent()/8);
        if(diffBreuk)
        {	//setAsHoogte(getChild(0).height - fm.getAscent()/8 - 1);
        	setAsHoogte(getChild(0).height + 3* fm.getAscent()/8);
        }
        
        
        setSize(width, height);
        //kind1.setLocation(k1x,k1y);
        //kind2.setLocation(k2x,k2y);
		
        
        
        
        
		//getChild(0).setPosition(5 * fm.getAscent() / 7 + 5, fm.getAscent() / 4);
	
		//getChild(1).setPosition(fm.getAscent() / 4, 0);
		//getChild(1).setEditable(false);
	
		//FormuleFontChanges changes = new FormuleFontChanges();
		//changes.setSmallText(FormuleFontChanges.TRUE);
	
		//getChild(1).setFontChanges(changes);
	}
	
	public int getAsHoogte()
	{
		
		if(diffBreuk)
        	//return getChild(0).height - fm.getAscent()/8 - 1;
			return getChild(0).height + 3 * fm.getAscent()/8;
		else
			//return getChild(1).y - fm.getAscent()/8 - 2;
			return getChild(1).y + 3 * fm.getAscent()/8;
        
	}
	
	@Override
	public void paintObject()
	{
		
		this.getChild(0).paint();
		this.getChild(1).paint();
	
		diffBreuk = getChild(0).toString().length()==1 && Character.isLetter(getChild(0).toString().charAt(0));
        double asc = fm.getAscent();
		double desc = fm.getDescent();
		width = (int) (asc/8+asc+asc/3+getChild(0).width+asc/3+asc/4);
        height = (int) (Math.max(getChild(0).height, 2*(asc+desc)+asc/4));
        if(diffBreuk) width = (int) (asc/8+getChild(0).width+asc/3+asc/4);
        int k1x = (int) (asc/8+asc+asc/3+1);
        int k1y = (height-getChild(0).height)/2-1;//was zonder - 1
        if(diffBreuk) 
        {	k1x = (int) (asc/2) - 1;
        	k1y = 0;//was -1;
        }
        int k2x = (int) (asc/8+asc/2 - 1)-2;//was zonder -2;
        int k2y = (int) ((height-(2*(asc+desc)+asc/4))/2+asc+desc+asc/8); //was met -1);
        
        //setAsHoogte(getChild(1).y - fm.getAscent()/8 - 2);
        setAsHoogte(getChild(1).y + 3 * fm.getAscent() / 8);
        //ashoogte = k2y- fm.getAscent()/8-2;//k1a + k1y;
        if(diffBreuk) 
        	//setAsHoogte(getChild(0).height - fm.getAscent()/8-1);
        	setAsHoogte(getChild(0).height + 3 * fm.getAscent()/8);
    	
        
        setSize(width, height);
        getChild(0).setPosition(k1x, k1y);
        getChild(1).setPosition(k2x, k2y);
        //kind1.setLocation(k1x,k1y);
        //kind2.setLocation(k2x,k2y);
		
		//setSize(5 * fm.getAscent() / 6 + getChild(0).width + 5, fm.getAscent() / 4 + getChild(0).height);
		//setAsHoogte(getChild(0).getAsHoogte() + fm.getAscent() / 4);
	
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
	
		//ctx.setStrokeStyle("#000");
		//ctx.setFillStyle("#000");
		
		ctx.setStrokeStyle(color);
		ctx.setFillStyle(color);
		
		String dString = "d";
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		fm.setItalic(false);
		ctx.setFont(fm.getFontStyle());
		
		ctx.beginPath();
		ctx.moveTo(fm.getAscent()/8, (height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2+fm.getAscent()+fm.getDescent());
		ctx.lineTo(fm.getAscent()/8+fm.getAscent(),(height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2+fm.getAscent()+fm.getDescent());
		ctx.stroke();
		//this.drawline(ctx, fm.getAscent()/8,(height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2+fm.getAscent()+fm.getDescent(),fm.getAscent()/8+fm.getAscent(),(height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2+fm.getAscent()+fm.getDescent());
		ctx.fillText(dString, fm.getAscent()/8+ (diffBreuk?0:fm.getAscent()/4),(height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2+fm.getAscent());
		ctx.fillText(dString, fm.getAscent()/8, height-fm.getDescent()-(height-(2*fm.getAscent()+2*fm.getDescent()+fm.getAscent()/4))/2-fm.getAscent()/6);
		fm.setItalic(true);
		//posities gecontroleerd en komen overeen.
		
		
		
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
			ctx.stroke();
			
			ctx.beginPath();
			ctx.moveTo(breedte-b-1-c, locy+d);
			ctx.lineTo(breedte-b+bb-1-c, locy+d+bb);
			ctx.lineTo(breedte-1-c, locy+d+hh-b);
			ctx.lineTo(breedte-1-c, locy+hoogte-hh+b-d);
			ctx.lineTo(breedte-b+bb-1-c, locy+hoogte-bb-d);
			ctx.lineTo(breedte-b-1-c, locy+hoogte-d);
			ctx.stroke();
			
			
			//this.drawline(ctx, locx+c+b, locy+d, locx+c+b-bb, locy+d+bb);
			//this.drawline(ctx, locx+c+b-bb, locy+d+bb, locx+c, locy+d+hh-b);
			//this.drawline(ctx, locx+c, locy+d+hh-b, locx+c, locy+hoogte-hh+b-d);		
			//this.drawline(ctx, locx+c+b-bb, locy+hoogte-bb-d, locx+c, locy+hoogte-hh+b-d);
			//this.drawline(ctx, locx+c+b, locy+hoogte-d, locx+c+b-bb, locy+hoogte-bb-d);
			
			//this.drawline(ctx, breedte-b-1-c, locy+d, breedte-b+bb-1-c, locy+d+bb);
			//this.drawline(ctx, breedte-b+bb-1-c, locy+d+bb, breedte-1-c, locy+d+hh-b);
			//this.drawline(ctx, breedte-1-c, locy+d+hh-b, breedte-1-c, locy+hoogte-hh+b-d);		
			//this.drawline(ctx, breedte-b+bb-1-c, locy+hoogte-bb-d, breedte-1-c, locy+hoogte-hh+b-d);
			//this.drawline(ctx, breedte-b-1-c, locy+hoogte-d, breedte-b+bb-1-c, locy+hoogte-bb-d);
		}

	
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


