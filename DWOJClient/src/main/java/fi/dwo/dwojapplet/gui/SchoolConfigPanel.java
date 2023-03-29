package fi.dwo.dwojapplet.gui;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JRadioButton;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.User;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

public class SchoolConfigPanel extends JPanel implements CenterSubPanel {

    private static final Logger LOG = Logger.getLogger(SchoolConfigPanel.class.getName());

    private CenterPanel center;
    private School school;
    JRadioButton modifyModules, readonlyModules;
    JCheckBox changeClassStudent;
    JRadioButton accessTeacher;
    ButtonGroup rights;
//    JCheckBox changeClassTeacher;

    public SchoolConfigPanel(School school) {
        this.school = school;
        setBackground(getSubHeaderColor());
        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setBorder(BorderFactory.createEmptyBorder(2, 20, 0, 0));
        setLayout(layout);
        JLabel title = new JLabel(TextMapper.format(TextMapper.GUIC_SETTINGS, new Object[]{school.toString()}));
        title.setForeground(GuiConstants.HEADER_COLOR);
        title.setFont(GuiConstants.SUB_HEADER_TEXT);
        add(title);
        add(Box.createVerticalStrut(10));

// insert checkboxes.		
// Checkboxes superfluous in v1.4.1        
        changeClassStudent = new JCheckBox();
        add(changeClassStudent);
//        changeClassTeacher = new JCheckBox();
//        add(changeClassTeacher);
        rights = new ButtonGroup();
        add(Box.createVerticalStrut(20));
        modifyModules = new JRadioButton();
        add(modifyModules);
        readonlyModules = new JRadioButton();
        add(readonlyModules);
        accessTeacher = new JRadioButton();
        if( (DwoHelper.isTest()||DwoHelper.isSamlLogin()) && DwoHelper.isPremium())
          add(accessTeacher);
        rights.add(modifyModules);
        rights.add(readonlyModules);
        rights.add(accessTeacher);
        
// opschriften		
        changeClassStudent.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_STUDENT));
//        changeClassTeacher.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_TEACHER));
        modifyModules.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_MODULE));
        readonlyModules.setText(TextMapper.getText(TextMapper.GUIC_SETTINGS_READONLY));
        accessTeacher.setText(TextMapper.dwo2Message().NUM_SEC_ORGANISATION_ACCESSTEACHER());
//        changeClassTeacher.setBackground(GuiConstants.CELL_BACKGROUND);

// initiele waarden
        boolean b;
        b = school.hasRight(User.CHANGE_CLASS_RIGHT);
        changeClassStudent.setSelected(b);
//        b = school.hasRight(User.CHANGE_CLASS_RIGHT_TEACHER);
//        changeClassTeacher.setSelected(b);
        b = school.hasRight(User.MODIFY_MODULES_RIGHT);
        modifyModules.setSelected(b);
        readonlyModules.setSelected(!b);
        b = school.hasRight(User.ACCESS_RIGHT);
        accessTeacher.setSelected(b);
    }

    @Override
    public void end() {
        StringBuffer sb = new StringBuffer();
        sb.append(school.getRights());
        int i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT));
        if (i >= 0) {
            sb.replace(i, i + 1, "");
        }
//        i = sb.indexOf(String.valueOf(User.CHANGE_CLASS_RIGHT_TEACHER));
//        if (i >= 0) {
//            sb.replace(i, i + 1, "");
//        }
         i = sb.indexOf(String.valueOf(User.MODIFY_MODULES_RIGHT));
        if (i >= 0) {
            sb.replace(i, i + 1, "");
        }
        i = sb.indexOf(String.valueOf(User.ACCESS_RIGHT));
        if (i>=0) 
          sb.deleteCharAt(i);
        
        
        boolean b;
        b = changeClassStudent.isSelected();
        if (b) {
            sb.append(User.CHANGE_CLASS_RIGHT);
        }
//        b = changeClassTeacher.isSelected();
//        if (b) {
//            sb.append(User.CHANGE_CLASS_RIGHT_TEACHER);
//        }
        b = modifyModules.isSelected();
        if (b) {
            sb.append(User.MODIFY_MODULES_RIGHT);
        }
        b = accessTeacher.isSelected();
        if (b) {
          sb.append(User.ACCESS_RIGHT);
        }
        // school.setRights(sb.toString()); // testing
        try {
            DomSchoolFull dom = new DomSchoolFull();
            dom.setSchoolRights(sb.toString());
            dom.setId(PersistentSchool.buildPersistenceId((long) school.getSchoolID()));
            GuiCreator.instance().getSchoolManager().updateSchool(dom);
            school.setRights(sb.toString());
// Update user with access right
            User u = DwoHelper.getCurrentFacadeUser();
            if (school.hasRight(User.ACCESS_RIGHT)) u.addRight(User.ACCESS_RIGHT);
            else {
              String ur = u.getRights();
              ur = ur.replace(Character.toString(User.ACCESS_RIGHT), "");
              u.setRights(ur);
            }
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Color getSubHeaderColor() {
      return Constants.COLOR20;
    }

    @Override
    public JComponent getHeaderPanel() {
        String opschrift = TextMapper.getText(TextMapper.GUIH_SETTINGS);
        HeaderPanel header = new HeaderPanel(opschrift);
        header.setBackground(getSubHeaderColor());
        return header;
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
