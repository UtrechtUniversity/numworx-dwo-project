package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public interface ClientFactory
{
	EventBus getEventBus();
				
	Promise<Void> logout();	
	
	boolean isPremium();


}
