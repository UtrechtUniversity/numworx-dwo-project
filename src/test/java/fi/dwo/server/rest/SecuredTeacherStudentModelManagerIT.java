/** Copyrighted Feb 12, 2018 */
package fi.dwo.server.rest;

import java.util.List;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author plas0006
 */
public class SecuredTeacherStudentModelManagerIT {
    
    public SecuredTeacherStudentModelManagerIT() {
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
     * Test of getStudentModels method, of class SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGetStudentModels() {
        System.out.println("getStudentModels");
        SecurityContext sc = null;
        RestContext context = null;
        SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
        List<DomStudentModelContext> expResult = null;
        List<DomStudentModelContext> result = instance.getStudentModels(sc, context);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addStudentModel method, of class SecuredTeacherStudentModelManager.
     */
    @Test
    public void testAddStudentModel() {
        System.out.println("addStudentModel");
        SecurityContext sc = null;
        RestStudentModelContext model = null;
        SecuredTeacherStudentModelManager instance = new SecuredTeacherStudentModelManager();
        DomStudentModelContext expResult = null;
        DomStudentModelContext result = instance.addStudentModel(sc, model);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
