package fi.dwo.dwojapplet.gui.domutils;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import fi.beans.copyright.NumworxInfo;
import fi.beans.numworxlf.JRadioButton;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.gui.GuiCreator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;

public class DwoProfileStrategy extends JPanel implements ActionListener {
	
  private static int first(int id, String name) {
	  try {
		  return PublicProfileCache.get(name).asLong().intValue();
	  } catch(Exception oops) {}
	  return id;
  }
	
	
  static final int HE_ID = first(100, "HE (English)");

  static final int SE_ID = first(92, "SE (English)");

  static final int HO_ID = first(99, "HO");

  static final int VO_ID = first(77, "VO");

  JRadioButton vo;

  JRadioButton ho;

  JRadioButton se;

  JRadioButton he;
  
  NumworxInfo info;

  protected ButtonGroup grp;

  public DwoProfileStrategy(NumworxInfo info) {
    super(null);
    this.info = info;
    info.addActionListener(this);
    info.setDwoProfile(Integer.toString(DWO.getDwoProfileID()));
    initialize();
  }
   
  protected void initialize() {
    //p.setBounds(dialog.getWidth() / 2 - 175, 110+100, 340, 100);
    JPanel p = this;
    Color PANEL_BACKGROUND = fi.beans.numworxlf.Constants.COLOR15;
    Color ITEM_BACKGROUND = Color.decode("#1b75bb");
    Font  PLAIN = new Font("SansSerif",Font.PLAIN, 13);
    Font  BOLD  = new Font("SansSerif", Font.BOLD, 13);
    p.setBackground(PANEL_BACKGROUND);
    vo = new JRadioButton("VO");
    vo.setBackground(PANEL_BACKGROUND);vo.setFont(PLAIN);
    if(DWO.getDwoProfileID() == VO_ID) vo.setSelected(true);
    vo.setSize(vo.getPreferredSize());vo.setLocation(20, 20);
    ho = new JRadioButton("HO");
    ho.setBackground(PANEL_BACKGROUND);ho.setFont(PLAIN);
    if(DWO.getDwoProfileID() == HO_ID) ho.setSelected(true);
    ho.setSize(ho.getPreferredSize());ho.setLocation(20, 60);
    se = new JRadioButton("SE (English)");
    se.setBackground(PANEL_BACKGROUND);se.setFont(PLAIN);
    if(DWO.getDwoProfileID() == SE_ID) se.setSelected(true);
    se.setSize(se.getPreferredSize());se.setLocation(160, 20);
    he = new JRadioButton("HE (English)");
    he.setBackground(PANEL_BACKGROUND);he.setFont(PLAIN);
    if(DWO.getDwoProfileID() == HE_ID) he.setSelected(true);
    he.setSize(he.getPreferredSize());he.setLocation(160, 60);
    grp = new ButtonGroup();
    grp.add(he);grp.add(se);grp.add(ho);grp.add(vo);
    ho.setForeground(Color.WHITE);
    vo.setForeground(Color.WHITE);
    he.setForeground(Color.WHITE);
    se.setForeground(Color.WHITE);
    
    p.add(ho); p.add(vo);p.add(se);p.add(he);


  }
  
  public void switchProfile() {
    DWO dwo = GuiCreator.instance().getDWO();
    info.removeActionListener(this);
    if(vo.isSelected() && DWO.getDwoProfileID() != VO_ID)
      dwo.switchProfile(VO_ID,"nl");
    else if (ho.isSelected() && DWO.getDwoProfileID() != HO_ID)
      dwo.switchProfile(HO_ID, "nl");
    else if (se.isSelected() && DWO.getDwoProfileID() != SE_ID)
      dwo.switchProfile(SE_ID, "en");
    else if (he.isSelected() && DWO.getDwoProfileID() != HE_ID)
      dwo.switchProfile(HE_ID, "en");
   }

  public static DwoProfileStrategy instance(NumworxInfo info) {
    DWO dwo = GuiCreator.instance().getDWO();
    String ext = dwo.getParameter(DWO.PROFILE_EXTENSION);
    if ("nlen".equals(ext))
      return new UUProfileStrategy(info);
    return new DwoProfileStrategy(info);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == info) {
      String lang = info.getLanguage();
      int pr = Integer.parseInt(info.getDwoProfile());
      DWO dwo = GuiCreator.instance().getDWO();
      grp.clearSelection();
      dwo.switchProfile(pr, lang);
    }
    
  }

  
}
