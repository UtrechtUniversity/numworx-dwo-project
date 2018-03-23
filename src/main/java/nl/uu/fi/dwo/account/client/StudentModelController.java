package nl.uu.fi.dwo.account.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.LocaleInfo;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class StudentModelController {

	static private Logger LOG = Logger.getLogger("StudentModelController");
	String locale = LocaleInfo.getCurrentLocale().getLocaleName();
	
	StudentModelView panel;
	DomUserFull user;
	
	private SecuredStudentStudentModelManager manager;
	
	private DomContext context;
	private Map<String, DomStudentModelContext> models = Collections.emptyMap();

	public StudentModelController(StudentModelView panel, String locale) {
		this(panel, DwoGlobalVars.instance().getCurrentUser(), DwoGlobalVars.instance().getActiveSchoolRoleAndClass());
		this.locale = locale;
	}
	
	public StudentModelController(StudentModelView panel, DomUserFull user, DomSchoolRoleAndClassV2 roleAndClass) {
		this.panel = panel;
		this.user = user;
		context = new DomContext();
		context.setDomHasRole(roleAndClass.getHasRole());
		
		manager = new SecuredStudentStudentModelManager();
		
		manager.getStudentModels(context).then(
				p-> {
					Map<String, DomStudentModelContext> map = new LinkedHashMap<>();
					List<DomStudentModelContext> list = p.getValue();
					for(DomStudentModelContext c: list) {
						String title = c.getModelStructure().getInfo().getTitle().get(locale);
						map.put(title, c);
					}
					models = map;
					panel.updateModels(map.keySet());
					return null;
				});
	}

	public void init() {
		manager.getStudentModels(context).then(
				p-> {
					Map<String, DomStudentModelContext> map = new LinkedHashMap<>();
					List<DomStudentModelContext> list = p.getValue();
					for(DomStudentModelContext c: list) {
						String title = c.getModelStructure().getInfo().getTitle().get(locale);
						//String description = c.getModelStructure().getInfo().getDescription().get(locale);
						map.put(title, c);
					}
					models = map;
					panel.updateModels(map.keySet());
					return null;
				});
	}

	public void select(String value) {
		LOG.info("select : " + value);		
		final DomStudentModelContext id = models.get(value);
		if(id == null) {
			panel.updateStructure(null, null);
		}
		manager.getStudentModelDataScore(context, id).then(
				p -> {
					panel.updateStructure(id.getModelStructure(), p.getValue().getDomStudentModelStructureScore());
					
					return null;
				});
				
		
	}
	
}
