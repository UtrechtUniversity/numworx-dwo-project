package nl.numworx.osiris;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Excel implements Iterable<CSVRecord> {

	public static final CSVFormat EXCEL = CSVFormat.EXCEL.withHeader().withDelimiter(';');
	public static final int BOM = '\uFEFF';

	public String charset;
	public CSVParser parser;
	public List<CSVRecord> records = Collections.emptyList();
	
	public Excel() {
	}

	public CSVParser parse(InputStream in) throws UnsupportedEncodingException, IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		bin.mark(100);
		Reader reader = new InputStreamReader(in, charset = "UTF-8");
		BufferedReader buffered = new BufferedReader(reader);
		buffered.mark(1);
		reader = buffered;
		if (buffered.read() != Excel.BOM) {
			buffered.reset();
			buffered.close();
			bin.reset();
			reader = new InputStreamReader(bin, charset = "Cp1252"); // Windows OS Default
			reader  = new BufferedReader(reader);
		}
		parser = CSVParser.parse(reader, Excel.EXCEL);
		records = parser.getRecords();
		return parser;
	}

	@Override
	public Iterator<CSVRecord> iterator() {
		return records.iterator();
	}

	public boolean verify(Col[] headers) {
		// if empty it's okay
	  if (records.isEmpty()) return true;
		  boolean verify = parser.getHeaderNames().size() == headers.length;
	  if (verify) {
	    Set<String> names = new TreeSet<>(parser.getHeaderNames());
	    for (Col key:headers) {
	      if (!names.contains(key.toString())) return false;	      
	    }
	    int cols = headers.length;
	    int rows = records.size();
	    for(int i = 0; i < cols; i++)
	      for(int j = 0; j < rows; j++) {
	        Object v = records.get(j).get(i);
	        if (v == null || v.toString().trim().isEmpty()) {
	          return false;
	        }
	      }
	    return true;
	  }
	  return false;		
	}

}
