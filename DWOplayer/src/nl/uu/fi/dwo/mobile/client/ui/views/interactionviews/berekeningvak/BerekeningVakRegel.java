package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.DecRound;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;

public  class BerekeningVakRegel  { //implements TekstElementWithFont{
	
	//parent
	protected BerekeningVak berekeningVak;
	//componenten
	private BerekeningVakFormuleEditor formuleEditor = null;
	private Panel formulePanel = null;
	private LayoutPanel goedFoutPanel = null;
	private VerticalPanel mainPanel;
	private HorizontalPanel regelPanel;
	private SimplePanel separator;
	private BerekeningVakButton rekenButton;
	
	private int breedte;
	private int hoogte;
	
	//private boolean actief;
	
	static final double E_MAX = 1.0E7;
	static final double E_MIN = 1.0E-3;
	static final double MARGE = 0.00000000000000001;
	
	private int regelScore;
	private boolean regelCorrect;
	private boolean regelFout;
	
	static private Logger logger = Logger.getLogger("BerekeningVakRegel");
	
	public BerekeningVakRegel(BerekeningVak berekeningVak, int width, int height) {
		this.berekeningVak = berekeningVak;
		
		breedte = width;
		hoogte = height;
		
		mainPanel = new VerticalPanel();
		
		regelPanel = new HorizontalPanel();
		regelPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
		regelPanel.getElement().getStyle().setBackgroundColor("transparent");
		regelPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		formuleEditor = new BerekeningVakFormuleEditor(this);
		formuleEditor.getMainRegel().setMinimumWidth(breedte - 40);
		formuleEditor.getMainRegel().setMinimumHeight(hoogte - 3);
		formuleEditor.setFormuleToolBijFocus(berekeningVak.settings.formuleToolBijFocus());
		
		goedFoutPanel = new LayoutPanel();
		goedFoutPanel.setPixelSize(0 , height );
		regelPanel.add(goedFoutPanel);
		
		formulePanel = formuleEditor.getAsPanel();
		formulePanel.add(formuleEditor.getMainRegel().asWidget());
		regelPanel.add(formulePanel);
		
		rekenButton = new BerekeningVakButton("rekenmachine");
		rekenButton.setSize(20, 20);
		rekenButton.addButtonListener(new RekenButtonListener());
		rekenButton.setTooltip("Bereken");
		rekenButton.setVisible(false);
		if(berekeningVak.settings.rmKnop())
			regelPanel.add(rekenButton);
		
		mainPanel.add(regelPanel);
		
		separator = new SimplePanel();
		separator.getElement().getStyle().setBackgroundColor(""+CssColor.make(211,229,244));
		if(berekeningVak.settings.meerregelig())
			mainPanel.add(separator);
		
		regelResize();
					
		formuleEditor.register(new BVformuleEditorTouchHandler(formuleEditor).initHandler());
	}
	
	public void setActief(boolean b) {
		rekenButton.setVisible(b);
	}
	
	public int geefRegelScore() {
		return regelScore;
	}
	
	public boolean geefRegelCorrect() {
		return regelCorrect;
	}
	
	public boolean geefRegelFout() {
		return regelFout;
	}
	
	public String getString() {
		return formuleEditor.toString();
	}
	
	public void wisGoedFout() {
		goedFoutPanel.clear();
		goedFoutPanel.setPixelSize(0 , hoogte );
	}
	
	public void zetGoedFout(int goedFout) {
		goedFoutPanel.clear();
		if(goedFout == AntwoordFormuleVakChecker.GOED) {
			FormuleViewer fv = new FormuleViewer("\u2705");
			//fv.setAsHoogte(10);
			goedFoutPanel.add(fv);
		}
		else if(goedFout == AntwoordFormuleVakChecker.HALF || goedFout == AntwoordFormuleVakChecker.DOOR) {
			FormuleViewer fv = new FormuleViewer("\u2714");
			//fv.setAsHoogte(10);
			goedFoutPanel.add(fv);
		}
		else if(goedFout == AntwoordFormuleVakChecker.FOUT) {
			FormuleViewer fv = new FormuleViewer("\u274c");
			//fv.setAsHoogte(10);
			goedFoutPanel.add(fv);
		}
	}
	
	public void prepareGoedFout() {
		goedFoutPanel.setPixelSize(20 , hoogte );
	}
	
	private void berekenEnPlaatsExpressie() {
		String isGelijkAntwoord = calculateAndFormatExpression(formuleEditor); //isteken + antwoord
		if(isGelijkAntwoord == null) {
			berekeningVak.showFeedback(this);
			return;
		}
		
		String formule0 = formuleEditor.toString();
		if(formuleEditor.hasSelection()) {
			formule0 = formuleEditor.getTillSelectionString();
		}
			
		if(formule0.endsWith("=") || formule0.endsWith("\u2248"))
			formule0 = formule0.substring(0, formule0.length()-1);
		formuleEditor.clearMain();
		formuleEditor.insert(formule0 + isGelijkAntwoord);
	}
	
	public String getTailString(FormuleEditor formuleEditor) {
		String rekenString = null;
		if(formuleEditor.hasSelection())
			rekenString = "$f"+formuleEditor.getSelectionString()+"@";
		else
			rekenString = "$f"+formuleEditor.toString()+"@";
		if(rekenString.endsWith("=@") || rekenString.endsWith("\u2248"+"@"))
			rekenString = rekenString.substring(0, rekenString.length()-2)+"@";
		if(rekenString.indexOf("=")>-1)
			rekenString = "$f"+rekenString.substring(rekenString.lastIndexOf("=")+1);
		if(rekenString.indexOf("\u2248")>-1)
			rekenString = "$f"+rekenString.substring(rekenString.lastIndexOf("\u2248")+1);
		return rekenString;
	}
	
