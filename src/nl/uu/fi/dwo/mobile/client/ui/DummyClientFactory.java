package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

@Singleton
public class DummyClientFactory implements ClientFactory {

	final private EventBus eventBus;
	final private boolean premium;
	private TrafficAgent agent;
	
	@Inject DummyClientFactory(EventBus eventBus, TrafficAgent agent, @Named("premium") boolean premium) {
      this.eventBus = eventBus;
      this.agent = agent;
      this.premium = premium;
      java.util.logging.Logger.getLogger("DummyClientFactory " + premium);
    }

  @Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public Promise<Void> logout() {
		return null;
	}

	public void addBarrier(Promise<?> p) {
		agent.addBarrier(p);
	}
 
	@Override
	public boolean isPremium() {
		return premium; // FIXME komt van buitenaf.
	}

}
