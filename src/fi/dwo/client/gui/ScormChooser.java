/**
 * 
 */
package fi.dwo.client.gui;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileFilter;

/**
 * @author Wim
 *
 */
public class ScormChooser extends JFileChooser  {

	JRadioButton scorm12, scorm2004;
	
	
	
	
	/**
	 * 
	 */
	public ScormChooser() {
		super();
		setDialogType(SAVE_DIALOG);
		scorm12 = new JRadioButton("Scorm 1.2", true);
		scorm2004  = new JRadioButton("Scorm 2004");
		ButtonGroup group = new ButtonGroup();
		group.add(scorm12);
		group.add(scorm2004);
		addChoosableFileFilter(ScormFilter.FILTER);
		//setAcceptAllFileFilterUsed(false);
		
		
	}


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
		box.add(Box.createHorizontalGlue());
		return box;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Frame dummy = new Frame("dummy");
		dummy.show();
		ScormChooser chooser = new ScormChooser();
		JDialog dialog = chooser.createDialog(dummy);
		dialog.show();
		System.out.println(chooser.getSelectedFile());
		System.out.println(chooser.isScorm2004());
		System.exit(0);
	}


	boolean isScorm2004() {
		return scorm2004.isSelected();
	}

}


class ScormFilter extends FileFilter 
{

	public boolean accept(File f) {
		return f.getName().toLowerCase().endsWith(".zip");
	}

	public String getDescription() {
		return "SCORM bestanden (*.zip)";
	}
	
	private ScormFilter() {}
	
	static final ScormFilter FILTER = new ScormFilter();
	
}