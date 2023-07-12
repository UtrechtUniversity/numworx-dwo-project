package nl.uu.fi.dwo.mobile.client.dagger;

import dagger.Binds;
import dagger.Module;
import nl.uu.fi.dwo.mobile.client.DWO2playerDefaults;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;

@Module
abstract class ProductionModule {
	  @Binds abstract DWOplayerParameters parameters(DWO2playerDefaults create);

}
