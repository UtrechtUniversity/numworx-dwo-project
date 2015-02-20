package fi.dwo.dwojapplet.domain;

import java.util.Hashtable;
/*
 * Read/write copy 
 */

public class SchoolPasswdMap extends Hashtable {

    public SchoolPasswdMap() {
    }

    public SchoolPasswdMap(School school) {
        this(school.getSchoolGroupList());
    }

    public SchoolPasswdMap(Hashtable h) {
        super(h);
    }

    public SchoolPasswdMap(SchoolGroup[] schoolGroupList) {
        for (int i = 0; i < schoolGroupList.length; i++) {
            SchoolGroup sg = schoolGroupList[i];
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
