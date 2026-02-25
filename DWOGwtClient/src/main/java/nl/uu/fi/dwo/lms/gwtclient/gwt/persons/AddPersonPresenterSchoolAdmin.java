package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class AddPersonPresenterSchoolAdmin extends AddPersonPresenter {

  private static final Logger LOG = Logger.getLogger(AddPersonPresenterSchoolAdmin.class.getName());

  @Inject
  AddPersonPresenterSchoolAdmin(DwoGlobalVars dwoGlobalVars, EventBus eventBus,
      PersonsServiceSchoolAdmin manager) {
    this.eventBus = eventBus;
    this.dwoGlobalVars = dwoGlobalVars;
    this.manager = manager;
    FAILURE = new LoggingFailure(LOG, eventBus);
    this.role = RoleType.SCHOOLADMIN;
  }

  @Override
  public void init() {
    view.clear();
    view.setHelp(dwoGlobalVars.buildHelpUrl("#addPerson"));
    view.init(RoleType.SCHOOLADMIN); // role of client user.
    view.setEmptyTableMessage();
    updateSchoolClasses();
  }

  @JsMethod
  public void submitTeacher(String schoolClassId, String username, String givenName,
      String insertion, String familyName, String eMail, String password, boolean invite) {
    // Verify formfields
    if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(password, familyName, givenName, eMail,
        username)) {
      LOG.log(Level.INFO, "valid required fields.");
      if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(insertion)) {
        insertion = insertion.trim();
      } else {
        insertion = null;
      }
    } else {
      eventBus
          .fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(
              DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Required_Fields)));
      return;
    }

    if (!SimpleValidUserFieldsChecker.isValidUserName(username)) {
      eventBus
          .fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(
              DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_UserName_Invalid)));
      return;
    }
    if (!SimpleValidUserFieldsChecker.isValidEmail(eMail)) {
      eventBus.fireEvent(new AlertDialogWithOKEvent(
          Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(),
              Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid)));
      return;
    } else {
      eMail = eMail.trim();
    }
    if (!SimpleValidUserFieldsChecker.isValidPassword(password)) {
      // invalid password format
      eventBus
          .fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(
              DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
      return;
    }
    DomUserFull newUser = new DomUserFull();
    newUser.setEmail(eMail);
    newUser.setFamilyName(familyName);
    newUser.setGivenName(givenName);
    newUser.setInsertion(insertion);
// crypt or md5
    if (invite)
    	newUser.setPassword(DomLoginCheck.crypt(password));
    else 
    	newUser.setPassword(MD5.md5(password));
    newUser.setSingleSchool(false);
    newUser.setUserName(username);

    Promise<?> promise = invite ? manager.submitTeacherv2(newUser) : manager.submitTeacher(newUser);
    DomSchoolClass schoolClass = taggedSchoolClasses.getOrDefault(schoolClassId, NULL).getSchoolClass();
    if (schoolClass != null) {
      promise = promise.then(p -> { return manager.getTeachersInSchool(); })
      .map ( list -> { return list.stream().filter(p -> p.getUserName().equals(username)).findAny().get(); } )
      .then( p-> {
        DomTeacher teacher = p.getValue();
        DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
        submit.setSchoolClass(schoolClass);
        submit.setTeacher(teacher);
        return manager.submitTeacherToSchoolClass(submit);
      });
    }
    promise
    .then( p-> {
      eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUM_DLG_User_TeacherAdded()));
      view.clear();
      view.init(role);

      return null;
    }, FAILURE);
  }

}
