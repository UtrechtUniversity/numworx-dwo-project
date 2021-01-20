package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;

import javax.swing.JTree;

class GenRAction extends MethodeAction {

  GenRAction() {
    super("Getal&Ruimte");
    KOPPELING_LEERDOEL = "Koppeling leerdoel aan Getal&Ruimte";
    grJaarlagen       = new String[] { "1HV", "2HV", "3V"};
    aantalHoofdstukken= new int[]    {  9,       8,    9 };
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