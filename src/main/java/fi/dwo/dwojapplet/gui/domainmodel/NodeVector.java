package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JComponent;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;

public class NodeVector extends Vector<Object> implements Node {

  private int path;
  
  
  public int getPath() {
    return path;
  }

  public void setPath(int path) {
    this.path = path;
  }

  
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
      nv.setPath(elementCount);
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
      NodeLeaf nodeleaf = new NodeLeaf(subtitle, obj.getInfo(), l);
      nodeleaf.setPath(elementCount);
      add(nodeleaf);
    }
    setSize(last+1);
  }

  public NodeVector(String lang) {
    info = new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>());
    this.lang = lang;
    setDescription("");
    setTitle("");
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
  @Override
  public DomStudentModelContextInfo getInfo() {
    return info;
  }
}
