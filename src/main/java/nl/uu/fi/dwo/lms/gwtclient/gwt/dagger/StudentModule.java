package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.StudentPresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.StudentViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsGraphView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsStudentSchoolClassView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessagesStudent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.StudentSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResults;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraphPresenter;

@Module
abstract class StudentModule {
  @Binds abstract PresenterFactory presenterFactory(StudentPresenterFactory factory);
  @Binds abstract SwitchViewEventHandler viewHandler(StudentViewHandler handler);
  @Binds abstract StudentSchoolclassPresenter.Display studentSchoolClassView(JsStudentSchoolClassView view);
  @Binds abstract StudentResultsGraphPresenter.Display studentResultGraphView(JsStudentResultsGraphView view);
  @Binds abstract DescriptionService descriptionService(StudentResults results);
  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessagesStudent.class);
  }
  @Reusable @Provides static MethodManager methodManager() {
	  return MethodManager.student();
  }

}
