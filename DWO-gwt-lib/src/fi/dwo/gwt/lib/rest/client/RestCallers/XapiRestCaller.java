package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.xapi.Statement;

public interface XapiRestCaller extends RestService {
  @POST
  @Path("/statements")
  public void createStatement(Statement statement, MethodCallback<List<String>> callback);

  @POST
  @Path("/statements")
  public void createStatements(List<Statement> list, MethodCallback<List<String>> callback);
}
