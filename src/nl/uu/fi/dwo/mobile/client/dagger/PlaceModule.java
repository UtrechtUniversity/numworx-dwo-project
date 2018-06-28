package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;

import com.google.gwt.place.shared.Place;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;

@Module abstract class PlaceModule {

  @Provides @Named("defaultPlace") static Place defaultPlace() {
    return new LoginPlace();
  }

}
