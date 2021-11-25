package nl.numworx.uploadwidgetgwt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nl.numworx.uploadwidget.shared.AtomEntry;

public class Store {
	private List<AtomEntry> entries = new Vector<>();
	
	private Store() { };
	
	public Iterable<AtomEntry> getEntries() {
		return entries;
	}
	
	public void addEntry(AtomEntry entry) {
		entries.add(entry);
	}
	// TODO remove...
	
	private static final Store _instance = new Store();
	
	public static Store instance() {
		return _instance;
	}
	
	
}
