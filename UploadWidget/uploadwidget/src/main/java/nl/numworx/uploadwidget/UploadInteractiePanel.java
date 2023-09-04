package nl.numworx.uploadwidget;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cbook.cbookif.AssessmentMode;
import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;

import dagger.Lazy;
import fi.beans.wiskopdrbeans.CBookAware;
import fi.beans.wiskopdrbeans.InteractieEditPanel;
import fi.beans.wiskopdrbeans.InteractiePanel;
import fi.beans.wiskopdrbeans.ResourceManagerClient;

public class UploadInteractiePanel extends JPanel implements InteractiePanel, CBookAware, ResourceManagerClient {

	
	@Inject UploadInteractiePanel() {super(null);}
	@Inject Provider<UploadInteractieEditPanel> editfactory;
	@Inject Lazy<Upload> instance;
	
	private ActionListener al;
	
	
	@Override
	public void addActionListener(ActionListener arg0) {
		al = AWTEventMulticaster.add(al, arg0);
	}

	@Override
	public void destroy() {
		instance.get().destroy();
	}

	@Override
	public InteractieEditPanel getEditPanel() {
		return editfactory.get().setInstance(this);
	}

	@Override
	public Hashtable getEditState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIpId() {
		return 0;
	}

	@Override
	public int getScore() {
		return instance.get().getScore();
	}

	private int scoreMax;
	private String id;
	ResourceManagerFactory rmf;
	
	@Override
	public int getScoreMax() {
		return scoreMax;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Hashtable getState() {
		return instance.get().getState();
	}

	@Override
	public boolean isCorrect() {
		return scoreMax == 0;
	}

	@Override
	public boolean isFout() {
		return false;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub

	}

	@Override
	public void kijkNa(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void opnieuw() {
		instance.get().reset();
	}

	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {
		super.setBounds(arg0, arg1, arg2, arg3);
		instance.get().setSize(arg2, arg3);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setEditState(Hashtable arg0) {
		zetMaat();
		instance.get().setLaunchData(arg0, Collections.emptyMap());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setState(Hashtable arg0) {
		instance.get().setState(arg0);

	}

	@Override
	public void start() {
		zetMaat();
		instance.get().start();

	}

	@Override
	public void stop() {
		instance.get().stop();

	}

	@Override
	public void wis() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zetMaat() {
		removeAll();
		JComponent c = instance.get().asComponent();
		c.setSize(getSize());
		add(c);
	}

	@Override
	public void zetMode(int arg0) {
		instance.get().setAssessmentMode(AssessmentMode.values()[arg0]);
	}

	@Override
	public void zetNagekeken(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void zetOpdracht(Hashtable arg0, String[] arg1, Hashtable arg2) {
		instance.get().setLaunchData(arg0, arg2);
	}

	@Override
	public void acceptCBookEvent(CBookEvent arg0) {
		instance.get().acceptCBookEvent(arg0);		
	}

	@Override
	public void addCBookEventListener(CBookEventListener arg0, String arg1) {
		instance.get().addCBookEventListener(arg0, arg1);
		
	}

	@Override
	public String[] getAcceptedCmds() {
		return Editor.accepted;
	}

	@Override
	public String getLocalizedCmd(String arg0) {
		return arg0;
	}

	@Override
	public String[] getSendCmds() {
		return Editor.sent;
	}

	@Override
	public void removeCBookEventListener(CBookEventListener arg0, String arg1) {
		instance.get().removeCBookEventListener(arg0, arg1);
	}

	@Override
	public String getClassName() {		
		return UploadWidget.class.getName();
	}

	@Override
	public String getInstanceId() {
		return id;
	}

	@Override
	public void setFactory(ResourceManagerFactory rmf) {
		this.rmf = rmf;
		
	}

	@Override
	public void setInstanceId(String id) {
		this.id = id;
	}

}
