import static org.junit.Assert.*;

import org.junit.Test;
import java.util.regex.Pattern;

public class PatternTest {

  boolean illegal(String q) {
    if (! Pattern.matches("[%#:a-zA-Z0-9=&:.]*", q))
        return true;
    return false;
  }
  
  @Test
  public void test() {
    String q= "locale=nl&profile=77&hash=%23c%3A212568&responsive=true&header=none";
    assertFalse(illegal(q));
  }

}
