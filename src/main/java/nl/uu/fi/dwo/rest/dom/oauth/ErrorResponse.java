package nl.uu.fi.dwo.rest.dom.oauth;

public class ErrorResponse {
  public String error, error_description;

  public ErrorResponse(String error, String error_description) {
    this.error = error;
    this.error_description = error_description;
  }

  public ErrorResponse(String error) {
    this.error = error;
  }

  public ErrorResponse() {
  }
  
}
