package fi.dwo.server.rest;

import static org.junit.Assert.*;

import org.junit.Test;

public class PublicRestTestManagerTest {


	@Test
	public void testVerifyBrowserExamKey() {
		String uri = "https://app.dwo.nl/toets/toets.jsp";
		String headerW = "2314075fbbe84c3d185713ab6263c3cd2f7c3eedb02fb5184259d8b107ffcf75"; // win 2.1.7
		String headerM = "f2a7783f64c49859d62cb878e6f890ddc40491c3dbcd8f0e485e8c5202d78788"; // mac 2.1.2
		
		String seedM = "acd95f0b55edb444702d17a644604459ede2cb0678db8ab43a9d6d3e25dac062";
		String seedW = "dbace2d457dad560309ad4300cc8d2e23ba75ea1cab7c1b9928ad343fab6fb1f";
		
		assertTrue( "MacKey", PublicRestTestManager.verifySEBHeader(headerM, uri, seedM));
		assertTrue( "WinKey", PublicRestTestManager.verifySEBHeader(headerW, uri, seedW));

		assertFalse( "wrong key", PublicRestTestManager.verifySEBHeader(headerW, uri, seedM));
		
		assertTrue ("any key", PublicRestTestManager.verifySEBHeader(headerM, uri, seedW, seedM));
		assertTrue ("any key", PublicRestTestManager.verifySEBHeader(headerW, uri, seedW, seedM));
		
		
		
	}

}
