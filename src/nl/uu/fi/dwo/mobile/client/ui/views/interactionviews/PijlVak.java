package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Vector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.graphtoolgwt.client.FormuleComponentGWT;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text_nl;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps.BordjesTouchHandler;



public class PijlVak extends LayoutPanel{

	public static Text_nl rb = new Text_nl();
	private static String font = "14px sans-serif";
	
	private String operator;
	private int ashoogte;
	private int width;
	private int height;
	
	FormuleViewer prefixVak;
	FormuleEditor editor;
	FormuleViewer viewer;
	TouchPanel editorPanel;
	
	private FormuleFont fm;
	private boolean hasPrefix = false;
	private Context2d ctx;
	
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
		
		this.add(canvas);
		this.setWidgetLeftRight(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		this.setWidgetTopBottom(canvas, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
		width = (fm.getAscent() + fm.getDescent())/2 + fm.getAscent()/4 + (int) ctx.measureText("  "+operator+"   ").getWidth();
		height = 5*(fm.getAscent() + fm.getDescent())/2;
		if(op.equals("abc") || op.equals("sub"))
			width = 110;
		
		 
		if(op.equals(""))
		{	//ashoogte = 15;
			width = 30;
			//height = 30;
		}
		ashoogte = 5*(fm.getAscent() + fm.getDescent())/4;
		
		
		
		setPixelSize(width, height);
		//this.getElement().getStyle().setBackgroundColor("red");
		if(operator.equals("abc") || operator.equals("sub"))
		{	hasPrefix = true;
			if(operator.equals("abc"))
			{	prefixVak = new FormuleViewer("D=");
				prefixVak.setFont(fm);
			}
			if(operator.equals("sub"))
			{	prefixVak = new FormuleViewer("p=");
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
		PijlVakFormuleEditor editor = new PijlVakFormuleEditor(this);
		//FormuleEditorWithAnswer editorInstance() {
		//	return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden);
		//}
		editor.setFormuleToolBijFocus(true);
		//if (!hasPrefix)
		//	editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		//editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(fm);
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
		if(editorPanel != null)
			editorPanel.clear();
		editorPanel = (TouchPanel) viewer.getAsPanel();
		//editorPanel.getElement().getStyle().setBackgroundColor("blue");
		//tp.getElement().getStyle().setProperty("display", "inline-block");
		//editor.setCurrent(0, 0);
		//editor.requestFocus();
		if (hasPrefix)
		{	prefixVak.getAsPanel().removeFromParent();
			this.add(prefixVak.getAsPanel());
			if(operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(prefixVak.getAsPanel(), 10, Style.Unit.PX, prefixVak.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte + viewer.getAsHoogte() - prefixVak.getAsHoogte(), Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
			}
		}
		
		if(!operator.equals("") && !operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
		{
			this.add(editorPanel);
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),(fm.getAscent() + fm.getDescent())/2);
			this.setWidgetLeftWidth(editorPanel, (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(), Style.Unit.PX, viewer.getWidth(), Style.Unit.PX);
			//this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-viewer.getMainRegel().getAsHoogte()-fm.getDescent()/2, Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			
			
			if(operator.equals("abc") || operator.equals("sub"))
			{
				this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, viewer.getWidth(), Style.Unit.PX);
				//p.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
				this.setWidgetTopHeight(editorPanel, ashoogte + viewer.getMainRegel().getAsHoogte() - prefixVak.getAsHoogte(), Style.Unit.PX, viewer.getHeight(), Style.Unit.PX);
			}
			
		}
		zetMaat();
		
	}
	
	public void setPijlVisible(boolean b)
	{
		if(!b && (operator.equals("implicatie") || operator.equals("")) && geefExpressieString().length() < 1)
			setVisible(false);
		else
			setVisible(true);
	}
	
	public void paintComponent()
	{	//ctx.setFont(font);
		ctx.setFont(XMLView.getDefaultFont());
		
        ctx.setFillStyle("black");
        ctx.setStrokeStyle("black");
		
        if(!(operator.equals("abc") || operator.equals("sub")))
		{	int h = ashoogte;
			//int h = height/2;
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
		
		if(operator.equals("*"))
		{	ctx.beginPath();
			ctx.moveTo((fm.getAscent() + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3);
			ctx.lineTo((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2);
			ctx.moveTo((fm.getAscent() + fm.getDescent())/2+6,ashoogte+fm.getAscent()/4+2);
			ctx.lineTo((7*fm.getAscent()/4 + fm.getDescent())/2+6,ashoogte-fm.getAscent()/4+3);
			ctx.stroke();
			
			//g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2);
			//g.drawLine((fm.getAscent() + fm.getDescent())/2+10,ashoogte+fm.getAscent()/4+2,(7*fm.getAscent()/4 + fm.getDescent())/2+10,ashoogte-fm.getAscent()/4+3);
		
		}
		else if(operator.equals(":"))
		{	int b=fm.getAscent() + fm.getDescent();
			ctx.fillRect(b/2+6,ashoogte-b/4+2,2,2);
			ctx.fillRect(b/2+6,ashoogte+b/4+1,2,2);	
			ctx.beginPath();
			ctx.moveTo(b/4+8, ashoogte+2);
			ctx.lineTo(3*b/4+7, ashoogte+2);
			ctx.stroke();
		
//			g.fillRect(b/2+10,ashoogte-b/4+2,2,2);
//			g.fillRect(b/2+10,ashoogte+b/4+1,2,2);
//			g.drawLine(b/4+11,ashoogte+2,3*b/4+10,ashoogte+2);
		}
		else if(operator.equals("haakjes"))
		{	ctx.fillText(rb.getString("haakjesLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			ctx.fillText(rb.getString("haakjesLabel1"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			//g.drawString(WiskOpdr.rb.getString("haakjesLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("haakjesLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("herleid"))
		{	ctx.fillText(rb.getString("herleidLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText(rb.getString("herleidLabel1"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
			//g.drawString(WiskOpdr.rb.getString("herleidLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString(WiskOpdr.rb.getString("herleidLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("ontbind"))
		{	ctx.fillText(rb.getString("ontbindLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("ontbindLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if(operator.equals("splits"))
		{	ctx.fillText(rb.getString("splitsLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("splitsLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if(operator.equals("wortel"))
		{	ctx.fillText(rb.getString("wortelLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			ctx.fillText("", (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
			
			//g.drawString(WiskOpdr.rb.getString("wortelLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent()+10);
			//g.drawString("",(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		}
		else if(operator.equals("gelijkwaardig"))
		{	ctx.fillText(rb.getString("gelijkwaardigLabel0"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			ctx.fillText(rb.getString("gelijkwaardigLabel1"), (fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		else if(operator.equals("implicatie"))
		{	//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel0"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte-fm.getDescent());
			//g.drawString(WiskOpdr.rb.getString("gelijkwaardigLabel1"),(fm.getAscent() + fm.getDescent())/2 ,ashoogte+fm.getAscent());
		
		}
		
		else if(operator.equals("abc"))
		{	ctx.setFillStyle(CssColor.make(255, 255, 200));
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setFillStyle("white");
			ctx.fillRect(0, 0, getWidth(), getHeight());
			ctx.setStrokeStyle("black");
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setStrokeStyle(CssColor.make(246,127,142).toString());
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
		
		else if(operator.equals("sub"))
		{	ctx.setFillStyle(CssColor.make(255, 255, 200));
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setFillStyle("white");
			ctx.fillRect(0, 0, getWidth(), getHeight());
			ctx.setStrokeStyle("black");
			//volgende regel eigenlijk alleen voor Noordhoff
			ctx.setStrokeStyle(CssColor.make(246,127,142).toString());
			ctx.beginPath();
			ctx.rect(0, 0, getWidth(), getHeight());
			ctx.stroke();
			
			ctx.setFillStyle("black");
			ctx.fillText(rb.getString("subLabel"), 5, ashoogte - fm.getDescent());
		}
		else
		{	
			ctx.setFillStyle("black");
			ctx.fillText("  "+operator+" ", (fm.getAscent() + fm.getDescent())/2, ashoogte+fm.getAscent()/2);
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
	{	if(editor != null)
			editor.insert(s);
		else
			addNewViewer(s);
	//vervangEditorDoorViewer();
		
		//formuleVak.vulVak("$f" + s + "@");
	}
	
	public void zetMaat()
	{	int b = (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+"   ").getWidth();
		if(editor != null)
			b += editor.getWidth();
		else
			b += viewer.getWidth();
		if(operator.equals("abc") || operator.equals("sub")) 
			b = Math.max(110, getWidth());
		width = b;
		if(operator.equals("abc") || operator.equals("sub"))
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
		if(editorPanel.getParent() == this) // FIXME Anders een assertion error bij setWidgetLeftWith
		{ 
			this.setWidgetLeftWidth(editorPanel, (fm.getAscent() + fm.getDescent())/2 + (int) ctx.measureText("  "+operator+" ").getWidth(), Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
		
			
			//hieronder: niet editor.getCurrentRegel().getAsHoogte() nodig??
		    this.setWidgetTopHeight(editorPanel, ashoogte + fm.getAscent()/2-((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte())-fm.getDescent()/2, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);
		
			//formuleVak.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" "),ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
			//formuleVakHaakjeR.setLocation((fm.getAscent() + fm.getDescent())/2 + fm.stringWidth("  "+operator+" ") + formuleVakHaakjeL.getSize().width + formuleVak.getSize().width,ashoogte-formuleVak.ashoogte-fm.getDescent()/2);
			if(operator.equals("abc") || operator.equals("sub"))
			{	
				this.setWidgetLeftWidth(editorPanel, 10 + prefixVak.getWidth(), Style.Unit.PX, (editor != null)?editor.getWidth():viewer.getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(editorPanel, ashoogte, Style.Unit.PX, (editor != null)?editor.getHeight():viewer.getHeight(), Style.Unit.PX);
				this.setWidgetTopHeight(prefixVak.getAsPanel(), ashoogte + ((editor != null)?editor.getMainRegel().getAsHoogte():viewer.getMainRegel().getAsHoogte()) - prefixVak.getAsHoogte(), Style.Unit.PX, prefixVak.getHeight(), Style.Unit.PX);
				
			}
		
		}
		fe.zetPijlVakMaat();
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
		public void enter() {
            
			if(!aanpasbaar)
				return;
			aanpasbaar = false;
			if(goedKrulImage.isAttached())
				remove(goedKrulImage);
			if(foutKruisImage.isAttached())
				remove(foutKruisImage);
			
			if(operator.equals("abc"))
			{	String vergelijkingString = fe.getLatestAnswer();
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
				
				if(goed)
				{
					pijlvak.add(goedKrulImage);
					setWidgetRightWidth(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					setWidgetBottomHeight(goedKrulImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					fe.zetEditorTerug();
				}
				else
				{	aanpasbaar = true;
					pijlvak.add(foutKruisImage);
					setWidgetRightWidth(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
					setWidgetBottomHeight(foutKruisImage, 0, Style.Unit.PX, 30, Style.Unit.PX);
				}
			}
			else if(operator.equals("sub"))
			{
				String substitutieString = editor.toString();
				Expressie substitutie = FormuleParser.geefExpressie("$f" + substitutieString + "@");
				fe.zetSubstitutie(substitutie);
				fe.zetEditorTerug();
			}
			else
			{	if(!operator.equals("haakjes") && !operator.equals("herleid") && !operator.equals("gelijkwaardig") && !operator.equals("ontbind") && !operator.equals("splits") && !operator.equals("wortel")  && !operator.equals("implicatie"))
				{	if(editor.toString().equals("")) 
					{	aanpasbaar = true;
						return;
					}
				}
				fe.maakBewerkingStap();
				Expressie exp = FormuleParser.parse(editor.toString());
		 		if(exp!=null && Algebra.geefTermen(exp,new Vector()).size()>1)
		 		{	editor.insert("$h" + exp.toString() + "@");
		 			
		 		}
			}
			if(!aanpasbaar)
				vervangEditorDoorViewer();
		}
		
		public void resize()
		{
			System.out.println("resize aangeroepen");
			pijlvak.zetMaat();
		}
		
	}
}
