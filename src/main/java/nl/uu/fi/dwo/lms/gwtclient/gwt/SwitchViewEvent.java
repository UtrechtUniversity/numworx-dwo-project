package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.Map;

import org.vectomatic.file.File;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class SwitchViewEvent extends GwtEvent<SwitchViewEventHandler> {

    /**
     * @return the resultStudent
     */
    public DomResultStudent getResultStudent() {
        return resultStudent;
    }

//    /**
//     * @return the resultScoContext
//     */
//    public DomResultScoContext getResultScoContext() {
//        return resultScoContext;
//    }

    /**
     * @return the resultTree
     */
    public DomResultTree getResultTree() {
        return resultTree;
    }

    public enum SelectedView {
        LOGIN,
        WELCOME,
        ACCOUNT,
        PERSONS,
        IMPORTPERSONS,
        ADDPERSON,
        EDITSTUDENT,
        EDITTEACHER,
        SCHOOLCLASSES,
        EDITSCHOOLCLASS,
        ADDSTUDENTTOSCHOOLCLASS,
        COPYORMOVESTUDENTTOSCHOOLCLASS,
        ADDTEACHERTOSCHOOLCLASS,
        EDITCOURSESOFSCHOOLCLASS,
        RESULTS, STUDENTRESULTS,
        SELECTEDRESULTS,
        RESULTSSCHOOLCLASSES,
        SELECTSTUDENTRESULTS,
        RESULTSSTUDENT,
        LOGRESULTS,
        SELECTEDRESULTSRETURN,
        MODULES,
        MODULESVIEW,
        ORGANISATION,
        BACKTORESULTS,
        ARROWUP,
        SEARCH,
        TRAIL,
        GOTO,
        MAYBELOGOUT,
        CLOSING
        // ACTIVERESULTS,
        // SCORESULTS,
        // COURSESOFSCHOOLCLASS,
        // STUDENTSINSCHOOLCLASS,
        // TEACHERSINSCHOOLCLASS,
        
    }

//    private DomStudent student;
    private DomUser user;
    private DomSchoolClass schoolClass;
    private DomResultScoContext scoResult;
    private DomStudentScoContext studentScoResult;
    private DomResultStudent resultStudent;
//    private DomResultScoContext resultScoContext;
    private DomResultTree resultTree;
    private DomResultStudentScoContext resultStudentScoContext;
    private JavaScriptObject moduleIds;
    private Map<String,String> userState;

    public static Type<SwitchViewEventHandler> TYPE = new Type<SwitchViewEventHandler>();
    public static SelectedView eventValue;

    public SwitchViewEvent(SelectedView aState) {
        this.setEventValue(aState);
    }
//
//    public SwitchViewEvent(SelectedView aState, DomStudent aStudent) {
//        this.setEventValue(aState);
//        student = aStudent;
//    }

    public SwitchViewEvent(SelectedView aState, DomUser aUser) {
        this.setEventValue(aState);
        user = aUser;
    }
    
    public SwitchViewEvent(SelectedView aState, DomSchoolClass aSchoolClass) {
        this.setEventValue(aState);
        schoolClass = aSchoolClass;
    }

    public SwitchViewEvent(SelectedView aState, DomResultTree aResultTree) {
        resultTree = aResultTree;
    }

    public SwitchViewEvent(SelectedView aState, DomResultTree aResultTree, JavaScriptObject aModuleIds) {
        this.setEventValue(aState);
        resultTree = aResultTree;
        moduleIds = aModuleIds;
    }
    public SwitchViewEvent(SelectedView aState, JavaScriptObject jso) {
      this.setEventValue(aState);
      moduleIds = jso;
  }

//    public SwitchViewEvent(SelectedView aState, DomResultTree aResultTree,
//            DomResultScoContext aStudentSco, DomResultStudent aResultStudent,
//            DomSchoolClass aSchoolClass) {
//        this.setEventValue(aState);
//        resultTree = aResultTree;
//        resultScoContext = aStudentSco;
//        resultStudent = aResultStudent;
//        schoolClass = aSchoolClass;
//    }

    public SwitchViewEvent(SelectedView resultsstudent, DomResultTree tree, DomResultStudentScoContext value, JavaScriptObject context, Map<String, String> map) {
		this(resultsstudent, tree, context);
		resultStudentScoContext = value;
		userState = map;
	}
    public SwitchViewEvent(SelectedView search, Map<String,String> map) {
      this(search);
      userState = map;
    }

	public SwitchViewEvent(SelectedView logresults, DomResultTree resultTree,
        JavaScriptObject context, DomResultScoContext sco,
        DomSchoolClass schoolclass) {
	  this(logresults, resultTree, context);
	  this.schoolClass = schoolclass;
      this.scoResult = sco;
    }

  @Override
    public Type<SwitchViewEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SwitchViewEventHandler handler) {
        handler.onSwitchViewEvent(this);
    }

    public void setEventValue(SelectedView view) {
        eventValue = view;
    }

    public SelectedView getEventValue() {
        return eventValue;
    }

    /**
     * @return the student
     */
    public DomUser getUser() {
        return user;
    }

//    /**
//     * @param student the student to set
//     */
//    public void setStudent(DomStudent student) {
//        this.student = student;
//    }
    /**
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @return the scoResult
     */
    public DomResultScoContext getScoResult() {
        return scoResult;
    }

    /**
     * @return the studentScoResult
     */
    public DomStudentScoContext getStudentScoResult() {
        return studentScoResult;
    }

    /**
     * @return the moduleIds
     */
    protected JavaScriptObject getResultState() {
        return moduleIds;
    }

    public DomResultStudentScoContext getResultStudentScoContext() {
      return resultStudentScoContext;
    }

    public Map<String,String> getUserState() {
      return userState;
    }
 
    public Map<String,String> getSearch() {
      return userState;
    }
    
    protected File getFile() {
      return moduleIds.cast();
    }
}
