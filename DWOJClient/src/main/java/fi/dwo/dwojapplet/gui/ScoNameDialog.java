// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoNameDialog.java
package fi.dwo.dwojapplet.gui;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.action.CopyLabel;
import fi.dwo.dwojapplet.gui.action.ShareHTMLAction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This is a dialog for editing the SCO name and description.
 *
 * @author M.J.B. Kupers
 *
 */
public class ScoNameDialog extends JDialog 

{
    private boolean confirmed;
    private JCheckBox showScore, showDocent;
    private JTextField nameField;
    private JTextArea textarea;
    private JButton logoBtn;

    public ScoNameDialog(Component owner, String title, int id, String name, String description,
      String guiName, String guiDescription, boolean b) {
      super(DwoHelper.getFrameForComponent(owner), true);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);

      Container content = getContentPane();
      content.setLayout(new BorderLayout());
      JLabel header = new JLabel(title);
      header.setOpaque(true);
      header.setFont(new Font("Ubuntu", Font.PLAIN, 24));
      header.setForeground(Constants.COLOR21);
      header.setBackground(GuiConstants.HEADER_COLOR);
      header.setHorizontalAlignment(JLabel.CENTER);
      header.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));

      content.add(header, BorderLayout.PAGE_START);
      JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
      footer.setOpaque(true);
      footer.setBackground(Constants.COLOR21);
      JButton ok = new JButton(TextMapper.getText(TextMapper.BTN_OK));
      ok.addActionListener(this::onOk);
      footer.add(ok);
      JButton cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
      sameSize(ok, cancel);
      cancel.addActionListener(this::onCancel);
      footer.add(cancel);
      content.add(footer, BorderLayout.PAGE_END);
      
      Box hb = Box.createHorizontalBox(); content.add(hb);
      
      Box vb;
// left box:
      vb = Box.createVerticalBox(); hb.add(vb);
      JLabel nameLabel = new JLabel(TextMapper.getText(guiName));
      nameLabel.setForeground(Constants.COLOR15);
      nameLabel.setFont(Constants.FONT13);
      vb.add(nameLabel); vb.add(Box.createVerticalStrut(5));
      nameLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      this.nameField = new JTextField(name);
      nameField.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      vb.add(nameField); vb.add(Box.createVerticalStrut(5));
      JLabel descLabel = new JLabel(TextMapper.getText(guiDescription));
      descLabel.setForeground(Constants.COLOR15);
      descLabel.setFont(Constants.FONT13);
      descLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      vb.add(descLabel); vb.add(Box.createVerticalStrut(5));
      this.textarea = new JTextArea(description, 0, 0);
      JScrollPane pane = new JScrollPane(textarea);
      pane.setSize(200, 100);
      pane.setPreferredSize(pane.getSize());
      pane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      pane.setBorder(BorderFactory.createLineBorder(Constants.colorBlue3));
      
      vb.add(pane); vb.add(Box.createVerticalStrut(5));
      
      showScore = new JCheckBox(TextMapper.getText(TextMapper.GUIS_SHOW_SCORE));
      showScore.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      vb.add(showScore);
      showDocent = new JCheckBox("Docent ziet score");
      showDocent.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      vb.add(showDocent);
      
      if (id > 0) {
        vb.add(Box.createVerticalStrut(5));
        JLabel idLabel = new JLabel(TextMapper.getText("Activiteitnummer: " + id));
        vb.add(idLabel);
        idLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        idLabel.setForeground(Constants.COLOR15);
        if (DwoHelper.isPremium()) {
          CopyLabel copyAction = new CopyLabel(idLabel.getText());
          copyAction.add(new ShareHTMLAction(id, true));        
          idLabel.addMouseListener(copyAction);
        }
      }
      hb.add(Box.createHorizontalStrut(40));
      hb.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
