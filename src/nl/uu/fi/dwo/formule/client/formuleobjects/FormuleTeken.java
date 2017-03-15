package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.wiskopdr.Letter;
import fi.wiskopdr.FormuleParser;

/**
 * Single character like - + = etc..
 * @author Danny Hendrix
 * 
 */
public class FormuleTeken extends FormuleElement
{
	//private FontMetrics fm;

	private static final int INFINITY_BONUS = 4;
	private String teken;
	private char character;
	private boolean combined;
	private boolean selected = false;
	private boolean functieTeken = false;
	private static boolean maalteken = false;
	private static boolean diffOperatoren = false;

	public FormuleTeken(FormuleElement holder, String tktk)
	{
		super(holder);
		character = tktk.charAt(0);
		teken = tktk;
		combined = true;
		setFont(fm);
	}
	
	
	
	public FormuleTeken(FormuleElement holder, char tk)
	{
		super(holder);
		character = tk;
		//\u2264: kleiner/gelijk, \u2265: groter/gelijk, \u2248: ongeveer gelijk.
		switch(tk) {
		case '+':
		case '=':
		case '<':
		case '>':
		case '\u2264':
		case '\u2265':
		case '\u2248':
		case ':':
			teken = " " + tk + " ";
			break;
		case '*':
		case '\u00d7':
		case '-':
		case '\u3008':
		case '\u3009':
		case '[':
		case ']':
		case '\u2220':
			teken = null; 
			break;
		default:
		
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

			teken = String.valueOf(tk);
		}
		selected = false;
		sizechanged = true;
	}

	private void calculateSize() {	
		double width = calculateWidth();

		int fontheight = fm.getAscent() + fm.getDescent();
		if(fm.isItalic())
		{	if(FormuleFont.formTimes)
			{
				if("f".equals(teken))
					this.setSize((int) width + 8, fontheight);
				else if("j".equals(teken))
					this.setSize((int) width + 5, fontheight);
				else if("p".equals(teken) || "y".equals(teken))
					this.setSize((int) width + 4, fontheight);
				else
					this.setSize((int) width + 2, fontheight);
			}
			else
			{
				if(teken.equals("j"))
					this.setSize((int) width + 4, fontheight);
				else
					this.setSize((int) width + 2, fontheight);
			}
		}
		else
		{	this.setSize((int) width + 1, fontheight);
		}

		this.setAsHoogte(fm.getAscent());//maakt geen verschil..?
	}

	public void zetMaat() {
		calculateSize();
		super.zetMaat();
	}
	
	private double calculateWidth() {
		switch(this.character) {
		case '\u221e':
			FormuleFont fm2 = fm.createCopy();
			fm2.setFontSize(fm.getFontSize() + INFINITY_BONUS); // other font!!!! for infinity
			return holder.measureWidth(this, fm2, teken);
		case '*':
		case '\u00d7':
			if(maalteken)
				return fm.getAscent() / 2 + 7;
			else
				return fm.getAscent() / 2 + 2;
		case '-':
		//case ':':
		case '\u2220':
			return fm.getAscent();
		case 'z':
			fm = fm.createCopy();
			fm.setItalic(true);
			return holder.measureWidth(this, fm, "z");
		case '\u3008':
		case '\u3009':
		case '[':
		case ']':
			return fm.getAscent() / 2;
		} 
		if (this.teken != null)
		{	
			return holder.measureWidth(this, fm, teken);
		}
		else
		{
			return holder.measureWidth(this, fm, " ");
		}
	}


	public static void zetMaalTeken(boolean b)
	{
		maalteken = b;
	}
	
	public static void zetDiffOperatoren(boolean b)
	{
		diffOperatoren = b;
	}
	
