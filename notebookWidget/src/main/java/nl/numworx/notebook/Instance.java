package nl.numworx.notebook;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.SuccessStatus;

@SuppressWarnings("serial")
class Instance extends JPanel implements CBookWidgetInstanceIF, CBookEventListener {

	Instance(Locale locale) {
		setLocale(locale);
	}

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

	public Map<String, ?> getState() {
		return Collections.emptyMap();
	}

	public SuccessStatus getSuccessStatus() {
		return SuccessStatus.PASSED;
	}

	public void init() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	public void setState(Map<String, ?> arg0) {
		// TODO Auto-generated method stub

	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	public void acceptCBookEvent(CBookEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
