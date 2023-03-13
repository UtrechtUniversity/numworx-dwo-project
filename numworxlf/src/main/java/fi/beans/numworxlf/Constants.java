package fi.beans.numworxlf;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public interface Constants {
  Color COLOR10 = new Color(228,242,251); // Module achtergrond
  Color COLOR11 = new Color(210,229,245); // body coursepanel/coursechoicepanel
  Color COLOR13 = new Color(114,151,199); // font text in header
  Color COLOR14 = new Color(27,117,187);  // button background
  Color COLOR15 = new Color(49,71,112);   // color 15 (color of menu text, body text)
  Color COLOR20 = new Color(237, 239, 241); // niet helemaal goed, moet zijn:  239,241,243
  Color COLOR21 = new Color(221,223,225);
  Color COLOR22 = new Color(168,171,177); // jbutton disabled
  Color COLOR30 = new Color(252,211, 69);  // bewerken

  Color BLUE1 = new ColorUIResource(COLOR14);
  Color colorBlue3 = new ColorUIResource(COLOR13);
  Color colorBlue1 = new ColorUIResource(COLOR15); 
  Font FONT13 = new FontUIResource("SansSerif", Font.BOLD, 13);
  Color WHITE = new ColorUIResource(Color.WHITE);
  Font FONT12 = new Font("SansSerif",Font.PLAIN,12);

  // scrollbar thumb 221,223,225 color21
  // scrollbar background 238,238,238 color20
}
