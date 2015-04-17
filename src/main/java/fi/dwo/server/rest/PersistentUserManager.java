/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author plas0006
 */
@Path("/user")
public class PersistentUserManager {

    private static final Logger log = Logger.getLogger(PersistentUserManager.class.getName());

   private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("DWO_MySQLDB");

   //CRUD
//   
//   
//   public PersistentUser createUser(PersistentUser user){
//       
//       
//   }
//
//   public PersistentUser readUser(PersistentUser user){
//       
//       
//   }
//
//   
//   public void updateUser(PersistentUser user){
//       
//       
//   }
//
//   public void deleteUser(UserId id){
//       
//       
//   }
//   
//
//    @GET
//    @Produces({"application/json"})
//    @Path("/json")
//    public List<PersistentUser> getStatusJson() {
//        return getStatus();
//    }
//
//    @GET
//    @Produces({"application/xml"})
//    @Path("/xml")
//    public List<PersistentUser> getStatusXml() {
//        return getStatus();
//    }
    @GET
    @Produces({"application/json"})
    @Path("/json")
    public String getStatusJson() {
        return "hello";
    }

}
