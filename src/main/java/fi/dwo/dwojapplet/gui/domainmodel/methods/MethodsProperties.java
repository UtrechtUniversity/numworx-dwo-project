package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.owlike.genson.Genson;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsProperties extends ArrayList<Row> {
  private static final Logger LOG = Logger.getLogger(MethodsProperties.class.getName());

  private final Genson genson;

  MethodsProperties() {
      super(3);  
      genson = StoredRestManager.getInstance().getGenson();
      try {
        InputStream in;
        Row row;
        in = getClass().getResourceAsStream("none.json");
        row = genson.deserialize(in, Row.class);
        add(row);
        in.close();
        
        in = getClass().getResourceAsStream("Getal&Ruimte.json");
        row = genson.deserialize(in, Row.class);
        add(row);
        in.close();
        
        in = getClass().getResourceAsStream("Moderne Wiskunde.json");
        row = genson.deserialize(in, Row.class);
        add(row);
        in.close();
        
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
    for (Row row: this) {
      if (row.id != null) {
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

  public Row getMethod(PersistenceId activeMethod) {
    for (Row row: this) {
      if (Objects.equals(activeMethod, row.id)) return row;
    }
    return null;
  }

}
