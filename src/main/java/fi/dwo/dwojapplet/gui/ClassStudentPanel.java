/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

/**
 * The panel where a student manages his class list.
 *
 * @author M.J.B. Kupers
 *
 */
public class ClassStudentPanel extends JPanel implements CenterSubPanel, ActionListener {

    private CenterPanel center;
    private SchoolClassManagementStudentJPanel schoolClassMngmtPanel;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public ClassStudentPanel() {
        this.init();
    }

    public void init() {
//        user = GuiCreator.instance().getUser();
//        groupList = groups;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(200, 500);
//        this.setPreferredSize(getSize());

        /* Variables used to create items */
        FontMetrics fm;
        /* Add accountdata-panel */
        schoolClassMngmtPanel = new SchoolClassManagementStudentJPanel();
//        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        schoolClassMngmtPanel.setBackground(GuiConstants.SUB_BACKGROUND);
        //      p.setBounds(getSize().width / 2 - 155, 20, 310, 130);
        this.setLayout(null);
        schoolClassMngmtPanel.setLocation(10, 10);
        //this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(schoolClassMngmtPanel);
    }


    /**
     * Returns a Panel that can functionate as a header panel.
     *
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public Component getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT));
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }

    @Override
    public void end() {
    }

    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
