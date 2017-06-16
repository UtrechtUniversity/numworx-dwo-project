package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.GwtEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

/**
 * GWTEvent that notifies of a login action.
 *
 * @author Gert van der Plas
 */
public class SchoolClassDialogEvent extends GwtEvent<SchoolClassDialogEventHandler> {
    private DomSchoolClass schoolClass;

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
    
    public enum Dialogs {
        NewSchoolClass,
        EditSchoolClass
    }

    public static Type<SchoolClassDialogEventHandler> TYPE = new Type<SchoolClassDialogEventHandler>();
    public static Dialogs eventValue;

    public SchoolClassDialogEvent(Dialogs aState, DomSchoolClass aSchoolClass) {
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
}
