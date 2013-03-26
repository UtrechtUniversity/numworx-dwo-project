package fi.servlet.dwomaccess;

import junit.framework.TestCase;

public class CourseDescriptionTest extends TestCase {

	private static final int _25630 = 25630;
	private static final int _10451 = 10451;

	DWOmAccess access;
	protected void setUp() throws Exception {
		access = new DWOmAccess();
	}

	public void testCourseDescription() throws Exception {
		int c = _25630;
		access.getCourseDescription(c, System.out);
		
	}

	public void testCourseDescription2() throws Exception {
		int c = _10451;
		access.getCourseDescription(c, System.out);
		
	}
}
