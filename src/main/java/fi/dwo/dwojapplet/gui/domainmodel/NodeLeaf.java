package fi.dwo.dwojapplet.gui.domainmodel;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

class NodeLeaf implements Node {

  
  private String title;
  private final DomStudentModelContextInfo info;
  /**
   * @return the info
   */
  public DomStudentModelContextInfo getInfo() {
    return info;
  }

  private String lang;

  NodeLeaf(String title, DomStudentModelContextInfo info, String l) {
    this.title = title;
    this.info = info;
    this.lang = l;
  }

  public String toString() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title  = title;
    info.getTitle().put(lang, title);
  }

  @Override
  public void setLanguage(String lang) {
    this.lang = lang;
  }

  @Override
  public String getLanguage() {
    return lang;
  }

  @Override
  public String getDescription() {
    return info.getDescription().get(lang);
  }

  @Override
  public void setDescription(String description) {
    info.getDescription().put(lang, description);
  }
  
  
}
