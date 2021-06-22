package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomMethod extends DomId {

	public DomMethod(PersistenceId id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	public DomMethod() {
		// TODO Auto-generated constructor stub
	}

	  public String method;
	  public String[] books;
	  public String[][] chapters;
	  public int[][] edges;
	  
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
