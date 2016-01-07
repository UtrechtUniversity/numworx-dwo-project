/**
 * 
 */
package nl.uu.fi.dwo.mobile;

import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;

import com.google.gwt.core.client.EntryPoint;

/**
 * @author peterboon
 *
 */
public class CoursePlayer extends DWOplayer implements EntryPoint {

	public CoursePlayer() {
		super();
		defaultPlace = new FlatModulePlace();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
	}

}
