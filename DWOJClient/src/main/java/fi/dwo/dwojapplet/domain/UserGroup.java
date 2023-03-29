// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\UserGroup.java
package fi.dwo.dwojapplet.domain;

/**
 * This interface is used for the results overview of teachers.<br>
 * It must be implemented by classes that represents a UserGroup (e.g.
 * SchoolClass, User)<br>
 *
 * @author M.J.B. Kupers
 *
 */
public interface UserGroup {

    /**
     * Returs the name representing the usergroup object
     *
     * @return java.lang.String
     *
     */
    public String getName();

    /**
     * Returns a title representing the UserGroup object.
     *
     * @return A title representing the UserGroup object.
     */
    public String getTitle();

    /**
     * Returns the unique-identifier for the UserGroup object.
     *
     * @return The unique-identifier for the UserGroup object.
     *
     */
    public int getID();

    /**
     * Indicates if this is the deepest UserGroup.
     *
     * @return If this is the deepest UserGroup it returns true. Otherwise it
     * returns false.
     */
    public boolean isDeepestLevel();

    /**
     * Indicates if this is the highest UserGroup.
     *
     * @return If this is the highest UserGroup it returns true. Otherwise it
     * returns false.
     */
    public boolean isHighestLevel();

    /**
     * Returns the name to order the usergroup. For example for the user object,
     * it returns the lastname of the user (instead of the fullname)
     *
     * @return The name to order the usergroup.
     */
    public String getOrderName();

    /**
     * Returns a typename representing the UserGroup. For example for the user
     * object, it returns the string 'Student'.
     *
     * @return A typename representing the UserGroup.
     */
    public String getType();

    /**
     * Returns a title represents the parent item.
     *
     * @return A title represents the parent item.
     */
    public String getParentTitle();

    /**
     * Returns a title represents the child item.
     *
     * @return A title represents the child item.
     */
    public String getChildTitle();

    /**
     * Returns a title represents the Ascending Order item.
     *
     * @return A title represents the Ascending Order item.
     */
    public String getOrderAscTitle();

    /**
     * Returns a title represents the Descending Order item.
     *
     * @return A title represents the Descending Order item.
     */
    public String getOrderDescTitle();

    public String getUsername();
}
