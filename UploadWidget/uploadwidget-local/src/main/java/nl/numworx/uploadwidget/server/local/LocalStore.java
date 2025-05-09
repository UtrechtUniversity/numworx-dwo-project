package nl.numworx.uploadwidget.server.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import nl.numworx.uploadwidget.server.Store;
import nl.numworx.uploadwidget.shared.AtomEntry;

public class LocalStore extends Store {
	
	private static final char SLASH = '|';

	static Logger LOG = Logger.getLogger(LocalStore.class.getName());
	
	File root;

	public LocalStore() {
		String TMPDIR = System.getProperty("java.io.tmpdir", "/tmp");
		root = new File(TMPDIR);
		root = new File(root, "localStore");
		root.mkdir();
		File[] files = root.listFiles();
		for(File f: files) {
			AtomEntry a = new AtomEntry();
			a.title = last(f.getName());
			a.url = decode(f.getName());
			a.id = f.getAbsolutePath();
			a.length = f.length();
			a.type = "application/octet-stream";
			super.addEntry(a, null, (InputStream) null);
		}
	}

	private String last(String name) {
		int hek = name.lastIndexOf(SLASH);
		if (hek >= 0) return name.substring(hek+1);
		return name;
	}

	
	@SuppressWarnings("deprecation")
	AtomEntry strip(AtomEntry e) {
		AtomEntry ee = new AtomEntry();
		ee.title = ee.url = e.title;
		ee.url = URLEncoder.encode(ee.url).replace("+", "%20");
		ee.id = e.id;
		ee.length = e.length;
		ee.type = e.type;
		return ee;
	}
	
	@Override
	public Iterable<AtomEntry> getEntries(String prefix) {
		final int skip = prefix.length();
		return entries.stream()
				.filter(e -> e.url.startsWith(prefix))
				.map(this::strip)
				.collect(Collectors.toList());
		//return super.getEntries(prefix);
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> tags, FileItem item) {
		try {
			addEntry(entry, tags, item.getInputStream());
			entry.type = item.getContentType();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "addEntry failed", e);
		}
	}

	@Override
	public void addEntry(AtomEntry entry, Map<String, String> map, InputStream inputStream) {
		File f = new File(root, encode(entry.url));
		entry.type = "application/octet-stream";
		try {
			deleteByURL(entry.url);
			entry.id = f.getCanonicalPath();
			FileOutputStream out = new FileOutputStream(f);
			IOUtils.copy(inputStream, out);
			out.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "addEntry " + f, e);
		}		
		entry.length = f.length();
		super.addEntry(entry, map, inputStream);
	}

	private String encode(String title) {
		return title.replace(';', '_').replace('/', SLASH);
	}
	private String decode(String title) {
		return title.replace('_', ';').replace(SLASH, '/');
	}

	@Override
	public void deleteByURL(String url) {
		Optional<AtomEntry> opt = findByURL(url);
		if (opt.isPresent()) {
			File f = new File(opt.get().id);
			f.delete();
		}
		super.deleteByURL(url);
	}

	@Override
	public Optional<AtomEntry> findByURL(String url) {
		// TODO Auto-generated method stub
		return super.findByURL(url);
	}

	@Override
	public void write(AtomEntry entry, HttpServletResponse resp) throws IOException {
		resp.setContentType(entry.type);
		try {
			resp.setContentLengthLong(entry.length.longValue()); // niet in tomcat7
		} catch (NoSuchMethodError e) {
			resp.setContentLength(entry.length.intValue());
		}
		File u = new File(entry.id);
		IOUtils.copy(new FileInputStream(u), resp.getOutputStream());
	}

}
