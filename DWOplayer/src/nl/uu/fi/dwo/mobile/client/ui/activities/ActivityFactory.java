package nl.uu.fi.dwo.mobile.client.ui.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;

public interface ActivityFactory {
	Activity create(SelectModuleItem item, Place place);
}