	public String calculateAndFormatExpression(FormuleEditor formuleEditor) {
		String expressieString = getTailString(formuleEditor);
		Expressie expressie = FormuleParser.geefExpressie(expressieString);
//		if(casNodig(expressie)) {
//			try {
//				expressie = Expressie.evalWithCAS(expressie);
//			}
//			catch(RestartException e) {}
//		}
//		logger.info("Na CAS:" + expressie.toString());
		if(expressie!=null && !(expressie instanceof BasisExpressie)) {
			double approxDouble = expressie.geefWaarde()/1.000000000000001; // ivm vreemde implementatie van DecRound.geefWaarde(
			int decimals = Math.min(14-(int)Math.log10(approxDouble), berekeningVak.settings.aantalDecRm());//anders heb je meer decimalen dan de rekenprecisie
			double afgerond = new DecRound(new BasisExpressie(approxDouble), new BasisExpressie(decimals)).geefWaarde();
			boolean isAfgerond = !Algebra.isGelijkDouble(expressie.geefWaarde(), afgerond, MARGE);
			if (!Double.isNaN(approxDouble)) {
				String antwoord;
				double abs = Math.abs(afgerond);
				if ( abs < E_MIN || abs >= E_MAX) {
					antwoord = Expressie.dfe.format(afgerond);						
					antwoord  = antwoord.replace("E", "*10$m") + "@";
				} 
				else 	{
					antwoord = Expressie.df.format(afgerond);
				}
				String isTeken = "=";
				if (isAfgerond){
					isTeken = "\u2248"; 
				}
				return isTeken + antwoord;
			}
		}
		return null;
	}
	
	private boolean casNodig(Expressie antwoord) {
		boolean casNodig;
		String string = antwoord.toString();
		casNodig = string.indexOf("$i")>-1 || string.indexOf("$d")>-1 || string.indexOf("$T")>-1  || string.indexOf("$S")>-1  || string.indexOf("$P")>-1;
		return casNodig;
	}
	
	public FormuleEditor geefFormuleEditor() {
		return formuleEditor;
	}
	
	public Panel getAsPanel() {
		return mainPanel;
	}
	
	public Widget asWidget() {
		return getAsPanel();
	}
	
	public void regelResize() {	
		if(!berekeningVak.settings.volledigeBreedte() && !berekeningVak.settings.meerregelig()) {
			breedte = formuleEditor.getMainRegel().getWidth() + 42;
		}
		if(berekeningVak.settings.meerregelig()) {
			if(formuleEditor.getMainRegel().getWidth() > breedte -40) {
				//wat te doen bij overloop van een regel?
			}
		}
		int extraSpace = 0;
		if(berekeningVak.settings.meerregelig())
			extraSpace = 2;
		int left =  formuleEditor.getMainRegel().getHeight();       // hoogte formule regel
		int right = rekenButton == null ? left : getAsHoogte()+6;  	// hoogte rekenButton = as - 14 + 20
		hoogte = Math.max(left,right) + extraSpace;
		formulePanel.setPixelSize((breedte-30-goedFoutPanel.getOffsetWidth()) , hoogte );
		regelPanel.setPixelSize((breedte-1) , hoogte-1 );
		mainPanel.setPixelSize((breedte-1) , hoogte );
		separator.setPixelSize((breedte-1) , 1 );
		if(rekenButton != null) 
			rekenButton.getElement().getStyle().setMarginTop(getAsHoogte()-14, Unit.PX);
	}
	
	public void setFont(FormuleFont fm) {
		formuleEditor.setFont(fm);
		formuleEditor.setDefaultFont(fm);
		formuleEditor.getMainRegel().setMinimumHeight(fm.getHeight() + 3);
		formuleEditor.paint();
		regelResize();
	}
	
	public void setEnabled(boolean b) {
		if (b && berekeningVak.editable) {
			formulePanel.removeStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().insert_formule_readonly());
			formuleEditor.register(new FormuleEditorTouchHandler(formuleEditor).initHandler());
			formuleEditor.requestFocus();
		}
		else 	{
			formulePanel.addStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().insert_formule_readonly());
			formuleEditor.removeTouchHandler();
			// zorg dat de formule editor geen focus heeft
			if (formuleEditor.getKeyboard() != null)	{
				formuleEditor.getKeyboard().setEditor(null);
				formuleEditor.getKeyboard().blur();
			}
		}
	}

	void setEditable(boolean editable) {
		getAsPanel().setStyleDependentName("readonly", !editable);
		if(!editable) setEnabled(false);
	}
			
	public int getHeight() {
		return hoogte+1;
	}
	
	public int getWidth() {	
		return breedte;
	}
	
	public int getAsHoogte() {
		return formuleEditor.getMainRegel().getAsHoogte(); 
	}
	
	public void zetVolledigeBreedte(int breedte) {
		this.breedte = breedte;
		formuleEditor.getMainRegel().setMinimumWidth(breedte - 40);
		formuleEditor.getMainRegel().setSize(breedte - 40, hoogte);
		formuleEditor.paint();
		
		formulePanel.setPixelSize((breedte-40) , (hoogte) );
		mainPanel.setPixelSize((breedte) , (hoogte) );
	}
	
	private class RekenButtonListener implements ButtonListener {
		
		public void onClick(Object sender) {
			berekenEnPlaatsExpressie();
		}
	}
}