package fi.dwo.dwojapplet.gui.numworx;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class NumworxTextFieldUI extends BasicTextFieldUI implements Constants {

  static class NumworxPassword extends BasicPasswordFieldUI {

    /* (non-Javadoc)
     * @see fi.dwo.dwojapplet.gui.numworx.NumworxTextFieldUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
      super.installDefaults();
      Border b = getComponent().getBorder();
      if (b == null || b instanceof UIResource) 
      {
        getComponent().setBorder(new BorderUIResource(BorderFactory.createLineBorder(colorBlue3)));
        getComponent().repaint();
      }
    }
    
  }
  
  
  public static TextUI createUI(javax.swing.JTextField c) {
    if (c instanceof JPasswordField) {
      return new NumworxPassword();
    }
    return new BasicTextFieldUI();
}

  private javax.swing.JTextField editor;
  
  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicTextUI#installDefaults()
   */
  @Override
  protected void installDefaults() {
    
    super.installDefaults();
    Border b = editor.getBorder();
    if (b == null || b instanceof UIResource) 
      editor.setBorder(new BorderUIResource(BorderFactory.createLineBorder(colorBlue3)));
  }

  /* (non-Javadoc)
   * @see javax.swing.plaf.basic.BasicTextUI#installUI(javax.swing.JComponent)
   */
  @Override
  public void installUI(JComponent c) {
    editor = (javax.swing.JTextField) c;
    super.installUI(c);
  }

}
