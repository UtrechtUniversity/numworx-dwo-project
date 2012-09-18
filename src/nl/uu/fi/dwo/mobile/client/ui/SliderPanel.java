package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleFont;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

/**
 * Allow zooming with a sliding bar
 * 
 * @author Danny Hendrix
 * 
 */
@Deprecated
public class SliderPanel
{
	private int panelheight = 0;
	private TouchButton button;
	private FlowPanel panel;

	private float buttony = 0;
	private float startTouchY = 0;

	private FormuleKeyboard keyboard;

	public SliderPanel(int height, FormuleKeyboard kb)
	{
		keyboard = kb;
		panelheight = height;
		buttony = height;

		panel = new FlowPanel();
		button = new TouchButton();
		panel.add(button);

		panel.setHeight((height + 40) + "px");
		panel.setWidth("30px");
		button.getElement().getStyle().setPosition(Position.RELATIVE);
		button.getElement().getStyle().setTop(this.buttony, Unit.PX);
		button.getElement().addClassName("button");
		button.setHeight("18px");

		panel.getElement().getStyle().setBackgroundColor("#444");

		button.addTouchHandler(new TouchHandler()
		{

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{

			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				event.preventDefault();

				if (keyboard != null)
				{
					int min = keyboard.getEditor().getDefaultFont().getFontSize();
					int max = 90;
					float dif = max - min;

					float percSlided = buttony / panelheight * 100;

					int value = max - Math.round((dif / 100) * percSlided);
					GWT.log(" " + value + "  " + percSlided + " " + dif);
					keyboard.getEditor().setFont(FormuleFont.createFromFontSize(value));
				}
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				event.preventDefault();
				int y = event.touches().get(0).getPageY();
				float move = y - startTouchY;
				buttony += move;

				if (buttony > panelheight)
					buttony = panelheight;
				if (buttony < 0)
					buttony = 0;

				button.getElement().getStyle().setTop(buttony, Unit.PX);
				startTouchY = y;

				if (keyboard != null)
				{
					int min = keyboard.getEditor().getDefaultFont().getFontSize();
					int max = 90;
					float dif = max - min;

					float percSlided = buttony / panelheight * 100;

					int value = max - Math.round((dif / 100) * percSlided);
					GWT.log(" " + value + "  " + percSlided + " " + dif);
					keyboard.getEditor().setFont(FormuleFont.createFromFontSize(value));
				}
			}

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.preventDefault();
				startTouchY = event.touches().get(0).getPageY();
			}
		});
	}

	public Panel getPanel()
	{
		return panel;
	}
}
