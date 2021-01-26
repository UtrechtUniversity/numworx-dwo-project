package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.TeacherViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceTeacher;

@Module
abstract class TeacherModule {
  @Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);
  @Binds abstract SwitchViewEventHandler viewHandler(TeacherViewHandler handler);
  @Binds abstract PersonsService personsService(PersonsServiceTeacher service);

  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessages.class);
  }
  
  @Reusable @Provides static SecuredTeacherStudentModelManager securedTSMM() {
    return new SecuredTeacherStudentModelManager();
  }
}
