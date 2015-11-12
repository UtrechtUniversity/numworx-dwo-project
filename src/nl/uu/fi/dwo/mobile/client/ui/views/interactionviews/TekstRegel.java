package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.TekstComponent;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.IFrameView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;

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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.graphtoolgwt.client.GraphToolGWT;


public class TekstRegel extends LayoutPanel
{
	private int ashoogte;
	private int tekstAshoogte;
	private int tekstHoogte;
	private int hoogte;
	private int breedte;
	private ArrayList<Object> regelObjects;
	private TekstVak tekstVak;

	
	private int font_size;
	private int font_style;
	private String font_name = "Arial";
	private Context2d ctx;
	private String fontString;
	//private fontType = "sans-serif";
	
	private FormuleFont fm;
	private CssColor fgColor = CssColor.make(0, 0, 0);
	
	private Logger logger = Logger.getLogger("TekstRegel");
	public TekstRegel(TekstVak tekstVak)
	{
		super();
		setStylePrimaryName("tekstregel");
		this.tekstVak = tekstVak;
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
	{	if(ashoogte - this.ashoogte > 0)
		{	setHeight(hoogte + ashoogte - this.ashoogte);
		}
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
	
	public void setFontName(String font_name)
	{
		this.font_name = font_name;
	}
	
	public void setFontSize(int font_size)
	{
		this.font_size = font_size;
		this.getElement().getStyle().setFontSize(font_size, Unit.PX);
		fm = FormuleFont.createFromFontSize(font_size, true);
		fm.setFont(font_name);
		tekstAshoogte = fm.getAscent();//dit is de hoogte van de baseline (de lijn die raakt aan alle onderkanten van de tekst, niet alleen aan de lage uitsteeksels), gezien vanaf de top.
		tekstHoogte = fm.getAscent() + fm.getDescent();
		
		Canvas canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		String fontTypeString = "";
		if(font_style == 1)
		{	fontTypeString = "bold";
			fm.setBold(true);
			fm.setItalic(false);
		}
		else if(font_style == 2)
		{	fontTypeString = "italic";
			fm.setItalic(true);
			fm.setBold(false);
		}
		else if(font_style == 3)
		{	fontTypeString = "bold italic";
			fm.setBold(true);
			fm.setItalic(true);
		}
		else
		{
			fm.setBold(false);
			fm.setItalic(false);
		}
		if(fontTypeString.equals(""))
			fontString = font_size + "px " + font_name;
		else
			fontString = fontTypeString + " " + font_size + "px " + font_name;
		ctx.setFont(fontString);
	}
	
	public void setFontStyle(int font_style)
	{
		this.font_style = font_style;
		this.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
		this.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
		
//		Canvas canvas = Canvas.createIfSupported();
//		ctx = canvas.getContext2d();
//		String fontTypeString = "";
//		if(font_style == 1)
//		{	fontTypeString = "bold";
//			fm.setBold(true);
//			fm.setItalic(false);
//		}
//		else if(font_style == 2)
//		{	fontTypeString = "italic";
//			fm.setItalic(true);
//			fm.setBold(false);
//		}
//		else if(font_style == 3)
//		{	fontTypeString = "bold italic";
//			fm.setBold(true);
//			fm.setItalic(true);
//		}
//		else
//		{	fm.setBold(false);
//			fm.setItalic(false);
//		}
//		if(fontTypeString.equals(""))
//		{	ctx.setFont(font_size + "px " + XMLView.getDefaultFontName());
//		}
//		else
//		{	ctx.setFont(fontTypeString + " " + font_size + "px " + XMLView.getDefaultFontName());
//		}
	}
	
	public void vulRegel()
	{	int horPositie = 0;
	
		for(int i = 0; i < regelObjects.size(); i++)
		{
			Object currentObject = regelObjects.get(i);
			int objectVerschuiving = 0;
			int objectBreedte = 0;
			int objectHoogte = 0;
				
		
			if(currentObject instanceof TekstElement) //formules, imageView, interactionView
			{	objectVerschuiving = ashoogte - ((TekstElement) currentObject).getAsHoogte();
				objectBreedte = ((TekstElement) currentObject).getWidth();
				objectHoogte = ((TekstElement) currentObject).getHeight();
			}
			else if(currentObject instanceof AnchorView)
			{
				objectVerschuiving = ashoogte - tekstAshoogte + 3;
				objectBreedte = (int) ctx.measureText(currentObject.toString()).getWidth();
				objectHoogte = tekstHoogte;
			}
			else if(currentObject instanceof String)
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
				objectBreedte = (int) ctx.measureText(currentObject.toString()).getWidth();
				if(ctx.getFont().contains("italic"))
					objectBreedte += 1;
				objectHoogte = tekstHoogte;
			}
			else
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
			}
			if(currentObject instanceof String)
			{
				
				TekstComponent tekst = new TekstComponent(fm, currentObject.toString(), objectBreedte, objectHoogte);
				tekst.setColor(fgColor);
				tekst.paint();
				
				if(horPositie == 0 && Character.isLetter(currentObject.toString().charAt(0)))
					horPositie = 2;
				Widget w = tekst.getAsPanel();
				
				this.add(w);
				this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			else if (currentObject instanceof FormuleViewer)
			{	Panel a = ((FormuleViewer) currentObject).getAsPanel();
				horPositie += 1;
				
				this.add(a);
				this.setWidgetLeftWidth(a, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(a, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
					horPositie += 3; //om te zorgen dat formule aan de rechterkant ook voldoende afstand tot vervolgtekst krijgt.
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Widget a = ((FormuleEditorWithSteps) currentObject).asWidget();
				
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
			else if (currentObject instanceof ImageView || currentObject instanceof IFrameView)
			{
				TekstElement iv = (TekstElement) currentObject;
				Widget w = ((IsWidget) iv).asWidget();
				objectBreedte = iv.getWidth();
				objectHoogte = iv.getHeight();
				if(w != null) //w kan null zijn als plaatje niet in lijst met images voorkomt.
				{
					this.add(w);
					this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
					this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
				}
				
			}
			else if (currentObject instanceof AnchorView)
			{
				AnchorView av = (AnchorView) currentObject;
				Widget w = av.asWidget();
				this.add(w);
				//objectVerschuiving = 0; // FIXME deze positie werkt niet in Anchorview -13
				this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			}
			horPositie += objectBreedte;
		}
		breedte = horPositie + 1; //zie resize tekstRegel WiskOpdr.
		//breedte = horPositie;
	}
	
	public void clear()
	{
		super.clear();
		regelObjects = new ArrayList<Object>();
	}
	
	public void resize()
	{
		bepaalAshoogte();
		
		hervulRegel();
		
		tekstVak.resize();
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
				{	horPositie += 1;
				}
			}
			else
			{
				objectVerschuiving = ashoogte - tekstAshoogte;
				if(regelObjects.get(i) instanceof String || regelObjects.get(i) instanceof AnchorView )
				{
					String s = regelObjects.get(i).toString();
					objectBreedte = (int) ctx.measureText(s).getWidth();
					objectHoogte = tekstHoogte;
					
					if(horPositie == 0 && Character.isLetter(s.charAt(0)))
						horPositie = 2;
				}
				
			}
			
			this.setWidgetLeftWidth(w, horPositie, Style.Unit.PX, objectBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(w, objectVerschuiving, Style.Unit.PX, objectHoogte, Style.Unit.PX);
			horPositie += objectBreedte;
			if(regelObjects.get(i) instanceof FormuleViewer)
				horPositie += 3;
		}
		breedte = horPositie  + 1; //zie resize tekstRegel WiskOpdr.
//		if(regelObjects.size() == 0)
//		{	this.setHeight(tekstHoogte);
//		}
		//breedte = horPositie;
	}
	
	
	
	
	public void bepaalAshoogte()
	{
		System.out.println("TekstRegel.bepaalAshoogte. Hoogte begin: " + hoogte);
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
			else if(currentObject instanceof String || currentObject instanceof AnchorView)
			{
				int hoogte = tekstHoogte;
				int ash = tekstAshoogte;
				if(ash > h1)
					h1 = ash;
				if(hoogte - ash > h2)
					h2 = hoogte - ash;
			}
		}
		if(regelObjects.size() > 0)
		{
			this.hoogte = h1 + h2;
			if(this.hoogte < tekstHoogte)
				this.hoogte = tekstHoogte;
			ashoogte = h1;
		}
		else
		{
			this.hoogte = tekstHoogte;
			ashoogte = tekstAshoogte;
		}
		this.setHeight(this.hoogte);
		System.out.println("TekstRegel.bepaalAshoogte. Hoogte eind: " + hoogte);
		
	}
	
	public void setColor(CssColor color)
	{
		fgColor = color;
		this.getElement().getStyle().setColor(color.toString());
		
	}
	
}
