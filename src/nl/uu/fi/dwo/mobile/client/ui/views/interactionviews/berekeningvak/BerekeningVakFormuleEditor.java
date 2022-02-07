package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class BerekeningVakFormuleEditor extends FormuleEditor {
	
	private BerekeningVak berekeningVak;
	
	public BerekeningVakFormuleEditor(BerekeningVak berekeningVak) {
		super();
		this.berekeningVak = berekeningVak;
	}
	
	public boolean isInputNeeded() {
		if(toString().equals(""))
			return false;
		else 
			return true;
	}
	
	@Override
	public void enter() {
		berekeningVak.maakRegel();
	}
	
	public void addElement(FormuleElement e)	{	
		super.addElement(e);
		berekeningVak.rresize();
	}
	public void removeCurrentElement() {
		super.removeCurrentElement();
		berekeningVak.rresize();
	}

	public void removeNextElement()	{
		super.removeNextElement();
		berekeningVak.rresize();
	}
	public void insert(String text)	{
		super.insert(text);
		berekeningVak.rresize();
	}
		
	public void setFont(FormuleFont fm) {
		super.setFont(fm);
		getMainRegel().setMinimumHeight(fm.getHeight() + 3);
		berekeningVak.rresize();
	}
}


