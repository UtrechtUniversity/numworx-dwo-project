package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Binds;
import dagger.Module;

import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SMDescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;

@Module
abstract class StudentModelModule {
	@Binds abstract DescriptionService description(SMDescriptionService sm);
}
