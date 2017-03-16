package nl.uu.fi.dwo.mobile.client.ui.dwokb;

import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.formule.client.formuleobjects.TouchButton;
import nl.uu.fi.dwo.mobile.utils.ImageUtils;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;

/**
 * Tabs between different keyboard layouts
 * 
 * @author Danny Hendrix
 * 
 */
public class KeyBoardTabPanel
{
	static final int KEYB_STATIC_HEIGHT = 44;

	private FlowPanel main = new FlowPanel();
	private FormuleKeyboard kb;
	
	private FlowPanel contentpanel = new FlowPanel();
	private FlowPanel tabpanel = new FlowPanel();
	private FlowPanel tabcontentpanel = new FlowPanel();

	private FlowPanel staticpanel = new FlowPanel();

	private Vector<Panel> tabs = new Vector<Panel>();
	private Vector<Integer> tabHeights = new Vector<Integer>();
	private Vector<TouchButton> tabbuttons = new Vector<TouchButton>();
	private Vector<String> tabkeys = new Vector<String>();

	private int current = 0;

	private final TouchButton keyboardButton = new TouchButton();
//	private final TouchButton keyboardRemoveButton = new TouchButton();
	private final TouchButton digitsButton = new TouchButton();

	private boolean enabled = false;
	
	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public KeyBoardTabPanel(FormuleKeyboard formuleKeyboard)
	{
		this.kb = formuleKeyboard;
		contentpanel.add(tabcontentpanel);
		tabcontentpanel.getElement().getStyle().setBorderWidth(0, Unit.PX);

		staticpanel.getElement().getStyle().setHeight(KEYB_STATIC_HEIGHT, Style.Unit.PX);

		main.add(contentpanel);
		main.add(staticpanel);
		main.getElement().addClassName("keyboard");
		tabpanel.getElement().addClassName("keyboardtabs");

		//main.add(panel);
	}

	public void zetMaat() {
		zetMaatCommon();
		staticpanel.getElement().getStyle().setBackgroundImage("url("
				+ DWOplayer.PARAMETERS.getResource("images/resources/footerbgimage.png")
				+ ")");
		contentpanel.getElement().getStyle().setBackgroundImage("url("
				+ DWOplayer.PARAMETERS.getResource("images/resources/keyboardgradientimage-new.png")
				+ ")");

		Image buttonImage = ImageUtils.newImage("images/resources/keyboardbutton.png");
		buttonImage.getElement().getStyle().setMargin(5, Unit.PX);
		keyboardButton.add(buttonImage);
		keyboardButton.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				if(enabled) 
				{
					goTo(FormuleKeyboard.KEYBOARD);
				}
			}});
		if(!FormuleKeyboard.hasKeyboard)
		{
			buttonImage = ImageUtils.newImage("images/resources/digits.png");
			buttonImage.getElement().getStyle().setMargin(5, Unit.PX);
			digitsButton.add(buttonImage);
			digitsButton.addTapHandler(new TapHandler() {

				@Override
				public void onTap(TapEvent event) {
					if(enabled)
					{

						goTo(FormuleKeyboard.SCRIBBLE);
					}
				}});
		}
		setKBVisible(false);
	}

	private void zetMaatCommon() {
		main.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		main.getElement().getStyle().setBottom(0, Style.Unit.PX);
	}

