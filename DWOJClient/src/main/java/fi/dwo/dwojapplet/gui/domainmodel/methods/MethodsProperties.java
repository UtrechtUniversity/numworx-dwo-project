package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsProperties extends Vector<DomMethod> implements Comparator<DomMethod>{
  private static final Logger LOG = Logger.getLogger(MethodsProperties.class.getName());

  private final Genson genson;

  MethodsProperties() {
      super(3);  
      genson = StoredRestManager.getInstance().getGenson();
      try {
        InputStream in;
        DomMethod row;
        in = DwoHelper.getAu().getStream("resources/none.json");
        if (in != null) {
        	row = genson.deserialize(in, DomMethod.class);
        	add(row);
        	in.close();
        } else {
        	row = new DomMethod(); row.method = "No Method"; row.standard=true;
        	row.books = Collections.emptyList();
        	row.chapters = Collections.emptyList();
        	row.edges = Collections.emptyList();
        	add(row);
        }
        
        List<DomMethod> list = getCurrentList();
        Collections.sort(list,this);
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

  public MethodsProperties reversed() { return this; } // dummy, nodig voor java 21
  
  public DomMethod delete(int i) {
    DomMethod m = remove(i);
    try {     
      SecureTeacherMethodManager.removeMethod(m, DWO.getDwoProfile());
    } catch (Dwo2Exception e) {
      LOG.log(Level.SEVERE, "failed remove", e);
    }
    return m;
  }
  
  
  private List<DomMethod> getCurrentList() throws Dwo2Exception {
    if (DwoHelper.isAdminLoggedIn())
      return SecureDwoAdminMethodManager.getList(DWO.getDwoProfile());
    if (DwoHelper.isContact())
      return SecureSchoolAdminMethodManager.getList(DWO.getDwoProfile());
    return SecureTeacherMethodManager.getList(DWO.getDwoProfile());
  }; 
  
  public void refresh() {
    try {
      List<DomMethod> list = getCurrentList();
      Collections.sort(list, this);
      removeRange(1, size());
      addAll(list);
    } catch(Exception e) {
      LOG.log(Level.WARNING, "refresh", e);
    }
  }
  
  private volatile static MethodsProperties instance;
    
  public synchronized static MethodsProperties instance() {
    if (instance == null) return reset();
    return instance;
  }
  
  public synchronized static void unset() {
    instance = null;
  }
  
  public synchronized static MethodsProperties reset() {
    return instance = new MethodsProperties();
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
      row = SecureTeacherMethodManager.addModel(row, DWO.getDwoProfile());
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

  @Override
  public int compare(DomMethod o1, DomMethod o2) {
    if (o1.standard && !o2.standard) return -1;
    if (o2.standard && !o1.standard) return +1;
    String s1 = o1.getMethod();
    String s2 = o2.getMethod();
    return s1.compareTo(s2);
  }

}
