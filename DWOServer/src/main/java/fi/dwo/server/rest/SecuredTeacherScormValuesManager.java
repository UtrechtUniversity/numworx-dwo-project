package fi.dwo.server.rest;

import java.sql.Date;
import java.sql.Time;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
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
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.UEscape;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.ScoPageUtilManager;
import fi.dwo.server.persistence.CmiConvert;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

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
    	UserState_HR_R_S_SG_U hstate = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(domHasRole, RoleType.TEACHER);
    	try {
    		user = hstate.getUser();
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}
        PersistentHasRole phr = hstate.getHasRole();
// FIXED check if domHasRole is a teacher of same school as pssc
        // ...
        Long id = MySQLPersistenceId.getNativeId(ssc);
		List<DomMapEntry<String, String>> entryList = rest.getDomTeacherScormValues().getValues();
		PersistentStudentScoContext pssc = StudentScoContextManager.findEntity(id);
		Long sgid = pssc.getPersistentHasRolePK().getSchoolGroupID();
		PersistentSchoolGroup sg = SchoolGroupManager.findEntity(sgid);
		if (Objects.equals(hstate.getSchool(), sg.getSchool())) {
		
			SecuredUserScoDataManager.getScormValues(entryList, pssc);
		}
		return rest.getDomTeacherScormValues();
		
    }

    @PUT
    @Produces({"application/json"})
    @Path("/set")
    public DomStudentScoContext set(@Context SecurityContext sc, RestTeacherScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	DomStudentScoContext ssc = rest.getDomTeacherScormValues().getStudentScoContext();
// Context
    	UserState_HR_R_S_SG_U hstate = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(domHasRole, RoleType.TEACHER);
    	PersistentUser user = null;
    	try {
    		user = hstate.getUser();
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
        PersistentHasRole phr = hstate.getHasRole();
// its own hasrole!
        Long u1 = phr.getPersistentHasRolePK().getUserID();
        Long u2 = user.getId();
        if (! u1.equals(u2)) {
        	LOG.log(Level.SEVERE,"Wrong hasrole for Username " + sc.getUserPrincipal().getName() );
        	throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "");
        }        
// FIXED check if domHasRole is TEACHER  of same school as pssc
        PersistentSchool school = hstate.getSchool();
		PersistentSchoolGroup studentSchoolGroup = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
        Long studentSchoolGroupID = studentSchoolGroup.getSchoolGroupID();
        Long sg2 = phr.getSchoolGroup().getSchoolGroupID();
        if ( studentSchoolGroupID.equals(sg2)) {
        	LOG.log(Level.SEVERE, "no student allowed " +sc.getUserPrincipal().getName() );
        	throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "");
        }
        PersistentStudentScoContext pssc;
        if(ssc.getId() != null) {
        	Long id = MySQLPersistenceId.getNativeId(ssc); // NPE
        	pssc = StudentScoContextManager.findEntity(id);
        } else {
        	pssc = null;
        }
