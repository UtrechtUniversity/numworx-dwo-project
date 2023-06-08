package fi.dwo.dwojapplet.domain;

import java.util.Hashtable;
/*
 * Read/write copy 
 */
import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

public class SchoolPasswdMap extends Hashtable<String,String> {

    public SchoolPasswdMap() {
    }

//    public SchoolPasswdMap(School school) {
//        this(school.getSchoolGroupList());
//    }
    
    public SchoolPasswdMap(DomSchoolFull dom) {
      List<DomMapEntry<RoleType, String>> list = dom.getPasswords();
      for(DomMapEntry<RoleType, String> item: list) {
        setPasswd(item.getKey().ordinal(), item.getValue());
      }
    }

    public SchoolPasswdMap(Hashtable h) {
        super(h);
    }

    public SchoolPasswdMap(SchoolGroup[] schoolGroupList) {
        for (SchoolGroup sg : schoolGroupList) {
            put(Integer.toString(sg.getGroupID()), sg.getPasswd());
        }
    }

    public String getPasswd(int groupID) {
        String result = (String) get(Integer.toString(groupID));
        if (result == null) {
            return "";
        }
        return result;
    }

    public void setPasswd(int groupID, String passwd) {
        if (passwd == null) {
            passwd = "";
        }
        put(Integer.toString(groupID), passwd);
    }

}
