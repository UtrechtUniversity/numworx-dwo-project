/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.testutil;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import java.util.concurrent.ThreadLocalRandom;
import javax.xml.bind.DatatypeConverter;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHexEncoding {

    public TestHexEncoding() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
     * Light testing CRUD and more of class SchoolManager.
     */
    @Test
    public void TestHexEncoding() {
        byte bytes[] = new byte[8];
        ThreadLocalRandom.current().nextBytes(bytes);
        String encoded = DatatypeConverter.printHexBinary(bytes);
        System.out.println(encoded);
        byte result[] = DatatypeConverter.parseHexBinary(encoded);
        System.out.println(encoded);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(result[i], bytes[i]);
        }

    }

}
