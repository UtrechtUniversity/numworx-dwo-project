package fi.dwo.commons.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class JavaTranslatorTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testTranslation() {
    DwoLocale en = new DwoLocale("en");
    DwoLocale nl = new DwoLocale("nl");
    
    Dwo2ExceptionJavaTranslator translator = new Dwo2ExceptionJavaTranslator();
    
    Dwo2ExceptionCode code = Dwo2ExceptionCode.User_Q_ForgotPassword;
    String vergeet = translator.getLocalizedCodeExplanation(nl, code);
    String forgot  = translator.getLocalizedCodeExplanation(en, code);
    
    assertFalse(forgot.equals(vergeet));
  }

}
