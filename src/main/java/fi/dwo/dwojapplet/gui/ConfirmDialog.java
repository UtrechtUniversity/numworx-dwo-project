package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class ConfirmDialog extends JDialog {

  static Window getWindowForComponent(Component parentComponent)
      throws HeadlessException {
      if (parentComponent == null)
          return DwoHelper.getFrameForComponent(null);
      if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
          return (Window)parentComponent;
      return getWindowForComponent(parentComponent.getParent());
  }
  
  private int option = JOptionPane.CLOSED_OPTION;

  public ConfirmDialog(Component owner, String title) {
    super(getWindowForComponent(owner), title);
    setModal(true);
    setDefaultCloseOperation(ConfirmDialog.DISPOSE_ON_CLOSE);
    Dimension screen = getToolkit().getScreenSize();
    screen.width -= 100;
    screen.height -= 100;
    setMaximumSize(screen);
  }
  public void ok(ActionEvent e) { option = JOptionPane.OK_OPTION; dispose(); }
  public void cancel(ActionEvent e) { option = JOptionPane.CANCEL_OPTION; dispose(); }
  
  public int getOption() { return option; }
  public void center() {
    int w = getWidth();
    int h = getHeight();
    Dimension screen = getToolkit().getScreenSize();
    setLocation( (screen.width-w)/2, (screen.height-h)/2);
  }
}