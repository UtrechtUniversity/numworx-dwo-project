package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

public class SVGButton extends SimplePanel {

	private static Logger logger = Logger.getLogger("SVGButton");
	protected String text = "";
	protected int width = 24;
	protected int height = 24;

	protected OMSVGSVGElement svg;
	protected SVGImage svgImage;
	protected OMSVGDocument doc;
	protected OMSVGRectElement borderActive;
	protected boolean fromResource;
	protected boolean handlersAdded;
	protected Image image;

	//protected ButtonListener listener = new DefaultButtonListener();

	private HandlerRegistration mouseMoveHandler, mouseUpHandler, mouseDownHandler, mouseOverHandler, mouseOutHandler;
	private HandlerRegistration touchMoveHandler, touchEndHandler, touchStartHandler;
	private HandlerRegistration pointerMoveHandler, pointerUpHandler, pointerDownHandler;

	protected CssColor defaultBorderColor = CssColor.make(211,229,244);
	protected CssColor defaultBorderColorActive = CssColor.make(38,115,182);
	protected CssColor defaultBgColor = CssColor.make(229, 240, 249);
	protected CssColor defaultForegroundColor = CssColor.make(120, 150, 202);
	protected CssColor defaultForegroundColorActive = CssColor.make(38,115,182);
	protected CssColor defaultTextColor = CssColor.make(49,71,112);
	
	protected ArrayList<ButtonListener> listeners = new ArrayList<ButtonListener>();
	
	protected Tooltip tooltip;
	
	public SVGButton(SVGResource resource) {
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		fromResource = true;
		doc = OMSVGParser.currentDocument();
		
		svg = resource.getSvg();
		svgImage = new SVGImage(svg);
 		svgImage.setPixelSize(width, height);
 		this.add(svgImage);
 		
 		addHandlers();
		//tooltip = new Tooltip(this, 10, 35, "", 5000, "");
	}
	
	public SVGButton(Image image) {
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.add(image);
 		this.image = image;
 		addHandlers();
		//tooltip = new Tooltip(this, 10, 35, "", 5000, "");
	}
	
	public SVGButton(String text) {
		this.text = text;
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		doc = OMSVGParser.currentDocument();
		svg = doc.createSVGSVGElement();
		svgImage = new SVGImage(svg);
		svgImage.setPixelSize(width, height);
		this.add(svgImage);
		draw();
		logger.info("SVG-element: "+svg.getElement().getInnerHTML());
		
		addHandlers();
		//tooltip = new Tooltip(this, 10, 35, "", 5000, "");
	}
	
	public void addHandlers() {
		MouseButtonHandler mouseButtonHandler = new MouseButtonHandler();
		mouseMoveHandler = addDomHandler((MouseMoveHandler) mouseButtonHandler, MouseMoveEvent.getType());
		mouseDownHandler = addDomHandler((MouseDownHandler) mouseButtonHandler, MouseDownEvent.getType());
		mouseUpHandler = addDomHandler((MouseUpHandler) mouseButtonHandler, MouseUpEvent.getType());
		mouseOverHandler = addDomHandler((MouseOverHandler) mouseButtonHandler, MouseOverEvent.getType());
		mouseOutHandler = addDomHandler((MouseOutHandler) mouseButtonHandler, MouseOutEvent.getType());

		TouchButtonHandler touchButtonHandler = new TouchButtonHandler();
		touchMoveHandler = addDomHandler((TouchMoveHandler) touchButtonHandler, TouchMoveEvent.getType());
		touchStartHandler = addDomHandler((TouchStartHandler) touchButtonHandler, TouchStartEvent.getType());
		touchEndHandler = addDomHandler((TouchEndHandler) touchButtonHandler, TouchEndEvent.getType());

		PointerButtonHandler pointerButtonHandler = new PointerButtonHandler();
		pointerMoveHandler = addDomHandler((PointerMoveHandler) pointerButtonHandler, PointerMoveEvent.getType());
		pointerUpHandler = addDomHandler((PointerUpHandler) pointerButtonHandler, PointerUpEvent.getType());
		
		handlersAdded = true;
	}
	
	public void setTooltip(String tooltipText) {
		tooltip = new Tooltip(this, 10, 35, tooltipText, 5000, "");
	}

	private void removeMouseTouchHandlers() {
		if (hasPointerSupport) {
			mouseMoveHandler.removeHandler();
			mouseDownHandler.removeHandler();
			//mouseUpHandler.removeHandler();
			touchMoveHandler.removeHandler();
			touchStartHandler.removeHandler();
			touchEndHandler.removeHandler();
		}
	}
	