	public static boolean isDiffOperator()
	{
		return diffOperatoren;
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
	
	public int getCorrectieLinks()
	{
		if(FormuleFont.formTimes && ("j".equals(teken) || "f".equals(teken)))
			return 3;
		else if("j".equals(teken) || (FormuleFont.formTimes && ("p".equals(teken) || "y".equals(teken))))
			return 2;
		
		return 0;
	}
	
	public int getCorrectieRechts()
	{
		if(FormuleFont.formTimes && "f".equals(teken))
			return 2;
		return 0;
	}

	private void setupCTXState(Context2d ctx)
	{
		if (this.isSelected())
		{
			ctx.setFillStyle("#aaf");
			ctx.fillRect(0, 0, this.width, this.height);
		}
		if (selected)
		{
			ctx.setStrokeStyle("#fff");
			ctx.setFillStyle("#fff");
		}
		else
		{
			ctx.setFillStyle(color);
			ctx.setStrokeStyle(color);
		}
		ctx.setFont(fm.getFontStyle());
	}

	@Override
	public void paintObject()
	{
		//TODO: set the correct width and x position for custom items like [] and x y z etc
		//currently all are draw on a canvas. overwrite the draw(ctx,x,y) method to draw straight on the parent canvas
		
		validate();
		
		Context2d ctx = this.ctx;
		this.setupCTXState(ctx);
		
		//draw single character
		if (teken != null)
		{	//setFont(fm);
			if(FormuleFont.formTimes && teken.equals("f"))
				ctx.fillText(teken, 4, this.getAsHoogte());
			else if(FormuleFont.formTimes && teken.equals("j")) 
				ctx.fillText(teken, 3, this.getAsHoogte());
			else if(teken.equals("j") || (FormuleFont.formTimes && (teken.equals("p") || teken.equals("y"))))
				ctx.fillText(teken, 2, this.getAsHoogte());
			else if(teken.equals("\u221e"))
			{
				FormuleFont fm2 = fm.createCopy();
				fm2.setFontSize(fm.getFontSize() + INFINITY_BONUS);
				ctx.setFont(fm2.getFontStyle());
				ctx.fillText(teken, 0, this.getAsHoogte()  + 3);
				ctx.setFont(fm.getFontStyle());
			}
			else
				ctx.fillText(teken, 0, this.getAsHoogte());
			
			
		} else switch(character) {
		case '*':
		case '\u00d7':
			drawKeer(ctx); break;
		case '-':
			drawMin(ctx); break;
		case ':': // FIXME not used?
			drawDubbelePunt(ctx); break;
			
		case '\u3008':
			{
				int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
				//x = this.width/2 - (3*fm.getAscent() / 8 /2)/2;
				this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
				this.drawline(ctx, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			} break;
		case '\u3009':
			{
//				this.width = fm.getAscent() / 2;
//				this.setSize(width, height);
				
				int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
				this.drawline(ctx, x + fm.getAscent() / 8, y, x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
				this.drawline(ctx, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			} break;
		case '[':
			{
				ctx.setLineWidth(0.6 * fm.getStrokeWidth());
				ctx.beginPath();
				ctx.moveTo(3 * fm.getAscent() / 8, 0);
				ctx.lineTo(fm.getAscent() / 8, 0);
				ctx.lineTo(fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.lineTo(3 * fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.stroke();
				ctx.setLineWidth(fm.getStrokeWidth());
			} break;
		case ']':
			{
				ctx.setLineWidth(0.6 * fm.getStrokeWidth());
				ctx.beginPath();
				ctx.moveTo(fm.getAscent() / 8, 0);
				ctx.lineTo(3 * fm.getAscent() / 8, 0);
				ctx.lineTo(3 * fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.lineTo(fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.stroke();
				ctx.setLineWidth(fm.getStrokeWidth());
			} break;
		case '\u2220':
			{
				ctx.beginPath();
				ctx.moveTo(fm.getAscent() / 4 + fm.getAscent() / 2, fm.getAscent() - 1);
				ctx.lineTo(fm.getAscent() / 4,  fm.getAscent() - 1);
				ctx.lineTo(fm.getAscent() / 4 + fm.getAscent() / 2, 4 * fm.getAscent() / 8);
				ctx.stroke();
			} break;
// FIXME not used?			
		case 'z':
			{
				//TODO: remove the italic?
				fm = fm.createCopy();
				fm.setItalic(true);
				ctx.setFont(fm.getFontStyle());
				ctx.setTextAlign(TextAlign.CENTER);
				ctx.fillText("z", this.width / 2, height);
			} break;	
		}

		/*
		 * else if(character=='2') { g.drawString("2", x,y+fm.getAscent());
		 * g.drawLine
		 * (x+1,y+fm.getAscent()-1,x+1+fm.getAscent()/5,y+fm.getAscent(
		 * )-2-fm.getAscent()/5); }
		 */

		/*
		else if (character == 'y')
		{
			ctx.fillText("y", this.width / 2, 0);
			
			boolean b = getFont().getSize() == 12 && (getFont().getName().equals("SansSerif") || getFont().getName().equals("Arial"));
			if (b)
				g.drawLine(x + fm.getAscent() / 3 - 1, y + fm.getAscent() - 2, x + 1, y + fm.getAscent() - 2 - fm.getAscent() / 3);
				
		}*/
		
		this.drawCursor();
	}

	
	
	public boolean setFont(FormuleFont fm)
	{
		boolean returnWaarde = super.setFont(fm);
				
		boolean italic = false;
		if(Letter.isLetter(character))
		{	if(!functieTeken || FormuleParser.isWoordFormule())
			{	italic = true;
				//bold = true;
			}
			
		}		
		fm.setItalic(italic);
		//fm.setBold(bold);
		
		return returnWaarde;
	}
	
	public boolean setColor(CssColor c)
	{	
		if(super.setColor(c) == false)
			return false;
		
		color = c.toString();
		//ctx.setFillStyle(c);
		//ctx.setStrokeStyle(c);
		return true;
		
	}

	private void drawKeer(Context2d ctx)
	{

//		//dit is veel te veel als de keer als punt wordt getekend. Kijken hoe de breedte in wiskOpdr wordt bepaald. 
//		if(maalteken)
//			this.width = fm.getAscent() / 2 + 7;
//		else
//			this.width = fm.getAscent() / 2 + 2;
//		//this.width = fm.getAscent();
//
//		this.setSize(width, height);

		ctx.setLineWidth(fm.getStrokeWidth());

		if (maalteken)
		{
			ctx.beginPath();
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 - 1);
			ctx.lineTo(fm.getAscent()/4 + 6, 5 * fm.getAscent() / 8 + 5);
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 + 5);
			ctx.lineTo(fm.getAscent()/4 + 6, 5 * fm.getAscent() / 8 - 1);
			ctx.stroke();
		}
		else
		{
			ctx.beginPath();
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8);
			ctx.lineTo(fm.getAscent()/4 + 2, 5 * fm.getAscent() / 8);
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 + 1);
			ctx.lineTo(fm.getAscent()/4 + 2, 5 * fm.getAscent() / 8 + 1);
			ctx.stroke();
		}
	}

	private void drawMin(Context2d ctx)
	{
//		this.width = fm.getAscent();
//		
//		//is dit nodig?
//		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
//		this.setSize(width, height);
		ctx.setLineWidth(0.6 * fm.getStrokeWidth());
		
		ctx.beginPath();
		ctx.moveTo(fm.getAscent() / 4, 5 * fm.getAscent()/8 + 1);
		ctx.lineTo(fm.getAscent()/4 + fm.getAscent()/2, 5 * fm.getAscent()/8 + 1);// + fm.getAscent() / 6);
		ctx.stroke();
		if (getFont().isBold())
		{	ctx.beginPath();
			ctx.moveTo(fm.getAscent() / 4, 5 * fm.getAscent()/8 + 1);
			ctx.lineTo(fm.getAscent()/4 + fm.getAscent()/2, 5 * fm.getAscent()/8 + 1);// + fm.getAscent() / 6);
			ctx.stroke();	
		}
		ctx.setLineWidth(fm.getStrokeWidth());
	}

	private void drawDubbelePunt(Context2d ctx) //FIXME not used?
	{
		int x = 0;
		int y = 0;
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
		functieTeken = b;
	}
	
	public boolean getFunctieTeken()
	{
		return functieTeken;
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
		if(combined) return teken;
		return String.valueOf(this.character);
	}
}
