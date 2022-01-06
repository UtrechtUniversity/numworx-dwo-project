/**
 * 
 */
package fi.beans.numworxlf;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FileChooserUI;




/**
 * @author wim
 *
 */
@SuppressWarnings("serial")
public class JFileChooser extends javax.swing.JFileChooser {

    private static final File USER_HOME = FileSystemView.getFileSystemView().getHomeDirectory();
	private static File currentDirectory = USER_HOME;
    public static File getGlobalDirectory() {
      return currentDirectory;
    }

    public static void setGlobalDirectory(File dir) {
      if (dir == null) return;
      if (!dir.isDirectory()) dir = dir.getParentFile();
      currentDirectory = dir;
    }
  /**
   * 
   */
  public JFileChooser() {
	  super();
  }

  /**
   * @param currentDirectoryPath
   */
  public JFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  /**
   * @param currentDirectory
   */
  public JFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * @param fsv
   */
  public JFileChooser(FileSystemView fsv) {
    super(fsv);
  }

  /**
   * @param currentDirectory
   * @param fsv
   */
  public JFileChooser(File currentDirectory, FileSystemView fsv) {
    super(currentDirectory, fsv);
   }

  /**
   * @param currentDirectoryPath
   * @param fsv
   */
  public JFileChooser(String currentDirectoryPath, FileSystemView fsv) {
    super(currentDirectoryPath, fsv);
  }

	@Override
	public void setCurrentDirectory(File dir) {	
		super.setCurrentDirectory(dir);
		if (dir == null) dir = USER_HOME; // komt voor
		setGlobalDirectory(dir);
	}
	
	
	@Override
	public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
		super.setCurrentDirectory(getGlobalDirectory());
		int result = super.showDialog(parent, approveButtonText);
		if (result == APPROVE_OPTION)
			setGlobalDirectory(getCurrentDirectory());
		return result;
	}
	
	@Override
	public void updateUI() {
		// TODO Implement Numworx Look and Feel
		super.updateUI();
	}

}
