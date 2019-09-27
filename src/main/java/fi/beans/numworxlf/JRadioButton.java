package fi.beans.numworxlf;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.ButtonUI;

public class JRadioButton extends javax.swing.JRadioButton {

  public final static ButtonUI UI = new NumworxRadioButtonUI();
  
  public JRadioButton() {
  }

  public JRadioButton(Icon icon) {
    super(icon);
  }

  public JRadioButton(Action a) {
    super(a);
  }

  public JRadioButton(String text) {
    super(text);
  }

  public JRadioButton(Icon icon, boolean selected) {
    super(icon, selected);
  }

  public JRadioButton(String text, boolean selected) {
    super(text, selected);
  }

  public JRadioButton(String text, Icon icon) {
    super(text, icon);
  }

  public JRadioButton(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
  }

  @Override
  public void updateUI() {
    //super.updateUI();
    setUI(UI);
  }

}
