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
 * Basic Illegal Request Exception.
 * 
 * Trying to perform an illegal operation. Possible trying to misuse the RestInterface.
 * 
 * @author G.A.J. van der Plas
 */
public class UnAuthorizedRestException extends WebApplicationException {
    public UnAuthorizedRestException(String message) {
        super(Response.status(Status.UNAUTHORIZED).entity(message).type("text/plain").build());
    }
}
