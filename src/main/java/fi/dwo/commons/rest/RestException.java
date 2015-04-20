/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Basic RestException
 * 
 * @author G.A.J. van der Plas
 */
public class RestException extends WebApplicationException {
    public RestException(String message) {
        super(Response.status(Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
}
