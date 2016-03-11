package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

public class SchoolConfigPanel extends JPanel implements CenterSubPanel {

    private static final Logger LOG = Logger.getLogger(SchoolConfigPanel.class.getName());

    private CenterPanel center;
    private School school;
    JCheckBox modifyModules;
//    JCheckBox changeClassStudent;
//    JCheckBox changeClassTeacher;

    public SchoolConfigPanel(School school) {
        this.school = school;
        setOpaque(false);
        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);
        JLabel title = new JLabel(TextMapper.format(TextMapper.GUIC_SETTINGS, new Object[]{school.toString()}));
        add(title);

// insert checkboxes.		
// Checkboxes superfluous in v1.4.1        
//        changeClassStudent = new JCheckBox();
//        add(changeClassStudent);
//        changeClassTeacher = new JCheckBox();
//        add(changeClassTeacher);
        modifyModules = new JCheckBox();
        add(modifyModules);

// opschriften		
//        changeClassStudent.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_STUDENT));
//        changeClassTeacher.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_TEACHER));
        modifyModules.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_MODULE));
//        changeClassStudent.setBackground(GuiConstants.CELL_BACKGROUND);
//        changeClassTeacher.setBackground(GuiConstants.CELL_BACKGROUND);
        modifyModules.setBackground(GuiConstants.CELL_BACKGROUND);
// initiele waarden
        boolean b;
//        b = school.hasRight(User.CHANGE_CLASS_RIGHT);
//        changeClassStudent.setSelected(b);
//        b = school.hasRight(User.CHANGE_CLASS_RIGHT_TEACHER);
//        changeClassTeacher.setSelected(b);
        b = school.hasRight(User.MODIFY_MODULES_RIGHT);
        modifyModules.setSelected(b);
    }

    @Override
    public void end() {
        StringBuffer sb = new StringBuffer();
        sb.append(school.getRights());
//        int i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT));
//        if (i >= 0) {
//            sb.replace(i, i + 1, "");
//        }
//        i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT_TEACHER));
//        if (i >= 0) {
//            sb.replace(i, i + 1, "");
//        }
        int i = sb.indexOf(String.valueOf(User.MODIFY_MODULES_RIGHT));
        if (i >= 0) {
            sb.replace(i, i + 1, "");
        }

        boolean b;
//        b = changeClassStudent.isSelected();
//        if (b) {
//            sb.append(User.CHANGE_CLASS_RIGHT);
//        }
//        b = changeClassTeacher.isSelected();
//        if (b) {
//            sb.append(User.CHANGE_CLASS_RIGHT_TEACHER);
//        }
        b = modifyModules.isSelected();
        if (b) {
            sb.append(User.MODIFY_MODULES_RIGHT);
        }

        // school.setRights(sb.toString()); // testing
        try {
            PersistenceFacade.instance().editSchool(school, sb.toString());
        }
        catch (PersistenceException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Component getHeaderPanel() {
        String opschrift = TextMapper.getText(TextMapper.GUIH_SETTINGS);
        return new HeaderPanel(opschrift);
    }

    @Override
    public Object getUserObject() {
        return this;
    }

    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }

}
