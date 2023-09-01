package nl.numworx.uploadwidget;

import java.io.File;
import java.util.Objects;

import com.google.common.base.Strings;

class InputFile implements Comparable<InputFile>{
	String name = "";
	
	public InputFile(File f) {
		name = f.getName();
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
}