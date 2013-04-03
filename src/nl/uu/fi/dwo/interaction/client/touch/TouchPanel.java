package nl.uu.fi.dwo.interaction.client.touch;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import com.google.gwt.user.client.ui.FlowPanel;

public class TouchPanel extends FlowPanel {

	static class TDownHandler implements com.google.gwt.event.dom.client.TouchStartHandler {
		TouchStartHandler handler;
		
		public TDownHandler(TouchStartHandler listener) {
			handler = listener;
		}

		@Override
		public void onTouchStart(
				com.google.gwt.event.dom.client.TouchStartEvent event) {
			handler.onTouchStart(new TouchStartEvent(event));
		}

	}

	static class TUpHandler implements com.google.gwt.event.dom.client.TouchEndHandler,
										com.google.gwt.event.dom.client.TouchMoveHandler 
	{
		TouchHandler handler;
		public TUpHandler(TouchHandler touchHandler) {
			handler = touchHandler;
		}
		@Override
		public void onTouchEnd(
				com.google.gwt.event.dom.client.TouchEndEvent event) {
			handler.onTouchEnd(new TouchEndEvent(event));	
		}
		@Override
		public void onTouchMove(
				com.google.gwt.event.dom.client.TouchMoveEvent event) {
			handler.onTouchMove(new TouchMoveEvent(event));
			
		}

	}

	static class MDownHandler implements MouseDownHandler {
		TouchStartHandler handler;

		@Override
		public void onMouseDown(MouseDownEvent event) {
			TouchStartEvent ev = new TouchStartEvent(event);
			handler.onTouchStart(ev);
		}

		public MDownHandler(TouchStartHandler handler) {
			super();
			this.handler = handler;
		}
	}
	
	class MUpHandler implements MouseUpHandler, MouseMoveHandler, MouseDownHandler {

		private TouchHandler touchHandler;
		private boolean mouseDown;

		public MUpHandler(TouchHandler touchHandler) {
			this.touchHandler = touchHandler;	
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			touchHandler.onTouchEnd(new TouchEndEvent(event));
			mouseDown = false;
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			final int nativeButton = event.getNativeButton();
			if( mouseDown && nativeButton == NativeEvent.BUTTON_LEFT)
			{
				touchHandler.onTouchMove(new TouchMoveEvent(event));
			}
			
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			mouseDown = true;
		}
		
	}
	
	
	public void addTouchHandler(
			TouchHandler touchHandler) {
		
		addTouchStartHandler(touchHandler);
		
		if(TouchStartEvent.isSupported())
		{
			final TUpHandler handler = new TUpHandler(touchHandler);
			addDomHandler(handler, TouchEndEvent.getType());
			addDomHandler(handler, TouchMoveEvent.getType());
			
		} else 
		{
			final MUpHandler handler = new MUpHandler(touchHandler);
			addDomHandler(handler, MouseUpEvent.getType());
			addDomHandler(handler, MouseMoveEvent.getType());
			addDomHandler(handler, MouseDownEvent.getType());
		}
		
	}
	
	public void addTouchStartHandler(TouchStartHandler listener) {
		if(TouchStartEvent.isSupported())
		{
			addDomHandler(new TDownHandler(listener), TouchStartEvent.getType());
		} else
			addDomHandler(new MDownHandler(listener), MouseDownEvent.getType());
	}


}
