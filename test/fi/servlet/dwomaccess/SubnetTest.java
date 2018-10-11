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
}
