package fi.dwo.client.gui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import fi.dwo.client.domain.School;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class SchoolConfigPanel extends JPanel implements CenterSubPanel {

	private CenterPanel center;
	private School school;
	JCheckBox changeClassStudent, 
			  changeClassTeacher,
			  modifyModules;
			  
	
	public SchoolConfigPanel(School school) {
		this.school = school;
		setOpaque(false);
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		JLabel title = new JLabel("Instellingen " + school.toString());
		add(title);
		
// insert checkboxes.		
		changeClassStudent = new JCheckBox();add(changeClassStudent);
		changeClassTeacher = new JCheckBox();add(changeClassTeacher);
		modifyModules = new JCheckBox(); add(modifyModules);
		
// opschriften		
		changeClassStudent.setText("Leerlingen kunnen zelf hun klas kiezen");
		changeClassTeacher.setText("Docenten kiezen de leerlingen van hun klas");
		modifyModules.setText("Docenten kunnen modules aanpassen");
		changeClassStudent.setBackground(GuiConstants.CELL_BACKGROUND);
		changeClassTeacher.setBackground(GuiConstants.CELL_BACKGROUND);
		modifyModules.setBackground(GuiConstants.CELL_BACKGROUND);
// initiele waarden
		boolean b;
		b = school.hasRight(User.CHANGE_CLASS_RIGHT);
		changeClassStudent.setSelected(b);
		b = school.hasRight(User.CHANGE_CLASS_RIGHT_TEACHER);
		changeClassTeacher.setSelected(b);
		b = school.hasRight(User.MODIFY_MODULES_RIGHT);
		modifyModules.setSelected(b);
	}

	public void end() {
		StringBuffer sb = new StringBuffer();
		sb.append(school.getRights());
		int i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT));
		if(i >= 0) sb.replace(i, i+1, "");
		i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT_TEACHER));
		if(i >= 0) sb.replace(i, i+1, "");
		i = sb.indexOf(String.valueOf(User.MODIFY_MODULES_RIGHT));
		if(i >= 0) sb.replace(i, i+1, "");

		boolean b; 
		b = changeClassStudent.isSelected();
		if(b) sb.append(User.CHANGE_CLASS_RIGHT);
		b = changeClassTeacher.isSelected();
		if(b) sb.append(User.CHANGE_CLASS_RIGHT_TEACHER);
		b = modifyModules.isSelected();
		if(b) sb.append(User.MODIFY_MODULES_RIGHT);

		// school.setRights(sb.toString()); // testing
		try {
			PersistenceFacade.instance().editSchool(school, sb.toString());
		} catch (PersistenceException e) {
			// TODO jammer dan....
			e.printStackTrace();
		}
	}

	public JComponent getComponent() {
		return this;
	}

	public Component getHeaderPanel() {
		String opschrift = TextMapper.getText("Instellingen school");
		return new HeaderPanel(opschrift);
	}

	public Object getUserObject() {
		return this;
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		center = centerPanel;
	}

	public void stateChanged(ChangeEvent e) {
	}

}
