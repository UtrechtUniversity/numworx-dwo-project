// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\server\\persistence\\DbAccess.java
package fi.dwo.server.persistence;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;

/**
 * Provides handles persistent entity operations on the database.
 *
 *
 */
class DbAccess {

    private static final Logger LOG = Logger.getLogger(DbAccess.class.getName());

  
    public static boolean checkPersistentVersion() {
      try {
      List<PersistentDwoSystemParameters> list = DwoSystemParametersManager.findEntities();
      Map<String,String> hashMap = list.stream().collect(Collectors.toMap(PersistentDwoSystemParameters::getName, PersistentDwoSystemParameters::getValue));
      if ( (
          hashMap.get("DBVersion Major").matches("1") 
              && hashMap.get("DBVersion Minor").matches("5") 
              && hashMap.get("DBVersion Revision").matches("6")
          ) 
          ||
          (
          hashMap.get("DBVersion Major").matches("1") 
              && hashMap.get("DBVersion Minor").matches("5") 
              && hashMap.get("DBVersion Revision").matches("7")
          )
          
          ) {
      LOG.log(Level.INFO, "We are compatible with the database model version: {0}.{1}.{2}",
              new Object[]{hashMap.get("DBVersion Major"),
                  hashMap.get("DBVersion Minor"),
                  hashMap.get("DBVersion Revision")});

  } else {
      LOG.log(Level.SEVERE, "Database is version {0}.{1}.{2} and not compatible with this war. Exiting.",
              new Object[]{hashMap.get("DBVersion Major"),
                  hashMap.get("DBVersion Minor"),
                  hashMap.get("DBVersion Revision")});
      return true;
  }
} catch (Exception ex) {
  LOG.log(Level.SEVERE, "Database model version of server not compatible with this war. Missing version numbers. Exiting.", ex);
  return true;
}
return false; // all ok...
    }
}
