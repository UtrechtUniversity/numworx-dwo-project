package fi.beans.numworxlf;

import java.text.Format;

public class JFormattedTextField extends javax.swing.JFormattedTextField {

	public JFormattedTextField() {
	}

	public JFormattedTextField(Object value) {
		super(value);
	}

	public JFormattedTextField(Format format) {
		super(format);
	}

	public JFormattedTextField(AbstractFormatter formatter) {
		super(formatter);
	}

	public JFormattedTextField(AbstractFormatterFactory factory) {
		super(factory);
	}

	public JFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
		super(factory, currentValue);
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
