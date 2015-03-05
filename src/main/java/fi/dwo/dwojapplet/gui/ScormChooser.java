/**
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

/**
 * @author Wim
 *
 */
public class ScormChooser extends JFileChooser {

    public JRadioButton scorm12, scorm2004, html5;

    /**
     *
     */
    public ScormChooser() {
        super();
        setDialogType(SAVE_DIALOG);
        scorm12 = new JRadioButton("Scorm 1.2", true);
        scorm2004 = new JRadioButton("Scorm 2004");
        html5 = new JRadioButton("HTML5");
        ButtonGroup group = new ButtonGroup();
        group.add(scorm12);
        group.add(scorm2004);
        group.add(html5);
        addChoosableFileFilter(ScormFilter.FILTER);
        //setAcceptAllFileFilterUsed(false);

    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        JComponent north = createTop();
        dialog.getContentPane().add(north, BorderLayout.NORTH);
        dialog.setTitle("Bewaar Scorm export");
        return dialog;
    }

    private JComponent createTop() {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(15));
        JLabel keuze = new JLabel("Kies SCORM type:");
        box.add(keuze);
        box.add(scorm12);
        box.add(scorm2004);
        box.add(html5);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Frame dummy = new Frame("dummy");
        dummy.show();
        ScormChooser chooser = new ScormChooser();
        chooser.showSaveDialog(dummy);

        System.out.println(chooser.getSelectedFile());
        System.out.println(chooser.isScorm2004());
        System.out.println(chooser.isHTML5());
        System.exit(0);
    }

    public boolean isScorm2004() {
        return scorm2004.isSelected();
    }

    public boolean isHTML5() {
        return html5.isSelected();
    }

}

class ScormFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".zip");
    }

    @Override
    public String getDescription() {
        return "SCORM bestanden (*.zip)";
    }

    private ScormFilter() {
    }

    static final ScormFilter FILTER = new ScormFilter();

}
