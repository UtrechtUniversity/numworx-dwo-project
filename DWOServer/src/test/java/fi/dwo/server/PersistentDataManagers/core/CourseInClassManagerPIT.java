/**
 * Copyrighted Sep 22, 2017
 */
package fi.dwo.server.PersistentDataManagers.core;

/**
 *
 * @author Gert van der Plas
 */
public class CourseInClassManagerPIT {
//
//    PersistentCourseInClass cicA = new PersistentCourseInClass();
//    PersistentCourseInClass cicB = new PersistentCourseInClass();
//
//    static DatabaseManager instance = null;
//
//    @BeforeClass
//    public static void setUpClass() {
//        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
//        instance = new DatabaseManager();
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        DwoEmfFactory.setDefaultEntityManagerFactory();
//        instance = null;
//    }
//
//    @Before
//    public void setUp() {
//        instance.IntializeTestDatabase();
//    }
//
//    @After
//    public void tearDown() {
//        instance.ClearDatabase();
//    }
//
//    public CourseInClassManagerPIT() {
//        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
//    }
//
//    /**
//     * Test of create method, of class CourseInClassManager.
//     */
//    @Test
//    public void testCreate() {
//        System.out.println("create");
//
//        //recreate with double class/course id
//        try {
//            PersistentCourseInClass cc = PersistentCourseInClass.buildEmptyPersistentCourseInClass();
//            cc.setClassID(2);
//            PersistentCourse pc = new PersistentCourse();
//            pc.setCourseID(6L);
//            cc.setCourse(pc);
//            CourseInClassManager.create(cc);
//            fail("Creating double copy should not work."); //unless nosql
//        } catch (Exception e) {
//            //success
//        }
//        //create
//        try {
//            PersistentCourseInClass cc = PersistentCourseInClass.buildEmptyPersistentCourseInClass();
//            cc.setClassID(1);
//            PersistentCourse pc = new PersistentCourse();
//            pc.setCourseID(7L);
//            cc.setCourse(pc);
//            CourseInClassManager.create(cc);
//        } catch (Exception e) {
//            fail("Failed to create legit classcourse.");
//        }
//    }
//
//    /**
//     * Test of findEntity method, of class CourseInClassManager.
//     */
//    @Test
//    public void testFindEntity() {
//        System.out.println("findEntity");
//        Long id = 1L;
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getClassCourseID(), id.longValue());
//        assertEquals(result.getClassID(), 1);
//        assertEquals(result.getCourseID().longValue(), 1);
//        // TODO review the generated test code and remove the default call to fail.
//    }
//
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEdit() {
//        System.out.println("edit");
//        Long id = 1L;
//        PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//        final Date first = DwoDateUtilities.getStartOfDay();
//        final Date last = DwoDateUtilities.getEndOfDay();
//        cc.setNotBefore(first);
//        cc.setNotAfter(last);
//        cc.setType(1);
//        cc.setViewState(ViewState.studentsAndTeachers);
//        CourseInClassManager.edit(cc);
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getType(), Integer.valueOf(1));
//        assertEquals(result.getViewState(), ViewState.studentsAndTeachers);
//        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
//        assertEquals(result.getNotAfter().toString(), last.toString());
//    }
//
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEditViewState() {
//        System.out.println("edit");
//        Long id = 1L;
//        PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//        final Date first = DwoDateUtilities.getStartOfDay();
//        final Date last = DwoDateUtilities.getEndOfDay();
//        cc.setViewState(ViewState.studentsAndTeachers);
//        CourseInClassManager.edit(cc);
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getType(), cc.getType());
//        assertEquals(result.getViewState(), ViewState.studentsAndTeachers);
//        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
//        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
//    }
//
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEditType() {
//        System.out.println("edit");
//        Long id = 1L;
//        PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//        final Date first = DwoDateUtilities.getStartOfDay();
//        final Date last = DwoDateUtilities.getEndOfDay();
//        cc.setType(1);
//        CourseInClassManager.edit(cc);
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getType().longValue(), 1);
//        assertEquals(result.getViewState(), cc.getViewState());
//        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
//        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
//    }
//
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEditTo() {
//        System.out.println("edit");
//        Long id = 1L;
//        final PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//        final Date last = DwoDateUtilities.getEndOfDay();
//        cc.setNotAfter(last);
//        CourseInClassManager.edit(cc);
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getType(), cc.getType());
//        assertEquals(result.getViewState(), cc.getViewState());
//        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
//        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
//    }
//
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEditFrom() {
//        System.out.println("edit");
//        Long id = 1L;
//        final PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//        final Date first = DwoDateUtilities.getStartOfDay();
//        cc.setNotBefore(first);
//        CourseInClassManager.edit(cc);
//        PersistentCourseInClass result = CourseInClassManager.findEntity(id);
//        assertEquals(result.getType(), cc.getType());
//        assertEquals(result.getViewState(), cc.getViewState());
//        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
//        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
//    }
//
//    /**
//     * Test of destroy method, of class CourseInClassManager.
//     */
//    @Test
//    public void testDestroy() {
//        System.out.println("destroy");
//        Long id = 1L;
//        try {
//            CourseInClassManager.destroy(id);
//            final PersistentCourseInClass cc = CourseInClassManager.findEntity(id);
//            assertEquals("Object exists where as it should be destroyed.", cc, null);
//        } catch (Exception e) {
//            if(!(e instanceof EntityNotFoundException)) fail("Classcourse destroy has curious exception.");
//        }
//    }

}
