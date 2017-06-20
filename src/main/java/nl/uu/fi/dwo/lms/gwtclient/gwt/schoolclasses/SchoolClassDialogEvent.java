package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.GwtEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class SchoolClassDialogEvent extends GwtEvent<SchoolClassDialogEventHandler> {
    private DomSchoolClass schoolClass;
    private DomStudent student;

    /**
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
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
    
    public enum Dialogs {
        NewSchoolClass,
        EditSchoolClass,
        EditStudent,
        EditStudents,
        EditTeachers,
        EditModules
    }

    public static Type<SchoolClassDialogEventHandler> TYPE = new Type<SchoolClassDialogEventHandler>();
    public static Dialogs eventValue;

    public SchoolClassDialogEvent(Dialogs aState, DomSchoolClass aSchoolClass) {
        schoolClass = aSchoolClass;
        this.setEventValue(aState);
    }

    public SchoolClassDialogEvent(Dialogs aState, DomStudent aStudent) {
        student = aStudent;
        this.setEventValue(aState);
    }

    @Override
    public Type<SchoolClassDialogEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SchoolClassDialogEventHandler handler) {
        handler.onDialogEvent(this);
    }

    public void setEventValue(Dialogs view) {
        eventValue = view;
    }

    public Dialogs getEventValue() {
        return eventValue;
    }
}