// right box
      vb = Box.createVerticalBox(); hb.add(vb);
      JLabel logoLabel = new JLabel(TextMapper.getText("Activiteitafbeelding"));
      logoLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      logoLabel.setForeground(Constants.COLOR15);
      logoLabel.setFont(Constants.FONT13);
      vb.add(logoLabel);vb.add(Box.createVerticalStrut(5));
      
      logoBtn = new JButton();
      logoBtn.setContentAreaFilled(false);
      logoBtn.setBorder(BorderFactory.createLineBorder(Constants.colorBlue3));
      logoBtn.setBorderPainted(true);
      logoBtn.setSize(252, 160);
      logoBtn.setPreferredSize(logoBtn.getSize());
      logoBtn.setMinimumSize(logoBtn.getSize());
      logoBtn.setMaximumSize(logoBtn.getSize());
      logoBtn.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      vb.add(logoBtn);
      vb.add(Box.createVerticalGlue());

      owner = DwoHelper.getApplet(); // centreer t.o.v. dwo applet
      Point p = owner != null ? owner.getLocationOnScreen() : new Point(0, 0);
      Dimension parentSize = owner != null ? owner.getSize()
              : Toolkit.getDefaultToolkit().getScreenSize();
      pack();
      Dimension mySize = getSize();
      int x = p.x + (parentSize.width - mySize.width) / 2;
      int y = p.y + (parentSize.height - mySize.height) / 2;
      setLocation(x, y);

    }

    private void sameSize(JButton... btns) {
      Dimension max = btns[0].getPreferredSize();
      for(JButton b: btns) {
        Dimension pref = b.getPreferredSize();
        if (pref.width > max.width) max.width = pref.width;
        if (pref.height > max.height) max.height = pref.height;
      }
      for(JButton b: btns) {
        b.setPreferredSize(max);
      }
      
    }

    private void onOk(ActionEvent e) {this.confirmed = true; dispose();}
    private void onCancel(ActionEvent e) { dispose(); }
    
    /**
     * @param owner
     * @param appletConfig
     * @param course
     * @return fi.dwo.client.domain.Sco
     */
    public static Sco addSco(Component owner, Course course, AppletConfig appletConfig) {
        ScoNameDialog cnd = new ScoNameDialog(owner, TextMapper
                .getText(TextMapper.GUISDLG_TTL_ADD_SCO), 0, appletConfig.getName(), "", TextMapper.GUISDLG_SCO_NAME, TextMapper.GUISDLG_SCO_DESCRIPTION, true);
        cnd.setShowScore(true);
        cnd.setShowDocent(true);
        JButton logobtn = cnd.addLogoBtn();
        LogoIconAction logoAction = new LogoIconAction();
		logobtn.setAction(logoAction);
        cnd.show();
        if (cnd.isConfirmed()) {
        		
            byte[] imageData = null;
            if(logoAction.isUpdate())
            {
               imageData = logoAction.getImageData();
            } 

			//System.out.println("voor hij wordt aangemaakt: " + appletConfig.getLaunchdata() + "; " + appletConfig.getAppletID());
            Sco s = GuiCreator.instance().addSco(course, appletConfig, cnd.getScoName(),
                    cnd.getScoDescription(), cnd.isShowScore(), cnd.isShowDocent(), imageData);
            if (s == null) { //something went wrong, reshow the dialog
                s = addSco(owner, course, appletConfig);
            }
            //System.out.println("en nu...: " + s.getLaunchdataString());
            s.setCourse(course);
            return s;
        } else { //action canceled
            return null;
        }
    }

    private boolean isConfirmed() {
      return confirmed;
    }

    private JButton addLogoBtn() {
      return logoBtn;
    }

    private void setShowScore(boolean b) {
      showScore.setSelected(b);      
    }
    private void setShowDocent(boolean b) {
    	showDocent.setSelected(b);
    }

    private String getScoName() {
      return nameField.getText();
    }

    private String getScoDescription() {
      return textarea.getText();
    }

    private boolean isShowScore() {
      return showScore.isSelected();
    }
    
    private boolean isShowDocent() {
    	return showDocent.isSelected();
    }

    public static boolean editSco(Sco sco) {
        return editSco(sco, DwoHelper.getApplet());
    }

    /**
     * @param sco
     * @param owner
     * @return boolean
     */
    public static boolean editSco(Sco sco, Component owner) {
        ScoNameDialog cnd = new ScoNameDialog(owner, TextMapper
                .getText(TextMapper.GUISDLG_TTL_EDIT_SCO), sco.getScoID(), sco.getScoName(),
                sco.getDescription(), TextMapper.GUISDLG_SCO_NAME, TextMapper.GUISDLG_SCO_DESCRIPTION, true);
        cnd.setShowScore(sco.isShowScore());
        cnd.setShowDocent(sco.isShowDocent());
        JButton logobtn = cnd.addLogoBtn();
        LogoIconAction logoAction = new LogoIconAction(sco);
		logobtn.setAction(logoAction);
        
        cnd.show();
        if (cnd.isConfirmed()) {
            String oldName = sco.getScoName();
            String oldDescription = sco.getDescription();
            Boolean oldShowScore = sco.getShowScore();
            sco.setName(cnd.getScoName());
            sco.setDescription(cnd.getScoDescription());
// keep null as long as possible. TRUE -> NULL if null
            if (cnd.isShowScore()) {
                if (sco.getShowScore() != null) {
                    sco.setShowScore(Boolean.TRUE);
                }
            } else {
                sco.setShowScore(Boolean.FALSE);
            }
            if (cnd.isShowDocent()) {
                if (sco.getShowDocent() != null) {
                    sco.setShowDocent(Boolean.TRUE);
                }
            } else {
                sco.setShowDocent(Boolean.FALSE);
            }

// pass ImageData to updateSco in persistenceFacade.
            if(logoAction.isUpdate())
            {
            	sco.setImageData(logoAction.getImageData());
            } else
            	sco.setImageData(null);
            
            
            boolean result = GuiCreator.instance().updateSco(sco);
            if (!result) { //something went wrong. Reset the data and reshow the dialog.
                sco.setName(oldName);
                sco.setDescription(oldDescription);
                sco.setShowScore(oldShowScore);
                result = editSco(sco, owner);
            }

            return result;
        } else { //action canceled
            return false;
        }
    }


}
