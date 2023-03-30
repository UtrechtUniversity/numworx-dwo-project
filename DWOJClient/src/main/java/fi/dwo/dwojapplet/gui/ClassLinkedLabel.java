/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.SchoolClass;

/**
 * A Linked Label that represents a SchoolClass.
 *
 * @author M.J.B. Kupers
 *
 */
public class ClassLinkedLabel extends LinkedLabel {

    private SchoolClass schoolClass;

    /**
     * Creates a new Linked Label with the specified SchoolClass.
     *
     * @param c The SchoolClass of the label.
     */
    public ClassLinkedLabel(SchoolClass c) {
        super("-  " + c.getName());
        schoolClass = c;
    }

    /**
     * Returns the SchoolClass of the Label.
     *
     * @return The SchoolClass of the Label.
     */
    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * Sets the SchoolClass of the label.
     *
     * @param schoolClass The schoolClass to set.
     */
    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        super.setText("-  " + schoolClass.getName());
    }
}
