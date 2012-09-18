package nl.uu.fi.dwo.mobile.client.ui.formuleholder;

import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleFont;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleRegel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Base class for viewing a formula
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleHolder
{
	private FormuleRegel main = null;

	private FormuleFont font = FormuleFont.createFromFontSize(18);
	private static FormuleFont defaultfont = FormuleFont.createFromFontSize(18);
	private FlowPanel sp = null;

	public FormuleHolder()
	{
		//main regel
		FormuleRegel regel = new FormuleRegel(this);
		GWT.log("Font size:" + defaultfont.getFontStyle());
		main = regel;
		//font = defaultfont;
	}

	public void paint()
	{
		main.paint();
	}

	public FormuleRegel getMainRegel()
	{
		return main;
	}

	@Override
	public String toString()
	{
		return this.main.toString();
	}

	public FormuleFont getFont()
	{
		return this.font;
	}

	public FormuleFont getDefaultFont()
	{
		return defaultfont;
	}

	public void setFont(FormuleFont fm)
	{
		this.font = fm;
		this.getMainRegel().setFont(this.font);
		this.paint();
	}
	
	public static void setDefaultFont(FormuleFont fm)
	{
		defaultfont = fm;
	}

	public Panel getAsPanel()
	{
		//FocusPanel sp = new FocusPanel();
		sp = new FlowPanel();
		sp.add(this.main.getCanvas());
		return sp;
	}

	public Panel getPanel()
	{
		return sp;
	}

	public Canvas getCanvas()
	{
		return this.main.getCanvas();
	}
}
