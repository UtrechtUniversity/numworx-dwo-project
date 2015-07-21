/**
 * Copyrighted Jul 20, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentEntityManagers.SchoolManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Initializes a test school with users. No results (yet).
 * 
 * @author G.A.J. van der Plas
 */
public class InitalizeTestSchool {
    
    public InitalizeTestSchool() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRoles method, of class PublicRoleManager.
     */
    @Test
    public void CreateSchool() {
        System.out.println("school check");
        
        PersistentSchool school = new PersistentSchool();
        school.setSchoolName("DWO2TestSchool");
        school.setSchoolRights("");
        school.setSchoollogin("dwo2testschool");
        school.setExport(Boolean.TRUE);
        school.setExpire(null);
        
        SchoolManager.create(school);
        PersistentSchool result = SchoolManager.findBySchoolName("DWO2TestSchool");
        assertEquals(school, result);
    }
    
}
