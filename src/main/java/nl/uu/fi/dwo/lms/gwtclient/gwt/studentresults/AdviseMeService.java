package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.ideas.client.Usermodel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

@RoleScope
public class AdviseMeService {

	private final IdeasIF ideas;
	private Promise<Usermodel> usermodel;
	
	@Inject AdviseMeService(IdeasIF i) {
		this.ideas = i;
	}
		
	private Promise<Usermodel> getUsermodel() {
		PromiseCallback<Usermodel[]> callback = new PromiseCallback<>();
		AbstractRule input = new AbstractRule() {
			
		};
		RuleIF[] inputs = new RuleIF[] { input };
		ideas.adviseMeUsermodel(inputs, "", callback);
		return callback.getPromise().map(ar -> ar[0]);
	}
	
	List<DomStudentModelContext> toContext(Usermodel u) {
		return Collections.EMPTY_LIST;
	}
	
	DomStudentModelDataScore toScore(Usermodel u) {
		return null;
	}
	
	public Promise<List<DomStudentModelContext>> getModels() {
		if (usermodel == null) usermodel = getUsermodel();
		return usermodel.map(this::toContext);
	}
	
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		if (usermodel == null) usermodel = getUsermodel();
		return usermodel.map(this::toScore);

	}
}
