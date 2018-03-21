package nl.uu.fi.dwo.account.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class StudentModelController {

	static private Logger LOG = Logger.getLogger("StudentModelController");
	String locale = "nl";
	
	StudentModelPanel panel;
	DomUserFull user;
	
	private SecuredStudentStudentModelManager manager;
	
	private DomContext context;
	private DomSchoolRoleAndClassV2 roleAndClass;
	private Map<String, DomStudentModelContext> models = Collections.EMPTY_MAP;

	public StudentModelController(StudentModelPanel panel, DomUserFull user, DomSchoolRoleAndClassV2 roleAndClass) {
		this.panel = panel;
		this.user = user;
		this.roleAndClass = roleAndClass;
		context = new DomContext();
		context.setDomHasRole(roleAndClass.getHasRole());
		
		manager = new SecuredStudentStudentModelManager();
		
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
		manager.getStudentModelDataScore(context, id).then(
				p -> {
					panel.updateStructure(id.getModelStructure(), p.getValue().getDomStudentModelStructureScore(), locale);
					
					return null;
				});
				
		
	}
	
}
