/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.testutil;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.util.DatatypeConverter;

import java.util.concurrent.ThreadLocalRandom;
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

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    
    public byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
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
        byte result[] = parseHexBinary(encoded);
        System.out.println(encoded);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(result[i], bytes[i]);
        }

    }

}
