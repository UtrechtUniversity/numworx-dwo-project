package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsProperties extends ArrayList<DomMethod> {
  private static final Logger LOG = Logger.getLogger(MethodsProperties.class.getName());

  private final Genson genson;

  MethodsProperties() {
      super(3);  
      genson = StoredRestManager.getInstance().getGenson();
      try {
        InputStream in;
        DomMethod row;
        in = getClass().getResourceAsStream("none.json");
        row = genson.deserialize(in, DomMethod.class);
        add(row);
        in.close();
        
        List<DomMethod> list = SecureTeacherMethodManager.getList();
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
       
  }; 
  
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
      if (row.getId() != null) {
        String key = row.key();
        for (int i = 0; i < row.books.length; i++ ) {
          String book = row.books[i];
          String[] chapters = row.chapters[i];
          for (int j = 0; j < chapters.length; j++) {
            result.put(key + "-" + book + "-" + String.valueOf(j+1), chapters[j]);
          }
        }
      }
    }
    return result;
  }

  public DomMethod getMethod(PersistenceId activeMethod) {
    for (DomMethod row: this) {
      if (Objects.equals(activeMethod, row.getId())) return row;
    }
    DomMethod dm = new DomMethod(activeMethod);
    dm.books = new String[0];
    dm.chapters = new String[0][];
    dm.method  = "Unknown method " + dm.key();
    
    return dm;
  }


  public Map<String, String> getBookDescriptionsMap(
      PersistenceId activeMethod) {
    Map<String,String> result = new LinkedHashMap<String,String>();
    for (DomMethod row: this) {
      if (row.getId() != null) {
        String key = row.key();
        for (int i = 0; i < row.books.length; i++ ) {
          String book = row.books[i];
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
        String idString = ("MYSQL;PersistentMethod;" + (random.nextLong() >>> 1));
        row.setId(new PersistenceId(idString));
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
        if (row.edges != null) return row.edges;
      }
    }
    // TODO Auto-generated method stub
    return new int[0][];
  }

}
