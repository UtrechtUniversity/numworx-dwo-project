package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.DecRound;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FEWSButton;

public  class BerekeningVakRegel  { //implements TekstElementWithFont{
	
	private BerekeningVak berekeningVak;
	//componenten
	private BerekeningVakFormuleEditor formuleEditor = null;
	private Panel formulePanel = null;
	private HorizontalPanel mainPanel;
	private FEWSButton rekenButton;
	
	private int breedte;
	private int hoogte;
	
	static final double E_MAX = 1.0E7;
	static final double E_MIN = 1.0E-3;
	static final double MARGE = 0.00000000000000001;
	
	public BerekeningVakRegel(BerekeningVak berekeningVak) {
		this.berekeningVak = berekeningVak;
		
		breedte = berekeningVak.settings.breedte();
		hoogte = berekeningVak.settings.hoogte();
		
		mainPanel = new HorizontalPanel();
		mainPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
		mainPanel.getElement().getStyle().setBackgroundColor("transparent");
		
		formuleEditor = new BerekeningVakFormuleEditor(berekeningVak);
		formuleEditor.getMainRegel().setMinimumWidth(breedte - 40);
		formuleEditor.getMainRegel().setMinimumHeight(hoogte - 3);
		formuleEditor.setFormuleToolBijFocus(berekeningVak.settings.formuleToolBijFocus());
		
		formulePanel = formuleEditor.getAsPanel();
		formulePanel.add(formuleEditor.getMainRegel().asWidget());
		mainPanel.add(formulePanel);
		
		rekenButton = new FEWSButton("rekenmachine", false);
		rekenButton.setSize(20, 20);
		rekenButton.addButtonListener(new RekenButtonListener());
		rekenButton.setTooltip("Bereken");
		mainPanel.add(rekenButton);
		
		regelResize();
					
		formuleEditor.register(new FormuleEditorTouchHandler(formuleEditor).initHandler());
	}
	
	private void berekenEnPlaatsExpressie() {
		String isGelijkAntwoord = calculateAndFormatExpression(formuleEditor); //isteken + antwoord
		if(isGelijkAntwoord == null)
			return;
		String formule0 = formuleEditor.toString();
		if(formule0.endsWith("=") || formule0.endsWith("\u2248"))
			formule0 = formule0.substring(0, formule0.length()-1);
		formuleEditor.clearMain();
		formuleEditor.insert(formule0 + isGelijkAntwoord);
	}
	
	public String getTailString(FormuleEditor formuleEditor) {
		String rekenString = "$f"+formuleEditor.toString()+"@";
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
		if(expressie!=null && !(expressie instanceof BasisExpressie)) {
			double approxDouble = expressie.geefWaarde();
			double afgerond = new DecRound(new BasisExpressie(approxDouble), new BasisExpressie(berekeningVak.settings.aantalDecRm())).geefWaarde();
			boolean isAfgerond = !Algebra.isGelijkDouble(approxDouble, afgerond, MARGE);
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
		if(!berekeningVak.settings.volledigeBreedte())
			breedte = formuleEditor.getMainRegel().getWidth() + 44;//extraWidth; //checkPanel.getOffsetWidth() + extraWidth;// + (getImageVisible()?26:0);
		hoogte = formuleEditor.getMainRegel().getHeight();
		formulePanel.setPixelSize((breedte-40) , hoogte );
		mainPanel.setPixelSize((breedte-1) , hoogte-1 );
		if(rekenButton != null) 
			rekenButton.getElement().getStyle().setMarginTop(getAsHoogte()-17, Unit.PX);
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
		return hoogte;
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