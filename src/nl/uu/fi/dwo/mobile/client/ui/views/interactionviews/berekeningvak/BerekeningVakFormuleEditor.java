package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import java.util.ArrayList;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.MainFormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class BerekeningVakFormuleEditor extends FormuleEditor {
	
	private BerekeningVakRegel berekeningVakRegel;
	
	public BerekeningVakFormuleEditor(BerekeningVakRegel berekeningVakRegel) {
		super();
		this.berekeningVakRegel = berekeningVakRegel;
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
		berekeningVakRegel.berekeningVak.zetActieveRegel(berekeningVakRegel);
	}
	
	@Override
	public void enter() {
		if(berekeningVakRegel.berekeningVak.settings.meerregelig() 
				&& (getMainRegel().getCurrentPosition()==-1 || getCurrentElement().getParent()==getMainRegel()))
			berekeningVakRegel.berekeningVak.maakRegel(getTailString());
		else if(!berekeningVakRegel.berekeningVak.settings.meerregelig())
			berekeningVakRegel.checkRegel();
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
	
	public String clean(String s) {
		s = s.replace("\u2705", "");
		s = s.replace("\u2714", "");
		s = s.replace("\u274c", "");
		return s;
	}
	
	@Override
	public void addElement(FormuleElement e)	{	
		super.addElement(e);
		berekeningVakRegel.berekeningVak.rresize();
	}
	
	@Override
	public void removeCurrentElement() {
		String tailString = toString();
		if(getMainRegel().getCurrentPosition()==-1)
			berekeningVakRegel.berekeningVak.removeActieveRegel(tailString);
		else if(getCurrentElement()!=null)
			super.removeCurrentElement();
		berekeningVakRegel.berekeningVak.rresize();
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		s = clean(s);
		return s;
	}

	@Override
	public void removeNextElement()	{
		super.removeNextElement();
		berekeningVakRegel.berekeningVak.rresize();
	}
	
	@Override
	public void cursorUp() {
		super.cursorUp();
		berekeningVakRegel.berekeningVak.cursorUp();
	}
	
	@Override
	public void cursorDown() {
		super.cursorDown();
		berekeningVakRegel.berekeningVak.cursorDown();
	}
//	
//	@Override
//	public void cursorToLeft() {
//		super.cursorToLeft();
//		berekeningVakRegel.berekeningVak.cursorToLeft();
//	}
//	
//	@Override
//	public void cursorToRight() {
//		super.cursorToRight();
//		berekeningVakRegel.berekeningVak.cursorToRight();
//	}
	
	
	@Override
	public void insert(String text)	{
		super.insert(text);
		berekeningVakRegel.berekeningVak.rresize();
	}
	
	@Override	
	public void setFont(FormuleFont fm) {
		super.setFont(fm);
		getMainRegel().setMinimumHeight(fm.getHeight() + 3);
		//berekeningVakRegel.berekeningVak.rresize();
	}
	
}


