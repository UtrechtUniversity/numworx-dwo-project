// Source file: C:\\fi\\dwo\\parameters\\gui\\HelpDialog.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.parameters.system.TextMapper;

public class HelpDialog extends JDialog implements ActionListener {

    private JButton closeButton;
    /**
     * @roseuid 425E240D0242
     */
    public HelpDialog(Component owner, String title, boolean modal,
            String text) {
        super(DwoHelper.getFrameForComponent(owner), title, modal);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        JTextArea wl = new JTextArea(text);
        wl.setOpaque(false);
        wl.setEditable(false);
        wl.setWrapStyleWord(true);
        wl.setLineWrap(true);
        wl.setSize(400, 300);
        wl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        wl.setFont(ParameterConstants.LABEL_FONT);
        Insets insets = this.getInsets();
        wl.setLocation(insets.left + 10, insets.top + 30);
        wl.setVisible(false);
        
        contentPane.add(wl, BorderLayout.CENTER);
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
        Box hbox = Box.createHorizontalBox();
        hbox.add(Box.createHorizontalGlue());
        hbox.add(closeButton);
        hbox.add(Box.createHorizontalGlue());
        contentPane.add(hbox, BorderLayout.SOUTH);
        closeButton.setVisible(true);

        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocationOnScreen() : new Point(0, 0);
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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == closeButton) {
            this.hide();
        }
    }

}