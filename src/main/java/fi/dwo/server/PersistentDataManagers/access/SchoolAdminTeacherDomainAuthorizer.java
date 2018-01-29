package fi.dwo.server.PersistentDataManagers.access;

import java.util.logging.Logger;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class
 * forms a state machine where the interfaces denote the possible transitions
 * (edges in a directed graph). Thus a regular language for the security access
 * can be built.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolAdminTeacherDomainAuthorizer extends UserDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(SchoolAdminTeacherDomainAuthorizer.class.getName());
    
    public class SchoolAdminTeacherPersistentContext extends UserPersistentContext{
    }
    
    protected SchoolAdminTeacherDomainAuthorizer(){
        super();    
    }
}
