package nl.uu.fi.dwo.mobile.client.ui.formuleholder;

import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleRegel;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

/**
 * Alows modification of a formula
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleEditor extends FormuleHolder
{
	//selector
	private FormuleRegel current = null;
	private FormuleElement currentElement = null;

	private int selectionStartX = -1;
	private int selectionStartY = 0;

	private boolean hasSelection = false;

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

	public void setCurrentFormuleTeken(FormuleRegel e)
	{
		current = e;
	}

	public Panel getAsPanel()
	{
		//FocusPanel sp = new FocusPanel();
		TouchPanel sp = new TouchPanel();
		sp.getElement().addClassName("insert_formule");
		sp.add(this.getMainRegel().getCanvas());
		return sp;
	}

	/**
	 * Selection
	 */
	public void startSelection(int x, int y)
	{
		this.selectionStartX = x;
		this.selectionStartY = y;
		//this.selectionDragStart = this.currentPosition;
		//DWOplayer.log("Start selection : " )
	}

	public void endSelection(int selectionEndX, int selectionEndY)
	{
		//swap?
		int selectionStartX = this.selectionStartX;
		int selectionStartY = this.selectionStartY;
		//if (selectionStartX == selectionEndX)
		//return;

		if (selectionEndX < selectionStartX)
		{
			int temp = selectionStartX;
			selectionStartX = selectionEndX;
			selectionEndX = temp;
			temp = selectionStartY;
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

	public String getSelectionString()
	{
		if (this.hasSelection() == false)
			return "";
		return current.getSelectionString();
	}

	public void deleteSelection()
	{
		this.current.deleteSelection();
		this.hasSelection = false;
	}

	public boolean hasSelection()
	{
		return this.hasSelection;
	}
}
