package fi.beans.numworxlf;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class NumworxComboBoxUI extends BasicComboBoxUI implements Constants {


  static Border border = new BorderUIResource(BorderFactory.createLineBorder(colorBlue3));
  static Font ARROW_FONT  = new Font("SansSerif", Font.BOLD, 20);

  class Arrow implements Icon {

    @Override
    public void paintIcon(Component c, Graphics gr, int x, int y) {
    		Graphics2D g = (Graphics2D)gr;
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
      Font f = g.getFont();
      g.setFont(ARROW_FONT);
      g.setColor(WHITE);
      g.drawString("▾", x+2, y+14);
      g.setFont(f);
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
  
  
  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicComboBoxUI#createArrowButton()
   */
  @Override
  protected JButton createArrowButton() {
    JButton button = new JButton(new Arrow());
    button.setName("ComboBox.arrowButton");
    button.setBorder(BorderFactory.createEmptyBorder());
    
    return button;
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicComboBoxUI#installDefaults()
   */
  @Override
  protected void installDefaults() {
    super.installDefaults();
    if (comboBox.getBorder() instanceof UIResource || comboBox.getBorder()== null) comboBox.setBorder(border);
    if (comboBox.getBackground() instanceof UIResource) comboBox.setBackground(WHITE);
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicComboBoxUI#paintCurrentValue(java.awt.Graphics, java.awt.Rectangle, boolean)
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
    ListCellRenderer renderer = comboBox.getRenderer();
    Component c;
    
    if ( hasFocus && !isPopupVisible(comboBox) ) {
        c = renderer.getListCellRendererComponent( listBox,
                                                   comboBox.getSelectedItem(),
                                                   -1,
                                                   true,
                                                   false );
    }
    else {
        c = renderer.getListCellRendererComponent( listBox,
                                                   comboBox.getSelectedItem(),
                                                   -1,
                                                   false,
                                                   false );
        c.setBackground(WHITE);
    }
    c.setFont(comboBox.getFont());
    if ( hasFocus && !isPopupVisible(comboBox) ) {
      c.setForeground(comboBox.getForeground());
      c.setBackground(comboBox.getBackground());
    }
    else {
        if ( comboBox.isEnabled() ) {
            c.setForeground(comboBox.getForeground());
            c.setBackground(comboBox.getBackground());
        }
        else { // TODO correct colors
            c.setForeground(Constants.COLOR22);
            c.setBackground(comboBox.getBackground());
        }
    }
    
    // Fix for 4238829: should lay out the JPanel.
    boolean shouldValidate = false;
    if (c instanceof JPanel)  {
        shouldValidate = true;
    }
    
    int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
    if (padding != null) {
        x = bounds.x + padding.left;
        y = bounds.y + padding.top;
        w = bounds.width - (padding.left + padding.right);
        h = bounds.height - (padding.top + padding.bottom);
    }
    
    currentValuePane.paintComponent(g,c,comboBox,x,y,w,h,shouldValidate);
  }


}
