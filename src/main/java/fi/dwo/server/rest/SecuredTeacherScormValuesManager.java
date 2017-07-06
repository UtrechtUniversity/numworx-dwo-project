package fi.dwo.server.rest;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.beans.scorm2xml.Scorm2Xml;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.persistence.CmiConvert;
import fi.dwo.server.persistence.DbAccess;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@PermitAll
@Path("/secure/teacher/scormValues")
public class SecuredTeacherScormValuesManager {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherScormValuesManager.class.getName());

    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomTeacherScormValues get(@Context SecurityContext sc, RestTeacherScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	DomStudentScoContext ssc = rest.getDomTeacherScormValues().getStudentScoContext();
    	// Context
    	PersistentUser user = null;
    	try {
    		user = UserManager.findByUserName(sc.getUserPrincipal().getName());
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
        PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// TODO check if domHasRole is a teacher of same school as pssc
        // ...
        Long id = MySQLPersistenceId.getNativeId(ssc);
		List<DomMapEntry<String, String>> entryList = rest.getDomTeacherScormValues().getValues();
		PersistentStudentScoContext pssc = StudentScoContextManager.findEntity(id);
		SecuredUserScoDataManager.getScormValues(entryList, pssc);
    	return rest.getDomTeacherScormValues();
    }

    @PUT
    @Produces({"application/json"})
    @Path("/set")
    public DomStudentScoContext set(@Context SecurityContext sc, RestTeacherScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	DomStudentScoContext ssc = rest.getDomTeacherScormValues().getStudentScoContext();
// Context
    	PersistentUser user = null;
    	try {
    		user = UserManager.findByUserName(sc.getUserPrincipal().getName());
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
        PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// TODO check if domHasRole is a teacher of same school as pssc
        // ...
        PersistentStudentScoContext pssc;
        if(ssc.getId() != null) {
        	Long id = MySQLPersistenceId.getNativeId(ssc); // NPE
        	pssc = StudentScoContextManager.findEntity(id);
        } else {
        	pssc = null;
        }
// FIXME Race condition
		if(pssc == null) {
			pssc = new PersistentStudentScoContext();
			long now = System.currentTimeMillis();
			pssc.setCreateDate(new Date(now));
			pssc.setCreateTime(new Time(now));
			
// XXX get Native ID from other persistenceId's
			Long scoId = getSingleNativeId(ssc.getScoID().getIdString(), PersistenceClassType.PersistentScoContext);
			PersistentScoContext scoContext = ScoContextManager.findEntity(scoId);
// NPE if not exists
			scoId = scoContext.getScoID();
			pssc.setScoID(scoId);			
/// XXX DomStudentScoContext should have HASROLE persistenceID
			PersistentHasRolePK studentRoleKey = idOf(ssc.getUserID(), ssc.getSchoolGroupID());
			PersistentHasRole   studentRole = HasRoleManager.findEntity(studentRoleKey);
// NPE if not exists
			studentRoleKey = studentRole.getPersistentHasRolePK();
			pssc.setPersistentHasRolePK(studentRoleKey);
// init all.
			pssc.setLocation("");
			pssc.setCompletionStatus("");
			pssc.setSessionTime("");
			pssc.setTotalTime("");
			
			StudentScoContextManager.create(pssc);

		}
		PersistentStudentScoData pssd = null;
		Scorm2Xml xml = null;
		List<DomMapEntry<String, String>> entryList = rest.getDomTeacherScormValues().getValues();

		for(DomMapEntry<String,String> entry: entryList) {
			ScormKey key = ScormKey.getKey(entry.getKey());
			String value = entry.getValue();
			switch(key) {
			case SCORE: 
				pssc.setScore(Float.parseFloat(value));break;
			case LOCATION:
				pssc.setLocation(value);break;
			case COMPLETION_STATUS:
				pssc.setCompletionStatus(value);break;
			case COCD:
				if(xml == null) {
					if(pssd == null) {
						pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
						if(pssd == null) {
							pssd = new PersistentStudentScoData(pssc.getStudentSco());
							pssd.setSuspendData("");
							pssd.setCocd("");
							StudentScoDataManager.create(pssd);
						}
					}
					String xmlStr = pssd.getCocd();
	                xml = new Scorm2Xml(String.valueOf(xmlStr));
				}
				xml.LMSSetValue(entry.getKey(), value);
				break;
			case SESSION_TIME:
				pssc.setSessionTime(value);
				break;
			case SESSION_TIME2004:
				pssc.setSessionTime(CMI.to1_2Timex(CMI.from2004Time(value)));
				break;
			case SUSPEND_DATA:
				value = DbAccess.convertUEsc(value);
				if(pssd == null) {
					pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
					if(pssd == null) {
						pssd = new PersistentStudentScoData(pssc.getStudentSco(), value);
						pssd.setSuspendData(value);
						pssd.setCocd("");
						StudentScoDataManager.create(pssd);
					} else {
						pssd.setSuspendData(value);
					}
				} else {
					pssd.setSuspendData(value);
				}
				break;
			case TOTAL_TIME:
				pssc.setTotalTime(value);
				break;
			case TOTAL_TIME2004:
				pssc.setTotalTime(CMI.to1_2Timex(CMI.from2004Time(value)));
				break;
			case XML:
				break;
			}
		}
		try {
			if (xml != null) {
				pssd.setCocd(xml.toString());
			}
			if (pssd != null) {
				StudentScoDataManager.edit(pssd);
			}
			pssc = StudentScoContextManager.edit(pssc);
		} catch (PersistenceException ex) {
            LOG.log(Level.WARNING, "User {0} could not update studentscocontext {1}.", new Object[]{sc.getUserPrincipal().getName(), pssc.getStudentSco()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update studentscocontext " + sc.getUserPrincipal().getName() + ".");
		}
    	return pssc.buildDomStudentScoContext();
    }

// FIXME Embed into MysqlNative
    private static Long getSingleNativeId(String id, PersistenceClassType type) throws Dwo2Exception {
        String[] strList = id.split(";");
        if (!strList[1].equals(type.name())) {
            throw new Dwo2Exception(Dwo2ExceptionCode.PersistentId_ConversionError, "Not a valid PersistenceClassType string.");
        }
        return Long.valueOf(strList[2]);
    }
    
	private static PersistentHasRolePK idOf(PersistenceId userID,
			PersistenceId schoolGroupID) throws Dwo2Exception {
		Long u = getSingleNativeId(userID.getIdString(), PersistenceClassType.PersistentUser);
		Long sg = getSingleNativeId(schoolGroupID.getIdString(), PersistenceClassType.PersistentSchoolGroup);
		return new PersistentHasRolePK(u, sg);
	}

    static private final CmiConvert CMI = new CmiConvert(); // utility class

	
}
