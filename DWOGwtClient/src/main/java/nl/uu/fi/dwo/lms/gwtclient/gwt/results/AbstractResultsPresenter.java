package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;

public abstract class AbstractResultsPresenter {

	protected final EventBus eventBus;
	protected DwoGlobalVars dwoGlobalVars;

	protected AbstractResultsPresenter(EventBus bus, DwoGlobalVars vars) {
		this.eventBus = bus;
		this.dwoGlobalVars = vars;
	}

	public abstract void init();

	public void showDescription() {
		// TODO Auto-generated method stub
		
	}

}
