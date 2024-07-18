package fi.dwo.dwojapplet.gui.action;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.yamlbeans.SafeYamlConfig;
import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlConfig.Quote;
import com.esotericsoftware.yamlbeans.YamlConfig.WriteClassName;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

public class SchoolDataActionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		YamlConfig cfg = new SafeYamlConfig();
		cfg.readConfig.setGuessNumberTypes(true);
		cfg.writeConfig.setWriteRootTags(false);
		cfg.writeConfig.setWriteRootElementTags(false);
		cfg.writeConfig.setWriteClassname(WriteClassName.NEVER);
		cfg.writeConfig.setQuoteChar(Quote.NONE);
		cfg.writeConfig.setAutoAnchor(false);
		String source = "aap: \"no\\not\"\nmies: 3.14\nwim: '1'";
		YamlReader reader = new YamlReader(source, cfg);
		Map o = (Map) reader.read();
		System.out.println(o);
		o.put("wim", 1);
		StringWriter w = new StringWriter();
		YamlWriter writer = new YamlWriter(w,cfg);
		writer.clearAnchors();
		writer.write(o);
		writer.close();
		System.out.println(w);
		
	}

}
