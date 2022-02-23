package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
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
	
	protected AntwoordFormuleVakChecker avChecker ;
	
	//componenten
	private ArrayList<BerekeningVakRegel> vakRegels = new ArrayList<BerekeningVakRegel>();
	private ArrayList<SimplePanel> seperators = new ArrayList<SimplePanel>();
	private BerekeningVakRegel actieveRegel;
	private VerticalPanel vakPanel;
	private BerekeningVakFeedbackPanel feedbackPanel;
	
		
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
	private FormuleFont font;
	
	
	public BerekeningVak(ActivityComponent a, HashMap<String, Object> launchData, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden) {
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
		
		avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) settings.launchState(), randomVarNamen, randomVarWaarden);
		
		vakPanel = new VerticalPanel();
		vakPanel.setStyleName(DWOplayer.templateCss().answerboxFEWA());
		if(!settings.boxMetRand())	{
			vakPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
			vakPanel.getElement().getStyle().setBackgroundColor("transparent");
		}
		
		maakRegel(null);
	}
	
	public ArrayList<Expressie> getAnswerExpressies() {
		ArrayList<Expressie> expressies = new ArrayList<Expressie>();
		for(int i=0 ; i<vakRegels.size() ; i++) {
			ArrayList<Expressie> regelExpressies = vakRegels.get(i).getExpressions();
			for (int j=0 ; j<regelExpressies.size() ; j++) {
				expressies.add(regelExpressies.get(j));
			}
		}
		return expressies;
	}
	
	public void check(ArrayList<Expressie> expressies) {
		for(int i=0 ; i<expressies.size() ; i++) {
			HashMap<String, Object> checkResults = new HashMap<String, Object>();
			try {
				checkResults = avChecker.checkAnswer(expressies.get(i).toString());
			}
			catch (RestartException e){}
			
		}
	}
	
	
	
	public void showFeedback(BerekeningVakRegel vakRegel) {
		if(feedbackPanel == null)
			feedbackPanel = new BerekeningVakFeedbackPanel(vakPanel);
		feedbackPanel.show(vakRegel.getAsPanel().getAbsoluteLeft()+actieveRegel.getWidth()-30, vakRegel.getAsPanel().getAbsoluteTop()+30);
		feedbackPanel.show();
		feedbackPanel.setVisible(true);
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
	
	public void maakRegel(String tailString) {
		if(!settings.meerregelig() && vakRegels.size()>0)
			return;
		int regelNr = vakRegels.indexOf(actieveRegel);
		BerekeningVakRegel vakRegel = new BerekeningVakRegel(this,breedte-6-2*borderWidth, settings.hoogte()-2*borderWidth);
		vakRegel.setFont(font);
		
		if(tailString!=null && !"".equals(tailString))
			vakRegel.geefFormuleEditor().getMainRegel().insert(tailString);
		if(vakRegel.geefFormuleEditor().getMainRegel().getElementCount()>0) {
			vakRegel.geefFormuleEditor().getMainRegel().setIndexAt(0);
			vakRegel.geefFormuleEditor().cursorToLeft();
			vakRegel.geefFormuleEditor().cursorToLeft();
			vakRegel.geefFormuleEditor().cursorToLeft();
		}
		else {
			vakRegel.geefFormuleEditor().clearMain();
		}
		
		vakRegel.geefFormuleEditor().paint();
		vakRegels.add(regelNr+1,vakRegel);
		vakPanel.insert(vakRegel.getAsPanel(), regelNr+1);
		
		zetActieveRegel(vakRegel);
		rresize();
	}
	
	public void removeActieveRegel(String tailString) {
		if(!settings.meerregelig())
			return;
		int regelNr = vakRegels.indexOf(actieveRegel);
		if(regelNr>0) {
			int aantalVorige = vakRegels.get(regelNr-1).geefFormuleEditor().getMainRegel().getElementCount();
			if(aantalVorige==0) {
				vakPanel.remove(vakRegels.get(regelNr-1).getAsPanel());
				vakRegels.remove(vakRegels.get(regelNr-1));
			}
			else {
				if(tailString!=null && !"".equals(tailString))
					vakRegels.get(regelNr-1).geefFormuleEditor().insert(tailString);
				FormuleElement  element = vakRegels.get(regelNr-1).geefFormuleEditor().getMainRegel().getElementAt(aantalVorige-1);
				vakRegels.get(regelNr-1).geefFormuleEditor().setCurrentElement(element);
				vakRegels.get(regelNr-1).geefFormuleEditor().getMainRegel().setIndexAt(aantalVorige-1);
				vakRegels.get(regelNr-1).geefFormuleEditor().paint();
				vakRegels.remove(actieveRegel);
				vakPanel.remove(actieveRegel.getAsPanel());
				zetActieveRegel(vakRegels.get(regelNr-1));
			}
		}
	}
	
	public void cursorUp() {
		int regelNr = vakRegels.indexOf(actieveRegel);
		if(regelNr > 0) {
			int aantalElements = vakRegels.get(regelNr-1).geefFormuleEditor().getMainRegel().getElementCount();
			if(aantalElements>0) {
				vakRegels.get(regelNr-1).geefFormuleEditor().getMainRegel().setIndexAt(0);
				vakRegels.get(regelNr-1).geefFormuleEditor().cursorToLeft();
				vakRegels.get(regelNr-1).geefFormuleEditor().cursorToLeft();
				vakRegels.get(regelNr-1).geefFormuleEditor().cursorToLeft();
			}
			else {
				vakRegels.get(regelNr-1).geefFormuleEditor().clearMain();
			}
			vakRegels.get(regelNr-1).geefFormuleEditor().paint();
			zetActieveRegel(vakRegels.get(regelNr-1));
		}
	}
	
	public void cursorDown() {
		int regelNr = vakRegels.indexOf(actieveRegel);
		if(regelNr < vakRegels.size()-1) {
			if(vakRegels.get(regelNr+1).geefFormuleEditor().getMainRegel().getElementCount()>0) {
				vakRegels.get(regelNr+1).geefFormuleEditor().getMainRegel().setIndexAt(0);
				vakRegels.get(regelNr+1).geefFormuleEditor().cursorToLeft();
				vakRegels.get(regelNr+1).geefFormuleEditor().cursorToLeft();
				vakRegels.get(regelNr+1).geefFormuleEditor().cursorToLeft();
			}
			else {
				vakRegels.get(regelNr+1).geefFormuleEditor().clearMain();
			}
			vakRegels.get(regelNr+1).geefFormuleEditor().paint();
			zetActieveRegel(vakRegels.get(regelNr+1));
		}
	}
	
	public void zetActieveRegel(BerekeningVakRegel berekeningVakRegel) {
		if(berekeningVakRegel == actieveRegel)
			return;
		if(actieveRegel!=null)
				actieveRegel.setActief(false);
		berekeningVakRegel.setActief(true);
		actieveRegel = berekeningVakRegel;
		requestFocus();
	}
 
	private int meetHoogteRegels() {
		int hoogte = 0;
		for(int i=0 ; i<vakRegels.size() ; i++) {
			hoogte += vakRegels.get(i).getHeight();
		}
		return hoogte;
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
		for(int i=0 ; i<vakRegels.size() ; i++) {
			 vakRegels.get(i).regelResize();
		}
		if(!settings.volledigeBreedte() && !settings.meerregelig())
			breedte = actieveRegel.getWidth()+8;//extraWidth; //checkPanel.getOffsetWidth() + extraWidth;// + (getImageVisible()?26:0);
		hoogte = meetHoogteRegels() + 3+2*borderWidth;
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
		return facade.wrapAsHoogte(actieveRegel.geefFormuleEditor().getMainRegel().getAsHoogte() + corr); //+ 6 /* margin top + padding top */);
	}
	
	public void requestFocus() {
		actieveRegel.geefFormuleEditor().requestFocus();
		
	}

	@Override
	public HashMap<String, Object> getState() {
		String[] antwoordStrings = new String[vakRegels.size()];
		for(int i=0 ; i<vakRegels.size() ; i++) {
			antwoordStrings[i] = vakRegels.get(i).geefFormuleEditor().toString();
		}		
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("antwoordStrings", antwoordStrings);
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h==null)
			return;
		ObjectMap state = JSONUtilities.wrapMap(h);
		String[] antwoordStrings = null;
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
			rresize();
			requestFocus();
		}
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
			actieveRegel.zetVolledigeBreedte(breedte-6-2*borderWidth);
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
		
		rresize();
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
	
	
}
