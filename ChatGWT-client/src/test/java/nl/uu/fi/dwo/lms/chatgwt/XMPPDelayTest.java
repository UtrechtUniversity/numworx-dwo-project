package nl.uu.fi.dwo.lms.chatgwt;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class XMPPDelayTest {

	@Before
	public void setUp() throws Exception {
		ISO_DATETIME = new SimpleDateFormat(ChatGWT.ISO8601_PATTERN);
	}

	@After
	public void tearDown() throws Exception {
	}

	static DateFormat ISO_DATETIME;

	public static Date fromDelay(String delay) throws ParseException {
		if (delay.endsWith("Z")) delay = delay.substring(0, delay.length()-1) + "+0000"; // REMOVE Z, add GMT		
		return ISO_DATETIME.parse(delay);
	}

	
	@Test
	public void test() throws Exception {
		String delay = "2022-09-01T00:00:00Z";
		Date date = fromDelay(delay);
		assertEquals(2, date.getHours());
	}

}
