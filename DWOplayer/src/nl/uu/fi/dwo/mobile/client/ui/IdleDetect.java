package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;

@Singleton 
public class IdleDetect extends fi.dwo.gwt.lib.rest.ui.IdleDetect {
		
	@Inject IdleDetect(EventBus bus) {
		super(bus);
	}
	
	
}
