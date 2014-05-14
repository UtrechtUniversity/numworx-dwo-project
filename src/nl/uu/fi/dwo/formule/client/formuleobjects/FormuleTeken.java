package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * Single character like - + = etc..
 * @author Danny Hendrix
 * 
 */
public class FormuleTeken extends FormuleElement
{
	//private FontMetrics fm;

	private String teken;
	private char character;
	private boolean selected = false;
	private boolean functieTeken = false;
	private static boolean maalteken = false;

	public FormuleTeken(FormuleElement holder, char tk)
	{
		super(holder);
		character = tk;
		if (tk == '+' || tk == '=' || tk == '<' || tk == '>' || tk == '\u2264' || tk == '\u2265' || tk == '\u2248' || tk == ':')
			teken = " " + tk + " ";
		//else if(maalteken && (tk == '*' || tk == '\u00d7'))
		//	teken = " \u00d7 ";
		else if (tk == '*')
			teken = null;
		else if (tk == '\u00d7')
			teken = null;
		else if (tk == '-')
			teken = null;
		//else if (tk == ':')
		//	teken = null;
		/*
		 * else if(tk=='2') { teken = null; }
		 */
		//Sietske: onderstaande weggecommentarieerd, want hierdoor nam een z in formules niet de juiste kleur aan.
		//else if (tk == 'z')
		//	teken = null;
		//TODO:removed this?
		//else if (tk == 'y')
		//teken = null;
		else if (tk == '\u3008')
			teken = null;
		else if (tk == '\u3009')
			teken = null;
		else if (tk == '[')
			teken = null;
		else if (tk == ']')
			teken = null;
		else if (tk == '\u2220')
			teken = null;
		else
			teken = "" + tk;

		selected = false;
		// setOpaque(false);
		ctx.setFont(fm.getFontStyle());
		TextMetrics m;
		if (this.teken != null)
			m = ctx.measureText(this.teken);
		else
			m = ctx.measureText(" ");

		this.setSize((int) m.getWidth(), fm.getHeight());
		//this.setAsHoogte(this.height / 2);
		this.setAsHoogte(fm.getAscent() / 2);
		
	}


	public static void zetMaalTeken(boolean b)
	{
		maalteken = b;
	}
	

	public int getCorrItalic()
	{
		// hm? :s
		return 0;
		// System.out.println("" +
		// fm.getStringBounds(teken,getGraphics()).getWidth());
		// System.out.println("" + fm.stringWidth(teken));
		// return
		// (int)Math.round((fm.getStringBounds(teken,getGraphics()).getWidth() -
		// fm.stringWidth(teken)));
	}

