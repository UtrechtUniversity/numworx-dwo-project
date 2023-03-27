package nl.numworx.notebookgwt.client;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.numworx.notebook.common.HubInitializer;

public interface NotebookService extends RestService {

	@PUT
	@Path("/create/sec:{p}/")
	void create(HubInitializer rest, @PathParam("p") String uid, MethodCallback<String> callback);
}
