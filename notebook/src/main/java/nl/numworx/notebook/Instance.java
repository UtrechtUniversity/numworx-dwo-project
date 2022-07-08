package nl.numworx.notebook;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.LessonMode;
import org.cbook.cbookif.SuccessStatus;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.api.SwingBrowserProvider;

@SuppressWarnings("serial")
class Instance extends JPanel implements CBookWidgetInstanceIF, CBookEventListener {

	Instance(Locale locale) {
		super(new BorderLayout());
		setLocale(locale);
	}

	private String url = "about:blank";
	transient private SwingBrowserFactory factory;
	LessonMode lessonMode;
	
	public void addCBookEventListener(CBookEventListener arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public JComponent asComponent() {
		return this;
	}

	public CBookEventListener asEventListener() {
		return this;
	}

	public void destroy() {
	}

	public int getScore() {
		return 0;
	}

	public Hashtable<String, ?> getState() {
		return new Hashtable<>();
	}

	public SuccessStatus getSuccessStatus() {
		return SuccessStatus.PASSED;
	}

	public synchronized void init() {
		if (factory == null)
			factory = new SwingBrowserProvider().getFactory();

	}

	public void removeCBookEventListener(CBookEventListener arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void setAssessmentMode(AssessmentMode mode) {
		// TODO Auto-generated method stub

	}

	public void setLaunchData(Map<String, ?> arg0, Map<String, Number> arg1) {
		url ="https://hub-dev.dwo.nl/";
	}

	public void setState(Map<String, ?> arg0) {
	}

	public void start() {
		SwingBrowser browser = factory.newBrowser();
		add(browser.asComponent(), BorderLayout.CENTER);
		validate();
		//browser.loadContent("<H1>It works</H1>", "text/html");
		browser.loadURL(url);
	}

	public void stop() {
		super.removeAll();
	}

	public void acceptCBookEvent(CBookEvent arg0) {
	}

	public int getMaxScore() {
		return 0;
	}

}
