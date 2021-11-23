package nl.numworx.uploadwidget;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Provider;

import org.cbook.cbookif.CBookEvent;
import org.cbook.cbookif.CBookEventListener;

import dagger.Lazy;
import fi.beans.wiskopdrbeans.CBookAware;
import fi.beans.wiskopdrbeans.InteractieEditPanel;
import fi.beans.wiskopdrbeans.InteractiePanel;

public class UploadInteractiePanel implements InteractiePanel, CBookAware {

	
	@Inject UploadInteractiePanel() {}
	@Inject Provider<InteractieEditPanel> editfactory;
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
		return editfactory.get();
	}

	@Override
	public Hashtable getEditState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIpId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScore() {
		return instance.get().getScore();
	}

	@Override
	public int getScoreMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable getState() {
		return instance.get().getState();
	}

	@Override
	public boolean isCorrect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFout() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEditState(Hashtable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(Hashtable arg0) {
		instance.get().setState(arg0);

	}

	@Override
	public void start() {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void zetMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void zetNagekeken(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void zetOpdracht(Hashtable arg0, String[] arg1, Hashtable arg2) {
		// TODO Auto-generated method stub

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

}
