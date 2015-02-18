/*
 * Created on Apr 5, 2005
 *
 */
package fi.dwo.server.persistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.beans.xmlrpc.Servlet;

/**
 * @author M.J.B. Kupers
 * @web.servlet name="DwoWebPage" description="Servlet voor het genereren van de
 * dwo.html pagina"
 * @web.servlet-init-param name="html_source" value="file:webapps/dwo/dwo.html"
 * @web.servlet-init-param name="local" value="true"
 * @web.servlet-init-param name="servlet" value="/dwo/dbaccess"
 * @web.servlet-mapping url-pattern="/dwowebpage"
 */
public class DwoWebPageServlet extends Servlet {

    private String HTML_SOURCE = "file:/home/projects/fisme-sites/www/dwo/dwo.html";

    //private String SERVLET = "/servlet/fi.dwo.server.persistence.DbAccessServlet";
    //private DbAccess dbAccess;
    /**
     *
     */
    public DwoWebPageServlet() {
        super();

    }

    /**
     * @param arg0
     */
    public DwoWebPageServlet(Object arg0) {
        super(arg0);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /* Read the html file */
        BufferedReader in = null;

        try {
            if (HTML_SOURCE.startsWith("file:")) {
                FileReader fis = new FileReader(HTML_SOURCE.substring(5));
                in = new BufferedReader(fis);
            } else {
                URL htmlSource = new URL(HTML_SOURCE);
                URLConnection connection = htmlSource.openConnection();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
        } catch (FileNotFoundException exception) {
            log(exception.toString());
        }

        if (in != null) {
            String result = "";
            String tmp = "";
            while ((tmp = in.readLine()) != null) {
                result += tmp + "\n";
            }
            in.close();

            String key = randomstring();

            String jar = null;

            String archives = "";
	        // End of life!
//	        /* Create for the number of jars a reference to the servlet */
//	        try {
//		        dbAccess.close(); //for lazy connection
//	            int nrJars = dbAccess.getNrJars();
////	            log("Total nr Jars: " + nrJars);
//                for(int i = 0; i < nrJars; i++) {
//                    /* the 'nr'-param is just for uniquenes of the archive name */
//                    archives += "," + SERVLET + "?key=" + key + "&nr=" + i;
//                    //log("loop: " + i + ";" + archives);
//                }
//            } catch (Exception e) {
//                getServletContext().log(e, "nrjars");
//            }
//	        dbAccess.close(); //for lazy connection

            String lang = (String) req.getParameter("language");

            if ((lang == null) || (lang.equals(""))) {
                lang = "nl";
            }

            String profile = (String) req.getParameter("profile");

            if ((profile == null) || (profile.equals(""))) {
                profile = "1";
            }

            String guestUser = (String) req.getParameter("guestUser");

            if ((guestUser == null) || (guestUser.equals(""))) {
                guestUser = "false";
            }

            String scoViewNr = (String) req.getParameter("scoViewNr");

            if ((scoViewNr == null) || (scoViewNr.equals(""))) {
                scoViewNr = "0";
            }

            String courseViewNr = (String) req.getParameter("courseViewNr");

            if ((courseViewNr == null) || (courseViewNr.equals(""))) {
                courseViewNr = "0";
            }

            String[] arguments = {archives, lang, key, profile, guestUser, scoViewNr, courseViewNr};
//	        log("arguments[0] " + arguments[0]);
//	        log("arguments[1] " + arguments[1]);
//	        log("arguments[2] " + arguments[2]);
//	        log("arguments[3] " + arguments[3]);
//            log("arguments[4] " + arguments[4]);
//            log("arguments[5] " + arguments[5]);
//            log("arguments[6] " + arguments[6]);
//	        log("result before: " + result);
            result = MessageFormat.format(result, arguments);
//	        log("result after: " + result);

            resp.setContentType("text/html");

            PrintWriter pw = resp.getWriter();

            pw.print(result);

        }
    }

    private static Random rn = new Random();

    private static int rand(int lo, int hi) {
        int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0) {
            i = -i;
        }
        return lo + i;
    }

    private static String randomstring(int lo, int hi) {
        int n = rand(lo, hi);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = (byte) rand('a', 'z');
        }
        return new String(b, 0);
    }

    private static String randomstring() {
        return randomstring(10, 20);
    }

    /**
     * Haal parameter html_source op. Andere parameters zijn servlet en local.
     *
     * @see #HTML_SOURCE
     * @see fi.beans.xmlrpc.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String param = getInitParameter("html_source");
        if (param != null) {
            HTML_SOURCE = param;
        }
//        param = getInitParameter("servlet");
//        if(param!=null)
//        	SERVLET = param;
    }

}
