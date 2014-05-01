package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;

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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TekstVak extends LayoutPanel //implements InteractionView
{
	
	private TekstVakPanel parent;
	private int rij;
	private int kolom;
	private ArrayList<Object> opdrachtObjects;
	private int ashoogte;
	private int tekstAshoogte;
	
	private FlowPanel flowVak;
	private VerticalPanel vPanel;
	
	private int cellMarge;
	
	private int font_size;
	private int font_style;
	private CssColor fgColor;
	int hoogte = 0;
	
	
	public TekstVak(TekstVakPanel parent, int rij, int kolom)
	{
		super();
		this.parent = parent;
		this.rij = rij;
		this.kolom = kolom;
		flowVak = new FlowPanel();
		flowVak.getElement().getStyle().setProperty("lineHeight", "1.2");
		vPanel = new VerticalPanel();
		vPanel.add(flowVak);
		vPanel.setHeight("100%");
		
		this.add(vPanel);
		this.setWidgetLeftRight(vPanel, 0, Unit.PX, 0, Unit.PX);
		this.setWidgetTopBottom(vPanel, 0, Unit.PX, 0, Unit.PX);
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
	
	public FlowPanel getFlowPanel()
	{
		return flowVak;
	}
	
	public void setColor(CssColor color)
	{
		this.fgColor = color;
		flowVak.getElement().getStyle().setColor(color.toString());
	}
	
	public void setFontSize(int font_size)
	{
		this.font_size = font_size;
		flowVak.getElement().getStyle().setFontSize(font_size, Unit.PX);
		tekstAshoogte = (font_size  - 2) / 2;
		//ascent = ;
		//descent = ;
	}
	
	public void setFontStyle(int font_style)
	{
		this.font_style = font_style;
		flowVak.getElement().getStyle().setFontStyle(font_style == 2 || font_style == 3 ? FontStyle.ITALIC : FontStyle.NORMAL);
		flowVak.getElement().getStyle().setFontWeight(font_style == 1 || font_style == 3 ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
	}
	
	public void setRonding(int ronding)
	{
		flowVak.getElement().getStyle().setProperty("borderRadius", (ronding / 2) + "px");
	}
	
	public void setCentering(boolean centerH, boolean centerV)
	{
		if(centerH)
			flowVak.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		if(centerV)
			vPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	public void setTekstVakBreedte(double tekstVakBreedte)
	{
		flowVak.setWidth(tekstVakBreedte + "px");
	}
	
	public void setMarges(int bovenMarge, int cellMarge)
	{
		this.cellMarge = cellMarge;
		vPanel.getElement().getStyle().setProperty("margin", "" + bovenMarge + "px " + cellMarge + "px");
	}
	
	public void setSize(int b, int h)
	{
		flowVak.setSize("" + (b - - 2 * cellMarge)  + "px", "" + h + "px");
		this.setSize("" + b + "px", "" + h + "px");
	}
	
	public void setObjects(ArrayList<Object> opdrachtObjects)
	{
		this.opdrachtObjects = opdrachtObjects;//niet per se nodig, want is al gedaan..
		//eerst fonts in formuleEditors en formuleViewers goed zetten, zodat ashoogtes goed worden berekend.
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof FormuleEditorWithAnswer)
			{
				((FormuleEditorWithAnswer) currentObject).setFont(FormuleFont.createFromFontSize(font_size));
				((FormuleEditorWithAnswer) currentObject).setColor(fgColor);
			}
			else if (currentObject instanceof FormuleViewer)
			{	
				FormuleFont f = FormuleFont.createFromFontSize(font_size);
				f.setBold(font_style == 1 || font_style == 3);
				((FormuleViewer) currentObject).setFont(f);
				((FormuleViewer) currentObject).setColor(fgColor);
			}
		}
		bepaalAshoogte();
		int verschuiving = hoogte - ashoogte;
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			int objectVerschuiving = 0;
			if(currentObject instanceof TekstElement)
				objectVerschuiving = ((TekstElement) currentObject).getHeight()-((TekstElement) currentObject).getAsHoogte();
			if(currentObject instanceof String)
			{
				currentObject = ((String) currentObject).replaceAll("  ", " &nbsp;");
				currentObject = ((String) currentObject).replaceAll("&nbsp; ", "&nbsp;&nbsp;");
				Element element = DOM.createSpan();
				element.setInnerHTML((String) currentObject);
				//element.getStyle().setProperty("verticalAlign", "top");
				System.out.println("tekst = " + currentObject + ", vertical align tekst: " + (verschuiving - (font_size - tekstAshoogte)));
				element.getStyle().setProperty("verticalAlign", "" + (verschuiving - (font_size - tekstAshoogte)) + "px");
				//element.getStyle().setProperty("verticalAlign", "" + 0 + "px");
				
				flowVak.getElement().appendChild(element);

				if (opdrachtObjects.size() > i + 1 && opdrachtObjects.get(i + 1) instanceof String)
				{	flowVak.getElement().appendChild(DOM.createElement("br"));
					
				//niet zo: 
				//destination.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
				//want dan krijgt het hele vak een marge van 2; tekstvakken worden dan dus ook 2 px naar rechts geduwd.
				}
			}
			else if (currentObject instanceof FormuleEditorWithAnswer)
			{
				TouchPanel tp = (TouchPanel) ((FormuleEditorWithAnswer) currentObject).getAsPanel();
				tp.getElement().getStyle().setProperty("display", "inline-block");
				parent.getKeyboard().setEditor(((FormuleEditorWithAnswer) currentObject));
				parent.addFormulePanelListeners(tp, ((FormuleEditorWithAnswer) currentObject));

				tp.getElement().getStyle().setProperty("display", "inline-block");
				//tp.getElement().getStyle().setProperty("verticalAlign", "top");
				//tp.getElement().getStyle().setProperty("verticalAlign", "" + (-hoogte + asHoogte + Math.rint(font_size * 0.33) + 1) + "px");
				tp.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				parent.getKeyboard().setEditor((FormuleEditorWithAnswer) currentObject);
				flowVak.add(tp);
			}
			else if (currentObject instanceof FormuleViewer)
			{	
				Panel a = ((FormuleViewer) currentObject).getAsPanel();
				a.getElement().getStyle().setProperty("display", "inline-block");
				
				//deze 2 px zijn overgenomen uit het WiskOpdr TekstFormuleVak, om te zorgen dat formules niet op tekst botsen. 
				a.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
				a.getElement().getStyle().setMarginRight(2, Style.Unit.PX);
				//Hieronder: gebruik f.getFontSize() ipv font_size omdat fontSize kan zijn aangepast ivm formules in Times Roman.
				//a.getElement().getStyle().setProperty("verticalAlign", "top");
				//a.getElement().getStyle().setProperty("verticalAlign", "" + (asHoogte - hoogte + Math.rint(f.getFontSize() * 0.33) + 1) + "px");
				//a.getElement().getStyle().setProperty("verticalAlign", "" + (ashoogte - ((TekstElement) currentObject).getAsHoogte()) + "px");
				//int vertAlign = ((TekstElement) currentObject).getAsHoogte() - ashoogte;
				//System.out.println("verticalAlign formuleviewer: " + vertAlign);
				a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				flowVak.add(a);
			}
			else if (currentObject instanceof FormuleEditorWithSteps)
			{
				Panel a = ((FormuleEditorWithSteps) currentObject).getAsPanel();
				
				a.getElement().getStyle().setProperty("display", "inline-block");
				a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
				flowVak.add(a);
			}
			else if (currentObject.getClass().getName().equals("fi.nabouwenaanzichtengwt.client.NabouwenAanzichtenGWT"))
			{
				Panel a = (Panel) (((InteractionView) currentObject).asWidget());
				a.getElement().getStyle().setProperty("display", "inline-block");
				//a.getElement().getStyle().setProperty("verticalAlign", (-font_size * 0.45) + "px");
				flowVak.add(a);
			}
			else if (currentObject instanceof InteractionView)
			{		
				Widget a = (((InteractionView) currentObject).asWidget());
				a.getElement().getStyle().setProperty("display", "inline-block");
				if(currentObject instanceof TekstVakPanel && !(a instanceof PopupButton))
				{
					a.getElement().getStyle().setProperty("verticalAlign", "" + (verschuiving - objectVerschuiving) + "px");
					//a.getElement().getStyle().setProperty("verticalAlign", "top");
					//a.getElement().getStyle().setProperty("verticalAlign", "" + (((TekstElement) currentObject).getAsHoogte() - ashoogte) + "px");
				}
				if(currentObject instanceof TekstVakPanel && ((TekstVakPanel) currentObject).isZwevend())
				{	this.add(a);
					this.setWidgetLeftWidth(a, ((TekstVakPanel)currentObject).getLocationX(), Style.Unit.PX, 
							((TekstVakPanel)currentObject).getBreedte(), Style.Unit.PX);
					this.setWidgetTopHeight(a, ((TekstVakPanel)currentObject).getLocationY(), Style.Unit.PX, 
							((TekstVakPanel)currentObject).getHoogte(), Style.Unit.PX);
					((TekstVakPanel) currentObject).setParent(this);
				}
				else
					flowVak.add(a);
				
			}
			else if (currentObject instanceof ImageView)
			{
				ImageView iv = (ImageView) currentObject;
				Widget w = iv.getImage();
				flowVak.add(w);
			}
			else if (currentObject instanceof AnchorView)
			{
				AnchorView av = (AnchorView) currentObject;
				Widget w = av.asWidget();
				flowVak.add(w);
			}
			
		}
	}
	
	public void bepaalAshoogte()
	{
		//int b = 1; Breedte hoef ik volgens mij niet te regelen, omdat ik alles in een flowpanel zet.
		int h1 = 0;
		int h2 = 0;
		for(int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
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
				int hoogte = font_size - 2;
				int ash = hoogte / 2;
				if(ash > h1)
					h1 = ash;
				if(hoogte - ash > h2)
					h2 = hoogte - ash;
			}
		}
		if(opdrachtObjects.size() > 0)
		{
			this.hoogte = h1 + h2;
			ashoogte = h1;
		}
		else
		{
			this.hoogte = font_size;//eigenlijk: fm.getAscent() + fm.getDescent()
			ashoogte = (hoogte - 2) / 2; //eigenlijk: fm.getAscent();
		}
		System.out.println("berekende ashoogte: " + ashoogte);
		
	}
	
	
	public int getAsHoogte()
	{
		return ashoogte;
	}
	
	public void setAshoogte(int ashoogte)
	{
		this.ashoogte = ashoogte;
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
