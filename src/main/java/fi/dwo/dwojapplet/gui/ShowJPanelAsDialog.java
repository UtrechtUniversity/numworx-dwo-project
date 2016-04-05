/**
 * Copyrighted Jul 24, 2015
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 * Shows a JPanel in a JDialog. For component reuse.
 * 
 * @author Gert van der Plas
 */
public class ShowJPanelAsDialog extends JDialog {

    
    /**
     * 
     * @param contentPane the JPanel to be shown.
     */
    public ShowJPanelAsDialog(Container contentPane){
        super();
        init(contentPane);
    }
            
    private void init(Container contentPane) {
        this.setResizable(true);
//        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
//        panel.add(contentPane);
//        panel.add(new JButton("Close window"));
        this.getContentPane().add(contentPane);
        
        this.pack();
        this.setSize(contentPane.getSize());
        this.setMinimumSize(contentPane.getMinimumSize());
        this.setMaximumSize(contentPane.getMaximumSize());
        Dimension Size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(new Double((Size.getWidth() / 2) - (this.getWidth() / 2)).intValue(), new Double((Size.getHeight() / 2) - (this.getHeight() / 2)).intValue());
    }


}
