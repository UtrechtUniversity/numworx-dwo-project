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
public class RestSchoolsRolesAndClasses {
    private RestSchoolRoleAndClass currentSchoolRoleAndClass;
    private List<RestSchoolRoleAndClass> schoolsRolesAndClassesList;

    public void init(){
        currentSchoolRoleAndClass = new RestSchoolRoleAndClass();
        schoolsRolesAndClassesList = new ArrayList<RestSchoolRoleAndClass>();
    }
    /**
     * @return the currentSchoolRoleAndClass
     */
    public RestSchoolRoleAndClass getActiveSchoolRoleAndClass() {
        return currentSchoolRoleAndClass;
    }

    /**
     * @param currentSRC the currentSchoolRoleAndClass to set
     */
    public void setActiveSchoolRoleAndClass(RestSchoolRoleAndClass currentSRC) {
        this.currentSchoolRoleAndClass = currentSRC;
    }

    /**
     * @return the schoolsRolesAndClassesList
     */
    public List<RestSchoolRoleAndClass> getSchoolsRolesAndClassesList() {
        return schoolsRolesAndClassesList;
    }

    /**
     * @param srcList the schoolsRolesAndClassesList to set
     */
    public void setSchoolsRolesAndClassesList(List<RestSchoolRoleAndClass> srcList) {
        this.schoolsRolesAndClassesList = srcList;
    }
    
}
