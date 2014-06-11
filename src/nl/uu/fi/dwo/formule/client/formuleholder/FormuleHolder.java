package nl.uu.fi.dwo.formule.client.formuleholder;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

/**
 * Base class for viewing a formula
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleHolder implements TekstElement
{
	public static void installKeyboard(FormuleKeyboardIF keyb) {
		kb = keyb;
	}
	
	public static final FormuleClientBundle FORMULE_BUNDLE = GWT.create(FormuleClientBundle.class);
	
	private FormuleRegel main = null;

	private FormuleFont font = FormuleFont.createFromFontSize(16);
	private static FormuleFont defaultfont = FormuleFont.createFromFontSize(16);
	protected static FormuleKeyboardIF kb;
	private FlowPanel sp = null;
	private int ashoogte;
	
	private CssColor color = CssColor.make(0, 0, 0);

	protected boolean hasSelection = false;

	protected int selectionStartX = -1;

	protected int selectionStartY = 0;

	public FormuleHolder()
	{
		//main regel
		FormuleRegel regel = new FormuleRegel(this);
		//GWT.log("Font size:" + defaultfont.getFontStyle());
		main = regel;
		ashoogte = main.getAsHoogte();//+(main.getFont().getAscent() - 1)/2 + 1;
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
	
	public CssColor getColor()
	{
		return this.color;
	}
			

	public void setFont(FormuleFont fm)
	{
		this.font = fm;
		this.getMainRegel().setFont(this.font);
		this.paint();
		ashoogte = getMainRegel().getAsHoogte();
	}
	
	public void setColor(CssColor c)
	{
		this.color = c;
		this.getMainRegel().setColor(c);
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
		TouchPanel tp = new TouchPanel();
		tp.add(this.main.getCanvas());
		sp.add(tp);
		return sp;
	}
	
	public TouchPanel getTouchPanel()
	{
		TouchPanel tp = new TouchPanel();
		tp.add(this.main.getCanvas());
		return tp;
	}

	public Panel getPanel()
	{
		return sp;
	}

	public Canvas getCanvas()
	{
		return this.main.getCanvas();
	}
	
	/**
	 * if true, draw an empty [], if needed.
	 * @return
	 */
	public boolean isInputNeeded() {
		return true;
	}

	public void clearSelection() {
	}

	public void endSelection(int x, int y) {		
	}
	
	public void requestFocus() {
		kb.setEditor(null);
	}

	/**
	 * Selection
	 */
	public void startSelection(int x, int y) {
		this.selectionStartX = x;
		this.selectionStartY = y;
	}

	public FormuleRegel getCurrentRegel() {
		return getMainRegel();
	}

	public void setCurrentElement(FormuleElement element) {
	}

	public void setCurrentRegel(FormuleRegel formuleRegel) {		
	}

	public String getSelectionString() {
		if(hasSelection())
			return getCurrentRegel().getSelectionString();
		return "";
	}

	public boolean hasSelection() {
		return this.hasSelection;
	}

	@Override
	public int getAsHoogte() {
		//return main.getAsHoogte();
		return ashoogte;
	}

	@Override
	public int getHeight() {
		
		return main.height;
	}

	@Override
	public int getWidth() {
		return main.getWidth();
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		main.setAsHoogte(ashoogte);
		//main.setAsHoogte(ashoogte);
	}
}
