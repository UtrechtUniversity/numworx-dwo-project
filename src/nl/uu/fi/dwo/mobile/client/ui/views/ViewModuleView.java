package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public interface ViewModuleView extends IsWidget
{
	interface Presenter {
		void goTo(Place place);
	}
	
	void removeBtns();
	void setScoType(ScoType type);
	void setTitle(String title);
	void setupModule(String name, String file);

	public HeaderButton getBackButton();

	void setApi(Scorm2004IF scorm_guest);

	void close();
	
	AnchorView.AnchorContext getAnchorContext();
	void setAnchorContext(AnchorView.AnchorContext context);
	
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
	void setModel(Promise<DomStudentModelContext> studentModel);
}
