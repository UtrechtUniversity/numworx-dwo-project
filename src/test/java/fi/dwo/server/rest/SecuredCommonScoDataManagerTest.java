package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;

public class SecuredCommonScoDataManagerTest {

	private PersistentStudentScoData pssd;
	private PersistentStudentScoContext pssc;

	@Before
	public void setUp() throws Exception {
		pssd = new PersistentStudentScoData();
		Map<String, Object> suspenddata = new HashMap<>();
		Map<String, Object> onsState  = new HashMap<>();
		onsState.put("orGoedFout", Arrays.asList(Arrays.asList(true)));
		onsState.put("orScores", Arrays.asList(Arrays.asList(5)));
		suspenddata.put("onsState", onsState);
		JsonBuilderFactory factory = Json.createBuilderFactory(Collections.emptyMap());
		JsonObject object = factory.createObjectBuilder(suspenddata).build();
        StringWriter newValue = new StringWriter();
        Json.createWriter(newValue).write(object);
		String serialize = newValue.toString();
		pssd.setSuspendData(serialize);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNoData() {		
		PersistentStudentScoData pssd = new PersistentStudentScoData();
		pssc = null;
		String key = "dme.scorewidget.cs.1.success_status";
		String result = SecuredCommonScoDataManager.scoreWidget(key, pssd, pssc);
		assertEquals("no suspenddata", "", result);
	}

	@Test
	public void testSuccessStatus() {		
		String key = "dme.scorewidget.cs.1.success_status";
		pssc = new PersistentStudentScoContext();
		String result = SecuredCommonScoDataManager.scoreWidget(key, pssd, pssc);
		assertEquals("success", "passed", result);
	}

	@Test
	public void testScore() throws IOException { 
		String key = "dme.scorewidget.cs.1.score.raw";
		pssc = new PersistentStudentScoContext();
		pssc.setCompletionStatus(SecuredCommonScoDataManager.COMPLETE);
		InputStream cocd = getClass().getResourceAsStream("cocd.xml");
		byte buf[]= new byte[cocd.available()];
		cocd.read(buf);
		pssd.setCocd(new String(buf, StandardCharsets.UTF_8));
		String result = SecuredCommonScoDataManager.scoreWidget(key, pssd, pssc);
		assertEquals("score", "95", result);		
	}
	@Test
	public void testCorrect() throws Exception {
		String key = "dme.scorewidget.cs.1.success_status";
		pssc = new PersistentStudentScoContext();
		pssc.setCompletionStatus(SecuredCommonScoDataManager.COMPLETE);
		InputStream cocd = getClass().getResourceAsStream("cocd.xml");
		byte buf[]= new byte[cocd.available()];
		cocd.read(buf);
		pssd.setCocd(new String(buf, StandardCharsets.UTF_8));
		String result = SecuredCommonScoDataManager.scoreWidget(key, pssd, pssc);
		assertEquals("status", "passed", result);		
		
	}
	
	
	@Test
	public void testScore2() { 
		String key = "dme.scorewidget.s.1.1.score.raw";
		pssc = new PersistentStudentScoContext(1L);
		pssc.setScoID(1L);
		String result = SecuredCommonScoDataManager.scoreWidget(key, pssd, pssc);
		assertEquals("score", "5", result);		
	}
	
	
	
}
