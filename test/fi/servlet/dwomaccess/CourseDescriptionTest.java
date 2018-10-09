package fi.servlet.dwomaccess;

import java.io.OutputStreamWriter;
import java.util.Hashtable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.dwojapplet.persistence.DbAccessBridge;
import junit.framework.TestCase;

public class CourseDescriptionTest extends TestCase {

	private static final int _25630 = 25630;
	private static final int _10451 = 10451;

	DWOmAccess access;
	protected void setUp() throws Exception {
		DbAccessIF instance = DbAccessBridge.createClient(null)
//		{
//			@Override
//			public Hashtable getRecord(String tbl, String key, int value) {
//				Hashtable result = new Hashtable();
//				result.put(key, value);
//				result.put("name", String.valueOf(value));
//				result.put("description", "H4sIAAAAAAAAAD2S246TYBSFcXQyo2Ni4ryAxnhFMhwL5Uqh0KFQoNDSlt5Mfg4tlH+A/hzLhU/kA5j4Ot76DjJGZ2WvnbX3uv2+/8YuS4TdHkED7uoqgXcqKOMK+DC6/fnu80ft14+X2MUUewNzEE5BUOVohr2uYhSVcQ7DrvjyFXvSdXs97JvBVxV25R+CHOZoSJ+mf1Vh7wHIKgCtIkQgiKsoe6Aq7MXgV495GA2RrLC3+b/2gRrai7HKljPxv+6pZUMTK1ea4JqYuwW3jD13kwCYyecUFJQ0lnSHEpWooI8Hwj9HhjAW2NHZW4HCobkK1qqc9yNxJHNmPzmvOxchJTYWQaTRZOyVKYHzNc83qGE0hMdc0pJ4f1TW5akDNR0nXSw4AY2gEyKfEQRESiOp76BhHiU1bvw4DYLNVnfcQGEjn9Ro38A3xbxZMo97AzrFXFtPcNrRT9RERUvqONVVfBy2EYF6VuACo7CtXIb30FQ4Mfc44Ix9j5UZfzMLDOhRi8f2jKZWdVhQ6r3lKryWx7LQbnD7tFVIpVv2wGGWwVZo7aOzK9Np7/upzevIHO33RMyFjTQ3Bdiv7NqqUWHiQplGqzG3pwKuXo1L0TtkokcfWXMy4xULMnRia9y6Cwh5pDSjMJZ8OelkpknXrisJs3a1jSczkvXrcEFo+rYrXTcMTdWSuv0s6TxkEPyuMhtGoFQ7Md1TR3Y6m4e7nZSzfcP3G9UyYGY5yCKlltKV1SlfT5caZYe8IZ/0+aGdM9sJoRPbcwAosNnZaS040aaU2/Se5Bcsz1N02XPCdOamrNCIAWcs92tePIjiM21iUCVNUkXDZCfsG3Y5MAaef08E3lhZGKEwiuCH4bqGIDvU4DDweJHB7g/eI0/yHQMAAA==");
//				result.put("image", "");
//				result.put("dwoProfileID", 1);		        
//				return result;
//			}
//			
//		}
		;
		DbAccessBridge.setInstance(instance);
		access = new DWOmAccess();
	}

	public void testCourseDescription() throws Exception {
		int c = _25630;
		access.getCourseDescription(c, /*new OutputStreamWriter*/(System.out));
		
	}

	public void testCourseDescription2() throws Exception {
		int c = _10451;
		String result =
		access.getCourseDescription(c);
		assertNotNull(result);
		Object o = new JSONParser().parse(result);
		assertTrue(o instanceof JSONObject);
		
		
	}
}
