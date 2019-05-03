package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.TreeMap;
import java.util.UUID;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

class NodeLeaf implements Node {

  
  private int path;
  
  
  public int getPath() {
    return path;
  }

  public void setPath(int path) {
    this.path = path;
  }

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
    if (info.getId() == null) {
      info.setId(UUID.randomUUID().toString());
    }
  }

  public NodeLeaf(String string) {
    this.lang = string;
    info = new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>());
    info.setId(UUID.randomUUID().toString());
    setTitle("");
    setDescription("");
// FIXME INITIAL VALUES INIT,LEARN,SLIP
    info.setInit(0.5);
    info.setSlip(0.05);
    info.setLearn(0.20);
  }

  public NodeLeaf(NodeLeaf u) {
    lang = u.lang;
    title = u.title;
    info = new DomStudentModelContextInfo(new TreeMap<>(u.info.getTitle()), new TreeMap<>(u.info.getDescription()));
    info.setId(UUID.randomUUID().toString());
    info.setInit(u.info.getInit());
    info.setSlip(u.info.getSlip());
    info.setLearn(u.info.getLearn());
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
    if (!lang.equals(this.lang)) {
      String nt = info.getTitle().getOrDefault(lang, "");
      if (!nt.isEmpty()) title = nt;
      String od = getDescription();
      this.lang = lang;
      String nd = getDescription();
      if (nd == null || nd.isEmpty()) setDescription(od);
    }
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
