package fi.dwo.server.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.persistence.PersistenceException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm2xml.Scorm2Xml;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.persistence.CmiConvert;
import fi.dwo.server.persistence.DbAccess;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Path("/secure/user/scoData")
public class SecuredUserScoDataManager {
    private static final Logger LOG = Logger.getLogger(SecuredUserScoDataManager.class.getName());
	private static final String COMPLETE = "complete";

    @PUT
    @Produces({"application/json"})    
    @Path("/getJSONLaunchDataBytes")
    public String getJSONLaunchDataBytes(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
// Context
    	PersistentUser user = null;
    	try {
    		user = UserManager.findByUserName(sc.getUserPrincipal().getName());
    		LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
    	}

        Long scoId = MySQLPersistenceId.getNativeId(rest.getDomScoContext());
        Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
		PersistentScoData scoData = ScoDataManager.findEntity(scoId);
        if(scoData == null) {
            LOG.log(Level.WARNING, "not found " + sc.getUserPrincipal().getName() );		
            return "{}"; // Not found, not fatal
        }
        PersistentScoContext scoContext = ScoContextManager.findEntity(scoId);
        Long courseID = scoContext.getCourseID();
        PersistentCourse     course = CourseManager.findEntity(courseID);
        if(course.getDwoProfileID().longValue() != profileID.longValue())
        {
            LOG.log(Level.WARNING, "profile mismatch " + sc.getUserPrincipal().getName() );		
        	//return "{}";
        }
        Long schoolID = course.getSchoolID();
        if(schoolID != null) {
			DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
			PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
            PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// userid must match
         		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
         		{
    	            LOG.log(Level.SEVERE, "user mismatch " + sc.getUserPrincipal().getName() );		
        			//return "{}";
         		}
// schoolID must match
         	if (phr.getSchoolGroup().getSchoolID() != schoolID.longValue())
         	{
	            LOG.log(Level.SEVERE, "school mismatch " + sc.getUserPrincipal().getName() );		
        		//return "{}";       	
         	}
        }
        
        byte[] launchData = scoData.getLaunchdatabytes();
        if(launchData != null)
        {  
        	byte[] buffer = new byte[1024];	
            try {
                ByteArrayInputStream inStream = new ByteArrayInputStream(launchData);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream(launchData.length);
                GZIPInputStream gzIn = new GZIPInputStream(inStream);

                int len;
                while ((len = gzIn.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }

                gzIn.close();
                outStream.close();
                
                return outStream.toString("UTF-8");
            }
            catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error while unzipping launchdata with scoid " + scoId + ".", ex);
            }
        }
//The slow conversion, if bytes are missing.     
        try {
            Hashtable<?, ?> map = (Hashtable<?, ?>) StringCodeObject.decodeStringToObject(scoData.getLaunchdata(), null);
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME zie DWOmAccess voor loader with wiskopdr.jar
	        return writer.toString();
        } catch(Exception ex) {
        	LOG.log(Level.SEVERE, "Error while decoding launchdata with scoid " + scoId + ".", ex);
        }
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Error with launchdata with scoid " + scoId + ".");
    }

    static private final CmiConvert CMI = new CmiConvert(); // utility class

    @PUT
    @Produces({"application/json"})
    @Path("/getValues")
    public DomScormValues getValues(@Context SecurityContext sc, RestScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	
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
// userid must match
 		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
 		{
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Hasrole mismatch");
    		throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");			
 		}
     	Long scocontextid = MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext());
     	LOG.log(Level.INFO, "getValues " + sc.getUserPrincipal().getName() + " " + scocontextid);
		PersistentScoContext scoContext = ScoContextManager.findEntity(scocontextid);
		List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
		if(list.isEmpty()) {
			return rest.getDomScormValues();
		}
		List<DomMapEntry<String, String>> entryList = rest.getDomScormValues().getValues();
		PersistentStudentScoContext pssc = list.get(0);
		getScormValues(entryList, pssc);

