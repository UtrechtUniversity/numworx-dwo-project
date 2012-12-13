/**
 * 
 */
package lx.interaction.touch;


import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class GWTMouseHandler
implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {
	protected boolean mouseDown;
	private TouchListener listener;

	public GWTMouseHandler(TouchListener l) {
		listener = l;
	}
	public void onMouseDown(MouseDownEvent event) {
		int x = event.getX();
		int y = event.getY();
		if(event.getNativeButton() == NativeEvent.BUTTON_LEFT)
		{
			mouseDown = true;
			listener.pointerPressed(x,y);
		}
	}
	public void onMouseUp(MouseUpEvent event) {
		int x = event.getX();
		int y = event.getY();
		if(event.getNativeButton() == NativeEvent.BUTTON_LEFT)
		{
			mouseDown = false;
			listener.pointerReleased(x,y);
		}
	}
	public void onMouseMove(MouseMoveEvent event) {
		int x = event.getX();
		int y = event.getY();
		if(mouseDown && event.getNativeButton() == NativeEvent.BUTTON_LEFT)
			listener.pointerDragged(x,y);
		}
	
}