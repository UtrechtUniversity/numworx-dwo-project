package fi.beans.numworxlf;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

public class NumworxTextFieldUI extends BasicTextFieldUI implements Constants {

  static class NumworxPassword extends BasicPasswordFieldUI {

    /* (non-Javadoc)
     * @see fi.beans.numworxlf.NumworxTextFieldUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
      super.installDefaults();
      JTextComponent editor = getComponent();
      Border b = editor.getBorder();
      if (b == null || b instanceof UIResource) 
      {
        getComponent().setBorder(createDefaultBorder());
        getComponent().repaint();
      }
      Color c = editor.getForeground();
      if (c instanceof UIResource) editor.setForeground(colorBlue1);

    }
    
  }
  
  
  public static TextUI createUI(javax.swing.JTextField c) {
    if (c instanceof JPasswordField) {
      return new NumworxPassword();
    }
    return new NumworxTextFieldUI();
}

  private static BorderUIResource createDefaultBorder() {
	return new BorderUIResource(
			BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(colorBlue3),
			BorderFactory.createEmptyBorder(2,2,2,2)
			));
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
      editor.setBorder(createDefaultBorder());
    Color c = editor.getForeground();
    if (c instanceof UIResource) editor.setForeground(colorBlue1);
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
