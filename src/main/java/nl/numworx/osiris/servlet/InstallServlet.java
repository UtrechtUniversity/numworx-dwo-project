package nl.numworx.osiris.servlet;

import java.io.FileInputStream;
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

import org.apache.commons.csv.CSVParser;
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

	Col TOETSEN[] = {
			Col.FACULTEIT, Col.COLLEGEJAAR, Col.CURSUS, Col.AANVANGSBLOK, Col.KORTE_NAAM_NL, Col.TOETS, Col.VOLTIJD_DEELTIJD, Col.BLOK,Col.GELEGENHEID, Col.OMSCHRIJVING
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
		try {
			OsirisBuilder osiris = new OsirisBuilder();
			InputSource is;
			Iterable<CSVRecord> toetsen = null;
			Part cursus = req.getPart("cursus");
			if (cursus != null) {
				is = new InputSource(cursus.getInputStream());
				is.setEncoding(UTF_8);
				osiris.setGroepenSource(is);
				close(is);
			}
			Part toets = req.getPart("toets");
			if (toets != null) {
				is = new InputSource(toets.getInputStream());
				is.setEncoding(UTF_8);
				osiris.setGroepenSource(is);
				close(is);
				
				Excel excel = new Excel();
				excel.parse(toets.getInputStream());
				excel.verify(TOETSEN);
				toetsen = excel;
				
			}
			Part student = req.getPart("student");
			if (student != null) {
				is = new InputSource(student.getInputStream());
				is.setEncoding(UTF_8);
				osiris.setGroepenSource(is);
				close(is);
				is = new InputSource(student.getInputStream());
				is.setEncoding(UTF_8);
				osiris.setLeerlingenSource(is);				
				close(is);
			}

			Part docent = req.getPart("docent");
			if (docent != null) {
				is = new InputSource(docent.getInputStream());
				is.setEncoding(UTF_8);
				osiris.setLeerkrachtenSource(is);
				close(is);
			}
			
			out.print("<p>Courses<p>"); 
			out.print(osiris.parseGroepen().keySet());
			
			out.print("<p>Students<p>");
			out.print(osiris.parseLeerlingen().keySet());
		} catch (Exception e) {
			out.print("<pre>");
			e.printStackTrace(out);
		}
	}
	
}
