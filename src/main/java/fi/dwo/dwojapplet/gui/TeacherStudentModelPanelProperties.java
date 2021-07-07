package fi.dwo.dwojapplet.gui;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.owlike.genson.Genson;

import fi.dwo.dwojapplet.domain.utils.Digest;
import nl.numworx.gwtpatch.client.GWTPatch;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;


/**
 *
 * @author Gert van der Plas
 */
public class TeacherStudentModelPanelProperties {
  
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

    TeacherStudentModelPanelProperties(){
        
    }

    void init() throws Dwo2Exception {
    }
    
    DomStudentModelContext addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return SecureTeacherStudentModelManager.addModel(modelContext);
    }

    List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        return SecureTeacherStudentModelManager.getReducedList();
    }

    DomStudentModelContext updateModel(DomStudentModelContext modelContext) throws Dwo2Exception {
      current = SecureTeacherStudentModelManager.updateModel(modelContext);
      structure = StoredRestManager.getInstance().getGenson().serialize(current.getModelStructure());
      return current;
    }
    
    public DomStudentModelStructure updateModel(DomStudentModelStructure model) throws Dwo2Exception {
      if (structure != null) {      
        JavaPatch patch = new JavaPatch();
        Genson genson = StoredRestManager.getInstance().getGenson();
        String old = structure;
        String now = genson.serialize(model);
        String diff = patch.createPatch(old, now);
        LOG.info("diff = " + diff);
        current.setModelStructure(model);
        structure = now;
        model = patchModel(diff, patch.digest).getModelStructure();
        return model;
      }
      current.setModelStructure(model);
      return updateModel(current).getModelStructure();
    }
        
    
    private DomStudentModelContext patchModel(String patch, String digest) throws Dwo2Exception {
      DomStudentModelContext context = current;
      DomStudentModelContextPatch domPatch = new DomStudentModelContextPatch(context);
      domPatch.setPatch(patch);
      domPatch.setDigest(digest);
      try {
        DomStudentModelContext result = SecureTeacherStudentModelManager.patchModel(domPatch);
        context.setLastChangeTimeStamp(result.getLastChangeTimeStamp());
        context.setOptLock(result.getOptLock());
        return context;
      } catch (Dwo2Exception e) {
        structure = null;
        if (e.getDwo2Code() == Dwo2ExceptionCode.Rest_ObjectModified) throw e;
      } catch(Exception oops) {
        LOG.log(Level.WARNING, "should not happen", oops);
        structure = null;
      }
      return updateModel(context);
    }

    public DomStudentModelContext getModel(DomStudentModelContextId modelContext) throws Dwo2Exception {
       current = SecureTeacherStudentModelManager.get(modelContext);
       structure = StoredRestManager.getInstance().getGenson().serialize(current.getModelStructure());
       return current;
    }
    
    void removeModel(DomStudentModelContext modelContext) throws Dwo2Exception {
      SecureTeacherStudentModelManager.removeModel(modelContext);
      if(modelContext == getCurrent()) setCurrent(null);
    }
        
    private DomStudentModelContext current;
    private String structure;

    public DomStudentModelContext getCurrent() {
      return current;
    }

    void setCurrent(DomStudentModelContext current) {
      this.current = current;
    }
    
    void end() {
      current = null;
      structure = null;
    }
}
