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
    private String[][] importData;

    /**
     * @return the importData
     */
    public String[][] getImportData() {
        return importData;
    }

    public enum Dialogs {
        NewSchoolClass,
        EditSchoolClass,
        EditStudent,
        EditStudents,
        EditTeachers,
        EditModules,
        LoadStudentFile,
        ImportStudentData
    }

    public static Type<SchoolClassDialogEventHandler> TYPE = new Type<SchoolClassDialogEventHandler>();
    public static Dialogs eventValue;

    public SchoolClassDialogEvent(Dialogs aState) {
        schoolClass=null;
        student = null;
        this.setEventValue(aState);
    }

    public SchoolClassDialogEvent(Dialogs aState, String[][] data) {
        schoolClass=null;
        student = null;
        importData = data;
        this.setEventValue(aState);
    }
    
    public SchoolClassDialogEvent(Dialogs aState, DomSchoolClass aSchoolClass) {
        student = null;
        schoolClass = aSchoolClass;
        this.setEventValue(aState);
    }

    public SchoolClassDialogEvent(Dialogs aState, DomStudent aStudent, DomSchoolClass aSchoolClass) {
        student = aStudent;
        schoolClass = aSchoolClass;
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
        
}
