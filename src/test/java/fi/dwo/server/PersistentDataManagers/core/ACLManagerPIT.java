/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * SchoolManager persistence integration tests (PIT).  <p/>
 * 
 * Light testing. Every method assumes other SchoolManager methods work proper.
 *
 * @author G.A.J. van der Plas
 */
public class ACLManagerPIT {

    PersistentACL acl1 = new PersistentACL();
    PersistentACL acl2 = new PersistentACL();

    static DatabaseManager instance = null;
    
    public ACLManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }
    
    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    PersistenceId jip = PersistentSchoolClass.buildPersistenceId(1L);
    PersistenceId janneke = PersistentSchool.buildPersistenceId(1L);
    
    @Before
    public void setUp() {
      instance.IntializeTestDatabase();
      acl1.setAccess(ACL.ACCESS);
      acl1.setEntity(jip.getIdString());
      acl1.setCourseID(1L);
      acl1.setSchoolID(1L);
      acl1.setDwoProfileID(1L);
 
      acl2.setAccess(ACL.ACCESS);
      acl2.setEntity(janneke.getIdString());
      acl2.setCourseID(1L);
      acl2.setSchoolID(1L);
      acl2.setDwoProfileID(1L);
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }
   
    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testCRUD() throws Exception {
        // create
        System.out.println("create ACL");
        ACLManager.create(acl1);
        ACLManager.create(acl2);
         
        // recreate
        try{
        System.out.println("create school again");
            //should fail
        ACLManager.create(acl1);
            fail("ACLManager.create() did not fail creating a copy of a ACL.");
        }catch(PersistenceException e){
            //succeeded
        }
        
        //read 
        List<PersistentACL> list;
        System.out.println("read acls");
        list = ACLManager.findByCourse(new PersistentCourse(acl1.getCourseID()));
        
        //update proper 
        System.out.println("update acl");
        PersistentACL acl;
        acl = ACLManager.findEntity(acl1.getAclID());
        acl.setAccess(ACL.FULL);
        ACLManager.edit(acl);
        acl = ACLManager.findEntity(acl1.getAclID());
        assertNotEquals("ACLManager.edit() failed.",acl1.getAccess(),acl.getAccess());
        //update should fail
        try{
          acl = ACLManager.findEntity(acl1.getAclID());
          acl.setEntity(acl2.getEntity());
          ACLManager.edit(acl);
          fail("ACLManager.edit() failed.");
        }catch(PersistenceException e){
            //works
        }
        
        //delete 
        System.out.println("delete acl");
        acl = ACLManager.findEntity(acl1.getAclID());
        ACLManager.destroy(acl.getAclID());
        acl = ACLManager.findEntity(acl2.getAclID());
        ACLManager.destroy(acl.getAclID());
        list = ACLManager.findByCourse(new PersistentCourse(acl2.getCourseID()));
        assertTrue("ACLManager.destroy() failed.",list.isEmpty());
        }    
    
    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test @Ignore
    public void testCreate() {
//        //create
//        try {
//            SchoolManager.create(schoolA);
//            SchoolManager.create(schoolB);
//            PersistentSchool schoolOne = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            PersistentSchool schoolTwo = SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin());
//            if ((!schoolA.similar(schoolOne)) || (!schoolB.similar(schoolTwo))) {
//                fail("School created is different.");
//            }
//        }
//        catch (Exception e) {
//            fail("Exception during create.");
//        }
//        //recreate
//        try {
//            SchoolManager.create(schoolA);
//            SchoolManager.create(schoolB);
//            fail("Creating double copy should not work.");
//        }
//        catch (Exception e) {
//            //works!
//        }
//
//        //cleanup
//        try {
//            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin()).getSchoolID());
//            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin()).getSchoolID());
//        }
//        catch (Exception e) {
//            fail("Exception during destroy.");
//        }
    }

    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test @Ignore
    public void testEdit() {
//        SchoolManager.create(schoolA);
//        SchoolManager.create(schoolB);
//        // edit
//        try {
//            System.out.println("update school");
//            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            school.setSchoolName(schoolB.getSchoolName());
//            SchoolManager.edit(school);
//            school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            if (school.getSchoolName().compareTo(schoolB.getSchoolName()) != 0) {
//                fail("SchoolManager.edit() failed.");
//            }
//            school.setSchoolName(schoolA.getSchoolName());
//            if (!school.similar(schoolA)) {
//                fail("SchoolManager.edit() failed.");
//            }
//        }
//        catch (Exception e) {
//            fail("SchoolManager.edit() failed.");
//        }
//
//        //update should fail
//        try {
//            System.out.println("update school");
//            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            school.setSchoolLogin(schoolB.getSchoolLogin());
//            SchoolManager.edit(school);
//            school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            if (school == null) {
//                fail("SchoolManager.edit() failed. School disappeared.");
//            }
//        }
//        catch (Exception e) {
//            // works!
//        }
//
//        //cleanup
//        try {
//            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin()).getSchoolID());
//            SchoolManager.destroy(SchoolManager.findBySchoolLogin(schoolB.getSchoolLogin()).getSchoolID());
//        }
//        catch (Exception e) {
//            fail("Exception during destroy.");
//        }
    }

    /**
     * Test of destroy method, of class SchoolManager.
     */
    @Test @Ignore
    public void testDestroy() {
//        SchoolManager.create(schoolA);
//        System.out.println("destroy");
//        Integer id = null;
//        try {
//            PersistentSchool school = SchoolManager.findBySchoolLogin(schoolA.getSchoolLogin());
//            SchoolManager.destroy(school.getSchoolID());
//            try {
//                school = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
//                if (school != null) {
//                    fail("School not destroyed.");
//                }
//            }
//            catch (Exception e) {
//                //works
//            }
//        }
//        catch (Exception e) {
//            fail("Exception during destroy.");
//        }
    }

    /**
     * Test of findEntities method, of class SchoolManager.
     */
    //    @Test
    public void testFindEntities_0args() {
//        System.out.println("findEntities");
//        List<PersistentSchool> expResult = null;
//        List<PersistentSchool> result = SchoolManager.findEntities();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntities method, of class SchoolManager.
     */
//    @Test
    public void testFindEntities_int_int() {
//        System.out.println("findEntities");
//        int maxResults = 0;
//        int firstResult = 0;
//        List<PersistentSchool> expResult = null;
//        List<PersistentSchool> result = SchoolManager.findEntities(maxResults, firstResult);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findEntity method, of class SchoolManager.
     */
    public void testFindEntity() {
//        System.out.println("findEntity");
//        Long id = null;
//        PersistentSchool expResult = null;
//        PersistentSchool result = SchoolManager.findEntity(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntityCount method, of class SchoolManager.
     */
    public void testGetEntityCount() {
//        System.out.println("getEntityCount");
//        int expResult = 0;
//        int result = SchoolManager.getEntityCount();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findByCourse method, of class ACLManager.
     */
    @Test 
    public void testFindByCourse() throws Exception {
      PersistentCourse c = new PersistentCourse(acl1.getCourseID());
      c.setDwoProfileID(acl1.getDwoProfileID());
      c.setSchoolID(acl1.getSchoolID());
      ACLManager.create(acl1);

      List<PersistentACL> list = ACLManager.findByCourse(c);
      list.add(acl2);
      list = ACLManager.updateByCourse(c, list);
      assertEquals(2, list.size());
      assertNotNull(list.get(1).getAclID());
      list.remove(0);
      list = ACLManager.updateByCourse(c, list);
      assertEquals(1, list.size());
      assertNull(ACLManager.findEntity(acl1.getAclID()));
      ACLManager.updateByCourse(c, Collections.emptyList());
    }
    /**
     * Test of findBySchool method, of class ACLManager.
     */
    @Test 
    public void testFindBySchool() throws Exception {
      PersistentCourse c = new PersistentCourse(acl1.getCourseID());
      c.setDwoProfileID(acl1.getDwoProfileID());
      c.setSchoolID(acl1.getSchoolID());
      ACLManager.create(acl1);

      PersistentDwoProfile p = new PersistentDwoProfile(acl1.getDwoProfileID());
      PersistentSchool s = new PersistentSchool(acl1.getSchoolID());
      List<PersistentACL> list = ACLManager.findBySchool(s,p);      
      assertEquals("size ", 1, list.size());
    }

}
