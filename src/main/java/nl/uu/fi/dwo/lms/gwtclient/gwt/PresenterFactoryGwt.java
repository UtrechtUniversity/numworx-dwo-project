package nl.uu.fi.dwo.lms.gwtclient.gwt;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.SMClassResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.SMStudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraphPresenter;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.OrganisationPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddPersonPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.AddStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditStudentPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.EditTeacherPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.ImportPersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectStudentResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SelectedResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.StudentScoResultPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.AddTeacherToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.CopyOrMoveStudentToSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ModulesOfSchoolclassPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.ProgressDialogWithAbortPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.welcome.WelcomePresenter;

/**
 * Local ViewFactory implementation class.
 *
 * @author G.A.J. van der Plas
 */
@RoleScope // one per Role change
public class PresenterFactoryGwt implements PresenterFactory {

    @Inject MainPresenter mainPresenter;
    @Inject LoginPresenter loginPresenter;
    @Inject WelcomePresenter welcomePresenter;
    @Inject AccountPresenter accountPresenter;
    @Inject SchoolclassesPresenter schoolclassesPresenter;
    @Inject EditSchoolclassPresenter editSchoolclassPresenter;
    @Inject AddStudentToSchoolclassPresenter addStudentToSchoolclassPresenter;
    @Inject CopyOrMoveStudentToSchoolclassPresenter copyOrMoveStudentToSchoolclassPresenter;
    @Inject AddTeacherToSchoolclassPresenter addTeacherToSchoolclassPresenter;
    @Inject ModulesOfSchoolclassPresenter modulesOfSchoolclassPresenter;
    @Inject ModulesPresenter modulesPresenter;
    @Inject ResultsPresenter resultsPresenter;
    @Inject SelectedResultsPresenter selectedResultsPresenter;
    @Inject SelectStudentResultsPresenter selectStudentResultsPresenter;
    @Inject StudentScoResultPresenter studentScoResultPresenter;
    @Inject LogResultsPresenter logResultsPresenter;

    @Inject PersonsPresenter personsPresenter;
    @Inject AddStudentPresenter addStudentPresenter;
    @Inject ImportPersonsPresenter importPersonsPresenter;
    @Inject EditStudentPresenter editStudentPresenter;
    @Inject EditTeacherPresenter editTeacherPresenter;

    @Inject MessageDialogWithOKPresenter messageDialogWithOKPresenter;
    @Inject AlertDialogWithOKPresenter alertDialogWithOKPresenter;
    @Inject AlertDialogWithConfirmCancelPresenter alertDialogWithConfirmCancelPresenter;
    @Inject ProgressDialogWithAbortPresenter progressDialogWithAbortPresenter;
    
    @Inject StudentModelPresenter studentModelPresenter;
    @Inject StudentResultsGraphPresenter studentResultsGraphPresenter;
    @Inject SMClassResultsPresenter smClassResultPresenter;
	@Inject SMStudentResultsPresenter smStudentResultsPresenter;


    @Inject public PresenterFactoryGwt() {}

    @Override @Inject
    public void bindViewFactory(ViewFactory viewFactory) {
        loginPresenter.setView(viewFactory.getLoginView());
        welcomePresenter.setView(viewFactory.getWelcomeView());
        accountPresenter.setView(viewFactory.getAccountView());
        schoolclassesPresenter.setView(viewFactory.getSchoolclassesView());
        editSchoolclassPresenter.setView(viewFactory.getEditSchoolclassView());
        addStudentToSchoolclassPresenter.setView(viewFactory.getAddStudentToSchoolclassView());
        copyOrMoveStudentToSchoolclassPresenter.setView(viewFactory.getCopyOrMoveStudentToSchoolclassView());
        addTeacherToSchoolclassPresenter.setView(viewFactory.getAddTeacherToSchoolclassView());
        modulesOfSchoolclassPresenter.setView(viewFactory.getModulesOfSchoolclassView());
        resultsPresenter.setView(viewFactory.getResultsView());
        selectedResultsPresenter.setView(viewFactory.getSelectedResultsView());
        studentScoResultPresenter.setView(viewFactory.getStudentScoResultView());

        personsPresenter.setView(viewFactory.getPersonsView());
        addStudentPresenter.setView(viewFactory.getAddPersonView());
        importPersonsPresenter.setView(viewFactory.getImportPersonsView());
        editTeacherPresenter.setView(viewFactory.getEditTeacherView());
        editStudentPresenter.setView(viewFactory.getEditStudentView());

        messageDialogWithOKPresenter.setView(viewFactory.getMessageDialogWithOKView());
        alertDialogWithOKPresenter.setView(viewFactory.getAlertDialogWithOKView());
        alertDialogWithConfirmCancelPresenter.setView(viewFactory.getAlertDialogWithConfirmCancelView());
        progressDialogWithAbortPresenter.setView(viewFactory.getProgressDialogWithAbortView());
        //last!
        mainPresenter.setView(viewFactory.getMainView());
    }

//    @Override
//    public EventBus getEventBus() {
//        return eventBus;
//    }
//
//    /**
//     * @return the dwoGlobalVars
//     */
//    @Override
//    public DwoGlobalVars getDwoGlobalVars() {
//        return dwoGlobalVars;
//    }

