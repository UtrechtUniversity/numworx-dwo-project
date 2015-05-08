/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class SchoolRoleAndClass {
    private int schoolId;
    private String schoolName;
    private int roleId;
    private String roleName;
    private int schoolClassId;
    private String schoolClassName;
    
    /**
     * @return the schoolId
     */
    public int getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(int schoolId) {
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
    public int getRoleId() {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(int roleId) {
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
    public int getSchoolClassId() {
        return schoolClassId;
    }

    /**
     * @param schoolClassId the schoolClassId to set
     */
    public void setSchoolClassId(int schoolClassId) {
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
