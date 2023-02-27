/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools, roles and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolsRolesAndClassesV2 {
    private DomSchoolRoleAndClassV2 currentSchoolRoleAndClass;
    private DomSchool nullSchool;
    private List<DomSchoolRoleAndClassV2> schoolsRolesAndClassesList;

    public void init(){
        currentSchoolRoleAndClass = new DomSchoolRoleAndClassV2();
        schoolsRolesAndClassesList = new ArrayList<DomSchoolRoleAndClassV2>();
    }
    /**
     * Returns the ActiveSchoolRoleAndClass stored in the Persistent Datastore.
     * 
     * @return the currentSchoolRoleAndClass
     */
    public DomSchoolRoleAndClassV2 getActiveSchoolRoleAndClass() {
        return currentSchoolRoleAndClass;
    }

    /**
     * Sets the ActiveSchoolRoleAndClass stored in the Persistent Datastore.
     * 
     * @param currentSRC the currentSchoolRoleAndClass to set
     */
    public void setActiveSchoolRoleAndClass(DomSchoolRoleAndClassV2 currentSRC) {
        this.currentSchoolRoleAndClass = currentSRC;
    }

    /**
     * @return the schoolsRolesAndClassesList
     */
    public List<DomSchoolRoleAndClassV2> getSchoolsRolesAndClassesList() {
        return schoolsRolesAndClassesList;
    }

    /**
     * @param srcList the schoolsRolesAndClassesList to set
     */
    public void setSchoolsRolesAndClassesList(List<DomSchoolRoleAndClassV2> srcList) {
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
