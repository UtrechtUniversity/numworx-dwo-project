package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.IFrameView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;




public class TekstVak extends LayoutPanel //implements InteractionView
{
	
	static Logger logger = Logger.getLogger("TekstVak");
	private TekstVakPanel parent;
	private int rij;
	private int kolom;
	private ArrayList<Object> opdrachtObjects;
	private ArrayList<TekstVakPanel> zwevendeTekstVakken = new ArrayList<TekstVakPanel>();
	private int ashoogte;
	private double tekstVakBreedte;
	
	private boolean pasAanH = false;
	private boolean pasAanB = false;
	
	private boolean centerH = false;
	private boolean centerV = false;
	
	//private FlowPanel flowVak;
	private TekstRegel[] regelVakken;
	//private VerticalPanel vPanel;
	private int aantalRegels;
	
	private int cellMarge;
	private int bovenMarge;
	private int interlinie = 0;
	
	//voor als er een uitklapknop links in dit vak staat:
	private int knopBreedte = 0;
	
	private int font_size;
	private int font_style;
	private String font_name;
	private CssColor fgColor;
	int hoogte = 0;
	int breedte = 0;
	
	
	public TekstVak(TekstVakPanel parent, int rij, int kolom)
	{
		super();
		setStylePrimaryName("tekstvak");
		this.parent = parent;
		this.rij = rij;
		this.kolom = kolom;
		//flowVak = new FlowPanel();
		//flowVak.getElement().getStyle().setProperty("lineHeight", "1.2");
		regelVakken = new TekstRegel[500];
		regelVakken[0] = new TekstRegel(this);
		regelVakken[1] = new TekstRegel(this);
		
//		regelVakken[0].getElement().getStyle().setBackgroundColor(CssColor.make(0, 255, 255).toString());
//		regelVakken[1].getElement().getStyle().setBackgroundColor(CssColor.make(200, 135, 255).toString());
		
		aantalRegels = 1;
		//vPanel = new VerticalPanel();
		//vPanel.add(flowVak);
		//vPanel.setHeight("100%");
		
		//this.add(vPanel);
		//this.setWidgetLeftRight(vPanel, 0, Unit.PX, 0, Unit.PX);
		//this.setWidgetTopBottom(vPanel, 0, Unit.PX, 0, Unit.PX);
	}
	
	public TekstVak()
	{
		this(null, 0, 0);
		
	}

	public TekstVakPanel getTekstVakParent()
	{
		return parent;
	}
	
	public int getRij()
	{
		return rij;
	}
	
	public int getKolom()
	{
		return kolom;
	}
	

	public void zetOpdrachtObjects(ArrayList<Object> objects)
	{
		this.opdrachtObjects = objects;
	}
	
	public ArrayList<Object> getOpdrachtObjects()
	{
		return opdrachtObjects;
	}
	
	//public FlowPanel getFlowPanel()
	//{
	//	return flowVak;
	//}
	
	public void setColor(CssColor color)
	{
		this.fgColor = color;
		//flowVak.getElement().getStyle().setColor(color.toString());
		for(int i = 0; i < aantalRegels  + 1; i++)
		{	regelVakken[i].setColor(color);
		}
	}
	
	public void setFontSize(int font_size)
	{
		this.font_size = font_size;
		//flowVak.getElement().getStyle().setFontSize(font_size, Unit.PX);
		for(int i = 0; i < aantalRegels  + 1; i++)
			regelVakken[i].setFontSize(font_size);
	}
	
	public void setFontStyle(int font_style)
	{
		this.font_style = font_style;
		//flowVak.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
		//flowVak.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
	
		for(int i = 0; i < aantalRegels  + 1; i++)
		{	regelVakken[i].setFontStyle(font_style);
		}
	}
	
	public void setFontName(String font_name)
	{
		this.font_name = font_name;
		
		for(int i = 0; i < aantalRegels  + 1; i++)
		{	regelVakken[i].setFontName(font_name);
		}
	}
	
	public void setRonding(int ronding)
	{
		//flowVak.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
		this.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
	}

