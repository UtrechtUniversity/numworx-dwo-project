/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools, roles and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class SchoolsRolesAndClasses {
    private SchoolRoleAndClass currentSchoolRoleAndClass;
    private List<SchoolRoleAndClass> schoolsRolesAndClassesList;

    public void init(){
        currentSchoolRoleAndClass = new SchoolRoleAndClass();
        schoolsRolesAndClassesList = new ArrayList<SchoolRoleAndClass>();
    }
    /**
     * @return the currentSchoolRoleAndClass
     */
    public SchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return currentSchoolRoleAndClass;
    }

    /**
     * @param currentSRC the currentSchoolRoleAndClass to set
     */
    public void setCurrentSchoolRoleAndClass(SchoolRoleAndClass currentSRC) {
        this.currentSchoolRoleAndClass = currentSRC;
    }

    /**
     * @return the schoolsRolesAndClassesList
     */
    public List<SchoolRoleAndClass> getSchoolsRolesAndClassesList() {
        return schoolsRolesAndClassesList;
    }

    /**
     * @param srcList the schoolsRolesAndClassesList to set
     */
    public void setSchoolsRolesAndClassesList(List<SchoolRoleAndClass> srcList) {
        this.schoolsRolesAndClassesList = srcList;
    }
    
}
