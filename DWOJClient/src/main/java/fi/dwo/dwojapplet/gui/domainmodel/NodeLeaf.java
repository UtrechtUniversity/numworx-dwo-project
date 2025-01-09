package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;

public class NodeLeaf implements Node {

  final boolean copy;
  private int path;
  private Map<String, Map<String,Set<Integer>>> methode;

  private DomStudentModelVariant variant;
  
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
    if (copy) return new DomStudentModelContextInfo(info);
    return info;
  }

  private String lang;

  NodeLeaf(String title, DomStudentModelContextInfo org, String l) {
    copy = true;
    this.info = new DomStudentModelContextInfo(org);
    this.lang = l;
    if (this.info.getId() == null) {
      this.info.setId(UUID.randomUUID().toString());
    }

    if (this.info.getMethods() == null) {
      methode = new TreeMap<>();
      this.info.setMethods(methode);
    } else {
      methode = this.info.getMethods();
    }
    setTitle(title);
    setDefaultVariant();
  }

  public NodeLeaf(String string) {
    copy = true;
    this.lang = string;
    info = new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>());
    info.setId(UUID.randomUUID().toString());
    setTitle("");
    setDescription("");
// FIXME INITIAL VALUES INIT,LEARN,SLIP
    info.setInit(0.5);
    info.setSlip(0.05);
    info.setLearn(0.20);
    methode = new TreeMap<>();
    info.setMethods(methode);
  }

  public NodeLeaf(NodeLeaf u) {
    copy = true;
    lang = u.lang;
    title = u.title;
    info = new DomStudentModelContextInfo(u.info);
    info.setId(UUID.randomUUID().toString());
    info.setInit(u.info.getInit());
    info.setSlip(u.info.getSlip());
    info.setLearn(u.info.getLearn());
    info.setNodeSize(u.info.getNodeSize());
    if (info.getMethods() == null) {
      methode = new TreeMap<>();
      info.setMethods(methode);
    } else {
      methode = info.getMethods();
    }
    setDefaultVariant();
  }

  public NodeLeaf(String subtitle1, DomStudentModelContextInfo info2, String locale, boolean b) {
    copy = b;
    this.info = (info2);
    this.lang = locale;
    if (this.info.getId() == null) {
      this.info.setId(UUID.randomUUID().toString());
    }
    
    if (this.info.getMethods() == null) {
      methode = new TreeMap<>();
      this.info.setMethods(methode);
    } else {
      methode = this.info.getMethods();
    }
    setTitle(subtitle1);
    setDefaultVariant();
  }

  protected void setDefaultVariant() {
	List<DomStudentModelVariant> variants = this.info.getVariants();
	if (variants != null && !variants.isEmpty()) {
    	variant=(variants.get(0));
    }
  }

  public String toString() {
    return Objects.toString(title);
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

  @Override
  public void setDescriptionAsJSON(String description) {
    if (description == null) 
      info.getDescription().remove(json());
    else
      info.getDescription().put(json(), description);
  }

  String json() {
    return lang +"@JSON";
  }

  
  private boolean value;
  public void setValue(boolean selected) {
    this.value = selected;
  }

  @Override
  public boolean isValue() {
    return value;
  }

  public Map<String, Map<String,Set<Integer>>> getMethode() {
    return methode;
  }

  public List<String> getVoorkennis() {
    return info.getVoorkennis();
  }

  public void setVoorkennis(List<String> list) {
    info.setVoorkennis(list);
    
  }

  public void setInit(Double value) {
    info.setInit(value);
  }

  public void setLearn(Double value) {
    info.setLearn(value);    
  }

  public void setSlip(Double value) {
    info.setSlip(value);
  }
  
  public void setNodeSize(Integer value) {
    info.setNodeSize(value);
  }
  
  public String getId() {
    return info.getId();
  }
  
  public Integer getX() {
    return info.getX();
  }
  
  public void setX(Integer x) {
    info.setX(x);
  }
  
  public Integer getY() {
    return info.getY();
  }

  public void setY(Integer y) {
    info.setY(y);
  }

  public List<DomStudentModelMethodInfo> getMethodeInfos() {
    return info.getMethodInfo();
  }

  public void setMethodeInfos(Collection<DomStudentModelMethodInfo> methodeInfos) {
    if (methodeInfos == null) info.setMethodInfo(null);
    else info.setMethodInfo(new ArrayList<>(methodeInfos));  
  }
  
  public WrappedSet getVariants() {
	  if (info.getVariants() == null) {
		  info.setVariants(new ArrayList<>());
	  }
	  return new WrappedSet(info.getVariants());
  }

public DomStudentModelVariant getVariant() {
	return variant;
}

public void setVariant(DomStudentModelVariant variant) {
	this.variant = variant;
	if (variant != null) {
		getVariants().replace(variant);
	}
}

public void setVariant(String string) {
	if (info.getVariants() == null) return;
	Optional<DomStudentModelVariant> opt = info.getVariants().stream().filter(v -> Objects.equals(v.getName(), string)).findAny();
	setVariant(opt.orElse(info.getVariants().get(0)));
}
  
  
}
