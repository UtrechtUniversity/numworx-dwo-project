package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;

class MWAction extends MethodeAction {
  MWAction() {
    super("Moderne Wiskunde");
    KOPPELING_LEERDOEL = "Koppeling leerdoel aan Moderne Wiskunde";
    grJaarlagen       = new String[] { "1HV",  "2HV",  "3V"};
    aantalHoofdstukken= new int[]    {  13,    12,      14 };   
  }
  MWAction(boolean b) {
    this();
    readonly = b;
  }
  MWAction(Component owner, JTree tree) {
    this(); setOwner(owner); setTree(tree);
  }
  MWAction(boolean b, Component owner, JTree tree) {
    this(b); setOwner(owner); setTree(tree);
  }
  
}