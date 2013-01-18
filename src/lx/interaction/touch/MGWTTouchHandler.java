/**
 * 
 */
package lx.interaction.touch;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.Touch;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

public class MGWTTouchHandler implements TouchHandler
{

	private Touch current;
	private Widget widget;

	public MGWTTouchHandler(TouchListener l, Widget widget)
	{
		listener = l;
		this.widget = widget;
	}

	private Touch getTouch(TouchEvent<?> event)
	{
		if (current != null)
		{
			int len = event.getTouches().length();
			for (int i = 0; i < len; i++)
			{
				if (event.getTouches().get(i).getIdentifier() == current.getIdentifier())
					return event.getTouches().get(i);
			}
			return null;
		}
		Touch touch = event.getTouches().get(0);
		return touch;
	}

	private int x, y;
	private TouchListener listener;

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		if (current != null)
			return;
		Element e = widget.getElement();
		Touch touch = getTouch(event);
		current = touch;

		x = getRelativeX(touch, e);
		y = getRelativeY(touch, e);
		listener.pointerPressed(x, y);
		event.getNativeEvent().preventDefault();
		event.getNativeEvent().stopPropagation();
	}

	private int getRelativeY(Touch touch, Element e)
	{
		return touch.getPageY() - (e != null ? e.getAbsoluteTop() : 0);
	}

	private int getRelativeX(Touch touch, Element e)
	{
		return touch.getPageX() - (e != null ? e.getAbsoluteLeft() : 0);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		Touch touch = getTouch(event);
		if (touch != null)
		{
			Element element = widget.getElement();
			x = getRelativeX(touch, element);
			y = getRelativeY(touch, element);
			listener.pointerDragged(x, y);
			event.getNativeEvent().preventDefault();
			event.getNativeEvent().stopPropagation();
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		Touch touch = getTouch(event);
		if (current != null && touch == null)
		{
			listener.pointerReleased(x, y);
			current = null;
			event.getNativeEvent().preventDefault();
			event.getNativeEvent().stopPropagation();
		}
	}

	@Override
	public void onTouchCanceled(TouchCancelEvent event)
	{
		Touch touch = getTouch(event);
		if (current != null && touch == null)
		{
			listener.pointerReleased(x, y);
			current = null;
			event.getNativeEvent().preventDefault();
			event.getNativeEvent().stopPropagation();
		}
	}
}