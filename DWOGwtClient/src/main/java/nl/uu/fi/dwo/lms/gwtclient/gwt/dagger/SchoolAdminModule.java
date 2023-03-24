package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import com.google.gwt.core.client.GWT;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SchoolAdminPresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SchoolAdminViewHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEventHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessagesSchoolAdmin;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsServiceSchoolAdmin;

@Module
public abstract class SchoolAdminModule {
  @Binds abstract PresenterFactory presenterFactory(SchoolAdminPresenterFactory factory);
  @Binds abstract SwitchViewEventHandler viewHandler(SchoolAdminViewHandler handler);
  @Binds abstract PersonsService personsService(PersonsServiceSchoolAdmin service);
  @Provides @RoleScope static GwtClientMessages rb() {
    return GWT.create(GwtClientMessagesSchoolAdmin.class);
  }

}
