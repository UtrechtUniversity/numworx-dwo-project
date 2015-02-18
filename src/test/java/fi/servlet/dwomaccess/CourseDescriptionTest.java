package fi.servlet.dwomaccess;

import fi.servlet.dwomaccess.DWOmAccess;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

public class CourseDescriptionTest extends TestCase {

	private static final int _25630 = 25630;
	private static final int _10451 = 10451;

	DWOmAccess access;
        @Override
	protected void setUp() throws Exception {
		access = new DWOmAccess();
	}

	public void testCourseDescription() throws Exception {
		int c = _25630;
		access.getCourseDescription(c, /*new OutputStreamWriter*/(System.out));
		
	}

	public void testCourseDescription2() throws Exception {
		int c = _10451;
		access.getCourseDescription(c, /*new OutputStreamWriter*/(System.out));
		
	}
}
