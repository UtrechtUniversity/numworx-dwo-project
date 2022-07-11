package nl.numworx.notebook.server;

import junit.framework.TestCase;
import nl.numworx.notebook.server.rest.Contents;
import nl.numworx.notebook.server.rest.File;
import nl.numworx.notebook.server.rest.Folder;
import nl.numworx.notebook.server.rest.Resource;
import nl.numworx.notebook.server.rest.Server;
import nl.numworx.notebook.server.rest.User;
import nl.numworx.notebook.server.rest.Version;

public class HubAPITest extends TestCase {

	HubAPI api;
	
	protected void setUp() throws Exception {
		api = new HubAPI();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestVersion() throws Exception {
		Version version = api.get(".", Version.class);
		assertNotNull(version);
		System.out.println(version.version);
	}
	
	public void xtestUserInfo() throws Exception {
		User user = api.getUserInfo("meesterwim");
		assertNotNull(user);
		assertEquals("meesterwim", user.name);
	}

	public void testStartServer() throws Exception {
		Server server = api.startServer("project_wim");
		assertNotNull(server);
		System.out.println(server.url);
	}
	
	public void testListFolder() throws Exception { 
		Folder folder = api.listFolder("project_wim", "");
		assertNotNull(folder);
		System.out.println(folder);
	}
	
	public void testMkdir() throws Exception {
		Folder folder = api.mkdir("project_wim", "ditiseenfolder");
		assertNotNull(folder);
		System.out.println(folder);
	}
	
	public void testCreateContents() throws Exception {
		Contents contents = new Contents();
		contents.content = "Dit is een bestand";
		contents.format = "text";
		contents.type = "file";
		contents.path = "klad.txt";
		File result = api.create("project_wim", contents.path, contents);
		System.out.println(result);
		assertNotNull(result);
	}
	
	public void testDownload() throws Exception {
		File result = api.download("project_wim", "klad.txt");
		System.out.println(result.content);
	}
	
	public void testGetToken() throws Exception {
		String token;
		token = api.getTokenFor("project_wim");
		//token = api.getTokenFor("meesterwim"); // As admin?
	}
	
	public void testCreateToken() throws Exception {
		String token;
		Server server = api.startServer("meesterwim");
		token = api.createTokenFor("meesterwim");
		System.out.println(token);
		
		HubAPI meesterwim = new HubAPI(token);
		Folder root = meesterwim.listFolder("meesterwim", "");
		System.out.println(root);		
	}
	public void testCreateTokenMe () throws Exception {
		String token;
		token = api.createTokenFor("project_wim");
		System.out.println(token);
		
		HubAPI meesterwim = new HubAPI(token);
		Folder root = meesterwim.listFolder("project_wim", "");
		System.out.println(root);		
	}
	
	
}
