/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.testutil;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTOTP {
private static final int TIMESTEP = 30;
	private static final int DIGITS = 8;

	// Seed for HMAC-SHA1 - 20 bytes
	private static final byte[] seed20 = "12345678901234567890".getBytes();
	// Seed for HMAC-SHA256 - 32 bytes
	private static final byte[] seed32 = "12345678901234567890123456789012".getBytes();
	// Seed for HMAC-SHA512 - 64 bytes
	private static final byte[] seed64 = "1234567890123456789012345678901234567890123456789012345678901234".getBytes();

	private static final long[] TEST_TIME = { 59L, 1111111109L, 1111111111L, 1234567890L, 2000000000L, 20000000000L };
	private static final String[] SHA1_VALUES = { "94287082", "07081804", "14050471", "89005924", "69279037",
			"65353130" };
	private static final String[] SHA256_VALUES = { "46119246", "68084774", "67062674", "91819424", "90698825",
			"77737706" };
	private static final String[] SHA512_VALUES = { "90693936", "25091201", "99943326", "93441116", "38618901",
			"47863826" };

    public TestTOTP() {
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
    public void TestTOTP() {
//https://www.programcreek.com/java-api-examples/index.php?source_dir=TOTP-authentication-demo-master/src/main/java/me/brandonc/security/totp/util/TOTP.java
		
                
//		HMac sha256Hmac = new HMac(new SHA256Digest());
//		sha256Hmac.init(new KeyParameter(seed32));
//		HMac sha512Hmac = new HMac(new SHA512Digest());
//		sha512Hmac.init(new KeyParameter(seed64));
                
//According to RFC 6238, the reference implementation is as follows:
//
//Generate a key, K, which is an arbitrary bytestring, and share it securely with the client.
//Agree upon an epoch, T0, and an interval, TI, which will be used to calculate the value of the counter C (defaults are the Unix epoch as T0 and 30 seconds as TI)
//Agree upon a cryptographic hash method (default is SHA-1)
//Agree upon a token length, N (default is 6)
 // See appendix A and B   https://tools.ietf.org/id/draft-mraihi-totp-timebased-06.html
    }
}


