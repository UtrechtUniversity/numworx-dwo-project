package fi.dwo.dwojapplet.gui.numworx;

import javax.swing.text.Document;

public class JTextField extends javax.swing.JTextField {

  public JTextField() {
    // TODO Auto-generated constructor stub
  }

  public JTextField(String text) {
    super(text);
    // TODO Auto-generated constructor stub
  }

  public JTextField(int columns) {
    super(columns);
    // TODO Auto-generated constructor stub
  }

  public JTextField(String text, int columns) {
    super(text, columns);
    // TODO Auto-generated constructor stub
  }

  public JTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see javax.swing.text.JTextComponent#updateUI()
   */
  @Override
  public void updateUI() {
    setUI(new NumworxTextFieldUI());
    invalidate();
  }

}
