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
public class SchoolRoleAndClass {
    private PersistenceId schoolId;
    private String schoolName;
    private PersistenceId roleId;
    private String roleName;
    private PersistenceId schoolClassId;
    private String schoolClassName;
    
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
     * @return the schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * @return the roleId
     */
    public PersistenceId getRoleId() {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(PersistenceId roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

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
    
}
