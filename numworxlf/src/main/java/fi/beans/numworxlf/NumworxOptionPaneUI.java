package fi.beans.numworxlf;

import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class NumworxOptionPaneUI extends BasicOptionPaneUI {
  /**
   * Creates a new BasicOptionPaneUI instance.
   */
 public static ComponentUI createUI(JComponent x) {
     return new NumworxOptionPaneUI();
 }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicOptionPaneUI#createButtonArea()
   */
  @Override
  protected Container createButtonArea() {
    Container area = super.createButtonArea();
    for (Component c: area.getComponents()) {
      if (c.getClass() == javax.swing.JButton.class) {
        ((AbstractButton) c).setUI(JButton.UI);
      }
    }
    return area;
  }

}
