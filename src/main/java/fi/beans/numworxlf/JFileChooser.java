/**
 * 
 */
package fi.beans.numworxlf;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

/**
 * @author wim
 *
 */
@SuppressWarnings("serial")
public class JFileChooser extends javax.swing.JFileChooser {

  /**
   * 
   */
  public JFileChooser() {
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

}