	public void setPasHoogteBreedteAan(boolean pasAanH, boolean pasAanB)
	{
		this.pasAanH = pasAanH;
		this.pasAanB = pasAanB;
	}
	
	public void setCentering(boolean centerH, boolean centerV)
	{
		this.centerH = centerH;
		this.centerV = centerV;
		
		/*
		if(centerH)
		{	flowVak.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			for(int i = 0; i < aantalRegels  + 1; i++)
				regelVakken[i].getElement().getStyle().setTextAlign(TextAlign.CENTER);
		}
		if(centerV)
			vPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		*/
	}
	
	public void setTekstVakBreedte(double tekstVakBreedte)
	{
		this.tekstVakBreedte = tekstVakBreedte;
		//flowVak.setWidth(tekstVakBreedte + "px");
		for(int i = 0; i < aantalRegels  + 1; i++)
			regelVakken[i].setWidth(tekstVakBreedte + "px");
	}
	
	public void setMarges(int bovenMarge, int cellMarge)
	{
		this.cellMarge = cellMarge;
		this.bovenMarge = bovenMarge;
		tekstVakBreedte = breedte - 2  * cellMarge - knopBreedte;//hier stond - 2 bij. Die was ergens goed voor, maar levert ook problemen als het vak zijn breedte aanpast aan de inhoud.
		if(tekstVakBreedte >= 0)
		{	//if(hoogte > 0)
			//	flowVak.setSize(tekstVakBreedte + "px", "" + hoogte + "px");
			for(int i = 0; i < aantalRegels  + 1; i++)
				regelVakken[i].setWidth(tekstVakBreedte + "px");
		}
		//vPanel.getElement().getStyle().setProperty("margin", "" + (bovenMarge  + 1)  + "px " + cellMarge + "px");
	}
	
	public void setInterlinie(int interlinie)
	{
		this.interlinie = interlinie;
	}
	
	public void setSize(int b, int h)
	{
		this.breedte = b;
		this.hoogte = h;
		tekstVakBreedte = b - 2 * cellMarge - knopBreedte;//hier stond - 2 bij. Die was ergens goed voor, maar levert ook problemen als het vak zijn breedte aanpast aan de inhoud.
		if(tekstVakBreedte >= 0 && h >= 0)
		{	//flowVak.setSize("" + tekstVakBreedte  + "px", "" + h + "px");
			for(int i = 0; i < aantalRegels  + 1; i++)
			{	regelVakken[i].setWidth(tekstVakBreedte + "px");
				//TODO gaat dit goed met centreren?
			}
		}
		if(b <= 0)
		{
			GWT.log(b + "<= 0");
			return;
		}
		if(h > 0)
			this.setPixelSize(b,h);
	}
	
	public int getInhoudBreedte()
	{
		int breedte = 0;
	
		for(int i = 0; i < aantalRegels; i++)
		{	if(regelVakken[i].getWidth() > breedte)
				breedte = regelVakken[i].getWidth();
		}
		return breedte;
	}
	
	public void clear()
	{
		for(int i = 0; i < regelVakken.length; i++)
		{
			if(regelVakken[i] == null)
				break;
			regelVakken[i].clear();
		}
		super.clear();
		
	}
	
