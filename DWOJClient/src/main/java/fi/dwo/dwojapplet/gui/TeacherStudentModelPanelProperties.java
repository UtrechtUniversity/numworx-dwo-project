package fi.dwo.dwojapplet.gui;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.owlike.genson.Genson;

import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.utils.Digest;
import fi.dwo.dwojapplet.gui.domainmodel.NodeVector;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.numworx.gwtpatch.client.GWTPatch;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;


/**
 *
 * @author Gert van der Plas
 */
public class TeacherStudentModelPanelProperties implements Comparator<DomStudentModelContext>{
  
    static class JavaBuilder implements GWTPatch.Builder {

      @SuppressWarnings("unchecked")
      @Override
      public Map<String, Object> createMap() {
        return new org.json.simple.JSONObject();
      }

      @SuppressWarnings("unchecked")
      @Override
      public List<Object> createList(int size) {
        return new org.json.simple.JSONArray();
      }
      
    }
  
    static class JavaPatch extends GWTPatch {
      
      String digest;
           
      JavaPatch() {
        super(new JavaBuilder());
      }

      @Override
      public String createPatch(String old, String now) {
        Object o1, o2;
        ObjectList result;
        try {
          JSONParser jsonParser = new JSONParser();
          o1 = jsonParser.parse(old);
          o2 = jsonParser.parse(now);
          try {
            digest = Digest.digest(o2);
          } catch (NoSuchAlgorithmException e) {
            // should not happen
          }
          result = createDiff(o1,o2);
          return result.toString();
        } catch (ParseException e) {
        }       
        return null;
      }
      
    }
  
    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanelProperties.class.getName());
    private final SecureStudentModelManager manager;
	private PersistenceId remoteMethod;

    TeacherStudentModelPanelProperties(SecureStudentModelManager manager){
        this.manager = manager;
    }

    void init() throws Dwo2Exception {
      MethodsProperties.reset();
    }
    
    DomStudentModelContext addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return SecureTeacherStudentModelManager.addModel(modelContext, DWO.getDwoProfile());
    }

    List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        return sort(manager.getReducedList(DWO.getDwoProfile()));
    }

    private List<DomStudentModelContext> sort(List<DomStudentModelContext> list) {
      Collections.sort(list, this);
      return list;
    }

    public DomStudentModelContext updateCurrentModel() throws Dwo2Exception {
      updateModel(current.getModelStructure());
      return current;
    }
    
    
    private DomStudentModelContext updateModel(DomStudentModelContext modelContext) throws Dwo2Exception {
      current = manager.updateModel(modelContext);
      structure = StoredRestManager.getInstance().getGenson().serialize(current.getModelStructure());
      remoteMethod = current.getModelStructure().getActiveMethod();
      standard = current.getPublishState() == PublishState.overt;
     return current;
    }
    
    public DomStudentModelStructure updateActiveMethod(DomStudentModelStructure model) throws Dwo2Exception {
        DomSchoolMethod dsm = manager.getActiveMethod(current);
        dsm.setActiveMethod(model.getActiveMethod());
        SecureTeacherStudentModelManager.updateActiveMethod(dsm);
        if (standard) {
        	current.setModelStructure(model);
       	}
       	return model;   	
    }
    
    
    public DomStudentModelStructure updateModel(DomStudentModelStructure model) throws Dwo2Exception {
      //if (standard)
      //{
        DomSchoolMethod dsm = current == null ? null : manager.getActiveMethod(current);
        if (dsm != null) {
	        dsm.setActiveMethod(model.getActiveMethod());
	        SecureTeacherStudentModelManager.updateActiveMethod(dsm);
	        if (standard) {
	        	current.setModelStructure(model);
	        	return model;
	      	}
        }

      if (structure != null) {      
        JavaPatch patch = new JavaPatch();
        Genson genson = StoredRestManager.getInstance().getGenson();
        String old = structure;
        //model.setActiveMethod(remoteMethod); // toch doorduwen.
        String now = genson.serialize(model);
        String diff = patch.createPatch(old, now);
        LOG.info("diff = " + diff);
        current.setModelStructure(model);
        //model.setActiveMethod(dsm.getActiveMethod());
        structure = now;
        //if(!"[]".equals(diff)) 
        	model = patchModel(diff, patch.digest).getModelStructure();
        standard = current.getPublishState() == PublishState.overt;
        return model;
      } else if (current == null) {
        current = new DomStudentModelContext();
        current.setModelStructure(model);
        current.setPublishState(PublishState.edit);
        current = addModel(current);
        return current.getModelStructure();
      }
      current.setModelStructure(model);
      model = updateModel(current).getModelStructure();
      //model.setActiveMethod(dsm.getActiveMethod());
      return model;
    }
        
    
    private DomStudentModelContext patchModel(String patch, String digest) throws Dwo2Exception {
      DomStudentModelContext context = current;
      DomStudentModelContextPatch domPatch = new DomStudentModelContextPatch(context);
      domPatch.setPatch(patch);
      domPatch.setDigest(digest);
      try {
        DomStudentModelContext result = manager.patchModel(domPatch);
        context.setLastChangeTimeStamp(result.getLastChangeTimeStamp());
        context.setOptLock(result.getOptLock());
        return context;
      } catch (Dwo2Exception e) {
        structure = null;
        if (e.getDwo2Code() == Dwo2ExceptionCode.Rest_ObjectModified  ||e.getDwo2Code() == Dwo2ExceptionCode.User_IllegalAction) throw e;
      } catch(Exception oops) {
        LOG.log(Level.WARNING, "should not happen", oops);
        structure = null;
      }
      return updateModel(context);
    }

    public DomStudentModelContext getModel(DomStudentModelContextId modelContext) throws Dwo2Exception {
       current = manager.get(modelContext);
       structure = StoredRestManager.getInstance().getGenson().serialize(current.getModelStructure());
       standard = current.getPublishState() == PublishState.overt;
       remoteMethod = current.getModelStructure().getActiveMethod();
       //if (standard)
       {
         DomSchoolMethod dsm = manager.getActiveMethod(modelContext);
         current.getModelStructure().setActiveMethod(dsm.getActiveMethod());
       }
       return current;
    }
    
    void removeModel(DomStudentModelContext modelContext) throws Dwo2Exception {
      SecureTeacherStudentModelManager.removeModel(modelContext);
      if(modelContext == getCurrent()) end();
    }
        
    private DomStudentModelContext current;
    private String structure;
    private boolean standard; // current is standard model

    public DomStudentModelContext getCurrent() {
      return current;
    }

    void setCurrent(DomStudentModelContext current) {
      this.current = current;
    }
    
    void end() {
      current = null;
      structure = null;
      standard = false;
    }

    private String getTitle(DomStudentModelContext m) {
      Map<String, String> title = m.getModelStructure().getInfo().getTitle();
      String locale = DwoHelper.getLocale().getLocale();
      return NodeVector.getTitle(title, locale);
    }
    
    @Override
    public int compare(DomStudentModelContext o1, DomStudentModelContext o2) {
      PublishState p1 = o1.getPublishState();
      PublishState p2 = o2.getPublishState();
      if (p1 == PublishState.overt && p2 != PublishState.overt) return -1;
      if (p2 == PublishState.overt && p1 != PublishState.overt) return +1;
      
      String s1 = getTitle(o1);
      String s2 = getTitle(o2);
      return s1.compareTo(s2);
    }
}
