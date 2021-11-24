package nl.numworx.uploadwidgetgwt.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.numworx.uploadwidgetgwt.shared.AtomEntry;

@SuppressWarnings("serial")
public class JavaUpload extends HttpServlet {
	
	Store store;
	
	String feed = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
			"<feed xmlns=\"http://www.w3.org/2005/Atom\">\n" + 
			"\n" + 
			"  <title>Example Feed</title>\n" + 
			"  <link href=\"http://example.org/\"/>\n" + 
			"  <updated>2003-12-13T18:30:02Z</updated>\n" + 
			"  <author>\n" + 
			"    <name>John Doe</name>\n" + 
			"  </author>\n" + 
			"  <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>\n" + 
			"\n" ;
	String entry = 
			"  <entry>\n" + 
			"    <title>haak.png</title>\n" + 
			"    <link href=\"http://localhost:8888/haak.png\" type=\"image/png\" length=\"1024\" />\n" + 
			"    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n" + 
			"    <updated>2003-12-13T18:30:02Z</updated>\n" + 
			"    <summary>Omschrijving hier</summary>\n" + 
			"  </entry>\n" ;
	String tail = 
			"\n" + 
			"</feed>\n" + 
			"";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/atom+xml");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(feed);
		for (AtomEntry entry: store.getEntries()) {
			out.println("<entry>");
			out.print(" <title>");out.print(entry.title);out.println("</title>");
			out.print(" <link href='");out.print(entry.url);out.print("' type='");out.print(entry.type);out.print("' length='");out.print(entry.length);out.println("' />");
			out.println("</entry>");
		}
		out.print(tail);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

	@Override
	public void init() throws ServletException {
		store = Store.instance();
	}

}
