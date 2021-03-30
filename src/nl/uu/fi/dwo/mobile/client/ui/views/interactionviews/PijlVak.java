package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;



public class PijlVak extends LayoutPanel
{
	/**
	 * Marge voor operator string.
	 */
	private static final String MARGE_VOOR = "  ";
	/**
	 * Marge na operator string.
	 */
	private static final String MARGE_NA = " ";
	/**
	 * Minimum breedte van het vak waarin de discriminant of 
	 * een substitutie kan worden ingevuld.
	 */
	private static final int MIN_WIDTH_ABC_SUB = 110;

	public static TextConstants rb = Text.constants;
	
	private static String font = "14px sans-serif";
	
	private String operator;
	private int ashoogte;
	private int width;
	private int height;
	
	FormuleViewer prefixVak;
	FormuleEditor editor;
	FormuleViewer viewer;
	Panel editorPanel;
	
	private FormuleFont fm;
	private boolean hasPrefix = false;
	private Context2d ctx;
	private OMSVGSVGElement svg;
	private SVGImage svgImage;
	private OMSVGDocument doc;

	/**
	 * Het discriminantvak (abc-formule) is te sluiten met een sluitknop met kruis.
	 */
	private SVGButton sluitKnop;
	
	private FormuleEditorWithSteps fe;
	
	Image goedKrulImage, foutKruisImage; //goedKrulHalfImage
	boolean aanpasbaar = true;
	
	
	public PijlVak(String op, FormuleEditorWithSteps fe, boolean setState)
	{
		super();
		this.fe = fe;
		
		operator = op;
		
		String font = XMLView.getDefaultFont();
		int fontSize = XMLView.getDefaultFontSize();
		
		fm = FormuleFont.createFromFontSize(fontSize);
		
		Canvas canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		ctx.setFont(font);
		
		//this.add(canvas);
		//this.setWidgetLeftRight(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		//this.setWidgetTopBottom(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		doc = OMSVGParser.currentDocument();
        svg =  doc.createSVGSVGElement();
        svgImage = new SVGImage(svg);
        this.add(svgImage);
 		this.setWidgetLeftRight(svgImage, 0, Style.Unit.PX, 0, Style.Unit.PX);
 		this.setWidgetTopBottom(svgImage, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		width = (fm.getAscent() + fm.getDescent())/2 + fm.getAscent()/4 + (int) ctx.measureText(MARGE_VOOR + operator + "   ").getWidth();
		height = 40;//5*(fm.getAscent() + fm.getDescent())/2;
		
		if (operator.equals("abc")) 
		{
			width = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + "Discriminant" + MARGE_NA).getWidth());
			
			sluitKnop = new FEWSButton("sluit", fe.isNoordhoff());
			sluitKnop.setSize(18, 18);
			sluitKnop.addButtonListener(new ButtonListener() {
				@Override
				public void onClick(Object sender)
				{
					fe.backStep(false);
					
					// openstaande pijl afsluiten
					fe.setOpenstaandePijl(false);
				}
				
			});
			this.add(sluitKnop);
			this.setWidgetRightWidth(sluitKnop, 0, Style.Unit.PX, 18, Style.Unit.PX);
			this.setWidgetTopHeight(sluitKnop, 0, Style.Unit.PX, 18, Style.Unit.PX);
		}
		else if (operator.equals("sub")) 
		{
			width = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + rb.subLabel() + MARGE_NA).getWidth());
			
