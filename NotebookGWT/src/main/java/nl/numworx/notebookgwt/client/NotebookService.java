package nl.numworx.notebookgwt.client;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.numworx.notebook.common.HubInitializer;

public interface NotebookService extends RestService {

	@PUT
	@Path("/create")
	void create(HubInitializer rest, MethodCallback<Boolean> callback);
}
