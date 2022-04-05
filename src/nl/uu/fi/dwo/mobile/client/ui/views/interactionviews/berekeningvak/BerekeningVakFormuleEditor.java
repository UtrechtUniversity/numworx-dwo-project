package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.MainFormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class BerekeningVakFormuleEditor extends FormuleEditor {
	
	private BerekeningVakRegel berekeningVakRegel;
	private BerekeningVakRegelManager regelManager;
	private BerekeningVakCheckManager checkManager;
	
	public BerekeningVakFormuleEditor(BerekeningVakRegel berekeningVakRegel) {
		super();
		this.berekeningVakRegel = berekeningVakRegel;
		this.regelManager = berekeningVakRegel.berekeningVak.regelManager;
		this.checkManager = berekeningVakRegel.berekeningVak.checkManager;
	}
	
	public boolean isInputNeeded() {
		if(toString().equals(""))
			return false;
		else 
			return true;
	}
	
	@Override
	public void requestFocus() {
		super.requestFocus();
		berekeningVakRegel.berekeningVak.regelManager.zetActieveRegel(berekeningVakRegel);
	}
	
	@Override
	public void enter() {
		if(berekeningVakRegel.berekeningVak.settings.meerregelig() 
				&& (getMainRegel().getCurrentPosition()==-1 || getCurrentElement().getParent()==getMainRegel()))
			regelManager.maakRegel(getTailString());
//		else if(!berekeningVakRegel.berekeningVak.settings.meerregelig() && berekeningVakRegel.berekeningVak.settings.check()) {
//			checkManager.check_enter();
//		}
		else if(berekeningVakRegel.berekeningVak.settings.check()) {
			checkManager.check_enter();
		}
	}
	
	
	public String getTailString() {
		String headString = "";
		String tailString = "";
		MainFormuleRegel mainRegel = getMainRegel();
		for(int i=0 ; i<mainRegel.getElementCount() ; i++) {
			FormuleElement element = mainRegel.getElementAt(i);
			if(i>mainRegel.getCurrentPosition()) 
				tailString += element.toString();
			else
				headString += element.toString();
		}
		clearAll();
		insert(headString);//+"\u2705");
		paint();
		return tailString;//+"\u274c";
	}
	
	public String getTillSelectionString() {
		String string = "";
		MainFormuleRegel mainRegel = getMainRegel();
		int endSelectionIndex = mainRegel.getSelectionEnd();
		for(int i=0 ; i<endSelectionIndex+1 ; i++) {
			FormuleElement element = mainRegel.getElementAt(i);
			string += element.toString();
		}
		return string;
	}
	
	public void cleanCheckMarks() {
		MainFormuleRegel mainRegel = getMainRegel();
		if(mainRegel.toString().indexOf('\u2705')>-1 || mainRegel.toString().indexOf('\u2714')>-1 || mainRegel.toString().indexOf('\u274c')>-1) {
			int position = mainRegel.getCurrentPosition();
			for(int i=0 ; i<mainRegel.getElementCount() ; i++) {
				FormuleElement element = mainRegel.getElementAt(i);
				if(element instanceof FormuleTeken) {
					char tk = ((FormuleTeken)element).geefChar()	;
					if(tk=='\u2705' || tk=='\u2714' || tk=='\u274c') {
						mainRegel.setIndexAt(i);
						mainRegel.removePrevious();
						if(i<position)
							position--;
					}
				}
			}
			mainRegel.setIndexAt(position);
			this.setCurrentElement(mainRegel.getCurrent());
			paint();
			checkManager.setAnswerChanged();
		}
	}
	
	public String cleanCheckMarks(String s) {
		s = s.replace("\u2705", "");
		s = s.replace("\u2714", "");
		s = s.replace("\u274c", "");
		return s;
	}
	
	@Override
	public void addElement(FormuleElement e)	{	
		super.addElement(e);
		cleanCheckMarks();
		berekeningVakRegel.berekeningVak.resize();
	}
	
	@Override
	public void removeCurrentElement() {
		String tailString = toString();
		if(getMainRegel().getCurrentPosition()==-1)
			regelManager.removeActieveRegel(tailString);
		else if(getCurrentElement()!=null)
			super.removeCurrentElement();
		cleanCheckMarks();
		
		berekeningVakRegel.berekeningVak.resize();
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		s = cleanCheckMarks(s);
		return s;
	}

	@Override
	public void removeNextElement()	{
		
		super.removeNextElement();
		cleanCheckMarks();
		berekeningVakRegel.berekeningVak.resize();
	}
	
	@Override
	public void cursorUp() {
		super.cursorUp();
		regelManager.cursorUp();
	}
	
	@Override
	public void cursorDown() {
		super.cursorDown();
		regelManager.cursorDown();
	}

	@Override
	public void insert(String text)	{
		super.insert(text);
		berekeningVakRegel.berekeningVak.resize();
	}
	
	@Override	
	public void setFont(FormuleFont fm) {
		super.setFont(fm);
		getMainRegel().setMinimumHeight(fm.getHeight() + 3);
		//berekeningVakRegel.berekeningVak.rresize();
	}
	
}


