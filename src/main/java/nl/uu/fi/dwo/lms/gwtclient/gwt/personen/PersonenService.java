package nl.uu.fi.dwo.lms.gwtclient.gwt.personen;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;


/**
 *
 * @author G.A.J. van der Plas
 */
public class PersonenService {
    
    private static final Logger LOG = Logger.getLogger(PersonenService.class.getName());

    private SecuredUserAccountManager manager = new SecuredUserAccountManager();

    private final DwoGlobalVars dwoGlobalVars;
    
    public PersonenService(DwoGlobalVars aDwoGlobalVars){
        dwoGlobalVars = aDwoGlobalVars; // for future use (hasRole fetch i.e.)
    }

 }
