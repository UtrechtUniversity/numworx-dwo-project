package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface SecureStudentModelManager {

	List<DomStudentModelContext> getReducedList() throws Dwo2Exception;

}
