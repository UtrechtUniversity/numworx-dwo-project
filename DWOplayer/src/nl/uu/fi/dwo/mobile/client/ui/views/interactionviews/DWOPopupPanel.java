package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.ResizableContentIF;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;
import com.google.gwt.user.client.impl.DOMImpl;

public class DWOPopupPanel extends PopupPanel {
	
	private static Logger logger = Logger.getLogger("PopupPanel");
	
	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 100;
	
	private PopupListener popupListener;
	private Label closeButton;
	private HorizontalPanel headerPanel;
	private SimplePanel contentPanel;
	private Label titleLabel;
	private ResizableContentIF content;
	
	private Widget parent;
	
	private boolean moving = false;
	private int startX, startY;
	
	private int minWidth = MIN_WIDTH;
	private int minHeight = MIN_HEIGHT;

	private int dragMode;
	private boolean resizable;
	
	private HandlerRegistration mouseMoveHandler, mouseUpHandler, mouseDownHandler;
	private HandlerRegistration touchMoveHandler, touchEndHandler, touchStartHandler;
	private HandlerRegistration pointerMoveHandler, pointerUpHandler, pointerDownHandler;
	
	public DWOPopupPanel(String title, PopupListener popupListener) {
		super(false);
		this.popupListener = popupListener;
		
		this.getElement().getStyle().setProperty("touchAction", "none");
		
		
		getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		getElement().getStyle().setBorderColor(""+CssColor.make(120,150,202));
		getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		getElement().getStyle().setPadding(0, Style.Unit.PX);
		getElement().getStyle().setBackgroundColor(""+CssColor.make(255,255,255));
		getElement().getStyle().setProperty("boxShadow", "1px 1px 20px 3px #A6A6A6");
		
		closeButton = new Label("×");
		closeButton.getElement().getStyle().setPaddingRight(6, Style.Unit.PX);
		closeButton.getElement().getStyle().setWidth(20, Style.Unit.PX);
		closeButton.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		closeButton.getElement().getStyle().setFontSize(20, Unit.PX);
		
		titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setTextAlign(TextAlign.LEFT);
		titleLabel.getElement().getStyle().setFontSize(16, Unit.PX);
		titleLabel.getElement().getStyle().setPaddingLeft(6, Style.Unit.PX);
		titleLabel.getElement().getStyle().setMarginTop(2, Unit.PX);
		
		
		headerPanel = new HorizontalPanel();
		headerPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		headerPanel.getElement().getStyle().setBorderColor(""+CssColor.make(180,195,228));
		headerPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		headerPanel.getElement().getStyle().setColor(""+CssColor.make(120,150,202));
		headerPanel.setWidth("100%");
		headerPanel.add(titleLabel);
		headerPanel.add(closeButton);
		headerPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		
		contentPanel = new SimplePanel();
		contentPanel.getElement().getStyle().setBackgroundColor(""+CssColor.make(255,255,255));
		contentPanel.setWidth("100%");
		
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().getStyle().setPadding(6, Style.Unit.PX);
		hp.setWidth("100%");
		hp.add(headerPanel);
		vp.add(hp);
		vp.add(contentPanel);
		add(vp);
		
		MousePopupHandler mousePopupHandler = new MousePopupHandler();
		mouseMoveHandler = addDomHandler((MouseMoveHandler)mousePopupHandler, MouseMoveEvent.getType()); 
		mouseDownHandler = addDomHandler((MouseDownHandler)mousePopupHandler, MouseDownEvent.getType()); 
		mouseUpHandler = addDomHandler((MouseUpHandler)mousePopupHandler, MouseUpEvent.getType()); 
		addDomHandler(mousePopupHandler, MouseOutEvent.getType());
		
		TouchPopupHandler touchPopupHandler = new TouchPopupHandler();
		touchMoveHandler = addDomHandler((TouchMoveHandler)touchPopupHandler, TouchMoveEvent.getType()); 
		touchStartHandler = addDomHandler((TouchStartHandler)touchPopupHandler,TouchStartEvent.getType()); 
		touchEndHandler = addDomHandler((TouchEndHandler)touchPopupHandler, TouchEndEvent.getType()); 
		
		PointerPopupHandler pointerPopupHandler = new PointerPopupHandler();
		pointerMoveHandler = addDomHandler((PointerMoveHandler)pointerPopupHandler, PointerMoveEvent.getType()); 
		pointerUpHandler = addDomHandler((PointerUpHandler)pointerPopupHandler, PointerUpEvent.getType()); 
		pointerDownHandler = addDomHandler((PointerDownHandler)pointerPopupHandler, PointerDownEvent.getType()); 
	}
	
