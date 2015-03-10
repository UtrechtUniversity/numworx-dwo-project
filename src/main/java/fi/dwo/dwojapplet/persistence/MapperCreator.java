// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\MapperCreator.java
package fi.dwo.dwojapplet.persistence;

import java.util.Hashtable;

/**
 * This class creates the mappers which can map database-hashtables on objects.
 * e.g. the data out the coursetable are read out of the database and put in a
 * hashtable (on the serverside). These hashtable is converted in a Course
 * object.
 * <img src="doc-files/MapperCreator-1.gif" alt="diagram of the converting of a hashtable to a Course-object">
 *
 * @author M.J.B. Kupers
 *
 */
class MapperCreator {

    private final static String DOMAIN_PACKAGE = "fi.dwo.dwojapplet.domain";

    private final static String PERSISTENCE_PACKAGE = "fi.dwo.dwojapplet.persistence";

    /**
     * A list of all the mappers of objects in the DWO. e.g. The Course is in
     * the DOMAIN_PACKAGE and the CourseMapper in the PERSISTENCE_PACKAGE
     */
    private final static String mapperDefenitionList[][] = {
        {"User", "UserMapper"},
        {"School", "SchoolMapper"},
        {"Group", "GroupMapper"},
        {"SchoolGroup", "SchoolGroupMapper"},
        {"Course", "CourseMapper"},
        {"Sco", "ScoMapper"},
        {"SchoolClass", "ClassMapper"},
        {"UserResultList", "UserResultListMapper"},
        {"AppletConfig", "AppletConfigMapper"},
        {"DwoProfile", "DwoProfileMapper"},
        {"AppletData", "AppletDataMapper"},
        {"CourseSequence", "CourseSequenceMapper"},
        {"ClassCourse", "ClassCourseMapper"},};

    /**
     * The mappers for external classes. e.g. The Applet-class is not in the
     * DOMAIN_PACKAGE
     */
    private final static String mapperDefenitionOtherList[][] = {
        {"java.applet.Applet", PERSISTENCE_PACKAGE + ".AppletMapper"}
    };

    private static Hashtable mapperList;

    private static Hashtable classList;

    /**
     *
     */
    public MapperCreator() {

    }

    /**
     * Fills the classList Hashmap with as key SchoolClass-objects, and as value
     * the name of the mapper class (including package)
     */
    private static void createClasses() {
        int i;
        classList = new Hashtable();
        java.lang.Class c;
        for (i = 0; i < mapperDefenitionList.length; i++) {
            try {
                c = Class.forName(DOMAIN_PACKAGE + "."
                        + mapperDefenitionList[i][0]);
                classList.put(c, PERSISTENCE_PACKAGE + "."
                        + mapperDefenitionList[i][1]);
            } catch (ClassNotFoundException e) {
            }
        }
        for (i = 0; i < mapperDefenitionOtherList.length; i++) {
            try {
                c = Class.forName(mapperDefenitionOtherList[i][0]);
                classList.put(c, mapperDefenitionOtherList[i][1]);
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Creates an instance of the corresponding mapper.
     *
     * @param c The class wherefor the mapper must be created.
     */
    private static void addMapper(Class c) {
        String className = (String) classList.get(c);
        try {
            java.lang.Class mapperClass = Class.forName(className);
            mapperList.put(c, mapperClass.newInstance());
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Returns an instance of a mapper for the corresponding class. For example,
     * if c is <code>Course</code> then a CourseMapper is returned.
     *
     * @param c The class wherefrom the mapper must be returned.
     * @return The corresponding mapper.
     */
    public static MapperIF instance(Class c) {
        if (mapperList == null) {
            mapperList = new Hashtable();
            createClasses();
        }

        /* Did we create a mapper before? */
        if (mapperList.containsKey(c)) {
            return (MapperIF) mapperList.get(c);
        } else {
            /* Create a new mapper */
            addMapper(c);
            if (mapperList.containsKey(c)) {
                return (MapperIF) mapperList.get(c);
            } else {
                /* We don't know the class and his mapper */
                return null;
            }
        }
    }
}
