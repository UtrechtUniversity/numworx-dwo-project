package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
				
	Promise<Void> logout();

	void gotoCourses();

	void goTo(Place next);	
	

}
