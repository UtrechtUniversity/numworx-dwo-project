package fi.servlet.dwomaccess;

import junit.framework.TestCase;
import static fi.servlet.dwomaccess.Subnet.*;
public class SubnetTest extends TestCase {

  public void testNetMatchRange() {
    boolean check = netMatchRange("131.211.22.0/23, 131.211.24.45", "131.211.24.45");
    assertTrue(check);
    check = netMatchRange("131.211.22.0/23, 131.211.24.45", "131.212.24.45");
    assertFalse(check);
  }

  public void testNetMatch() {
    boolean check;
    check = netMatch("131.211.22.0/23", "131.211.23.45");
    assertTrue(check);
    check = netMatch("131.211.23.45", "131.211.23.45");
    assertTrue(check);
    check = netMatch("131.211.23.45/32", "131.211.23.45");
    assertTrue(check);

    check = netMatch("131.211.22.0/23", "131.211.24.45");
    assertFalse(check);
    check = netMatch("131.211.23.45", "131.211.24.45");
    assertFalse(check);
    check = netMatch("131.211.23.45/32", "131.211.24.45");
    assertFalse(check);
    check = netMatch("0.0.0.0/0", "131.211.24.45");
    assertTrue(check);
  }

  public void testNetMaskFail() {
    boolean check;
    check = netMatch("ERROR", "131.211.24.45");
    assertFalse(check);
    check = netMatch("0.0.0.0", "ERROR");
    assertFalse(check);
  }
  
  public void testNetMaskFail6() {
	  boolean check;
	  check = netMatch("131.211.0.0/16", "::1");
	  assertFalse(check);
	  check = netMatch("2001::/16", "131.211.42.27");
	  assertFalse(check);
	  check = netMatch("2001::/16", "2003::2345");
	  assertFalse(check);
  }
  
  public void testNetMask6() {
	  boolean check; 
	  check = netMatch("2001:1234:1234:1234::/64", "2001:1234:1234:1234:1234:1234:1234:1234");
	  assertTrue(check);
	  check = netMatch("2001:8000::/17", "2001::");
	  assertFalse(check);
	  check = netMatch("2001:8000::/17", "2001:AAAA::00001");
	  assertTrue(check);
	  check = netMatch("2001:8000::", "2001:8000::");
	  assertTrue(check);
	  check = netMatch("2001:8000::", "2001:8000::1");
	  assertFalse(check);
  }
  
  public void testNetMatchRange6() {
	    boolean check = netMatchRange("131.211.22.0/23, 131.211.24.45", "::1");
	    assertFalse(check);
	    check = netMatchRange("131.211.22.0/23, 0::0/127, 131.211.24.45", "::1");
	    assertTrue(check); 
  }
  
  public void testnull() {
	  boolean check = netMatch("::/0", "1.2.3.4");
	  assertFalse(check);
	  check = netMatch("::/0", "::1");
	  assertTrue(check);
	  check = netMatch("0.0.0.0/0", "1.2.3.4");
	  assertTrue(check);
	  check = netMatch("0.0.0.0/0", "::1");
	  assertFalse(check);
	    
  }
  
  
}
