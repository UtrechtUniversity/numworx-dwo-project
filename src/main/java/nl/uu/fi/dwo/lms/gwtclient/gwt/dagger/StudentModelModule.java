package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;

@Module
abstract class StudentModelModule {
	@Provides static DescriptionService description() {
		return null;
	}
}
