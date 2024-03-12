package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.IFrameView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen.StelselAntwoordVak;
import nl.uu.fi.dwo.mobile.utils.PopupFacadeWithFont;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;




public class TekstVak extends LayoutPanel //implements InteractionView
{
	
	static Logger logger = Logger.getLogger("TekstVak");
	private TekstVakPanel parent;
	private int rij;
	private int kolom;
	private boolean zoom;
	private ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
	private ArrayList<Object> opdrachtObjectsForLayout = opdrachtObjects;
	private ArrayList<TekstVakPanel> zwevendeTekstVakken = new ArrayList<TekstVakPanel>();
	private int ashoogte;
	double tekstVakBreedte;
	
	boolean pasAanH = false;
	private boolean pasAanB = false;
	
	private boolean centerH = false;
	private boolean centerV = false;
	
	//private FlowPanel flowVak;
	private TekstRegel[] regelVakken;
	final LayoutPanel regelLayer = new LayoutPanel();
	//private VerticalPanel vPanel;
	protected int aantalRegels;
	
	private int cellMarge;
	private int bovenMarge;
	private int interlinie = 0;
	
	//voor als er een uitklapknop links in dit vak staat:
	private int knopBreedte = 0;
	
	private int font_size;
	private int font_style;
	private String font_name;
	private CssColor fgColor;
	public int hoogte = 0;
	public int breedte = 0;
	
	
	public TekstVak(TekstVakPanel parent, int rij, int kolom)
	{
		super();
		setStylePrimaryName(DWOplayer.DWO_BUNDLE.dwoplayercss().tekstvak());
		this.parent = parent;
		this.rij = rij;
		this.kolom = kolom;
		regelVakken = new TekstRegel[500];
		regelVakken[0] = new TekstRegel(this);
		regelVakken[1] = new TekstRegel(this);
		add(regelLayer); // parent of regelVakken
		
		aantalRegels = 1;
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
	

	public void zetOpdrachtObjects(ArrayList<Object> objects, ArrayList<Object> layout)
	{
		this.opdrachtObjects = objects;
		this.opdrachtObjectsForLayout = layout;
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
			regelVakken[i].setWidth(tekstVakBreedte , Unit.PX);
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
				regelVakken[i].setWidth(tekstVakBreedte , Unit.PX);
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
		if (zoom) tekstVakBreedte = b;
		else
		  tekstVakBreedte = b - 2 * cellMarge - knopBreedte;//hier stond - 2 bij. Die was ergens goed voor, maar levert ook problemen als het vak zijn breedte aanpast aan de inhoud.
		if(tekstVakBreedte >= 0 && h >= 0)
		{	//flowVak.setSize("" + tekstVakBreedte  + "px", "" + h + "px");
			for(int i = 0; i < aantalRegels  + 1; i++)
			{	regelVakken[i].setWidth(tekstVakBreedte , Unit.PX);
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
		add(regelLayer);
		
	}
	public void clearRegels() {
      for(int i = 0; i < regelVakken.length; i++)
      {
          if(regelVakken[i] == null)
              break;
          regelVakken[i].clearRegel();
      }
	  
	}
	
	
	public void setObjects(ArrayList<Object> opdrachtObjects)
	{
		this.opdrachtObjects = this.opdrachtObjectsForLayout = opdrachtObjects;
		
		aantalRegels = 1;
		
		double regelBreedte = 0;
		//ctx maken om tekst te kunnen meten. 
		Canvas canvas = Canvas.createIfSupported();
		Context2d ctx = canvas.getContext2d();
		String fontTypeString = "";
		if (font_style == 1)
			fontTypeString = "bold";
		else if (font_style == 2)
			fontTypeString = "italic";
		else if (font_style == 3)
			fontTypeString = "bold italic";
		
		if (fontTypeString.equals(""))
			ctx.setFont(font_size + "px " + font_name);
		else
			ctx.setFont(fontTypeString + " " + font_size + "px " + font_name);
		
		double spatieBreedte = ctx.measureText(" ").getWidth();
				
		//Voor alle objecten bepalen op welke regel ze terechtkomen, door breedtes te meten.
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof String)
			{
				String s = (String) currentObject;
				
				String sInRegel = "";
				double width = 0;
				//int stringBreedte = 0;
				if (i > 0 && opdrachtObjects.get(i - 1) instanceof String && s.length() > 0 && s.startsWith(" "))
				{
					s = s.substring(1);
				}
				//s = s.replaceAll("  ", " &nbsp;");
				//s = s.replaceAll("&nbsp; ", "&nbsp;&nbsp;");
				while (s.contains(" "))
				{	
					String sub = s.substring(0, s.indexOf(" ") + 1); 
					s = s.substring(s.indexOf(" ") + 1); 
					//int width = (int) Math.round(ctx.measureText(sub.substring(0, sub.length() - 1)).getWidth());
					String substring = sub.substring(0, sub.length() - 1);
					//double width = ctx.measureText(substring).getWidth();
					width = ctx.measureText(sInRegel + substring).getWidth();
					if (regelBreedte == 0)
					{
						regelBreedte = 2;
					}
					
					if (regelBreedte < 2 || regelBreedte + width  <= tekstVakBreedte || pasAanB)
					{
						regelVakken[aantalRegels - 1].addObject(substring);
						sInRegel = sInRegel + substring;
						//regelBreedte += width; regelBreedte = measureText van wat er nu in staat. Maar rekening houden met mogelijke ander vakjes..
						if (regelBreedte + width <= 2 || regelBreedte + width + spatieBreedte <= tekstVakBreedte || pasAanB)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							//regelBreedte += spatieBreedte;
							sInRegel = sInRegel + " ";
							width = ctx.measureText(sInRegel).getWidth();
						}
						else if (s.length() > 0)
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
						if (regelBreedte + width <= 2 || regelBreedte + width + spatieBreedte <= tekstVakBreedte)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							//regelBreedte += spatieBreedte;
							sInRegel = sInRegel + " ";
							width = ctx.measureText(sInRegel).getWidth();
						}
						else if (s.length() > 0)
						{
							voegRegelToe();
							regelBreedte = 0;
							sInRegel = "";
							width = 0;
						}
					}
				}
				if (s.length() > 0)//nu zitten er in s geen spaties meer. De rest nog proberen te plaatsen.
				{
					//double width = ctx.measureText(s).getWidth();
					width = ctx.measureText(sInRegel + s).getWidth();
/// XXX if regelbreedte=0 then +2 hier niet?
					if (regelBreedte < 2 || sInRegel.length() == 0 || regelBreedte + width  <= tekstVakBreedte || pasAanB)
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
			else if (currentObject instanceof InteractionView)
				((InteractionView) currentObject).zetVolledigeBreedte((int) tekstVakBreedte);
			
			else if (currentObject instanceof ImageView)
				((ImageView) currentObject).zetVolledigeBreedte((int) tekstVakBreedte);

			if (currentObject instanceof TekstElementWithFont) {
				TekstElementWithFont tmf = (TekstElementWithFont) currentObject;
				tmf.setFontSize(font_size);
				tmf.setFontName(font_name);
				tmf.setFontStyle(font_style);
				regelBreedte = setupTekstElement(regelBreedte, tmf, false);
				tmf.setParentRegel(regelVakken[aantalRegels-1]);
				
			} 
			else if (currentObject instanceof FormuleViewer)
			{	
				FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				if (!FormuleFont.formTimes)
					f.setFont(font_name);
				FormuleViewer formuleViewer = (FormuleViewer) currentObject;
				formuleViewer.setFont(f);
				formuleViewer.setDefaultFont(f);
				formuleViewer.setColor(fgColor);
				regelBreedte = setupTekstElement(regelBreedte, formuleViewer, false) + 4;
			}
			else if (currentObject instanceof InteractionView)
			{	
				if (currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).isZwevend())
				{	zwevendeTekstVakken.add((TekstVakPanel) currentObject);
				}
				else
				{	
					boolean isSymbol = currentObject instanceof SymboolPanel; 
					regelBreedte = setupTekstElement(regelBreedte, (InteractionView) currentObject, isSymbol);
				}
				
			}
			else if (currentObject instanceof ImageView || currentObject instanceof IFrameView)
			{
				TekstElement iv = (TekstElement) currentObject;
				regelBreedte = setupTekstElement(regelBreedte, iv, true);
			}
		}
		
		//regelvakken vullen. Zo krijgen ze ook de juiste maten.
		for (int i = 0; i < aantalRegels; i++)
		{
			regelVakken[i].bepaalAshoogte();
			regelVakken[i].vulRegel();
		}
		
		this.ashoogte = regelVakken[0].getAsHoogte();
		plaatsRegels(false);
	}

