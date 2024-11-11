package fi.dwo.server.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.owlike.genson.Genson;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm2xml.Scorm2Xml;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.UEscape;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.ScoPageUtilManager;
import fi.dwo.server.persistence.CmiConvert;
import fi.dwo.server.rest.util.Digest;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

abstract class SecuredCommonScoDataManager {
  private static final Logger LOG = Logger.getLogger(SecuredCommonScoDataManager.class.getName());
  static final String COMPLETE = "completed";
  static final String REVIEW_DATA = "cmi.comments_from_lms.0.comment";
  static final String REVIEW_CORRECT = "cmi.comments_from_lms.2.comment";
  static final CmiConvert CMI = new CmiConvert(); // utility class

  String normalizeETag(String match) {
    if (match.endsWith("-gzip")) // Apache adds -gzip to etag
      match = match.substring(0, match.length() - 5);
    return match;
  }

  String buildETag(PersistentStudentScoContext ssContext, PersistentStudentScoData ssData) {
    if (ssContext == null | ssData == null) return "";
    return ssContext.getOptlock() + "+" + ssContext.getLastChangeTimeStamp() + "+"
        + ssData.getOptlock() + "+" + ssData.getLastChangeTimeStamp();
  }

  static void logEntry(Logger log, String pfx, DomMapEntry<String, String> entry, Long userid,
      Long scoid) {
    log.log(Level.INFO, "{0} u:{3}, s:{4}, k:{1}, v:{2}", new Object[] {pfx, entry.getKey(),
        shrink(entry.getValue()), String.valueOf(userid), String.valueOf(scoid)});
  }
  private static void logEntry(String pfx, DomMapEntry<String, String> entry, Long userid,
                       Long scoid) {
         logEntry(LOG, pfx, entry, userid, scoid);              
  }

  static final char ELLIPSIS = '\u2026';

