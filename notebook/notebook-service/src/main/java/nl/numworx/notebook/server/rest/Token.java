package nl.numworx.notebook.server.rest;

import java.time.Instant;

public class Token {
	public String token;
	public String id;
	public Instant expires_at;
	public String user, service, roles[];
	public String note;
	public String kind;
	
}