	private double setupTekstElement(double regelBreedte, TekstElement tmf, boolean as) {
		double width = tmf.getWidth();
		if( !(pasAanB || regelBreedte == 0 || regelBreedte + width <= tekstVakBreedte))
		{
			voegRegelToe();
			regelBreedte = 0;
		}
		TekstRegel tekstRegel = regelVakken[aantalRegels-1];
		tekstRegel.addObject(tmf);
		if(as)tmf.setAsHoogte(tekstRegel.getTekstAsHoogte());
		regelBreedte += width;
		return regelBreedte;
	}
	
	public void voegRegelToe()
	{
		aantalRegels++;
		regelVakken[aantalRegels] = new TekstRegel(this);
		if(tekstVakBreedte >= 0)
			regelVakken[aantalRegels].setWidth(tekstVakBreedte , Unit.PX);
		regelVakken[aantalRegels].setHeight(font_size + 4 , Unit.PX); //dit is nog een beetje willekeurig...
		//regelVakken[aantalRegels].setHeight(font_size + 5);
		regelVakken[aantalRegels].setFontStyle(font_style);
		regelVakken[aantalRegels].setFontName(font_name);
		regelVakken[aantalRegels].setFontSize(font_size);
		if (fgColor != null) {
			regelVakken[aantalRegels].setColor(fgColor);
			//regelVakken[aantalRegels].getElement().getStyle().setColor(fgColor.toString());
		}
		//regelVakken[aantalRegels].getElement().getStyle().setBackgroundColor(CssColor.make(20*aantalRegels, 255 - 20 * aantalRegels, 255).toString());
		
		//if(centerH)
		//	regelVakken[aantalRegels].getElement().getStyle().setTextAlign(TextAlign.CENTER);
		//flowVak.add(regelVakken[aantalRegels]);
	}
	
	
	public int getAsHoogte()
	{
		return ashoogte;
	}
	
