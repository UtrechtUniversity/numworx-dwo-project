package fi.dwo.dwojapplet.gui.domainmodel.methods;

import nl.uu.fi.dwo.rest.dom.entities.DomId;

public class Row extends DomId {
  public String method;
  public String[] books;
  public String[][] chapters;
  
  public String key() {
    if (getId() == null) return null;
    String[] split = getId().getIdString().split(";", 3);
    return split[2];
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}