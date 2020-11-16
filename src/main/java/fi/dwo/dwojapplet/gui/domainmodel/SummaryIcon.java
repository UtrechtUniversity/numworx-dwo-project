package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

public class SummaryIcon implements Icon, PIcon {
  final FontMetrics fm;

  int green, red, white;
  String redText, greenText;
  
  SummaryIcon(DomStudentModelScore<?> s, FontMetrics fm) {
    this.fm = fm;
    double green,red;
    double scale = s.getTotalCount() > 0 ? s.getCount() / (double)s.getTotalCount(): 1;
    if (s.getGreenCount() > 0) {
      green = (s.getGreenScore()/s.getGreenCount() - 0.5) * scale;;
    } else green = 0;
    if (s.getRedCount() > 0) {
      red = (0.5-s.getRedScore()/s.getRedCount()) * scale;
    } else red = 0;

    calculate(green, red);
  }
  
  
  SummaryIcon(double green, double red, FontMetrics fm) {
    this.fm = fm;
    calculate(green, red);
  }


  private void calculate(double green, double red) {
    this.green = Math.round((float) green * 40);
    if (this.green == 0 && green > 0) this.green = 1;
    this.red = Math.round((float)red * 40);
    if(this.red == 0 && red > 0) this.red = 1;    
    white = 20 - this.red - this.green;
    redText = Math.round(red*200)+"%";
    greenText = Math.round(green*200)+"%";
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    int h = getIconHeight()-2-3;
    int w = 7;
    int i = 0;
    y += 1;
    y += h/4; // centreer, halve hoogte
    h = h/2;
    x += 1;
    g.setColor(LeerdomeinResultsPanel2.GREEN);
    while ( i < green) {
      g.fillRect(x, y, w-2, h);
      x += w;
      i++;
    }
    g.setColor(LeerdomeinResultsPanel2.RED);
    while (i < (red + green)) {
      g.fillRect(x, y, w-2, h);
      x += w;
      i++;
      
    }
    g.setColor(Color.WHITE);
    while (i < 20) {
      g.fillRect(x, y, w-2, h);
      x += w;
      i++;
    }
  }

  @Override
  public int getIconWidth() {
    return 150;
  }

  @Override
  public int getIconHeight() {
    return fm.getHeight()+4+3;
  }

  @Override
  public String getRedPercentage() {
    return "";
  }

  @Override
  public String getGreenPercentage() {
    return "";
  }

}
