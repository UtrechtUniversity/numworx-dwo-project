package nl.numworx.osiris.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.csv.CSVRecord;
import org.xml.sax.InputSource;

import nl.numworx.edexml.OsirisBuilder;
import nl.numworx.osiris.Col;
import nl.numworx.osiris.Excel;


@SuppressWarnings("serial")
@WebServlet(urlPatterns="/upload.html")
@MultipartConfig
public class InstallServlet extends HttpServlet {

	private static final String UTF_8 = "UTF-8";

	final static Col TOETSEN[] = {
			Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING
	};

	final static Col STUDENTEN[] = {
			Col.STUDENTNUMMER, Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING
	};

	final static Col DOCENTEN[] = {
			Col.COLLEGEJAAR, Col.CURSUS, Col.LDAP_LOGIN
	};
	final static Col COURSES[] = {
			Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL
	};
	
	public InstallServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream in = getClass().getResourceAsStream("/upload.html");
		byte buffer[] = new byte[1024];
		int len;
		resp.setContentType("text/html");
		resp.setCharacterEncoding(UTF_8);
		while ( (len = in.read(buffer)) >= 0) resp.getOutputStream().write(buffer, 0, len);
	}

	private void close(InputSource is) {
		try {
			is.getByteStream().close();
		} catch (IOException e) {
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.print("<h1>Import data</h1>");
		out.println("User: " + req.getRemoteUser() + ", " + req.getAuthType());
		try {
			OsirisBuilder osiris = new OsirisBuilder();
			InputSource is;
			Iterable<CSVRecord> toetsen = null;
			Part cursus = req.getPart("cursus");
			if (cursus != null) {
				Excel excel = new Excel();
				InputStream in = cursus.getInputStream();
				excel.parse(in);
				in.close();
				excel.verify(COURSES);
				osiris.setGroepenSource(excel);
			}
			Part toets = req.getPart("toets");
			if (toets != null) {				
				Excel excel = new Excel();
				InputStream in = toets.getInputStream();
				excel.parse(in);
				in.close();
				excel.verify(TOETSEN);
				toetsen = excel;
				osiris.setGroepenSource(toetsen);
				
			}
			Part student = req.getPart("student");
			if (student != null) {
				Excel excel = new Excel();
				InputStream in = student.getInputStream();
				excel.parse(in);
				in.close();
				excel.verify(STUDENTEN);
								
				Iterable<CSVRecord> studenten = excel;
				osiris.setGroepenSource(studenten);				
				osiris.setLeerlingenSource(studenten);				
			}

			Part docent = req.getPart("docent");
			if (docent != null) {
				Excel excel = new Excel();
				InputStream in = docent.getInputStream();
				excel.parse(in);
				in.close();
				excel.verify(DOCENTEN);
				osiris.setLeerkrachtenSource(excel);
			}
			
			out.print("<p>Courses<p>"); 
			out.println(osiris.parseGroepen().keySet());
			out.print("<p>Exams<p>");
			for(CSVRecord r: toetsen) { out.print(r.get(Col.TOETS));out.print(' '); } 
			out.print("<p>Students<p>");
			out.print(osiris.parseLeerlingen().keySet());
			out.print("<p>Teachers<p>");
			out.println(osiris.parseLeerkrachten().keySet());
			
		} catch (Exception e) {
			out.print("<pre>");
			e.printStackTrace(out);
		}
	}
	
}
