package nl.numworx.uploadwidgetgwt.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import nl.numworx.uploadwidget.shared.AtomEntry;

public class Store {
	private List<AtomEntry> entries = new Vector<>();
	
	private Store() { };
	
	public Iterable<AtomEntry> getEntries() {
		return entries;
	}
	
	public void addEntry(AtomEntry entry, Map<String,String> tags) {
		entries.add(entry);
	}
	// TODO remove...
	
	public void removeEntry(AtomEntry entry) {
		
	}
	
	private static final Store _instance = new Store();
	
	public static Store instance() {
		return _instance;
	}

	public void deleteByURL(String url) {
		Iterator<AtomEntry> iter = getEntries().iterator();
		while (iter.hasNext()) {
			AtomEntry entry =  iter.next();
			if (url.equals(entry.url)) 
			{
				iter.remove();
				break;
			}
		}
	}

	public Optional<AtomEntry> findByURL(String url) {
		Iterator<AtomEntry> iter = getEntries().iterator();
		while (iter.hasNext()) {
			AtomEntry entry =  iter.next();
			if (url.equals(entry.url)) 
			{
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}
	
	
}
