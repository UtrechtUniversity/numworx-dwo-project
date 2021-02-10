package nl.uu.fi.dwo.register.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DemoError extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/hetbosin.html");
    
    dispatch.forward(req, resp);
  }

  @Override
  public void init() throws ServletException {
    // TODO Auto-generated method stub
    super.init();
  }

}
