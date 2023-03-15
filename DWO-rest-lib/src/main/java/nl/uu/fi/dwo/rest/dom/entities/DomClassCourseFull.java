package nl.uu.fi.dwo.rest.dom.entities;

public class DomClassCourseFull extends DomClassCourse4Teacher {

    private Integer optlock;
    private Long lastChangeTimeStamp;

	public DomClassCourseFull() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the optlock
	 */
	public Integer getOptlock() {
		return optlock;
	}

	/**
	 * @param optlock the optlock to set
	 */
	public void setOptlock(Integer optlock) {
		this.optlock = optlock;
	}

	/**
	 * @return the lastChangeTimestamp
	 */
	public Long getLastChangeTimeStamp() {
		return lastChangeTimeStamp;
	}

	/**
	 * @param lastChangeTimestamp the lastChangeTimestamp to set
	 */
	public void setLastChangeTimeStamp(Long lastChangeTimestamp) {
		this.lastChangeTimeStamp = lastChangeTimestamp;
	}



}