    /**
     * @return the mainPresenter
     */
    @JsMethod
    @Override
    public MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    /**
     * @return the loginPresenter
     */
    @JsMethod
    @Override
    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    /**
     * @return the resultsPresenter
     */
    @JsMethod
    @Override
    public ResultsPresenter getResultsPresenter() {
        return resultsPresenter;
    }

    /**
     * @return the resultsPresenter
     */
    @JsMethod
    @Override
    public SelectedResultsPresenter getSelectedResultsPresenter() {
        return selectedResultsPresenter;
    }

    @JsMethod
    @Override
    public SchoolclassesPresenter getSchoolclassesPresenter() {
        return schoolclassesPresenter;
    }


    /**
     * @return the accountPresenter
     */
    @JsMethod
    @Override
    public AccountPresenter getAccountPresenter() {
        return accountPresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    //@JsMethod
    @Override
    public WelcomePresenter getWelcomePresenter() {
        return welcomePresenter;
    }

    /**
     * @return the addSchoolclassPresenter
     */
    @JsMethod
    @Override
    public EditSchoolclassPresenter getEditSchoolclassPresenter() {
        return editSchoolclassPresenter;
    }

    /**
     * @return the messageDialogWithOKPresenter
     */
    @JsMethod
    @Override
    public MessageDialogWithOKPresenter getMessageDialogWithOKPresenter() {
        return messageDialogWithOKPresenter;
    }

    @JsMethod
    @Override
    public AlertDialogWithConfirmCancelPresenter getAlertDialogWithConfirmCancelPresenter() {
        return alertDialogWithConfirmCancelPresenter;
    }

    @JsMethod
    @Override
    public ProgressDialogWithAbortPresenter getProgressDialogWithAbortPresenter() {
        return progressDialogWithAbortPresenter;
    }

    @JsMethod
    @Override
    public AlertDialogWithOKPresenter getAlertDialogWithOKPresenter() {
        return alertDialogWithOKPresenter;
    }

    @JsMethod
    public AddStudentToSchoolclassPresenter getAddStudentToSchoolclassPresenter() {
        return addStudentToSchoolclassPresenter;
    }

    @JsMethod
    @Override
    public AddTeacherToSchoolclassPresenter getAddTeacherToSchoolclassPresenter() {
        return addTeacherToSchoolclassPresenter;
    }

    /**
     * @return the copyOrMoveStudentToSchoolclassPresenter
     */
    @JsMethod
    @Override
    public CopyOrMoveStudentToSchoolclassPresenter getCopyOrMoveStudentToSchoolclassPresenter() {
        return copyOrMoveStudentToSchoolclassPresenter;
    }

    /**
     * @return the modulesOfSchoolclassPresenter
     */
    @JsMethod
    @Override
    public ModulesOfSchoolclassPresenter getModulesOfSchoolclassPresenter() {
        return modulesOfSchoolclassPresenter;
    }

    /**
     * @return the modulesPresenter
     */
    @JsMethod
    @Override
    public ModulesPresenter getModulesPresenter() {
        return modulesPresenter;
    }

    @JsMethod
    @Override
    public SelectStudentResultsPresenter getSelectStudentResultsPresenter() {
        return selectStudentResultsPresenter;
    }

    @JsMethod
    @Override
    public StudentScoResultPresenter getStudentScoResultPresenter() {
        return studentScoResultPresenter;
    }

    @JsMethod
    @Override
    public PersonsPresenter getPersonsPresenter() {
        return personsPresenter;
    }

    /**
     * @return the addStudentPresenter
     */
    @JsMethod
    @Override
    public AddPersonPresenter getAddStudentPresenter() {
        return addStudentPresenter;
    }

    /**
     * @return the editPersonPresenter
     */
    @JsMethod
    @Override
    public EditStudentPresenter getEditStudentPresenter() {
        return editStudentPresenter;
    }

    /**
     * @return the editPersonPresenter
     */
    @JsMethod
    @Override
    public EditTeacherPresenter getEditTeacherPresenter() {
        return editTeacherPresenter;
    }

    /**
     * @return the importPersonsPresenter
     */
    @JsMethod
    @Override
    public ImportPersonsPresenter getImportPersonsPresenter() {
        return importPersonsPresenter;
    }

    @Override
    @JsMethod
    public LogResultsPresenter getLogResultsPresenter() {
      return logResultsPresenter;
    }

    @Override
    public void setStage(int stage) {
      getPersonsPresenter().setStage(stage);
      getSelectedResultsPresenter().setStage(stage);
      getResultsPresenter().setStage(stage);
      getLoginPresenter().setStage(stage);
      getMainPresenter().setStage(stage);
    }

    @Override
    public OrganisationPresenter getOrganisationPresenter() {
      return null;
    }

	@Override
	@JsMethod
	public StudentResultsGraphPresenter getResultsGraphPresenter() {
		return studentResultsGraphPresenter;
	}

	@JsMethod
	public StudentModelPresenter getStudentModelPresenter() {
		return studentModelPresenter;
	}

	@JsMethod
	public SMClassResultsPresenter getSMClassResultsPresenter() {
		return smClassResultPresenter;
	}

	@JsMethod
	public SMStudentResultsPresenter getSMResultsPresenter() {
		return smStudentResultsPresenter;
	}
}
