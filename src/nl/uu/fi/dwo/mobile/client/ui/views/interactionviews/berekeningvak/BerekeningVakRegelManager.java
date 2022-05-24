package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.VerticalPanel;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class BerekeningVakRegelManager {

	private BerekeningVak berekeningVak;
	private ArrayList<BerekeningVakRegel> vakRegels;
	protected BerekeningVakRegel actieveRegel;
	
	private VerticalPanel vakPanel;
	
	public BerekeningVakRegelManager(BerekeningVak berekeningVak) {
		this.berekeningVak = berekeningVak;
		vakRegels = berekeningVak.geefVakRegels();
		vakPanel = (VerticalPanel)berekeningVak.getVakPanel();
	}
	
	public void maakRegel(String tailString) {
		if(!berekeningVak.settings.meerregelig() && vakRegels.size()>0)
			return;
		int regelNr = vakRegels.indexOf(actieveRegel);
		BerekeningVakRegel vakRegel = new BerekeningVakRegel(berekeningVak,berekeningVak.settings.breedte()-2*berekeningVak.borderWidth-6, berekeningVak.settings.hoogte()-2*berekeningVak.borderWidth);
		vakRegel.setFont(berekeningVak.font);
		
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
		berekeningVak.resize();
	}
	
	public void removeActieveRegel(String tailString) {
		if(!berekeningVak.settings.meerregelig())
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
		actieveRegel.geefFormuleEditor().requestFocus();
	}
	
}
