package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Module;
import dagger.Provides;

@Module public abstract class PlayerModule {

  @Provides @Singleton EventBus eventBus() {
      return new SimpleEventBus();
  }
 
}
