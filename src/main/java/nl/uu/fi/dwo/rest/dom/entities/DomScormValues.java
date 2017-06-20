package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

public class DomScormValues {
	private List<DomMapEntry<String,String>> values;
	private DomScoContext scoContext;

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
	
}
