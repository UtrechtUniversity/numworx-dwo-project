package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import nl.uu.fi.dwo.lms.gwtclient.gwt.restyutil.RestyTestCodec;

/**
 *
 * @author plas0006
 */
public class GwtTestRestyMapConverter extends GWTTestCase {

    /**
     * Light testing CRUD and more of class SchoolManager.
     */
    public void testRestyMapConverter() {
        RestyTestCodec stringCodec = GWT.create(RestyTestCodec.class);
        JSONValue json = stringCodec.encode("Hallo");
        System.out.println(json.toString());
        String result = stringCodec.decode(json);
        System.out.println(result);
//        RestyMapCodec codec = GWT.create(RestyMapCodec.class);
//        Map map = new HashMap<String, String>();
//        map.put("key", "value");
//        JSONValue json2 = codec.encode(map);
//        System.out.println(json2);
//        // decoding an object to from JSON
//        Map other = codec.decode(json2);
//        System.out.println(other);
        
    }

    public void testSimple() {   
        System.out.println("Starting test");
        assertTrue(true);
        System.out.println("Ending test");
    }

    @Override
    public String getModuleName() {
        return "nl.uu.fi.dwo.lms.gwtclient.gwtclient";
    }
}
