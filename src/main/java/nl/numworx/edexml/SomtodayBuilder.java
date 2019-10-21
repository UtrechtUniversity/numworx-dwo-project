package nl.numworx.edexml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class SomtodayBuilder implements Builder {

	private Document document;

	Map<String, DomUserFull> leerlingen = Collections.emptyMap();
	Map<String, DomUserFull> docenten = Collections.emptyMap();

	private final Map<String, DomSchoolClassFull> groepen;
	private final Map<String, Collection<String>> memberships;
	
	public SomtodayBuilder() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.newDocument();
		} catch (ParserConfigurationException e) {
		}
		groepen = new TreeMap<>();
		memberships = new TreeMap<>();

	}

	public Document getDocument() {
		return document;
	}

	public void setLeerlingenSource(InputSource is) throws IOException {
		leerlingen = new TreeMap<>();
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		CSVParser parser = CSVParser.parse(reader, CSVFormat.EXCEL.withHeader().withDelimiter(';'));

		for( CSVRecord record: parser) {
			String leerlingnummer = record.get(0);
			String roepnaam = record.get(1);
			String achternaam = record.get(2);
			String tussenvoegsel = "";
			int komma = achternaam.indexOf(',');
			if (komma >= 0) {
				tussenvoegsel = achternaam.substring(komma+1).trim();
				achternaam = achternaam.substring(0,komma);
			}
			
			String email = record.get(3);
			
			DomUserFull user = new DomUserFull();
			user.setEmail(email);
			user.setFamilyName(achternaam);
			user.setGivenName(roepnaam);
			user.setPassword(getHashString(roepnaam + "2019"));
			user.setInsertion(tussenvoegsel);
			user.setUserName(email); //????
			
			leerlingen.put(leerlingnummer, user);
		}
		
	}

	
	private String getHashString(String string) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(string.getBytes(StandardCharsets.UTF_8));
			return DatatypeConverter.printHexBinary(bytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public Map<String, DomUserFull> parseLeerlingen() {
		return leerlingen;
	}

	private static final int BOM = '\uFEFF';

	public void setLesGroupLeerlingSource(InputSource is) throws IOException {
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		BufferedReader buffered = new BufferedReader(reader);
		buffered.mark(1);
		reader = buffered;
		if (buffered.read() != BOM) {
			buffered.reset();
		}

		CSVParser parser = CSVParser.parse(reader, CSVFormat.EXCEL.withDelimiter(';'));
		for( CSVRecord record: parser) {
			String klas = record.get(0);
			String student = record.get(1);
			addMember(student, klas);
		    addGroep(klas);
		}
	}

	private void addMember(String student, String klas) {
		Collection<String> member = memberships.get(student);
		if (member == null) {
			member = new HashSet<>();
			memberships.put(student, member);
		}
		member.add(klas);
	}

	private void addGroep(String klas) {
		if (! groepen.containsKey(klas)) {
			DomSchoolClassFull dom = new DomSchoolClassFull();
			dom.setSchoolClassName(klas);
			groepen.put(klas, dom);
		}
	}

	
	
	
	
	
	
	@Override
	public Map<String, DomSchoolClassFull> parseGroepen() {
		return groepen;
	}

	@Override
	public Map<String, Collection<String>> memberships() {
		return memberships;
	}

	public void setLesGroupDocentSource(InputSource is) throws IOException {
		docenten = new TreeMap<>();
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		CSVParser parser = CSVParser.parse(reader, CSVFormat.EXCEL.withHeader().withDelimiter(';'));
		for( CSVRecord record: parser) {
			String klas = record.get(0);
			String aantal = record.get(1);
			String[] ids = record.get(2).split(",");
			String[] names = record.get(3).split(",");
			String[] emails = record.get(4).split(",");
			
			addGroep(klas);
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i].trim();
				addMember(id,klas);
				if (docenten.containsKey(id)) continue;
				String achternaam = names[i].trim();
				String email = emails[i].trim();
				String voornaam = "";
				String insertion = "";
				int space = achternaam.indexOf(' ');
				if (space >= 0) {
					voornaam = achternaam.substring(0, space);
					achternaam = achternaam.substring(space).trim();
				}
				space = achternaam.lastIndexOf(' ');
				if (space >= 0) {
					insertion = achternaam.substring(0,space);
					achternaam = achternaam.substring(space).trim();
				}
				DomUserFull user = new DomUserFull();
				user.setEmail(email);
				user.setFamilyName(achternaam);
				user.setGivenName(voornaam);
				user.setInsertion(insertion);
				user.setUserName(email);
				user.setPassword(this.getHashString(voornaam + "2019"));
				docenten.put(id, user);
			}
			
			
			
		}
	}

	@Override
	public Map<String, DomUserFull> parseLeerkrachten() {
		return docenten;
	}
	
}
