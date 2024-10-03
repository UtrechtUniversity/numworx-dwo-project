package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
//import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNone;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * @see GWT
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class ClientFactoryImpl implements ClientFactory
{
	protected final EventBus eventBus;
			
	// singleton pattern.
	protected Provider<? extends HeaderView> headerView;

	protected void setup(Provider<HeaderViewNone> none, Provider<HeaderView> numworx)
	{
	  if (Actions.isAvailable() )
	  {
	    HeaderViewNone headerViewNone = none.get();
	    headerViewNone.setPresenter(placeController::goTo);
	    headerView = none;
	  } else {
        HeaderView impl = numworx.get();
        impl.setPresenter(placeController::goTo);
	    headerView = numworx;
	  }
	};

	
	protected final PlaceController placeController;
	private final Provider<ViewModuleViewBuilder> entryView;
	protected RPCHandler handler;
	
	public ClientFactoryImpl(EventBus bus, PlaceController controller, 
	                 final Provider<ViewModuleViewBuilder> entryView	                 
	       )
	{
	  this.eventBus = bus;
	  this.placeController = controller;
	  this.entryView = entryView;
	}

	protected abstract Scorm2004IF setupAPI();

	public EventBus getEventBus()
	{
		return eventBus;
	}

	public ViewModuleView getEntryView()
	{
		ViewModuleView view = entryView.get();
		return view;
	}

	public HeaderView getHeaderView() {
		return headerView.get();
	}
		
	protected void setRPCHandler(RPCHandler handler) {
		this.handler = handler;
	}
			
	protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}


	@Override
	public void goTo(Place place) {
		placeController.goTo(place);
	}	
	
}
