package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;

import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsProperties extends Vector<DomMethod> {
  private static final Logger LOG = Logger.getLogger(MethodsProperties.class.getName());

  private final Genson genson;

  MethodsProperties() {
      super(3);  
      genson = StoredRestManager.getInstance().getGenson();
      try {
        InputStream in;
        DomMethod row;
        in = DwoHelper.getAu().getStream("resources/none.json");
        row = genson.deserialize(in, DomMethod.class);
        add(row);
        in.close();
        
        List<DomMethod> list = getCurrentList();
        addAll(list);
        
//        in = getClass().getResourceAsStream("Getal&Ruimte.json");
//        row = genson.deserialize(in, DomMethod.class);
//        add(row);
//        in.close();
//        
//        in = getClass().getResourceAsStream("Moderne Wiskunde.json");
//        row = genson.deserialize(in, DomMethod.class);
//        add(row);
//        in.close();
        
      } catch (Exception e) {
        LOG.log(Level.WARNING, "load initial methods", e);
      }
       
  }

  private List<DomMethod> getCurrentList() throws Dwo2Exception {
    if (DwoHelper.isAdminLoggedIn())
      return SecureDwoAdminMethodManager.getList();
    if (DwoHelper.isContact())
      return SecureSchoolAdminMethodManager.getList();
    return SecureTeacherMethodManager.getList();
  }; 
  
  void refresh() {
    try {
      List<DomMethod> list = getCurrentList();
      removeRange(1, size());
      addAll(list);
    } catch(Exception e) {
      LOG.log(Level.WARNING, "refresh", e);
    }
  }
  
  private static MethodsProperties instance = new MethodsProperties();
    
  public static MethodsProperties instance() {
    return instance;
  }
  
  public static void reset() {
    instance = new MethodsProperties();
  }

  public Map<String, String> getDescriptionsMap(PersistenceId activeMethod) {
    Map<String,String> result = new TreeMap<String,String>();
    for (DomMethod row: this) {
      if (row.getId() != null
          && row.getId().equals(activeMethod)
          ) {
        String key = row.key();
        for (int i = 0; i < row.books.size(); i++ ) {
          String book = row.books.get(i);
          List<String> chapters = row.chapters.get(i);
          for (int j = 0; j < chapters.size(); j++) {
            result.put(key + "-" + book + "-" + String.valueOf(j+1), chapters.get(j));
          }
        }
      }
    }
    return result;
  }

  private Optional<DomMethod> getMethod0(PersistenceId pid) {
    for (DomMethod row: this) {
      if (Objects.equals(pid, row.getId())) return Optional.of(row);
    }    
    return Optional.empty();
  }
    
  public DomMethod getMethod(PersistenceId activeMethod) {
    return getMethod0(activeMethod).orElseGet(() -> {
      DomMethod dm = new DomMethod(activeMethod);
      dm.books = Collections.emptyList();
      dm.chapters = Collections.emptyList();
      dm.edges = Collections.emptyList();
      dm.method = "Unknown method " + dm.key();
      dm.standard = true;
      return dm;
    });
  }

  public Map<String, String> getBookDescriptionsMap(
      PersistenceId activeMethod) {
    Map<String,String> result = new LinkedHashMap<String,String>();
    for (DomMethod row: this) {
      if (row.getId() != null
          && row.getId().equals(activeMethod)
          ) {
        String key = row.key();
        for (int i = 0; i < row.books.size(); i++ ) {
          String book = row.books.get(i);
          result.put(key + "-" + book, book);
        }
      }
    }
    return result;
  }

  Random random = new Random();

  public DomMethod persist(DomMethod row) {
    
    try {
      if (row.getId() == null) {
        PersistenceId id;
        do {
          String idString = ("MYSQL;PersistentMethod;" + (random.nextLong() >>> 1));
          id = new PersistenceId(idString);
        } while(getMethod0(id).isPresent());
        row.setId(id);
      } else {
        Optional<DomMethod> m = getMethod0(row.getId());
        if (m.isPresent()) {
          remove(m.get());
          row.setOptLock(m.get().getOptLock());
          return row;
        }
        
        
      }
      row = SecureTeacherMethodManager.addModel(row);
    } catch (Dwo2Exception e) {
      LOG.log(Level.SEVERE, "add method", e);
    }
    return row;
  }

  public int[][] getBookEdges(PersistenceId activeMethod) {
    for( DomMethod row: this) {
      if (Objects.equals(activeMethod, row.getId())) {
        if (row.edges != null) {
          int[][] result = new int[row.edges.size()][];
          for (int i = 0; i < result.length; i++) {
            List<Integer> li = row.edges.get(i);
            int[] ri = result[i] = new int[li.size()];
            for (int j = 0; j < ri.length; j++) {
              ri[j] = li.get(j);
            }
          }
          return result;
        }
      }
    }
    return new int[0][];
  }

}
