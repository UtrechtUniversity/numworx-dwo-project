package nl.uu.fi.dwo.rest.dom.entities;

public class DomAppletFull extends DomAppletId {
  private String appletName, classname, features, jarname;

  public String getAppletName() {
    return appletName;
  }

  public void setAppletName(String appletName) {
    this.appletName = appletName;
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public String getFeatures() {
    return features;
  }

  public void setFeatures(String features) {
    this.features = features;
  }

  public String getJarname() {
    return jarname;
  }

  public void setJarname(String jarname) {
    this.jarname = jarname;
  }
}
