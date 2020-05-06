package nl.numworx.edexml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
		Reader reader = bom(new InputStreamReader(in, is.getEncoding()));
		CSVParser parser = CSVParser.parse(reader, EXCEL);
		setLeerlingenSource(parser);
	}

	public void setLeerlingenSource(Iterable<CSVRecord> parser) {
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
			
			String groepNaam = groepNaam(record.get(COLLEGEJAAR), record.get(CURSUS), record.get(AANVANGSBLOK), record.get(KORTE_NAAM_NL));
			groepen.computeIfAbsent(groepNaam,(key)-> createSchoolClass(key, record.get(CURSUS)));
			addMember(leerlingnummer, groepNaam);
		}
	}

	private static final int BOM = '\uFEFF';

	public static final String COLLEGEJAAR = "COLLEGEJAAR";
	public static final String CURSUS = "CURSUS";
	public static final String KORTE_NAAM_NL = "KORTE_NAAM_NL";
	public static final String AANVANGSBLOK = "AANVANGSBLOK";
	public static final String SOLIS_ID = "LDAP_LOGIN";
	
	
	public void setGroepenSource(InputSource is) throws IOException {
		InputStream in = is.getByteStream();
		Reader reader = new InputStreamReader(in, is.getEncoding());
		reader = bom(reader);
		CSVParser parser = CSVParser.parse(reader, EXCEL);
		setGroepenSource(parser);		
	}

	public void setGroepenSource(Iterable<CSVRecord> parser) {
		for( CSVRecord record: parser) {
			String collegejaar = record.get(COLLEGEJAAR);
			String cursus = record.get(CURSUS);
			String blok = record.get(AANVANGSBLOK);
			String korteNaam = record.get(KORTE_NAAM_NL);
			
			String groepNaam = groepNaam(collegejaar, cursus, blok, korteNaam);
			if (groepen.containsKey(groepNaam)) continue;
			
			DomSchoolClassFull schoolklas = createSchoolClass(groepNaam, cursus);			
			groepen.putIfAbsent(groepNaam, schoolklas);
		}
	}

	private DomSchoolClassFull createSchoolClass(String groepNaam, String password) {
		DomSchoolClassFull schoolklas = new DomSchoolClassFull();
		schoolklas.setSchoolClassName(groepNaam);
		schoolklas.setRegistrationKey(password);
		schoolklas.setHasRegKey(Boolean.TRUE);
		schoolklas.setIconizer(Boolean.TRUE); // TODO wat is de default?
		return schoolklas;
	}

	private Reader bom(Reader reader) throws IOException {
		BufferedReader buffered = new BufferedReader(reader);
		buffered.mark(1);
		if (buffered.read() != BOM) {
			buffered.reset();
		}
		return buffered;
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
		Reader reader = bom(new InputStreamReader(in, is.getEncoding()));
		CSVParser parser = CSVParser.parse(reader, EXCEL);
		setLeerkrachtenSource(parser);
	}

	public void setLeerkrachtenSource(Iterable<CSVRecord> parser) {
		for( CSVRecord record: parser) {
			String solisid = record.get(SOLIS_ID);
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
			
			String groepnaam = groepNaam(record.get(COLLEGEJAAR), record.get(CURSUS), "*" , "*"); // FIXME stars
			if (! groepen.isEmpty()) {
				Pattern pattern = Pattern.compile(createRegexFromGlob(groepnaam));
				final String id = solisid;
				groepen.keySet().stream().filter(pattern.asPredicate()).forEach(n -> addMember(id, n));
			} else 
				addMember(solisid, groepnaam);
		}
	}

	public void setGroepenSource(Collection<DomSchoolClassFull> values) {
		for(DomSchoolClassFull item: values) {
			String key = item.getSchoolClassName();
			groepen.putIfAbsent(key, item);
		}
		
	}
}
