package nl.uu.fi.dwo.mobile.client.ui;

import java.util.logging.Logger;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;


public class MessageEvent extends GwtEvent<MessageEventHandler>{

  public static final Type<MessageEventHandler> TYPE = new Type<>();
  private static Logger LOG = Logger.getLogger(MessageEvent.class.getName());

  @Override
  public Type<MessageEventHandler> getAssociatedType() {
    return TYPE;
  }

  public String getMessage() {
    return message;
  }
  public String getOrigin() {
    return origin;
  }
   
  @Override
  protected void dispatch(MessageEventHandler handler) {
    LOG.info("dispatch " + message);
    handler.onMessage(this);
  }

  private static EventBus bus;
  private static MessageEvent shared = new MessageEvent();

  private MessageEvent() {
  }

  private String message,origin;
  private static void fire(String message,String origin) {
    shared.message = message;
    shared.origin = origin;
    if(bus != null) {
      LOG.info("fire messageEvent " + message  + " from " + origin);
      bus.fireEvent(shared);
    }
    
  }
  
  private static native void injectEventListener() /*-{
  function postMessageListener(e) {
      @nl.uu.fi.dwo.mobile.client.ui.MessageEvent::fire(Ljava/lang/String;Ljava/lang/String;)(e.data, e.origin); // call function with the name
  }
  if (window.addEventListener) {
      // "Normal" browsers
      $wnd.addEventListener("message", postMessageListener, false);
  } else {
      // fucking IE
      $wnd.attachEvent("onmessage", postMessageListener, false);
  }
}-*/;

  static {
    injectEventListener();
  }
  
  public static void initialize(EventBus bus) {
      MessageEvent.bus = bus;
  }

  public static MessageEvent getLastEvent() {
    return shared;
  }
  
}
