package nl.numworx.uploadwidget.shared;

public class AtomEntry {
	public String title, url, type;
	public Long length;
// more to follow: zie atom spec: id, updated, summary
	

	public String toString() {
		return String.valueOf(title);
	}
}