  static String shrink(String string) {
    if (string == null || string.length() <= 20) return string;
    return string.substring(0, 19) + ELLIPSIS;
  }

  
  static void getScormValues(List<DomMapEntry<String, String>> entryList,
                             PersistentStudentScoContext pssc) {
                        if (pssc == null) {
                            return;
                        }
                        
                        PersistentStudentScoData pssd = null;
                        PersistentStudentModelData psmd = null;
                        Genson genson = null;
                        Scorm2Xml xml = null;
                        for(DomMapEntry<String,String> entry: entryList) {
                            ScormKey key = ScormKey.getKey(entry.getKey());
                            String total_time;
                            switch(key) {
                            case STUDENT_MODEL:
                                if (psmd == null) {
                                    genson = new GensonProvider().getContext(DomStudentModelStructureScore.class);
                                    PersistentHasRole hasRole = HasRoleManager.findEntity(pssc.getPersistentHasRolePK());               
                                    PersistentScoContext ctx = ScoContextManager.findEntity(pssc.getScoID());
                                    psmd = StudentModelDataManager.findEntity(ctx, hasRole);
                                }
                                DomStudentModelStructureScore modelData = psmd.getModelData();
                                String json = genson.serialize(modelData);
                                entry.setValue(json);
                                break;
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
                                entry.setValue(Objects.toString(pssc.getSessionTime(), ""));break;
                            case SESSION_TIME2004:
                                String sessionTime = pssc.getSessionTime();
                                if(sessionTime == null) sessionTime = "0";
                                entry.setValue(CMI.to2004Timex(CMI.from1_2Timex(sessionTime)));break;
                            case TOTAL_TIME:
                                total_time = pssc.getTotalTime();
                                if(total_time == null) total_time = "0000:00:00.00";
                                entry.setValue(total_time);break;
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
                                String suspendData = pssd.getSuspendData();
                                if(suspendData == null) suspendData = "";
                                entry.setValue((suspendData));
                                break;
                            case SCORE_WIDGET:
                                entry.setValue(scoreWidget(entry.getKey(),pssd, pssc));
                            }
                            logEntry("get", entry, pssc.getPersistentHasRolePK().getUserID(), pssc.getScoID());
                        }
                    }

  
  Response patchValues(SecurityContext sc, RestScormValues rest, String match) throws Dwo2Exception {
    PersistentStudentScoContext ssContext = null;
    PersistentStudentScoData    ssData = null;
    if (match != null) {
      EntityTag t = EntityTag.valueOf(match);
      match = t.getValue();
      match = normalizeETag(match);
    }
    DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
    PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
    PersistentScoContext scoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext()));
    PersistentCourse course = CourseManager.findEntity(scoContext.getCourseID());
    Long schoolID = course.getSchoolID();
    if (schoolID != null) {
        // schoolID must match
        if (! schoolID.equals(phr.getSchoolGroup().getSchool().getSchoolID())) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": School mismatch");
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");          
        }
        if (phr.getSchoolGroup().getGroupID() == RoleType.STUDENT.ordinal()) {
            DomSchoolClassId domClassID = rest.getDomScormValues().getSchoolClassID();
            Long classID = MySQLPersistenceId.getNativeId(domClassID);
            PersistentSchoolClass schoolClass = new PersistentSchoolClass(classID);
            List<PersistentClassCourse> pccList = ClassCourseManager.findEntities(schoolClass, course);
            PersistentClassCourse pcc = pccList.get(0);
            boolean ok = pcc.getViewState() == ViewState.studentsAndTeachers;
            ok &= isSoC(phr, schoolClass);
            if (pcc.getNotAfter() != null) ok &= pcc.getNotAfter().after(new java.util.Date());
            if (pcc.getNotBefore() != null) ok &= pcc.getNotBefore().before(new java.util.Date());
            if (!ok || !checkType(pcc)) {
                  LOG.severe("ClassCourse not open " + rest.getDomScormValues().getScoContext().getId());
                  return Response.ok(Boolean.FALSE, MediaType.APPLICATION_JSON_TYPE).build();                       
            }
        }
     }

    List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
    if(!list.isEmpty()) {
        ssContext = list.get(0);
        if(COMPLETE.equals(ssContext.getCompletionStatus()))
        {
            LOG.warning("data is readonly "+ sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
            return Response.ok(Boolean.FALSE, MediaType.APPLICATION_JSON_TYPE).build();
        }
        ssData = StudentScoDataManager.findEntity(ssContext.getStudentSco());
        if(ssData == null) {
            ssData = new PersistentStudentScoData(ssContext.getStudentSco(), "{}");
            ssData.setCocd("");
            StudentScoDataManager.create(ssData);
        }
    }
   String etag = buildETag(ssContext, ssData);
    if (!etag.equals(match))
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Wrong if-match", Response.Status.PRECONDITION_FAILED);
// PATCH
    String digest = null;
    for(DomMapEntry<String,String> entry: rest.getDomScormValues().getValues()) {
        logEntry("patch", entry, ssContext.getPersistentHasRolePK().getUserID(), ssContext.getScoID());
        ScormKey key = ScormKey.getKey(entry.getKey());
        String value = entry.getValue();
        switch(key) {
        case SUSPEND_DIGEST:
            digest = value;
            break;
        case SUSPEND_DATA:
            String oldValue = ssData.getSuspendData();
            JsonParser parser = Json.createParser(new StringReader(oldValue));
            parser.next();
            JsonObject oldObject = parser.getObject();
            parser = Json.createParser(new StringReader(value));
            parser.next();
            JsonArray  patch     = parser.getArray();
            JsonObject newObject = Json.createPatch(patch).apply(oldObject);
            if (digest != null) {
              String patched = new Digest().digest(newObject);
              if( !digest.equals(patched)) {
                LOG.severe("patch digest error " + patched + " " + digest);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Wrong digest", Response.Status.PRECONDITION_FAILED);
              }
            }
 // only students, voor testen uitzetten
 //           if (phr.getSchoolGroup().getGroupID() == RoleType.STUDENT.ordinal())
            	ScoPageUtilManager.updateSuspendData(ssContext, newObject);
            
            StringWriter newValue = new StringWriter();
            Json.createWriter(newValue).write(newObject);
            ssData.setSuspendData(UEscape.convertUEsc(newValue.toString()));
            break;
        default:
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "wrong key");
        }
    }
    ssContext = StudentScoContextManager.edit(ssContext);
    ssData = StudentScoDataManager.edit(ssData);
    etag = buildETag(ssContext, ssData);
    return Response.ok(Boolean.TRUE, MediaType.APPLICATION_JSON_TYPE)
            .tag(etag)
            .build();
}

  boolean checkType(PersistentClassCourse pcc) {
    return true;
  }

  private boolean isSoC(PersistentHasRole hr, PersistentSchoolClass sc) {
    PersistentStudentOfClassPK pk 
    = new PersistentStudentOfClassPK(
        hr.getPersistentHasRolePK().getUserID(), 
        sc.getClassID(), hr.getPersistentHasRolePK().getSchoolGroupID());
    return null != StudentOfClassManager.findEntity(pk);
  }
  
  String getJSONLaunchDataBytes(SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
//Context
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
      PersistentCourse course = CourseManager.findEntity(courseID);
      if(course.getDwoProfileID().longValue() != profileID.longValue())
      {
          LOG.log(Level.WARNING, "profile mismatch " + sc.getUserPrincipal().getName() );  
          LOG.warning( "course " + course.getCourseID() + " " + course.getDwoProfileID() );
          LOG.warning( "sco " + scoContext.getScoID() + " " + scoContext.getDwoProfileID());
          LOG.warning("profile " + profileID);
          return "{}";
      }
      Long schoolID = course.getSchoolID();
      if(schoolID != null) {
          DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
          PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
          PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
//userid must match
              if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
              {
                  LOG.log(Level.SEVERE, "user mismatch " + sc.getUserPrincipal().getName() );     
                  return "{}";
              }
//schoolID must match
          if (phr.getSchoolGroup().getSchoolID() != schoolID.longValue())
          {
              LOG.log(Level.SEVERE, "school mismatch " + sc.getUserPrincipal().getName() );       
              return "{}";          
          }
          if (phr.getSchoolGroup().getGroupID() == RoleType.STUDENT.ordinal()) {
            DomSchoolClassId domClassID = rest.getSchoolClassID();
            Long classID = MySQLPersistenceId.getNativeId(domClassID);
            PersistentSchoolClass schoolClass = new PersistentSchoolClass(classID);
            List<PersistentClassCourse> pccList = ClassCourseManager.findEntities(schoolClass, course);
            PersistentClassCourse pcc = pccList.get(0);
            boolean ok = pcc.getViewState() == ViewState.studentsAndTeachers;
            ok &= isSoC(phr, schoolClass);
            if (pcc.getNotAfter() != null) ok &= pcc.getNotAfter().after(new java.util.Date());
            if (pcc.getNotBefore() != null) ok &= pcc.getNotBefore().before(new java.util.Date());
            if (!ok || !checkType(pcc)) {
                  LOG.severe("ClassCourse not open " + rest.getDomScoContext().getId());
                  return "{}";                       
            }
          } else if (phr.getSchoolGroup().getGroupID() == RoleType.TEACHER.ordinal()) {
        	  PersistentSchool school = phr.getSchoolGroup().getSchool();
        	  if (school.accessControl()) {
        		  ACL acl = getACL(phr, course);
        		  if (acl == ACL.NONE||acl == ACL.ACCESS) {
        			  return "{}";
        		  }
        	  }
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

  static ACL getACL(PersistentHasRole phr, PersistentCourse course) {
	  List<PersistentTeacherOfClass> l = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
	  List<PersistenceId> classes = l.stream().map(i -> i.getPersistentTeacherOfClassPK().getClassID()).map(PersistentSchoolClass::buildPersistenceId).collect(Collectors.toList());

	  return getACL(phr.getUser(), phr.getSchoolGroup().getSchool(), classes, course);
  }

  static ACL getACL(PersistentUser user, PersistentSchool school, List<PersistenceId> classes, PersistentCourse course) {
	  List<PersistentACL> acls;
	  do {
		  acls = ACLManager.findByCourse(course);
		  if (acls != null && ! acls.isEmpty()) break;
		  course = CourseManager.findEntity(course.getParentID());
	  } while (course != null);
	  if (!acls.isEmpty()) {
		  Set<String> rights = classes.stream().map(PersistenceId::getIdString).collect(Collectors.toSet());
		  rights.add(user.buildPersistenceId().getIdString());
		  rights.add(school.buildPersistenceId().getIdString());
		  ACL acl = acls.stream()
                  .filter(item -> rights.contains(item.getEntity()))
                  .map(PersistentACL::getAccess)
                  .sorted( (ACL aa, ACL bb) -> - aa.compareTo(bb))
                  .findFirst()
                  .orElse(ACL.NONE);
		  return acl;
	  }
	  
	  if (school.teachersCanWrite())
		  return ACL.FULL;
	  else
		  return ACL.NONE; // parent is altijd "true" (voor sco's)
  }

  
  
  
public Response getValues(SecurityContext sc, RestScormValues rest) throws Dwo2Exception {
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
//userid must match
    if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
    {
        LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Hasrole mismatch");
        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");          
    }
    Long scocontextid = MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext());
    LOG.log(Level.INFO, "getValues " + sc.getUserPrincipal().getName() + " " + scocontextid);
    PersistentScoContext scoContext = ScoContextManager.findEntity(scocontextid);
    PersistentCourse course;
    if(scoContext == null) // Non existent
    {
    	if (scocontextid.longValue() == 0L) {
    		course = new PersistentCourse(0L);
    		scoContext = new PersistentScoContext(scocontextid);
    	} else
    		return Response.ok(rest.getDomScormValues(), MediaType.APPLICATION_JSON_TYPE).build();
    } else
    	course = CourseManager.findEntity(scoContext.getCourseID());
    Long schoolID = course.getSchoolID();
    if (schoolID != null) {
        // schoolID must match
        if (! schoolID.equals(phr.getSchoolGroup().getSchool().getSchoolID())) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": School mismatch");
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");          
        }
        if (phr.getSchoolGroup().getGroupID() == RoleType.STUDENT.ordinal()) {
            DomSchoolClassId domClassID = rest.getDomScormValues().getSchoolClassID();
            Long classID = MySQLPersistenceId.getNativeId(domClassID);
            PersistentSchoolClass schoolClass = new PersistentSchoolClass(classID);
            List<PersistentClassCourse> pccList = ClassCourseManager.findEntities(schoolClass, course);
            PersistentClassCourse pcc = pccList.get(0);
            boolean ok = pcc.getViewState() == ViewState.studentsAndTeachers;
            ok &= isSoC(phr, schoolClass);
            if (pcc.getNotAfter() != null) ok &= pcc.getNotAfter().after(new java.util.Date());
            if (pcc.getNotBefore() != null) ok &= pcc.getNotBefore().before(new java.util.Date());
            if (!ok || !checkType(pcc)) {
                  LOG.severe("ClassCourse not open " + rest.getDomScormValues().getScoContext().getId());
                  return Response.ok(rest.getDomScormValues(), MediaType.APPLICATION_JSON_TYPE).build();                       
            }
        }
     }

    List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
    if(list.isEmpty()) {
    	if (true || scoContext.getScoID().longValue() == 0L) {
    		PersistentStudentScoContext pssc = new PersistentStudentScoContext(scoContext.getScoID());
    		pssc.setPersistentHasRolePK(hasRoleKey);
    		pssc.setScoID(scoContext.getScoID());
			list.add(pssc);
    	} else
    		return Response.ok(rest.getDomScormValues(), MediaType.APPLICATION_JSON_TYPE).build();
    }
    List<DomMapEntry<String, String>> entryList = rest.getDomScormValues().getValues();
    PersistentStudentScoContext pssc = list.get(0);
    getScormValues(entryList, pssc);

    DomScormValues domScormValues = rest.getDomScormValues();
    LOG.log(Level.INFO, "getValues done " + sc.getUserPrincipal().getName() + " " + scocontextid);
    PersistentStudentScoData pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
    
    return Response.ok(domScormValues, MediaType.APPLICATION_JSON_TYPE).tag(buildETag(pssc,pssd)).build();
}

  
  public Response setValues(SecurityContext sc, RestScormValues rest, EntityTag match) throws Dwo2Exception {
    DomHasRole domHasRole = rest.getRestContext().getDomHasRole();
    UserState_U ustate = AnonDomainAuthorizer.build().submitUser(sc);
    // Context
    PersistentUser user = ustate.getUser();
    PersistentStudentScoContext pssc = null;
    PersistentStudentScoData pssd = null;
	UserState_HR_R_S_SG_U rstate = ustate.setHasRole(domHasRole);

    PersistentHasRole phr = rstate.getHasRole();
    PersistentHasRolePK hasRoleKey = phr.getPersistentHasRolePK();
//userid must match
    if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
    {
        LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Hasrole mismatch");
        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");          
    }
    PersistentScoContext scoContext = null;
    try {
        scoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext()));
        if(scoContext == null)
        {
          LOG.warning("Scocontext missing " + rest.getDomScormValues().getScoContext().getId());
          return Response.ok(Boolean.FALSE, MediaType.APPLICATION_JSON_TYPE).build();
        }
        PersistentCourse course = CourseManager.findEntity(scoContext.getCourseID());
        Long schoolID = course.getSchoolID();
        if (schoolID != null) {
            // schoolID must match
            if (! schoolID.equals(rstate.getSchool().getSchoolID())) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": School mismatch");
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "This will be logged.");          
            }
            if (rstate.getRoleType() == RoleType.STUDENT) {
                DomSchoolClassId domClassID = rest.getDomScormValues().getSchoolClassID();
                Long classID = MySQLPersistenceId.getNativeId(domClassID);
                PersistentSchoolClass schoolClass = new PersistentSchoolClass(classID);
                List<PersistentClassCourse> pccList = ClassCourseManager.findEntities(schoolClass, course);
                PersistentClassCourse pcc = pccList.get(0);
                boolean ok = pcc.getViewState() == ViewState.studentsAndTeachers;
                ok &= isSoC(phr, schoolClass);
                if (pcc.getNotAfter() != null) ok &= pcc.getNotAfter().after(new java.util.Date());
                if (pcc.getNotBefore() != null) ok &= pcc.getNotBefore().before(new java.util.Date());
                if (!ok || !checkType(pcc)) {
                      LOG.severe("ClassCourse not open " + rest.getDomScormValues().getScoContext().getId());
                      return Response.ok(Boolean.FALSE, MediaType.APPLICATION_JSON_TYPE).build();                       
                }
            }
         }
        
        
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
		pssc.setLocation("");
		pssc.setCompletionStatus("");
		pssc.setSessionTime("");
		pssc.setTotalTime("");
        StudentScoContextManager.create(pssc); // throw duplicate error
        LOG.fine("studentsco1 = " + pssc.getStudentSco());
        list = StudentScoContextManager.findEntities(scoContext, hasRoleKey);
        pssc = list.get(0);
        LOG.fine("studentsco2 = " + pssc.getStudentSco());
