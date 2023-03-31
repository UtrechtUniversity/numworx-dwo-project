package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;

@Module abstract class PlaceModule {
	
	static native private String getDefaultPlace() /*-{
		return $wnd.defaultPlace
	}-*/;
	

  @Provides @Named("defaultPlace") static Place defaultPlace(PlaceHistoryMapper mapper) {
	  try { 
		  String token = getDefaultPlace();
		  if (!token.isEmpty()) {
			  Place p = mapper.getPlace(token);
			  if (p != null) return p;
		  }
	  } catch(Exception oops) { }
    return new LoginPlace();
  }

}