//	public void zetMaatNoordhoff() {
//		zetMaatCommon();
//		//main.setPixelSize(886, -1);
//		main.getElement().getStyle().setFontSize(0, Style.Unit.PX); // anders main is 1 regel = 13 px
//		main.remove(staticpanel);
//		main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//		margins = 22; // die div.tabkeyboard-noordhoff (x2)
//	}

	public void zetMaatTrifork() {
		zetMaatCommon();
		main.getElement().getStyle().setFontSize(0, Style.Unit.PX); // anders main is 1 regel = 13 px
		main.remove(staticpanel);
		main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}

	
	public void clearStaticPanel()
	{
		staticpanel.clear();
		staticpanel.add(keyboardButton);
		if(! FormuleKeyboard.hasKeyboard) staticpanel.add(digitsButton);
//		staticpanel.add(keyboardRemoveButton);
	}

	public void apply()
	{
		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setProperty("clear", "both");
		tabpanel.add(sp);
	}
	static final Logger logger = Logger.getLogger("KeyboardTabPanel");

	HasHeight scrollPanel; int origHeight = 426 - 40;
	int origDelta = 0;
	
	private void resizeScrollPanel(int size) {
		origDelta = size;
		if(scrollPanel != null)
			scrollPanel.setHeight(origHeight - size);
	}
	public void setScrollPanel(HasHeight w, int h) {
		scrollPanel = w;
		origHeight = h;
		if(scrollPanel != null) scrollPanel.setHeight(origHeight - origDelta);
	}
	
	public void addTab(String buttonText, Panel p, int height)
	{
		p.setStylePrimaryName("tabkeyboard");
		p.addStyleDependentName(FormuleKeyBoardButtons.getDependentName());

		TouchButton button = new TouchButton();
		button.setText(buttonText);
		this.tabs.add(p);
		this.tabbuttons.add(button);
		this.tabHeights.add(height);
		//logger.info("addTab " + buttonText + " before " + tabkeys);
		this.tabkeys.add(buttonText);
		//logger.info("addTab " + buttonText + " added to " + tabkeys);

		this.tabpanel.add(this.tabbuttons.lastElement());
		this.tabcontentpanel.add(this.tabs.lastElement());
		p.getElement().getStyle().setDisplay(Display.NONE);

		final int index = this.tabs.size() - 1;

		button.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
				Panel panel = tabs.get(index);
				panel.getElement().getStyle().setDisplay(Display.BLOCK);
				int offsetHeight = extractHeight(index, panel);
				resizeScrollPanel(offsetHeight);
				current = index;
			}
		});
		button.getElement().getStyle().setFloat(Style.Float.LEFT);
	}

	public void hideTabButton(String panel)
	{
		int index = this.tabkeys.indexOf(panel);
		if(index >= 0 && index < tabkeys.size())
			tabbuttons.get(index).getElement().getStyle().setDisplay(Display.NONE);
		else 
		{
			logger.severe(" indexOf " + panel + " from " + tabkeys + " is " + index);
		}
	}

	public void goTo(String panel)
	{
		if(FormuleKeyboard.SCRIBBLE.equals(panel))
			kb.setWriteString();
		int index = this.tabkeys.indexOf(panel);
		tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
		Panel panel2 = tabs.get(index);
		panel2.getElement().getStyle().setDisplay(Display.BLOCK);
		int offsetHeight = extractHeight(index, panel2);
		resizeScrollPanel(offsetHeight);
		current = index;
	}

	public boolean isCurrent(String panel)
	{
		return this.tabkeys.indexOf(panel) == current;
	}

	public Panel getPanel()
	{
		return this.main;
	}

	public Panel getStaticPanel()
	{
		return this.staticpanel;
	}

	/**
	 * 
	 */
	void hideKeyboard() {
		tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
		resizeScrollPanel(0);
	}
	
	void blur() {
		hideKeyboard();
		setKBVisible(false);
	}

	private void setKBVisible(boolean b) {
		Display display = b ? Display.INLINE_BLOCK : Display.NONE;
		keyboardButton.getElement().getStyle().setDisplay(display);
		digitsButton.getElement().getStyle().setDisplay(display);
		
	}

	public void showKeyboard() {
		if(enabled) {
			tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
			Panel panel = tabs.get(current);
			panel.getElement().getStyle().setDisplay(Display.BLOCK);
			int offsetHeight = extractHeight(current, panel);
			resizeScrollPanel(offsetHeight);
			setKBVisible(true);
		}
	}
	
	public void showSoftKeyboard() {
		if (! FormuleKeyboard.hasKeyboard) 
			showKeyboard();
		else
			setKBVisible(true);
	}

	private static int margins = 0;
	private int extractHeight(final int index, Panel panel) {
		int offsetHeight = panel.getOffsetHeight() + margins; // binnenmaat en buitenmaat verschillend bij noordhoff
		logger.info("height = " + offsetHeight + " == " + tabHeights.get(index));
		if(Math.abs(offsetHeight-tabHeights.get(index)) >  20) {
			offsetHeight=tabHeights.get(index);
		}
		return offsetHeight;
	}
}
