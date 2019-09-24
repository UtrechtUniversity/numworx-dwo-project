package fi.dwo.dwojapplet.gui.numworx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

import sun.swing.SwingUtilities2;

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

  @Override
  protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
    JComponent c = (JComponent)b;
    AbstractButton b1 = (AbstractButton) c;
    ButtonModel model = b1.getModel();
    FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
    int mnemonicIndex = b1.getDisplayedMnemonicIndex();
    
    /* Draw the Text */
    if(model.isEnabled() || true ){
        /*** paint the text normally */
        g.setColor(b1.getForeground());
    }
    else {
        /*** paint the text disabled ***/
        g.setColor(b1.getBackground().brighter());
    }
    SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex,
      textRect.x + getTextShiftOffset(),
      textRect.y + fm.getAscent() + getTextShiftOffset());
  }

}
