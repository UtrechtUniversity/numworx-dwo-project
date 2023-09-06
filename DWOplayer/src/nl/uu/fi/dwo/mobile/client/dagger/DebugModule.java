package nl.uu.fi.dwo.mobile.client.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.mobile.client.DWO2playerDebug;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;

@Module
abstract class DebugModule {
	  @Binds abstract DWOplayerParameters parameters(DWO2playerDebug create);

}
