package fi.dwo.dwojapplet.persistence.cache;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredStudentScoDataManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherResultsManager;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.util.Collections;
import java.util.logging.Logger;

class NoCache implements IStore {
    private static final Logger LOG = Logger.getLogger(NoCache.class.getName());

    
    NoCache() {
    }

    @Override
    public String getValue(int uid, int scoid, int sgid, int clsid, String key) throws PersistenceException {
        String result = null;
        try {
          PersistenceId schoolGroupId = PersistentSchoolGroup.buildPersistenceId(Long.valueOf(sgid));
          if (uid == DwoHelper.getCurrentFacadeUser().getUserID() && schoolGroupId .equals( DwoHelper.getCurrentFacadeUser().getSchoolGroupID())) {
            // SELF 
            DomScormValues dom = new DomScormValues();
            DomScoContext scoContext  = new DomScoContext();
            scoContext.setId(PersistentScoContext.buildPersistenceId(Long.valueOf(scoid)));
            dom.setScoContext(scoContext);
            setSchoolClass(dom);
            DomMapEntry<String,String> o = new DomMapEntry<>(key, "");
            dom.setValues(Collections.singletonList(o ));
            result = SecuredStudentScoDataManager.get(dom).getValues().get(0).getValue();
          } else {
            // Student Of Teacher
            //result = dbAccess.LMSGetValue(scoid, uid, sgid, key);
            // Student Of Teacher
            DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
            dom.setDomProfile(DWO.getDwoProfile());
            DomSchoolClass domSchoolClass = new DomSchoolClass();
            domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)clsid));
            dom.setDomSchoolClass(domSchoolClass);
            DomScoContext scoContext  = new DomScoContext();
            scoContext.setId(PersistentScoContext.buildPersistenceId(Long.valueOf(scoid)));
            dom.setDomScoContext(scoContext);
            DomStudent domStudent = new DomStudent();
            domStudent.setId(PersistentUser.buildPersistenceId((long)uid));
            dom.setDomStudentList(Collections.singletonList(domStudent));
            DomStudentScoContext ssc = SecuredTeacherResultsManager.createStudentResults(dom).getStudentScoContexts().get(0).getValue();
            result = SecuredTeacherResultsManager.getValues(ssc, Collections.singleton(key)).get(key);
          }
        } catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        return result;
    }

    @Override
    public String setValue(int uid, int scoid, int sgid, int clsid, String key, String value) throws PersistenceException {
        String random = Long.toHexString(Double.doubleToRawLongBits(Math.random()));
        String result;
        DomScoContext scoContext  = new DomScoContext();
        scoContext.setId(PersistentScoContext.buildPersistenceId(Long.valueOf(scoid)));
       try {
          PersistenceId schoolGroupId = PersistentSchoolGroup.buildPersistenceId(Long.valueOf(sgid));
          if (uid == DwoHelper.getCurrentFacadeUser().getUserID() && schoolGroupId .equals( DwoHelper.getCurrentFacadeUser().getSchoolGroupID())) {
            // SELF 
            DomScormValues dom = new DomScormValues();
            dom.setScoContext(scoContext);
            setSchoolClass(dom);
            DomMapEntry<String,String> o = new DomMapEntry<>(key, value);
            dom.setValues(Collections.singletonList(o ));
            return String.valueOf( SecuredStudentScoDataManager.set(dom) );
          } else {
            // Student Of Teacher
            DomClearStudentDataForScoAndClass dom = new DomClearStudentDataForScoAndClass();
            dom.setDomProfile(DWO.getDwoProfile());
            DomSchoolClass domSchoolClass = new DomSchoolClass();
            domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId((long)clsid));
            dom.setDomSchoolClass(domSchoolClass);
            dom.setDomScoContext(scoContext);
            DomStudent domStudent = new DomStudent();
            domStudent.setId(PersistentUser.buildPersistenceId((long)uid));
            dom.setDomStudentList(Collections.singletonList(domStudent));
            DomStudentScoContext ssc = SecuredTeacherResultsManager.createStudentResults(dom).getStudentScoContexts().get(0).getValue();
            SecuredTeacherResultsManager.setValues(ssc, Collections.singletonMap(key, value));
//            result = dbAccess.LMSSetValue(scoid, uid, sgid, key, value, random);
//            if (result.equals(random))
            {
                return "true"; // all's well
            }
          }
        } catch (Dwo2Exception e) {
          log(e.getMessage());
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
//        result = "LMSSetValue " + key + ": " + result + " <> " + random;
//        log(result);
//        throw new PersistenceException(PersistenceException.EX_DB);
    }

    private void setSchoolClass(DomScormValues dom) {
      try {
        dom.setSchoolClassID(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchoolClass());
      } catch (Exception e) {
        dom.setSchoolClassID(null);
      }
    }

    private void log(String result) {
//        try {
//            dbAccess.log(result);
//        } catch (Exception e) {
//            System.err.println(result);
//            LOG.log(Level.SEVERE,null,e);
//        }
        LOG.severe(result);
    }

    @Override
    public String commit(int uid, int scoid, String param) {
        return "true";
    }

    @Override
    public void destroy() {
    }

//    @Override
//    public boolean changeSco(int scoid, String scoName, String description,
//            boolean delete, String launchdataString, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
//        assert !delete;
//    	{
//            boolean result = dbAccess.changeSco(scoid, scoName, description, delete, launchdataString);
//            if (result && null != showScore) // heel onwaarschijnlijk?
//            {
//                dbAccess.changeSco(scoid, scoName, description, showScore.booleanValue());
//            }
//            return result;
//        }
//    }

//    @Override
//    public boolean changeSco(int scoid, String scoName, String description,
//            boolean delete, byte[] launchdata, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
//        if (showScore == null) {
//            showScore = Boolean.TRUE;
//        }
//        boolean result = dbAccess.changeSco(scoid, scoName, description, delete, launchdata, showScore);
//        return result;
//    }

    @Override
    public void clear(int scoid) {
    }

	@Override
	public void uncache(Sco sco, boolean delete) {
	}

}
