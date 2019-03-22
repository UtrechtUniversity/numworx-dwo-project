package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.List;
import java.util.Vector;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;

public class NodeVector extends Vector<Object> implements Node {

  public String title, lang;
  public DomStudentModelContextInfo info;
  public NodeVector(List<DomStudentModelCategory> categories, DomStudentModelContextInfo info, String l) {
    String title = info.getTitle().get(l);
    this.title = title;
    this.info = info;
    this.lang = l;
    int last = -1;
    for (DomStudentModelCategory cat: categories) {    
      String subtitle = cat.getInfo().getTitle().get(l);
      if (!subtitle.isEmpty()) last = size();
      NodeVector nv = new NodeVector(cat.getObjectives(), subtitle, l, cat.getInfo());
      add(nv);
    }
    setSize(last+1);
  }
  public NodeVector(List<DomStudentModelObj> objectives, String title, String l, DomStudentModelContextInfo leaf) {
    this.title = title;
    this.info = leaf;
    this.lang = l;
    int last = -1;
    for (DomStudentModelObj obj: objectives) {
      String subtitle = obj.getInfo().getTitle().get(l);
      if (!subtitle.isEmpty()) last = size();
      add(new NodeLeaf(subtitle, obj.getInfo(), l));
    }
    setSize(last+1);
  }

  public String toString() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
    info.getTitle().put(lang, title);
  }
  @Override
  public void setLanguage(String lang) {
    
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
  @Override
  public DomStudentModelContextInfo getInfo() {
    return info;
  }
}
