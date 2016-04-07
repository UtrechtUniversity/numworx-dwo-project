/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools, roles and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolsRolesAndClasses {
    private DomSchoolRoleAndClass currentSchoolRoleAndClass;
    private DomSchool nullSchool;
    private List<DomSchoolRoleAndClass> schoolsRolesAndClassesList;

    public void init(){
        currentSchoolRoleAndClass = new DomSchoolRoleAndClass();
        schoolsRolesAndClassesList = new ArrayList<DomSchoolRoleAndClass>();
    }
    /**
     * @return the currentSchoolRoleAndClass
     */
    public DomSchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return currentSchoolRoleAndClass;
    }

    /**
     * @param currentSRC the currentSchoolRoleAndClass to set
     */
    public void setActiveSchoolRoleAndClass(DomSchoolRoleAndClass currentSRC) {
        this.currentSchoolRoleAndClass = currentSRC;
    }

    /**
     * @return the schoolsRolesAndClassesList
     */
    public List<DomSchoolRoleAndClass> getSchoolsRolesAndClassesList() {
        return schoolsRolesAndClassesList;
    }

    /**
     * @param srcList the schoolsRolesAndClassesList to set
     */
    public void setSchoolsRolesAndClassesList(List<DomSchoolRoleAndClass> srcList) {
        this.schoolsRolesAndClassesList = srcList;
    }

    /**
     * @return the nullSchool
     */
    public DomSchool getNullSchool() {
        return nullSchool;
    }

    /**
     * @param nullSchool the nullSchool to set
     */
    public void setNullSchool(DomSchool nullSchool) {
        this.nullSchool = nullSchool;
    }
    
}
