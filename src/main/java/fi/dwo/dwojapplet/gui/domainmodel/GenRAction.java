package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;

import javax.swing.JTree;

class GenRAction extends MethodeAction {

  GenRAction() {
    super("Getal&Ruimte");
    KOPPELING_LEERDOEL = "Koppeling leerdoel aan Getal&Ruimte";
    grJaarlagen       = new String[] { "1HV", "1V", "2HV", "2V", "3H", "3V"};
    aantalHoofdstukken= new int[]    {  10,    10,   10,    10,   10,   10};
  }

  GenRAction(boolean b) {
    this();
    readonly = b;
  }   

  GenRAction(Component owner, JTree tree) {
    this(); setOwner(owner); setTree(tree);
  }

  GenRAction(boolean b, Component owner, JTree tree) {
    this(b); setOwner(owner); setTree(tree);
  }
}