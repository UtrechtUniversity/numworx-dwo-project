/*
 * Created on Jun 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.client.domain;

import fi.dwo.client.system.TextMapper;

/**
 * Singleton Guest user. Goed voor implementatie van het null-pattern.
 * @author wim
 */

public class Guest extends User
{

    private static User _instance;

    protected Guest()
    {
        super();
    }
    
    static public User instance()
    {
        if ( _instance == null)
            _instance = new Guest();
        return _instance;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.domain.User#getName()
     */
    public String getName()
    {
         return TextMapper.getText(TextMapper.USER_GUEST);
    }

    /**
     * Guest user is locked in.
     */
/*    public boolean canLogout()
    {
        return false;
    }
 */   /**
     * Guest user is readonly.
     */
    public boolean isReadonly()
    {
        return true;
    }
    
}
