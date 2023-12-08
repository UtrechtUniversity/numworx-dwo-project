package nl.uu.fi.dwo.formule.client.formuleholder;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.web.bindery.event.shared.HandlerRegistrations;
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerEventsSupport;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;
import com.google.gwt.user.client.Event;

/**
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorTouchHandler 
  implements MouseDownHandler, MouseUpHandler, MouseMoveHandler, 
             PointerDownHandler, PointerMoveHandler, PointerCancelHandler, PointerUpHandler, 
             TouchStartHandler, TouchMoveHandler, TouchCancelHandler, TouchEndHandler, MouseOutHandler
{
  
    public class MoveEvent {
      public final int x, y;

      MoveEvent(int x, int y) {
        this.x = x;
        this.y = y;
      }
  }

    public class StartEvent {
      public final int x, y;
      final EventTarget target;
      StartEvent(int x, int y, EventTarget target) {
        this.x = x;
        this.y = y;
        this.target = target;
      }
    }
  
    public class EndEvent {
      public final int x, y;

      EndEvent(int x, int y) {
        this.x = x;
        this.y = y;
      }
    }
    
    protected void onStart(StartEvent event) {
      
        LOG.info("onStart " + event.x + "," +event.y);
         
        EventTarget target = event.target;
        boolean when = Element.is(target) && (Element.as(target) == editor.getCanvas().getElement());
        when = PointerEventsSupport.isSupported();
        if(when) {
        	LOG.severe("CAPTURE " + event);
        	capture = editor.getAsPanel().getElement();
            Event.setCapture(capture);
        } 
        editor.requestFocus();

        int x = event.x;
        int y = event.y;

        editor.clearSelection();
        editor.startSelection(x, y);
        editor.endSelection(x, y);
        this.x = x;
        this.y = y;
    }
  
    protected void onEnd(EndEvent event) {
      LOG.info("onEnd " + event.x + "," +event.y);
      release();
      int x = event.x;
      int y = event.y;
      editor.endSelection(x, y);
      this.x = x; this.y = y;    
    }
    
	protected final FormuleHolder editor;
	//private final HashMap<String, Double> dif = new HashMap<String, Double>();
	protected int x,y;
	//private boolean soft;
	protected Element capture;
    private boolean mousedown;
	private static Logger LOG = Logger.getLogger("FormuleEditorTouchHandler");
	
	public FormuleEditorTouchHandler(FormuleHolder editor)
	{
		this.editor = editor;
		PointerEventsSupport.init();
		//LOG.severe( "supported " + PointerEventsSupport.isSupported());
	}

	HandlerRegistration oldRegistration;
	
	public HandlerRegistration initHandler() {
	  FormulePanel w = (FormulePanel) editor.getAsPanel();
	  HandlerRegistration mouseRegistration = HandlerRegistrations.compose(
	    w.addMouseDownHandler(this),
	    w.addMouseMoveHandler(this),
	    w.addMouseUpHandler(this));
	  HandlerRegistration pointerRegistration = 
	      HandlerRegistrations.compose(
	        w.addMouseOutHandler(this), // no pointerout
	        w.addPointerHandler(this));
	  HandlerRegistration touchRegistration = w.addTouchHandler(this);
	  oldRegistration = HandlerRegistrations.compose(mouseRegistration, touchRegistration);
    return HandlerRegistrations.compose(oldRegistration, pointerRegistration);
	}
	
	public void onTouchStart(TouchStartEvent event)
	{
		try
		{
		LOG.info("onTouchStart " + event.getTouches().length()  + ", " + event.getTouches().get(0).getIdentifier() + ", " + event.getTouches().get(0).getPageX());
		{
			event.preventDefault();
			event.stopPropagation();
		} 
			EventTarget target = event.getNativeEvent().getEventTarget();
		
			int x = event.getTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.getTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			onStart(new StartEvent(x, y, target));
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "onTouchStart: " + e, e);
		}
	}

	public void onTouchMove(TouchMoveEvent event)
	{
		{
			event.preventDefault();
			event.stopPropagation();
		}
		try
		{
			LOG.info("onTouchMove " + event.getTouches().length()  + ", " + event.getTouches().get(0).getIdentifier() + ", " + event.getTouches().get(0).getPageX());
			int x = event.getTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			
			int y = event.getTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();

			onMove(new MoveEvent(x,y));

		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "onTouchMove: " + e, e);
		}

	}

	public void onTouchEnd(TouchEndEvent event)
	{
		try
		{
			LOG.info("onTouchEnd " + event.getChangedTouches().length()  );
			if (event.getChangedTouches().length() > 0) {
				LOG.info("touches: " + event.getChangedTouches().get(0).getIdentifier() + ", " + event.getChangedTouches().get(0).getPageX());
			} else {
				LOG.severe("No ChangedTouches");
				return;
			}
			{
				event.preventDefault();
				event.stopPropagation();
			}
			int x = event.getChangedTouches().get(0).getPageX() - editor.getCanvas().getAbsoluteLeft();
			int y = event.getChangedTouches().get(0).getPageY() - editor.getCanvas().getAbsoluteTop();
			onEnd(new EndEvent(x,y));
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "onTouchEnd: " + e, e);
		}
		
	}

	public void onTouchCancel(TouchCancelEvent event)
	{
		try {
			LOG.info("onTouchCancel " + event.getChangedTouches().length()  + ", " + event.getChangedTouches().get(0).getIdentifier() + ", " + event.getChangedTouches().get(0).getPageX());
			release();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "onTouchCancel: " + e, e);
		}
	}

	private void release() {
		if(capture != null)
		{	Event.releaseCapture(capture);
			capture = null;
		}
	}

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    try {
		int x = event.getRelativeX(editor.getCanvas().getElement());
		int y = event.getRelativeY(editor.getCanvas().getElement());
		int btn = event.getNativeButton();
		MoveEvent move = new MoveEvent(x,y);
		if (mousedown)
		  onMove(move);
	} catch (Exception e) {
	    LOG.log(Level.SEVERE, "onMouseMove", e);
	}
  }

  protected void onMove(MoveEvent event) {
    LOG.info("onMove " + event.x + "," +event.y);
    int x = event.x;   
    int y = event.y;
    editor.endSelection(x, y);
    this.x = x; this.y = y;
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
	if (!mousedown) {
		LOG.severe("mouse up extra");
		release();
		return;
	}
	  
    try {
      mousedown = false;
      int x = event.getRelativeX(editor.getCanvas().getElement());
      int y = event.getRelativeY(editor.getCanvas().getElement());
      EndEvent end = new EndEvent(x,y);
      onEnd(end);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "onMouseUp", e);
    }    
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
		if (mousedown) {
			LOG.severe("MouseDown extra");
			return;
		}
    try {
		int x = event.getRelativeX(editor.getCanvas().getElement());
		int y = event.getRelativeY(editor.getCanvas().getElement());
		EventTarget target = event.getNativeEvent().getEventTarget();
		StartEvent start = new StartEvent(x,y,target);
		mousedown = true;
		onStart(start);
	} catch (Exception e) {
	    LOG.log(Level.SEVERE, "onMouseDown", e);
	}
  }

  @Override
  public void onPointerUp(PointerUpEvent event) {
    if (!mousedown) {
      LOG.log(Level.SEVERE, "pointer UPUP");
      release();
      return;
    }
    try {
      mousedown = false;
      int x = event.getRelativeX(editor.getCanvas().getElement());
      int y = event.getRelativeY(editor.getCanvas().getElement());
      EndEvent end = new EndEvent(x,y);
      onEnd(end);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "onPointerUp", e);
    }    
  }

  @Override
  public void onPointerCancel(PointerCancelEvent event) {
    release();
  }

  @Override
  public void onPointerMove(PointerMoveEvent event) {
    if (oldRegistration != null) {
      oldRegistration.removeHandler();
      oldRegistration = null;
    }
    event.preventDefault();
    event.stopPropagation();
    int x = event.getRelativeX(editor.getCanvas().getElement());
    int y = event.getRelativeY(editor.getCanvas().getElement());
    MoveEvent move = new MoveEvent(x,y);
    if (mousedown)
      onMove(move);
  }

  @Override
  public void onPointerDown(PointerDownEvent event) {
    if (oldRegistration != null) {
      oldRegistration.removeHandler();
      oldRegistration = null;
    }
    int x = event.getRelativeX(editor.getCanvas().getElement());
    int y = event.getRelativeY(editor.getCanvas().getElement());
    EventTarget target = event.getNativeEvent().getEventTarget();
    StartEvent start = new StartEvent(x,y,target);
    mousedown = true;
    event.preventDefault();
    event.stopPropagation();
    onStart(start);
  }

@Override
public void onMouseOut(MouseOutEvent event) {
	LOG.info("mouseOut");
	if (capture == null) mousedown = false;
}

}