// FIXME Race condition
		if(pssc == null) {
// A teacher can create studentscocontext of it own school and for students
// create 
			
			pssc = new PersistentStudentScoContext();
			long now = System.currentTimeMillis();
			pssc.setCreateDate(new Date(now));
			pssc.setCreateTime(new Time(now));
			
// XXX get Native ID from other persistenceId's
			Long scoId = getSingleNativeId(ssc.getScoID().getIdString(), PersistenceClassType.PersistentScoContext);
			Long studentUserID = getSingleNativeId(ssc.getUserID().getIdString(), PersistenceClassType.PersistentUser);
		
			PersistentScoContext scoContext = ScoContextManager.findEntity(scoId);
// TODO NPE if not exists, should be dwo2exception
			scoId = scoContext.getScoID();
			pssc.setScoID(scoId);						
			PersistentHasRole studentRole = HasRoleUtilManager.getHasRole(studentUserID, RoleType.STUDENT, school);
			pssc.setPersistentHasRolePK(studentRole.getPersistentHasRolePK());
// init all.
			pssc.setLocation("");
			pssc.setCompletionStatus("");
			pssc.setSessionTime("");
			pssc.setTotalTime("");
			
			try {
				StudentScoContextManager.create(pssc);
			} catch (PersistenceException e) {
				String msg = MessageFormat.format("could not create studentscocontext for hasrole {0}, sco {1}", new Object[] {studentRole.getPersistentHasRolePK(), scoId });
				LOG.log(Level.SEVERE, msg, e);
				throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentScoExists, msg);
			}

		} else {
			Long schoolGroupID = pssc.getPersistentHasRolePK().getSchoolGroupID();
			if( ! schoolGroupID .equals ( studentSchoolGroup.getSchoolGroupID())
					  )
			{
				LOG.severe("Security: set wrong schoolgroup by "+ sc.getUserPrincipal().getName());
				throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "");
			}
		}

		
		
		PersistentStudentScoData pssd = null;
		Scorm2Xml xml = null;
		List<DomMapEntry<String, String>> entryList = rest.getDomTeacherScormValues().getValues();

		for(DomMapEntry<String,String> entry: entryList) {
			logEntry("setTeacher "+sc.getUserPrincipal().getName(), entry, pssc.getPersistentHasRolePK().getUserID(), pssc.getScoID());
			ScormKey key = ScormKey.getKey(entry.getKey());
			String value = entry.getValue();
			switch(key) {
			case SCORE: 
				pssc.setScore(Float.parseFloat(value));break;
			case LOCATION:
				pssc.setLocation(value);break;
			case COMPLETION_STATUS:
	        	if(xml == null) {
	                if(pssd == null) {
	                    pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
	                    if(pssd == null) {
	                        pssd = new PersistentStudentScoData(pssc.getStudentSco());
	                        pssd.setSuspendData("");
	                        pssd.setCocd(Scorm2Xml.EMPTY_DOC);
	                        StudentScoDataManager.create(pssd);
	                    }
	                }
	                String xmlStr = pssd.getCocd();
	                xml = new Scorm2Xml(String.valueOf(xmlStr));
	            }
	        	if (SecuredCommonScoDataManager.COMPLETE.equals(value) ) {
	        		if (!SecuredCommonScoDataManager.COMPLETE.equals(pssc.getCompletionStatus())) {
						java.util.Date now = new java.util.Date(Math.max(pssc.getLastChangeTimeStamp(), pssd.getLastChangeTimeStamp()));
						xml.LMSSetValue("cmi.comments_from_lms.0.timestamp", new SimpleDateFormat(RestyDateTimeFormat.RESTY_DATETIME_FORMAT).format(now));
					}
	        	} else {
	        		xml.LMSSetValue("cmi.comments_from_lms.0.timestamp", ""); // or remove?
	        	}

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
				if ("cmi.comments_from_lms.0.comment".equals(entry.getKey())) {
					ScoPageUtilManager.updateDocentCorrectie(pssc, value);
				} else
				if ("cmi.comments_from_lms.2.comment".equals(entry.getKey())) {
					ScoPageUtilManager.updateDocentCorrect(pssc, value);
				}
				break;
			case SESSION_TIME:
				pssc.setSessionTime(value);
				break;
			case SESSION_TIME2004:
				pssc.setSessionTime(CMI.to1_2Timex(CMI.from2004Time(value)));
				break;
			case SUSPEND_DATA:
				value = UEscape.convertUEsc(value);
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
			case STUDENT_MODEL:
			case COMPLETION_TIMESTAMP:
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

	private static void logEntry(String pfx, DomMapEntry<String, String> entry, Long userid, Long scoid) {
		LOG.log(Level.INFO, "{0} u:{3}, s:{4}, k:{1}, v:{2}", new Object[] { pfx, entry.getKey(), shrink(entry.getValue()), String.valueOf(userid), String.valueOf(scoid)} );
	}


	private static final char ELLIPSIS = '\u2026';
	private static String shrink(String string) {	  
    if (string == null || string.length() <= 20) return string;
    return string.substring(0,19) + ELLIPSIS;
  }

}
