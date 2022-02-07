package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.DecRound;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FEWSButton;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen.StelselEditor;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class BerekeningVak implements InteractionView, TekstElementWithFont{
	
	//context
	private final ActivityComponent activity;
	private TekstRegel parentRegel;
	private OpdrNavIF comRoot;
	private PopupFacade facade;
	private String[] randomVarNamen = null;
	private HashMap randomVarWaarden = null;
	
	private static boolean fontOvererving = false;
	public static void zetFontOverervingForm(boolean b) {
		fontOvererving = b;
	}
	
	private AntwoordFormuleVakChecker avChecker ;
	
	//componenten
	private BerekeningVakRegel vakRegel;
	//private FormuleEditor formuleEditor = null;
	//private Panel formulePanel = null;
	private VerticalPanel vakPanel;
	//private FEWSButton rekenButton;
		
	//instellingen
	protected BerekeningVakSettings settings;
	protected boolean editable = true;
	
	//template layout
	private int borderWidth = (Integer)DWOplayer.templateConstants.answerboxFEWA("border-width");
	private int paddingTop = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-top");
	private int paddingLeft = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-left");
	private int paddingRight = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-right");
	
	// overige attributen
	private int breedte;
	private int hoogte;
	
	
	public BerekeningVak(ActivityComponent a, HashMap<String, Object> launchData, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden) {
		activity = a;
		if(launchData == null)
			return;
		facade = new PopupFacade(JSONUtilities.wrapMap(launchData), activity);
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		settings = new BerekeningVakSettings(launchData);
		breedte = settings.breedte();
		hoogte = settings.hoogte();
		
		avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) settings.launchState(), randomVarNamen, randomVarWaarden);
		
		vakRegel = new BerekeningVakRegel(this);
		vakPanel = new VerticalPanel();
		vakPanel.setStyleName(DWOplayer.templateCss().answerboxFEWA());
		if(!settings.boxMetRand())	{
			vakPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
			vakPanel.getElement().getStyle().setBackgroundColor("transparent");
		}
		vakPanel.add(vakRegel.getAsPanel());
		rresize();
	}
	
	public void tab() {
		if(parentRegel != null) {
			parentRegel.getTekstVak().tabFocus(this, true);
		}
	}
	
	public void shiftTab() {
		if(parentRegel != null) {
			parentRegel.getTekstVak().shiftTabFocus(this, true);
		}
	}
	
	public void maakRegel() {
	}
	
	public Panel getAsPanel() {
		return vakPanel;
	}
	
	@Override
	public Widget asWidget() {
		return facade.wrap(getAsPanel());
	}
	
	public boolean isPopup() {
		return facade.isPopup();
	}
	
	public void rresize() {	
		vakRegel.regelResize();
		if(!settings.volledigeBreedte())
			breedte = vakRegel.getWidth()+8;//extraWidth; //checkPanel.getOffsetWidth() + extraWidth;// + (getImageVisible()?26:0);
		hoogte = vakRegel.getHeight()+8;
		vakPanel.setPixelSize((breedte-2*borderWidth) , (hoogte-2*borderWidth) );
		if(parentRegel != null) {	
			parentRegel.resize();
		}
	}
	
	public int getHeight() {
		return facade.wrapHeight(hoogte);
	}
	
	public int getWidth() {	
		return facade.wrapWidth(breedte  + paddingLeft + paddingRight);
	}
	
	public int getAsHoogte() {
		int corr = settings.boxMetRand() ? 2*borderWidth : paddingTop;
		return facade.wrapAsHoogte(vakRegel.geefFormuleEditor().getMainRegel().getAsHoogte() + corr); //+ 6 /* margin top + padding top */);
	}
	
	public void requestFocus() {
		vakRegel.geefFormuleEditor().requestFocus();
	}

	@Override
	public HashMap<String, Object> getState() {
		String antwoordString = vakRegel.geefFormuleEditor().toString();
		
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("antwoordString", antwoordString);
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h==null)
			return;
		String antwoordString = (String)h.get("antwoordString");
		if(antwoordString!=null && !"".equals(antwoordString.trim()))
			vakRegel.geefFormuleEditor().insert(antwoordString);
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isCorrect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(settings.volledigeBreedte())	{	
			this.breedte = breedte;
			vakPanel.setPixelSize((breedte-2*borderWidth) , (hoogte-2*borderWidth) );
			vakRegel.zetVolledigeBreedte(breedte-6-2*borderWidth);
		}
		
	}

	public void setEnabled(boolean b) {
		vakRegel.setEnabled(b);
	}

	void setEditable(boolean editable) {
		this.editable = editable;
		getAsPanel().setStyleDependentName("readonly", !editable);
		if(!editable) setEnabled(false);
	}

	@Override
	public void setFontSize(int font_size) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFontName(String font_name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFontStyle(int font_style) {
		// TODO Auto-generated method stub
	}



	@Override
	public void setParentRegel(TekstRegel regel)	{
		parentRegel = regel;
		if (fontOvererving) {
			FormuleFont font = FormuleFont.createFromFontSize(parentRegel.getFont().getFontSize(), false);
			if (!FormuleFont.formTimes)
				font.setFont(parentRegel.getFont().getFont());
			vakRegel.geefFormuleEditor().setFont(font);
			vakRegel.geefFormuleEditor().setDefaultFont(font);
		}
		vakRegel.geefFormuleEditor().paint();
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
}
