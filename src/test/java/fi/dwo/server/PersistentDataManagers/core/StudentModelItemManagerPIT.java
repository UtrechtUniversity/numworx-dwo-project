/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.core;

import nl.uu.fi.dwo.rest.dom.entities.DomAnalyticalModelScoreTree;
import nl.uu.fi.dwo.rest.dom.entities.DomJsonModelScoreNode;
import nl.uu.fi.dwo.rest.dom.entities.DomAnalyticalModelTemplateTree;
import nl.uu.fi.dwo.rest.dom.entities.DomJsonModelTemplateNode;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * SchoolManager persistence integration tests (PIT).
 * <p/>
 *
 * Light testing. Every method assumes other SchoolManager methods work proper.
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelItemManagerPIT {

    PersistentStudentModelContext modelA = new PersistentStudentModelContext();
    PersistentStudentModelContext modelB = new PersistentStudentModelContext();

    static DatabaseManager instance = null;

    public StudentModelItemManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
//        DwoEmfFactory.setDefaultEntityManagerFactory();
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
//        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    @Before
    public void setUp() {
        instance.IntializeTestDatabase();
        modelA.setSchoolID(1L);
        modelA.setModelStructure(new DomStudentModelStructure());
        modelA = StudentModelContextManager.create(modelA);
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void testDomStudentModelContextSerialization() {
        DomStudentModelObj model = new DomStudentModelObj();
        Map<String, String> titleMap = new HashMap<>();
        Map<String, String> descrMap = new HashMap<>();
        titleMap.put("nl", "A mödel");
        titleMap.put("en", "A mødel");
        descrMap.put("nl", "A déscription");
        descrMap.put("en", "A dèscription");
        model.setInfo(new DomStudentModelContextInfo(titleMap, descrMap));
        model.getInfo().setId(UUID.randomUUID().toString());
        List<DomStudentModelObj> catList = new ArrayList<>(3);
        model.setObjectives(catList);
        for (int c = 0; c < 3; c++) {
            catList.add(new DomStudentModelObj());
            List<DomStudentModelObj> objList = new ArrayList<>(2);
            catList.get(c).setObjectives(objList);
            Map<String, String> catTitle = new HashMap<>();
            Map<String, String> catDescr = new HashMap<>();
            catTitle.put("nl", "A cat " + c);
            catTitle.put("en", "A cat " + c);
            catDescr.put("nl", "A description");
            catDescr.put("en", "A description");
            catList.get(c).setInfo(new DomStudentModelContextInfo(catTitle, catDescr));
            for (int o = 0; o < 2; o++) {
                {
                    Map<String, String> objTitle = new HashMap<>();
                    Map<String, String> objDescr = new HashMap<>();
                    objTitle.put("nl", "A öbj " + o);
                    objTitle.put("en", "A öbj " + o);
                    objDescr.put("nl", "A dëscription");
                    objDescr.put("en", "A dëscription");
                    objList.add(new DomStudentModelObj());
                    objList.get(o).setInfo(new DomStudentModelContextInfo(objTitle, objDescr));
                }
            }
        }
        PersistentStudentModelItem item = new PersistentStudentModelItem();
        item.setSchoolID(modelA.getSchoolID());
        item.setModelID(modelA.getModelID());
        item.setItem(model);
        item.setId(model.getInfo().getId());
        item = StudentModelItemManager.create(item);
        
        List<PersistentStudentModelItem> result = StudentModelItemManager.findEntities(modelA);
        assertEquals("size", 1, result.size());
        
        assertEquals("id", item.getId(), result.get(0).getId());
        assertEquals("model id", item.getItem().getInfo().getId(), result.get(0).getItem().getInfo().getId());
        
        StudentModelItemManager.destroy(item.getItemID());
        
        assertTrue( StudentModelItemManager.findEntities(modelA).isEmpty());
        
        
    }

}
