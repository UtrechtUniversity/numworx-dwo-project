package fi.dwo.commons.system;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TextMapperTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testGetDwo2Messages() {
    String admin = TextMapper.dwo2Message().NUM_LBL_ADVISEME_EXAMPLE();
    System.out.println(admin);
  }

  @Test
  public void testDwo2Message() {
    
  }

}
