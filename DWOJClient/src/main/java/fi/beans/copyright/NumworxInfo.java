package fi.beans.copyright;

import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JComboBox;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class NumworxInfo extends JLabel implements ActionListener {

  static class WL extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        e.getWindow().dispose();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        e.getWindow().dispose();
    }
  }

  class Mouse extends MouseAdapter {

    @Override
    public void mouseReleased(MouseEvent e) {
        openInfo(e);
    }   
}

  private static final String HE_ENGLISH = "HE (English)";
  private static final String SE_ENGLISH = "SE (English)";
  private static final String VO = "VO";
  private static final String HO = "HO";
  private String title, text[];
  private JComboBox<String> lang;
  private JComboBox<String> profile;
  private ActionListener al;

  public NumworxInfo(String title, String[] text) {
    this.title = title;
    this.text = text;
    setIcon(new ImageIcon(getClass().getResource("NumworxAuthor.png")));
    setHorizontalAlignment(CENTER);
    addMouseListener(new Mouse());
  }

  public void addActionListener(ActionListener l) {
    al = AWTEventMulticaster.add(al, l);
  }
  
  public void removeActionListener(ActionListener l) {
    al = AWTEventMulticaster.remove(al, l);
  }
  
  public void openInfo(MouseEvent e) {
    Frame f = JOptionPane.getFrameForComponent(this);
    JDialog infoDialog = new JDialog(f, title,false);
    infoDialog.addWindowListener(new WL());
    infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    Box b = Box.createVerticalBox();
    for(String line : text) b.add(label(line));
    if(DwoHelper.isTest())
      b.add(label("Java: " + System.getProperty("java.vendor")  + ", " + System.getProperty("java.version")));
    lang = new JComboBox<String>();
    lang.addItem("nl");
    lang.addItem("de");
    lang.addItem("en");
    lang.addItem("fr");
    lang.setEditable(true);
    lang.setSelectedItem(getLanguage().intern());
    lang.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    profile = new JComboBox<String>();
    profile.addItem(VO);
    profile.addItem(HO);
    profile.addItem(SE_ENGLISH);
    profile.addItem(HE_ENGLISH);
    profile.setEditable(true);
    profile.setSelectedItem(fromProfile(getDwoProfile()));
    profile.addActionListener(this);
    profile.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    b.add(Box.createVerticalStrut(5));
    b.add(profile);
    b.add(Box.createVerticalStrut(5));
    b.add(lang);
    b.add(Box.createVerticalStrut(5));
    
    JButton start = new JButton("Start");
    start.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    start.addActionListener(ev -> { 
      String item = (String) lang.getSelectedItem();
      setLanguage(item);
      item = (String) profile.getSelectedItem();
      item = toProfile(item);
      setDwoProfile(item);
      infoDialog.dispose();
      restart();
    });
    b.add(start);
    b.add(Box.createVerticalStrut(5));
    b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    infoDialog.getContentPane().add(b);
    infoDialog.getContentPane().setBackground(Constants.COLOR20);
    infoDialog.pack();
    Dimension size = f.getToolkit().getScreenSize();
    size.width -= infoDialog.getWidth()+3;
    size.height -= infoDialog.getHeight()+3;
    int x = e.getXOnScreen()-30;
    x = Math.max(0, x);
    x = Math.min(size.width, x);
    int y = e.getYOnScreen()-30;
    y = Math.max(0, y);
    y = Math.min(size.height, y);       
    infoDialog.setLocation(x, y);
    infoDialog.setVisible(true);
}

  private Component label(String line) {
    JLabel label = new JLabel(line);
    label.setForeground(GuiConstants.HEADER_COLOR);
    label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    label.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
    return label;
  }

  private String fromProfile(String item) {
    try {
      DomDwoProfileFull dwo = PublicProfileManager.get(item);
      return dwo.getDwoProfileName();
    } catch (Dwo2Exception e) {
    }
    return item;
  }

  private String toProfile(String item) {
    try {
      DomDwoProfileFull dwo = PublicProfileManager.get(item);
      return f(dwo.getId().getIdString());
    } catch (Dwo2Exception e) {
    }
    return item;
  }

  private String f(String s) {
    int i = s.lastIndexOf(';');
    int j = i;
    while ( s.charAt(++i)=='0' ) j = i;
    return s.substring(j+1);
  }

  private void restart() {
    if (al != null)
      al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "restart"));
  }

  private String language = getLocale().getLanguage();
  private String dwoProfile = VO;
  
  public String getLanguage() {
    return language;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String item = (String) profile.getSelectedItem();
    System.out.println(item);
    item = toProfile(item);
    toLanguage(item);
  }

  private boolean toLanguage(DomDwoProfileFull profile) {
	  String lang = profile.getLanguage();
	  if (lang != null) this.lang.setSelectedItem(lang);
	  return lang != null;
  }
  
  private void toLanguage(String item) {
	    try {
	        DomDwoProfileFull dwo = PublicProfileManager.get(item);
	        if (toLanguage(dwo))
	        	return;
	      } catch (Dwo2Exception e) {
	      }
// oldschool
    if("99".equals(item))    lang.setSelectedItem("nl");
    else if("77".equals(item)) lang.setSelectedItem("nl");
    else if("92".equals(item)) lang.setSelectedItem("en");
    else if("100".equals(item)) lang.setSelectedItem("en");
  }

  public String getDwoProfile() {
    return dwoProfile;
  }

  public void setDwoProfile(String dwoProfile) {
    this.dwoProfile = dwoProfile;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

}
