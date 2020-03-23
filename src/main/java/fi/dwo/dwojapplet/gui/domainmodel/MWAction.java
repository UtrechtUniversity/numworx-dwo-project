package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;

import javax.swing.JTree;

class MWAction extends MethodeAction {
  MWAction() {
    super("Moderne Wiskunde");
    KOPPELING_LEERDOEL = "Koppeling leerdoel aan Moderne Wiskunde";
    grJaarlagen       = new String[] { "1Vb", "1Vkgt", "1VgtH", "1HV", "1V", "2HV", "2V", "3H", "3V"};
    aantalHoofdstukken= new int[]    {  12,    12,      12,      12,    10,   10,    10,   10,   10};   
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