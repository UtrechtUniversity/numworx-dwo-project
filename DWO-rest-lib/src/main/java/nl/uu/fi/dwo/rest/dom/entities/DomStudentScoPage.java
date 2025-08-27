package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@SuppressWarnings("serial")
public class DomStudentScoPage extends DomId {
	private PersistenceId scoID, userID, schoolGroupID;
	private Long sequencenr; // start at 0
	private String label;
	
	private Boolean docentCorrectie;
	private Integer  maxScore, score, correctie;
	private Float maxFactor;

	public PersistenceId getScoID() {
		return scoID;
	}
	public void setScoID(PersistenceId scoID) {
		this.scoID = scoID;
	}
	public PersistenceId getUserID() {
		return userID;
	}
	public void setUserID(PersistenceId userID) {
		this.userID = userID;
	}
	public PersistenceId getSchoolGroupID() {
		return schoolGroupID;
	}
	public void setSchoolGroupID(PersistenceId schoolGroupID) {
		this.schoolGroupID = schoolGroupID;
	}
	public Long getSequencenr() {
		return sequencenr;
	}
	public void setSequencenr(Long sequencenr) {
		this.sequencenr = sequencenr;
	}
	public Boolean getDocentCorrectie() {
		return docentCorrectie;
	}
	public void setDocentCorrectie(Boolean docentCorrectie) {
		this.docentCorrectie = docentCorrectie;
	}
	public Integer getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Integer getCorrectie() {
		return correctie;
	}
	public void setCorrectie(Integer correctie) {
		this.correctie = correctie;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Float getMaxFactor() {
		return maxFactor;
	}
	public void setMaxFactor(Float maxFactor) {
		this.maxFactor = maxFactor;
	}
	
	
}
