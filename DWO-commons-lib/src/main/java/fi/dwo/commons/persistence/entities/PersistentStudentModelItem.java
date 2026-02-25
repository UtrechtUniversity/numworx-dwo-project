package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

import fi.dwo.commons.persistence.JpaEclipseConverterDomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

@Entity
@Table(name = "tblstudentmodelitem", schema = "")
@Converter(name = "studentModelObjConverter", converterClass = JpaEclipseConverterDomStudentModelObj.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentModelItem.findByModelID", query = "SELECT p FROM PersistentStudentModelItem p WHERE p.modelID = :modelID")})
public class PersistentStudentModelItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "itemID", nullable = false)
  private Long itemID;
  @Column(name = "modelID", nullable = false)
  private Long modelID;
  @Column(name = "schoolID", nullable = false)
  private Long schoolID;
  @NotNull
  @Column(name = "item", nullable = false, length = 16777215, columnDefinition="JSON")
  @Convert("studentModelObjConverter")
  @Lob
  private DomStudentModelObj item;
  @Column(name = "optlock")
  @Version
  private Long optlock;
  @Column(name = "lastChangeTimeStamp")
  private long lastChangeTimeStamp;
  @Column(name = "id", nullable = false, length = 50)
  @Size(max = 50)
  @NotNull
  private String id;
  @NotNull
  @Column(name = "del", nullable = false)
  private DelState delState = DelState.not;
  /**
   * @return the itemID
   */
  public Long getItemID() {
    return itemID;
  }
  /**
   * @param itemID the itemID to set
   */
  public void setItemID(Long itemID) {
    this.itemID = itemID;
  }
  /**
   * @return the modelID
   */
  public Long getModelID() {
    return modelID;
  }
  /**
   * @param modelID the modelID to set
   */
  public void setModelID(Long modelID) {
    this.modelID = modelID;
  }
  /**
   * @return the schoolID
   */
  public Long getSchoolID() {
    return schoolID;
  }
  /**
   * @param schoolID the schoolID to set
   */
  public void setSchoolID(Long schoolID) {
    this.schoolID = schoolID;
  }
  /**
   * @return the item
   */
  public DomStudentModelObj getItem() {
    return item;
  }
  /**
   * @param item the item to set
   */
  public void setItem(DomStudentModelObj item) {
    this.item = item;
  }
  /**
   * @return the optlock
   */
  public Long getOptlock() {
    return optlock;
  }
  /**
   * @param optlock the optlock to set
   */
  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }
  /**
   * @return the lastChangeTimeStamp
   */
  public long getLastChangeTimeStamp() {
    return lastChangeTimeStamp;
  }
  /**
   * @param lastChangeTimeStamp the lastChangeTimeStamp to set
   */
  public void setLastChangeTimeStamp(long lastChangeTimeStamp) {
    this.lastChangeTimeStamp = lastChangeTimeStamp;
  }
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  
}
