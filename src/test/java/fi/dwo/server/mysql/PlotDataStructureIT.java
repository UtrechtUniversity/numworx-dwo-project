package fi.dwo.server.mysql;

import fi.dwo.server.rest.*;
import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class PlotDataStructureIT {

    private static final Logger LOG = Logger.getLogger(PlotDataStructureIT.class.getName());

    static DatabaseManager instance = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    PublicScoContextManager manager;

    @Before
    public void setUp() throws Exception {
        instance.IntializeTestDatabase();
        manager = new PublicScoContextManager();
    }

    @After
    public void tearDown() throws Exception {
        instance.ClearDatabase();
    }

    @Test
    public void plotDataStructure() throws Exception {
        String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
        System.out.print("\n\n");
        for (PersistentUser u : UserManager.findEntities()) {
            System.out.print(tabs.substring(0, 1) + u.buildPersistenceId() + " " + u.getUsername() + "\n");
        }
        System.out.print(tabs.substring(0, 0) + "null public\n");
        for (PersistentCourse nc : CourseManager.findEntities(new PersistentSchool())) {
            System.out.print(tabs.substring(0, 1) + nc.buildPersistenceId() + " " + nc.getName() + "\n");
        }
        for (PersistentSchool s : SchoolManager.findEntities()) {
            System.out.print(tabs.substring(0, 0) + s.buildPersistenceId() + " " + s.getSchoolName() + "\n");
            for (PersistentCourse c : CourseManager.findEntities(s)) {
                System.out.print(tabs.substring(0, 1) + c.buildPersistenceId() + " " + c.getName() + "\n");
            }
            for (PersistentSchoolGroup sg : SchoolGroupManager.findEntity(s)) {
                System.out.print(tabs.substring(0, 2) + sg.buildPersistenceId() + " " + sg.getRole().getGroupname() + "\n");
                for (PersistentHasRole hr : HasRoleManager.findEntities(sg)) {
                    System.out.print(tabs.substring(0, 3) + hr.buildPersistenceId() + "\n");
                }
            }

            System.out.print("\n\n");
        }
    }
}
