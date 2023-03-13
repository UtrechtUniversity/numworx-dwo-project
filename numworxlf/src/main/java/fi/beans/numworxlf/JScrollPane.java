package fi.beans.numworxlf;

import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.plaf.ScrollBarUI;

public class JScrollPane extends javax.swing.JScrollPane {

  public JScrollPane() {
  }

  public JScrollPane(Component view) {
    super(view);
  }

  public JScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy, hsbPolicy);
  }

  public JScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
  }

  /* (non-Javadoc)
   * @see javax.swing.JScrollPane#createHorizontalScrollBar()
   */
  @Override
  public JScrollBar createHorizontalScrollBar() {
    JScrollBar b = super.createHorizontalScrollBar();
    b.setUI((ScrollBarUI) NumworxScrollBarUI.createUI(b));
    return b;
  }

  /* (non-Javadoc)
   * @see javax.swing.JScrollPane#createVerticalScrollBar()
   */
  @Override
  public JScrollBar createVerticalScrollBar() {
    JScrollBar b = super.createVerticalScrollBar();
    b.setUI((ScrollBarUI) NumworxScrollBarUI.createUI(b));
    return b;
  }

}
