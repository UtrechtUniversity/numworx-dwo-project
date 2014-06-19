package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.TekstElement;
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
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;


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
	
	private FormuleFont fm;
	
	public TekstRegel(TekstVak tekstVak)
	{
		super();
		setStylePrimaryName("tekstregel");
		this.tekstVak = tekstVak;
		this.getElement().getStyle().setProperty("lineHeight", "1.2");
		regelObjects = new ArrayList<Object>();
	}
	
	public int getAsHoogte()
	{
		return ashoogte;
	}
	
	public int getTekstAsHoogte()
	{
		return tekstAshoogte;
	}
	
	public FormuleFont getFont()
	{
		return fm;
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
		
		hervulRegel();
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
		tekstAshoogte = fm.getAscent() / 2 - 1; // - 1 om te zorgen voor goede afstand tot bovenrand tekstvak. Zou ook in marge opgelost kunnen worden, afh van omgang met meerdere regels nu.
		
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
	{	//this.clear();
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
				if(currentObject instanceof FormuleViewer)
					objectVerschuiving += 1; //+1 om gelijk te houden met gewone tekst.
				//if(currentObject instanceof FormuleEditorWithAnswer)
				//	objectVerschuiving -= 1; //weet nog niet of dit de beste oplossing is..
				objectBreedte = ((TekstElement) currentObject).getWidth();
				objectHoogte = ((TekstElement) currentObject).getHeight();
			//System.out.println("Object.toString: " + ((TekstElement) currentObject).toString() + " en objectVerschuiving: " + objectVerschuiving);
			}
			else if(currentObject instanceof String)
			{
				//objectVerschuiving = fm.getHeight() - tekstAshoogte;
				objectVerschuiving = ashoogte - tekstAshoogte;
				objectBreedte = bepaalStringBreedte(currentObject.toString());
				objectHoogte = fm.getAscent() + fm.getDescent();
			}
			else
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
			}
			
			//System.out.println("vulVak: " + currentObject.toString() + " en objectVerschuiving = " + objectVerschuiving);
			if(currentObject instanceof String)
			{
				//String tekst = currentObject.toString().replaceAll("  ", " &nbsp;");
				//tekst = tekst.replaceAll("&nbsp; ", "nbsp;&nbsp;");
				//System.out.println("tekst = " + tekst);		
				Label label = new Label(currentObject.toString());
				label.getElement().getStyle().setProperty("whiteSpace", "pre");//om te zorgen dat meerdere spaties niet worden samengetrokken tot één spatie.
				label.getElement().getStyle().setFontSize(font_size, Style.Unit.PX);
				label.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
				label.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
				label.getElement().getStyle().setPaddingRight(0, Style.Unit.PX);
				//int paddingLeft = 0;
				//if(currentObject.toString().startsWith(" "))
				//	paddingLeft += ctx.measureText(" ").getWidth();
				//if(font_style == 2 || font_style == 3)
				//	paddingLeft += 2;
				//if(font_style == 2 || font_style == 3)
				//{	paddingLeft += 1;
				//}
					//	objectBreedte += 2;
				//}
				//label.getElement().getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
				//objectBreedte += paddingLeft;
				//if(font_style == 3)
				//	objectVerschuiving -= 1;
					
				//label.getElement().getStyle().setProperty("display", "inline-block");
				//label.getElement().getStyle().setProperty("verticalAlign", "top");
				//label.getElement().getStyle().setProperty("verticalAlign", objectVerschuiving + "px");
				//als het eerste element op een regel tekst is, krijgt het een marge van 2. Dat gebeurt in wiskOpdr ook.
				
				if(horPositie == 0 && Character.isLetter(currentObject.toString().charAt(0)))
					horPositie = 2;
				
				
				this.add(label);
				this.setWidgetLeftWidth(label, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(label, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof FormuleViewer)
			{	Panel a = ((FormuleViewer) currentObject).getAsPanel();
				//a.getElement().getStyle().setPaddingLeft(0, Style.Unit.PX);
				//a.getElement().getStyle().setPaddingRight(4, Style.Unit.PX);
				//objectBreedte += 4;
				//a.getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
				//a.getElement().getStyle().setPaddingRight(2, Style.Unit.PX);
				horPositie += 1;
				
				//objectBreedte += 2;
				FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				//a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				//a.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				horPositie += 3; //zorgen dat formule aan de rechterkant ook voldoende afstand tot vervolgtekst krijgt.
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Panel a = ((FormuleEditorWithSteps) currentObject).getAsPanel();
				
				a.getElement().getStyle().setProperty("verticalAlign", "" + objectVerschuiving + "px");
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
			{
				Panel a = (Panel) (((InteractionView) currentObject).asWidget());
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
				if(!(a instanceof PopupButton))
				{	this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
					this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				}
				else 
				{	//TODO: objectBreedte en hoogte nog aanpassen.
					this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
					this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				}
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				Widget w = iv.getImage();
				objectBreedte = iv.getWidth();
				objectHoogte = iv.getHeight();
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
	
	public void resize()
	{
		bepaalAshoogte();
		
		hervulRegel();
		
		tekstVak.resize();
	}
	
	public int bepaalStringBreedte(String s)
	{
		int breedte = (int) ctx.measureText(s).getWidth();
		//int paddingLeft = 0;
		//if(s.startsWith(" "))
		//	paddingLeft += ctx.measureText(" ").getWidth();
		//breedte += paddingLeft;
		//als italic: 1 pixel meer ruimte links en 1 pixel meer ruimte rechts. Juiste padding wordt in vulRegel geregeld.
		if(font_style == 2 || font_style == 3) //TODO moet dit voor bold tekst ook?
		{	breedte += 2;
		}
		return breedte;
	}
	
	public void hervulRegel()
	{
		int horPositie = 0;
		for(int i = 0; i < this.getWidgetCount(); i++)
		{
			int objectBreedte = 0;
			int objectHoogte = 0;
			int objectVerschuiving = 0;
			Widget w = this.getWidget(i);
			if(regelObjects.get(i) instanceof TekstElement)
			{
				TekstElement object = (TekstElement) regelObjects.get(i);
				objectBreedte = object.getWidth();
				objectHoogte = object.getHeight();
				objectVerschuiving = ashoogte - object.getAsHoogte();
				if(object instanceof FormuleViewer)
				{	//objectBreedte += 2;
					horPositie += 1;
					objectVerschuiving += 1;
				}
			}
			else
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
				if(regelObjects.get(i) instanceof String)
				{
					String s = ((Label) w).getText();
					objectBreedte = bepaalStringBreedte(s);
					//objectHoogte = fm.getHeight() + 1;//waarom deze +1?
					objectHoogte = fm.getAscent() + fm.getDescent();
					
					if(horPositie == 0 && Character.isLetter(s.charAt(0)))
						horPositie = 2;
				}
// ImageView is een tekstelement
//				else if(regelObjects.get(i) instanceof ImageView)
//				{	objectBreedte = w.getOffsetWidth();
//					objectHoogte = w.getOffsetHeight();
//				}
				else if(regelObjects.get(i) instanceof AnchorView)
				{	objectBreedte = w.getOffsetWidth();
					objectHoogte = w.getOffsetHeight();
				}
			}
			this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			horPositie += objectBreedte;
			if(regelObjects.get(i) instanceof FormuleViewer)
				horPositie += 3;
		}
		breedte = horPositie;
	}
	
	
	
	
	public void bepaalAshoogte()
	{
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
				int hoogte = fm.getHeight() + 1;
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
			//if(h1 + h2 > this.hoogte)
				this.hoogte = h1 + h2;
			//if(this.hoogte < font_size + 4)
			//	this.hoogte = font_size + 4;
			if(this.hoogte < fm.getHeight() + 1)
				this.hoogte = fm.getHeight() + 1;
			//if(h1 > ashoogte)
				ashoogte = h1;
		}
		else
		{
			//this.hoogte = font_size + 4;//eigenlijk: fm.getAscent() + fm.getDescent()
			//if(fm.getHeight() > this.hoogte)
				this.hoogte = fm.getHeight() + 1;
			//System.out.println("fm.getHeight: " + fm.getHeight() + " en fm.getAscent() + fm.getDescent(): " + (fm.getAscent() + fm.getDescent()));
			//ashoogte = (font_size + 2) / 2; //eigenlijk: fm.getAscent();
			//if(fm.getAscent()/2 > ashoogte)
				ashoogte = fm.getAscent() / 2;
		}
		//eigenHoogte = hoogte;
		//eigenAsHoogte = ashoogte;
		this.setHeight(this.hoogte);
		//System.out.println("berekende ashoogte: " + ashoogte);
		
	}
	
}
