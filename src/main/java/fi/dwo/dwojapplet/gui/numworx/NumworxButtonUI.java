package fi.dwo.dwojapplet.gui.numworx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

public class NumworxButtonUI extends BasicButtonUI {

  private static final Color BACKGROUND = new ColorUIResource(0x1b75bb);
  private Color FOREGROUND = new ColorUIResource(Color.white);
  private static final Font FONT = new FontUIResource("Ubuntu", Font.BOLD, 13);
  private static final Border BORDER = new BorderUIResource(BorderFactory.createEmptyBorder(3,12,3,12));

  NumworxButtonUI() {
    super();
  }

  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    Font f = b.getFont();
    if (f instanceof UIResource) b.setFont(FONT);
    Color c;
    c = b.getBackground();
    if (c instanceof UIResource) b.setBackground(BACKGROUND);
    c = b.getForeground();
    if (c instanceof UIResource) b.setForeground(FOREGROUND);
    Border border = b.getBorder();
    if (border instanceof UIResource) b.setBorder(BORDER);
    LookAndFeel.installProperty(b, "opaque", Boolean.TRUE);
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    // TODO Auto-generated method stub
    super.paint(g, c);
  }

  @Override
  public void update(Graphics arg0, JComponent arg1) {
    // TODO Auto-generated method stub
    super.update(arg0, arg1);
  }

}