	private void removeMouseTouchHandlers() {
//		if(hasPointerSupport) {
//			mouseMoveHandler.removeHandler();
//			mouseDownHandler.removeHandler();
//			mouseUpHandler.removeHandler();
//			touchMoveHandler.removeHandler();
//			touchStartHandler.removeHandler();
//			touchEndHandler.removeHandler();
//		}
//		if(hasMouseSupport) {
//			pointerMoveHandler.removeHandler();
//			pointerDownHandler.removeHandler();
//			pointerUpHandler.removeHandler();
//			touchMoveHandler.removeHandler();
//			touchStartHandler.removeHandler();
//			touchEndHandler.removeHandler();
//		}
	}
	
	public void addContent(Widget contentWidget) {
		contentPanel.add(contentWidget);
	}
	
	public void setResizableContent(ResizableContentIF content) {
		this.content = content;
	}
	
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	
	public void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	private void deferTearDown() {
		OpdrNav.defer(
		  new ScheduledCommand() {
			  public void execute() {
				  tearDown();
			  }
		  }
		);
	}
	
	private void tearDown() {
		if(isShowing()) {	
			popupListener.onHide();
			hide();
		}
	}
	
	protected int calcDragMode(int x, int y)	{
		Rectangle r = new Rectangle(this);
		int d = 10;
		
		if(new Rectangle(r.x, r.y, d, d).contains(x, y))
			return 0;
		if(new Rectangle(r.x+r.w-d, r.y, d, d).contains(x, y))
			return 2;
		if(new Rectangle(r.x, r.y+r.h-d, d, d).contains(x, y))
			return 6;
		if(new Rectangle(r.x+r.w-d, r.y+r.h-d, d, d).contains(x, y))
			return 8;
		if(new Rectangle(r.x, r.y, r.w, d).contains(x, y))
			return 1;
		if(new Rectangle(r.x, r.y, d, r.h).contains(x, y))
			return 3;
		if(new Rectangle(r.x+r.w-d, r.y, d, r.h).contains(x, y))
			return 5;
		if(new Rectangle(r.x, r.y+r.h-d, r.w, d).contains(x, y))
			return 7;
		return -1;
	}
	
	protected static void updateCursor(int dm, com.google.gwt.dom.client.Element element)	{
		Cursor cursor;

		switch (dm) {
			case 0:
				cursor = Cursor.NW_RESIZE;
				break;
			case 1:
				cursor = Cursor.N_RESIZE;
				break;
			case 2:
				cursor = Cursor.NE_RESIZE;
				break;
			case 3:
				cursor = Cursor.W_RESIZE;
				break;
			case 5:
				cursor = Cursor.E_RESIZE;
				break;
			case 6:
				cursor = Cursor.SW_RESIZE;
				break;
			case 7:
				cursor = Cursor.S_RESIZE;
				break;
			case 8:
				cursor = Cursor.SE_RESIZE;
				break;
			default:
				cursor = Cursor.AUTO;
				break;
		}
		element.getStyle().setCursor(cursor);
	}

	protected void dragResizeWidget(int dx, int dy)	{
		int x = this.getAbsoluteLeft();
		int y = this.getAbsoluteTop();
		int width = 0;
		int height = 0;

		Widget widget = this.getWidget();

		// left + right
		if ((this.dragMode % 3) != 1){
			int w = widget.getOffsetWidth()+2;

			// left edge -> move left
			if ((this.dragMode % 3) == 0) {
				x += dx;
				w -= dx;
			}
			else 	{
				w += dx;
			}
			w = w < this.minWidth ? this.minWidth : w;
			width = w;
		}
		// up + down
		if ((this.dragMode / 3) != 1) {
			int h = contentPanel.getOffsetHeight()+2;

			// up = dy is negative
			if ((this.dragMode / 3) == 0) {
				y += dy;
				h -= dy;
			}
			else {
				h += dy;
			}

			h = h < this.minHeight ? this.minHeight : h;
			height = h;
		}
		if (this.content instanceof ResizableContentIF) {
			if (width > 0 && height == 0) {
				((ResizableContentIF) this.content).setWidth(width);
			}
			else if (width == 0 && height > 0) {
				((ResizableContentIF) this.content).setHeight(height);
			}
			else if (width > 0 && height > 0) {
				((ResizableContentIF) this.content).setSize(width, height);
			}
		}
		if (this.dragMode / 3 == 0 || this.dragMode % 3 == 0)
			setPopupPosition(x, y);
	}
	
