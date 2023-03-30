package fi.dwo.server.db;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CookieWrap extends HttpServletResponseWrapper {

  List<Cookie> cookies;
  
  public CookieWrap(HttpServletResponse response) {
    super(response);
    cookies = new Vector<>(); // Synchronized
  }

  @Override
  public void addCookie(Cookie cookie) {
    super.addCookie(cookie);
    cookies.add(cookie);
  }
  
  public Cookie[] getCookies() {
    return cookies.toArray(new Cookie[cookies.size()]);
  }
}
