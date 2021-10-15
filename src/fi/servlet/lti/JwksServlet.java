package fi.servlet.lti;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class JwksServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // just fake!
    // need real public key
      InputStream in = getClass().getResourceAsStream("resources/jwks.json");
      byte[] buffer = new byte[1024];
      int len = in.read(buffer);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getOutputStream().write(buffer, 0, len);
  }

}
