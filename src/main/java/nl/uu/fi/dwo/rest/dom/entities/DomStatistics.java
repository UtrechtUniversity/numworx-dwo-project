package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DomStatistics {

	private DomSchool school;
	private List<DomMapEntry<String, String>> statistics;
	private Long fetchTimeStamp;
	/**
	 * @return the school
	 */
	public DomSchool getSchool() {
		return school;
	}
	/**
	 * @param school the school to set
	 */
	public void setSchool(DomSchool school) {
		this.school = school;
	}
	/**
	 * @return the statistics
	 */
	public List<DomMapEntry<String, String>> getStatistics() {
		return statistics;
	}
	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(List<DomMapEntry<String, String>> statistics) {
		this.statistics = statistics;
	}
	/**
	 * @return the fetchTimeStamp
	 */
	public Long getFetchTimeStamp() {
		return fetchTimeStamp;
	}
	/**
	 * @param fetchTimeStamp the fetchTimeStamp to set
	 */
	public void setFetchTimeStamp(Long fetchTimeStamp) {
		this.fetchTimeStamp = fetchTimeStamp;
	}
}