	protected void setBorderActive(boolean b) {
		if(image!=null)
			return;
		if(b) {
			float e = (float)width/24;
			borderActive = doc.createSVGRectElement(e, e, width - 2 * e, height - 2 * e, 2 * e, 2 * e);
			borderActive.getStyle().setSVGProperty(SVGConstants.CSS_FILL_OPACITY_PROPERTY, "0.0");
			borderActive.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultBorderColorActive.toString());
			borderActive.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 1.2*e);
			svg.appendChild(borderActive);
		}
		else {
			svg.removeChild(borderActive);
			
		}
	}
	
	public void setText(String text) {
		if(image!=null)
			return;
		this.text = text;
		this.remove(svgImage);
		svg = doc.createSVGSVGElement();
		svgImage = new SVGImage(svg);
		svgImage.setPixelSize(width, height);
		this.add(svgImage);
		draw();
		if(!handlersAdded)
			addHandlers();
	}

	public OMSVGSVGElement getSVG() {
		return svg;
	}
	
	public String getText() {
		return text;
	}
	
	public void setEnabled(boolean b) {
	}

	public void setSize(int width, int height) {
		
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		// svgImage.setPixelSize(width, height);
		this.width = width;
		this.height = height;
		if(image!=null)
			return;
		if(fromResource) {
			svgImage.setPixelSize(width, height);
			return;
		}
		this.remove(svgImage);
		svg = doc.createSVGSVGElement();
		svgImage = new SVGImage(svg);
		svgImage.setPixelSize(width, height);
		this.add(svgImage);
		draw();
		if(!handlersAdded)
			addHandlers();
	}

	public void draw() {
		float w = width;
		float h = height;
		float e = w / 24;
		OMSVGRectElement rect = doc.createSVGRectElement(e, e, width - 2 * e, height - 2 * e, 2 * e, 2 * e);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultBgColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, defaultBorderColor.toString());
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + e);
		svg.appendChild(rect);
		
		OMSVGTextElement label = doc.createSVGTextElement(4*e,h/2+3*e, OMSVGLength.SVG_LENGTHTYPE_PX, text);
		label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_SIZE_PROPERTY, "12");
		label.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, defaultTextColor.toString());
		label.getStyle().setSVGProperty(SVGConstants.CSS_FONT_WEIGHT_PROPERTY,  SVGConstants.CSS_NORMAL_VALUE);
		svg.appendChild(label);
	}

	private boolean hasPointerSupport = false;

	class MouseButtonHandler
			implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOverHandler, MouseOutHandler {
		@Override
		public void onMouseUp(MouseUpEvent event) {
			if(tooltip!=null) {
				tooltip.cancelShow();
				tooltip.hide();
			}
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			callOnClick();
			event.stopPropagation();
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			if(tooltip!=null)
				tooltip.show();
			setBorderActive(true);
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			if(tooltip!=null) {
				tooltip.cancelShow();
				tooltip.hide();
			}
			setBorderActive(false);
		}
	}

	class PointerButtonHandler implements PointerDownHandler, PointerMoveHandler, PointerUpHandler {
		@Override
		public void onPointerUp(PointerUpEvent event) {
			// event.preventDefault();
			if(tooltip!=null) {
				tooltip.cancelShow();
				tooltip.hide();
			}
			setBorderActive(false);
			event.stopPropagation();
		}

		@Override
		public void onPointerMove(PointerMoveEvent event) {
			// event.preventDefault();
			event.stopPropagation();
		}

		@Override
		public void onPointerDown(PointerDownEvent event) {
			hasPointerSupport = true;
			if(tooltip!=null) {
				tooltip.cancelShow();
				tooltip.hide();
			}
			//setActive(true);
			// event.preventDefault();
			removeMouseTouchHandlers();
			logger.info("pointerDown");
			callOnClick();
			event.stopPropagation();
		}
	}

	class TouchButtonHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler {
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.preventDefault();
			event.stopPropagation();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			event.preventDefault();
			event.stopPropagation();
		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			callOnClick();
			event.stopPropagation();
		}
	}

	public ButtonListener addButtonListener(ButtonListener listener) {
		this.listeners.add(listener);
		return listener;
	}
	
	public void removeButtonListener(ButtonListener listener) {
		this.listeners.remove(listener);
	}
	
	private void callOnClick() {
		for(int i = 0 ; i<listeners.size() ; i++) {
			listeners.get(i).onClick(SVGButton.this);
		}
		
	}

	public interface ButtonListener {
		public void onClick(Object sender);
	}

	public class DefaultButtonListener implements ButtonListener {
		public void onClick(Object sender) {

		}
	}

	private class Tooltip extends PopupPanel {
		private int delay;
		private static final int DEFAULT_OFFSET_X = 10;
		private static final int DEFAULT_OFFSET_Y = 35;
		private Widget sender;
		private int offsetX;
		private int offsetY;
		Timer tShow;

		public Tooltip(Widget sender, int offsetX, int offsetY, final String text, final int delay,
				final String styleName) {
			super(true);
			this.sender = sender;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.delay = delay;

			HTML contents = new HTML(text);
			add(contents);

			getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			getElement().getStyle().setBorderColor("" + CssColor.make(120, 150, 202));
			getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
			getElement().getStyle().setPadding(3, Style.Unit.PX);
			getElement().getStyle().setPaddingLeft(5, Style.Unit.PX);
			getElement().getStyle().setPaddingRight(5, Style.Unit.PX);
			getElement().getStyle().setBackgroundColor("" + CssColor.make(237,239,241));
			getElement().getStyle().setProperty("boxShadow", "1px 1px 5px 2px #D6D6D6");
			getElement().getStyle().setFontSize(12, Unit.PX);
			getElement().getStyle().setColor(""+CssColor.make(49,71,112));

			// setStyleName(styleName);
		}
		
		public void cancelShow() {
			tShow.cancel();
		}

		public void show() {

			int left = sender.getAbsoluteLeft() + offsetX;
			int top = sender.getAbsoluteTop() + offsetY;
			setPopupPosition(left, top);
			tShow = new Timer() {
				public void run() {
					Tooltip.super.show();
					Timer tHide = new Timer() {
						public void run() {
							Tooltip.this.hide();
						}
					};
					tHide.schedule(delay);
				}
			};
			tShow.schedule(1000);
		}
	}
}
