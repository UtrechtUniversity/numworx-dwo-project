package nl.uu.fi.dwo.formule.client.formuleholder;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Breukvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Haakjesvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.IntegraalVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Kwadraatvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeWortelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.user.client.ui.Panel;

/**
 * Alows modification of a formula
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleEditor extends FormuleHolder implements FormuleEditorIF
{
	//selector
	private FormuleRegel current = null;
	private FormuleElement currentElement = null;

	public void enter() {}
	
	
	public FormuleEditor()
	{
		super();
		current = this.getMainRegel();
	}

	public void addElement(FormuleElement e)
	{
		//DWOplayer.log("Start selection : " + selectionStartX + "  " + selectionStartY);
		String copy = "";
		if (hasSelection())
			copy = getSelectionString();
		if (current == null)
			current = this.getMainRegel();
		e = current.insert(e);
		FormuleElement newElement = e.getCurrentOnNew();
		if (newElement instanceof FormuleRegel && hasSelection())
		{
			((FormuleRegel) newElement).insert(copy);
			newElement = e.getCurrentOnNewOnSelection();
		}
		if (newElement instanceof FormuleRegel)
			setCurrentRegel((FormuleRegel) newElement);
		//make the new element the current element
		this.setCurrentElementRepaint(newElement);
		this.paint();
		this.hasSelection = false;
	}

	@Deprecated
	public void setCurrent(int x, int y)
	{
		FormuleElement temp = currentElement;
		getMainRegel().setCurrentElementAt(x, y);

		this.paint();
	}

	public void removeCurrentElement()
	{
		current.removePrevious();
		this.hasSelection = false;
		this.paint();
	}

	public void removeNextElement()
	{
		current.removeNext();
		this.hasSelection = false;
		this.paint();
	}

	public void insert(String text)
	{
		current.insert(text);
		this.hasSelection = false;
		this.paint();
	}

	public void setCurrentRegel(FormuleRegel regel)
	{
		this.current = regel;
	}

	public FormuleRegel getCurrentRegel()
	{
		return this.current;
	}

	public void setCurrentElement(FormuleElement e)
	{
		if (currentElement != null)
			currentElement.setCurrent(false);
		currentElement = e;
		if (currentElement != null)
			currentElement.setCurrent(true);
	}

	public void setCurrentElementRepaint(FormuleElement e)
	{
		if (currentElement != null)
		{
			currentElement.setCurrent(false);
			//currentElement.repaint();
		}
		currentElement = e;
		if (currentElement != null)
			currentElement.setCurrent(true);
		else
			current.clearSelection();
		//currentElement.repaint();
		this.paint();
	}
	
	public void setCurrentElementRepaint() {
		setCurrentElementRepaint(null);
	}

	public void setCurrentFormuleTeken(FormuleRegel e)
	{
		current = e;
	}

	public Panel getAsPanel()
	{
		//FocusPanel sp = new FocusPanel();
		nl.uu.fi.dwo.interaction.client.touch.TouchPanel sp = new nl.uu.fi.dwo.interaction.client.touch.TouchPanel();
		sp.getElement().addClassName("insert_formule");
		sp.add(this.getMainRegel().getCanvas());
		return sp;
	}

	public void endSelection(int selectionEndX, int selectionEndY)
	{
		//swap?
		int selectionStartX = this.selectionStartX;
		int selectionStartY = this.selectionStartY;
		//if (Math.abs(selectionStartX - selectionEndX)<4 && Math.abs(selectionStartY - selectionEndY)<4)
		//{	clearSelection();
		//	this.paint();
		//	return;
		//}

		if (selectionEndX < selectionStartX)
		{
			int temp = selectionStartX;
			selectionStartX = selectionEndX;
			selectionEndX = temp;
		}
		if (selectionEndY < selectionStartY)
		{
			int temp = selectionStartY;
			selectionStartY = selectionEndY;
			selectionEndY = temp;
		}

		FormuleRegel l = this.getMainRegel().selection(selectionStartX, selectionStartY, selectionEndX, selectionEndY);
		hasSelection = l.hasSelection();
		this.paint();
	}

	public void clearSelection()
	{
		if (this.hasSelection() == false)
			return;
		this.current.clearSelection();
		this.hasSelection = false;
	}
	
	public void clearAll()
	{
		this.current.deleteAll();
		this.hasSelection = false;
	}

	public void deleteSelection()
	{
		this.current.deleteSelection();
		this.hasSelection = false;
	}

	public void requestFocus(FormuleKeyboardIF kb) {
		if(kb!=null)
			kb.setEditor(this);
	}


	@Override
	public void cursorToLeft() {
		getCurrentRegel().cursorToLeft();	
	}


	@Override
	public void cursorToRight() {
		getCurrentRegel().cursorToRight();
	}


	@Override
	public void insert(char charAt) {
		addElement(new FormuleTeken(getCurrentRegel(), charAt));
	}


	@Override
	public void macht() {
		addElement(new Machtvak(getCurrentRegel()));
	}


	@Override
	public void wortel() {
		addElement(new WortelVak(getCurrentRegel()));		
	}


	@Override
	public void breuk() {
		addElement(new Breukvak(getCurrentRegel()));
	}


	@Override
	public void kwadraat() {
		addElement(new Kwadraatvak(getCurrentRegel()));	
	}


	@Override
	public void ndewortel() {
		addElement(new NdeWortelVak(getCurrentRegel()));	
	}


	@Override
	public void haakjes() {
		addElement(new Haakjesvak(getCurrentRegel()));
	}


	@Override
	public void integraal() {
		addElement(new IntegraalVak(getCurrentRegel()));
	}

}
