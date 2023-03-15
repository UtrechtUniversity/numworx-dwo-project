package fi.beans.numworxlf;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.ButtonUI;

public class JCheckBox extends javax.swing.JCheckBox {

  public final static ButtonUI UI = new NumworxCheckBoxUI();

  public JCheckBox() {
  }

  public JCheckBox(Icon icon) {
    super(icon);
  }

  public JCheckBox(String text) {
    super(text);
  }

  public JCheckBox(Action a) {
    super(a);
  }

  public JCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
  }

  public JCheckBox(String text, boolean selected) {
    super(text, selected);
  }

  public JCheckBox(String text, Icon icon) {
    super(text, icon);
    // TODO Auto-generated constructor stub
  }

  public JCheckBox(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
  }
  @Override
  public void updateUI() {
    //super.updateUI();
    setUI(UI);
  }

}
