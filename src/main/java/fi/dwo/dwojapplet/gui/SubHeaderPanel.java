package fi.dwo.dwojapplet.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

class SubHeaderPanel extends JPanel {

  private static final int HEIGHT = 40;
  private JComponent sub;
  
  SubHeaderPanel() {
    super(null);
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setMinimumSize(new Dimension(HEIGHT, HEIGHT));
    setMaximumSize(new Dimension(Short.MAX_VALUE, HEIGHT));
    setPreferredSize(new Dimension(500, HEIGHT)); // sort of.. stretch horizontal.
    setBackground(GuiConstants.MAIN_BACKGROUND); 
  }

  public void setSubHeaderPanel(JComponent sub) {
    if (this.sub != null)
      remove(this.sub);
    this.sub = sub;
    if (sub != null) {
      add(sub);
    }
  }

}
