package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;

import javax.swing.JTree;

import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import fi.dwo.dwojapplet.gui.domainmodel.methods.Row;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class AnyMethodAction extends MethodeAction {
  
  
  AnyMethodAction() {
    super("");
  }
  AnyMethodAction(boolean b) {
    this();
    readonly = b;
  }   

  AnyMethodAction(Component owner, JTree tree) {
    this(); setOwner(owner); setTree(tree);
  }

  AnyMethodAction(boolean b, Component owner, JTree tree) {
    this(b); setOwner(owner); setTree(tree);
  }


  void setMethode(Row methode) {
    putValue(NAME, methode.method);
    putValue(KEY,  methode.key());
    KOPPELING_LEERDOEL = "Koppeling leerdoel aan " + methode.method;
    grJaarlagen       = methode.books;
    aantalHoofdstukken= new int[grJaarlagen.length];
    for (int i = 0; i < aantalHoofdstukken.length; i++) {
      aantalHoofdstukken[i] = methode.chapters[i].length;
    }
  }

  void setMethode(PersistenceId active) {
    setMethode(MethodsProperties.instance().getMethod(active));
  }
  
  // hulpje voor constructor;
  AnyMethodAction init(int i) {
    setMethode(MethodsProperties.instance().get(i));
    return this;
  }

  
}
