package fi.dwo.dwojapplet.gui.domutils;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import fi.beans.copyright.NumworxInfo;
import fi.beans.numworxlf.JRadioButton;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.gui.GuiCreator;

public class UUProfileStrategy extends DwoProfileStrategy {

  static final Locale EN = Locale.forLanguageTag("en");
  static final Locale NL = Locale.forLanguageTag("nl");
  
  public UUProfileStrategy(NumworxInfo info) {
    super(info);
  }

  @Override
  protected void initialize() {
    //p.setBounds(dialog.getWidth() / 2 - 175, 110+100, 340, 100);
    JPanel p = this;
    Color PANEL_BACKGROUND = fi.beans.numworxlf.Constants.COLOR15;
    Font  PLAIN = new Font("SansSerif",Font.PLAIN, 13);
    p.setBackground(PANEL_BACKGROUND);
    ho = new JRadioButton("Nederlands");
    ho.setBackground(PANEL_BACKGROUND);ho.setFont(PLAIN);
    if(getLocale().equals(NL)) ho.setSelected(true);
    ho.setSize(ho.getPreferredSize());ho.setLocation(20, 60);
    he = new JRadioButton("English");
    he.setBackground(PANEL_BACKGROUND);he.setFont(PLAIN);
    if(getLocale().equals(EN)) he.setSelected(true);
    he.setSize(he.getPreferredSize());he.setLocation(160, 60);
    grp  = new ButtonGroup();
    grp.add(he);grp.add(ho);
    ho.setForeground(Color.WHITE);
    he.setForeground(Color.WHITE);    
    p.add(ho); p.add(he);
  }

  @Override
  public void switchProfile() {
    DWO dwo = GuiCreator.instance().getDWO();
   info.removeActionListener(this);
   int profile = DWO.getDwoProfileID();
    Locale locale = getLocale();
    if (ho.isSelected() && !NL.equals(locale))
      dwo.switchProfile(profile, "nl");
    else if (he.isSelected() && !EN.equals(locale))
      dwo.switchProfile(profile, "en");
  }

}
