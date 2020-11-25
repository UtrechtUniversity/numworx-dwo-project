package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.shared.GWT;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentStudentModelRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentStudentModelManager {

	SecuredStudentStudentModelRestCaller service = GWT.create(SecuredStudentStudentModelRestCaller.class);
	
	public Promise<List<DomStudentModelContext>> getStudentModels(DomContext context) {
		RestContext rest = new RestContext();
		rest.setRestContext(context);
		return F(service::getStudentModels,PathId.getId(context), rest);
	}
	
	public Promise<DomStudentModelDataScore> getStudentModelDataScore(DomContext context, DomStudentModelContextId id) {
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		return F(service::getStudentModelDataScore,PathId.getId(context), rest);
	}
	
	public Promise<DomLRS> getLRS(DomContext context) {
	  RestContext rest = new RestContext();
	  rest.setRestContext(context);
	  return F(service::getLRS,PathId.getId(context), rest);
	}
	
	public Promise<List<DomStudentModelContext>> getReducedModels(DomContext context, DomSchoolClass sc) {
		RestSchoolClass rest = new RestSchoolClass();
		rest.setDomSchoolClass(sc);
		rest.setRestContext(context);
		return F(service::getReducedList, PathId.getId(context), rest);
	}
	
	public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId id) {
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		return F(service::getStudentModel, PathId.getId(context), rest);
		
	}
}
