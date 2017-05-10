/*Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * JPA/EclipseLink entity for the Courses.
 *
 * <p>
 * A set of Courses that contains a set of multi-tree of learning modules. Each
 * multi-tree is a one-directional graph without any cycles or references to
 * another tree in the Courses set. Each course entity describes a tree-node,
 * best described by the tuple of:
 * <p>
 * &lt; courseId, parentId, schoolId, dwoProfileId, hasChildren, courseData &gt;
 * <p>
 * <b>courseId</b> : Unique, non-negative and not null<br>
 * <b>parentId</b> : 0 if no parent exists.<br>
 * <b>schoolId</b> : The schoolId of the school that owns it and may execute
 * CRUD operations on it, NULL if it is visible to all schools.<br>
 * <b>dwoProfileId</b>: The dwoProfileId in which a school must be to access and
 * see the course.
 * <p>
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblcourse", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "schoolID", "dwoProfileID", "parentID"})})
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "PersistentCourse.findBySchoolAndProfileID", query = "SELECT p FROM PersistentCourse p WHERE p.parentID = 0 AND p.dwoProfileID = :dwoProfileID AND p.schoolID = :schoolID"),
	@NamedQuery(name = "PersistentCourse.findByNullSchoolAndProfileID", query = "SELECT p FROM PersistentCourse p WHERE p.parentID = 0 AND p.dwoProfileID = :dwoProfileID AND p.schoolID IS NULL"),
    @NamedQuery(name = "PersistentCourse.findAll", query = "SELECT p FROM PersistentCourse p"),
    @NamedQuery(name = "PersistentCourse.findByCourseID", query = "SELECT p FROM PersistentCourse p WHERE p.courseID = :courseID"),
    @NamedQuery(name = "PersistentCourse.findBySchoolID", query = "SELECT p FROM PersistentCourse p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentCourse.findByName", query = "SELECT p FROM PersistentCourse p WHERE p.name = :name"),
    @NamedQuery(name = "PersistentCourse.findByImage", query = "SELECT p FROM PersistentCourse p WHERE p.image = :image"),
    @NamedQuery(name = "PersistentCourse.findByDwoProfileID", query = "SELECT p FROM PersistentCourse p WHERE p.dwoProfileID = :dwoProfileID"),
    @NamedQuery(name = "PersistentCourse.findByExport", query = "SELECT p FROM PersistentCourse p WHERE p.export = :export"),
    @NamedQuery(name = "PersistentCourse.findByWithChildren", query = "SELECT p FROM PersistentCourse p WHERE p.withChildren = :withChildren"),
    @NamedQuery(name = "PersistentCourse.findByParentIDAndProfileID", query = "SELECT p FROM PersistentCourse p WHERE p.dwoProfileID = :dwoProfileID AND p.parentID = :parentID"),
    @NamedQuery(name = "PersistentCourse.findByParentID", query = "SELECT p FROM PersistentCourse p WHERE p.parentID = :parentID")})
//    @NamedQuery(name = "PersistentCourse.findByNotVisible", query = "SELECT p FROM PersistentCourse p WHERE p.notVisible = :notVisible")})
public class PersistentCourse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "courseID", nullable = false)
    private Long courseID;
    @Column(name = "schoolID")
    private Long schoolID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "name", nullable = false, length = 40)
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 16777215)
    @Column(name = "description", nullable = false, length = 16777215)
    private String description;
    @Size(max = 128)
    @Column(name = "image", length = 128)
    private String image;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dwoProfileID", nullable = false)
    private Long dwoProfileID;
    @Lob
    @Column(name = "imageData")
    private byte[] imageData;
    @Column(name = "export")
    private Boolean export;
    @Column(name = "withChildren")
    private Boolean withChildren;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parentID", nullable = false) //0 means no parent.
    private Long parentID;

  /**
   * show list of children (sco/course): false = do, true = don't 
   */
    @Basic(optional = false)
    @NotNull
    @Column(name = "notVisible", nullable = false)
    private boolean notVisible; 

    @Basic(optional = false)
    @NotNull
    //update tblcourse set sequenceNr=0 where sequenceNr is null
    //alter table tblcourse alter column sequenceNr set DEFAULT '0';
    //
//UPDATE tblcourse as c INNER JOIN tblcoursesequence as s on (c.courseID=s.courseID
//and c.schoolID=s.schoolID and c.parentID=s.parent and c.dwoProfileID=s.profileID)
//SET c.sequencenr = s.sequencenr    
    @Column(name = "sequencenr", nullable = false)
    private Long sequenceNr;
    @Size(max = 250)
    @Column(name = "treeIndex", length = 250)
    private String treePath;
    @Basic(optional = false)
    @Column(name = "lastChangeTimeStamp", nullable = true)
    private Long lastChangeTimeStamp;

