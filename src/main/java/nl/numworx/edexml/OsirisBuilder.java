package nl.numworx.edexml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.InputSource;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public class OsirisBuilder implements Builder {

	private static final CSVFormat EXCEL = CSVFormat.EXCEL.withHeader().withDelimiter(';');
	private Map<String, DomUserFull> leerlingen;
	private Map<String, DomSchoolClassFull> groepen;
	private Map<String, Collection<String>> memberships;
	private Map<String, DomUserFull> leerkrachten;

	public OsirisBuilder() {
		groepen = new TreeMap<>();
		memberships = new HashMap<>();
		leerlingen = new TreeMap<>();
		leerkrachten = new TreeMap<>();
	}
	
	@Override
	public Map<String, DomUserFull> parseLeerlingen() {
		return leerlingen;
	}

	@Override
	public Map<String, DomSchoolClassFull> parseGroepen() {
		return groepen;
	}

	@Override
	public Map<String, Collection<String>> memberships() {
		return memberships;
	}

	@Override
	public Map<String, DomUserFull> parseLeerkrachten() {
		return leerkrachten;
	}

	public void setLeerlingenSource(InputSource is) throws IOException {
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		CSVParser parser = CSVParser.parse(reader, EXCEL);

		for( CSVRecord record: parser) {
			String leerlingnummer = record.get(10);
			String roepnaam = "student";
			String achternaam = leerlingnummer;
			String tussenvoegsel = "";
			
			
			String email = leerlingnummer + "@students.uu.nl";
			
			DomUserFull user = new DomUserFull();
			user.setEmail(email);
			user.setFamilyName(achternaam);
			user.setGivenName(roepnaam);
			user.setInsertion(tussenvoegsel);
			user.setUserName(leerlingnummer); //????
			
			leerlingen.put(leerlingnummer, user);
			
			String groepNaam = groepNaam(record.get(1), record.get(2), record.get(3), record.get(4));
			groepen.putIfAbsent(groepNaam, null);
			addMember(leerlingnummer, groepNaam);
		}
	}

	public void setGroepenSource(InputSource is) throws IOException {
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		CSVParser parser = CSVParser.parse(reader, EXCEL);

		for( CSVRecord record: parser) {
			String collegejaar = record.get(0);
			String cursus = record.get(1);
			String blok = record.get(2);
			String korteNaam = record.get(3);
			
			String groepNaam = groepNaam(collegejaar, cursus, blok, korteNaam);
			
			DomSchoolClassFull schoolklas = new DomSchoolClassFull();
			schoolklas.setSchoolClassName(groepNaam);
			schoolklas.setRegistrationKey(cursus);
			schoolklas.setIconizer(Boolean.TRUE); // TODO wat is de default?
			
			groepen.put(groepNaam, schoolklas);
		}
		
	}

	private String groepNaam(String collegejaar, String cursus, String blok, String korteNaam) {
		return trunk100(collegejaar + " - " + cursus + " - " + blok + " - " + korteNaam);
	}

	private String trunk100(String string) {
	  string = string.trim();
      return string.length()>100?string.substring(0,100):string;
  }

  private void addMember(String student, String klas) {
		Collection<String> member = memberships.get(student);
		if (member == null) {
			member = new HashSet<>();
			memberships.put(student, member);
		}
		member.add(klas);
	}

	private  static String createRegexFromGlob(String glob)
	{
	    String out = "^";
	    for(int i = 0; i < glob.length(); ++i)
	    {
	        final char c = glob.charAt(i);
	        switch(c)
	        {
	        case '*': out += ".*"; break;
	        case '?': out += '.'; break;
	        case '.': out += "\\."; break;
	        case '\\': out += "\\\\"; break;
	        default: out += c;
	        }
	    }
	    out += '$';
	    return out;
	}
	public void setLeerkrachtenSource(InputSource is) throws IOException {
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, "UTF-8");
		CSVParser parser = CSVParser.parse(reader, EXCEL);

		for( CSVRecord record: parser) {
			String solisid = record.get(2);
			String roepnaam = "docent";
			String achternaam = solisid;
			String tussenvoegsel = "";
			String email = solisid + "@soliscom.uu.nl";
			DomUserFull user = new DomUserFull();
			user.setEmail(email);
			user.setFamilyName(achternaam);
			user.setGivenName(roepnaam);
			user.setInsertion(tussenvoegsel);
			user.setUserName(solisid); //????
			
			leerkrachten.put(solisid, user);
			
			String groepnaam = groepNaam(record.get(1), record.get(0), "*" , "*"); // FIXME stars
			if (! groepen.isEmpty()) {
				Pattern pattern = Pattern.compile(createRegexFromGlob(groepnaam));
				final String id = solisid;
				groepen.keySet().stream().filter(pattern.asPredicate()).forEach(n -> addMember(id, n));
			} else 
				addMember(solisid, groepnaam);
		}
	}
}
