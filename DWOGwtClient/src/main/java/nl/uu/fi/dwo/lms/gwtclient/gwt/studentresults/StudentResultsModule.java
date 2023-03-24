package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class StudentResultsModule {

	private StudentResultsModule() {
	}

	
	@Binds abstract StudentResults studentResults(XAPIService service);
}
