package nl.numworx.notebook.common;

public class Resource {
	
	public String type, name, content;

	public Resource() { }
	
	public Resource(String name, String type, String content) {
		this.name = name;
		this.type = type;
		this.content = content;
	}

}
