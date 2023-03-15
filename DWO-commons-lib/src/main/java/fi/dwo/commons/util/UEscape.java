package fi.dwo.commons.util;

public class UEscape {
  // replace chars > 100 with \ u escapes
  static public String convertUEsc(String s) {
      char[] charArray = s.toCharArray();
      int length = charArray.length;
      int start = 0;
      for (; start < length; start++) {
          if (needEscape(charArray[start])) {
              break;
          }
      }
      if (start == length) {
          return s;
      }
      StringBuilder b = new StringBuilder();
      if (start > 0) {
          b.append(charArray, 0, start);
      }
      for (; start < length; start++) {
          char c = charArray[start];
          if (needEscape(c)) {
              b.append("\\u").append(toHexString(c));
          } else {
              b.append(c);
          }
      }
      return b.toString();
  }

  private static String toHexString(char c) {
      String r = Integer.toHexString(c);
      while (r.length() < 4) {
          r = '0' + r;
      }
      return r;
  }

  private static boolean needEscape(char c) {
      return c > '\u007F' // || c < ' '
              ;
  }

}
