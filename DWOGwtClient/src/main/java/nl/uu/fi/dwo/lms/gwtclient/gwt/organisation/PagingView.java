package nl.uu.fi.dwo.lms.gwtclient.gwt.organisation;

import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public interface PagingView<T> {

	void showPersonen(Map<String, T> collect, RoleType role);
	  
  }