package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps.BordjesTouchHandler;



public class PijlVak extends LayoutPanel{

	private static String font = "14px sans-serif";
	
	private String operator;
	private int ashoogte;
	private int width;
	private int height;
	
	FormuleViewer prefixVak;
	FormuleEditor editor;
	TouchPanel editorPanel;
	
	private FormuleFont fm;
	private boolean hasPrefix = false;
	private Context2d ctx;
	
	private FormuleEditorWithSteps fe;
	
	Image goedKrulImage, foutKruisImage; //goedKrulHalfImage
	
	
	public PijlVak(String op, FormuleEditorWithSteps fe)
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
		
		this.add(canvas);
		this.setWidgetLeftRight(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		this.setWidgetTopBottom(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		width = (fm.getAscent() + fm.getDescent())/2 + fm.getAscent()/4 + (int) ctx.measureText("  "+operator+"   ").getWidth();
		height = 5*(fm.getAscent() + fm.getDescent())/2;
		if(op.equals("abc") || op.equals("sub"))
			width = 110;
		
		 
		ashoogte = 5*(fm.getAscent() + fm.getDescent())/4;
		
		if(op.equals(""))
		{	ashoogte = 15;
			width = 30;
			height = 30;
		}
		
		setPixelSize(width, height);
		//this.getElement().getStyle().setBackgroundColor("red");
		if(operator.equals("abc") || operator.equals("sub"))
		{	hasPrefix = true;
			if(operator.equals("abc"))
				prefixVak = new FormuleViewer("D=");
			if(operator.equals("sub"))
				prefixVak = new FormuleViewer("p=");
			//Panel p = prefixVak.getAsPanel();
			//p.getElement().getStyle().setProperty("display", "inline");
			//this.add(prefixVak.getAsPanel());
			//this.setWidgetLeftWidth(p, 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
			//this.setWidgetTopHeight(p, ashoogte, Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			//p.getElement().getStyle().setBackgroundColor(CssColor.make(255,255,200).toString());
			
			
		}
		//prefixVak.setEditable(false);
		//prefixVak.setLocation((fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(),(fm.getAscent() + fm.getDescent())/2);
		
		editor = addNewEditor(this);
		//formuleVak = new FormuleVak();
		//formuleVak.addActionListener(this);
		
		//TODO: Noordhoff-onderscheid maken
				//goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en().getSafeUri());
				//foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis().getSafeUri());
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.mw_kruisje_rood().getSafeUri());
	}
	
	
	public FormuleEditor addNewEditor(LayoutPanel p)
	{
		FormuleEditor editor = new FormuleEditor(){
			public void enter()
			{
				enterActie();
			}
		};
		//FormuleEditorWithAnswer editorInstance() {
		//	return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden);
		//}
		editor.setFormuleToolBijFocus(true);
		//if (!hasPrefix)
		//	editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		//editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(fm);
		editor.zetPijlVak(this);
		editorPanel = (TouchPanel) editor.getAsPanel();
		//tp.getElement().getStyle().setProperty("display", "inline-block");
		//editor.setCurrent(0, 0);
		//editor.requestFocus();
		if (hasPrefix)
		{	p.add(prefixVak.getAsPanel());
			if(operator.equals("abc") || operator.equals("sub"))
			{
				p.setWidgetLeftWidth(prefixVak.getAsPanel(), 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
				p.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte, Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			}
		}
		
		if(!operator.equals("") && !operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
		{
			p.add(editorPanel);
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),(fm.getAscent() + fm.getDescent())/2);
			p.setWidgetLeftWidth(editorPanel, (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(), Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
			p.setWidgetTopHeight(editorPanel, (fm.getAscent() + fm.getDescent())/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			if(operator.equals("abc") || operator.equals("sub"))
			{
				p.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
				p.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			}
			
		}
		
		editor.requestFocus();
		return editor;
	}
	
	
	
	public void paintComponent()
	{	ctx.setFont(font);
		
        ctx.setFillStyle("black");
        ctx.setStrokeStyle("black");
		
        if(!operator.equals("abc"))
		{	int h = ashoogte;
			ctx.beginPath();
			ctx.arc(-h/2, h, h, (int)(180*Math.PI/4),(int)(-180*Math.PI/2));
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
		
		/*
		if(operator.equals("*"))
		{	g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2);
			g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3);
		
		}
		else if(operator.equals(":"))
		{	int b=fm.getAscent() + fm.getDescent();
			g.fillRect(b/2+10,ashoogte-b/4+2,2,2);
			g.fillRect(b/2+10,ashoogte+b/4+1,2,2);
			g.drawLine(b/4+11,ashoogte+2,3*b/4+10,ashoogte+2);
		}
		else if(operator.equals("haakjes"))
		{	g.drawString(WiskOpdr.rb.getString("haakjesLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			g.drawString(WiskOpdr.rb.getString("haakjesLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("herleid"))
		{	g.drawString(WiskOpdr.rb.getString("herleidLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			g.drawString(WiskOpdr.rb.getString("herleidLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("ontbind"))
		{	g.drawString(WiskOpdr.rb.getString("ontbindLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("splits"))
		{	g.drawString(WiskOpdr.rb.getString("splitsLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("wortel"))
		{	g.drawString(WiskOpdr.rb.getString("wortelLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("gelijkwaardig"))
		{	g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("implicatie"))
		{	//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		*/
		else if(operator.equals("abc"))
		{	ctx.setFillStyle("white");
			ctx.fillRect(0, 0, getWidth(), getHeight());
			ctx.setStrokeStyle(CssColor.make(246,127,142).toString());
			ctx.setFillStyle("black");
			ctx.beginPath();
			ctx.rect(0, 0, getWidth(), getHeight());
			ctx.stroke();
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
		
		/*
		else if(operator.equals("sub"))
		{	g.setColor(new Color(255,255,200));
			if("GR".equals(WiskOpdr.deployVariant))g.setColor(new Color(255,255,255));
			g.fillRect(0,0,getSize().width,getSize().height);
			g.setColor(Color.black);
			if("GR".equals(WiskOpdr.deployVariant))g.setColor(new Color(246,127,142));
			g.drawRect(0,0,getSize().width-1,getSize().height-1);
			g.setColor(Color.black);
			g.drawString(WiskOpdr.rb.getString("subLabel"),5 ,ashoogte-fm.getDescent());
			//g.drawString("p = ",15 ,ashoogte+fm.getAscent());
		}
		else
		{	g.drawString("  "+operator+" ",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent()/2);
		}
		*/
		
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
	
	public void zetMaat()
	{
		int b = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+"   ").getWidth() + editor.getWidth();
		if(operator.equals("abc") || operator.equals("sub")) 
			b = Math.max(110, getWidth());
		width = b;
		setPixelSize(width, height);
		
		this.setWidgetLeftWidth(editorPanel, (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+"   ").getWidth(), Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
		this.setWidgetTopHeight(editorPanel, ashoogte-editor.getAsHoogte()-fm.getDescent()/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
		//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
		//formuleVakHaakjeR.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" ") + formuleVakHaakjeL.getSize().width + formuleVak.getSize().width,ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
		if(operator.equals("abc") || operator.equals("sub"))
		{	
			this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, editor.getWidth(), Style.Unit.PX);
			this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
		}
	}
	
	public void enterActie()
	{
		if(goedKrulImage.isAttached())
			remove(goedKrulImage);
		if(foutKruisImage.isAttached())
			remove(foutKruisImage);
		if(operator.equals("abc"))
		{	String vergelijkingString = fe.getLatestAnswer();
			VergelijkingMeerv vergelijking = FormuleParser.parseVergelijking("$f" + vergelijkingString + "@");
			
			double d = Algebra.geefDiscriminant(vergelijking.geefVergelijking(0));
			String antwoordString = editor.toString();
			Expressie antwoordExpressie = FormuleParser.parse(antwoordString);
			double dAnt = antwoordExpressie.geefWaarde();
			
			boolean goed = Algebra.isGelijkDouble(d, dAnt);
			//goedIC.setVisible(goed);
			//foutIC.setVisible(!goed);
			if(goed)
			{
				this.add(goedKrulImage);
				setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
				setWidgetBottomHeight(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
			}
			else
			{
				this.add(foutKruisImage);
				setWidgetRightWidth(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
				setWidgetBottomHeight(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
			}
			if(goed)
			{
				fe.zetEditorTerug();
			}
		}
	}
}
