package fi.beans.numworxlf;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;

public class OptionPaneFrame {

  public static void main(String[] args) {
    
    Object message = new JButton("DIT IS EEN TEST");
    //JOptionPane.showMessageDialog(null, message);
    
    JOptionPane.showConfirmDialog(null, message, "TITEL", JOptionPane.YES_NO_CANCEL_OPTION);

  }

}