    	DomScormValues domScormValues = rest.getDomScormValues();
     	LOG.log(Level.INFO, "getValues done " + sc.getUserPrincipal().getName() + " " + scocontextid);
		return domScormValues;
    }


	static void getScormValues(List<DomMapEntry<String, String>> entryList,
			PersistentStudentScoContext pssc) {
		if (pssc == null) {
			return;
		}
		
		PersistentStudentScoData pssd = null;
		Scorm2Xml xml = null;
		for(DomMapEntry<String,String> entry: entryList) {
			ScormKey key = ScormKey.getKey(entry.getKey());
			switch(key) {
			case SCORE: 
				entry.setValue(String.valueOf(pssc.getScore())); break;
			case LOCATION:
				String location = pssc.getLocation();
				if(location == null) {
					if(pssd == null) {
						pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
						if(pssd == null) 
							pssd = new PersistentStudentScoData();
					}
					if(xml == null) {
						xml = new Scorm2Xml(String.valueOf(pssd.getCocd()));
					}
					entry.setValue(xml.LMSGetValue(entry.getKey()));
				} else
					entry.setValue(String.valueOf(location));break;
			case COMPLETION_STATUS:
				String completionStatus = pssc.getCompletionStatus();
				if(completionStatus == null) {
					if(pssd == null) {
						pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
						if(pssd == null) 
							pssd = new PersistentStudentScoData();
					}
					if(xml == null) {
						xml = new Scorm2Xml(String.valueOf(pssd.getCocd()));
					}
					entry.setValue(xml.LMSGetValue(entry.getKey()));
				} else
					entry.setValue(String.valueOf(completionStatus)); break;
			case SESSION_TIME:
				entry.setValue(String.valueOf(pssc.getSessionTime()));break;
			case SESSION_TIME2004:
				String sessionTime = pssc.getSessionTime();
				if(sessionTime == null) sessionTime = "0";
				entry.setValue(CMI.to2004Timex(CMI.from1_2Timex(sessionTime)));break;
			case TOTAL_TIME:
				entry.setValue(String.valueOf(pssc.getTotalTime()));break;
			case TOTAL_TIME2004:
				String totalTime = pssc.getTotalTime();
				if(totalTime == null) totalTime = "0";
				entry.setValue(CMI.to2004Timex(CMI.from1_2Timex(totalTime)));break;
// TODO XML aanvullen met gegevens uit pssc
			case XML:
				if(pssd == null) {
					pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
					if(pssd == null) 
						pssd = new PersistentStudentScoData();
				}
				entry.setValue(String.valueOf(pssd.getCocd()));
				break;
			case COCD:
				if(pssd == null) {
					pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
					if(pssd == null) 
						pssd = new PersistentStudentScoData();
				}
				if(xml == null) {
					xml = new Scorm2Xml(String.valueOf(pssd.getCocd()));
				}
				entry.setValue(xml.LMSGetValue(entry.getKey()));
				break;
			case SUSPEND_DATA:
				if(pssd == null) {
					pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
					if(pssd == null) 
						pssd = new PersistentStudentScoData();
				}
				entry.setValue(String.valueOf(pssd.getSuspendData()));
				break;
			}
			logEntry("get", entry, pssc.getPersistentHasRolePK().getUserID(), pssc.getScoID());
		}
	}


	private static void logEntry(String pfx, DomMapEntry<String, String> entry, Long userid, Long scoid) {
		LOG.log(Level.INFO, "{0} u:{3}, s:{4}, k:{1}, v:{2}", new Object[] { pfx, entry.getKey(), entry.getValue(), String.valueOf(userid), String.valueOf(scoid)} );
	}


	@PUT
    @Produces({"application/json"})
    @Path("/setValues")
    public Boolean setValues(@Context SecurityContext sc, RestScormValues rest) throws Dwo2Exception {
    	DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    	
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
// userid must match
 		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
 		{
    		LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Hasrole mismatch");
    		throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");			
 		}
		PersistentStudentScoContext pssc = null;
		PersistentStudentScoData pssd = null;
		PersistentScoContext scoContext = null;
		DomSchoolClassId domClassID = rest.getDomScormValues().getSchoolClassID();
		Long classID = MySQLPersistenceId.getNativeId(domClassID);
		try {
			scoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext()));
     	LOG.log(Level.INFO, "setValues starts " + sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
		List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
		Scorm2Xml xml = null;
		if (list.isEmpty()) {
			pssc = new PersistentStudentScoContext();
			long now = System.currentTimeMillis();
			pssc.setCreateDate(new Date(now));
			pssc.setCreateTime(new Time(now));
			pssc.setScoID(scoContext.getScoID());
			pssc.setPersistentHasRolePK(hasRoleKey);
			StudentScoContextManager.create(pssc); // throw duplicate error
			LOG.fine("studentsco1 = " + pssc.getStudentSco());
			list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
			pssc = list.get(0);
			LOG.fine("studentsco2 = " + pssc.getStudentSco());
// NIEUW
//			pssc.setClassID(MySQLPersistenceId.getNativeId(classID));

			
		} else {
			pssc = list.get(0);
			if(COMPLETE.equals(pssc.getCompletionStatus()))
			{
				LOG.warning("data is readonly "+ sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
				return Boolean.FALSE;
			}
// Niet nieuw: check if <> then null
		
		}
		
		for(DomMapEntry<String,String> entry: rest.getDomScormValues().getValues()) {
			logEntry("set", entry, pssc.getPersistentHasRolePK().getUserID(), pssc.getScoID());
			ScormKey key = ScormKey.getKey(entry.getKey());
			String value = entry.getValue();
			switch(key) {
			case SCORE: 
				try {
					pssc.setScore(Float.parseFloat(value));
				} catch (Exception e) {
					LOG.warning("setValues: score= " + value + " e:" + e);
				}break;
			case LOCATION:
				pssc.setLocation(value);break;
			case COMPLETION_STATUS:
				pssc.setCompletionStatus(value);break;
			case COCD:
				try {
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
				} catch (Exception e) {
					LOG.warning("setValues: cocd= " + entry.getKey() + ","+ value + " e:" + e);
				}
				break;
			case SESSION_TIME:
				pssc.setSessionTime(value);
				break;
			case SESSION_TIME2004:
				try {
					pssc.setSessionTime(CMI.to1_2Timex(CMI.from2004Time(value)));
				} catch (Exception e) {
					LOG.warning("setValues: sessiontime= " + entry.getKey() + ","+ value + " e:" + e);
				}
				break;
			case SUSPEND_DATA:
				try {
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
				} catch (Exception e) {
					LOG.warning("setValues: suspenddata= " + value + " e:" + e);
				}
				break;
			case TOTAL_TIME:
				pssc.setTotalTime(value);
				break;
			case TOTAL_TIME2004:
				try {
					pssc.setTotalTime(CMI.to1_2Timex(CMI.from2004Time(value)));
				} catch (Exception e) {
					LOG.warning("setValues: totaltime= " + value + " e:" + e);

				}
				break;
			case XML:
// TODO
				break;
			}
		}
			if (xml != null) {
				pssd.setCocd(xml.toString());
			}
			if (pssd != null) {
				StudentScoDataManager.edit(pssd);
			}
			StudentScoContextManager.edit(pssc);
			LOG.log(Level.INFO, "setValues returns " + sc.getUserPrincipal().getName() + " " + sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
		} catch (PersistenceException ex) {
            LOG.log(Level.WARNING, "User {0} could not update studentscocontext {1}.", new Object[]{sc.getUserPrincipal().getName(), pssc.getStudentSco()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update studentscocontext " + sc.getUserPrincipal().getName() + ".");
		}
    	return Boolean.TRUE;
    }
}
