package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomScormValues {
	private List<DomMapEntry<String,String>> values;
	private DomScoContext scoContext;
	private PersistenceId schoolClassID;
	/**
	 * @return the values
	 */
	public List<DomMapEntry<String,String>> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<DomMapEntry<String,String>> values) {
		this.values = values;
	}

	/**
	 * @return the scoContext
	 */
	public DomScoContext getScoContext() {
		return scoContext;
	}

	/**
	 * @param scoContext the scoContext to set
	 */
	public void setScoContext(DomScoContext scoContext) {
		this.scoContext = scoContext;
	}

	/**
	 * @return the schoolClassID
	 */
	public PersistenceId getSchoolClassID() {
		return schoolClassID;
	}

	/**
	 * @param schoolClassID the schoolClassID to set
	 */
	public void setSchoolClassID(PersistenceId schoolClassID) {
		this.schoolClassID = schoolClassID;
	}
	
}
