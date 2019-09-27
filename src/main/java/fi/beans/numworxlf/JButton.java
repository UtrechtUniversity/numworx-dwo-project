package fi.beans.numworxlf;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.ButtonUI;

public class JButton extends javax.swing.JButton {

  public final static ButtonUI UI = new NumworxButtonUI();
  
  public JButton() {
    super();
  }

  public JButton(Action a) {
    super(a);
  }

  public JButton(Icon icon) {
    super(icon);
  }

  public JButton(String text, Icon icon) {
    super(text, icon);
  }

  public JButton(String text) {
    super(text);
  }

  @Override
  public void updateUI() {
    setUI(UI);
  }


}
