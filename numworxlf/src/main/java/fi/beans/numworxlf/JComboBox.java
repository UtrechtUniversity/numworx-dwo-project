package fi.beans.numworxlf;

import java.util.Vector;

import javax.swing.ComboBoxModel;

public class JComboBox<E> extends javax.swing.JComboBox<E> {

  public JComboBox() {
    // TODO Auto-generated constructor stub
  }

  public JComboBox(ComboBoxModel<E> aModel) {
    super(aModel);
    // TODO Auto-generated constructor stub
  }

  public JComboBox(E[] items) {
    super(items);
    // TODO Auto-generated constructor stub
  }

  public JComboBox(Vector<E> items) {
    super(items);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see javax.swing.JComboBox#updateUI()
   */
  @Override
  public void updateUI() {
    setUI(new NumworxComboBoxUI());
  }

}
