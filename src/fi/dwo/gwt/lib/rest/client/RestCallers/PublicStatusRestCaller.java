package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;


public interface PublicStatusRestCaller extends RestService {

    @GET
    @Path("/public/status/getHeartBeat")
    public void getHeartBeat(MethodCallback<DomHeartBeat> callback);

}