//    For future use in case of optimistic locking.    
//    @Version
//    @Column(name = "optlock", columnDefinition = "integer DEFAULT 0", nullable = false)
//    private long version = 0L;
    public PersistentCourse() {
    }

    public PersistentCourse(Long courseID) {
        this.courseID = courseID;
    }

//    public PersistentCourse(Long courseID, String name, String description, int dwoProfileID, int parentID, short notVisible) {
//        this.courseID = courseID;
//        this.name = name;
//        this.description = description;
//        this.dwoProfileID = dwoProfileID;
//        this.parentID = parentID;
//        this.notVisible = notVisible;
//    }
    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public Long getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(Long schoolID) {
        this.schoolID = schoolID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getDwoProfileID() {
        return dwoProfileID;
    }

    public void setDwoProfileID(Long dwoProfileID) {
        this.dwoProfileID = dwoProfileID;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Boolean isWithChildren() {
        return withChildren;
    }

    public void setWithChildren(Boolean withChildren) {
        this.withChildren = withChildren;
    }

    public long getParentID() {
        return parentID;
    }

    public void setParentID(long parentID) {
        this.parentID = parentID;
    }
//
//    public short getNotVisible() {
//        return notVisible;
//    }
//
//    public void setNotVisible(short notVisible) {
//        this.notVisible = notVisible;
//    }
//

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseID != null ? courseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentCourse)) {
            return false;
        }
        PersistentCourse other = (PersistentCourse) object;
        if ((this.courseID == null && other.courseID != null) || (this.courseID != null && !this.courseID.equals(other.courseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentCourse[ courseID=" + courseID + " ]";
    }

    /**
     * @return the sequenceNr
     */
    public Long getSequencenr() {
        return sequenceNr;
    }

    /**
     * @param sequencenr the sequenceNr to set
     */
    public void setSequencenr(Long sequencenr) {
        this.sequenceNr = sequencenr;
    }

    /**
     * @return the treePath
     */
    public String getTreeIndex() {
        return treePath;
    }

    /**
     * @param treeIndex the treePath to set
     */
    public void setTreeIndex(String treeIndex) {
        this.treePath = treeIndex;
    }

    /**
     * @return the lastChangeTimeStamp
     */
    public Long getLastChangeTimeStamp() {
        return lastChangeTimeStamp;
    }

    /**
     * @param lastChangeTimeStamp the lastChangeTimeStamp to set
     */
    public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
        this.lastChangeTimeStamp = lastChangeTimeStamp;
    }

    public DomCourse buildDomCourse() {
        DomCourse course = new DomCourse();
        fillDomCourse(course);
        return course;
    }

    private void fillDomCourse(DomCourse course) {
        course.setId(buildPersistenceId());
        course.setName(name);
        if (this.schoolID != null) {
            course.setSchoolId(PersistentSchool.buildPersistenceId(this.schoolID));
        }
        //note that parent 0 means there is no parent!
        if (this.parentID != 0) {
            course.setParentID(buildPersistenceId(this.parentID));
        } else {
            course.setParentID(null);
        }
        course.setSequenceNr(sequenceNr);
        course.setTreeIndex(treePath);
        course.setWithChildren(withChildren);
//        course.setNotVisible(notVisible);
        course.setLastChangeTimeStamp(lastChangeTimeStamp);
    }
//
//    public DomCourseStudent buildDomCourseStudent() {
//        DomCourseFull course = new DomCourseFull();
//        fillDomCourseFull(course);
//        return course;
//    }
//
//    private void fillDomCourseStudent(DomCourseStudent course) {
//        fillDomCourse(course);
//        course.setImage(image);
//        course.setImageData(imageData);
//		  course.setNotVisible(notVisible);
//        course.setDescription(description);
//    }

    public DomCourseFull buildDomCourseFull() {
        DomCourseFull course = new DomCourseFull();
        fillDomCourseFull(course);
        return course;
    }

    private void fillDomCourseStudent(DomCourseStudent course) {
    	fillDomCourse(course);
    	course.setImage(image);
    	course.setImageData(imageData);
    	course.setDescription(description);
    	course.setId(buildPersistenceId());
    	course.setNotVisible(notVisible);
    }
    
    public DomCourseStudent buildDomCourseStudent() {
    	DomCourseStudent course = new DomCourseStudent();
    	fillDomCourseStudent(course);
    	return course;
    }
    
    private void fillDomCourseFull(DomCourseFull course) {
        fillDomCourseStudent(course);
        course.setExport(export);
        course.setDwoProfileId(buildDwoProfileId());
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(courseID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aCourseId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aCourseId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentCourse.name(), aCourseId));
        return id;
    }
    
    private PersistenceId buildDwoProfileId() {
        return PersistentDwoProfile.buildPersistenceId(dwoProfileID);
    	
    }

	public boolean isNotVisible() {
		return notVisible;
	}

	public void setNotVisible(boolean notVisible) {
		this.notVisible = notVisible;
	}
    
}
