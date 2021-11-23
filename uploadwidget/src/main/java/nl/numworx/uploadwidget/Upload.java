package nl.numworx.uploadwidget;

import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventHandler;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.SuccessStatus;

public class Upload extends JPanel implements CBookWidgetInstanceIF, CBookEventListener {

	@Inject Upload() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CBookEventHandler handler = new CBookEventHandler(this);
	
	@Override
	public void addCBookEventListener(CBookEventListener listener, String command) {
		handler.addCBookEventListener(listener, command);
	}

	@Override
	public JComponent asComponent() {
		return this;
	}

	@Override
	public CBookEventListener asEventListener() {
		return this;
	}

	@Override
	public void destroy() {

	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public Hashtable<String, ?> getState() {
		Hashtable<String, ?> state = new Hashtable<>();
		return state;
	}

	@Override
	public SuccessStatus getSuccessStatus() {
		return SuccessStatus.UNKNOWN;
	}

	@Override
	public void init() {
	}

	@Override
	public void removeCBookEventListener(CBookEventListener listener, String command) {
		handler.removeCBookEventListener(listener, command);
	}

	@Override
	public void reset() {
	}

	@Override
	public void setAssessmentMode(AssessmentMode mode) {
	}

	@Override
	public void setLaunchData(Map<String, ?> launchData, Map<String, Number> randomVars) {
	}

	@Override
	public void setState(Map<String, ?> state) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void acceptCBookEvent(CBookEvent ev) {
	}

}
