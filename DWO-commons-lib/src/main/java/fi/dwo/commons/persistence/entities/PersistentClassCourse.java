/* Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * JPA/EclipseLink entity for the ClassCourses.
 * <p>
 * ClassCourses. Each classCourse links a class to a tree or sub tree in the
 * Courses data set.
 * <p>
 * &lt; courseId, classId, type, notBefore, notAfter &gt;
 * <p>
 * <b>classCourseId</b> : Unique, non-negative and not null<br>
 * <b>courseId</b> : Unique, non-negative and not null.<br>
 * <b>classId</b> : Unique, non-negative and not null.<br>
 * <b>type</b> : Declares under which conditions a school may access it.<br>
 * <b>notBefore</b> : The referenced course module tree is not visible before
 * notBefore. <br>
 * <b>notAfter</b> : The referenced course module tree is not visible after
 * notAfter.<br>
 * <p>
 * @author G.A.J. van der Plas
 */
@Entity
@SqlResultSetMapping(
        name = "CourseInClassMapping",
        entities = {
            @EntityResult(entityClass = PersistentCourse.class, // note the @FieldResult's are not required in case the mapping is straightforward.
                    fields = {
                    @FieldResult(name = "courseID", column = "courseID"),
                    @FieldResult(name = "schoolID", column = "schoolID"),
                    @FieldResult(name = "name", column = "name"),
                    @FieldResult(name = "description", column = "description"),
                    @FieldResult(name = "image", column = "image"),
                    @FieldResult(name = "dwoProfileID", column = "dwoProfileID"),
                    @FieldResult(name = "imageData", column = "imageData"),
                    @FieldResult(name = "export", column = "export"),
                    @FieldResult(name = "withChildren", column = "withChildren"),
                    @FieldResult(name = "parentID", column = "parentID")
                    }),
            @EntityResult(entityClass = PersistentClassCourse.class, 
                    fields = {
                        @FieldResult(name = "ClassCourseID", column = "ClassCourseID"),
                        @FieldResult(name = "ClassID", column = "ClassID"),
                        @FieldResult(name = "type", column = "type"),
                        @FieldResult(name = "viewState", column = "viewState"),
                        @FieldResult(name = "notBefore", column = "notBefore"),
                        @FieldResult(name = "notAfter", column = "notAfter"),
                        @FieldResult(name = "courseID", column = "CourseID")
                    })
        })
@Table(name = "tblclasscourse", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ClassID", "CourseID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentClassCourse.findAll", query = "SELECT p FROM PersistentClassCourse p"),
    @NamedQuery(name = "PersistentClassCourse.findByClassCourseID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classCourseID = :classCourseID"),
    @NamedQuery(name = "PersistentClassCourse.findByClassID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentClassCourse.findVisibleByClassID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classID = :classID and p.viewState = :viewState"),
    @NamedQuery(name = "PersistentClassCourse.findVisibleByProfileInClass", query = "SELECT p FROM PersistentClassCourse p WHERE p.classID = :classID and p.viewState = :viewState and (p.dwoProfileID = :dwoProfileID or p.dwoProfileID = null)"),
    @NamedQuery(name = "PersistentClassCourse.findByClassIDAndCourseID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classID = :classID and p.courseID = :courseID"),
    @NamedQuery(name = "PersistentClassCourse.findByType", query = "SELECT p FROM PersistentClassCourse p WHERE p.type = :type"),
    @NamedQuery(name = "PersistentClassCourse.findByNotBefore", query = "SELECT p FROM PersistentClassCourse p WHERE p.notBefore = :notBefore"),
    @NamedQuery(name = "PersistentClassCourse.findByNotAfter", query = "SELECT p FROM PersistentClassCourse p WHERE p.notAfter = :notAfter"),
    @NamedQuery(name = "PersistentClassCourse.findByCourseID", query = "SELECT p FROM PersistentClassCourse p WHERE p.courseID = :courseID")})
