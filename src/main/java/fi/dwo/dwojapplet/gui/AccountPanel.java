package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Group;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

/**
 * This class is an Account panel. The panel has two sub panels that one for
 * managing account data and the other for managing school roles combinations.
 *
 * @author G.A.J. van der Plas
 *
 */
public class AccountPanel extends JPanel implements CenterSubPanel,
        ActionListener {

    private CenterPanel center;
    private AccountDataFullUserJPanel accountDataPanel;
    private AccountSchoolsRolesJPanel sarPanel;
    private RegisterMoreSchoolsPanel rmsPanel = new RegisterMoreSchoolsPanel();
    private JButton okButton, cancelButton;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public AccountPanel(Group[] groups) {
        this.init();
    }

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public AccountPanel() {
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
        accountDataPanel = new AccountDataFullUserJPanel();
        accountDataPanel.setUser(DwoHelper.getCurrentUser());
//        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        accountDataPanel.setBackground(GuiConstants.SUB_BACKGROUND);
        //      p.setBounds(getSize().width / 2 - 155, 20, 310, 130);

        /* Add schoolrole-panel */
//        l = new RegisterMoreSchoolsPanel(groups);
        sarPanel = new AccountSchoolsRolesJPanel();
        sarPanel.setVisible(!DwoHelper.isSingleSchoolStudent());

//        l.setBorder(BorderFactory.createLineBorder(getForeground()));
        sarPanel.setBackground(GuiConstants.SUB_BACKGROUND);
        //      p.setBounds(getSize().width / 2 - 155, 20, 310, 130);
        this.setLayout(null);
        accountDataPanel.setLocation(10, 10);
        //this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        Box panel = Box.createHorizontalBox();
        panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.add(accountDataPanel);
        panel.add(okButton);
        panel.add(Box.createRigidArea(new Dimension(30, 0)));
        panel.add(cancelButton);        
        sarPanel.setLocation(330, 10);
        this.add(sarPanel);
    }

    /**
     * Returns a Panel that can functionate as a header panel.
     *
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public Component getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIP_MY_PROFILE));
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