//			sluitKnop = new FEWSButton("sluit");
//			sluitKnop.setSize(18, 18);
//			sluitKnop.addButtonListener(new ButtonListener() {
//				@Override
//				public void onClick(Object sender)
//				{
//					fe.backStep(false);
//					
//					// openstaande pijl afsluiten
//					fe.setOpenstaandePijl(false);
//				}
//				
//			});
//			this.add(sluitKnop);
//			this.setWidgetRightWidth(sluitKnop, 0, Style.Unit.PX, 18, Style.Unit.PX);
//			this.setWidgetTopHeight(sluitKnop, 0, Style.Unit.PX, 18, Style.Unit.PX);
			
		}

		
		if (op.equals(""))
		{	//ashoogte = 15;
			width = 30;
			//height = 30;
		}
		
		ashoogte = 5*(fm.getAscent() + fm.getDescent())/4; // why?
		
		setPixelSize(width, height);
		//this.getElement().getStyle().setBackgroundColor("red");
		if (operator.equals("abc") || operator.equals("sub"))
		{
			hasPrefix = true;
			if (operator.equals("abc"))
			{
				prefixVak = new FormuleViewer("D=");
				prefixVak.setFont(fm);
			}
			
			if (operator.equals("sub"))
			{	
				if (this.fe.isVergelijkingVak)
					prefixVak = new FormuleViewer("p="); // dit is in de java-versie het geval
				else
					prefixVak = new FormuleViewer("u=");
				
				prefixVak.setFont(fm);
			}
			//Panel p = prefixVak.getAsPanel();
			//p.getElement().getStyle().setProperty("display", "inline");
			//this.add(prefixVak.getAsPanel());
			//this.setWidgetLeftWidth(p, 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
			//this.setWidgetTopHeight(p, ashoogte, Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			//p.getElement().getStyle().setBackgroundColor(CssColor.make(255,255,200).toString());
		}
		//prefixVak.setEditable(false);
		//prefixVak.setLocation((fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(),(fm.getAscent() + fm.getDescent())/2);
		//if(setState)
		//	addNewViewer("");
		if(!setState)
			editor = addNewEditor(this);
		//formuleVak = new FormuleVak();
		//formuleVak.addActionListener(this);
		
		//TODO: Noordhoff-onderscheid maken
				//goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en().getSafeUri());
				//foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis().getSafeUri());
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.mw_kruisje_rood().getSafeUri());
	}
	
	
	public PijlVakFormuleEditor addNewEditor(LayoutPanel p)
	{
		PijlVakFormuleEditor editor = new PijlVakFormuleEditor(this) {

          @Override
          public Panel getAsPanel() {
            return editorPanel;
          }
		  
		};
		//editor.setFormuleToolBijFocus(true);
		editor.setFont(fm);
// FIXED editorPanel === editor.getAsPanel()
		editorPanel = editor.createPanel();
		editorPanel.add(editor.getMainRegel().asWidget());
		editor.register((new FormuleEditorTouchHandler(editor)).initHandler());

		if (hasPrefix)
		{
			p.add(prefixVak.getAsPanel());
			if (operator.equals("abc") || operator.equals("sub"))
			{
				p.setWidgetLeftWidth(prefixVak.getAsPanel(), 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
				p.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte, Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			}
		}
		
		if (!operator.equals("") && !operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
		{
			p.add(editorPanel);
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),(fm.getAscent() + fm.getDescent())/2);
			double left = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR + operator + MARGE_NA).getWidth();
			p.setWidgetLeftWidth(editorPanel, left, Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
			p.setWidgetTopHeight(editorPanel, (fm.getAscent() + fm.getDescent())/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			if (operator.equals("abc") || operator.equals("sub"))
			{
				p.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
				//p.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
				p.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			}
			
		}
		
		//editor.requestFocus();
		return editor;
	}
	
	public void addNewViewer(String text)
	{
		viewer = new FormuleViewer(text);
		viewer.setFont(fm);
		
		if (editorPanel != null)
			editorPanel.clear();
		editorPanel = viewer.getAsPanel();
		//editorPanel.getElement().getStyle().setBackgroundColor("blue");
		//tp.getElement().getStyle().setProperty("display", "inline-block");
		//editor.setCurrent(0, 0);
		//editor.requestFocus();
		if (hasPrefix)
		{
			prefixVak.getAsPanel().removeFromParent();
			this.add(prefixVak.getAsPanel());
			if (operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(prefixVak.getAsPanel(), 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte + viewer.getAsHoogte() - prefixVak.getAsHoogte(), Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			}
		}
		
		if (!operator.equals("") && !operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
		{
			this.add(editorPanel);
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),(fm.getAscent() + fm.getDescent())/2);
			double left = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR+operator+MARGE_NA).getWidth();
			this.setWidgetLeftWidth(editorPanel, left, Style.Unit.PX, viewer.getWidth(), Style.Unit.PX);
			//this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-viewer.getMainRegel().getAsHoogte()-fm.getDescent()/2, Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			
			if (operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, viewer.getWidth(), Style.Unit.PX);
				//p.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
				this.setWidgetTopHeight(editorPanel, ashoogte + viewer.getMainRegel().getAsHoogte() - prefixVak.getAsHoogte(), Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			}
			
			if (operator.equals("abc"))
			{
				this.remove(this.getSluitKnop());
			}
		}
		
		zetMaat();
	}
	
	/**
	 * Voeg een editor toe.
	 */
	public void addEditor()
	{
		addNewEditor(this);
	}
	
	public void setPijlVisible(boolean b)
	{
		if (!b && (operator.equals("implicatie") || operator.equals("")) && geefExpressieString().length() < 1)
			setVisible(false);
		else
			setVisible(true);
	}
	
	public void paintComponent()
	{
		 // Create an arrow-shaped path
        OMSVGPathElement pijl = doc.createSVGPathElement();
        OMSVGPathSegList segsPijl = pijl.getPathSegList();
        segsPijl.appendItem(pijl.createSVGPathSegMovetoAbs(1f, 5f));
        segsPijl.appendItem(pijl.createSVGPathSegCurvetoQuadraticAbs(1f, height-7, 16, height/2));
        pijl.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
        pijl.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.5");
        pijl.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_WHITE_VALUE);
        
        svg.appendChild(pijl);
        
        OMSVGPathElement punt = doc.createSVGPathElement();
        OMSVGPathSegList segsPunt = punt.getPathSegList();
        segsPunt.appendItem(punt.createSVGPathSegMovetoAbs(1f, height-7));
        segsPunt.appendItem(punt.createSVGPathSegLinetoAbs(2f, height-13));
        segsPunt.appendItem(punt.createSVGPathSegLinetoAbs(6f, height-8));
        segsPunt.appendItem(punt.createSVGPathSegLinetoAbs(1f, height-7));
        segsPunt.appendItem(punt.createSVGPathSegClosePath());
        punt.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
        punt.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
        
        svg.appendChild(punt);
        
    		
        if (operator.equals("*"))
		{
        		OMSVGPathElement maal = doc.createSVGPathElement();
            OMSVGPathSegList segsMaal = maal.getPathSegList();
            segsMaal.appendItem(maal.createSVGPathSegMovetoAbs((fm.getAscent() + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3));
            segsMaal.appendItem(maal.createSVGPathSegLinetoAbs((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2));
            segsMaal.appendItem(maal.createSVGPathSegMovetoAbs((fm.getAscent() + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2));
            segsMaal.appendItem(maal.createSVGPathSegLinetoAbs((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3));
            maal.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
            maal.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.0");
            svg.appendChild(maal);
		}
        else if (operator.equals(":"))
		{
        		int b = fm.getAscent() + fm.getDescent();
			double marge = ctx.measureText(MARGE_VOOR).getWidth();
			OMSVGCircleElement deel1 = doc.createSVGCircleElement((float)b / 2 + 9, (float)ashoogte - b / 4 + 2, 1f);
			OMSVGCircleElement deel2 = doc.createSVGCircleElement((float)b / 2 + 9, (float)ashoogte + b / 4 + 2, 1f);
			OMSVGLineElement deel3 = doc.createSVGLineElement((float)b/2+5, (float)ashoogte + 2, (float)(7*fm.getAscent()/4 + fm.getDescent())/2+8, (float)ashoogte + 2);
			deel3.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
			deel3.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.3");
			svg.appendChild(deel1);
			svg.appendChild(deel2);
			svg.appendChild(deel3);
		}
        else if (operator.equals("-"))
		{
        		int b = fm.getAscent() + fm.getDescent();
			double marge = ctx.measureText(MARGE_VOOR).getWidth();
			OMSVGLineElement min = doc.createSVGLineElement((float)(fm.getAscent() + fm.getDescent())/2+6, (float)ashoogte + 2, (7*fm.getAscent()/4 + fm.getDescent())/2+6, (float)ashoogte + 2);
			min.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
			min.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "1.3");
			svg.appendChild(min);
		}
        else if (operator.equals("abc"))
		{
        		OMSVGRectElement rect = doc.createSVGRectElement(1f, 1f, getWidth()-2, getHeight()-2, 2f, 2f);
        		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, ""+CssColor.make(239,241,243).toString());
        		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, ""+CssColor.make(211,229,244).toString());
        		svg.appendChild(rect);
        		
        		OMSVGTextElement text = doc.createSVGTextElement(5f, (float)ashoogte - fm.getDescent(), OMSVGLength.SVG_LENGTHTYPE_PX, "Discriminant");
            text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "14");
            text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
            //text.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, ""+CssColor.make(38,115,182).toString());
            svg.appendChild(text);
		}
        else if (operator.equals("sub"))
		{
	        	OMSVGRectElement rect = doc.createSVGRectElement(1f, 1f, getWidth()-2, getHeight()-2, 2f, 2f);
	    		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, ""+CssColor.make(239,241,243).toString());
	    		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, ""+CssColor.make(211,229,244).toString());
	    		svg.appendChild(rect);
	    		
	    		OMSVGTextElement text = doc.createSVGTextElement(5f, (float)ashoogte - fm.getDescent(), OMSVGLength.SVG_LENGTHTYPE_PX, rb.subLabel());
	        text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "14");
	        text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
	        //text.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, ""+CssColor.make(38,115,182).toString());
	        svg.appendChild(text);
		}
        else
        {	String txt = "";
        		if(operator.equals("herleid"))
        			txt = rb.herleidLabel0();
        		else if(operator.equals("haakjes"))
        			txt = rb.haakjesLabel0();
        		else if(operator.equals("splits"))
        			txt = rb.splitsLabel0();
        		else if(operator.equals("ontbind"))
        			txt = rb.ontbindLabel0();
        		else if(operator.equals("wortel"))
        			txt = rb.wortelLabel0();
        		else if(operator.equals("gelijkwaardig"))
        			txt = rb.gelijkwaardigLabel0();
        		else
        			txt = operator;
        	 	OMSVGTextElement text = doc.createSVGTextElement((float)(fm.getAscent() + fm.getDescent())/2+6, (float)ashoogte + fm.getAscent() / 4+3, OMSVGLength.SVG_LENGTHTYPE_PX, MARGE_VOOR + txt + MARGE_NA);
            text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "14");
            text.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
            svg.appendChild(text);
        }
        
		
		ctx.setFont(this.fm.toString());
		
        ctx.setFillStyle("black");
        ctx.setStrokeStyle("black");
		
        if (!(operator.equals("abc") || operator.equals("sub")))
		{
        	// teken de pijl
        	
        	int h = ashoogte;
			//int h = height/2;
			ctx.beginPath();
			ctx.arc(-h / 2, h, h, (int) (180 * Math.PI / 4), (int) (-180 * Math.PI / 2));
			ctx.stroke();
		
	        ctx.beginPath();
	        ctx.moveTo(0, 2 * h - 2);
	        ctx.lineTo(2, 2 * h - 10);
	        ctx.lineTo(7, 2 * h - 2);
	        ctx.closePath();
	        ctx.fill();
	        ctx.stroke();
		}
		//g.drawLine(0,getSize().height-4,2,getSize().height-12);
		//g.drawLine(0,getSize().height-4,7,getSize().height-4);
		//g.setColor(Color.black);
		
		if (operator.equals("*"))
		{
			ctx.beginPath();
			ctx.moveTo((fm.getAscent() + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3);
			ctx.lineTo((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2);
			ctx.moveTo((fm.getAscent() + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2);
			ctx.lineTo((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3);
			ctx.stroke();
			
			//g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2);
			//g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3);
		
		}
		else if (operator.equals(":"))
		{
			int b = fm.getAscent() + fm.getDescent();
			double marge = ctx.measureText(MARGE_VOOR).getWidth();
			ctx.fillRect(marge + b / 2 + 6, ashoogte - b / 4 + 2, 2, 2);
			ctx.fillRect(marge + b / 2 + 6, ashoogte + b / 4 + 1, 2, 2);
			ctx.beginPath();
			ctx.moveTo(marge + b / 4 + 8, ashoogte + 2);
			ctx.lineTo(marge + 3 * b / 4 + 7, ashoogte + 2);
			ctx.stroke();
		
//			g.fillRect(b/2+10,ashoogte-b/4+2,2,2);
//			g.fillRect(b/2+10,ashoogte+b/4+1,2,2);
//			g.drawLine(b/4+11,ashoogte+2,3*b/4+10,ashoogte+2);
		}
		else if (operator.equals("haakjes"))
		{
			ctx.fillText(rb.haakjesLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			ctx.fillText(rb.haakjesLabel1(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			//g.drawString(WiskOpdr.rb.getString("haakjesLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("haakjesLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if (operator.equals("herleid"))
		{
			ctx.fillText(rb.herleidLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText(rb.herleidLabel1(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
			//g.drawString(WiskOpdr.rb.getString("herleidLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString(WiskOpdr.rb.getString("herleidLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if (operator.equals("ontbind"))
		{
			ctx.fillText(rb.ontbindLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("ontbindLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if (operator.equals("splits"))
		{
			ctx.fillText(rb.splitsLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("splitsLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if (operator.equals("wortel"))
		{
			ctx.fillText(rb.wortelLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("wortelLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if (operator.equals("gelijkwaardig"))
		{
			ctx.fillText(rb.gelijkwaardigLabel0(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			ctx.fillText(rb.gelijkwaardigLabel1(), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if (operator.equals("implicatie"))
		{
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		
		else if (operator.equals("abc"))
		{
			ctx.setFillStyle(CssColor.make(255, 255, 200));
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setFillStyle("white");
			ctx.fillRect(0, 0, getWidth(), getHeight());
			ctx.setGlobalAlpha(1);
			ctx.setStrokeStyle("black");
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setStrokeStyle(CssColor.make(246,127,142).toString()); // rood randje
			ctx.beginPath();
			ctx.rect(0, 0, getWidth(), getHeight());
			ctx.stroke();
			ctx.setFillStyle("black");
			ctx.fillText("Discriminant", 5, ashoogte - fm.getDescent());
//			g.setColor(new Color(255,255,200));
//			if("GR".equals(WiskOpdr.deployVariant))g.setColor(new Color(255,255,255));
//			g.fillRect(0,0,getSize().width,getSize().height);
//			g.setColor(Color.black);
//			if("GR".equals(WiskOpdr.deployVariant))g.setColor(new Color(246,127,142));
//			g.drawRect(0,0,getSize().width-1,getSize().height-1);
//			g.setColor(Color.black);
//			g.drawString("Discriminant",5 ,ashoogte-fm.getDescent());
			//g.drawString("D = ",15 ,ashoogte+fm.getAscent());
		}
		
		else if (operator.equals("sub"))
		{
			ctx.setFillStyle(CssColor.make(255, 255, 200));
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setFillStyle("white");
			ctx.fillRect(0, 0, getWidth(), getHeight());
			ctx.setGlobalAlpha(1);
			ctx.setStrokeStyle("black");
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setStrokeStyle(CssColor.make(246,127,142).toString());
			ctx.beginPath();
			ctx.rect(0, 0, getWidth(), getHeight());
			ctx.stroke();
			
			ctx.setFillStyle("black");
			ctx.fillText(rb.subLabel(), 5, ashoogte - fm.getDescent());
		}
		else
		{	
			ctx.setFillStyle("black");
			ctx.fillText(MARGE_VOOR + operator + MARGE_NA, 
				(fm.getAscent() + fm.getDescent()) / 2,
				ashoogte + fm.getAscent() / 2);
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public static void setFont(String fontString)
	{
		font = fontString;
	}
	
	public FormuleEditor getEditor()
	{
		return editor;
	}
	
	public String geefOperator()
	{	return operator;
	}
	
	public String geefExpressieString()
	{	//Expressie e = formuleVak.geefExpressie();
		//if(e==null)return null;
		//String s = e.toStringStrikt();
		if(editor != null)
			return editor.toString();
		else if (viewer != null)
			return viewer.toString();
		else
			return "";
	}
	
	public void zetExpressie(String s)
	{
		if (editor != null) {
			editor.insert(s);
		}
		else
			addNewViewer(s);
	//vervangEditorDoorViewer();
		
		//formuleVak.vulVak("$f" + s + "@");
	}
	
	public void zetOperator(String s)
	{
		this.operator = s;
	}

	public void zetMaat()
	{
		int b = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR + operator + "   ").getWidth();
		if (editor != null)
			b += editor.getWidth();
		else
			b += viewer.getWidth();

		if (operator.equals("abc")) 
		{
			b = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + "Discriminant" + MARGE_NA).getWidth());
		}
		else if (operator.equals("sub")) 
		{
			b = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + rb.subLabel() + MARGE_NA).getWidth());
		}

		width = b;
		
		if (operator.equals("abc") || operator.equals("sub"))
		{
			//hier nog zorgen voor juiste hoogte bij invullen 'hoge' formules.
			int h = height;
			if(editor != null)
				h = ashoogte + editor.getHeight() + 5;
			else if(viewer != null)
				h = ashoogte + viewer.getHeight() + 5;
			h = Math.max(ashoogte + prefixVak.getHeight() + 5, h);
				
			height = h;
		}
		setPixelSize(width, height);
		if (editorPanel.getParent() == this) // FIXME Anders een assertion error bij setWidgetLeftWith
		{
			double left = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR + operator + MARGE_NA).getWidth();
			double leftDelen = (fm.getAscent() + fm.getDescent())/2 - 1;
			if (":".equals(operator))
			{
				left = left + leftDelen;
			}
//			System.out.println("PijlVak.zetMaat(): ctx.getFont() = " + ctx.getFont() + ", left editorpanel = " + left + ", ascent = " + fm.getAscent() + ", descent = " + fm.getDescent() + ", measureText = " + (int) ctx.measureText(MARGE_VOOR + operator + MARGE_NA).getWidth());
			this.setWidgetLeftWidth(editorPanel, left, Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
			//hieronder: niet editor.getCurrentRegel().getAsHoogte() nodig??
		    this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte())-fm.getDescent()/2, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);
		
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
			//formuleVakHaakjeR.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" ") + formuleVakHaakjeL.getSize().width + formuleVak.getSize().width,ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
			if (operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);
				this.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte + ((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte()) - prefixVak.getAsHoogte(), Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);	
			}
		}
		
		fe.zetPijlVakMaat(this);
		paintComponent();
	}
	
	public void vervangEditorDoorViewer()
	{
		String text = editor.toString();
		editor = null;
		addNewViewer(text);
		//if(editorPanel.isAttached())
		//	this.remove(editorPanel);
		//viewer.insert("$f" + text + "@");
		//editorPanel = (TouchPanel) viewer.getAsPanel();
		//viewer.setFont(fm);
		//Panel viewerPanel = viewer.getAsPanel();
		//this.add(viewerPanel);
		//this.setWidgetLeftWidth(editorPanel, (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(), Style.Unit.PX, viewer.getWidth(), Style.Unit.PX);
		//this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-viewer.getAsHoogte()-fm.getDescent()/2, Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
		
	}
	
	public class PijlVakFormuleEditor extends FormuleEditor {

		PijlVak pijlvak;
		public PijlVakFormuleEditor(PijlVak pv)
		{
			super();
			pijlvak = pv;
		}
		
		@Override
		public void enter()
		{
			if (!aanpasbaar)
				return;
			aanpasbaar = false;
			if (goedKrulImage.isAttached())
				remove(goedKrulImage);
			if (foutKruisImage.isAttached())
				remove(foutKruisImage);
			
			if (operator.equals("abc"))
			{
				String vergelijkingString = fe.getLatestAnswer();
				VergelijkingMeerv vergelijking = FormuleParser.parseVergelijking("$f" + vergelijkingString + "@");
				
				double d = Algebra.geefDiscriminant(vergelijking.geefVergelijking(0));
				String antwoordString = editor.toString();
				Expressie antwoordExpressie = FormuleParser.geefExpressie("$f" + antwoordString + "@");
						//FormuleParser.parse(antwoordString);//is parse hier de juiste? Niet beter: geefExpressie? (met $f en @)
				boolean goed = false;
				try{
					double dAnt = antwoordExpressie.geefWaarde();
					goed = Algebra.isGelijkDouble(d, dAnt);
				}
				catch(Exception e)
				{}
				
				if (goed)
				{
					pijlvak.add(goedKrulImage);
					setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					setWidgetBottomHeight(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					pijlvak.remove(pijlvak.getSluitKnop());
					fe.zetEditorTerug();
				}
				else
				{
					aanpasbaar = true;
					pijlvak.add(foutKruisImage);
					setWidgetRightWidth(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					setWidgetBottomHeight(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
				}
			}
			else if (operator.equals("sub"))
			{
				String substitutieString = editor.toString();
				Expressie substitutie = FormuleParser.geefExpressie("$f" + substitutieString + "@");
				fe.zetSubstitutie(substitutie);
				fe.zetEditorTerug();
			}
			else
			{
				if (!operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
				{
					if (editor.toString().equals("")) 
					{
						aanpasbaar = true;
						return;
					}
				}
				fe.maakBewerkingStap();
				Expressie exp = FormuleParser.parse(editor.toString());
		 		if (exp!=null && Algebra.geefTermen(exp, new Vector()).size()>1)
		 		{
		 			editor.clearAll();
		 			editor.insert("$h" + exp.toString() + "@");
		 		}
			}
			
			// openstaande pijl afsluiten
			fe.setOpenstaandePijl(false);
			// administratie bijwerken
			fe.setPijlVakInhouden(fe.getPijlVakOperatorenArray().size() - 1, editor.toString());
			
			if (!aanpasbaar)
				vervangEditorDoorViewer();
		}
		
		public void resize()
		{
			pijlvak.zetMaat();
		}
		
	}

	/**
	 * Zet het font van het pijlvak (operatie) met de bijbehorende
	 * editor en resize de benodigde vakken.
	 * 
	 * @param font
	 */
	public void setFont(FormuleFont font)
	{
		// het font van de operatie in het pijlvak
		this.fm = font;
		// zet context font
		ctx.setFont(font.toString());
		// zet ashoogte
		ashoogte = 5 * (fm.getAscent() + fm.getDescent())/4;
		
		// en pas hoogte en breedte aan
		this.height = 5*(fm.getAscent() + fm.getDescent())/2;
		
		int b = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR + operator + "   ").getWidth(); // waarom ruimere marge na?
		if (editor != null)
			b += editor.getWidth();
		else if (viewer != null)
			b += viewer.getWidth();
		
		if (operator.equals("abc")) 
		{
			b = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + "Discriminant" + MARGE_NA).getWidth());
		}
		else if (operator.equals("sub")) 
		{
			b = Math.max(MIN_WIDTH_ABC_SUB, (int) ctx.measureText(MARGE_VOOR + rb.subLabel() + MARGE_NA).getWidth());
		}
		
		width = b;
		
		setPixelSize(width, height);
		
		// zet font van prefixvak
		if (operator.equals("abc") || operator.equals("sub"))
		{
			prefixVak.setFont(fm);
			prefixVak.setDefaultFont(fm);
		}
		
		// het font van de editor
		if (editor != null)
		{
			this.editor.setFont(font);
			this.editor.setDefaultFont(font);
		}
		
		// positioneer de editor
		// uit zetMaat():
		if (editorPanel != null && editorPanel.getParent() == this)
		{
			double left = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText(MARGE_VOOR + operator + MARGE_NA).getWidth();
			double leftDelen = (fm.getAscent() + fm.getDescent())/2 - 1;
			if (":".equals(operator))
			{
				left = left + leftDelen;
			}

			this.setWidgetLeftWidth(editorPanel, left, Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
		    this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte())-fm.getDescent()/2, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);

			if (operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);
				this.setWidgetLeftWidth(prefixVak.getAsPanel(), 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte + ((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte()) - prefixVak.getAsHoogte(), Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			}
		}
	}
	
	SVGButton getSluitKnop()
	{
		return sluitKnop;
	}
}
