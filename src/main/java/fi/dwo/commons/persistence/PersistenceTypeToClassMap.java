/*Copyrighted 2015. */
package fi.dwo.commons.persistence;

import fi.dwo.commons.rest.RestClassType;
import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.EnumMap;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class PersistenceTypeToClassMap {
    private static final Logger LOG = Logger.getLogger(PersistenceTypeToClassMap.class.getName());

    protected static final EnumMap<RestClassType, Class> classCastMap;

    static {
        EnumMap<RestClassType, Class> tMap = new EnumMap<RestClassType, Class>(RestClassType.class);
        
        tMap.put(RestClassType.PersistentUser, PersistentUser.class);
        
        classCastMap = (EnumMap<RestClassType, Class>) tMap;
    }

    public static Class get(RestClassType type){
        return classCastMap.get(type);
    }
}
