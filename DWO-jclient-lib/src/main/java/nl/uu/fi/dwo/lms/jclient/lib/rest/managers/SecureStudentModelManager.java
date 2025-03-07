package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface SecureStudentModelManager {

	List<DomStudentModelContext> getReducedList(DomDwoProfile profile) throws Dwo2Exception;

	DomStudentModelContext updateModel(DomStudentModelContext submit) throws Dwo2Exception;

	DomStudentModelContext patchModel(DomStudentModelContextPatch submit) throws Dwo2Exception;
	DomStudentModelContext patchModel(DomStudentModelContextPatch submit, DomDwoProfileId profile) throws Dwo2Exception;

	DomStudentModelContext get(DomStudentModelContextId modelContext) throws Dwo2Exception;

	DomSchoolMethod getActiveMethod(DomStudentModelContextId modelContext) throws Dwo2Exception;

}
