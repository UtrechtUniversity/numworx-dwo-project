package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelStructure objective score node.
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelObjectiveScore extends DomStudentModelScore<DomStudentModelObjectiveScore> {

   @Override
  public List<DomStudentModelObjectiveScore> getChildren() {
    return super.getChildren();
  }

  @Override
  public void setChildren(List<DomStudentModelObjectiveScore> children) {
    super.setChildren(children);
  }
    
}
