package nl.numworx.notebook;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;
import org.cbook.cbookif.Constants;
import org.cbook.cbookif.LessonMode;
import org.cbook.cbookif.SuccessStatus;

import fi.beans.wiskopdrbeans.CBookAware;
import fi.beans.wiskopdrbeans.InteractieEditPanel;
import fi.beans.wiskopdrbeans.InteractiePanel;
import fi.wiskopdr.opdrnav.OpdrNavStruct;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NotebookInteractiePanel extends JPanel implements
		InteractiePanel, CBookContext, CBookAware {

	private static final long serialVersionUID = -4868744357817393056L;

	private  final CBookEvent CHECK = new CBookEvent(this, Constants.CHECK);

	private Instance instance;
	private boolean started;

	private class CBookActionListener implements CBookEventListener {

		private ActionListener al;
		private ActionEvent event = new ActionEvent(NotebookInteractiePanel.this, ActionEvent.ACTION_PERFORMED, "changed");
		@Override
		public void acceptCBookEvent(CBookEvent arg0) {
			if(al != null) {
				al.actionPerformed(event);
			}
		}
	}
	
	private Hashtable launchData;

	private boolean[][] logObjectives;

	private final Notebook widget;
	
	NotebookInteractiePanel(Notebook widget) {
		super(new BorderLayout());
		this.widget = widget;
		setOpaque(false);
		initInstance(this);
	}

	private void initInstance(CBookContext context) {
		instance = new Instance(widget.getLocale(), widget.getHubBase(), context);
		add(instance.asComponent(), BorderLayout.CENTER);
		all = new CBookActionListener();
		instance.addCBookEventListener(all, Constants.CHECKED); // ons kent ons
	}
	
	static String LOG_OBJECTIVES = "logObjectives";
	public void zetOpdracht(Hashtable b, String[] randomVars,
			Hashtable randomValues) {
		launchData = b;
		this.logObjectives = OpdrNavStruct.toBooleanArrayArray(b.get(LOG_OBJECTIVES));
		doLayout(); // Assume size is valid.
		instance.init();
		Map randomvars = launchRandomVars();
		randomvars.putAll(randomValues);
		instance.setLaunchData(b, randomvars);
	}

	public void setState(Hashtable b) {
		instance.setState(b);
	}

	public void setEditState(Hashtable b) {
		doLayout(); // Assume size is valid.
		instance.init();
		this.launchData = b;
		Map randomvars = launchRandomVars();
		instance.setLaunchData(b, randomvars);
	}

	private Map launchRandomVars() {
		Map randomvars = Collections.emptyMap();
		return randomvars;
	}

	public Hashtable getState() {
		return instance.getState();
	}

	public Hashtable getEditState() {		
		return launchData;
	}

	public InteractieEditPanel getEditPanel() {
		return new NotebookInteractieEditPanel(widget);
	}

	public void wis() {
		instance.reset();
	}

	public void zetMaat() {
		instance.asComponent().doLayout();
	}

	public int getIpId() {
		return 0;
	}

	public int getScore() {
		return instance.getScore();
	}

	public int[][] getScoreObjectives() {
		if (logObjectives == null)
			return null;
		int score = getScore();
		int[][] scoreObjectives = new int[logObjectives.length][];
		for (int i = 0; i < logObjectives.length; i++) {
			scoreObjectives[i] = new int[logObjectives[i].length];
			for (int j = 0; j < logObjectives[i].length; j++) {
				if (logObjectives[i][j])
					scoreObjectives[i][j] = score;
			}
		}
		return scoreObjectives;
	}

	public int getScoreMax() {
		return instance.getMaxScore();
	}

	public boolean isCorrect() {
		return instance.getSuccessStatus() == SuccessStatus.PASSED;
	}

	public boolean isFout() {
		return instance.getSuccessStatus() == SuccessStatus.FAILED;
	}

	public void zetMode(int mode) {
		instance.setAssessmentMode(AssessmentMode.values()[mode]);
	}

	public void zetNagekeken(boolean b) {
        CBookEvent ev = new CBookEvent(this, Constants.CHECK, Collections.singletonMap(Constants.CHECKED, Boolean.valueOf(b)));
        instance.acceptCBookEvent(ev);
	}

	public void stop() {
		if (started) {
			instance.stop();
			started = false;
		}
	}

	public void start() {
		if (!started) {
			started = true;
			instance.start();
		}
	}

	public void destroy() {
		instance.destroy();
	}

	public void opnieuw() {
		instance.reset();
	}

	public void kijkNa() {
		instance.acceptCBookEvent(CHECK);
	}

	public void kijkNa(int stapNr) {
		kijkNa();
	}

	private CBookActionListener all;

	public synchronized void addActionListener(ActionListener al) {
		all.al = AWTEventMulticaster.add(al, all.al);
	}

	public Object getProperty(String key) {
	    if (context != null) {
	      return context.getProperty(key);
	    }
		return null;
	}

	public void acceptCBookEvent(CBookEvent ev) {
		instance.acceptCBookEvent(ev);
	}

	public void addCBookEventListener(CBookEventListener listener, String command) {
		instance.addCBookEventListener(listener, command);
	}

	public String[] getAcceptedCmds() {
		boolean isConst = true;
		List<String> cmds = getCmds(isConst);
		cmds.add(Constants.CHECK);
		return cmds.toArray(new String[cmds.size()]);
	}

	public List<String> getCmds(boolean isConst) {
		List<String> cmds;
		cmds = new ArrayList<String>();
		return cmds;
	}

	public String getLocalizedCmd(String cmd) {
		return cmd;
	}

	public String[] getSendCmds() {
		List<String> cmds = getCmds(false);
		return cmds.toArray(new String[cmds.size()]);
	}

	public void removeCBookEventListener(CBookEventListener arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

  private CBookContext context = null;

  @Override
  public void setCBookContext(CBookContext context) {
    this.context = context;
    Object o = context.getProperty("lessonMode");
    if (o instanceof LessonMode) {
      instance.lessonMode = (LessonMode) o;
    }
  }

	
	
}
