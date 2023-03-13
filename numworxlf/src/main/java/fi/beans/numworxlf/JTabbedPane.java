package fi.beans.numworxlf;

public class JTabbedPane extends javax.swing.JTabbedPane {

  public JTabbedPane() {
  }

  public JTabbedPane(int tabPlacement) {
    super(tabPlacement);
  }

  public JTabbedPane(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
  }

  @Override
  public void updateUI() {
    setUI(NumworxTabbedPaneUI.createUI(this));
  }

}
