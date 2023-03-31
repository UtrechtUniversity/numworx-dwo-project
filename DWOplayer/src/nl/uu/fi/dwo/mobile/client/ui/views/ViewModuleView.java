package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public interface ViewModuleView extends IsWidget
{
	interface Presenter extends GotoController {
	}
	
	void removeBtns();
	void setScoType(ScoType type);
	void setTitle(String title);
	Promise<Boolean> setupModule(String name, String file);

	void close();
	
	AnchorContext getAnchorContext();
	void setAnchorContext(AnchorContext context);
	
	 public interface Loader {
	        void viewModuleViewSetupDone();
	    }

	void setUnitId(String id);

	OpdrNavIF getOpdrNav();
	Number getScoreRaw();
	
	void setReadonly(boolean readonly);
	boolean isReadonly();
	void setTrail(List<SelectModuleItem> trail);
	Scorm2004IF getApi();
	
	void setPresenter(Presenter p);
	void setModel(Promise<DomStudentModelContextId> studentModel);
	
	boolean nextPageAction();
    void showIcon(boolean b);
    
    default void setLocation(String location) {
    	getApi().SetValue(Memento.LOCATION, location);
    }
}
