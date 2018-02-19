package nl.uu.fi.dwo.rest.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;
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
public class TOTPTest {

    //Reference test settings IETF
    final private String seed = "3132333435363738393031323334353637383930";
    final private long T0 = 0;
    final private long X = 30;

    final private long testTime[] = {59, 1111111109, 1111111111,
        1234567890, 2000000000};

    //totp codes needed as a result
    final private String testVectorsSHA1[] = {"94287082", "07081804", "14050471", "89005924", "69279037"};
    final private String testVectorsSHA256[] = {"32247374", "34756375", "74584430", "42829826", "78428693"};
    final private String testVectorsSHA512[] = {"69342147", "63049338", "54380122", "76671578", "56464532"};

    public TOTPTest() {
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
     * Test of generateTOTP method, of class TOTP.
     */
    @Test
    public void testGenerateTOTP() {
        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");
            System.out.println(
                    "|  Time(sec)   |   Time (UTC format)   "
                    + "| Value of T(Hex)  |  TOTP   | Mode   |");
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");

            String results[] = new String[testTime.length];
            for (int i = 0; i < testTime.length; i++) {
                long T = (testTime[i] - T0) / X;
                steps = Long.toHexString(T).toUpperCase();
                while (steps.length() < 16) {
                    steps = "0" + steps;
                }
                String fmtTime = String.format("%1$-10s", testTime[i]);
                String utcTime = df.format(new Date(testTime[i] * 1000));
                System.out.print("|  " + fmtTime + "  |  " + utcTime
                        + "  | " + steps + " | ");
                results[i] = TOTP.generateTOTP(seed, steps, "8");
                System.out.println(results[i] + "| SHA1   |");

                System.out.println(
                        "+--------------+-----------------------+"
                        + "------------------+----------+-------+");
            }
            for (int i = 0; i < testTime.length; i++) {
                assertEquals(testVectorsSHA1[i], results[i]);
            }
        } catch (final Exception e) {
            System.out.println("Error : " + e);
        }
    }

    /**
     * Test of generateTOTP256 method, of class TOTP.
     */
    @Test
    public void testGenerateTOTP256() {
        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");
            System.out.println(
                    "|  Time(sec)   |   Time (UTC format)   "
                    + "| Value of T(Hex)  |  TOTP   | Mode   |");
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");

            String results[] = new String[testTime.length];
            for (int i = 0; i < testTime.length; i++) {
                long T = (testTime[i] - T0) / X;
                steps = Long.toHexString(T).toUpperCase();
                while (steps.length() < 16) {
                    steps = "0" + steps;
                }
                String fmtTime = String.format("%1$-10s", testTime[i]);
                String utcTime = df.format(new Date(testTime[i] * 1000));
                System.out.print("|  " + fmtTime + "  |  " + utcTime
                        + "  | " + steps + " | ");
                results[i] = TOTP.generateTOTP256(seed, steps, "8");
                System.out.println(results[i] + "| SHA256 |");

                System.out.println(
                        "+--------------+-----------------------+"
                        + "------------------+----------+-------+");
            }
            for (int i = 0; i < testTime.length; i++) {
                assertEquals(testVectorsSHA256[i], results[i]);
            }
        } catch (final Exception e) {
            System.out.println("Error : " + e);
        }
    }

    /**
     * Test of generateTOTP512 method, of class TOTP.
     */
    @Test
    public void testGenerateTOTP512() {
        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");
            System.out.println(
                    "|  Time(sec)   |   Time (UTC format)   "
                    + "| Value of T(Hex)  |  TOTP   | Mode   |");
            System.out.println(
                    "+--------------+-----------------------+"
                    + "------------------+----------+-------+");

            String results[] = new String[testTime.length];
            for (int i = 0; i < testTime.length; i++) {
                long T = (testTime[i] - T0) / X;
                steps = Long.toHexString(T).toUpperCase();
                while (steps.length() < 16) {
                    steps = "0" + steps;
                }
                String fmtTime = String.format("%1$-10s", testTime[i]);
                String utcTime = df.format(new Date(testTime[i] * 1000));
                System.out.print("|  " + fmtTime + "  |  " + utcTime
                        + "  | " + steps + " | ");
                results[i] = TOTP.generateTOTP512(seed, steps, "8");
                System.out.println(results[i] + "| SHA512 |");

                System.out.println(
                        "+--------------+-----------------------+"
                        + "------------------+----------+-------+");
            }
            for (int i = 0; i < testTime.length; i++) {
                assertEquals(testVectorsSHA512[i], results[i]);
            }
        } catch (final Exception e) {
            System.out.println("Error : " + e);
        }
    }

    /**
     * Test of verifyTOTP method, of class TOTP.
     */
    @Test
    public void testVerifyTOTP() {
        System.out.println("verifyTOTP");
        Long time = DwoDateUtilities.getCurrentDwoUnixTimeStamp() / TOTP.defaultPeriod;
        String timeString = time.toString();
        String expResult = TOTP.generateTOTP(seed, timeString, "8");
        Boolean result1 = TOTP.verifyTOTP(expResult, seed, "8");
        assertEquals(result1, true);


        time = DwoDateUtilities.getCurrentDwoUnixTimeStamp() / 100;
        timeString = time.toString();
        expResult = TOTP.generateTOTP(seed, timeString, "8");
        result1 = TOTP.verifyTOTP(expResult, seed, "8",100);
        assertEquals(result1, true);
        try {
            Thread.sleep( 200);
        } catch (InterruptedException ex) {
            Logger.getLogger(TOTPTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Boolean result2 = TOTP.verifyTOTP(expResult, seed, "8",100);
        assertNotEquals(result1, result2);
        // TODO review the generated test code and remove the default call to fail.
        System.out.println("The test case is a success.");
    }
}
