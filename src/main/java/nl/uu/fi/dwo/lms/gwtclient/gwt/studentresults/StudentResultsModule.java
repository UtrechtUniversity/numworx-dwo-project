package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.ideas.client.IdeasClient;
import nl.uu.fi.dwo.ideas.client.IdeasIF;

@Module
public abstract class StudentResultsModule {

	private StudentResultsModule() {
	}

	@Provides @Singleton static IdeasIF ideas() {
		return new IdeasClient("/gwtclient/ideas/IdeasServlet");
	}
	
	@Binds abstract StudentResults studentResults(AdviseMeService service);
}
