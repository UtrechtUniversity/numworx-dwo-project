package fi.dwo.dwojapplet.domain;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

public class SchoolAdmin extends Teacher {

    public SchoolAdmin() {
        
    }

    /**
     * Een schooladmin kan altijd meer dan een docent.
     * @return 
     */
    @Override
    public boolean hasRight(char right) {
        switch (right) {
            case MODIFY_MODULES_RIGHT:
            case CHANGE_CLASS_RIGHT:
            case CHANGE_CLASS_RIGHT_TEACHER:
                return true;
            default:
                return super.hasRight(right);

        }
    }

    @Override
    public void setSchoolRoleAndClass(DomSchoolsRolesAndClassesV2 dom) {
      superSetSchoolRoleAndClass(dom);
      setClasses(null);
    }

}
