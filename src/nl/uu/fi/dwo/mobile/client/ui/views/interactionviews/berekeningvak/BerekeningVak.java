package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class BerekeningVak implements InteractionView, TekstElementWithFont, CBookEventListener{
	
	//context
	private final ActivityInterface activity;
	private TekstRegel parentRegel;
	private OpdrNavIF comRoot;
	private PopupFacade facade;
	protected String[] randomVarNamen = null;
	protected HashMap randomVarWaarden = null;
	
	private static boolean fontOvererving = false;
	public static void zetFontOverervingForm(boolean b) {
		fontOvererving = b;
	}
	
	// tools
	protected BerekeningVakCheckManager checkManager;
	protected BerekeningVakRegelManager regelManager;
	protected BerekeningVakLoggingManager loggingManager;
	
	//componenten
	private ArrayList<BerekeningVakRegel> vakRegels = new ArrayList<BerekeningVakRegel>();
	private ArrayList<SimplePanel> seperators = new ArrayList<SimplePanel>();
	private VerticalPanel mainPanel;
	private VerticalPanel vakPanel;
	private BerekeningVakFeedbackPanel feedbackPanel;
	private CorrectieFacade correctieFacade;
	private BerekeningVakButton checkButton;
		
	//instellingen
	protected BerekeningVakSettings settings;
	protected boolean editable = true;
	
	//template layout
	protected int borderWidth = (Integer)DWOplayer.templateConstants.answerboxFEWA("border-width");
	private int paddingTop = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-top");
	private int paddingLeft = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-left");
	private int paddingRight = (Integer)DWOplayer.templateConstants.answerboxFEWA("padding-right");
	
	// overige attributen
	private int breedte;
	private int hoogte;
	protected FormuleFont font;
	
	public BerekeningVak(ActivityInterface a, HashMap<String, Object> launchData, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden) {
		activity = a;
		font = FormuleFont.createFromFontSize(XMLView.getDefaultFontSize());
		if(launchData == null)
			return;
		facade = new PopupFacade(JSONUtilities.wrapMap(launchData), activity);
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		settings = new BerekeningVakSettings(launchData);
		breedte = settings.breedte();
		hoogte = settings.hoogte();
		
		checkManager = new BerekeningVakCheckManager(this);
		
		checkButton = new BerekeningVakButton("CONTROLEER");
		checkButton.setSize(126, 24);
		checkButton.addButtonListener(new checkButtonListener());
		checkButton.setTooltip("Bereken");
		
		
		
		vakPanel = new VerticalPanel();
		vakPanel.setStyleName(DWOplayer.templateCss().answerboxFEWA());
		if(!settings.boxMetRand())	{
			vakPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
			vakPanel.getElement().getStyle().setBackgroundColor("transparent");
		}
		
		mainPanel = new VerticalPanel();
		mainPanel.add(vakPanel);
		
		regelManager = new BerekeningVakRegelManager(this);
		regelManager.maakRegel(null);
		
		
		
		loggingManager = new BerekeningVakLoggingManager(this, settings, activity);
		// alternatief 
		// loggingManager = new BerekeningVakLoggingManager(this, settings.launchState(), activity);
	}
	
	public ArrayList<BerekeningVakRegel> geefVakRegels() {
		return vakRegels;
	}
	
	public BerekeningVakRegel geefVakRegel(int index) {
		return vakRegels.get(index);
	}
	
	public void showFeedback(BerekeningVakRegel vakRegel) {
		if(feedbackPanel == null)
			feedbackPanel = new BerekeningVakFeedbackPanel(vakPanel);
		feedbackPanel.show(vakRegel.getAsPanel().getAbsoluteLeft()+regelManager.actieveRegel.getWidth()-30, vakRegel.getAsPanel().getAbsoluteTop()+30);
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
 
	private int meetHoogteRegels() {
		int hoogte = 0;
		for(int i=0 ; i<vakRegels.size() ; i++) {
			hoogte += vakRegels.get(i).getHeight();
		}
		return hoogte;
	}
	
	public Panel getAsPanel() {
		return mainPanel;
	}
	
	public Panel getVakPanel() {
		return vakPanel;
	}
	
	@Override
	public Widget asWidget() {
		return facade.wrap(getAsPanel());
	}
	
	public boolean isPopup() {
		return facade.isPopup();
	}
	
	public void resize() {	
		for(int i=0 ; i<vakRegels.size() ; i++) {
			 vakRegels.get(i).regelResize();
		}
		if(!settings.volledigeBreedte() && !settings.meerregelig())
			breedte = regelManager.actieveRegel.getWidth()+8;//extraWidth; //checkPanel.getOffsetWidth() + extraWidth;// + (getImageVisible()?26:0);
		hoogte = meetHoogteRegels() + 3+2*borderWidth+25;
		vakPanel.setPixelSize((breedte-2*borderWidth) , (hoogte-2*borderWidth-25) );
		mainPanel.setPixelSize((breedte-2*borderWidth) , (hoogte-2*borderWidth) );
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
		if(settings.meerregelig()) {
			return  font.getAscent() + corr; 
		}
		else {
			return facade.wrapAsHoogte(regelManager.actieveRegel.geefFormuleEditor().getMainRegel().getAsHoogte() + corr); //+ 6 /* margin top + padding top */);
		}
	}
	
	public void requestFocus() {
		regelManager.actieveRegel.geefFormuleEditor().requestFocus();
	}
	
	public void wisGoedFout() {
		for(int i=0 ; i<vakRegels.size() ; i++) {
			vakRegels.get(i).wisGoedFout();
		}
	}
	
	public void prepareGoedFout() {
		for(int i=0 ; i<vakRegels.size() ; i++) {
			vakRegels.get(i).prepareGoedFout();
		}
	}

	@Override
	public HashMap<String, Object> getState() {
		String[] antwoordStrings = new String[vakRegels.size()];
		for(int i=0 ; i<vakRegels.size() ; i++) {
			antwoordStrings[i] = vakRegels.get(i).geefFormuleEditor().toString();
		}		
		HashMap<String, Object> state = new HashMap<String, Object>();
		state.put("antwoordStrings", antwoordStrings);
		state.put("editable", Boolean.valueOf(editable));
		checkManager.getCheckerState(state);
		
		//if(!settings.meerregelig() && settings.check())
		if(settings.check())
			checkManager.check_getState();
		
		if(correctieFacade != null) 
			correctieFacade.correctie(state);
		
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h==null)
			return;
		ObjectMap state = JSONUtilities.wrapMap(h);
		
		CorrectieFacade.showReview(h, p -> {
			vakPanel.add(p);
			if (null != vakPanel.getElement().getStyle().getPosition())
				vakPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		}, this, settings.scoreMax(), activity);
		
		String[] antwoordStrings = null;
		checkManager.setCheckerState(state);
		if(h.containsKey("antwoordStrings"))
			antwoordStrings = state.getStringArray("antwoordStrings");
		if(antwoordStrings!=null) {
			for(int i=0 ; i<antwoordStrings.length ; i++) {
				if(i>0  && vakRegels.size()<i+1) {
					vakRegels.add(new BerekeningVakRegel(this,breedte-6-2*borderWidth, font.getHeight()+3));
					vakRegels.get(i).setFont(font);
					vakPanel.add(vakRegels.get(i).getAsPanel());
				}
				if(antwoordStrings[i]!=null && !"".equals(antwoordStrings[i].trim())) {
					vakRegels.get(i).geefFormuleEditor().clearAll();
					vakRegels.get(i).geefFormuleEditor().insert(antwoordStrings[i]);
					vakRegels.get(i).geefFormuleEditor().requestFocus();
				}
			}
			//if(!settings.meerregelig() && settings.check())
			if(settings.check())
				checkManager.check_setState();
			resize();
			requestFocus();
		}
		if (correctieFacade == null) // eenmalig
			correctieFacade = CorrectieFacade.get(h, this, getAsPanel(), settings.scoreMax(), comRoot, loggingManager.logging, activity);
	}
	
	@Override
	public int getScore() {
		//if(!settings.meerregelig())
			return checkManager.getScore();
		//return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boolean isCorrect() {
		//if(!settings.meerregelig())
			return checkManager.getCorrect();
		//return true;
	}

	@Override
	public void kijkNa() {
		//if(!settings.meerregelig() && settings.check())
		if(settings.check())
			checkManager.check_kijkNa();
	}

	@Override
	public void zetNagekeken(boolean b) {
		checkManager.zetNagekeken(b);
	}
	
	public void setChanged() {
		comRoot.setChanged(false);
	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		checkManager.setCommunicationRoot(comRoot);
		
		comRoot.addCBookEventListener("input", this);
		comRoot.addCBookEventListener("index", this);
		comRoot.addCBookEventListener("double", this);
		comRoot.addCBookEventListener("equation", this);
		comRoot.addCBookEventListener("expression", this);
		comRoot.addCBookEventListener("action.setNotEditable", this);
		comRoot.addCBookEventListener("action.check", this);

		if (loggingManager.logging != null) 
			loggingManager.logging.setCommunicationRoot(comRoot);
		
		if(settings.check() && settings.meerregelig() && (getMode()==0 || getMode()==1))
			mainPanel.add(checkButton);
		
	}
	
	public int getMode() {
		return comRoot.getMode();
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(settings.volledigeBreedte())	{	
			this.breedte = breedte;
			vakPanel.setPixelSize((breedte-2*borderWidth) , (hoogte-2*borderWidth) );
			regelManager.actieveRegel.zetVolledigeBreedte(breedte-6-2*borderWidth);
		}
		
	}

	public void setEnabled(boolean b) {
		for(int i=0 ; i<vakRegels.size() ; i++) {
			vakRegels.get(i).setEnabled(b);
		}
	}

	void setEditable(boolean editable) {
		this.editable = editable;
		getAsPanel().setStyleDependentName("readonly", !editable);
		if(!editable) setEnabled(false);
	}
	
	public void setFont(FormuleFont fm) {
		for(int i=0 ; i<vakRegels.size() ; i++) {
			 vakRegels.get(i).setFont(fm);
		}
		
		resize();
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
			setFont(font);
		}
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private class checkButtonListener implements ButtonListener {
		
		public void onClick(Object sender) {
			kijkNa();
		}
	}
}
