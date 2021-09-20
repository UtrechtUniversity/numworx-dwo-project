package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import java.util.Objects;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomMethod extends DomId {

	
	public DomMethod(PersistenceId id) {
		super(id);
	}

	public DomMethod() {
	}

	  public String method;
	  public List<String> books;
	  public List<List<String>> chapters;
	  public List<List<Integer>> edges;
	  public boolean standard;
	  
	  public static String key(PersistenceId id) {
	    if (id == null) return null;
	    String[] split = id.getIdString().split(";", 3);
	    return split[2];
	  }

	  public String key() {
	    return key(getId());
	  }

	  public String getMethod() {
	    return method;
	  }

	  public void setMethod(String method) {
	    this.method = method;
	  }

	  public String toString() {
		  return Objects.toString(method);
	  }
	
}
