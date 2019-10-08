package fi.beans.numworxlf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

public class NumworxTabbedPaneUI extends BasicTabbedPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new NumworxTabbedPaneUI();
}
  
  protected Color selectedColor;
  protected Color selectedForeground;
  
  
  @Override
  protected void installDefaults() {
    super.installDefaults();
    selectedColor = Constants.BLUE1;   
    selectedForeground = Constants.COLOR10;
  }

  @Override
  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w,
      int h, boolean isSelected) {
  }

  @Override
  protected void paintTabBackground(Graphics g, int tabPlacement,
                                    int tabIndex,
                                    int x, int y, int w, int h,
                                    boolean isSelected ) {
      g.setColor(!isSelected || selectedColor == null?
                 tabPane.getBackgroundAt(tabIndex) : selectedColor);
      switch(tabPlacement) {
        case LEFT:
            g.fillRect(x+1, y+1, w-1, h-3);
            break;
        case RIGHT:
            g.fillRect(x, y+1, w-2, h-3);
            break;
        case BOTTOM:
            g.fillRect(x+1, y, w-3, h-1);
            break;
        case TOP:
        default:
            g.fillRect(x+1, y+1, w-3, h-1);
      }
  }
  protected void paintText(Graphics g, int tabPlacement,
                           Font font, FontMetrics metrics, int tabIndex,
                           String title, Rectangle textRect,
                           boolean isSelected) {

      g.setFont(font);

      View v = getTextViewForTab(tabIndex);
      if (v != null) {
          // html
          v.paint(g, textRect);
      } else {
          // plain text
          int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

          if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
              Color fg = tabPane.getForegroundAt(tabIndex);
              if (isSelected && (fg instanceof UIResource)) {
                  Color selectedFG = selectedForeground;
                  if (selectedFG != null) {
                      fg = selectedFG;
                  }
              }
              g.setColor(fg);

              drawStringUnderlineCharAt(tabPane, g,
                           title, mnemIndex,
                           textRect.x, textRect.y + metrics.getAscent());

          } else { // tab disabled
              g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
              drawStringUnderlineCharAt(tabPane, g,
                           title, mnemIndex,
                           textRect.x, textRect.y + metrics.getAscent());
              g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
              drawStringUnderlineCharAt(tabPane, g,
                           title, mnemIndex,
                           textRect.x - 1, textRect.y + metrics.getAscent() - 1);

          }
      }
  }

  private void drawStringUnderlineCharAt(JTabbedPane tabPane, Graphics g, String title,
      int mnemIndex, int x, int y) {
    g.drawString(title, x, y);   
  }
}
