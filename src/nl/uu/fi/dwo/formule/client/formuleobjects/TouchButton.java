package nl.uu.fi.dwo.formule.client.formuleobjects;

import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;


/**
 * Bug with MGWT Button, use this button instead
 * 
 * @author Danny Hendrix
 * @deprecated use normal button
 */

public class TouchButton extends TouchPanel
{
	public void setText(String text)
	{
		this.getElement().setInnerText(text);
	}

}
