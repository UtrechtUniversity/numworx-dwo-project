package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartHandler;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Tabs between different keyboard layouts
 * 
 * @author Danny Hendrix
 * 
 */
public class KeyBoardTabPanel
{
	private FlowPanel main = new FlowPanel();
	
	private FlowPanel contentpanel = new FlowPanel();
	private FlowPanel tabpanel = new FlowPanel();
	private FlowPanel tabcontentpanel = new FlowPanel();

	private FlowPanel staticpanel = new FlowPanel();

	private Vector<Panel> tabs = new Vector<Panel>();
	private Vector<TouchButton> tabbuttons = new Vector<TouchButton>();
	private Vector<String> tabkeys = new Vector<String>();

	private int current = 0;

//	private final TouchButton keyboardButton = new TouchButton();
//	private final TouchButton keyboardRemoveButton = new TouchButton();
//	private final TouchButton digitsButton = new TouchButton();

	private boolean enabled = false;
	
	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public KeyBoardTabPanel()
	{

		contentpanel.add(tabcontentpanel);
		//FIXME CSS contentpanel.getElement().getStyle().setBackgroundImage("url(images/resources/keyboardgradientimage-new.png)");
		tabcontentpanel.getElement().getStyle().setBorderWidth(0, Unit.PX);

		staticpanel.getElement().getStyle().setHeight(4, Style.Unit.PX);
		//FIXME CSS staticpanel.getElement().getStyle().setBackgroundImage("url(images/resources/footerbgimage.png)");

		tabcontentpanel.setStylePrimaryName("tabkeyboard");
		tabcontentpanel.addStyleDependentName(FormuleKeyBoardButtons.getDependentName());
		main.add(contentpanel);
		main.add(staticpanel);
		main.getElement().addClassName("keyboard");
		tabpanel.getElement().addClassName("keyboardtabs");

		//main.add(panel);
	}

	public void zetMaat() {
		main.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		main.getElement().getStyle().setBottom(0, Style.Unit.PX);
	}

	public void zetMaatNoordhoff() {
		zetMaat();
		main.setPixelSize(886, -1);
		main.getElement().getStyle().setFontSize(0, Style.Unit.PX); // anders main is 1 regel = 13 px
		staticpanel.setPixelSize(-1, 1);
		main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}

	public void clearStaticPanel()
	{
		staticpanel.clear();
//		staticpanel.add(keyboardButton);
//		if(! FormuleKeyboard.hasKeyboard) staticpanel.add(digitsButton);
//		staticpanel.add(keyboardRemoveButton);
	}

	public void apply()
	{
		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setProperty("clear", "both");
		tabpanel.add(sp);
	}
	static final Logger logger = Logger.getLogger("KeyboardTabPanel");

	public void addTab(String buttonText, Panel p)
	{
		TouchButton button = new TouchButton();
		button.setText(buttonText);
		this.tabs.add(p);
		this.tabbuttons.add(button);
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
				tabs.get(index).getElement().getStyle().setDisplay(Display.BLOCK);
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
		int index = this.tabkeys.indexOf(panel);
		tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
		tabs.get(index).getElement().getStyle().setDisplay(Display.BLOCK);
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
	}

	public void showKeyboard() {
		if(enabled) {
			tabs.get(current).getElement().getStyle().setDisplay(Display.NONE);
			tabs.get(0).getElement().getStyle().setDisplay(Display.BLOCK);
			current = 0;

		}
	}
}
