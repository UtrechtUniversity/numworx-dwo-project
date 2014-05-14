package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class TekstRegel extends LayoutPanel
{
	private int ashoogte;
	private int tekstAshoogte;
	private int hoogte;
	private int breedte;
	private ArrayList<Object> regelObjects;
	private TekstVak tekstVak;
	
	private int font_size;
	private int font_style;
	private Context2d ctx;
	private String fontString;
	//private int asVerschuiving = 0;
	//private int eigenHoogte = 0;
	//private int eigenAsHoogte = 0;
	
	private FormuleFont fm;
	
	public TekstRegel(TekstVak tekstVak)
	{
		super();
		this.tekstVak = tekstVak;
		this.getElement().getStyle().setProperty("lineHeight", "1.2");
		regelObjects = new ArrayList<Object>();
	}
	
	public int getAsHoogte()
	{
		return ashoogte;
	}
	
	public void setAsHoogte(int ashoogte)
	{	//System.out.println("setAsHoogte: " + ashoogte);
		//eigenAsHoogte = this.ashoogte;
		//eigenHoogte = hoogte;
	//asVerschuiving = hoogte - this.ashoogte;// - this.ashoogte;
		
		//System.out.println("asVerschuiving: " + asVerschuiving);
		if(ashoogte - this.ashoogte > 0)
		{	//System.out.println("oude hoogte = " + hoogte);
			setHeight(hoogte + ashoogte - this.ashoogte);
			//System.out.println("nieuwe hoogte = " + hoogte);
		}
		//else
		//	System.out.println("hoogte blijft " + hoogte);
		this.ashoogte = ashoogte;
		
		vulRegel();
		//hier vulRegel aanroepen? Zodat alles opnieuw neer wordt gezet? Of alleen de juiste hoogtes instellen?
	}
	
	public int getHeight()
	{
		return hoogte;
	}
	
	public int getWidth()
	{
		return breedte;
	}
	
	public void setHeight(int hoogte)
	{
		this.hoogte = hoogte;
		this.setHeight(hoogte + "px");
		//vulRegel();
		//hier vulRegel aanroepen? Zodat alles opnieuw neer wordt gezet? Of alleen de juiste hoogtes instellen?
	}
	
	public void addObject(Object object)
	{
		//Als een string wordt toegevoegd en het laatste object in de regelObjects ook een string is, deze strings samenvoegen
		if(regelObjects.size() > 0)
		{	Object laatsteObject = regelObjects.get(regelObjects.size() - 1);
			if(laatsteObject instanceof String && object instanceof String)
			{	laatsteObject = (String) laatsteObject + (String) object;
				laatsteObject = ((String) laatsteObject).replaceAll("&nbsp;", " ");
				regelObjects.remove(regelObjects.size() - 1);
				regelObjects.add(laatsteObject);
				return;
			}
		}
		if(object instanceof String)
			object = ((String) object).replaceAll("&nbsp;", " ");
		regelObjects.add(object);
	}
	
	public ArrayList<Object> getRegelObjects()
	{
		return regelObjects;
	}
	
	public void setFontSize(int font_size)
	{
		this.font_size = font_size;
		this.getElement().getStyle().setFontSize(font_size, Unit.PX);
		fm = FormuleFont.createFromFontSize(font_size);
		//tekstAshoogte = fm.getAscent() / 2;
		tekstAshoogte = fm.getAscent() / 2;
		
		Canvas canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		String fontTypeString = "";
		if(font_style == 1)
			fontTypeString = "bold";
		else if(font_style == 2)
			fontTypeString = "italic";
		else if(font_style == 3)
			fontTypeString = "bold italic";
		if(fontTypeString.equals(""))
			fontString = font_size + "px sans-serif";
		else
			fontString = fontTypeString + " " + font_size + "px sans-serif";
		ctx.setFont(fontString);
	}
	
	public void setFontStyle(int font_style)
	{
		this.font_style = font_style;
		this.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
		this.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
		
		Canvas canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		String fontTypeString = "";
		if(font_style == 1)
			fontTypeString = "bold";
		else if(font_style == 2)
			fontTypeString = "italic";
		else if(font_style == 3)
			fontTypeString = "bold italic";
		if(fontTypeString.equals(""))
			ctx.setFont(font_size + "px sans-serif");
		else
			ctx.setFont(fontTypeString + " " + font_size + "px sans-serif");
	}
	
	public void vulRegel()
	{
		this.clear();
		int horPositie = 0;
		//this.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		//this.getElement().getStyle().setProperty("verticalAlign", "top");
		//int verschuiving = eigenHoogte - hoogte + ashoogte - eigenAsHoogte;//nu verschuift hij altijd, ook als hij al goed stond...
		//int verschuiving = 0;
		
		for(int i = 0; i < regelObjects.size(); i++)
		{
			Object currentObject = regelObjects.get(i);
			int objectVerschuiving = 0;
			int objectBreedte = 0;
			int objectHoogte = 0;
			//if(currentObject instanceof TekstElement)
				
		
			if(currentObject instanceof TekstElement)
			{	//objectVerschuiving = ((TekstElement) currentObject).getHeight()-((TekstElement) currentObject).getAsHoogte();
				objectVerschuiving = ashoogte - ((TekstElement) currentObject).getAsHoogte();
				objectBreedte = ((TekstElement) currentObject).getWidth();
				objectHoogte = ((TekstElement) currentObject).getHeight();
			//System.out.println("Object.toString: " + ((TekstElement) currentObject).toString() + " en objectVerschuiving: " + objectVerschuiving);
			}
			else if(currentObject instanceof String)
			{
				//objectVerschuiving = fm.getHeight() - tekstAshoogte;
				objectVerschuiving = ashoogte - tekstAshoogte;
				objectBreedte = (int) ctx.measureText(currentObject.toString()).getWidth();
				objectHoogte = fm.getHeight();
			}
			else
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
			}
			
			//System.out.println("vulVak: " + currentObject.toString() + " en objectVerschuiving = " + objectVerschuiving);
			if(currentObject instanceof String)
			{
				Label label = new Label(currentObject.toString());
				label.getElement().getStyle().setFontSize(font_size, Style.Unit.PX);
				label.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
				label.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
				int paddingLeft = 0;
				if(currentObject.toString().startsWith(" "))
					paddingLeft += ctx.measureText(" ").getWidth();
				if(font_style == 2 || font_style == 3)
					paddingLeft += 2;
				label.getElement().getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
				objectBreedte += paddingLeft;
				if(font_style == 2)
					objectVerschuiving -= 1;
					
				//label.getElement().getStyle().setProperty("display", "inline-block");
				//label.getElement().getStyle().setProperty("verticalAlign", "top");
				//label.getElement().getStyle().setProperty("verticalAlign", objectVerschuiving + "px");
				//als het eerste element op een regel tekst is, krijgt het een marge van 2. Dat gebeurt in wiskOpdr ook.
				
				//if(horPositie == 0)
				//	horPositie = 2;
				
				
				this.add(label);
				this.setWidgetLeftWidth(label, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(label, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				//Element element = DOM.createSpan();
				//element.setInnerHTML((String) currentObject);
				
				//System.out.println("verticalAlignment: " + (verschuiving - (font_size + 1 - tekstAshoogte)));
				//element.getStyle().setProperty("verticalAlign", "" + (verschuiving - (font_size + 4 - tekstAshoogte)) + "px");
				//element.getStyle().setProperty("verticalAlign", "top");
				//element.getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				//element.getStyle().setProperty("verticalAlign", objectVerschuiving + "px");
				
				/*
				this.getElement().appendChild(element);
				System.out.println("")
				this.setWidgetLeftWidth(element, horPositie, Style.Unit.PX, element.getOffsetWidth(), Style.Unit.PX);
				
				*/
				
				
				//destination.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
				//want dan krijgt het hele vak een marge van 2; tekstvakken worden dan dus ook 2 px naar rechts geduwd.
				//dit werkte niet bij het flowpanel. Bij de regelpanels werkt dit wellicht wel; ik kan kijken of het eerste
				//element van de regel een string is (of op het moment van toevoegen de breedte nog 0 is), 
				//en in dat geval links een marge van 2 px zetten.
				
				 
				
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				TouchPanel tp = (TouchPanel) ((FormuleEditorWithAnswer) currentObject).getAsPanel();
				//tp.getElement().getStyle().setBackgroundColor(CssColor.make(255, 0, 0).toString());
				//tp.getElement().getStyle().setProperty("display", "inline-block");
				tekstVak.getTekstVakParent().getKeyboard().setEditor(((FormuleEditorWithAnswer) currentObject));
				tekstVak.getTekstVakParent().addFormulePanelListeners(tp, ((FormuleEditorWithAnswer) currentObject));

				//tp.getElement().getStyle().setProperty("display", "inline-block");
				//tp.getElement().getStyle().setProperty("verticalAlign", "" + (-hoogte + asHoogte + Math.rint(font_size * 0.33) + 1) + "px");
				//tp.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				//tp.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				this.add(tp);
				this.setWidgetLeftWidth(tp, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(tp, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				}
			else if (currentObject instanceof FormuleViewer)
			{	Panel a = ((FormuleViewer) currentObject).getAsPanel();
				//a.getElement().getStyle().setProperty("display", "inline-block");
				
				//deze 2 px zijn overgenomen uit het WiskOpdr TekstFormuleVak, om te zorgen dat formules niet op tekst botsen. 
				a.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);
				a.getElement().getStyle().setPaddingRight(2, Style.Unit.PX);
				objectBreedte += 4;
				FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				//a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				//a.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Panel a = ((FormuleEditorWithSteps) currentObject).getAsPanel();
				
				//a.getElement().getStyle().setProperty("display", "inline-block");
				//a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				a.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
			{
				Panel a = (Panel) (((InteractionView) currentObject).asWidget());
				//a.getElement().getStyle().setProperty("display", "inline-block");
				//a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof InteractionView)
			{		
				Widget a = (((InteractionView) currentObject).asWidget());
				//a.getElement().getStyle().setProperty("display", "inline-block");
				//if(currentObject instanceof TekstVakPanel && !(a instanceof PopupButton))
				//{
					//a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
					//a.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				//}
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				Widget w = iv.getImage();
				objectBreedte = w.getOffsetWidth();
				objectHoogte = w.getOffsetHeight();
				this.add(w);
				this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof AnchorView)
			{
				AnchorView av = (AnchorView) currentObject;
				Widget w = av.asWidget();
				objectBreedte = w.getOffsetWidth();
				objectHoogte = w.getOffsetHeight();
				this.add(w);
				this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			horPositie += objectBreedte;
		}
		breedte = horPositie;
	}
	
	
	
	
	public void bepaalAshoogte()
	{
		//System.out.println("begin bepaalAshoogte");
		int h1 = 0;
		int h2 = 0;
		for(int i = 0; i < regelObjects.size(); i++)
		{
			Object currentObject = regelObjects.get(i);
			if(currentObject instanceof TekstElement)
			{
				int hoogte = ((TekstElement) currentObject).getHeight();
				int ash = ((TekstElement) currentObject).getAsHoogte();
				if(ash > h1)
					h1 = ash;
				if(hoogte - ash > h2)
					h2 = hoogte - ash;
			}
			else if(currentObject instanceof String)
			{
				//int hoogte = font_size + 4;
				int hoogte = fm.getHeight();
				//System.out.println("string: hoogte = " + hoogte);
				//int ash = (font_size + 2) / 2;
				//int ash = fm.getHeight() / 2;
				int ash = fm.getAscent() / 2;
				if(ash > h1)
					h1 = ash;
				if(hoogte - ash > h2)
					h2 = hoogte - ash;
			}
		}
		if(regelObjects.size() > 0)
		{
			this.hoogte = h1 + h2;
			//if(this.hoogte < font_size + 4)
			//	this.hoogte = font_size + 4;
			if(this.hoogte < fm.getHeight())
				this.hoogte = fm.getHeight();
			ashoogte = h1;
		}
		else
		{
			//this.hoogte = font_size + 4;//eigenlijk: fm.getAscent() + fm.getDescent()
			this.hoogte = fm.getHeight();
			//System.out.println("fm.getHeight: " + fm.getHeight() + " en fm.getAscent() + fm.getDescent(): " + (fm.getAscent() + fm.getDescent()));
			//ashoogte = (font_size + 2) / 2; //eigenlijk: fm.getAscent();
			ashoogte = fm.getAscent() / 2;
		}
		//eigenHoogte = hoogte;
		//eigenAsHoogte = ashoogte;
		this.setHeight(this.hoogte);
		//System.out.println("berekende ashoogte: " + ashoogte);
		
	}
	
}