public class PersistentClassCourse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ClassCourseID", nullable = false)
    private Long classCourseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ClassID", nullable = false)
    private long classID;
    @Column(name = "type") //enum afschermd, normaal, todo beveiligd (safeexamebrowser), chrome
    private Integer type;
    @Column(name = "viewState") //enum afschermd, normaal, todo beveiligd (safeexamebrowser), chrome
    private ViewState viewState;
    @Column(name = "notBefore")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notBefore;
    @Column(name = "notAfter")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notAfter;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CourseID", nullable = false)
    private long courseID;
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinTable(name = "tblcourse", joinColumns=@JoinColumn(name="CourseID"),
//    inverseJoinColumns=@JoinColumn(name="CourseID"))
//    PersistentCourse course;
//
//    public PersistentCourse getCourse() {
//        return course;
//    }
//    
//    public void setCourse(PersistentCourse aCourse) {
//        course = aCourse;
//    }

    /**
     * @since 1.5.0
     */
    @Column(name = "accessKey")
    private String accessKey;
    @Column(name = "optlock")
    @Version int optlock;
    @Column(name = "lastChangeTimeStamp")
    long lastChangeTimeStamp;
    /**
     * @Since 1.5.5
     */
    @Column(name = "dwoProfileID")
    private Long dwoProfileID;
    
    private String syExamID;

    /**
     * @Since 1.5.7
     */
    @Column(name = "results")
    private Boolean results;
    
    @PrePersist
    @PreUpdate
    void changeTimestamp() {
    	lastChangeTimeStamp = System.currentTimeMillis();
    }
    
    public PersistentClassCourse() {
    }

    public PersistentClassCourse(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public PersistentClassCourse(long classCourseID, long classID, long courseID) {
        this.classCourseID = classCourseID;
        this.classID = classID;
        this.courseID = courseID;
    }

    public long getClassCourseID() {
        return classCourseID;
    }

    public void setClassCourseID(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public long getClassID() {
        return classID;
    }

    public void setClassID(long classID) {
        this.classID = classID;
    }

    public void setClassID(Long classID) {
      this.classID = classID;
  }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    public void setCourseID(Long courseID) {
      this.courseID = courseID;
  }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += Long.hashCode(classCourseID);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentClassCourse)) {
            return false;
        }
        PersistentClassCourse other = (PersistentClassCourse) object;
        if ((this.classCourseID == null && other.classCourseID != null) || (this.classCourseID != null && !this.classCourseID.equals(other.classCourseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentClassCourse[ classCourseID=" + classCourseID + " ]";
    }

    /**
     * Builds a PersistentClassCourse
     *
     * @return
     */
    public static PersistentClassCourse buildEmptyPersistentClassCourse() {
        PersistentClassCourse result = new PersistentClassCourse();
        result.setViewState(ViewState.invisible);

        return result;
    }

//    /**
//     * Builds a PersistentClassCourse
//     *
//     * @return
//     */
//    public static PersistentClassCourse buildFilledPersistentClassCourse() {
//        PersistentClassCourse result = new PersistentClassCourse();
//        result.setClassCourseID(01);
//        result.setClassID(02);
//        result.setCourseID(03);
//        result.setNotBefore(DwoDateUtilities.getStartOfDay());
//        result.setNotAfter(DwoDateUtilities.getEndOfDay());
//        result.setType(CourseType.normal.ordinal());
//        result.setViewState(ViewState.invisible);
//        return result;
//    }

    public DomClassCourse4Teacher buildDomClassCourse4Teacher() {
        DomClassCourse4Teacher classCourse = new DomClassCourse4Teacher();
        PersistentClassCourse.this.fillDomClassCourse4Teacher(classCourse);
        return classCourse;
    }

    public DomClassCourseFull buildDomClassCourseFull() {
        DomClassCourseFull classCourse = new DomClassCourseFull();
        fillDomClassCourseFull(classCourse);
        return classCourse;
    }

    private void fillDomClassCourse4Teacher(DomClassCourse4Teacher classCourse) {
        fillDomClassCourse(classCourse);
        classCourse.setCourseType(CourseType.values()[this.type]);
        classCourse.setViewState(this.viewState);
        classCourse.setAccessKey(this.accessKey);
        classCourse.setResults(this.results);
    }

    private void fillDomClassCourseFull(DomClassCourseFull classCourse) {
    	fillDomClassCourse4Teacher(classCourse);
        classCourse.setOptlock(optlock);
        classCourse.setLastChangeTimeStamp(lastChangeTimeStamp);
    	
    }
    
    public DomClassCourse buildDomClassCourse() {
        DomClassCourse classCourse = new DomClassCourse();
        PersistentClassCourse.this.fillDomClassCourse(classCourse);
        return classCourse;
    }

    private void fillDomClassCourse(DomClassCourse classCourse) {
        classCourse.setId(buildPersistenceId());
        classCourse.setClassId(PersistentSchoolClass.buildPersistenceId(this.classID));
        classCourse.setCourseId(PersistentCourse.buildPersistenceId(this.courseID));
        classCourse.setNotAfter(this.notAfter);
        classCourse.setNotBefore(this.notBefore);
        if (!viewState.moduleVisible() && this.type == CourseType.normal.ordinal()) // mark normal invisible modules.
        	classCourse.setCourseType(CourseType.invisible);
        else
            classCourse.setCourseType(CourseType.values()[this.type]);
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(classCourseID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aClassCourseId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aClassCourseId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentClassCourse.name(), aClassCourseId));
        return id;
    }

    /**
     * @return the viewState
     */
    public ViewState getViewState() {
        return viewState;
    }

    /**
     * @param viewState the viewState to set
     */
    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
    }

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public int getOptlock() {
		return optlock;
	}

	public void setOptlock(int optlock) {
		this.optlock = optlock;
	}

	public long getLastChangeTimeStamp() {
		return lastChangeTimeStamp;
	}

	public void setLastChangeTimeStamp(long lastChangeTimestamp) {
		this.lastChangeTimeStamp = lastChangeTimestamp;
	}

  public Long getDwoProfileID() {
    return dwoProfileID;
  }

  public void setDwoProfileID(Long dwoProfileID) {
    this.dwoProfileID = dwoProfileID;
  }

public String getSyExamID() {
	return syExamID;
}

public void setSyExamID(String syExamId) {
	this.syExamID = syExamId;
}

/**
 * @return the results
 */
public Boolean hasResults() {
	return results;
}

/**
 * @param results the results to set
 */
public void setResults(Boolean results) {
	this.results = results;
}
    
}