	public void pasHoogteAanInhoudAan(boolean vanTekstVakPanel)
	{
		FormuleFont fm = regelVakken[0].getFont();
		int regelafstand = fm.getAscent()+fm.getDescent()+interlinie;
		int regelHoogtes = 0;
		//even de hoogte van alle tekstvakken met vulhoogte op 0 zetten, zorgen dat die later weer goed worden gezet.
		if(vanTekstVakPanel && opdrachtObjects != null)
		{
			for(int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object obj = opdrachtObjects.get(i);
				if(obj instanceof TekstVakPanel)
				{	TekstVakPanel tvp = (TekstVakPanel) obj;
					if(tvp.vulHoogteMogelijk())
						tvp.setCurrentSize(tvp.getWidth(), 0);
				}
				if(obj instanceof SymboolPanel)
				{
					SymboolPanel sp = (SymboolPanel) obj;
					sp.zetVolledigeHoogte(0);
				}
			}
			
		}
		for(int i = 0; i < aantalRegels; i++)
		{
			if(vanTekstVakPanel)
			{	regelVakken[i].bepaalAshoogte();
			}
		
			int corr = 0;
        	if(i>0)corr = Math.max(regelafstand-(regelVakken[i-1].getHeight()-regelVakken[i-1].getAsHoogte()+regelVakken[i].getAsHoogte()), 0);
		   	regelHoogtes += regelVakken[i].getHeight() + corr;
			
		}
		//ashoogte = regelVakken[0].getAsHoogte();
		hoogte = 2 * bovenMarge + regelHoogtes;
		for(int i = 0; i < zwevendeTekstVakken.size(); i++)
		{
			TekstVakPanel panel = (zwevendeTekstVakken.get(i));
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
			int horPositie = zoom ? 0 : cellMarge + knopBreedte;
			if(centerH)
				horPositie += (int) (tekstVakBreedte - regelVakken[i].getWidth())/2;
			
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
				TekstVakPanel panel =  zwevendeTekstVakken.get(i);
				Widget a = panel.asWidget();
				a.getElement().getStyle().setProperty("display", "inline-block");
				if(a.getParent() != this) {
					this.add(a);
				} else { 
//					a.removeFromParent();
//					this.add(a); // herplaats aan de top.
				}
				this.setWidgetLeftWidth(a, panel.getLocationX(), Style.Unit.PX, panel.getBreedte(), Style.Unit.PX);
				this.setWidgetTopHeight(a, panel.getLocationY(), Style.Unit.PX, panel.getHoogte(), Style.Unit.PX);
				panel.setParent(this);
			}
		}
		
		
		ashoogte = regelVakken[0].getAsHoogte();

	}

  private void setWidgetTopHeight(TekstRegel tekstRegel, int top, Unit px, int height, Unit px2) {
    tekstRegel.setHeight(height, px2);
    tekstRegel.setY(top); 
  }

  private void setWidgetLeftWidth(TekstRegel tekstRegel, int left, Unit px, int width,
      Unit px2) {
    tekstRegel.setWidth(width, px2);
    tekstRegel.setX(left);
  }

  /*
	 * Verplaats focus naar eerstvolgende antwoordvak (FormuleEditorWithAnswer of AntwoordTekstVak)
	 * Geeft true als de focus is verplaatst naar een antwoordvak in dit tekstvak en false anders.
	 * Source is de zender van het tabcommando, up is true als de source zich binnen dit tekstvak bevindt en false anders. 
	 */
	public boolean tabFocus(InteractionView source, boolean up)
	{
		int start = 0;
		boolean focusVerlegd = false;
		
		//if tekstVak is invisible, do not move focus to anything inside tekstVak
		if(!isVisible())
			return false;
		//if source outside TekstVak: start searching at 0.
		//if source inside TekstVak: start searching at next OpdrachtObject
		if(up)
		{
			for(int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object object = opdrachtObjects.get(i);
				if(object.equals(source))
				{	start = i + 1;
					break;
				}
				else if(object instanceof PopupFacadeWithFont)
				{
					PopupFacadeWithFont facade = (PopupFacadeWithFont) object;
					if(facade.getDelegate().equals(source))
					{
						start = i + 1;
						break;
					}
				}
			}
		}
		
		for(int i = start; i < opdrachtObjects.size(); i++)
		{
			Object object = opdrachtObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{
				FormuleEditorWithAnswer fewa = (FormuleEditorWithAnswer) object;
				if(!fewa.isPopup() && !fewa.isReadOnly())
				{
					fewa.requestFocus();
					//om te zorgen dat cursor ook getekend wordt:
					if(fewa.getCurrentElement() == null)
					{	fewa.setCurrentElementRepaint(fewa.getCurrentRegel());
						//zorgen dat cursor netjes aan eind vakje staat.
						fewa.cursorToLeft();
						fewa.cursorToRight();
					}
					return true;
				}
			}
			else if(object instanceof AntwoordTekstVak)
			{
				AntwoordTekstVak atv = (AntwoordTekstVak) object;
				if(!atv.isPopup())
				{
					atv.requestFocus();
					if(atv.heeftFormuleInvoer())
						atv.tekenCursor();
					return true;
				}
				
			}
			else if(object instanceof AntwoordTekstVak2)
			{
				AntwoordTekstVak2 atv = (AntwoordTekstVak2) object;
				if(!atv.isPopup() && !atv.isReadOnly())
				{
					atv.requestFocus();
					if(atv.heeftFormuleInvoer())
						atv.tekenCursor();
					return true;
				}
				
			}
			else if(object instanceof FormuleEditorWithSteps)
			{
				FormuleEditorWithSteps fews = (FormuleEditorWithSteps) object;
				if(!fews.isPopup() && !fews.isReadOnly())
				{
					if(fews.getEditor() != null)
					{	fews.getEditor().requestFocus();
						if(fews.getEditor().getCurrentElement() == null)
						{	fews.getEditor().setCurrentElementRepaint(fews.getEditor().getMainRegel());
							//zorgen dat cursor netjes aan eind vakje staat.
							fews.getEditor().cursorToLeft();
							fews.getEditor().cursorToRight();
						}
						return true;
					}
				}
			}
			else if(object instanceof PopupFacadeWithFont && ((PopupFacadeWithFont) object).getDelegate() instanceof TextEditor)
			{
				PopupFacadeWithFont facade = (PopupFacadeWithFont) object;
				TextEditor textEditor = (TextEditor) facade.getDelegate();
				if(!textEditor.isReadOnly() && !facade.isPopup()) 
					textEditor.requestFocus();
				return true;
			}
			else if(object instanceof StelselAntwoordVak)
			{
				StelselAntwoordVak sav = (StelselAntwoordVak) object;
				sav.requestFocus();
				if(sav.isRekenVakZichtbaar())
				{	FormuleEditor editor = sav.getRekenVak().geefHoofdEditor().getEditor();
					if(editor != null && editor.getCurrentElement() == null)
					{
						editor.setCurrentElementRepaint(editor.getMainRegel());
					}
				}
				return true;
			}
			else if(object instanceof TekstVakPanel)
			{
				TekstVakPanel tvp = (TekstVakPanel) object;
				if(!tvp.isPopup())
				{
					focusVerlegd = tvp.tabFocus(this, false);
					if(focusVerlegd)
						return true;
				}
			}
		}
		
		//hier gekomen heb je alles binnen het TekstVak bekeken en niets gevonden, dus naar volgende tekstvak binnen tekstvakpanel.
		return parent.tabFocus(this, true);
			
	}
	
	
	/*
	 * Verplaats focus naar laatste antwoordvak (FormuleEditorWithAnswer of AntwoordTekstVak) voor dit vak. 
	 * Geeft true als de focus is verplaatst naar een antwoordvak in dit tekstvak en false anders.
	 * Source is de zender van het tabcommando, up is true als de source zich binnen dit tekstvak bevindt en false anders. 
	 */
	public boolean shiftTabFocus(InteractionView source, boolean up)
	{
		int start = opdrachtObjects.size() - 1;
		boolean focusVerlegd = false;
		
		//if tekstVak is invisible, do not move focus to anything inside tekstVak
		if(!isVisible())
			return false;
				
		//if source outside TekstVak: start searching at 0.
		//if source inside TekstVak: start searching at next OpdrachtObject
		if(up)
		{
			for(int i = opdrachtObjects.size() - 1; i >= 0; i--)
			{
				Object object = opdrachtObjects.get(i);
				if(object.equals(source))
				{	start = i - 1;
					break;
				}
				else if(object instanceof PopupFacadeWithFont)
				{
					PopupFacadeWithFont facade = (PopupFacadeWithFont) object;
					if(facade.getDelegate().equals(source))
					{
						start = i - 1;
						break;
					}
				}
			}
		}
		
		for(int i = start; i >= 0; i--)
		{
			Object object = opdrachtObjects.get(i);
			if(object instanceof FormuleEditorWithAnswer)
			{
				FormuleEditorWithAnswer fewa = (FormuleEditorWithAnswer) object;
				if(!fewa.isPopup() && !fewa.isReadOnly())
				{
					fewa.requestFocus();
					//om te zorgen dat cursor ook getekend wordt:
					if(fewa.getCurrentElement() == null)
					{	fewa.setCurrentElementRepaint(fewa.getMainRegel());
						//zorgen dat cursor netjes aan eind vakje staat.
						fewa.cursorToLeft();
						fewa.cursorToRight();
					}
					return true;
				}
			}
			else if(object instanceof AntwoordTekstVak)
			{
				AntwoordTekstVak atv = (AntwoordTekstVak) object;
				if(!atv.isPopup())
				{
					atv.requestFocus();
					if(atv.heeftFormuleInvoer())
						atv.tekenCursor();
					return true;
				}
				
			}
			else if(object instanceof AntwoordTekstVak2)
			{
				AntwoordTekstVak2 atv = (AntwoordTekstVak2) object;
				if(!atv.isPopup() && !atv.isReadOnly())
				{
					atv.requestFocus();
					if(atv.heeftFormuleInvoer())
						atv.tekenCursor();
					return true;
				}
				
			}
			else if(object instanceof FormuleEditorWithSteps)
			{
				FormuleEditorWithSteps fews = (FormuleEditorWithSteps) object;
				if(!fews.isPopup() && !fews.isReadOnly())
				{
					if(fews.getEditor() != null)
					{	fews.getEditor().requestFocus();
						if(fews.getEditor().getCurrentElement() == null)
						{	fews.getEditor().setCurrentElementRepaint(fews.getEditor().getMainRegel());
							//zorgen dat cursor netjes aan eind vakje staat.
							fews.getEditor().cursorToLeft();
							fews.getEditor().cursorToRight();
						}
						return true;
					}
				}
			}
			else if(object instanceof PopupFacadeWithFont && ((PopupFacadeWithFont) object).getDelegate() instanceof TextEditor)
			{
				PopupFacadeWithFont facade = (PopupFacadeWithFont) object;
				TextEditor textEditor = (TextEditor) facade.getDelegate();
				if(!textEditor.isReadOnly() && !facade.isPopup()) 
					textEditor.requestFocus();
				return true;
			}
			else if(object instanceof StelselAntwoordVak)
			{
				StelselAntwoordVak sav = (StelselAntwoordVak) object;
				sav.requestFocus();
				if(sav.isRekenVakZichtbaar())
				{	FormuleEditor editor = sav.getRekenVak().geefHoofdEditor().getEditor();
					if(editor != null && editor.getCurrentElement() == null)
					{
						editor.setCurrentElementRepaint(editor.getMainRegel());
					}
				}
				return true;
			}
			else if(object instanceof TekstVakPanel)
			{
				TekstVakPanel tvp = (TekstVakPanel) object;
				if(!tvp.isPopup())
				{
					focusVerlegd = tvp.shiftTabFocus(this, false);
					if(focusVerlegd)
						return true;
				}
			}
		}
		
		//hier gekomen heb je alles binnen het TekstVak bekeken en niets gevonden, dus naar vorige tekstvak binnen tekstvakpanel.
		return parent.shiftTabFocus(this, true);
	}
	
	public void resize()
	{
		hoogte = 0;
		//boolean opvulhoogteGecorrigeerd = corrigeerOpvulHoogte();
		
		for(int i=0 ; i<aantalRegels; i++)
	    {	if(contentUitklapbaar())// || opvulhoogteGecorrigeerd
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
		for(int i = 0; i < aantalRegels; i++)
		{
			TekstRegel w = getRegelVak(i);
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
	
	public void corrigeerOpvulHoogte()
	{
		if(opdrachtObjects != null) // NPE voorkomen, why?
		{	for(int i=0 ; i<opdrachtObjects.size() ; i++)  
			{
				Object currentObject = opdrachtObjects.get(i);
				if(currentObject instanceof TekstVakPanel)
				{
					TekstVakPanel tvp = (TekstVakPanel) currentObject;
					if(tvp.vulHoogteMogelijk())
					{	tvp.corrigeerRestHoogte(geefRestHoogte());
						for(int j = 0; j < aantalRegels; j++)
						{
							ArrayList<Object> regelList = getRegelVak(j).getRegelObjects();
							if(regelList.contains(currentObject))
							{	
								getRegelVak(j).setHeight(tvp.getHoogte());
								break;
							}
						}
						plaatsRegels(true);
					}
					else
						tvp.corrigeerOpvulHoogtes();
				}
			}
		}
	}
	
	public void vulSymboolHoogtes()
	{
		if(opdrachtObjects != null)
		{
			for(int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object currentObject = opdrachtObjects.get(i);
				if(currentObject instanceof SymboolPanel)
				{	((SymboolPanel) currentObject).zetVolledigeHoogte(this.hoogte);
					
					for(int j = 0; j < aantalRegels; j++)
					{
						ArrayList<Object> regelList = getRegelVak(j).getRegelObjects();
						if(regelList.contains(currentObject))
						{	
							//getRegelVak(j).setHeight(this.hoogte);
							if(getRegelVak(j).getHeight() < ((SymboolPanel) currentObject).getHeight())
								getRegelVak(j).setHeight(((SymboolPanel) currentObject).getHeight());
							getRegelVak(j).hervulRegel();
							break;
						}
					}
				
//					getRegelVak(0).setHeight(this.hoogte);
//					getRegelVak(0).hervulRegel();
					plaatsRegels(true);
				}
				else if(currentObject instanceof TekstVakPanel)
				{
					((TekstVakPanel) currentObject).vulSymboolHoogtes();
				}
				
			}
			
		}
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

  public void reLayout() {
    clearRegels();
    zwevendeTekstVakken.clear();
// keep subset
    ArrayList<Object> all = opdrachtObjects;
    ArrayList<Object> layout = opdrachtObjectsForLayout;
    setObjects(opdrachtObjectsForLayout); 
    zetOpdrachtObjects(all, layout);
    regelLayer.forceLayout();
    resize();
  }

	public void zoom(TekstVakPanel tekstVakPanel) {
	    zoomLayout(tekstVakPanel);    
		
		parent.zoom(this, rij, kolom);
//		// relayout
//		clearRegels();
//		zwevendeTekstVakken.clear();
//	    all = getOpdrachtObjects();
//		all.stream().filter(t -> t != tekstVakPanel && (t instanceof IsWidget))
//			.forEach( item -> 
//			regelLayer.setWidgetVisible(((IsWidget) item).asWidget(), false)
//		);
//		single = new ArrayList<>(); single.add(tekstVakPanel);
//		setObjects(single);
//		zetOpdrachtObjects(all, single);
		//resize();
	}

	public void unzoom(TekstVakPanel tekstVakPanel) {
		zoom = false;
		parent.unzoom(this, rij, kolom);
		// relayout
		clearRegels();
		zwevendeTekstVakken.clear();
		ArrayList<Object> all = getOpdrachtObjects();
		all.stream().filter(t -> t != tekstVakPanel && (t instanceof IsWidget))
			.forEach( item -> 
			regelLayer.setWidgetVisible(((IsWidget) item).asWidget(), true)
		);
		setObjects(all);
		zetOpdrachtObjects(all, all);
		resize();
	}

  public void zoom1(TekstVakPanel tekstVakPanel, int width) {
    zoom(tekstVakPanel);
    TekstVakPanel t = tekstVakPanel;
    while (t != null) { 
      t.zetVolledigeBreedte2(width); 
      TekstVak tv = t.parent;
      t = tv == null ? null : tv.parent; }
  }

  public void zoomLayout(TekstVakPanel tekstVakPanel) {
    zoom = true;
    ArrayList<Object> single = new ArrayList<>(); single.add(tekstVakPanel);
    ArrayList<Object> all = getOpdrachtObjects();
    zetOpdrachtObjects(all, single);
    all.stream().filter(t -> t != tekstVakPanel && (t instanceof IsWidget))
    .forEach( item -> 
        regelLayer.setWidgetVisible(((IsWidget) item).asWidget(), false)
    );
    reLayout();
  }

	
}
