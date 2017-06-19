package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.event.shared.GwtEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class SwitchViewEvent extends GwtEvent<SwitchViewEventHandler> {

    public enum SelectedView {
        LOGIN,
        ACCOUNT,
        SWITCHSCHOOL,
        RESULTS,
        SCORESULTS,
        SCHOOLCLASSES,
        STUDENTSINSCHOOLCLASS,
        TEACHERSINSCHOOLCLASS
    }
    
    private DomStudent student;
    private DomSchoolClass schoolClass;
    private DomResultScoContext scoResult;
    private DomStudentScoContext studentScoResult;
    
    public static Type<SwitchViewEventHandler> TYPE = new Type<SwitchViewEventHandler>();
    public static SelectedView eventValue;

    public SwitchViewEvent(SelectedView aState){
        this.setEventValue(aState);
    }

    public SwitchViewEvent(SelectedView aState, DomStudent aStudent){
        this.setEventValue(aState);
        student = aStudent;
    }
    
    public SwitchViewEvent(SelectedView aState, DomSchoolClass aSchoolClass){
        this.setEventValue(aState);
        schoolClass = aSchoolClass;
    }

    @Override
    public Type<SwitchViewEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SwitchViewEventHandler handler) {
        handler.onSwitchViewEvent(this);
    }
    
    public void setEventValue(SelectedView view){
        eventValue = view;
    }
    public SelectedView getEventValue(){
        return eventValue;
    }

    /**
     * @return the student
     */
    public DomStudent getStudent() {
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(DomStudent student) {
        this.student = student;
    }

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
    }}
