package fi.beans.numworxlf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

//import sun.swing.SwingUtilities2; // FIXME DUS NOT WORK

public class NumworxButtonUI extends BasicButtonUI {

  private static final Color BACKGROUND = Constants.colorBlue3;
  private Color FOREGROUND = new ColorUIResource(Color.white);
  private static final Font FONT = Constants.FONT13;
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
    AbstractButton b = (AbstractButton) c;
    if (!c.isEnabled() && b.isContentAreaFilled()) {
      g.setColor(Constants.COLOR22);
      g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }
    super.paint(g, c);
  }

  @Override
  public void update(Graphics arg0, JComponent arg1) {
    // TODO Auto-generated method stub
    super.update(arg0, arg1);
  }

  private final AbstractButton dummy = new AbstractButton() { { 
	  setModel(new DefaultButtonModel());
  }  };
  @Override
  protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
    if (!b.isEnabled()) {
      //b.setEnabled(true);
    	dummy.setFont(b.getFont());
    	dummy.setForeground(b.getForeground());
      super.paintText(g, dummy, textRect, text);
      //b.setEnabled(false);
      return;
    }
    super.paintText(g, b, textRect, text);
  }
}
