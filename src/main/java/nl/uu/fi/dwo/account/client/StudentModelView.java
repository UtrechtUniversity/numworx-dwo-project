package nl.uu.fi.dwo.account.client;

import java.util.Collection;

import org.osgi.util.promise.Promise;

import com.google.gwt.user.client.ui.PopupPanel;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public interface StudentModelView {

	void updateModels(Collection<String> titles);

	void updateStructure(DomStudentModelStructure modelStructure,
			DomStudentModelStructureScore domStudentModelStructureScore);

	void setPopup(PopupPanel popup);

	void setInitialStructure(Promise<DomStudentModelContext> studentModelStructure);

}
