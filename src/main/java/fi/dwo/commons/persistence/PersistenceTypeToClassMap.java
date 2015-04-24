/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.EnumMap;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class PersistenceTypeToClassMap {
    private static final Logger log = Logger.getLogger(PersistenceTypeToClassMap.class.getName());

    protected static final EnumMap<PersistenceClassType, Class> classCastMap;

    static {
        EnumMap<PersistenceClassType, Class> tMap = new EnumMap<PersistenceClassType, Class>(PersistenceClassType.class);
        
        tMap.put(PersistenceClassType.PersistentUser, PersistentUser.class);
        
        classCastMap = (EnumMap<PersistenceClassType, Class>) tMap;
    }

    public static Class get(PersistenceClassType type){
        return classCastMap.get(type);
    }
}