	private void mouseTouchPointerDown(int x, int y) {
		removeMouseTouchHandlers();
		startX = x;
		startY = y;
		
		dragMode = calcDragMode(x,y);
		updateCursor(dragMode,this.getElement());
		
		if(new Rectangle(closeButton).contains(x,y))
			deferTearDown();
		else if(new Rectangle(headerPanel).contains(x,y)) {
			moving = true;
			DOM.setCapture(getElement()); 
		}
		else if(dragMode>0) {
			DOM.setCapture(getElement()); // werkt helaas niet voor pointerevents
		}
	}
	
	private void mouseTouchPointerMove(int x, int y) {
		if(moving) {
			int dx = x - startX;
			int dy = y - startY;
			parent = this.getParent();
			int w = parent.getOffsetWidth();
			int h = parent.getOffsetHeight();
			this.setPopupPosition(Math.min(w-20, Math.max(40-this.getOffsetWidth(),this.getAbsoluteLeft()+dx)), Math.min(h-20, Math.max(0,this.getAbsoluteTop()+dy)));
			this.show();
			startX = x;
			startY = y;
		}
		else if(dragMode>0) {
			int dx = x - startX;
			int dy = y - startY;
			dragResizeWidget(dx, dy);
			startX = x;
			startY = y;
		}
		else {
			int preDragMode = calcDragMode(x,y);
			updateCursor(preDragMode,this.getElement());
		}
	}

	private void mouseTouchPointerEnd(int x, int y) {
		moving = false;
		dragMode = -1;
		DOM.releaseCapture(getElement());
	}

	private boolean hasPointerSupport;
	private boolean hasMouseSupport;
	
