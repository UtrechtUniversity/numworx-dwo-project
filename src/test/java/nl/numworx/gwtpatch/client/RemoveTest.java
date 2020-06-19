package nl.numworx.gwtpatch.client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import org.junit.Test;



public class RemoveTest {


	String VOOR  = "/request-voor.txt";
	String PATCH = "/request-patch.txt";
	String NA = "/request-na.txt";
	String digest = "1f03f837d2d878651581505168631121";
	
	@Test public void testna() throws Exception {
		String oldValue = readfully(NA);
		String value = readfully(PATCH);
        JsonParser parser = Json.createParser(new StringReader(oldValue));
        parser.next();
        JsonObject oldObject = parser.getObject();
        oldObject = oldObject.getJsonObject("domScormValues");
        JsonArray values = oldObject.getJsonArray("values");
        oldObject = values.getJsonObject(3);
        oldValue = oldObject.getString("value");
        parser = Json.createParser(new StringReader(oldValue));
        parser.next();
        oldObject = parser.getObject();
        Digest digest2 = new Digest();
		String patched = digest2.digest(oldObject);
		FileOutputStream klad = new FileOutputStream("target/klad.txt");
		klad.write(digest2.sb.toString().getBytes(StandardCharsets.UTF_8));
		klad.close();
		assertEquals(digest, patched);
	}
	String md5 = "onsStateaantalSessies200000000000E-11activiteitNr000000000000E0bezocht12TTisCorrectZelftoets12FFnakijkenZelftoetsPending12FFopdrContStates12RandomVarNamen0RandomVarWaardeninteractiePanelStates6feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1532000000000E-9ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1532000000000E-9ingeklaptFinteractiePanelStates3feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1210000000000E-10ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1180000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedTvisibleTnagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates2feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1468000000000E-9ingeklaptFinteractiePanelStates3feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1467000000000E-9ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1160000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1450000000000E-9ingeklaptFinteractiePanelStates1STUBVIEW_correctnullSTUBVIEW_score10model32EUCLIDES MODEL V1.0\r\n" + 
			"400000000000E-11R000000000000E0R100000000000E-11P700000000000E-11100000000000E-11126000000000E-9100000000000E-11101000000000E-9P800000000000E-11100000000000E-11335000000000E-9100000000000E-11133000000000E-9400000000000E-11R200000000000E-11R300000000000E-11R400000000000E-11R500000000000E-11000000000000E0100000000000E-11R500000000000E-11000000000000E0positionsO4100000000000E-11248000000000E-9100000000000E-11221000000000E-9e4100000000000E-11298000000000E-9100000000000E-11221000000000E-9i4100000000000E-11000000000000E0100000000000E-11000000000000E0valuesnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTRandomVarNamen0RandomVarWaardeninteractiePanelStates6feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1532000000000E-9ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1532000000000E-9ingeklaptFinteractiePanelStates3feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1210000000000E-10ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1180000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedTvisibleTnagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates1feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates2feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1430000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1468000000000E-9ingeklaptFinteractiePanelStates3feedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1467000000000E-9ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1160000000000E-10ingeklaptFinteractiePanelStates0nagekekenFpopupUsedFselectedFvisibleTfeedbackStatistiekgoedHalfFoutStatistiek400000000000E-11hoogtes1450000000000E-9ingeklaptFinteractiePanelStates1STUBVIEW_correcttrueSTUBVIEW_score0model20EUCLIDES MODEL V1.0\r\n" + 
			"200000000000E-11R000000000000E0R100000000000E-11400000000000E-11R200000000000E-11R300000000000E-11R400000000000E-11R500000000000E-11000000000000E0100000000000E-11R500000000000E-11000000000000E0positionsO4100000000000E-11248000000000E-9100000000000E-11221000000000E-9e4100000000000E-11298000000000E-9100000000000E-11221000000000E-9i4100000000000E-11000000000000E0100000000000E-11000000000000E0valuesnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTnagekekenFpopupUsedFselectedFvisibleTorGoedFout12FTorScores11100000000000E-10scoresZelftoets12000000000000E0000000000000E0tempotoetsLockedFtempotoetsSecondsLeft000000000000E0zelftoetsNagekekenF";
	
	@Test public void kladtest() throws Exception {
		FileOutputStream klad = new FileOutputStream("target/klad2.txt");
		klad.write(md5.getBytes(StandardCharsets.UTF_8));
		klad.close();		
	}
	
	
	@Test
	public void test() throws Exception {
		String oldValue = readfully(VOOR);
		String value = readfully(PATCH);
        JsonParser parser = Json.createParser(new StringReader(oldValue));
        parser.next();
        JsonObject oldObject = parser.getObject();
        oldObject = oldObject.getJsonObject("domScormValues");
        JsonArray values = oldObject.getJsonArray("values");
        oldObject = values.getJsonObject(3);
        oldValue = oldObject.getString("value");
        parser = Json.createParser(new StringReader(oldValue));
        parser.next();
        oldObject = parser.getObject();
        
        
        parser = Json.createParser(new StringReader(value));
        parser.next();
        JsonObject tmp = parser.getObject();
        tmp = tmp.getJsonObject("domScormValues");
        JsonArray tmpa = tmp.getJsonArray("values");
        value = tmpa.getJsonObject(1).getString("value");
        parser = Json.createParser(new StringReader(value));
        parser.next();
        JsonArray  patch     = parser.getArray();
        JsonObject newObject = Json.createPatch(patch).apply(oldObject);
        if (digest != null) {
          String patched = new Digest().digest(newObject);
          if( !digest.equals(patched)) {
            fail("patch digest error " + patched + " " + digest);
          }
        }
        StringWriter newValue = new StringWriter();
        Json.createWriter(newValue).write(newObject);
		
	}


	private String readfully(String file) throws IOException {
		InputStream in = getClass().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line;
		while ( (line = reader.readLine()) != null) sb.append(line);
		return sb.toString();
	}

}
