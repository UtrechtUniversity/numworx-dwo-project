package fi.beans.numworxlf;

import javax.swing.text.Document;

@SuppressWarnings("serial")
public class JTextField extends javax.swing.JTextField {

  public JTextField() {    // TODO Auto-generated constructor stub
  }

  public JTextField(String text) {
    super(text);
  }

  public JTextField(int columns) {
    super(columns);
  }

  public JTextField(String text, int columns) {
    super(text, columns);
  }

  public JTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
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
