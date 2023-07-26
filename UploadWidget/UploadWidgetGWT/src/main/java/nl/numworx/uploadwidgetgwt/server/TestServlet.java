package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.numworx.uploadwidgetgwt.server.az.AZProvider;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		log("started");
		out.println("<p>");
		out.println("okay");
try {		
//		Properties prop = System.getProperties();
//		out.println(prop);
//		Map<String, String> env = System.getenv();
//		out.println(env);
		AZProvider provider = new AZProvider();
		out.println(provider);
//		Iterable<BlobItem> entries = provider.getEntries("/");
//		entries.forEach(out::println);
		out.println("oops");
		out.flush();
		Set<String> list = provider.list();
		out.println(list);
		out.flush();
} catch(Throwable e) {
		log("oops", e);
		e.printStackTrace(out);
		
}
		
		out.println("</p>");
		
	}

}