//NIEUW
//      pssc.setClassID(MySQLPersistenceId.getNativeId(classID));

        
    } else {
        pssc = list.get(0);
        if(COMPLETE.equals(pssc.getCompletionStatus()))
        {
            LOG.warning("data is readonly "+ sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
            return Response.ok(Boolean.FALSE, MediaType.APPLICATION_JSON_TYPE).build();
        }
//Niet nieuw: check if <> then null
        pssd = StudentScoDataManager.findEntity(pssc.getStudentSco()); // ALWAYS get context.
    }

    if(match != null) {
      String etag = buildETag(pssc, pssd);
        if (!etag.equals(normalizeETag(match.getValue())))
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Wrong if-match", Response.Status.PRECONDITION_FAILED);
    }

    for(DomMapEntry<String,String> entry: rest.getDomScormValues().getValues()) {
        logEntry("set", entry, pssc.getPersistentHasRolePK().getUserID(), pssc.getScoID());
        ScormKey key = ScormKey.getKey(entry.getKey());
        String value = entry.getValue();
        switch(key) {
        case STUDENT_MODEL:
            PersistentStudentModelData psmd = StudentModelDataManager.findEntity(scoContext, phr);
            DomStudentModelStructureScore modelData = null;
            modelData = new GensonProvider().getContext(DomStudentModelStructureScore.class).deserialize(value, DomStudentModelStructureScore.class);
            if(psmd == null) {
                psmd = new PersistentStudentModelData();
                psmd.setScoID(scoContext.getScoID());
                psmd.setPersistentHasRolePK(phr.getPersistentHasRolePK());
                psmd.setModelID(scoContext.getModelID());
                psmd.setModelData(modelData);
                StudentModelDataManager.create(psmd);
            } else {
                psmd.setModelData(modelData);
                psmd = StudentModelDataManager.edit(psmd);
            }
            break;
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
                            pssd.setCocd(Scorm2Xml.EMPTY_DOC);
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
                value = UEscape.convertUEsc(value);
                if(pssd == null) {
                    pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
                    if(pssd == null) {
                        pssd = new PersistentStudentScoData(pssc.getStudentSco(), value);
                        pssd.setCocd("");
                        StudentScoDataManager.create(pssd);
                    } else {
                        pssd.setSuspendData(value);
                    }
                } else {
                    pssd.setSuspendData(value);
                }
//           if (phr.getSchoolGroup().getGroupID() == RoleType.STUDENT.ordinal())
               ScoPageUtilManager.updateSuspendData(pssc, value);
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
//TODO
            break;
        }
    }
        if (xml != null) {
            pssd.setCocd(xml.toString());
        }
        if (pssd != null) {
            pssd = StudentScoDataManager.edit(pssd);
        }
        pssc = StudentScoContextManager.edit(pssc);
        LOG.log(Level.INFO, "setValues returns " + sc.getUserPrincipal().getName() + " " + sc.getUserPrincipal().getName() + " " + scoContext.getScoID());
    } catch (PersistenceException ex) {
        LOG.log(Level.WARNING, "User {0} could not update studentscocontext {1}.", new Object[]{sc.getUserPrincipal().getName(), pssc.getStudentSco()});
        LOG.log(Level.SEVERE, "", ex);
        throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update studentscocontext " + sc.getUserPrincipal().getName() + ".");
    }
    return Response.ok(Boolean.TRUE, MediaType.APPLICATION_JSON_TYPE).tag(buildETag(pssc,pssd)).build();
}

  static String scoreWidget(String key, PersistentStudentScoData pssd, PersistentStudentScoContext pssc) {
	String[] split = key.split("\\.");
	String page = "1";
	if ("cs".equals(split[2])) {
		page = split[3];
        if(pssd == null) {
            pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
         }
	} else if ("s".equals(split[2])) {
		page = split[4];
		long sco = Long.parseLong(split[3]);
		if (sco != pssc.getScoID().longValue() || pssd == null) {
		    PersistentScoContext sc = new  PersistentScoContext(sco);
		    List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(sc, pssc.getPersistentHasRolePK());
			if (!list.isEmpty()) 
			{
				pssc = list.get(0);
				pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
			}
			else 
			{
				pssd = null;
				pssc = new PersistentStudentScoContext();
				pssc.setScoID(sco);
			}
		}
	} else if ("cc".equals(split[2])) {
		pssd = null;
		page = split[4];
		long sconr = Long.parseLong(split[3]);
		PersistentScoContext scoContext = ScoContextManager.findSibling(pssc.getScoID(), sconr);
		if (scoContext == null) return "";
		List<PersistentStudentScoContext> list = 
				StudentScoContextManager.findEntities(scoContext, pssc.getPersistentHasRolePK());
		if (!list.isEmpty()) {
			pssc = list.get(0);
			pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
		} else {
			pssc = new PersistentStudentScoContext();
			pssc.setScoID(scoContext.getScoID());
		}
	} else if ("c".equals(split[2])) {
		page = split[5];
		pssd = null;
		long courseid = Long.parseLong(split[3]);
		long actnr = Long.parseLong(split[4]);
		PersistentCourse course = CourseManager.findEntity(courseid);
		if (course == null) return "";
		List<PersistentScoContext> list = ScoContextManager.findEntities(course);
			for (PersistentScoContext s: list ) {
				if (s.getSequencenr().longValue() == actnr) {
					List<PersistentStudentScoContext> list2 = StudentScoContextManager.findEntities(s, pssc.getPersistentHasRolePK());
					if (!list2.isEmpty())
					{
						pssc = list2.get(0);
						pssd = StudentScoDataManager.findEntity(pssc.getStudentSco());
					} else {
						pssc = new PersistentStudentScoContext();
						pssc.setScoID(s.getScoID());
					}
					break;
				}
			}
		
	} else return "";
	if (pssd == null) 
		pssd = new PersistentStudentScoData(pssc.getStudentSco());
	try {
		String suspend_data = pssd.getSuspendData();
		if (suspend_data == null || suspend_data.length() < 6) suspend_data = "{}";
		JsonParser parser = Json.createParser(new StringReader(suspend_data));
		parser.next();
		JsonObject data = parser.getObject();
		JsonObject onsState = data.getJsonObject("onsState");
		int pagenr = Integer.parseInt(page)-1;
// bij zelftoets pas een score/succes_status als er minstens 1 keer op de kijkna knop is gedrukt (aantalNakijken>0)		
		if (key.endsWith(".score.raw")) {
			if (pagenr<0) {
				if (pssc.getCompletionStatus() == null||"not attempted".equals(pssc.getCompletionStatus())) // null of aangemaakt door docent alleen.
					return ""; // ab-initio
				if (onsState != null && !COMPLETE.equals(pssc.getCompletionStatus())) {
					JsonArray nakijken = onsState.getJsonArray("aantalNakijken");
					if (nakijken != null && 0 == nakijken.getInt(0) && !COMPLETE.equals(pssc.getCompletionStatus())) {
						return "";
					}
				}
				return String.valueOf(Math.round(pssc.getScore()));
			}
			if (onsState == null) return "";
			JsonArray orScores = onsState.getJsonArray("orScores");
			Number n;
			try {
				orScores = orScores.getJsonArray(0);
				n = orScores.getJsonNumber(pagenr).numberValue();
			} catch(Exception oops) {
				n = 0; // there is ALWAYS a value
			}
			if (COMPLETE.equals(pssc.getCompletionStatus()) && pssd.getCocd() != null) {
				Scorm2Xml xml = new Scorm2Xml(pssd.getCocd());
				String json = xml.LMSGetValue(REVIEW_DATA);
				if (!json.isEmpty()) {
				parser = Json.createParser(new StringReader(json));
				parser.next();
				data = parser.getObject();
				JsonArray contState = data.getJsonArray("opdrContStates");
				contState = contState.getJsonArray(0);
				data = contState.getJsonObject(pagenr);
				int sum = sumOfCorrectie(data);
				n = Integer.valueOf(sum + n.intValue());
			}} else {
				JsonArray nakijken = onsState.getJsonArray("aantalNakijken");
				if (nakijken != null && 0 == nakijken.getInt(0) && !COMPLETE.equals(pssc.getCompletionStatus())) {
					return "";
				}
				if (nakijken != null) {
					JsonArray scoresZelftoets = onsState.getJsonArray("scoresZelftoets");
					if (scoresZelftoets != null) {
						scoresZelftoets = scoresZelftoets.getJsonArray(0);
						n = scoresZelftoets.getJsonNumber(pagenr).numberValue();
					}
				}
			}
			return n.toString();
		}
		if (key.endsWith(".success_status")) {
			if (pagenr < 0) {
				return pssc.getScore() > 99f ? "passed" : "";
			}
			if (onsState == null) return "";
			JsonArray orGoedFout = onsState.getJsonArray("orGoedFout");
			if (orGoedFout == null) return "";
			orGoedFout = orGoedFout.getJsonArray(0);
			boolean ok = orGoedFout.getBoolean(pagenr);
			if (COMPLETE.equals(pssc.getCompletionStatus()) && pssd.getCocd() != null) {
				Scorm2Xml xml = new Scorm2Xml(pssd.getCocd());
				String correct = xml.LMSGetValue(REVIEW_CORRECT);
				if (correct.length() > pagenr) {
					switch(correct.charAt(pagenr)) {
					case 'T': ok = true; break;
					case 'F': ok = false; break;
					}
				}
			} else {
				JsonArray nakijken = onsState.getJsonArray("aantalNakijken");
				if (nakijken != null && 0 == nakijken.getInt(0) && !COMPLETE.equals(pssc.getCompletionStatus())) {
					return "";
				}
				if (nakijken != null) {
					JsonArray correct = onsState.getJsonArray("isCorrectZelftoets");
					if (correct != null) {
						correct = correct.getJsonArray(0);
						ok = correct.getBoolean(pagenr);
					}
				}
			}
			return ok ? "passed" : "failed";    
		}
		if (key.endsWith(".entry")) {
			//if (pagenr < 0) return "resume"; // er is suspend_data;
			JsonArray bezocht;
			if (onsState == null) return "ab-initio";
			bezocht = onsState.getJsonArray("visited");
			if (bezocht == null)
				bezocht = onsState.getJsonArray("bezocht");
			bezocht = bezocht.getJsonArray(0);
			if (pagenr >= bezocht.size()) return "ab-initio";
			boolean ok;
			if (pagenr < 0) { // alle pagina's bezocht!
				ok = true;
				for (pagenr = 0 ; ok && pagenr < bezocht.size(); pagenr++) 
					ok = isGedaan(bezocht, pagenr);
			} else
				ok = isGedaan(bezocht, pagenr);
				
			return ok ? "resume" : "ab-initio";
		}
		if (key.endsWith(".completion_status")) {
			return Objects.toString(pssc.getCompletionStatus(),"");
		}
		if (key.endsWith(".id")) {
			return Objects.toString(pssc.getScoID(), "");
		}
	} catch (Exception e) {
		LOG.log(Level.WARNING, "scoreWidget " + key, e);
	}
    return "";   
  }

private static boolean isGedaan(JsonArray bezocht, int pagenr) {
	JsonValue v = bezocht.get(pagenr);
	boolean ok;
	if (v.getValueType() == ValueType.ARRAY) {
		ok = v.asJsonArray().isEmpty();
	} else {
		ok = v.getValueType() == ValueType.TRUE;
	}
	return ok;
}

private static int sumOfCorrectie(JsonObject data) {
	JsonObject correctie = data.getJsonObject("reviewInteractieData");
	if (correctie == null) {
		JsonArray panelStates = data.getJsonArray("interactiePanelStates");
		if (panelStates != null)
			return sumOfCorrectie(panelStates);
		return 0;
	}
	JsonNumber n = correctie.getJsonNumber("reviewScoreCorrectie");
	return n.intValue();
}

private static int sumOfCorrectie(JsonArray panelStates) {
	int size = panelStates.size();
	int sum = 0;
	for (int i = 0; i < size; i++) {
		JsonValue value = panelStates.get(i);
		if (value.getValueType() == ValueType.OBJECT) {
			JsonObject json = value.asJsonObject();
			sum += sumOfCorrectie(json);
		}
	}
	return sum;
}
  
  
}
