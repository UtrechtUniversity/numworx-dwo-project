package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentLogData;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.LoginDataManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Supplies stats
 *
 * @author Gert van der Plas
 */
@Path("/secure/stats")
public class SecureUserStatsManager {
//    @Context
//    private ServletContext context;    

    private static final Logger LOG = Logger.getLogger(SecureUserStatsManager.class.getName());

    @GET
    @Produces({"application/json"})
    @Path("/loginDataToday")
    public List<PersistentLogData> loginsTodayJSON() {
        //StringBuilder result = new StringBuilder();
        //fetch statistics for servlet wide singleton
        Date today = DwoDateUtilities.getCurrentDwoDate();
        long fromTimeStamp = DwoDateUtilities.getTimeStampForStartOfDay(today);
        long toTimeStamp = DwoDateUtilities.getTimeStampForEndOfDay(today);
        List<PersistentLogData> data = LoginDataManager.findEntities(fromTimeStamp, toTimeStamp);        
//        LoginDataManager.findEntities(fromTimeStamp, toTimeStamp);
        return data;
    }

}