	private void setupCTXState()
	{
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		/*
				if (this.isCurrent())
				{
					ctx.setStrokeStyle("#f00");
					ctx.setFillStyle("#f00");
				}
				else */if (selected)
		{
			ctx.setStrokeStyle("#fff");
			ctx.setFillStyle("#fff");
		}
		//g.setColor(Color.white);
		else
		{
			//ctx.setStrokeStyle("#000");
			//ctx.setFillStyle("#000");
			ctx.setFillStyle(color);
			ctx.setStrokeStyle(color);
			
		}
		// g.setColor(fgColor);

		// g.setFont(getFont());
		ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.HANGING);
		ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());
	}

	@Override
	public void paintObject()
	{
		//TODO: set the correct width and x position for custom items like [] and x y z etc
		//currently all are draw on a canvas. overwrite the draw(ctx,x,y) method to draw straight on the parent canvas
		
		//wat moet de ashoogte zijn? Gewoon hoogte / 2 is volgens mij niet zo'n goed idee, dan komen - en x heel laag terecht.
		
		//this.setAsHoogte(this.height / 2);
		
		this.setSize(width, height);
		this.setAsHoogte(fm.getAscent()/2);
		
		//this.setAsHoogte(holder.getMainRegel().getAsHoogte());
		
		
		//this.setAsHoogte(holder.getMainRegel().getAsHoogte());
		//this.setSize(width, height);

		int x = 0;
		int y = 0;
		//is dit nodig?
		x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		//draw single character
		if (teken != null)
		{	setFont(fm);
			this.setupCTXState();
			ctx.fillText(teken, this.width / 2, this.height);
			//ctx.beginPath();ctx.rect(0, 0, width, height);ctx.stroke();
		}

		// g.drawString(teken,x,y+fm.getAscent());
		else if (character == '*' || character == '\u00d7')
		{
			this.drawKeer();
		}

		else if (character == '-')
		{
			this.drawMin();
		}
		else if (character == ':')
		{
			this.drawDubbelePunt();
		}
		/*
		 * else if(character=='2') { g.drawString("2", x,y+fm.getAscent());
		 * g.drawLine
		 * (x+1,y+fm.getAscent()-1,x+1+fm.getAscent()/5,y+fm.getAscent(
		 * )-2-fm.getAscent()/5); }
		 */

		else if (character == 'z')
		{
			//TODO: remove the italic?
			fm = fm.createCopy();
			fm.setItalic(true);
			ctx.setFont(fm.getFontStyle());

			TextMetrics m = ctx.measureText("z");
			this.setSize((int) m.getWidth(), height);
			ctx.setFont(fm.getFontStyle());
			ctx.setTextAlign(TextAlign.CENTER);
			ctx.setTextBaseline(TextBaseline.BOTTOM);
			ctx.fillText("z", this.width / 2, height);
			// boolean b = getFont().getSize()==12 &&
			// (getFont().getName().equals("SansSerif") ||
			// getFont().getName().equals("Arial"));
			// g.drawLine(x-1,y+fm.getAscent()-1,x+fm.getAscent()/3,y+fm.getAscent()-2-fm.getAscent()/3);
		}
		/*
		else if (character == 'y')
		{
			ctx.fillText("y", this.width / 2, 0);
			
			boolean b = getFont().getSize() == 12 && (getFont().getName().equals("SansSerif") || getFont().getName().equals("Arial"));
			if (b)
				g.drawLine(x + fm.getAscent() / 3 - 1, y + fm.getAscent() - 2, x + 1, y + fm.getAscent() - 2 - fm.getAscent() / 3);
				
		}*/
		else if (character == '\u3008')
		{
			this.setupCTXState();
			x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
			//x = this.width/2 - (3*fm.getAscent() / 8 /2)/2;
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
		}
		else if (character == '\u3009')
		{
			this.setupCTXState();
			x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
			this.drawline(ctx, x + fm.getAscent() / 8, y, x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			this.drawline(ctx, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
		}
		else if (character == '[')
		{
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + fm.getAscent() / 8, y);
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1);
			this.drawline(ctx, x + fm.getAscent() / 8, y, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1);
		}
		else if (character == ']')
		{
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + fm.getAscent() / 8, y);
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1);
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent() - 1);
		}
		else if (character == '\u2220')
		{
			this.drawline(ctx, x + fm.getAscent() / 4, y + fm.getAscent() - 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + fm.getAscent() - 1);
			this.drawline(ctx, x + fm.getAscent() / 4, y + fm.getAscent() - 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 4 * fm.getAscent() / 8);
		}

		this.drawCursor();
	}

	public boolean setFont(FormuleFont fm)
	{
		if (super.setFont(fm) == false)
			return false;
		
		boolean italic = false;
		if(Character.isLetter(character))
		{	if(!functieTeken)
				italic = true;
		}
		
		fm.setItalic(italic);
		
		ctx.setFont(fm.getFontStyle());
		TextMetrics m;
		if (this.teken != null)
			m = ctx.measureText(this.teken);
		else
			m = ctx.measureText(" ");

		this.setSize((int) m.getWidth()+1, fm.getHeight());
		this.setAsHoogte(fm.getAscent()/2);
		
		return true;
	}
	
	public boolean setColor(CssColor c)
	{	
		if(super.setColor(c) == false)
			return false;
		
		color = c;
		//ctx.setFillStyle(c);
		//ctx.setStrokeStyle(c);
		return true;
		
	}

	private void drawKeer()
	{
		int x = 0;
		//int y = 0;
		int y = this.getAsHoogte();
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;

		//dit is veel te veel als de keer als punt wordt getekend. Kijken hoe de breedte in wiskOpdr wordt bepaald. 
		if(maalteken)
			this.width = fm.getAscent() / 4 + 6 + fm.getAscent()/4;
		else
			this.width = fm.getAscent() / 4 + fm.getAscent()/4;
		//this.width = fm.getAscent();

		this.setSize(width, height);
		this.setupCTXState();
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4 + 5;
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		ctx.setLineWidth(fm.getStrokeWidth());

		if (maalteken)
		{
			ctx.beginPath();
			//ctx.moveTo(x + fm.getAscent()/4 + 1, y + 5 * fm.getAscent()/8 - 1);
			//ctx.lineTo(x + fm.getAscent()/4 + 6, y + 5 * fm.getAscent()/8 + 4);
			//ctx.moveTo(x + fm.getAscent()/4 + 1, y + 5 * fm.getAscent()/8 + 4);
			//ctx.lineTo(x + fm.getAscent()/4 + 6, y + 5 * fm.getAscent()/8 - 1);
			ctx.moveTo(x + fm.getAscent()/4, y - 1);
			ctx.lineTo(x + fm.getAscent()/4 + 6, y + 5);
			ctx.moveTo(x + fm.getAscent()/4, y + 5);
			ctx.lineTo(x + fm.getAscent()/4 + 6, y - 1);
			ctx.stroke();
			//this.drawline(ctx, x + fm.getAscent() / 4 + 1, y + 5 * fm.getAscent() / 8 - 1, x + fm.getAscent() / 4 + 6, y + 5 * fm.getAscent() / 8 + 4);
			//this.drawline(ctx, x + fm.getAscent() / 4 + 1, y + 5 * fm.getAscent() / 8 + 4, x + fm.getAscent() / 4 + 6, y + 5 * fm.getAscent() / 8 - 1);
		}
		else
		{
			ctx.beginPath();
			ctx.moveTo(x + fm.getAscent()/4, y + 5 * fm.getAscent()/8);
			ctx.lineTo(x + fm.getAscent()/4 + 2, y + 5 * fm.getAscent()/8);
			ctx.moveTo(x + fm.getAscent()/4, y + 5 * fm.getAscent()/8 + 1);
			ctx.lineTo(x + fm.getAscent()/4 + 2, y + 5 * fm.getAscent()/8 + 1);
			ctx.stroke();
			//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8, x + fm.getAscent() / 4 + 2, y + 5 * fm.getAscent() / 8);
			//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8 + 1, x + fm.getAscent() / 4 + 2, y + 5 * fm.getAscent() / 8 + 1);
		}

		//g.drawLine(x+fm.getAscent()/4,y+5*fm.getAscent()/8,x+fm.getAscent()/4+1,y+5*fm.getAscent()/8);
		//g.drawLine(x+fm.getAscent()/4,y+5*fm.getAscent()/8+1,x+fm.getAscent()/4+1,y+5*fm.getAscent()/8+1);
	}

	private void drawMin()
	{
		int x = 0;
		int y = this.getAsHoogte();
		//is dit nodig?
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
//System.out.println("in drawMin: x, was 0, wordt: " + x +" en width is nu nog " + width);
//fm.getAscent() = fm.getAscent()/4 + fm.getAscent()/2 + fm.getAscent()/4. Maar dat is natuurlijk niet helemaal waar vanwege afronding.
this.width = fm.getAscent()/4 + fm.getAscent()/2 + fm.getAscent()/4;
		//System.out.println("width is nu: " + width);

		this.setSize(width, height);
		this.setupCTXState();
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		ctx.setLineWidth(fm.getStrokeWidth());
		
		ctx.beginPath();
		ctx.moveTo(x + fm.getAscent() / 4, y);// + fm.getAscent() / 6);
		ctx.lineTo(x + fm.getAscent()/4 + fm.getAscent()/2, y);// + fm.getAscent() / 6);
		ctx.stroke();
		//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8);
		//TODO:
		if (getFont().isBold())
		{	ctx.beginPath();
			ctx.moveTo(x + fm.getAscent() / 4, y + 1);// fm.getAscent() / 6 + 1);
			ctx.lineTo(x + fm.getAscent()/4 + fm.getAscent()/2, y + 1);// + fm.getAscent() / 6 + 1);
			ctx.stroke();	
		
		//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8 + 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 1);
		}
	}

	private void drawDubbelePunt()
	{
		int x = 0;
		int y = 0;
		x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		this.width = fm.getAscent();

		this.setSize(width, height);
		this.setupCTXState();
		x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4 + 5;
		ctx.setLineWidth(fm.getStrokeWidth());
		this.drawline(ctx, x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 - 2, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 - 2);
		this.drawline(ctx, x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 - 1, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 - 1);
		this.drawline(ctx, x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 4, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 + 4);
		this.drawline(ctx, x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 5, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 + 5);
	}

	public char geefChar()
	{
		return this.character;
	}

	public void zetFunctieTeken(boolean b)
	{
		//TODO: zetFunctieTeken?
	}

	@Deprecated
	@Override
	public FormuleElement setCurrentElementAt(int x, int y)
	{
		//ignore if the formule is not editable
		if (holder instanceof FormuleEditor == false)
			return null;
		FormuleHolder holder = (FormuleHolder) this.holder;

		if (x < this.width / 2)
			return null;
		holder.setCurrentElement(this);
		return this;
	}

	@Override
	public boolean isNumber()
	{
		if (this.character == '.' || this.character == ',')
			return true;
		if (this.character >= '0' && this.character <= '9')
			return true;
		return false;
	}

	public String toString()
	{
		return "" + this.character;
	}
}
