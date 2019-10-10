package fi.beans.numworxlf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class NumworxScrollBarUI extends BasicScrollBarUI implements Constants {

  private static final Border BORDER = new BorderUIResource(BorderFactory.createLineBorder(Color.WHITE));

public static ComponentUI createUI(JComponent c)    {
    return new NumworxScrollBarUI();
}

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#installDefaults()
   */
  @Override
  protected void installDefaults() {
    super.installDefaults();
    if (scrollbar.getBorder() == null || scrollbar.getBorder() instanceof UIResource)
    	scrollbar.setBorder(BORDER);
    scrollbar.setBackground(COLOR20);
    thumbColor = COLOR21;
    thumbDarkShadowColor = COLOR21;
    thumbHighlightColor = COLOR21;
    thumbLightShadowColor = COLOR21;
    trackColor = COLOR20;
    trackHighlightColor = COLOR20;
    
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#paint(java.awt.Graphics, javax.swing.JComponent)
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    super.paint(g, c);
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#paintTrack(java.awt.Graphics, javax.swing.JComponent, java.awt.Rectangle)
   */
  @Override
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    super.paintTrack(g, c, trackBounds);
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#paintThumb(java.awt.Graphics, javax.swing.JComponent, java.awt.Rectangle)
   */
  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    super.paintThumb(g, c, thumbBounds);
   }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#createDecreaseButton(int)
   */
  @Override
  protected JButton createDecreaseButton(int orientation) {
    JButton b = super.createDecreaseButton(orientation);
    b.setBorder(BorderFactory.createEmptyBorder());
    b.setBackground(COLOR20);
    return b;
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicScrollBarUI#createIncreaseButton(int)
   */
  @Override
  protected JButton createIncreaseButton(int orientation) {
    JButton b = super.createIncreaseButton(orientation);
    b.setBorder(BorderFactory.createEmptyBorder());
    b.setBackground(COLOR20);
    return b;
  }

}
