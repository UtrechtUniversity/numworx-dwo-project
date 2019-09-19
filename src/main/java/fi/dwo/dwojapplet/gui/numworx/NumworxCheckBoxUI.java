package fi.dwo.dwojapplet.gui.numworx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicCheckBoxUI;


public class NumworxCheckBoxUI extends BasicCheckBoxUI implements Constants {

  private static class NumworxCheckIcon implements Icon {
    static final int GAP = 0;
    static final int GAP2 = GAP*2;
    private static Font vfont = new Font("SansSerif", Font.BOLD, 13);
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(Color.white);
      g.fillRect(x, y, getIconWidth(), getIconHeight());
      g.setColor(colorBlue3);
      g.drawRect(x, y, getIconWidth(), getIconHeight());
      AbstractButton b = (AbstractButton) c;
      if (b.isSelected()) { 
        g.fillRect(x+GAP, y+GAP, getIconWidth()-GAP2, getIconHeight()-GAP2);
        g.setColor(Color.white);
        Font f = g.getFont();
        g.setFont(vfont);
        g.drawString("v",x+4,y+10);
        g.setFont(f);
      }
    }

    @Override
    public int getIconWidth() {
      return 15;
    }

    @Override
    public int getIconHeight() {
      return 15;
    }
    
  }
  
  @Override
  protected void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    Font f = b.getFont();
    if (f instanceof UIResource) b.setFont(FONT12);
    Color c = b.getForeground();
    if (c instanceof UIResource) b.setForeground(colorBlue1);
    icon = new NumworxCheckIcon();
  }

}
