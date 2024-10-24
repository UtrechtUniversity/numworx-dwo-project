package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.user.client.ui.Widget;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.MementoModule;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.sco.WiskOpdrMemento;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module
public class PrintViewModule {
	
	static class PrintMementoModule extends MementoModule {
		@Override
		protected Memento memento(ActivityComponent a, Provider<Scorm2004IF> api) {
			WiskOpdrMemento w = new WiskOpdrMemento(a, api.get());
			w.setCurrentOpdracht(0);
			return w;
		}

	}
	
	@Provides @Singleton ActivityInterface activity(ActivityComponent.Builder builder) {
		return builder.loggingModule(new SMLogger.LoggingModule()).mementoModule(new PrintMementoModule()).build();
	}
		
   final ViewModuleViewImpl createEntryView(RPCHandler rpc, boolean header, ActivityInterface activity) {
		return new ViewModuleViewImpl(
				(ActivityComponent) activity, 
				rpc, header, activity.api()) {

			@Override
			public Widget asWidget() {
				return contentPanel;
			}

		}
		.initialize();
	}

  @Provides protected ViewModuleViewImpl getViewModuleView(RPCHandler rpc, ActivityInterface activity) {
    return createEntryView(rpc, true, activity);
  }
  
  @Provides @Singleton protected SMLogger.LoggingModule loggingModule() { return new SMLogger.LoggingModule(); }
  
  protected PrintViewModule() {}
}
