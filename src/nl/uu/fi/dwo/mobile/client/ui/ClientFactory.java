package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
				
	Promise<Void> logout();

	void gotoCourses();	
	

}
