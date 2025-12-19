package fi.dwo.commons.persistence.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

@Entity
@Table(name = "tblscopage")
@NamedQueries({
    @NamedQuery(name="PersistentScoPage.bySco", query="SELECT p FROM PersistentScoPage p WHERE p.id.scoID = :scoID and p.id.userID = 0 ORDER BY p.id.sequencenr ASC"),
    @NamedQuery(name="PersistentScoPage.byStudentSco", query="SELECT p FROM PersistentScoPage p WHERE p.id.scoID = :scoID and p.id.userID = :userID and p.id.schoolGroupID = :schoolGroupID ORDER BY p.id.sequencenr ASC"),
})
public class PersistentScoPage implements PersistentEntity {
  private static final Float FACTOR_1 = Float.valueOf(1.000f);	
	
  @EmbeddedId
  private PersistentScoPagePK id;

  @Column(name = "optlock")
  private Long optlock;
  @Column(name = "lastChangeTimeStamp")
  private long lastChangeTimeStamp;
  @NotNull
  @Column(name="del",nullable = false)
  private DelState delState = DelState.not;

  public void changeTimestamp() {
      lastChangeTimeStamp = System.currentTimeMillis();
      if (maxFactor == null) maxFactor = FACTOR_1;
  }

  @Column(name = "courseID") // optional, foreign key to tblcourse.courseID
  private Long courseID;

  @Column(name = "score")
  Integer score;
  @Column(name = "maxScore")
  Integer maxScore;
  @Column(name = "checkDocent")
  Boolean checkDocent;
  @Column(name = "correctie")
  Integer correctie;

  @Column(name = "correct")
  Boolean correct;
  @Column(name = "docentCorrect")
  Boolean docentCorrect;
  @Column(name = "visited")
  Boolean visited;
  @Column(name = "label")
  String label;
  @Column(name = "maxFactor")
  Float maxFactor = FACTOR_1;
  
/**
 * @return the id
 */
public PersistentScoPagePK getId() {
	return id;
}
/**
 * @param id the id to set
 */
public void setId(PersistentScoPagePK id) {
	this.id = id;
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
 * @return the courseID
 */
public Long getCourseID() {
	return courseID;
}
/**
 * @param courseID the courseID to set
 */
public void setCourseID(Long courseID) {
	this.courseID = courseID;
}
/**
 * @return the score
 */
public Integer getScore() {
	return score;
}
/**
 * @param score the score to set
 */
public void setScore(Integer score) {
	this.score = score;
}
/**
 * @return the maxScore
 */
public Integer getMaxScore() {
	return maxScore;
}
/**
 * @param maxScore the maxScore to set
 */
public void setMaxScore(Integer maxScore) {
	this.maxScore = maxScore;
}
/**
 * @return the checkDocent
 */
public Boolean getCheckDocent() {
	return checkDocent;
}
/**
 * @param checkDocent the checkDocent to set
 */
public void setCheckDocent(Boolean checkDocent) {
	this.checkDocent = checkDocent;
}
/**
 * @return the correctie
 */
public Integer getCorrectie() {
	return correctie;
}
/**
 * @param correctie the correctie to set
 */
public void setCorrectie(Integer correctie) {
	this.correctie = correctie;
}
/**
 * @return the correct
 */
public Boolean getCorrect() {
	return correct;
}
/**
 * @param correct the correct to set
 */
public void setCorrect(Boolean correct) {
	this.correct = correct;
}
/**
 * @return the docentCorrect
 */
public Boolean getDocentCorrect() {
	return docentCorrect;
}
/**
 * @param docentCorrect the docentCorrect to set
 */
public void setDocentCorrect(Boolean docentCorrect) {
	this.docentCorrect = docentCorrect;
}
/**
 * @return the visited
 */
public Boolean getVisited() {
	return visited;
}
/**
 * @param visited the visited to set
 */
public void setVisited(Boolean visited) {
	this.visited = visited;
}

public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
/**
 * @return the maxFactor
 */
public Float getMaxFactor() {
	return maxFactor;
}
/**
 * @param maxFactor the maxFactor to set
 */
public void setMaxFactor(Float maxFactor) {
	if (maxFactor == null) 
		this.maxFactor = FACTOR_1;
	else
		this.maxFactor = maxFactor;
}

public void setMaxFactor(Number factor) {
	if (factor instanceof Float) 
		maxFactor = (Float) factor;
	else if (factor != null) 
		maxFactor = factor.floatValue();
	else
		maxFactor = FACTOR_1;
}
  
}
