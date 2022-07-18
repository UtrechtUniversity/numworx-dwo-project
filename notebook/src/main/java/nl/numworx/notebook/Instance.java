package nl.numworx.notebook;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import java.awt.BorderLayout;
import java.net.URI;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.Constants;
import org.cbook.cbookif.LessonMode;
import org.cbook.cbookif.SuccessStatus;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.api.SwingBrowserProvider;

@SuppressWarnings("serial")
class Instance extends JPanel implements CBookWidgetInstanceIF, CBookEventListener {

	final URI hubBase;
	final CBookContext context;
	
	
	Instance(Locale locale, URI hubBase, CBookContext context) {
		super(new BorderLayout());
		this.hubBase = hubBase;
		this.context = context;
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
		String project = (String) arg0.get(Editor.PROJECT);
		String notebook = (String) arg0.get(Editor.NOTEBOOK);
		URI hub = hubBase;
		if (notebook != null) {
			if (project != null) {
				notebook = project + "/" + notebook;
			}
			while(notebook.startsWith("/")) notebook = notebook.substring(1);
			String user = (String) context.getProperty(Constants.LEARNER_NAME);
			hub = hub.resolve("user/"+ user + "/");
			hub = hub.resolve("notebooks/").resolve(notebook);		
		} else if (project != null) {
			while(project.startsWith("/")) project = project.substring(1);
			String user = (String) context.getProperty(Constants.LEARNER_NAME);
			hub = hub.resolve("user/"+ user + "/");
			hub = hub.resolve("lab/tree/").resolve(project);
		}
		
		url = hub.toASCIIString();
	}

	public void setState(Map<String, ?> arg0) {
	}

	public void start() {
		SwingBrowser browser = factory.newBrowser();
		add(browser.asComponent(), BorderLayout.CENTER);
		validate();
		//browser.loadContent("<H1>It works</H1>", "text/html");
		System.out.println(url);
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
