package fi.dwo.client.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.gui.ExportImportDialog;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class ShareCourseAction extends GuiAction {

	public ShareCourseAction(Component component) {
		super(TextMapper.getText(TextMapper.GUIC_COURSE_SHARE));
		setEnabled(instance().getUser() instanceof Teacher);
	}

	public void actionPerformed(ActionEvent ev) {
		Component component;
		Object o = ev.getSource();
		if(o instanceof Component)
			component = (Component)o;
		else
			component = DwoHelper.getApplet();
		ExportImportDialog dialog;
		try {
			dialog = new ExportImportDialog(DwoHelper.getFrameForComponent(component), instance().getUser(), instance().getDWO().getDwoProfile());
			dialog.setVisible(true);
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}		
		
	}

}
