package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.utils.HasHide;
import nl.uu.fi.dwo.mobile.utils.PopupFacade.PopupListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;

public class PopupButton extends Composite implements ClickHandler, /*TouchStartHandler, MouseDownHandler,*/ HasHide {

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
					deferTearDown();
				}});
			addTouchStartHandler(new TouchStartHandler() {

				@Override
				public void onTouchStart(TouchStartEvent event) {
					event.stopPropagation();
					event.preventDefault();
					deferTearDown();
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

	/**
	 * These are the default onShow/onHide methods.
	 * assert view != null
	 * @deprecated make your own interface.
	 * @author wim
	 * @see nl.uu.fi.dwo.mobile.utils.PopupFacade
	 *
	 */	
	class DefaultListener implements PopupListener {

		@Override
		public void onShow() {
			if( view instanceof FormuleEditorWithAnswer)
			{	
				((FormuleEditorWithAnswer) view).setEnabled(false);
				
				if (content instanceof FormuleEditorWithSteps)
				{
					// zet isUitgeklapt t.b.v. verwerken antwoord FormuleEditorWithSteps of FormuleEditorWithAnswer
					((FormuleEditorWithSteps) content).setUitgeklapt(true);
					((FormuleEditorWithSteps) content).setIsBoss(false);
				}
				
				state = view.getState();
				view.setState(state);
	// Fire popup event ......			
				if (content instanceof FormuleEditorWithSteps)
				{
					// als de state is gezet is FEWS de baas
					((FormuleEditorWithSteps) content).setIsBoss(true);
				}
				
				
			}
			else if(state != null)
				view.setState(state);

			if (view instanceof TekstVakPanel) {
				((TekstVakPanel)view).setPopupUsed();
			}

			if(content instanceof FormuleEditorWithSteps)
			{	
				if(((FormuleEditorWithSteps) content).getEditor() != null)
				{	
					FormuleEditor editor = ((FormuleEditorWithSteps) content).getEditor();
					editor.requestFocus();
			
					//om te zorgen dat cursor ook getekend wordt:
					if(editor.getCurrentElement() == null)
					{	editor.setCurrentElementRepaint(editor.getMainRegel());
					}
				}
			}

		}

		@Override
		public void onHide() {
				if (view instanceof FormuleEditorWithAnswer) 
				{
					((FormuleEditorWithAnswer) view).haalAntwoordOp();
				}
				state = view.getState();
				if (view instanceof FormuleEditorWithAnswer)
				{
					view.setState(state);
					((FormuleEditorWithAnswer) view).setEnabled(true);
				}

				if (content instanceof FormuleEditorWithSteps) {
					// zet isUitgeklapt t.b.v. verwerken antwoord FormuleEditorWithSteps of FormuleEditorWithAnswer
					((FormuleEditorWithSteps) content).setUitgeklapt(false);
				}
		}
		
	}
	

	static final PopupListener NOVIEW_LISTENER =  new PopupListener() {
		public void onShow() {}
		public void onHide() {}		
	};
	
	ButtonBase btn;
	IsWidget content;
	DialogBox box;
	InteractionView view;
	HashMap<String,Object> state;
	PopupListener listener;

	class NothingOnTouch implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler  {

		@Override
		public void onTouchCancel(TouchCancelEvent event) {
			event.stopPropagation();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.stopPropagation();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			event.stopPropagation();
		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.stopPropagation();		
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			event.stopPropagation();
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			event.stopPropagation();
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
			event.stopPropagation();
		}
	
	}

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
		this(content, new Image(DWOplayer.DWO_BUNDLE.appletknop().getSafeUri()), null, null);
	}
	
	public PopupButton(StubView view) {
		this(view.getWidget(),new Image(DWOplayer.DWO_BUNDLE.appletknop().getSafeUri()), view, null);
	}

	public PopupButton(IsWidget content, Image image, InteractionView view, PopupListener popupListener) {
		Image img = image;
		this.listener = popupListener == null ? 
				(view == null ? NOVIEW_LISTENER : new DefaultListener()) 
			  : popupListener;
		if(img == null)
			img = new Image(DWOplayer.PARAMETERS.getResource("images/resources/tekstknop.gif"));
		btn = new PushButton(img);
		btn.getElement().getStyle().setPadding(0, Style.Unit.PX);
		btn.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		btn.addClickHandler(this);
// do not propagate...		
		NothingOnTouch tt = new NothingOnTouch();
		btn.addMouseDownHandler(tt);
		btn.addMouseMoveHandler(tt);
		btn.addMouseUpHandler(tt);
		btn.addTouchStartHandler(tt);
		btn.addTouchCancelHandler(tt);
		btn.addTouchEndHandler(tt);
		btn.addTouchMoveHandler(tt);
		this.content = content;
		this.view = view;
		initWidget(btn);
	}

	//private int clientX,clientY;
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
// doe niets als je in het contentvak klikt middels stopPropagation
			NothingOnTouch nt = new NothingOnTouch();
			FocusPanel wrap = FocusOnTouch.wrap(content.asWidget(),false);
			wrap.addDomHandler(nt, TouchStartEvent.getType());
			wrap.addDomHandler(nt, TouchMoveEvent.getType());
			wrap.addDomHandler(nt, TouchEndEvent.getType());
			wrap.addDomHandler(nt, TouchCancelEvent.getType());
			
			box.setWidget(wrap);
		}
// fire popupevent here.
		if(!box.isShowing())
		{
			box.showRelativeTo(this);
			listener.onShow();
		}
	}

	public void hide() {
		if(box != null) 
		{	
			box.hide();
		}
	}

	public boolean popupShowing() {
		if(view == null) return true; // use delegate all the time.
		if(box != null) return box.isShowing(); // use delegate only if visible
		return false;
	}
	
//	@Override
//	public void onTouchStart(TouchStartEvent event) {
//		Touch touch = event.getChangedTouches().get(0);
//		clientX = touch.getClientX();
//		clientY = touch.getClientY();
//		//event.stopPropagation();
//	}

//	@Override
//	public void onMouseDown(MouseDownEvent event) {
//		clientX = event.getClientX();
//		clientY = event.getClientY();
//		event.stopPropagation();
//	}

	void deferTearDown() {
		OpdrNav.defer(
		  new ScheduledCommand() {
			  public void execute() {
				  tearDown();
			  }
		  }
		);
	}
	
	/*
	 *  deze kandidaat voor PrepareGetState.
	 *  
	 */
	void tearDown() {
		if(box != null  && box.isShowing()) 
		{	
			listener.onHide();
			box.hide();
		}
	}

	/**
	 * @return the state
	 */
	public HashMap<String, Object> getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(HashMap<String, Object> state) {
		this.state = state;
	}

	
	

}
