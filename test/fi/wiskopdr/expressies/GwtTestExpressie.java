package fi.wiskopdr.expressies;

import java.util.logging.Logger;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

import fi.wiskopdr.FormuleParser;

public class GwtTestExpressie extends GWTTestCase {

	  private Logger LOG;
	  public void gwtSetUp() throws Exception {
	    LOG = Logger.getLogger("Expressie");
	  }
	
@Test public void testParser() {
	String formuleString = FormuleParser.formuleString("$f" + "0" + "@"); assertNotNull(formuleString);
	String schoon = FormuleParser.schoon(formuleString);assertNotNull(schoon);
	Expressie parse = FormuleParser.parse(schoon);
	assertNotNull(parse);
}
	  
	  
	  
@Test public void testDecideWithoutCAS() {
// 0 variables
	assertTrue( decide("0=0"));
    assertTrue( decide("2=1+1"));
    assertTrue( decide("-1=i*i"));
    assertTrue( decide("sin(\u03C0)=0"));
    
    assertFalse( decide("x+y=y+x"));
    
    
  }

  private boolean decide(String s) {
	  VergelijkingMeerv vgl = FormuleParser.parseVergelijking("$f" + s + "@");
	  assertNotNull(vgl);
	  
	  String[] namen = vgl.geefVarNamen();
	  
	  return namen.length <= 1; 
  }

	public String getModuleName() {	
		return "nl.uu.fi.dwo.mobile.DWOplayer";
	}
  
  
  
  
  // x*2 = x + x
  // d/dx x^2 = 2x
  // 2 = 1 + 1
  // sin(pi)=0
  // i^2 = -1
  // 2 > 1
  // d/dx log(x) = 1 / x
  

}
