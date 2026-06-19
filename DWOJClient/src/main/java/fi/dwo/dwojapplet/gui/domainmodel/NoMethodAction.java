package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

import fi.dwo.dwojapplet.gui.domainmodel.ExportAction.ExportPanel;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class NoMethodAction extends AbstractAction {

	private static final String METHOD = "Wiskunde B VWO-programma";
	private ExportPanel panel;

	public NoMethodAction(ExportPanel leerdomeinEditPanel) {
		super("probeersel: copy graph to nomethod");
		this.panel = leerdomeinEditPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("starting");
		DomStudentModelStructure model = panel.getModel();
		PersistenceId active = model.getActiveMethod();
		List<String> methods = model.getMethods();
		DomMethod source = null;
		Iterator<DomMethod> iter = MethodsProperties.instance().iterator();
		while (iter.hasNext()) {
			DomMethod domMethod = (DomMethod) iter.next();
			if (METHOD.equals(domMethod.getMethod()) || active.equals(domMethod.getId())) {
				source = domMethod;
				break;
			}
		}
		if (source == null) return;
		
		List<DomStudentModelCategory> cats = model.getCategories();
		for(DomStudentModelCategory cat : cats) {
			List<DomStudentModelObj> objs = cat.getObjectives();
			for (DomStudentModelObj obj : objs) {
				doiets(obj,source);
			}
		}
		panel.save(model);
	}

	private void doiets(DomStudentModelObj obj, DomMethod source) {
		List<DomStudentModelObj> objs = obj.getObjectives();
		if (objs != null) {
			for (DomStudentModelObj child: objs) {
				doiets(child,source);
			}
			return;
		}
		// obj is a leaf in the graph:
		DomStudentModelContextInfo info = obj.getInfo();
		List<DomStudentModelMethodInfo> minfos = info.getMethodInfo();
		if (minfos != null)
		for (DomStudentModelMethodInfo minfo : minfos) {
			String method = minfo.getMethod();
			if (source.key().equals(method)) {
				Integer x = minfo.getX();
				Integer y = minfo.getY();				
				info.setX(x);
				info.setY(y);
			}
		}
	}

}
