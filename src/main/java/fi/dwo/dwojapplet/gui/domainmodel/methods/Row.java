package fi.dwo.dwojapplet.gui.domainmodel.methods;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class Row {
  public PersistenceId id;
  public Long optLock;
  public String method;
  public String[] books;
  public String[][] chapters;
  
  public String key() {
    if (id == null) return null;
    String[] split = id.getIdString().split(";", 3);
    return split[2];
  }
}