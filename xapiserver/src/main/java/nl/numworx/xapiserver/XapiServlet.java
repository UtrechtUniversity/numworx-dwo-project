package nl.numworx.xapiserver;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class XapiServlet extends HttpServlet {

	private final static String STATEMENTS = "/statements";
	private final static String STATE = "/activities/state";
	private final static String CONTENT_TYPE = "application/json";
	
	private void createStatement(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonReader reader = Json.createReader(request.getReader());
		JsonStructure value = reader.read();
		reader.close();
		JsonArray array;
		int length = 1;
		switch(value.getValueType()) {
		case ARRAY:
			array = value.asJsonArray();
			length = array.size();
			break;
		case OBJECT:
			length = 1;
			break;
		default:
			// invalid
			length = 0;
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(int i = 0; i < length; i++) {
			builder.add(UUID.randomUUID().toString()); // dummy...
		}
		array = builder.build();
		response.setContentType(CONTENT_TYPE);
		JsonWriter writer = Json.createWriter(response.getWriter());
		writer.write(array);
		writer.close();
	}

	private void queryStatement(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("more", "");
		builder.add("statements", Json.createArrayBuilder());
		JsonWriter writer = Json.createWriter(response.getWriter());		
		response.setContentType(CONTENT_TYPE);
		writer.write(builder.build());
		writer.close();                                                     	}
	
	private void updateState(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void createState(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}	

	private void getState(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (STATEMENTS.equals(path)) {
			createStatement(req, resp);
		} else
		if (STATE.equals(path)) {
			updateState(req,resp);
		} else
			super.doPost(req, resp);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (STATEMENTS.equals(path)) {
			queryStatement(req, resp);
		} else
		if (STATE.equals(path)) {
			getState(req,resp);
		} else
		super.doGet(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (STATE.equals(path)) {
			createState(req,resp);
		} else
		super.doPut(req, resp);
	}
	
	
}
