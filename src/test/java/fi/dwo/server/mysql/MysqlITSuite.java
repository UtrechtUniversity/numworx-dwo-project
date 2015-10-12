/**
 * Copyrighted Oct 12, 2015
 */
package fi.dwo.server.mysql;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author G.A.J. van der Plas
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({fi.dwo.server.mysql.TestDatabaseManagerIT.class})
public class MysqlITSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
