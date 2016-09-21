/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.dom.entities;

import com.owlike.genson.annotation.JsonIgnore;
import fi.dwo.rest.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolRoleAndClassV2 {

    //school
    private DomSchool school;
// Legacy way.    
//    private PersistenceId schoolId;
//    private String schoolName;
//    private String schoolRights;

    //hasRole
    private DomHasRole hasRole;
//    private PersistenceId userId;
//    private PersistenceId schoolGroupId;
//    private String roleRights;

    //role
    private DomRole role;
//    private PersistenceId roleId;
//    private String roleName;

    //schoolclass
    private DomSchoolClass schoolClass;
//    private PersistenceId schoolClassId;
//    private String schoolClassName;
//    private Boolean iconizer;

//    /**
//     * @return the schoolId
//     */
//    @Deprecated
//    @JsonIgnore
//    public PersistenceId getSchoolId() {
//        return getSchool().getId();
//    }
//
//    /**
//     * @param schoolId the schoolId to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolId(PersistenceId schoolId) {
//        getSchool().setId(schoolId);
//    }
//
//    /**
//     * @return the schoolName
//     */
//    @Deprecated
//    @JsonIgnore
//    public String getSchoolName() {
//        return getSchool().getSchoolName();
//    }
//
//    /**
//     * @param schoolName the schoolName to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolName(String schoolName) {
//        getSchool().setSchoolName(schoolName);
//    }
//
//    /**
//     * @return the roleId
//     */
//    @Deprecated
//    @JsonIgnore
//    public PersistenceId getRoleId() {
//        return role.getId();
//    }
//
//    /**
//     * @param roleId the roleId to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setRoleId(PersistenceId roleId) {
//        role.setId(roleId);
//    }
//
//    /**
//     * @return the roleName
//     */
//    @Deprecated
//    @JsonIgnore
//    public String getRoleName() {
//        return role.getRoleName();
//    }
//
//    /**
//     * @param roleName the roleName to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setRoleName(String roleName) {
//        role.setRoleName(roleName);
//    }
//
//    /**
//     * @return the schoolClassId
//     */
//    @Deprecated
//    @JsonIgnore
//    public PersistenceId getSchoolClassId() {
//        return (schoolClass != null) ? schoolClass.getId() : null;
//    }
//
//    /**
//     * @param schoolClassId the schoolClassId to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolClassId(PersistenceId schoolClassId) {
//        schoolClass.setId(schoolClassId);
//    }
//
//    /**
//     * @return the schoolClassName
//     */
//    @Deprecated
//    @JsonIgnore
//    public String getSchoolClassName() {
//        return (schoolClass != null) ? schoolClass.getSchoolClassName() : null;
//    }
//
//    /**
//     * @param schoolClassName the schoolClassName to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolClassName(String schoolClassName) {
//        schoolClass.setSchoolClassName(schoolClassName);
//    }
//
//    /**
//     * @return the userId
//     */
//    @Deprecated
//    @JsonIgnore
//    public PersistenceId getUserId() {
//        return hasRole.getId();
//    }
//
//    /**
//     * @param userId the userId to set
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setUserId(PersistenceId userId) {
//        hasRole.setId(userId);
//    }
//
//    /**
//     * @return the groupId
//     */
//    @Deprecated
//    @JsonIgnore
//    public PersistenceId getSchoolGroupId() {
//        return (hasRole != null) ? hasRole.getSchoolGroupId() : null;
//    }
//
//    /**
//     * @param schoolGroupId
//     */
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolGroupId(PersistenceId schoolGroupId) {
//        hasRole.setSchoolGroupId(schoolGroupId);
//    }
//
//    @Deprecated
//    @JsonIgnore
//    public Boolean getIconizer() {
//        return (schoolClass != null) ? schoolClass.getIconizer() : null;
//    }
//
//    @Deprecated
//    @JsonIgnore
//    public void setIconizer(Boolean iconizer) {
//        schoolClass.setIconizer(iconizer);
//    }
//
//    @Deprecated
//    @JsonIgnore
//    public String getRoleRights() {
//        return (hasRole != null) ? hasRole.getRights() : null;
//    }
//
//    @Deprecated
//    @JsonIgnore
//    public void setRoleRights(String roleRights) {
//        hasRole.setRights(roleRights);
//    }
//
//    @Deprecated
//    public String getSchoolRights() {
//        return (school != null) ? school.getSchoolRights(): null;
//    }
//
//    @Deprecated
//    @JsonIgnore
//    public void setSchoolRights(String schoolRights) {
//        getSchool().setSchoolRights(schoolRights);
//    }

    /**
     * @return the school
     */
    public DomSchool getSchool() {
        return school;
    }

    /**
     * @param school the school to set
     */
    public void setSchool(DomSchool school) {
        this.school = school;
    }

    /**
     * @return the hasRole
     */
    public DomHasRole getHasRole() {
        return hasRole;
    }

    /**
     * @param hasRole the hasRole to set
     */
    public void setHasRole(DomHasRole hasRole) {
        this.hasRole = hasRole;
    }

    /**
     * @return the role
     */
    public DomRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(DomRole role) {
        this.role = role;
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

}
