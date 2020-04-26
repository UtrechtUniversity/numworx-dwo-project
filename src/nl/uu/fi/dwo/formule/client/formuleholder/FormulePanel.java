package nl.uu.fi.dwo.formule.client.formuleholder;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.HandlerRegistrations;
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

class FormulePanel extends FlowPanel {

  FormulePanel() {
  }

  FormulePanel(String tag) {
    super(tag);
  }

  HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return addDomHandler(handler, MouseMoveEvent.getType());
  }

  HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return addDomHandler(handler, MouseUpEvent.getType());
  }

  HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return addDomHandler(handler, MouseDownEvent.getType());
  }
  
  HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
	  return addDomHandler(handler, MouseOutEvent.getType());
  }

  HandlerRegistration addMouseHandler(FormuleEditorTouchHandler handler) {
    return HandlerRegistrations.compose(
        addMouseOutHandler(handler),
        addMouseDownHandler(handler),
        addMouseMoveHandler(handler),
        addMouseUpHandler(handler));
  }

  private static final boolean NOTOUCH = false;
  private static final HandlerRegistration dummy = () -> {};
  
  HandlerRegistration addPointerHandler(
      FormuleEditorTouchHandler handler) {
	  
    if (NOTOUCH) return dummy;
	  
    return HandlerRegistrations.compose(
      addPointerDownHandler(handler),
      addPointerMoveHandler(handler),
      addPointerCancelHandler(handler),
      addPointerUpHandler(handler)
      );
  }

  private HandlerRegistration addPointerDownHandler(PointerDownHandler handler) {
    return addDomHandler(handler, PointerDownEvent.getType());
  }
  private HandlerRegistration addPointerMoveHandler(PointerMoveHandler handler) {
    return addDomHandler(handler, PointerMoveEvent.getType());
  }
  private HandlerRegistration addPointerUpHandler(PointerUpHandler handler) {
    return addDomHandler(handler, PointerUpEvent.getType());
  }
  private HandlerRegistration addPointerCancelHandler(PointerCancelHandler handler) {
    return addDomHandler(handler, PointerCancelEvent.getType());
  }
 
  
  
  public HandlerRegistration addTouchHandler(FormuleEditorTouchHandler handler) {
    return HandlerRegistrations.compose(addTouchStartHandler(handler), addTouchMoveHandler(handler),
        addTouchCancelHandler(handler), addTouchEndHandler(handler));
  }

  private HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
    return addDomHandler(handler, TouchStartEvent.getType());
  }

  private HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
    return addDomHandler(handler, TouchMoveEvent.getType());
  }

  private HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    return addDomHandler(handler, TouchEndEvent.getType());
  }

  private HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
    return addDomHandler(handler, TouchCancelEvent.getType());
  }
                                           
  
 
}
