package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Lamba {

  void ja(ActionEvent ev) { System.out.println("ja"); }
  
  final ActionListener twee = this::ja;
 
  public static void main(String[] args) {
    Lamba aap = new Lamba();
    ActionListener een = aap::ja;
    System.out.println( een == aap.twee);
    System.out.println( een );
    System.out.println( aap.twee);
  }

}
