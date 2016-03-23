package nl.uu.fi.dwo.formule.client.formuleholder;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.AbsVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.BinVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.BreukVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.ConjugVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.DiffPartialVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.DiffVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Haakjesvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.IntegraalVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Kwadraatvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.LimietVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeLogVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeWortelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PrimitieveVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PrvVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SigmaVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.StelselVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SubscriptVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

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
	private boolean changed = false;

	public void enter() {}
	
		
	public FormuleEditor()
	{
		super();
		current = this.getMainRegel();
	}
	
	public void setChanged(boolean c)
	{
		changed = c;
	}
	
	public boolean isChanged()
	{
		return changed;
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
		changed = true;
		resize();
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
		changed = true;
		resize();
	}

	public void removeNextElement()
	{
		current.removeNext();
		this.hasSelection = false;
		this.paint();
		changed = true;
		resize();
	}

	public void insert(String text)
	{
		current.insert(text);
		this.hasSelection = false;
		this.paint();
		changed = true;
		resize();
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
	
	public FormuleElement getCurrentElement()
	{
		return currentElement;
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
		//nl.uu.fi.dwo.interaction.client.touch.TouchPanel sp = new nl.uu.fi.dwo.interaction.client.touch.TouchPanel();
		TouchPanel sp = new TouchPanel();
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
		this.selectionStartX = selectionStartX;
		this.selectionEndX = selectionEndX;
		this.selectionStartY = selectionStartY;
		this.selectionEndY = selectionEndY;
		this.paint();
	}

	public void clearSelection()
	{
		if (this.hasSelection() == false)
			return;
		this.current.clearSelection();
		this.hasSelection = false;
		resize();
	}
	
	public void clearAll()
	{
		this.current.deleteAll();
		this.hasSelection = false;
		changed = true;
		resize();
	}

	public void deleteSelection()
	{
		this.current.deleteSelection();
		this.hasSelection = false;
		changed = true;
		resize();
	}

	public void requestFocus() {
		if(kb!=null)
		{
			kb.setEditor(this);
			if(isSoft()) kb.softFocus(); else kb.focus();
			//XXX BREEKT FEWS getCurrentRegel().getCanvas().getElement().scrollIntoView();
		}
	}
	
	public FormuleKeyboardIF getKeyboard()
	{
		return kb;
	}

	@Override
	public void cursorToLeft() {
		getCurrentRegel().cursorToLeft();	
		hasSelection = getCurrentRegel().hasSelection();
	}

	@Override
	public void cursorToRight() {
		getCurrentRegel().cursorToRight();
		hasSelection = getCurrentRegel().hasSelection();
	}
	
	@Override
	public void cursorToLeftShift() {
		
		getCurrentRegel().cursorToLeftShift();	
		hasSelection = getCurrentRegel().hasSelection();
	}


	@Override
	public void cursorToRightShift() {
		getCurrentRegel().cursorToRightShift();
		hasSelection = getCurrentRegel().hasSelection();
	}
	
	@Override
	public void cursorUp() {
		getCurrentRegel().cursorUp();
		hasSelection = getCurrentRegel().hasSelection();
	}
	
	@Override
	public void cursorDown() {
		getCurrentRegel().cursorDown();
		hasSelection = getCurrentRegel().hasSelection();
	}
	
	public void knip(FormuleClipboardIF clip)
	{
		getCurrentRegel().knip( clip);
	}
	
	public void kopieer(FormuleClipboardIF clip)
	{
		getCurrentRegel().kopieer(clip);
	}
	
	public void plak(FormuleClipboardIF clip)
	{
		getCurrentRegel().plak(clip);
		changed = true;
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
		addElement(new BreukVak(getCurrentRegel()));
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
		addElement(new IntegraalVak(getCurrentRegel(), "x"));
	}


	@Override
	public void prv() {
		addElement(new PrvVak(getCurrentRegel()));
	}


	@Override
	public void ndelog() {
		addElement(new NdeLogVak(getCurrentRegel()));
	}


	@Override
	public void abs() {
		addElement(new AbsVak(getCurrentRegel()));
	}


	@Override
	public void subscript() {
		addElement(new SubscriptVak(getCurrentRegel()));
	}


	@Override
	public void bin() {
		addElement(new BinVak(getCurrentRegel()));
	}


	@Override
	public void diff() {
		addElement(new DiffVak(getCurrentRegel()));
	}

	@Override
	public void diff_partial(){
		addElement(new DiffPartialVak(getCurrentRegel()));
	}

	@Override
	public void limiet0() {
		LimietVak vak = new LimietVak(getCurrentRegel());
		vak.zetRichting(0);
		addElement(vak);
	}


	@Override
	public void limiet1() {
		LimietVak vak = new LimietVak(getCurrentRegel());
		vak.zetRichting(1);
		addElement(vak);
	}


	@Override
	public void limiet2() {
		LimietVak vak = new LimietVak(getCurrentRegel());
		vak.zetRichting(2);
		addElement(vak);
	}


	@Override
	public void primitieve() {
		addElement(new PrimitieveVak(getCurrentRegel()));
	}


	@Override
	public void conjug() {
		addElement(new ConjugVak(getCurrentRegel()));
	}


	@Override
	public void sigma() {
		addElement(new SigmaVak(getCurrentRegel()));
	}
	
	@Override
	public void stelsel() {
		addElement(new StelselVak(getCurrentRegel()));
	}


	public void clearMain() {
		setCurrentElement(getMainRegel());
		clearAll();
		resize();
	}

	//Wordt in uitbreidingen van deze class (in GraphToolGWT en in PijlVak FormuleEditorWithSteps) overschreven.
	public void resize()
	{
		
	}
	
}
