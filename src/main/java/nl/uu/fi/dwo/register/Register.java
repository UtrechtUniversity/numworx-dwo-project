package nl.uu.fi.dwo.register;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Register extends HttpServlet {

  private final Logger LOG = Logger.getLogger(getClass().getName());
  private SystemManager manager;

  @Override
  public void destroy() {
  }

  @Override
  public void init() throws ServletException {

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().println("<h1>Register servlet</h1>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    // grab variables
    String email = req.getParameter("email");
    String givenName = req.getParameter("givenName");
    String insertion = req.getParameter("insertion");
    String familyName = req.getParameter("familyName");
    String type = req.getParameter("type");

    String result = registerForm(email, givenName, insertion, familyName, type);
  }

  private String registerForm(String email, String givenName, String insertion, String familyName,
      String type) {
    LOG.log(Level.INFO, "register (0), (1), (2), (3), (4)", new Object[] {
                              email,givenName, insertion, familyName, type
    });
    
    
    return null;
  }

}
