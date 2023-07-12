package nl.uu.fi.dwo.mobile;

import nl.uu.fi.dwo.mobile.client.dagger.DWO2PlayerComponent;
import nl.uu.fi.dwo.mobile.client.dagger.DaggerDWO2PlayerDebugComponent;

public class DWO2playerDebug extends DWO2player {

	public DWO2playerDebug() {
	}

	@Override
	protected void createClientFactory() {
		// TODO Auto-generated method stub
		DWO2PlayerComponent create = DaggerDWO2PlayerDebugComponent.builder()
		    .profile(PROFILE_ID)
		    .build();
		create.inject(this);
		start(create.placeHistoryHandler());
	}

}