	class MousePopupHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler {
		@Override
		public void onMouseUp(MouseUpEvent event) {
			mouseTouchPointerEnd(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
		@Override
		public void onMouseMove(MouseMoveEvent event) {
			mouseTouchPointerMove(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
		@Override
		public void onMouseDown(MouseDownEvent event) {
			hasMouseSupport = true;
			mouseTouchPointerDown(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
		@Override
		public void onMouseOut(MouseOutEvent event) {
			logger.log(Level.SEVERE, "mouse out " + event.getClientX() + "," + event.getClientY());
			mouseTouchPointerEnd(event.getClientX(),event.getClientY());
			
		}
	}
	class PointerPopupHandler implements PointerDownHandler, PointerMoveHandler, PointerUpHandler	{
		@Override
		public void onPointerUp(PointerUpEvent event) {
			//event.preventDefault();
			mouseTouchPointerEnd(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
		@Override
		public void onPointerMove(PointerMoveEvent event) {
			//event.preventDefault();
			mouseTouchPointerMove(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
		@Override
		public void onPointerDown(PointerDownEvent event) {
			hasPointerSupport = true;
			//event.preventDefault();
			mouseTouchPointerDown(event.getClientX(),event.getClientY());
			event.stopPropagation();
		}
	}
	class TouchPopupHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler {
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.preventDefault();
			mouseTouchPointerEnd(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY());
			event.stopPropagation();
		}
		@Override
		public void onTouchMove(TouchMoveEvent event) {
			event.preventDefault();
			mouseTouchPointerMove(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY());
			event.stopPropagation();
		}
		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			mouseTouchPointerDown(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY());
			event.stopPropagation();
		}
	}
	
	class Rectangle {
		int x,y,w,h;
		
		public Rectangle(int x,int y,int w,int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		public Rectangle(Widget w) {
			this.x = w.getAbsoluteLeft();
			this.y = w.getAbsoluteTop();
			this.w = w.getOffsetWidth();
			this.h = w.getOffsetHeight();
		}
		
		public boolean contains (int a, int b) {
			if(a>x && a<x+w && b>y && b<y+h)
				return true;
			return false;
		}
	}
	// copy from PopupPanel.class v 2.9.0
	public void position(final UIObject relativeObject, int offsetWidth, int offsetHeight) {
	    // Calculate left position for the popup. The computation for
	    // the left position is bidi-sensitive.

	    int textBoxOffsetWidth = relativeObject.getOffsetWidth();

	    // Compute the difference between the popup's width and the
	    // textbox's width
	    int offsetWidthDiff = offsetWidth - textBoxOffsetWidth;

	    int left;

	    if (LocaleInfo.getCurrentLocale().isRTL()) { // RTL case

	      int textBoxAbsoluteLeft = relativeObject.getAbsoluteLeft();

	      // Right-align the popup. Note that this computation is
	      // valid in the case where offsetWidthDiff is negative.
	      left = textBoxAbsoluteLeft - offsetWidthDiff;

	      // If the suggestion popup is not as wide as the text box, always
	      // align to the right edge of the text box. Otherwise, figure out whether
	      // to right-align or left-align the popup.
	      if (offsetWidthDiff > 0) {

	        // Make sure scrolling is taken into account, since
	        // box.getAbsoluteLeft() takes scrolling into account.
	        int windowRight = Window.getClientWidth() + Window.getScrollLeft();
	        int windowLeft = Window.getScrollLeft();

	        // Compute the left value for the right edge of the textbox
	        int textBoxLeftValForRightEdge = textBoxAbsoluteLeft
	            + textBoxOffsetWidth;

	        // Distance from the right edge of the text box to the right edge
	        // of the window
	        int distanceToWindowRight = windowRight - textBoxLeftValForRightEdge;

	        // Distance from the right edge of the text box to the left edge of the
	        // window
	        int distanceFromWindowLeft = textBoxLeftValForRightEdge - windowLeft;

	        // If there is not enough space for the overflow of the popup's
	        // width to the right of the text box and there IS enough space for the
	        // overflow to the right of the text box, then left-align the popup.
	        // However, if there is not enough space on either side, stick with
	        // right-alignment.
	        if (distanceFromWindowLeft < offsetWidth
	            && distanceToWindowRight >= offsetWidthDiff) {
	          // Align with the left edge of the text box.
	          left = textBoxAbsoluteLeft;
	        }
	      }
	    } else { // LTR case

	      // Left-align the popup.
	      left = relativeObject.getAbsoluteLeft();

	      // If the suggestion popup is not as wide as the text box, always align to
	      // the left edge of the text box. Otherwise, figure out whether to
	      // left-align or right-align the popup.
	      if (offsetWidthDiff > 0) {
	        // Make sure scrolling is taken into account, since
	        // box.getAbsoluteLeft() takes scrolling into account.
	        int windowRight = Window.getClientWidth() + Window.getScrollLeft();
	        int windowLeft = Window.getScrollLeft();

	        // Distance from the left edge of the text box to the right edge
	        // of the window
	        int distanceToWindowRight = windowRight - left;

	        // Distance from the left edge of the text box to the left edge of the
	        // window
	        int distanceFromWindowLeft = left - windowLeft;

	        // If there is not enough space for the overflow of the popup's
	        // width to the right of hte text box, and there IS enough space for the
	        // overflow to the left of the text box, then right-align the popup.
	        // However, if there is not enough space on either side, then stick with
	        // left-alignment.
// hier een iets ander besluit nemen: als het rechts en links niet past, dan left = windowLeft
	        if (distanceToWindowRight < offsetWidth
	            && distanceFromWindowLeft >= offsetWidthDiff) {
	          // Align with the right edge of the text box.
	          left -= offsetWidthDiff;
	        }
	      }
	    }

	    // Calculate top position for the popup

	    int top = relativeObject.getAbsoluteTop();

	    // Make sure scrolling is taken into account, since
	    // box.getAbsoluteTop() takes scrolling into account.
	    int windowTop = Window.getScrollTop();
	    int windowBottom = Window.getScrollTop() + Window.getClientHeight();

	    // Distance from the top edge of the window to the top edge of the
	    // text box
	    int distanceFromWindowTop = top - windowTop;

	    // Distance from the bottom edge of the window to the bottom edge of
	    // the text box
	    int distanceToWindowBottom = windowBottom
	        - (top + relativeObject.getOffsetHeight());

	    // If there is not enough space for the popup's height below the text
	    // box and there IS enough space for the popup's height above the text
	    // box, then position the popup above the text box. However, if there
	    // is not enough space on either side, then stick with displaying the
	    // popup below the text box.
	    if (distanceToWindowBottom < offsetHeight
	        && distanceFromWindowTop >= offsetHeight) {
	      top -= offsetHeight;
	    } else {
	      // Position above the text box
	      top += relativeObject.getOffsetHeight();
	    }
	    setPopupPosition(left, top);
	}
}
