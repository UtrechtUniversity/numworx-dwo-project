package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;

public class PopupButton extends Composite implements ClickHandler, TouchStartHandler, MouseDownHandler {

	public class CaptionImpl extends Composite implements Caption {

		FlowPanel flow = new FlowPanel();
		HTML btn;
		private DialogBox.CaptionImpl label;
		public CaptionImpl() {
			btn  = new DialogBox.CaptionImpl();
			btn.setHTML("<span class='btn btn-danger'><i class='fa fa-times fa-lg'></i> &nbsp;</span>");
			Style btnStyle = btn.getElement().getStyle();
			btnStyle.setFloat(Style.Float.LEFT);
			btnStyle.setWidth(2, Unit.EM);
			label = new DialogBox.CaptionImpl();
			label.setText("Popup");
			flow.add(btn);
			flow.add(label);
			initWidget(flow);
			addMouseDownHandler(new MouseDownHandler(){

				@Override
				public void onMouseDown(MouseDownEvent event) {
					event.stopPropagation();
					event.preventDefault();
					tearDown();
				}});
			addTouchStartHandler(new TouchStartHandler() {

				@Override
				public void onTouchStart(TouchStartEvent event) {
					event.stopPropagation();
					event.preventDefault();
					tearDown();
				}
				
			});
			
		}

		@Override
		public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
			return btn.addMouseDownHandler(handler);
		}
		public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
			return btn.addDomHandler(handler, TouchStartEvent.getType());
		}

		
		@Override
		public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
			return null;
		}

		@Override
		public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
			return null;
		}

		@Override
		public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
			return null;
		}

		@Override
		public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
			return null;
		}

		@Override
		public HandlerRegistration addMouseWheelHandler(
				MouseWheelHandler handler) {
			return null;
		}

		@Override
		public String getHTML() {
			return label.getHTML();
		}

		@Override
		public void setHTML(String html) {
			label.setHTML(html);
		}

		@Override
		public String getText() {
			return label.getText();
		}

		@Override
		public void setText(String text) {
			label.setText(text);
		}

		@Override
		public void setHTML(SafeHtml html) {
			label.setHTML(html);
		}

	}

	static class MyAnchor extends Anchor {

		public MyAnchor(Element element) {
			super(element);
			// TODO Auto-generated constructor stub
		}


	}

	ButtonBase btn;
	IsWidget content;
	DialogBox box;
	InteractionView view;
	HashMap<String,Object> state;

	
	class DragOnTouch implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler, com.google.gwt.animation.client.AnimationScheduler.AnimationCallback  {
		int x,y;
		boolean track;
		AnimationHandle handle;
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			track = false;
			if(handle != null) handle.cancel();
			handle = null;
			box.onMouseMove(box.getCaption().asWidget(), x, y);
			box.onMouseUp(box.getCaption().asWidget(), x, y);
			//logger.info("touch end " + x + "," + y);
			event.stopPropagation();
			event.preventDefault();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			getXY(event);
//			box.onMouseMove(box.getCaption().asWidget(), x, y);
			//logger.info("touch move " + x + "," + y);
		}

		void getXY(TouchEvent<?> event) {
			x = event.getTouches().get(0).getRelativeX(box.getElement());
			y = event.getTouches().get(0).getRelativeY(box.getElement());
			event.stopPropagation();
			event.preventDefault();
		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			getXY(event);
			box.onMouseDown(box.getCaption().asWidget(), x, y);
			track = true;
			handle = AnimationScheduler.get().requestAnimationFrame(this,box.getElement());
			//logger.info("touch start " + x + "," + y);
		}

		@Override
		public void onTouchCancel(TouchCancelEvent event) {
			track = false;
			box.onMouseUp(box.getCaption().asWidget(), x, y);
		}

		@Override
		public void execute(double timestamp) {
			if(track) {
				box.onMouseMove(box.getCaption().asWidget(), x, y);
				handle = AnimationScheduler.get().requestAnimationFrame(this,box.getElement());
			}
		}
		
	}
	
	
	
	public PopupButton(IsWidget content) {
		this(content, new Image(DWOplayer.DWO_BUNDLE.appletknop().getSafeUri()), null);
	}
	
	public PopupButton(StubView view) {
		this(view.getWidget(),new Image(DWOplayer.DWO_BUNDLE.appletknop().getSafeUri()), view);
	}

	public PopupButton(IsWidget content, Image image, InteractionView view) {
		Image img = image;
		btn = new PushButton(img);
		btn.getElement().getStyle().setPadding(0, Style.Unit.PX);
		btn.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		btn.addClickHandler(this);
		btn.addTouchStartHandler(this);
		btn.addMouseDownHandler(this);
		this.content = content;
		this.view = view;
		initWidget(btn);
	}

	private int clientX,clientY;
	private static Logger logger = Logger.getLogger("PopupButton");
	@Override
	public void onClick(ClickEvent event) {
		if(box == null) {
			DialogBox.Caption caption = new CaptionImpl();
			box = new DialogBox(false,false, caption);
			DragOnTouch t = new DragOnTouch();
			box.addDomHandler(t, TouchStartEvent.getType());
			box.addDomHandler(t, TouchMoveEvent.getType());
			box.addDomHandler(t, TouchEndEvent.getType());
			box.addDomHandler(t, TouchCancelEvent.getType());
			box.setWidget(FocusOnTouch.wrap(content.asWidget(),false));
		}
		if(!box.isShowing() && view != null && view instanceof FormuleEditorWithAnswer)
		{	state = view.getState();
			view.setState(state);
		}
		else if(!box.isShowing() && view != null && state != null)
			view.setState(state);
		
		if(!box.isShowing() )
				box.showRelativeTo(this);		
	}

	public void hide() {
		if(box != null) box.hide();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		Touch touch = event.getChangedTouches().get(0);
		clientX = touch.getClientX();
		clientY = touch.getClientY();
		event.stopPropagation();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		clientX = event.getClientX();
		clientY = event.getClientY();
		event.stopPropagation();
	}

	void tearDown() {
		if(view != null)
		{	if(view instanceof FormuleEditorWithAnswer)
			{
				((FormuleEditorWithAnswer) view).haalAntwoordOp();
			}
			state = view.getState();
			if(view instanceof FormuleEditorWithAnswer)
				view.setState(state);
		}
		box.hide();
	}

	
	

}
