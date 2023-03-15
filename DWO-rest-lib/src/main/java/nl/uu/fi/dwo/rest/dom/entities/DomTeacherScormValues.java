package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

public class DomTeacherScormValues {
	private List<DomMapEntry<String,String>> values;
	private DomStudentScoContext studentScoContext;

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
	 * @return the studentScoContext
	 */
	public DomStudentScoContext getStudentScoContext() {
		return studentScoContext;
	}

	/**
	 * @param studentScoContext the studentScoContext to set
	 */
	public void setStudentScoContext(DomStudentScoContext studentScoContext) {
		this.studentScoContext = studentScoContext;
	}
	
}
