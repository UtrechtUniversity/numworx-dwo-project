package nl.numworx.oauth2client.server;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class SubTest {

	@Test
	public void testExtractsub() throws IOException {
		String jwt = "eyJraWQiOjQsImFsZyI6IkhTMjU2In0.eyJhdWQiOiJOT05FIiwiZXhwIjoxNzY1OTc5OTg4LCJzdWIiOiJwcm9qZWN0X3dpbSJ9.8_3YWQocONhuyFwjG0lgVXe6_yKozsKpNyPfN4Ox4L8";
		String sub = OAuth2Filter.extractsub(jwt);
		assertEquals("project_wim", sub);
	}

}
