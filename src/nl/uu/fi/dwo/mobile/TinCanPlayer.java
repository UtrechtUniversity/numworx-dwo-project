package nl.uu.fi.dwo.mobile;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.mobile.client.dagger.DaggerWiskOpdrComponent;
import nl.uu.fi.dwo.mobile.client.dagger.WiskOpdrComponent;
import nl.uu.fi.dwo.mobile.client.sco.TinCanAPI;

public class TinCanPlayer extends NoordhoffPlayer {

	@Override
	protected Promise<String> inject() {
		// TODO Auto-generated method stub
		TinCanAPI api = new TinCanAPI();
		return api.Initialize().then( p -> {
			WiskOpdrComponent build = DaggerWiskOpdrComponent.builder().api(api).moduleView(new ModuleViewModuleImpl(true)).premium(true).build();
			api.register(build.bus());
			build.inject(this);
			return p;
		});
	}

}
