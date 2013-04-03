package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartHandler;


/**
 * Bug with MGWT Button, use this button instead
 * 
 * @author Danny Hendrix
 * 
 */

public class TouchButton extends TouchPanel
{
	public void setText(String text)
	{
		this.getElement().setInnerText(text);
	}

}
