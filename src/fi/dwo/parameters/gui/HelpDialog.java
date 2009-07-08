// Source file: C:\\fi\\dwo\\parameters\\gui\\HelpDialog.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.gui.DwoButton;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.parameters.system.TextMapper;

public class HelpDialog extends Dialog implements ActionListener, WindowListener {

    private JButton closeButton;
    /**
     * @roseuid 425E240D0242
     */
    public HelpDialog(Component owner, String title, boolean modal,
            String text) {
        super(DwoHelper.getFrameForComponent(owner), title, modal);
        this.setLayout(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        WrappingLabel wl = new WrappingLabel(text, WrappingLabel.LEFT_ALIGNMENT, WrappingLabel.TOP_ALIGNMENT);
        wl.setSize(400, 300);
        wl.setFont(ParameterConstants.LABEL_FONT);
        Insets insets = this.getInsets();
        wl.setLocation(insets.left + 10, insets.top + 30);
        wl.setVisible(false);
        
        this.add(wl);
        wl.setVisible(true);
        
        closeButton = new JButton(TextMapper.getText(TextMapper.BTN_CLOSE));

        FontMetrics fm = closeButton.getFontMetrics(closeButton.getFont());
        closeButton.setSize(fm.stringWidth(closeButton.getLabel()) + 40, fm.getHeight() + 10);
        closeButton.addActionListener(this);

        closeButton.setLocation(0, wl.getSize().height
                + wl.getLocation().y + 10);

        this.setSize(wl.getSize().width + insets.left + insets.right, closeButton.getSize().height
                + closeButton.getLocation().y + insets.bottom + 25);

        closeButton.setLocation((this.getSize().width / 2) - (closeButton.getSize().width / 2), wl.getSize().height
                + wl.getLocation().y + 10);
        closeButton.setVisible(false);
        this.add(closeButton);
        closeButton.setVisible(true);

        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        if(x < 10) {
            x = 10;
        }
        y = p.y + (parentSize.height - mySize.height) / 2;
        if( y < 10) {
            y = 10;
        }

        setLocation(x, y);
        this.addWindowListener(this);
    }

    /**
     * @param parameter
     * @roseuid 425D04D40290
     */
    public static void showHelpDialog(Component parent, String text) {
        HelpDialog hd = new HelpDialog(parent, TextMapper.getText(TextMapper.TITLE_HELP), true, text);
        hd.show();

    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == closeButton) {
            this.hide();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        dispose();
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
    }
}