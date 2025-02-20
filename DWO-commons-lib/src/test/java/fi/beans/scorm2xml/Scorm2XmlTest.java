package fi.beans.scorm2xml;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

public class Scorm2XmlTest {

	  @Test
	  public void testSetValue3() {
	    Scorm2Xml xml = new Scorm2Xml();
	    xml.LMSSetValue("cmi.values.1.item.name", "HERE");
	    assertEquals("count", "2", xml.LMSGetValue("cmi.values._count"));
	    xml.LMSSetValue("cmi.values.0.item.name", "THERE");
	    assertEquals("there", "THERE", xml.LMSGetValue("cmi.values.0.item.name"));
	    assertEquals("hier", "HERE", xml.LMSGetValue("cmi.values.1.item.name"));
	    assertEquals("count", "2", xml.LMSGetValue("cmi.values._count"));

	    xml.LMSSetValue("cmi.values.0.item.name", "");
	    assertEquals("empty", "", xml.LMSGetValue("cmi.values.0.item.name"));
	    assertEquals("hier", "HERE", xml.LMSGetValue("cmi.values.1.item.name"));
	    assertEquals("count", "2", xml.LMSGetValue("cmi.values._count"));
	    System.out.println(xml);
	  }

	
	
  @Test
  public void testSetValue2() {
    Scorm2Xml xml = new Scorm2Xml();
    xml.LMSSetValue("cmi.values.1.item", "HERE");
    assertEquals("empty", "", xml.LMSGetValue("cmi.values.0.item"));
    assertEquals("hier", "HERE", xml.LMSGetValue("cmi.values.1.item"));
    assertEquals("count", "2", xml.LMSGetValue("cmi.values._count"));
  }
  @Test
  public void testSetValue1() {
    Scorm2Xml xml = new Scorm2Xml();
    xml.LMSSetValue("cmi.values.0.item", "HERE");
    assertEquals("hier", "HERE", xml.LMSGetValue("cmi.values.0.item"));
    assertEquals("empty", "", xml.LMSGetValue("cmi.values.1.item"));
    
  }

  @Test
  public void testProperties() {
    Scorm2Xml xml = new Scorm2Xml();
    xml.LMSSetValue("cmi.values.item", "HERE");
    Properties p = xml.toProperties();
    assertEquals("size", 1, p.size());
    assertEquals("hier", "HERE", p.get("cmi.values.item"));
    
  }
  
  @Test @Ignore
  public void testPropertiesTODO() {
    Scorm2Xml xml = new Scorm2Xml();
    xml.LMSSetValue("cmi.values.0.item", "HERE");
    Properties p = xml.toProperties();
    assertEquals("size", 1, p.size());
    assertEquals("hier", "HERE", p.get("cmi.values.0.item"));
    
  }
  
  
}
