package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.i18n.client.LocaleInfo;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.ideas.client.Usermodel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class AdviseMeService implements StudentResults {

	private static final String ADVISEME = "adviseme-usermodel";
  private final IdeasIF ideas;
	private Promise<Usermodel> usermodel;
    final Map<String,String> context = new HashMap<>();
	
	@Inject AdviseMeService(IdeasIF i, DwoGlobalVars vars) {
		this.ideas = i;
		PersistenceId id = vars.getCurrentUser().getId();
		Object userid = PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
		context.put("userid", userid.toString());
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		if("default".equals(locale)) locale = "nl";
		context.put("language", locale);
	}
		
	private Promise<Usermodel> getUsermodel() {
		PromiseCallback<Usermodel[]> callback = new PromiseCallback<>();
		
		AbstractRule input = new AbstractRule() {
			@Override
			public Map getContext() {				
				return context;
			}
			
		};
		RuleIF[] inputs = new RuleIF[] { input };
		ideas.adviseMeUsermodel(inputs, ADVISEME, callback);
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

	@Override
	public void clear() {
		usermodel = null;	
	}
}