	public void setObjects(ArrayList<Object> opdrachtObjects)
	{
		this.opdrachtObjects = opdrachtObjects;
		
		aantalRegels = 1;
		
		double regelBreedte = 0;
		//ctx maken om tekst te kunnen meten. 
		Canvas canvas = Canvas.createIfSupported();
		Context2d ctx = canvas.getContext2d();
		String fontTypeString = "";
		if(font_style == 1)
			fontTypeString = "bold";
		else if(font_style == 2)
			fontTypeString = "italic";
		else if(font_style == 3)
			fontTypeString = "bold italic";
		if(fontTypeString.equals(""))
			ctx.setFont(font_size + "px " + font_name);
		else
			ctx.setFont(fontTypeString + " " + font_size + "px " + font_name);
		double spatieBreedte = ctx.measureText(" ").getWidth();
				
		//Voor alle objecten bepalen op welke regel ze terechtkomen, door breedtes te meten.
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof String)
			{
				String s = (String) currentObject;
				String sInRegel = "";
				double width = 0;
				//int stringBreedte = 0;
				if(i > 0 && opdrachtObjects.get(i - 1) instanceof String && s.length() > 0 && s.startsWith(" "))
				{
					s = s.substring(1);
				}
				//s = s.replaceAll("  ", " &nbsp;");
				//s = s.replaceAll("&nbsp; ", "&nbsp;&nbsp;");
				while(s.contains(" "))
				{	String sub = s.substring(0, s.indexOf(" ") + 1); 
					s = s.substring(s.indexOf(" ") + 1); 
					//int width = (int) Math.round(ctx.measureText(sub.substring(0, sub.length() - 1)).getWidth());
					String substring = sub.substring(0, sub.length() - 1);
					//double width = ctx.measureText(substring).getWidth();
					width = ctx.measureText(sInRegel + substring).getWidth();
					if(regelBreedte == 0)
					{
						regelBreedte = 2;
					}
					if(regelBreedte < 2 || sInRegel.length() == 0 || regelBreedte + width  <= tekstVakBreedte || pasAanB)
					{
						regelVakken[aantalRegels - 1].addObject(substring);
						sInRegel = sInRegel + substring;
						//regelBreedte += width; regelBreedte = measureText van wat er nu in staat. Maar rekening houden met mogelijke ander vakjes..
						if(regelBreedte + width <= 2 || regelBreedte + width + spatieBreedte <= tekstVakBreedte || pasAanB)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							//regelBreedte += spatieBreedte;
							sInRegel = sInRegel + " ";
							width = ctx.measureText(sInRegel).getWidth();
						}
						else if(s.length() > 0)
						{
							voegRegelToe();
							regelBreedte = 0;
							sInRegel = "";
							width = 0;
						}
					}
					else
					{
						voegRegelToe();
						regelVakken[aantalRegels - 1].addObject(substring);
						//regelBreedte = (int) width + 2;
						sInRegel = substring;
						width = ctx.measureText(substring).getWidth();
						//regelBreedte = width + 2;
						regelBreedte = 2;
						if(regelBreedte + width <= 2 || regelBreedte + width + spatieBreedte <= tekstVakBreedte)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							//regelBreedte += spatieBreedte;
							sInRegel = sInRegel + " ";
							width = ctx.measureText(sInRegel).getWidth();
						}
						else if(s.length() > 0)
						{
							voegRegelToe();
							regelBreedte = 0;
							sInRegel = "";
							width = 0;
						}
					}
				}
				if(s.length() > 0)//nu zitten er in s geen spaties meer. De rest nog proberen te plaatsen.
				{
					//double width = ctx.measureText(s).getWidth();
					width = ctx.measureText(sInRegel + s).getWidth();
/// XXX if regelbreedte=0 then +2 hier niet?
					if(regelBreedte < 2 || sInRegel.length() == 0 || regelBreedte + width  <= tekstVakBreedte || pasAanB)
					{
						regelVakken[aantalRegels - 1].addObject(s);
						//regelBreedte += width;
						sInRegel = sInRegel + s;
					}	
					else
					{
						voegRegelToe();
						regelVakken[aantalRegels - 1].addObject(s);
						//regelBreedte = (int) width + 2;
						regelBreedte = 2;
						sInRegel = s;
						width = ctx.measureText(s).getWidth();
					}
				}
				
				regelBreedte += width;
				//nodig om nieuwe regels te krijgen bij enter. De tweede voorwaarde is een (enigszins) kunstmatige oplossing 
				//om laatste regel (die in wiskopdr niet bestaat) niet te maken, maar anders gaat verticaal centreren niet goed.
				
				
				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String
						&& !(opdrachtObjects.size() == i + 2 && opdrachtObjects.get(i + 1).toString().equals(" ")))
				{	
					voegRegelToe();
					regelBreedte = 0;
					sInRegel = "";	
				
				}
			}
			else if(currentObject instanceof InteractionView)
				((InteractionView) currentObject).zetVolledigeBreedte((int) tekstVakBreedte);
			if(currentObject instanceof AntwoordTekstVak)
			{
				((AntwoordTekstVak) currentObject).setFontSize(font_size);
				if(regelBreedte == 0 || regelBreedte + ((AntwoordTekstVak) currentObject).getWidth() <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					((AntwoordTekstVak) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					regelBreedte += ((AntwoordTekstVak) currentObject).getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					((AntwoordTekstVak) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					regelBreedte = ((AntwoordTekstVak) currentObject).getWidth();
				}
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				//((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				//((FormuleEditorWithAnswer) currentObject).setColor(fgColor);
				if(regelBreedte == 0 || regelBreedte + ((FormuleEditorWithAnswer) currentObject).getWidth() <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					((FormuleEditorWithAnswer) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					((FormuleEditorWithAnswer) currentObject).paint();
					regelBreedte += ((FormuleEditorWithAnswer) currentObject).getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					((FormuleEditorWithAnswer) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					regelBreedte = ((FormuleEditorWithAnswer) currentObject).getWidth();
				}
			}
			else if(currentObject instanceof FormuleEditorWithSteps)
			{
				if(regelBreedte == 0 || regelBreedte + ((FormuleEditorWithSteps) currentObject).getWidth() <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					((FormuleEditorWithSteps) currentObject).setFont(regelVakken[aantalRegels - 1]);
					regelBreedte += ((FormuleEditorWithSteps) currentObject).getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					((FormuleEditorWithSteps) currentObject).setFont(regelVakken[aantalRegels - 1]);
					regelBreedte = ((FormuleEditorWithSteps) currentObject).getWidth();
				}
			}
			else if (currentObject instanceof FormuleViewer)
			{	FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				if(!FormuleFont.formTimes)
					f.setFont(font_name);
				((FormuleViewer) currentObject).setFont(f);
				((FormuleViewer) currentObject).setDefaultFont(f);
				((FormuleViewer) currentObject).setColor(fgColor);
				
				if(regelBreedte == 0 || regelBreedte + ((FormuleViewer) currentObject).getWidth() + 4 <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte += ((FormuleViewer) currentObject).getWidth() + 4;
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte = ((FormuleViewer) currentObject).getWidth() + 4;
				}
			}
			else if (currentObject instanceof InteractionView)
			{	
				if(currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).isZwevend())
				{	zwevendeTekstVakken.add((TekstVakPanel) currentObject);
				}
				else
				{	if(regelBreedte == 0 || regelBreedte + ((InteractionView) currentObject).getWidth() <= tekstVakBreedte || pasAanB)
					{	regelVakken[aantalRegels - 1].addObject(currentObject);
						regelBreedte += ((InteractionView) currentObject).getWidth();
					}
					else
					{
						voegRegelToe();
						regelVakken[aantalRegels - 1].addObject(currentObject);
						regelBreedte = ((InteractionView) currentObject).getWidth();
					}
				}
				
				
				
			}
			else if (currentObject instanceof ImageView || currentObject instanceof IFrameView)
			{
				TekstElement iv = (TekstElement) currentObject;
				//Widget w = iv.getImage();
				if(regelBreedte == 0 || regelBreedte + iv.getWidth() <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte += iv.getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte = iv.getWidth();
				}
				iv.setAsHoogte(regelVakken[aantalRegels - 1].getTekstAsHoogte());
			}
			else if (currentObject instanceof AnchorView)
			{
				AnchorView av = (AnchorView) currentObject;
				String text = av.toString();
				double width = ctx.measureText(text).getWidth();
				if(regelBreedte == 0 || regelBreedte + width <= tekstVakBreedte || pasAanB)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					//regelBreedte += (int)width;
					regelBreedte += width;
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					//regelBreedte = (int)width;
					regelBreedte += width;
				}
			}
		}
		
		//regelvakken vullen. Zo krijgen ze ook de juiste maten.
		for(int i = 0; i < aantalRegels; i++)
		{
			regelVakken[i].bepaalAshoogte();
			regelVakken[i].vulRegel();
		}
		this.ashoogte = regelVakken[0].getAsHoogte();
		plaatsRegels(false);
	}
	
	public void voegRegelToe()
	{
		aantalRegels++;
		regelVakken[aantalRegels] = new TekstRegel(this);
		if(tekstVakBreedte >= 0)
			regelVakken[aantalRegels].setWidth(tekstVakBreedte + "px");
		regelVakken[aantalRegels].setHeight(font_size + 4 + "px"); //dit is nog een beetje willekeurig...
		//regelVakken[aantalRegels].setHeight(font_size + 5);
		regelVakken[aantalRegels].setFontStyle(font_style);
		regelVakken[aantalRegels].setFontName(font_name);
		regelVakken[aantalRegels].setFontSize(font_size);
		regelVakken[aantalRegels].setColor(fgColor);
		//regelVakken[aantalRegels].getElement().getStyle().setBackgroundColor(CssColor.make(20*aantalRegels, 255 - 20 * aantalRegels, 255).toString());
		regelVakken[aantalRegels].getElement().getStyle().setColor(fgColor.toString());
		
		//if(centerH)
		//	regelVakken[aantalRegels].getElement().getStyle().setTextAlign(TextAlign.CENTER);
		//flowVak.add(regelVakken[aantalRegels]);
	}
	
	
	public int getAsHoogte()
	{
		return ashoogte;
	}
	
	public void pasHoogteAanInhoudAan(boolean b)
	{
		FormuleFont fm = regelVakken[0].getFont();
		int regelafstand = fm.getAscent()+fm.getDescent()+interlinie;
		int regelHoogtes = 0;
		for(int i = 0; i < aantalRegels; i++)
		{
			if(b)
				regelVakken[i].bepaalAshoogte();
		
			int corr = 0;
        	if(i>0)corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
		   	regelHoogtes += regelVakken[i].getHeight() + corr;
			
		}
		//ashoogte = regelVakken[0].getAsHoogte();
		hoogte = 2 * bovenMarge + regelHoogtes;
		for(int i = 0; i < zwevendeTekstVakken.size(); i++)
		{
			TekstVakPanel panel = ((TekstVakPanel) zwevendeTekstVakken.get(i));
			hoogte = Math.max(hoogte, panel.getLocationY() + panel.getHoogte());
		}
		if(parent != null) 
		{
			hoogte = Math.max(hoogte, parent.getFirstRowMinHeight(this));
		}
		
	}
	
	public void setAshoogte(int ashoogte)
	{
		this.ashoogte = ashoogte;
		//regelVakken[0].setAsHoogte(ashoogte);
		//resize();
		plaatsRegels(true);
	}
	
	public int getRegelHoogte()
	{
		return regelVakken[0].getHeight();
	}
	
	public int getRegelBreedte()
	{
		return regelVakken[0].getWidth() + cellMarge;
	}
	
	public TekstRegel getRegelVak(int i)
	{
		return regelVakken[i];
	}
	
	public void plaatsRegels(boolean herplaats)
	{
		FormuleFont fm = regelVakken[0].getFont();
		int regelafstand = fm.getAscent()+fm.getDescent()+interlinie;
		int regelHoogtes = 0;
		
		for(int i=0 ; i<aantalRegels; i++)
        {   int corr = 0;
        	if(i>0)corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
		   	regelHoogtes += regelVakken[i].getHeight() + corr;
        }
		
		int vertPositie = bovenMarge + ashoogte - regelVakken[0].getAsHoogte(); //dit is nieuw (de toevoeging van ashoogte hier).
		if(centerV)
		{
			vertPositie = (hoogte - regelHoogtes) / 2;
		}
		
		for(int i = 0; i < aantalRegels; i++)
		{
			int horPositie = cellMarge + knopBreedte;
			if(centerH)
				horPositie += (int) (tekstVakBreedte - regelVakken[i].getWidth())/2;
			
			if(regelVakken[i].getParent() == null)
				this.add(regelVakken[i]);
			int corr = 0;
			if(i > 0)
				corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
			this.setWidgetLeftWidth(regelVakken[i], horPositie, Style.Unit.PX, regelVakken[i].getWidth(), Style.Unit.PX);
			this.setWidgetTopHeight(regelVakken[i], vertPositie + corr, Style.Unit.PX, regelVakken[i].getHeight(), Style.Unit.PX);
			vertPositie += regelVakken[i].getHeight() + corr;
			//flowVak.add(regelVakken[i]);
		}
		
		//zwevende tekstvakken toevoegen. (hoeft alleen eerste keer)
		if(!herplaats)
		{
			for(int i = 0; i < zwevendeTekstVakken.size(); i++)
			{
				TekstVakPanel panel = ((TekstVakPanel) zwevendeTekstVakken.get(i));
				Widget a = panel.asWidget();
				a.getElement().getStyle().setProperty("display", "inline-block");
				this.remove(a);
				this.add(a);
				this.setWidgetLeftWidth(a, panel.getLocationX(), Style.Unit.PX, panel.getBreedte(), Style.Unit.PX);
				this.setWidgetTopHeight(a, panel.getLocationY(), Style.Unit.PX, panel.getHoogte(), Style.Unit.PX);
				panel.setParent(this);
			}
		}
		
		
		ashoogte = regelVakken[0].getAsHoogte();

	}
	
	
	
	public void resize()
	{
		hoogte = 0;
		boolean opvulhoogteGecorrigeerd = corrigeerOpvulHoogte();
		
		for(int i=0 ; i<aantalRegels; i++)
	    {	if(opvulhoogteGecorrigeerd || contentUitklapbaar())
	    	{	regelVakken[i].bepaalAshoogte();	
	    		regelVakken[i].hervulRegel();
	    	}
			hoogte += regelVakken[i].getHeight();
	    }
		if(pasAanB)
		{
			tekstVakBreedte = regelVakken[0].getWidth();
			for(int i = 0; i < aantalRegels; i++)
				if(regelVakken[i].getWidth() > tekstVakBreedte)
					tekstVakBreedte = regelVakken[i].getWidth();
			breedte = (int) tekstVakBreedte + 2 * cellMarge + knopBreedte;
		}
		
		if(pasAanH)
		{	
			/*
			FormuleFont fm = regelVakken[0].getFont();
			int regelafstand = fm.getAscent()+fm.getDescent()+interlinie;
			int regelHoogtes = 0;
			
			for(int i=0 ; i<aantalRegels; i++)
	        {   int corr = 0;
	        	if(i>0)corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
			   	regelHoogtes += regelVakken[i].getHeight() + corr;
	        }
		
			hoogte = 2 * bovenMarge + regelHoogtes;
			for(int i = 0; i < zwevendeTekstVakken.size(); i++)
			{
				TekstVakPanel panel = ((TekstVakPanel) zwevendeTekstVakken.get(i));
				hoogte = Math.max(hoogte, panel.getLocationY() + panel.getHoogte());
			}
			*/
			pasHoogteAanInhoudAan(false);
		}
		if(parent != null) 
		{
			hoogte = Math.max(hoogte, parent.getFirstRowMinHeight(this));
		}
		setSize(breedte, hoogte); 
		
		plaatsRegels(true);
		
		if(parent != null)
		{	parent.setPositionUitklapButton(this);
			parent.resize();
		}
		
		/*
		//toegevoegde widgets zijn ofwel tekstregels, ofwel zwevende tekstvakken, of een in/uitklapknop
		//aan posities zwevende tekstvakken hoeft niets te gebeuren.
		//positie uitklapknop moet ik maar in TekstVakPanel regelen, dat is er hooguit één per tekstvakpanel.
		TekstRegel vorigeTekstRegel = null;
		for(int i = 0; i < this.getWidgetCount(); i++)
		{
			Widget w = this.getWidget(i);
			if(w instanceof TekstRegel)
			{
				int horPositie = cellMarge + knopBreedte;
				if(centerH)
					horPositie += (int) (tekstVakBreedte - ((TekstRegel) w).getWidth())/2;
				
				if(vorigeTekstRegel == null)
				{
					this.setWidgetLeftWidth((TekstRegel) w, horPositie, Style.Unit.PX, ((TekstRegel) w).getWidth(), Style.Unit.PX);
					this.setWidgetTopHeight((TekstRegel) w, vertPositie, Style.Unit.PX, ((TekstRegel) w).getHeight(), Style.Unit.PX);
					vertPositie += ((TekstRegel) w).getHeight();
				}
				else
				{
					int corr = Math.max(regelafstand-(vorigeTekstRegel.getHeight()-vorigeTekstRegel.getAsHoogte()+((TekstRegel) w).getAsHoogte()), 0);
					this.setWidgetLeftWidth((TekstRegel) w, horPositie, Style.Unit.PX, ((TekstRegel) w).getWidth(), Style.Unit.PX);
					this.setWidgetTopHeight((TekstRegel) w, vertPositie + corr, Style.Unit.PX, ((TekstRegel) w).getHeight(), Style.Unit.PX);
					vertPositie += ((TekstRegel) w).getHeight() + corr;
				}
				
				vorigeTekstRegel = (TekstRegel) w;
			}
		}
		
		ashoogte = regelVakken[0].getAsHoogte();
		*/
		
		
	}
	
	public void zetUitklapKnopLinks(int knopBreedte)
	{
		this.knopBreedte = knopBreedte;
		tekstVakBreedte = this.breedte - 2 * cellMarge - knopBreedte;
		for(int i = 0; i < this.getWidgetCount(); i++)
		{
			Widget w = this.getWidget(i);
			if(w instanceof TekstRegel)
			{
				int horPositie = cellMarge + knopBreedte;
				if(centerH)
					horPositie += (int) (tekstVakBreedte - ((TekstRegel) w).getWidth())/2;
				
				this.setWidgetLeftWidth((TekstRegel) w, horPositie, Style.Unit.PX, ((TekstRegel) w).getWidth(), Style.Unit.PX);
				
				
				
				/*
				int horPositie = cellMarge;
				if(centerH)
					horPositie += (int) (tekstVakBreedte - regelVakken[i].getWidth())/2;
				
				
				this.setWidgetLeftWidth(regelVakken[i], horPositie, Style.Unit.PX, regelVakken[i].getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(regelVakken[i], vertPositie, Style.Unit.PX, regelVakken[i].getHeight(), Style.Unit.PX);
				vertPositie += regelVakken[i].getHeight();
				*/
			}
		}
	}
	
	

	public int getAantalRegels() {
		return aantalRegels;
	}
	
	

	public int getHeight() {
		 
		return hoogte;
	}
	
	
	
	public int geefRestHoogte()
	{
		int regelafstand = font_size +interlinie;
		int actieveHoogte = 2 * bovenMarge + Math.max(regelafstand,regelVakken[0].getHeight());
		int corr = 0;
		for(int i=1 ; i<aantalRegels; i++)
	    {	
			corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
			actieveHoogte += regelVakken[i].getHeight() + corr;
		}
		return getHeight()-actieveHoogte;
	}
	
	public int geefOpgevuldeHoogte()
	{
		for(int i=0 ; i<opdrachtObjects.size() ; i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).vulHoogteMogelijk())
			{	return ((TekstVakPanel) currentObject).geefOpgevuldeHoogte();
			}
		}
		return 0;
	}
	
	public boolean corrigeerOpvulHoogte()
	{
		boolean correctieGedaan = false;
		for(int i=0 ; i<opdrachtObjects.size() ; i++)  
		{
			Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof TekstVakPanel)
			{
				TekstVakPanel tvp = (TekstVakPanel) currentObject;
				if(tvp.vulHoogteMogelijk())
				{	tvp.corrigeerRestHoogte();
					correctieGedaan = correctieGedaan || true;
				}
//				else
//				{
//					correctieGedaan = correctieGedaan || tvp.corrigeerOpvulHoogtes();
//							
//				}
			}
		}
		return correctieGedaan;
	}

	public boolean contentUitklapbaar()
	{
		boolean uitklapbaar = false;
		for(int i=0 ; i<opdrachtObjects.size() ; i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).isInklapbaar())
			{	uitklapbaar = uitklapbaar || true;
			}
		}
		return uitklapbaar;
	}
	
	/*
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCorrect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		// TODO Auto-generated method stub
		
	}
	*/
}
