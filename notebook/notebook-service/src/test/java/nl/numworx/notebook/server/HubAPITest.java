package nl.numworx.notebook.server;

import java.io.IOException;

import junit.framework.TestCase;
import nl.numworx.notebook.server.rest.Contents;
import nl.numworx.notebook.server.rest.File;
import nl.numworx.notebook.server.rest.Folder;
import nl.numworx.notebook.server.rest.Server;
import nl.numworx.notebook.server.rest.Tokens;
import nl.numworx.notebook.server.rest.User;
import nl.numworx.notebook.server.rest.Version;

public class HubAPITest extends TestCase {

	HubAPI api;
	
	protected void setUp() throws Exception {
		assertNotNull(System.getProperty(HubAPI.DWO_HUB_TOKEN));
		api = new HubAPI();
		
		startServer("project_wim");
		startServer("meesterwim");
		
	}

	private void startServer(String user) throws IOException {
		Server s = api.startServer(user);
		if (s != null && s.ready != Boolean.TRUE) {
			api.progress(user, t -> System.out.println(t.ready));
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestVersion() throws Exception {
		Version version = api.get(".", Version.class);
		assertNotNull(version);
		System.out.println(version.version);
	}
	
	public void testUserInfo() throws Exception {
		User user = api.getUserInfo("meesterwim");
		assertNotNull(user);
		assertEquals("meesterwim", user.name);
	}

	public void testStartServer() throws Exception {
		Server server = api.startServer("project_wim");
		assertNotNull(server);
		System.out.println(server.url);
	}
	
	public void testListFolderMe() throws Exception { 
		Folder folder = api.listFolder("project_wim", "");
		assertNotNull(folder);
		System.out.println(folder);
	}
	
	public void testListFolder() throws Exception {
		Folder folder = api.listFolder("meesterwim", "");
		assertNotNull(folder);
		System.out.println(folder);
		
	}
	
	public void testMkdirMe() throws Exception {
		Folder folder = api.mkdir("project_wim", "ditiseenfolder");
		assertNotNull(folder);
		System.out.println(folder);
	}

	public void testMkdir() throws Exception {
		Folder folder = api.mkdir("meesterwim", "ditiseenfolder");
		assertNotNull(folder);
		System.out.println(folder);
	}
	
	public void testCreateContentsMe() throws Exception {
		Contents contents = new Contents();
		contents.content = "Dit is een bestand";
		contents.format = "text";
		contents.type = "file";
		contents.path = "klad.txt";
		File result = api.create("project_wim", contents.path, contents);
		System.out.println(result);
		assertNotNull(result);
	}
	public void testCreateContents() throws Exception {
		Contents contents = new Contents();
		contents.content = "Dit is een bestand";
		contents.format = "text";
		contents.type = "file";
		contents.path = "klad.txt";
		File result = api.create("meesterwim", contents.path, contents);
		System.out.println(result);
		assertNotNull(result);
	}
	
	public void testDownload() throws Exception {
		File result = api.download("project_wim", "klad.txt");
		System.out.println(result.content);
	}
	
	public void testGetToken() throws Exception {
		String token;
		Tokens tokens = api.getTokenFor("project_wim");
		tokens = api.getTokenFor("meesterwim"); // As admin?
	}
	
	public void xtestCreateToken() throws Exception {
		String token;
		Server server = api.startServer("meesterwim");
		token = api.createTokenFor("meesterwim");
		System.out.println(token);
		
		HubAPI meesterwim = new HubAPI(token);
		Folder root = meesterwim.listFolder("meesterwim", "");
		System.out.println(root);		
	}

	public void xtestCreateTokenMe () throws Exception {
		String token;
		token = api.createTokenFor("project_wim");
		System.out.println(token);
		
		HubAPI meesterwim = new HubAPI(token);
		Folder root = meesterwim.listFolder("project_wim", "");
		System.out.println(root);		
	}
	
	public void testProgressMe() throws Exception {
		api.progress("project_wim", t -> System.out.println(t.message));
	}
	
	public void testCreateUser() throws Exception {
		User u = api.createUser("meesterwim5");
		User u2 = api.getUserInfo(u.name);
		api.deleteUser("meesterwim5");
		assertEquals("meesterwim5", u.name);
		assertNotNull("servers", u.servers);
		assertEquals("meesterwim5", u2.name);
		assertNotNull("servers", u2.servers);
	}
	
	public void testUnknownUser() throws Exception {
		try {
			api.getUserInfo("deze bestaat niet");
			fail("should error");
		} catch(HubException he) {
			int code = he.status;
			assertEquals("not found", 404, code);
		} 
	}
}
