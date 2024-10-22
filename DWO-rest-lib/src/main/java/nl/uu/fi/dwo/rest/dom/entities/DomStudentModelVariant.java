package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Objects;

public class DomStudentModelVariant implements Comparable<DomStudentModelVariant> {
	private static final String DEFAULT_VARIANT = "";

	private String name;
	
	public String toString() {
		return Objects.toString(name, DEFAULT_VARIANT);// o i d
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomStudentModelVariant other = (DomStudentModelVariant) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public int compareTo(DomStudentModelVariant o) {
		String t1 = Objects.toString(name, "");
		String t2 = Objects.toString(o.name, "");
		return t1.compareTo(t2);
	}
	
}
