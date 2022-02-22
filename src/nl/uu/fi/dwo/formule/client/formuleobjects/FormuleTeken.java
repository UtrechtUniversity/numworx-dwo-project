package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.CanvasBuilder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PathBuilder;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SvgBuilder;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

import java.awt.Font;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGImageElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;

import fi.wiskopdr.Letter;
import fi.wiskopdr.FormuleParser;

/**
 * Single character like - + = etc..
 * @author Danny Hendrix
 * 
 */
public class FormuleTeken extends FormuleElement
{
	private static final int INFINITY_BONUS = 4;
	private String teken;
	private char character;
	private boolean combined;
	private boolean selected;
	private boolean functieTeken;
	private OMSVGRectElement selectedRect;
	private static boolean maalteken;
	private static boolean diffOperatoren;

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
		case '\u00d7':
		case '·':
		case ':':
			teken = "\u00A0" + tk + "\u00A0";
			break;
		case '*':
		case '-':
		case '\u3008':
		case '\u3009':
		case '\u2705':
		case '\u2714':
		case '\u274c':
		case '[':
		case ']':
		case '\u2220':
			teken = null; 
			break;
		case ' ':
			teken = "\u00A0";
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
		if(teken != null) setFont(fm);
	}

	private void calculateSize() {	
		double width = calculateWidth();
		int fontheight = fm.getAscent() + fm.getDescent();
		if(teken != null) {
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
		} else {
			setSize( (int)width, fontheight); // zonder +1
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
		//case '\u00d7':
			if(maalteken)
				return fm.getAscent() / 2 + 7;
			else
				return fm.getAscent() / 2 + 2;
		
		case '-':
		//case ':':
		case '\u2220':
			return fm.getAscent();
		case '\u2705':
		case '\u2714':
		case '\u274c':
			return 4*fm.getAscent()/3;
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
		
		paintComponent(this.ctx);
		
		this.drawCursor();
	}



	public void paintComponent(Context2d ctx) {
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
		} else
			buildChar(new CanvasBuilder(ctx));

	}

	protected void buildChar(PathBuilder ctx) {
		ctx.setStrokeStyle(selected?"white":color);
		switch(character) {
		case '*':
		//case '\u00d7':
			drawKeer(ctx); break;
		case '-':
			drawMin(ctx); break;
		case ':': // FIXME not used?
			drawDubbelePunt(ctx); break;			
		case '\u2705':
			drawGoedVink(ctx); break;	
		case '\u2714':
			drawHalfVink(ctx); break;
		case '\u274c':
			drawFoutKruis(ctx); break;
		case '\u3008':
			{
				int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
				ctx.setStrokeStyle(selected?"white":color);
				ctx.beginPath();
				ctx.moveTo(x + 3 * fm.getAscent() / 8, 0);
				ctx.lineTo(x + fm.getAscent() / 8, 0 + (fm.getAscent() / 2 + fm.getDescent() / 2));
				ctx.lineTo(x + 3 * fm.getAscent() / 8, 0 + fm.getAscent() + fm.getDescent());
				ctx.stroke();			
			} break;
		case '\u3009':
			{				
				int x = this.width / 2 - 3 / 2 - fm.getAscent() / 4;
				ctx.drawline(x + fm.getAscent() / 8, 0, x + 3 * fm.getAscent() / 8, 0 + (fm.getAscent() / 2 + fm.getDescent() / 2));
				ctx.drawline(x + fm.getAscent() / 8, 0 + fm.getAscent() + fm.getDescent(), x + 3 * fm.getAscent() / 8, 0 + (fm.getAscent() / 2 + fm.getDescent() / 2));
			} break;
		case '[':
			{
				ctx.setLineWidth(1.25 * fm.getStrokeWidth());
				ctx.beginPath();
				ctx.moveTo(3 * fm.getAscent() / 8, 1);
				ctx.lineTo(fm.getAscent() / 8, 1);
				ctx.lineTo(fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.lineTo(3 * fm.getAscent() / 8, fm.getAscent() + fm.getDescent() - 1);
				ctx.stroke();
				ctx.setLineWidth(fm.getStrokeWidth());
			} break;
		case ']':
			{
				ctx.setLineWidth(1.25 * fm.getStrokeWidth());
				ctx.beginPath();
				ctx.moveTo(fm.getAscent() / 8, 1);
				ctx.lineTo(3 * fm.getAscent() / 8, 1);
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
		}
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
		if(this.fm.isItalic() != italic)
		{
			(this.fm = this.fm.createCopy()).setItalic(italic);
			setChanged(true);
			returnWaarde = true;
		}
		//fm.setBold(bold);
		
		return returnWaarde;
	}
	
	public boolean setColor(CssColor c)
	{	
		if(super.setColor(c) == false)
			return false;		
		color = c.toString();
		return true;
		
	}

	private void drawKeer(PathBuilder ctx)
	{
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
	
	private void drawGoedVink(PathBuilder ctx)
	{
		ctx.setLineWidth(2.5*fm.getStrokeWidth());
		ctx.setStrokeStyle((CssColor.make(0, 180, 0)).toString());
		int x = fm.getAscent()/3;
		int y = 0;
		int d = 2*fm.getAscent()/3+1;
		
		ctx.beginPath();
		ctx.moveTo(x, y+d);
		ctx.lineTo(x+d-1, y+d);
		ctx.moveTo(x, y+d/2);
		ctx.lineTo(x+d/3+1, y+d-2);
		ctx.lineTo(x+d-1, y+1);
		ctx.stroke();
	}
	
	private void drawHalfVink(PathBuilder ctx)
	{
		ctx.setLineWidth(2.5*fm.getStrokeWidth());
		ctx.setStrokeStyle((CssColor.make(255, 200, 0)).toString());
		int x = fm.getAscent()/3;
		int y = 0;
		int d = 2*fm.getAscent()/3+1;
		
		ctx.beginPath();
		ctx.moveTo(x, y+d);
		ctx.lineTo(x+d/2, y+d);
		ctx.moveTo(x, y+d/2);
		ctx.lineTo(x+d/3+1, y+d-2);
		ctx.lineTo(x+d-1, y+1);
		ctx.stroke();
		
		ctx.setStrokeStyle((CssColor.make(255, 240, 180)).toString());
		ctx.beginPath();
		ctx.moveTo(x+d/2, y+d);
		ctx.lineTo(x+d-1, y+d);
		ctx.stroke();
	}
	
	private void drawFoutKruis(PathBuilder ctx)
	{
		ctx.setLineWidth(2.5*fm.getStrokeWidth());
		ctx.setStrokeStyle((CssColor.make(200, 0, 0)).toString());
		int x = fm.getAscent()/3;
		int y = 0;
		int d = 2*fm.getAscent()/3+1;
		
		ctx.beginPath();
		ctx.moveTo(x+1, y+1);
		ctx.lineTo(x+d-1, y+d-1);
		ctx.moveTo(x+1, y+d-1);
		ctx.lineTo(x+d-1, y+1);
		ctx.stroke();
	}


	private void drawMin(PathBuilder ctx)
	{
		ctx.setLineWidth(1.25 * fm.getStrokeWidth());
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

	private void drawDubbelePunt(PathBuilder ctx) //FIXME not used?
	{
		int x = 0;
		int y = 0;
		x = this.width / 2 - (fm.getAscent() / 2) / 2 - fm.getAscent() / 4 + 5;
		ctx.setLineWidth(fm.getStrokeWidth());
		ctx.drawline( x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 - 2, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 - 2);
		ctx.drawline( x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 - 1, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 - 1);
		ctx.drawline( x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 4, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 + 4);
		ctx.drawline( x + fm.getAscent() / 2, y + 5 * fm.getAscent() / 8 + 5, x + fm.getAscent() / 2 + 1, y + 5 * fm.getAscent() / 8 + 5);
	}

	public char geefChar()
	{
		return this.character;
	}

	public void zetFunctieTeken(boolean b)
	{	boolean old = functieTeken;
		functieTeken = b;
		if(b != old)
		{	
			setFont(fm); // 
			setChanged(true);
		}
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
	

	private void createSelection(OMSVGElement svg) {
		selectedRect = new OMSVGRectElement(x,y,width,height,0, 0);
		paintSelection();
		svg.appendChild(selectedRect);
	}
	
	public void paintSelection() {
		if (isSelected()) {
			selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "#AAAAFF");
		} else {
			selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
		}
		selectedRect.getWidth().getBaseVal().setValue(width);
		selectedRect.getX().getBaseVal().setValue(x);
		
		drawCursor((OMSVGElement)null);
	}
	
	@Override
	public void draw(OMSVGElement svg) {
		createSelection(svg);

		if(teken != null) {
			int dx = x;
			int dy = y+getAsHoogte();
			int fs = fm.getFontSize();
// times f offset 4
			if(FormuleFont.formTimes && teken.equals("f"))
				dx += 4;
// times j offset 3
			else if(FormuleFont.formTimes && teken.equals("j")) 
				dx += 3;
// gewone j, times p of times y
			else if(teken.equals("j") || (FormuleFont.formTimes && (teken.equals("p") || teken.equals("y"))))
				dx += 2;
			else if(teken.equals("\u221e"))
			{
				fs += INFINITY_BONUS;
				dy += 3;
			}			
			OMSVGTextElement t = new OMSVGTextElement(dx, dy, OMSVGLength.SVG_LENGTHTYPE_NUMBER, teken);
			t.getStyle().setFontSize(fs , Unit.PX);
			t.getStyle().setFontStyle(fm.isItalic() ? FontStyle.ITALIC: FontStyle.NORMAL);
			t.getStyle().setSVGProperty(SVGConstants.CSS_FONT_FAMILY_PROPERTY, fm.getFont());
			t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY,(selected ? "white" : color));
			t.getStyle().setFontWeight(fm.isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
			svg.appendChild(t);
		} else {
			buildChar(new SvgBuilder(svg, x, y));
		}

		//drawCursor(svg);
	}

	protected void drawCursor(OMSVGElement svg) {
		drawCursor(width, svg);
	}
	
	protected void drawCursor(int width, OMSVGElement notused) {
		if (this.isCurrent() == false || this.isSelected() || this.holder.hasSelection())
			return;
		selectedRect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "#00F");
		selectedRect.getWidth().getBaseVal().setValue(2f);
		selectedRect.getX().getBaseVal().setValue(x+width-2);
	}

//	protected void drawCursor(int x, PathBuilder pb) {
//		pb.setLineWidth(2);
//		pb.setStrokeStyle("#00f");
//		if (x - 1 < 0)
//			x += 2;
//		pb.beginPath();
//		pb.moveTo(x - 1, 2);
//		pb.lineTo(x - 1, height - 2);
//		pb.stroke();
//	}

	
}
