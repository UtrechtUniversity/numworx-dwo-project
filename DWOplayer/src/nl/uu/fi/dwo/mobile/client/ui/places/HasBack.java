package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public interface HasBack {
	void setBack(SelectModuleItem item);
	SelectModuleItem getBack();
	PersistenceId getID();
	Place getPlace();
}
