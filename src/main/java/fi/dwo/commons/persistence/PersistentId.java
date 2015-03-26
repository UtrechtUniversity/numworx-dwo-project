/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence;

import java.util.EnumMap;

/**
 * Abstract class defining a persistentId with a natural ordering.
 * 
 * No attributes of a persistentId should be read or set other than during 
 * construction and for transporting (i.e. XML-RPC,JSON).
 *
 * @author plas0006
 */
public class PersistentId implements PersistentIdInterface {

    private int id;
    private PersistentIdClassTypes idClass;

    private static final EnumMap<PersistentIdClassTypes, Class> classCastMap;

    static {
        EnumMap<PersistentIdClassTypes, Class> tMap = new EnumMap<PersistentIdClassTypes, Class>(PersistentIdClassTypes.class);
//        tMap.put(PersistentIdClassTypes.persistentUser, PersistentUser.class);
//        tMap.put(PersistentIdClassTypes.persistentStudent, PersistentStudent.class);
//        tMap.put(PersistentIdClassTypes.persistentTeacher, PersistentTeacher.class);
//        tMap.put(PersistentIdClassTypes.persistentSchool, PersistentSchool.class);
//        tMap.put(PersistentIdClassTypes.persistentCourse, PersistentCourse.class);
        classCastMap = (EnumMap<PersistentIdClassTypes, Class>) tMap;//Collections.unmodifiableMap(tMap);
    }
    
    /**
     * Constructs a persistentId.
     * 
     * @param aId
     * @param aIdClass 
     */
    public PersistentId(int aId, PersistentIdClassTypes aIdClass) {
        id = aId;
        idClass = aIdClass;
    }


    /**
     * Defines an uninitialized persistentId.
     */
    public PersistentId() {
        id = -1;
        idClass = PersistentIdClassTypes.none;
    }
    
    /**
     * Defines a natural order for PeristentId objects.
     *
     * First a comparison is done on lexicographical ordering on the Class and
     * then a natural ordering is done on the integer id value.
     *
     * @param pid
     * @return
     */
    @Override
    public int compareTo(PersistentId pid) {

        if (pid.equals(pid)) {
            return 0;
        }

        int r = idClass.name().compareTo(pid.idClass.name());
        if (r != 0) {
            return r;
        } else {
            if (id == pid.id) {
                return 0;
            } else if (id < pid.id) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        PersistentId pid = (PersistentId) o;
        return compareTo(pid);
    }

}
