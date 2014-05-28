package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
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

import fi.antwoordkeuzevakgwt.client.AntwoordKeuzeVakGWT;

public class TekstVak extends LayoutPanel //implements InteractionView
{
	
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
	
	private int font_size;
	private int font_style;
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
		//regelVakken[0].getElement().getStyle().setBackgroundColor(CssColor.make(0, 255, 255).toString());
		//regelVakken[1].getElement().getStyle().setBackgroundColor(CssColor.make(200, 135, 255).toString());
		aantalRegels = 1;
		//vPanel = new VerticalPanel();
		//vPanel.add(flowVak);
		//vPanel.setHeight("100%");
		
		//this.add(vPanel);
		//this.setWidgetLeftRight(vPanel, 0, Unit.PX, 0, Unit.PX);
		//this.setWidgetTopBottom(vPanel, 0, Unit.PX, 0, Unit.PX);
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
			regelVakken[i].getElement().getStyle().setColor(color.toString());
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
		tekstVakBreedte = breedte - 2  * cellMarge;//hier stond - 2 bij. Die was ergens goed voor, maar levert ook problemen als het vak zijn breedte aanpast aan de inhoud.
		if(tekstVakBreedte >= 0)
		{	//if(hoogte > 0)
			//	flowVak.setSize(tekstVakBreedte + "px", "" + hoogte + "px");
			for(int i = 0; i < aantalRegels  + 1; i++)
				regelVakken[i].setWidth(tekstVakBreedte + "px");
		}
		//vPanel.getElement().getStyle().setProperty("margin", "" + (bovenMarge  + 1)  + "px " + cellMarge + "px");
	}
	
	public void setSize(int b, int h)
	{
		this.breedte = b;
		this.hoogte = h;
		tekstVakBreedte = b - 2 * cellMarge;//hier stond - 2 bij. Die was ergens goed voor, maar levert ook problemen als het vak zijn breedte aanpast aan de inhoud.
		if(tekstVakBreedte >= 0 && h >= 0)
		{	//flowVak.setSize("" + tekstVakBreedte  + "px", "" + h + "px");
			for(int i = 0; i < aantalRegels  + 1; i++)
			{	regelVakken[i].setWidth(tekstVakBreedte + "px");
				//TODO gaat dit goed met centreren?
			}
		}
		if(h > 0)
			this.setSize("" + b + "px", "" + h + "px");
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
	
	public void setObjects(ArrayList<Object> opdrachtObjects)
	{
		this.opdrachtObjects = opdrachtObjects;
		
		aantalRegels = 1;
		
		int regelBreedte = 0;
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
			ctx.setFont(font_size + "px sans-serif");
		else
			ctx.setFont(fontTypeString + " " + font_size + "px sans-serif");
		
		double spatieBreedte = ctx.measureText(" ").getWidth();
				
		//Voor alle objecten bepalen op welke regel ze terechtkomen, door breedtes te meten.
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if(currentObject instanceof String)
			{
				String s = (String) currentObject;
				if(i > 0 && opdrachtObjects.get(i - 1) instanceof String && s.length() > 0 && s.startsWith(" "))
				{
					s = s.substring(1);
				}
				s = s.replaceAll("  ", " &nbsp;");
				s = s.replaceAll("&nbsp; ", "&nbsp;&nbsp;");
				
				while(s.contains(" "))
				{	String sub = s.substring(0, s.indexOf(" ") + 1); 
					s = s.substring(s.indexOf(" ") + 1); 
					double width = ctx.measureText(sub.substring(0, sub.length() - 1)).getWidth();
					//if(regelBreedte == 0)
					//	regelBreedte = 2;
					if(regelBreedte == 0 || regelBreedte + width  <= tekstVakBreedte)
					{
						regelVakken[aantalRegels - 1].addObject(sub.substring(0, sub.length() - 1));
						regelBreedte += width;
						if(regelBreedte == 0 || regelBreedte + spatieBreedte <= tekstVakBreedte)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							regelBreedte += spatieBreedte;
						}
						else if(s.length() > 0)
						{
							voegRegelToe();
							regelBreedte = 0;
						}
					}
					else
					{
						voegRegelToe();
						regelVakken[aantalRegels - 1].addObject(sub.substring(0, sub.length() - 1));
						regelBreedte = (int) width;
						if(regelBreedte == 0 || regelBreedte + spatieBreedte <= tekstVakBreedte)
						{
							regelVakken[aantalRegels - 1].addObject(" ");
							regelBreedte += spatieBreedte;
						}
						else if(s.length() > 0)
						{
							voegRegelToe();
							regelBreedte = 0;
						}
					}
				}
				if(s.length() > 0)//nu zitten er in s geen spaties meer. De rest nog proberen te plaatsen.
				{
					double width = ctx.measureText(s).getWidth();
					if(regelBreedte == 0 || regelBreedte + width  <= tekstVakBreedte)
					{
						regelVakken[aantalRegels - 1].addObject(s);
						regelBreedte += width;
					}	
					else
					{
						voegRegelToe();
						regelVakken[aantalRegels - 1].addObject(s);
						regelBreedte = (int) width;
					}
				}
				//nodig om nieuwe regels te krijgen bij enter. De tweede voorwaarde is een (enigszins) kunstmatige oplossing 
				//om laatste regel (die in wiskopdr niet bestaat) niet te maken, maar anders gaat verticaal centreren niet goed.
				
				
				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String
						&& !(opdrachtObjects.size() == i + 2 && opdrachtObjects.get(i + 1).toString().equals(" ")))
				{	
					voegRegelToe();
					regelBreedte = 0;
						
				
				}
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				//((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				//((FormuleEditorWithAnswer) currentObject).setColor(fgColor);
				if(regelBreedte == 0 || regelBreedte + ((FormuleEditorWithAnswer) currentObject).getWidth() <= tekstVakBreedte)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					((FormuleEditorWithAnswer) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					regelBreedte += ((FormuleEditorWithAnswer) currentObject).getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					//((FormuleEditorWithAnswer) currentObject).setParentRegel(regelVakken[aantalRegels - 1]);
					regelBreedte = ((FormuleEditorWithAnswer) currentObject).getWidth();
				}
			}
			else if (currentObject instanceof FormuleViewer)
			{	FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				((FormuleViewer) currentObject).setFont(f);
				((FormuleViewer) currentObject).setColor(fgColor);
				
				if(regelBreedte == 0 || regelBreedte + ((FormuleViewer) currentObject).getWidth() + 4 <= tekstVakBreedte)
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
				{	
					if(regelBreedte == 0 || regelBreedte + ((InteractionView) currentObject).getWidth() <= tekstVakBreedte)
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
				if(currentObject instanceof PopupFacade && ((PopupFacade) currentObject).getDelegate() instanceof AntwoordKeuzeVakGWT)
					{	//System.out.println("instance of antwoordkeuzevak, ashoogte wordt: " + (regelVakken[aantalRegels - 1].getTekstAsHoogte() + 20));
					((PopupFacade) currentObject).getDelegate().setAsHoogte(regelVakken[aantalRegels - 1].getTekstAsHoogte() + 4);
				}
				
				
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				//Widget w = iv.getImage();
				if(regelBreedte == 0 || regelBreedte + iv.getWidth() <= tekstVakBreedte)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte += iv.getWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte = iv.getWidth();
				}
				iv.setAsHoogte(regelVakken[aantalRegels - 1].getTekstAsHoogte() + 4);
			}
			else if (currentObject instanceof AnchorView)
			{
				AnchorView av = (AnchorView) currentObject;
				Widget w = av.asWidget();
				if(regelBreedte == 0 || regelBreedte + w.getOffsetWidth() <= tekstVakBreedte)
				{	regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte += w.getOffsetWidth();
				}
				else
				{
					voegRegelToe();
					regelVakken[aantalRegels - 1].addObject(currentObject);
					regelBreedte = w.getOffsetWidth();
				}
			}
		}
		
		//regelvakken vullen. Zo krijgen ze ook de juiste maten.
		for(int i = 0; i < aantalRegels; i++)
		{
			regelVakken[i].bepaalAshoogte();
			regelVakken[i].vulRegel();
		}
		
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
		regelVakken[aantalRegels].setFontSize(font_size);
		regelVakken[aantalRegels].setFontStyle(font_style);
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
	
	public void pasHoogteAanInhoudAan()
	{
		int regelHoogtes = 0;
		for(int i = 0; i < aantalRegels; i++)
		{
			regelVakken[i].bepaalAshoogte();
			regelHoogtes += regelVakken[i].getHeight();
		}
		ashoogte = regelVakken[0].getAsHoogte();
		hoogte = 2 * bovenMarge + regelHoogtes;
	}
	
	public void setAshoogte(int ashoogte)
	{
		this.ashoogte = ashoogte;
		regelVakken[0].setAsHoogte(ashoogte);
		plaatsRegels(true);
	}
	
	public int getRegelHoogte()
	{
		return regelVakken[0].getHeight();
	}
	
	public int getRegelBreedte()
	{
		return regelVakken[0].getWidth();
	}
	
	public TekstRegel getRegelVak(int i)
	{
		return regelVakken[i];
	}
	
	public void plaatsRegels(boolean herplaats)
	{
		//this.clear();
		//regelVakken toevoegen op juiste posities.
		int vertPositie = bovenMarge - 1;
		if(centerV)
		{
			int regelHoogtes = 0;
			for(int j = 0; j < aantalRegels; j++)
				regelHoogtes += regelVakken[j].getHeight();
			vertPositie += (hoogte - 2 * bovenMarge - regelHoogtes) / 2;
		}
		
		for(int i = 0; i < aantalRegels; i++)
		{
			int horPositie = cellMarge;
			if(centerH)
				horPositie += (int) (tekstVakBreedte - regelVakken[i].getWidth())/2;
			
			if(!herplaats)
				this.add(regelVakken[i]);
			this.setWidgetLeftWidth(regelVakken[i], horPositie, Style.Unit.PX, regelVakken[i].getWidth(), Style.Unit.PX);
			this.setWidgetTopHeight(regelVakken[i], vertPositie, Style.Unit.PX, regelVakken[i].getHeight(), Style.Unit.PX);
			vertPositie += regelVakken[i].getHeight();// + interlinie; nog implementeren
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
				this.setWidgetLeftWidth(a, panel.getLocationX(), Style.Unit.PX, 
						panel.getBreedte(), Style.Unit.PX);
				this.setWidgetTopHeight(a, panel.getLocationY(), Style.Unit.PX, 
						panel.getHoogte(), Style.Unit.PX);
				panel.setParent(this);
			}
		}
		
		
		ashoogte = regelVakken[0].getAsHoogte();

	}
	
	public void resize()
	{
		if(pasAanB)
		{
			tekstVakBreedte = regelVakken[0].getWidth();
			for(int i = 0; i < aantalRegels; i++)
				if(regelVakken[i].getWidth() > tekstVakBreedte)
					tekstVakBreedte = regelVakken[i].getWidth();
			breedte = (int) tekstVakBreedte + 2 * cellMarge;
		}
		if(pasAanH)
		{
			int regelHoogtes = 0;
			for(int j = 0; j < aantalRegels; j++)
				regelHoogtes += regelVakken[j].getHeight();
			hoogte = 2 * bovenMarge + regelHoogtes;
		}
		setSize(breedte, hoogte); //even kijken of dit niet voor oneindige loop zorgt..
		
		int vertPositie = bovenMarge;
		if(centerV)
		{
			int regelHoogtes = 0;
			for(int j = 0; j < aantalRegels; j++)
				regelHoogtes += regelVakken[j].getHeight();
			vertPositie += (hoogte - 2 * bovenMarge - regelHoogtes) / 2;
		}
		
		//toegevoegde widgets zijn ofwel tekstregels, ofwel zwevende tekstvakken, of een in/uitklapknop
		//aan posities zwevende tekstvakken hoeft niets te gebeuren.
		//positie uitklapknop moet ik maar in TekstVakPanel regelen, dat is er hooguit één per tekstvakpanel.
		for(int i = 0; i < this.getWidgetCount(); i++)
		{
			Widget w = this.getWidget(i);
			if(w instanceof TekstRegel)
			{
				int horPositie = cellMarge;
				if(centerH)
					horPositie += (int) (tekstVakBreedte - regelVakken[i].getWidth())/2;
				this.setWidgetLeftWidth(regelVakken[i], horPositie, Style.Unit.PX, regelVakken[i].getWidth(), Style.Unit.PX);
				this.setWidgetTopHeight(regelVakken[i], vertPositie, Style.Unit.PX, regelVakken[i].getHeight(), Style.Unit.PX);
				vertPositie += regelVakken[i].getHeight();
			}
		}
		
		ashoogte = regelVakken[0].getAsHoogte();
		
		parent.resize();
	}

	public int getAantalRegels() {
		return aantalRegels;
	}
	
	

	/*
	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

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
