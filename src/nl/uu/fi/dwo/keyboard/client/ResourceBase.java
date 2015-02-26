package nl.uu.fi.dwo.keyboard.client;

public class ResourceBase {
	static String base = /*"http://ws.fisme.science.uu.nl/dwo/apps/" + */"images/kb";
	
	public String base() { 
		return base;
	}
	
	private ResourceBase() {}
	
	public static final ResourceBase INSTANCE = new ResourceBase();
}
