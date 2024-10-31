package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class DomStudentModelVariant implements Comparable<DomStudentModelVariant> {
	private static final String DEFAULT_VARIANT = "";

	private String name;
	private TreeMap<String,Boolean> layers = new TreeMap<>();
	
	public DomStudentModelVariant() { }
	public DomStudentModelVariant(String name) { this.name = name; }
	
	public DomStudentModelVariant(DomStudentModelVariant v) {
		this(v.name);
		if (v.layers != null) {
			layers = new TreeMap<>(v.layers);
		}
	}
	
	
	
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
	public Map<String,Boolean> getLayers() {
		return layers;
	}

	public void setLayers(Map<String,Boolean> layers) {
		if (layers != null)
			this.layers = new TreeMap<>(layers);
		else 
			this.layers = new TreeMap<>();
	}
	
}
