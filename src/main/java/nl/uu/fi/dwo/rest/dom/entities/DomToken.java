package nl.uu.fi.dwo.rest.dom.entities;

public class DomToken {
	public static final String APARAM="http://dwo.nl/param/a";
	private String access_token;
	private String request_token;
	private String token_type;
	private Integer expires_in;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getRequest_token() {
		return request_token;
	}
	public void setRequest_token(String request_token) {
		this.request_token = request_token;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public Integer getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Integer expires_in) {
		this.expires_in = expires_in;
	}
}
