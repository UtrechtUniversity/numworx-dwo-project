package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Group;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private RegisterMoreSchoolLoginsPanel rmsPanel = new RegisterMoreSchoolLoginsPanel();

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
     */
    public AccountPanel() {
        this.init();
    }

    public void init() {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(200, 500);

        /* Variables used to create items */
        FontMetrics fm;
        /* Add accountdata-panel */
        accountDataPanel = new AccountDataFullUserJPanel();
        accountDataPanel.setUser(DwoHelper.getCurrentUser());
        accountDataPanel.setBackground(GuiConstants.SUB_BACKGROUND);
        //      p.setBounds(getSize().width / 2 - 155, 20, 310, 130);

        /* Add schoolrole-panel */
        sarPanel = new AccountSchoolsRolesJPanel();
        sarPanel.setVisible(!DwoHelper.isSingleSchoolStudent());

        sarPanel.setBackground(GuiConstants.SUB_BACKGROUND);
        this.setLayout(null);
        accountDataPanel.setLocation(10, 10);
        //this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(accountDataPanel);
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
