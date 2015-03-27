// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Group.java
package fi.dwo.dwojapplet.domain;

/**
 * This class is responsible for the Group data.<br>
 * A group is the possible usergroup of the user (e.g. Teacher or Student).<br>
 *
 * @author M.J.B. Kupers
 *
 */
public class Group {

    private int groupID;

    private String name;

    /**
     * Creates a new instance of a Group Object.
     *
     */
    public Group() {

    }

    /**
     * Returns the current GroupID
     *
     * @return The groupID.
     */
    public int getGroupID() {
        return groupID;
    }

    /**
     * Sets the groupID
     *
     * @param groupID The groupID to set.
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    /**
     * Returns the group-name.
     *
     * @return The group-name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the group-name
     *
     * @param name The group-name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
