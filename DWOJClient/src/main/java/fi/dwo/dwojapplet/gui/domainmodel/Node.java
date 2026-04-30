package fi.dwo.dwojapplet.gui.domainmodel;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

public interface Node {
  public String toString();
  public void setTitle(String title);
  public void setLanguage(String lang);
  public String getLanguage();
  
  public String getDescription();
  public void setDescription(String description);
  public void setDescriptionAsJSON(String description);
  
  public DomStudentModelContextInfo getInfo();
  
  public int getPath();
  public void setPath(int path);

  public boolean isValue();
  
}
