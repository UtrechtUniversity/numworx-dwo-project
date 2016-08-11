// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\WelcomePanel.java
package fi.dwo.dwojapplet.gui;

import fi.beans.copyright.FIButton;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.MD5;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountManager;
import fi.dwo.rest.dom.entities.DomLoginContext;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * This class represents the panel that is been showed when you start the
 * application.
 *
 * @author M.J.B. Kupers
 *
 */
public class WelcomePanel extends ContentPanel implements ActionListener {

    private static final Logger LOG = Logger.getLogger(WelcomePanel.class.getName());

    private JTextField loginname;

    private JPasswordField password;

    private JButton loginButton;

    private JButton guestButton;

    private JButton registerNewUserButton;

    FIButton fiButton;
    JPanel dialog;

    private JCheckBox linkcheck;

    /**
     * Layout manager voor de fiButton. Hou de fiButton in de rechtsbovenhoek.
     *
     * @author wim
     *
     */
    private class FiButtonMover extends ComponentAdapter {

        /* (non-Javadoc)
         * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
         */
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            int width = getWidth();
            fiButton.setLocation(width - fiButton.getWidth(), fiButton.getY());
// extra: move dialogbox  horizontal
            dialog.setLocation(width / 2 - dialog.getWidth() / 2, dialog.getY());
        }

    }

    private class OpenUrlAction implements ActionListener {

        URI uri;

        public OpenUrlAction(URI anUri) {
            uri = anUri;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            open(uri);
        }

        void open(URI uri) {
            if (DwoHelper.isApplication() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "", e);
                }
            } else {
                try {
                    DwoHelper.getApplet().getAppletContext().showDocument(uri.toURL(), "_blank");
                    /* TODO: error handling */
                } catch (MalformedURLException ex) {
                    LOG.log(Level.SEVERE, "", ex);
                }
            }
        }
    }

    /**
     * Creates a new WelcomePanel with the posibilities to
     * configurePanelsForUser (as guest) or to register.
     *
     */
    public WelcomePanel() {
        this(false, null);
    }

    public WelcomePanel(boolean loginOnly) {
        this(loginOnly, null);
    }

    public WelcomePanel(boolean loginOnly, Map linkdata) {
        super(null, true);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setPreferredSize(getSize());
        this.setOpaque(true);

        dialog = new JPanel(null);
        dialog.setOpaque(false);
        dialog.setBounds(getWidth() / 2 - 350, 0, 700, getHeight());
        this.add(dialog);

        String version = "unknown";
        String revisie = "unknown";
        URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
        try {
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            // do stuff with it
            version = manifest.getMainAttributes().getValue("Implementation-Version");
            revisie = manifest.getMainAttributes().getValue("Implementation-Build");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't read Implementation-Version from manifest.mf.");
        }
        fiButton = new FIButton("DWO", new String[]{"versie-info: " + version,
        	"revisie: " + revisie, 
            "auteur: Peter Boon",
            "programmeur: M.J.B. Kupers,",
            "Gert van der Plas",
            "Wim van Velthoven",
            "Freudenthal Instituut",
            "www.fisme.science.uu.nl",
            ""
        });
        fiButton.setBounds(GuiConstants.DWO_WIDTH - 30, 0, 20, 30);
        add(fiButton);
        addComponentListener(new FiButtonMover()); // layout management voor FiButton
        /* Variables used to create items */
        JPanel p;
        JLabel l;
        FontMetrics fm;

        /* Add FI logo */
        Image fiLogo = null;
        fiLogo = DwoHelper.getResourceImage(GuiConstants.WISWEB_LOGO_LOCATION);

        ImagePanel ip = new ImagePanel(fiLogo);
        ip.setLocation(dialog.getWidth() / 2 - ip.getWidth() / 2, 440);
        dialog.add(ip);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(ip);
        }

        /* Warning Label */
 /*l = new Label("Helaas zijn er problemen met de DWO. Wordt aan gewerkt.");
         l.setFont(GuiConstants.RED_TEXT_ITALIC);
         l.setForeground(Color.red);
         fm = l.getFontMetrics(l.getFont());
         l.setBounds(ip.getLocation().x + ip.getSize().width + 10, 70, fm.stringWidth(l.getText()), fm.getHeight());
         l.setVisible(false);
         this.add(l);
         l.setVisible(true);*/
 /* Welcome Label */
        //l = new Label(TextMapper.getText(TextMapper.GUIW_WELCOME) + "!");
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_FI_NAME));
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        l.setForeground(new Color(3, 65, 123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText()) / 2, 520, fm.stringWidth(l.getText()) + 5, fm.getHeight());
        dialog.add(l);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(l);
        }

        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_SHORT));
        l.setFont(GuiConstants.HEADER_TEXT);
        l.setForeground(new Color(3, 65, 123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText()) / 2, 20, fm.stringWidth(l.getText()) + 5, fm.getHeight());
        dialog.add(l);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(l);
        }

        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_FULL));
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        fm = l.getFontMetrics(l.getFont());
        l.setForeground(new Color(3, 65, 123));
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText()) / 2, 65, fm.stringWidth(l.getText()) + 5, fm.getHeight());
        dialog.add(l);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(l);
        }

        /* Add Login-panel */
        p = new JPanel(null);
        //p.setFocusCycleRoot(true);
        p.setBorder(BorderFactory.createLineBorder(new Color(52, 90, 126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        //p.setBorderColor(new Color(52,90,126));
        p.setBounds(dialog.getWidth() / 2 - 175, 110, 340, 155);
        dialog.add(p);

        /* Inlogdata label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_LOGINDATA) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Username label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_USERNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Username field */
        loginname = new JTextField(DwoHelper.getDefaultUsername());
        loginname.setBounds(130, 28, 120, 20);
        loginname.addActionListener(this);
        p.add(loginname);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Password field */
        password = new JPasswordField(DwoHelper.getDefaultPassword());
        password.setBounds(130, 53, 120, 20);
        password.setEchoChar('*');
        password.addActionListener(this);
        p.add(password);

        /* linkdata */
        Object org = null;
        int h = 0;
        if (linkdata != null && null != (org = linkdata.get("dwoSAMLOrganization"))) {
            linkcheck = new JCheckBox("Inloggen via '" + org + '\'');
            linkcheck.setBackground(p.getBackground());
            linkcheck.setFont(GuiConstants.NORMAL_TEXT);
            p.add(linkcheck);
            h = linkcheck.getPreferredSize().height;
            p.setSize(p.getWidth(), h + p.getHeight());
            linkcheck.setBounds(7, 75, 250, 20);
        }

        /* Login button */
        loginButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_LOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = loginButton.getFontMetrics(loginButton.getFont());
        loginButton.setSize(loginButton.getPreferredSize());
        loginButton.setLocation((p.getSize().width / 2)
                - (loginButton.getSize().width / 2), p.getHeight() - 35);
        p.add(loginButton);

        loginButton.addActionListener(this);

        /* Register label */
        JButton button = new JButton();
        button.setText("<HTML><p color=\"red\"> <FONT color=\"#000099\"><U>" + Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(),
                Dwo2ExceptionCode.User_Q_ForgotPassword) + "</U></FONT></HTML>");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBackground(Color.WHITE);
        URI uri;
        try {
            try {
                //button.setToolTipText(uri.toString());
                uri = (new URL(DwoHelper.getServerUrlPath(), "rest/public/user/requestNewPassword" + "?language=" + TextMapper.getLanguage())).toURI();
                button.addActionListener(new OpenUrlAction(uri));
                button.setToolTipText(uri.toString());
            } catch (MalformedURLException ex) {
                Logger.getLogger(WelcomePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        button.setLocation(10, 5);
        button.setSize(button.getPreferredSize());
        button.setBounds(p.getSize().width / 2 - button.getWidth() / 2, p.getHeight() - 70, button.getWidth(), fm.getHeight());
        p.add(button);
//        l = new JLabel(TextMapper.getText("<html>The rain in <a href=\"action.spain\">Spain</a> falls mainly in the <a href=\"action.plain\">plain</a>."));
//        l.setForeground(GuiConstants.RED_COLOR);
//        l.setFont(GuiConstants.RED_TEXT);
//        fm = l.getFontMetrics(l.getFont());
//        l.setBounds(10
//                - (l.getSize().width / 2), p.getHeight() - 70, fm.stringWidth(l.getText()), fm.getHeight());
//        p.add(l);

        if (loginOnly) {
            return;
        }
        /* Add GuestLogin-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52, 90, 126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(dialog.getWidth() / 2 - 175, 275 + h, 340, 85);
        //p.setBorderColor(new Color(52,90,126));
        dialog.add(p);

        /* Guestlogin label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_GUESTLOGIN) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* GuestLogin button */
        guestButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_GUESTLOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = guestButton.getFontMetrics(guestButton.getFont());
        guestButton.setSize(guestButton.getPreferredSize());
        guestButton.setLocation((p.getSize().width / 2)
                - (guestButton.getSize().width / 2), 27);
        p.add(guestButton);

        /* GuestLogin message */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_MSG_WORK_NOT_SAVE));
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
        p.add(l);


        /* Add Register-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52, 90, 126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(dialog.getWidth() / 2 - 175, 370 + h, 340, 65);
        //p.setBorderColor(new Color(52,90,126));
        dialog.add(p);

        /* Register label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_REGISTER) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Register button */
        registerNewUserButton = new JButton(TextMapper.getText(TextMapper.GUIW_MSG_REGISTER_NEW));// GuiConstants.SUB_BACKGROUND);
        fm = registerNewUserButton.getFontMetrics(registerNewUserButton.getFont());
        registerNewUserButton.setSize(registerNewUserButton.getPreferredSize());
        registerNewUserButton.setLocation((p.getSize().width / 2)
                - (registerNewUserButton.getPreferredSize().width / 2), 27);
//        p.setBounds(dialog.getWidth() / 2 - 175, 350 , 340, 35);
        p.add(registerNewUserButton);

//        /* Register button */
//        registerExistingUserButton = new JButton(TextMapper.getText(TextMapper.GUIW_MSG_REGISTER_EXISTING));//, GuiConstants.SUB_BACKGROUND);
//        fm = registerNewUserButton.getFontMetrics(registerExistingUserButton.getFont());
//        registerExistingUserButton.setSize(registerExistingUserButton.getPreferredSize());
//        registerExistingUserButton.setLocation((p.getSize().width / 2)
//                - 2*(registerExistingUserButton.getSize().width / 2), 27);
//        p.add(registerExistingUserButton);
//        /* Register message */
//        l = new JLabel(TextMapper.getText(TextMapper.GUIW_REGISTER));
//        l.setFont(GuiConstants.NORMAL_TEXT);
//        fm = l.getFontMetrics(l.getFont());
//        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
//        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
//        p.add(l);
        guestButton.addActionListener(this);
        registerNewUserButton.addActionListener(this);
        loginButton.requestFocus();

    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ((src == loginButton) || (src == loginname) || (src == password)) {
            try {
                //Fetch LoginContext to see if there is already a session.
                DomLoginContext loginContext = SecureUserAccountManager.getLoginContext(loginname.getText(), MD5.getHashString(String.valueOf(password.getPassword())));
// XXX uitgezet, te verwarrend
                if (loginContext != null && loginContext.getLastLoginTimeStamp() != null && false) {
                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(),
                            Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmNewLoginSession)
                    ) != JOptionPane.OK_OPTION) {
                        return;
                    };
                }
                GuiCreator.instance().login(loginname.getText(), String.valueOf(password.getPassword()));
                if (linkcheck != null && linkcheck.isSelected()) {
                    GuiCreator.instance().linkViaSAML();
                }
            } catch (LoginException exc) {
                if (LOG.getLevel() == Level.INFO) {
                    LOG.log(Level.INFO, "Login failed.");
                } else {
                    LOG.log(Level.FINE, "Login exception.", exc);
                }
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            } catch (Dwo2Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), null, JOptionPane.ERROR_MESSAGE);
                LOG.log(Level.SEVERE, "", ex);
            }
        } else if (src == guestButton) {
            try {
                GuiCreator.instance().login();
            } catch (LoginException exc) {
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            }
        } else if (src == registerNewUserButton) {
            GuiCreator.instance().toRegisterNewUser();
        }
    }

    public void setUsername(String username) {
        this.loginname.setText(username);
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }
}
