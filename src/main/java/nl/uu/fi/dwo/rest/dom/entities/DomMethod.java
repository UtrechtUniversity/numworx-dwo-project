package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

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
	  
	  public String key() {
	    if (getId() == null) return null;
	    String[] split = getId().getIdString().split(";", 3);
	    return split[2];
	  }

	  public String getMethod() {
	    return method;
	  }

	  public void setMethod(String method) {
	    this.method = method;
	  }

	
	
}
