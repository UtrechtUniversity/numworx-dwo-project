package nl.numworx.uploadwidget;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.cbook.cbookif.rm.Resource;
import org.cbook.cbookif.rm.ResourceContainer;

import nl.numworx.uploadwidget.shared.AtomEntry;

class InputFile extends AtomEntry implements Comparable<InputFile>{
	String name = "";
	URI uri;
	
	public InputFile(File f) {
		name = f.getName();
		uri  = f.toURI();
	}
	
	public InputFile(Map<String, Object> map) {
		name = (String) map.get("name");
		url  = (String) map.get("url");
	}

	public String toString() { return name; }

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InputFile other = (InputFile) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public int compareTo(InputFile o) {
		return String.CASE_INSENSITIVE_ORDER.compare(name, o.name);
	}

	public void destroy() {
		// TODO Auto-generated method stub	
	}
	
	Map<String,Object> toMap() {
		Map<String,Object> map = new TreeMap<>();
		map.put("name", name);
		if (url != null) map.put("url", url);
		return map;
	}
	
	void persist(ResourceContainer container) throws IOException {
		if (uri != null)
		{
			Resource r = container.create(name, uri.toURL());
			url = r.getURL().toExternalForm();
			// relatief t.o.v. sec:1-.../
			int i = url.indexOf("sec:");
			i = url.indexOf('/', i);
			url = url.substring(i+1);
			
		}
	}
}