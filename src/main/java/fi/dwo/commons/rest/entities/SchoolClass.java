/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class SchoolClass {
    private PersistenceId schoolClassId;
    private PersistenceId schoolId;
    private String schoolClassName;
    private int iconizer;

    /**
     * @return the schoolClassId
     */
    public PersistenceId getSchoolClassId() {
        return schoolClassId;
    }

    /**
     * @param schoolClassId the schoolClassId to set
     */
    public void setSchoolClassId(PersistenceId schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    /**
     * @return the schoolId
     */
    public PersistenceId getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(PersistenceId schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return the schoolClassName
     */
    public String getSchoolClassName() {
        return schoolClassName;
    }

    /**
     * @param schoolClassName the schoolClassName to set
     */
    public void setSchoolClassName(String schoolClassName) {
        this.schoolClassName = schoolClassName;
    }

    /**
     * @return the iconizer
     */
    public int getIconizer() {
        return iconizer;
    }

    /**
     * @param iconizer the iconizer to set
     */
    public void setIconizer(int iconizer) {
        this.iconizer = iconizer;
    }
    
    
}
