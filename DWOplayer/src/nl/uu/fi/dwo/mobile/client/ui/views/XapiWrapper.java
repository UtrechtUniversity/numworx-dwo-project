package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.HtmlLogFormatter;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.mobile.client.ui.ResizableDialogBox;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger.LogStrategy;

@Singleton
public class XapiWrapper implements ScheduledCommand, SMLogger.LogStrategy {
	
	private static final Logger LOG = Logger.getLogger(XapiWrapper.class.getName());

	private ScrollPanel scroller;
	private ResizableDialogBox popup;
	private VerticalPanel box;
	private int count;

	private LogStrategy delegate;

	private HasWidgetsLogHandler handler;
	
	class Formatter extends HtmlLogFormatter {

		public Formatter(boolean showStackTraces) {
			super(showStackTraces);
		}

		DateTimeFormat hhmm = DateTimeFormat.getFormat(PredefinedFormat.TIME_SHORT);
		@Override
		protected String getRecordInfo(LogRecord event, String newline) {
			Date date = new Date(event.getMillis());
			StringBuilder s = new StringBuilder();
			s.append(hhmm.format(date)).append(": ");
			return s.toString();
		}
	}

	@Inject XapiWrapper() {
		popup = new ResizableDialogBox(false, true, true, true);
		box = new VerticalPanel() { 
			@Override
			public void add(Widget w) {
				super.add(w);
				scroller.scrollToBottom();				
			}
		} ;
		scroller = new ScrollPanel(box);
		scroller.getElement().getStyle().setHeight(10, Style.Unit.EM);
		scroller.getElement().getStyle().setWidth(40, Style.Unit.EM);
		popup.setWidget(scroller);
		handler = new HasWidgetsLogHandler(box);
		handler.setFormatter(new Formatter(false));
		LOG.addHandler(handler);
		
	}

	public MenuItem getMenuItem() {
		popup.hide();
		return new MenuItem("xapi logs", this);
	}

	@Override
	public void execute() {		
		LOG.info("start logging");
		popup.setPopupPositionAndShow((w, h) -> { 
			popup.setPopupPosition(Window.getClientWidth() - w - 20, 90);
		});
	}

	@Override
	public Promise<String> saveStatement(Statement s) {
		int c = count++;
		LOG.info(c + ": saving " + s.context.contextActivities.parent.get(0).id + " " + s.result.response); 
		Promise<String> promise = delegate.saveStatement(s);
		promise.then(p -> call(p,c) , p-> fail(p, c));
		return promise;
	}
	
	public SMLogger.LogStrategy wrap(SMLogger.LogStrategy delegate) {
		this.delegate = delegate;
		return this;
	}

	public void fail(Promise<?> resolved, int c) throws Exception {
		LOG.log(Level.SEVERE, c + ": " +  resolved.getFailure(), resolved.getFailure());		
	}

	public Promise<String> call(Promise<String> resolved, int c) throws Exception {
		LOG.log(Level.INFO, c + ": success " + resolved.getValue());
		return resolved;
	}
	
	public void destroy() {
		//LOG.removeHandler(handler);
		handler.clear();
		popup.hide();
	}
}
