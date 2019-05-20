package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.xapi.Statement;

public interface XapiRestCaller extends RestService {
  @POST
  @Path("/statements")
  public void createStatement(Statement statement, MethodCallback<List<String>> callback);
}
