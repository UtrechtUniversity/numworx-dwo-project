package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.i18n.client.LocaleInfo;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.ideas.client.Usermodel;
import nl.uu.fi.dwo.ideas.client.Usermodel.Competence;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class AdviseMeService implements StudentResults {

	private static final String ADVISEME = "adviseme-usermodel";
	private final IdeasIF ideas;
	private Promise<Usermodel> usermodel;
    final Map<String,String> context = new HashMap<>();
	private String locale;
	
	@Inject AdviseMeService(IdeasIF i, DwoGlobalVars vars) {
		this.ideas = i;
		PersistenceId id = vars.getCurrentUser().getId();
		Object userid = PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
		context.put("userid", userid.toString());
		locale = LocaleInfo.getCurrentLocale().getLocaleName();
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
	
	List<DomStudentModelContext4Student> toContext(Usermodel u) {
		DomStudentModelContext4Student context = to1Context(u);
		return Collections.singletonList(context);
	}

	private DomStudentModelContext4Student to1Context(Usermodel u) {
		DomStudentModelContext4Student context = new DomStudentModelContext4Student();
		context.setId(new PersistenceId("ADVISEME;"+PersistenceClassType.PersistentStudentModelContext + ";" + u.getStudent()));
		DomStudentModelStructure structure = new DomStudentModelStructure();
		structure.setCategories(toCategories(u.getCompetence().getChildren()));
		structure.setInfo(toInfo(u.getCompetence()));
		context.setModelStructure(structure);
		context.setFilter(Collections.emptyMap());
		return context;
	}
	
	private List<DomStudentModelCategory> toCategories(List<Competence> children) {
		return children.stream().map(item -> { 
			DomStudentModelCategory result = new DomStudentModelCategory();
			result.setInfo(toInfo(item));
			result.setObjectives(toObjectives(item.getChildren()));			
			return result;
		}).collect(Collectors.toList());
	}

	private List<DomStudentModelObj> toObjectives(List<Competence> children) {
		if (children == null) return null;
		return children.stream().map(item -> {
			DomStudentModelObj result = new DomStudentModelObj();
			result.setInfo(toInfo(item));
			result.setObjectives(toObjectives(item.getChildren()));
			return result;
		}).collect(Collectors.toList());
		
	}

	
	
	private DomStudentModelContextInfo toInfo(Competence item) {
		DomStudentModelContextInfo info = new DomStudentModelContextInfo(new HashMap<>(), new HashMap<>());
		info.setId(item.getId());
		info.getTitle().put(locale, item.getLabel());
	    String description = item.getDescription();
	    if (description == null) description = "";
	    String example = item.getExample();
	    DwoLocalesForGWT instance = DwoLocalesForGWT.instance;
	    String EXAMPLE = instance.NUM_LBL_ADVISEME_EXAMPLE();
	    if (example != null && !example.isEmpty()) {
	      description += "\n\n" + EXAMPLE + "\n\n" + example;
	    }
	    info.getDescription().put(locale, description); // XXX wat komt hier?
		return info;
	}

	DomStudentModelDataScore toScore(Usermodel u) {
		DomStudentModelDataScore result = new DomStudentModelDataScore();
		DomStudentModelStructureScore model = new DomStudentModelStructureScore();
		model.setScore(u.getCompetence().getValue().doubleValue());
		model.setCategories(toCategoriesScore(u.getCompetence().getChildren()));
		result.setDomStudentModelStructureScore(model);
		return result;
	}
	
	private List<DomStudentModelCategoryScore> toCategoriesScore(List<Competence> children) {
		return children.stream().map(item -> { 
			DomStudentModelCategoryScore score = new DomStudentModelCategoryScore();
			score.setScore(item.getValue().doubleValue());
			score.setObjectives(toObjectivesScore(item.getChildren()));
			return score;
		}).collect(Collectors.toList());
	}

	private List<DomStudentModelObjectiveScore> toObjectivesScore(List<Competence> children) {
		if (children == null)
			return null;
		return children.stream().map(item -> {
			DomStudentModelObjectiveScore score = new DomStudentModelObjectiveScore();
			score.setScore(item.getValue().doubleValue());
			score.setChildren(toObjectivesScore(item.getChildren()));
			return score;
		}).collect(Collectors.toList());
	}

	public Promise<List<DomStudentModelContext4Student>> getModels() {
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

	@Override
	public Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id) {
		if (usermodel == null) usermodel = getUsermodel();
		return usermodel.map(this::to1Context);
	}
}
