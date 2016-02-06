package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
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

	private String teken;
	private char character;
	private boolean combined;
	private boolean selected = false;
	private boolean functieTeken = false;
	private static boolean maalteken = false;

	public FormuleTeken(FormuleElement holder, String tktk)
	{
		super(holder);
		character = tktk.charAt(0);
		teken = tktk;
		combined = true;
		calculateSize();
	}
	
	
	
	public FormuleTeken(FormuleElement holder, char tk)
	{
		super(holder);
		character = tk;
		//\u2264: kleiner/gelijk, \u2265: groter/gelijk, \u2248: ongeveer gelijk.
		if (tk == '+' || tk == '=' || tk == '<' || tk == '>' || tk == '\u2264' || tk == '\u2265' || tk == '\u2248' || tk == ':')
			teken = " " + tk + " "; 
		//else if(maalteken && (tk == '*' || tk == '\u00d7'))
		//	teken = " \u00d7 ";
		else if (tk == '*')
			teken = null;
		else if (tk == '\u00d7') //vermenigvuldigingspunt
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
		else if (tk == '\u3008') //punthaak (<) links
			teken = null;
		else if (tk == '\u3009') //punthaak (>) rechts
			teken = null;
		else if (tk == '[')
			teken = null;
		else if (tk == ']')
			teken = null;
		else if (tk == '\u2220') //hoekteken
			teken = null;
		else
			teken = "" + tk;
		

		selected = false;
		// setOpaque(false);
		calculateSize();
	}



	private void calculateSize() {
		
		ctx.setFont(fm.getFontStyle());
		TextMetrics m;
		if (this.teken != null)
		{	m = ctx.measureText(this.teken);
			
		}
		else
		{	m = ctx.measureText(" ");
		}
		this.setSize((int) m.getWidth(), fm.getAscent() + fm.getDescent());
		//this.setAsHoogte(fm.getAscent() / 2);
		this.setAsHoogte(fm.getAscent());//maakt geen verschil..?

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
		//ctx.setTextAlign(TextAlign.CENTER);
		//ctx.setTextBaseline(TextBaseline.HANGING);
		//ctx.setTextBaseline(TextBaseline.BOTTOM);
		ctx.setFont(fm.getFontStyle());
	}

	@Override
	public void paintObject()
	{
		//TODO: set the correct width and x position for custom items like [] and x y z etc
		//currently all are draw on a canvas. overwrite the draw(ctx,x,y) method to draw straight on the parent canvas
		
		this.setSize(width, height);
		this.setAsHoogte(fm.getAscent());
		
		//is dit nodig?
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		//draw single character
		if (teken != null)
		{	setFont(fm);
			this.setupCTXState();
			if(FormuleFont.formTimes && teken.equals("f"))
				ctx.fillText(teken, 4, this.getAsHoogte());
			else if(FormuleFont.formTimes && teken.equals("j")) 
				ctx.fillText(teken, 3, this.getAsHoogte());
			else if(teken.equals("j") || (FormuleFont.formTimes && (teken.equals("p") || teken.equals("y"))))
				ctx.fillText(teken, 2, this.getAsHoogte());
			else if(teken.equals("\u221e"))
			{
				FormuleFont fm2 = fm.createCopy();
				fm2.setFontSize(fm.getFontSize() + 4);
				ctx.setFont(fm2.getFontStyle());
				ctx.fillText(teken, -1, this.getAsHoogte()  + 3);
				ctx.setFont(fm.getFontStyle());
			}
			else
				ctx.fillText(teken, 0, this.getAsHoogte());
			
			
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
			//ctx.setTextBaseline(TextBaseline.BOTTOM);
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
			this.width = fm.getAscent() / 2;
			this.setSize(width, height);
			this.setupCTXState();
			int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
			//x = this.width/2 - (3*fm.getAscent() / 8 /2)/2;
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y, x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			this.drawline(ctx, x + 3 * fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
		}
		else if (character == '\u3009')
		{
			this.width = fm.getAscent() / 2;
			this.setSize(width, height);
			
			this.setupCTXState();
			int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
			this.drawline(ctx, x + fm.getAscent() / 8, y, x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
			this.drawline(ctx, x + fm.getAscent() / 8, y + fm.getAscent() + fm.getDescent(), x + 3 * fm.getAscent() / 8, y + (fm.getAscent() / 2 + fm.getDescent() / 2));
		}
		else if (character == '[')
		{
			this.width = fm.getAscent() / 2;
			this.setSize(width, height);
			this.setupCTXState();
			ctx.setLineWidth(0.6 * fm.getStrokeWidth());
			ctx.beginPath();
			ctx.moveTo(3 * fm.getAscent() / 8, 0);
			ctx.lineTo(fm.getAscent() / 8, 0);
			ctx.lineTo(fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
			ctx.lineTo(3 * fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
			ctx.stroke();
			ctx.setLineWidth(fm.getStrokeWidth());
			
		}
		else if (character == ']')
		{
			this.width = fm.getAscent() / 2;
			this.setSize(width, height);
			this.setupCTXState();
			ctx.setLineWidth(0.6 * fm.getStrokeWidth());
			ctx.beginPath();
			ctx.moveTo(fm.getAscent() / 8, 0);
			ctx.lineTo(3 * fm.getAscent() / 8, 0);
			ctx.lineTo(3 * fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
			ctx.lineTo(fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
			ctx.stroke();
			ctx.setLineWidth(fm.getStrokeWidth());
			
		}
		else if (character == '\u2220')
		{
			this.width = fm.getAscent();
			this.setSize(width, height);
			this.setupCTXState();
			ctx.beginPath();
			ctx.moveTo(fm.getAscent() / 4 + fm.getAscent() / 2, fm.getAscent() - 1);
			ctx.lineTo(fm.getAscent() / 4,  fm.getAscent() - 1);
			ctx.lineTo(fm.getAscent() / 4 + fm.getAscent() / 2, 4 * fm.getAscent() / 8);
			ctx.stroke();
			//this.drawline(ctx, x + fm.getAscent() / 4, y + fm.getAscent() - 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + fm.getAscent() - 1);
			//this.drawline(ctx, x + fm.getAscent() / 4, y + fm.getAscent() - 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 4 * fm.getAscent() / 8);
		}

		this.drawCursor();
	}

	
	
	public boolean setFont(FormuleFont fm)
	{
		boolean returnWaarde = true;
		
		if (super.setFont(fm) == false)
			returnWaarde = false;
		
		
		boolean italic = false;
		if(Letter.isLetter(character))
		{	if(!functieTeken || FormuleParser.isWoordFormule())
			{	italic = true;
				//bold = true;
			}
			
		}
		
		fm.setItalic(italic);
		
		
		//fm.setBold(bold);
		
		ctx.setFont(fm.getFontStyle());
		TextMetrics m;
		if (this.teken != null)
			m = ctx.measureText(this.teken);
		else
			m = ctx.measureText(" ");

		int fontheight = fm.getAscent() + fm.getDescent();
		if(fm.isItalic())
		{	if(FormuleFont.formTimes)
			{
				if("f".equals(teken))
					this.setSize((int) m.getWidth() + 8, fontheight);
				else if("j".equals(teken))
					this.setSize((int) m.getWidth() + 5, fontheight);
				else if("p".equals(teken) || "y".equals(teken))
					this.setSize((int) m.getWidth() + 4, fontheight);
				else
					this.setSize((int) m.getWidth() + 2, fontheight);
			}
			else
			{
				if(teken.equals("j"))
					this.setSize((int) m.getWidth() + 4, fontheight);
				else
					this.setSize((int) m.getWidth() + 2, fontheight);
			}
			
			
		
		}
		else
		{	this.setSize((int) m.getWidth() + 1, fontheight);
		}
		//this.setAsHoogte(fm.getAscent()/2);
		this.setAsHoogte(fm.getAscent());
		return returnWaarde;
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
		//int y = this.getAsHoogte();
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;

		//dit is veel te veel als de keer als punt wordt getekend. Kijken hoe de breedte in wiskOpdr wordt bepaald. 
		if(maalteken)
			this.width = fm.getAscent() / 2 + 7;
		else
			this.width = fm.getAscent() / 2 + 2;
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
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 - 1);
			ctx.lineTo(fm.getAscent()/4 + 6, 5 * fm.getAscent() / 8 + 5);
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 + 5);
			ctx.lineTo(fm.getAscent()/4 + 6, 5 * fm.getAscent() / 8 - 1);
			ctx.stroke();
			//this.drawline(ctx, x + fm.getAscent() / 4 + 1, y + 5 * fm.getAscent() / 8 - 1, x + fm.getAscent() / 4 + 6, y + 5 * fm.getAscent() / 8 + 4);
			//this.drawline(ctx, x + fm.getAscent() / 4 + 1, y + 5 * fm.getAscent() / 8 + 4, x + fm.getAscent() / 4 + 6, y + 5 * fm.getAscent() / 8 - 1);
		}
		else
		{
			ctx.beginPath();
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8);
			ctx.lineTo(fm.getAscent()/4 + 2, 5 * fm.getAscent() / 8);
			ctx.moveTo(fm.getAscent()/4, 5 * fm.getAscent() / 8 + 1);
			ctx.lineTo(fm.getAscent()/4 + 2, 5 * fm.getAscent() / 8 + 1);
			ctx.stroke();
			//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8, x + fm.getAscent() / 4 + 2, y + 5 * fm.getAscent() / 8);
			//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8 + 1, x + fm.getAscent() / 4 + 2, y + 5 * fm.getAscent() / 8 + 1);
		}
		
		
		

		//g.drawLine(x+fm.getAscent()/4,y+5*fm.getAscent()/8,x+fm.getAscent()/4+1,y+5*fm.getAscent()/8);
		//g.drawLine(x+fm.getAscent()/4,y+5*fm.getAscent()/8+1,x+fm.getAscent()/4+1,y+5*fm.getAscent()/8+1);
	}

	private void drawMin()
	{
		this.width = fm.getAscent();
		
		//is dit nodig?
		//x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4;
		this.setSize(width, height);
		this.setupCTXState();
		ctx.setLineWidth(0.6 * fm.getStrokeWidth());
		
		ctx.beginPath();
		ctx.moveTo(fm.getAscent() / 4, 5 * fm.getAscent()/8 + 1);
		ctx.lineTo(fm.getAscent()/4 + fm.getAscent()/2, 5 * fm.getAscent()/8 + 1);// + fm.getAscent() / 6);
		ctx.stroke();
		//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8);
		if (getFont().isBold())
		{	ctx.beginPath();
			//ctx.moveTo(x + fm.getAscent() / 4, y + 1);// fm.getAscent() / 6 + 1);
			//ctx.lineTo(x + fm.getAscent()/4 + fm.getAscent()/2, y + 1);// + fm.getAscent() / 6 + 1);
			ctx.moveTo(fm.getAscent() / 4, 5 * fm.getAscent()/8 + 1);
			ctx.lineTo(fm.getAscent()/4 + fm.getAscent()/2, 5 * fm.getAscent()/8 + 1);// + fm.getAscent() / 6);
		
			ctx.stroke();	
		
		//this.drawline(ctx, x + fm.getAscent() / 4, y + 5 * fm.getAscent() / 8 + 1, x + fm.getAscent() / 4 + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 1);
		}
		
		ctx.setLineWidth(fm.getStrokeWidth());
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
