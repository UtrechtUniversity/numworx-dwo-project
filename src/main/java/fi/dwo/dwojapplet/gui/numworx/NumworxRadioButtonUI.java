package fi.dwo.dwojapplet.gui.numworx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class NumworxRadioButtonUI extends BasicRadioButtonUI implements Constants {

  private class NumworxRadioIcon implements Icon, UIResource {

    static final int GAP = 4;
    static final int GAP2 = GAP*2;
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(Color.white);
      g.fillOval(x, y, getIconWidth(), getIconHeight());
      g.setColor(colorBlue3);
      g.drawOval(x, y, getIconWidth(), getIconHeight());
      AbstractButton b = (AbstractButton) c;
      if (b.isSelected()) { 
        g.fillOval(x+GAP, y+GAP, getIconWidth()-GAP2, getIconHeight()-GAP2);
      }
    }

    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public int getIconHeight() {
      return 16;
    }
    
  }
    
  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    Font f = b.getFont();
    if (f instanceof UIResource) b.setFont(FONT12);
    Color c = b.getForeground();
    if (c instanceof UIResource) b.setForeground(colorBlue1);
    icon = new NumworxRadioIcon();
  }

}
