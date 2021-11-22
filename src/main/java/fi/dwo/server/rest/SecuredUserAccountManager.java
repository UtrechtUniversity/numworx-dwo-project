package fi.dwo.server.rest;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.internal.util.Base64;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DatatypeConverter;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.LoginContextUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.security.TOTP;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/user/account")
public class SecuredUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountManager.class.getName());
//    @Context  //injected response proxy supporting multiple threads
//    private HttpServletResponse response;

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get")
    public DomUserFull getCurrentUser(@Context SecurityContext sc) {
        PersistentUser user = null;

        try {
            Principal p = sc.getUserPrincipal();
            if (p instanceof DwoUserPrincipal) 
              user = ((DwoUserPrincipal) p).getUser();
            else 
              user = UserManager.findByUserName(p.getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "User " + sc.getUserPrincipal() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user " + sc.getUserPrincipal() + " .");
        }
        return user.buildDomUserFull();
    }

    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomUserFull getCurrentUser(@Context SecurityContext sc, RestContext ctx) {
        PersistentUser user = null;

        try {
            Principal p = sc.getUserPrincipal();
            if (p instanceof DwoUserPrincipal) 
              user = ((DwoUserPrincipal) p).getUser();
            else 
              user = UserManager.findByUserName(p.getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "User " + sc.getUserPrincipal() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user " + sc.getUserPrincipal() + " .");
        }
        String realm = ctx.getRestContext().getRealm();
		return user.buildDomUserFull(realm);
    	
    }

    
    
    /**
     * Returns the current LoginContext. The information is retrieved from the
     * data store.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/getLoginContext")
    public DomLoginContext getLoginContext(@Context SecurityContext sc) {
        PersistentUser user = null;
        PersistentLoginContext loginContext = null;

        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            List<PersistentLoginContext> list = LoginContextManager.findEntities(user.getId());
            if (list.size() == 1) {
                loginContext = list.get(0);
                if(loginContext.getSchoolGroupId() == null) {
                	loginContext.setSchoolGroupId(user.getSchoolGroupId());
                	try {
						LoginContextManager.edit(loginContext); // not fatal
					} catch (Exception e) {
					}
                }
            } else {
                loginContext = new PersistentLoginContext();
                loginContext.setUserId(user.getId());
                loginContext.setSchoolGroupId(user.getSchoolGroupId());
                loginContext.setLastLogin(null);
                loginContext.setRegisterTimeStamp(user.getRegisterDate().getTime());
                LoginContextManager.create(loginContext);
            }
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
        DomLoginContext dom = loginContext.buildDomLoginContext();
        dom.setRealm(user.getRealm());
		return dom;
    }

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/login")
    public DomUserFullwLoginContext loginUser(@Context SecurityContext sc) {
        PersistentUser u;

        try {
            u = UserManager.findByUserName(sc.getUserPrincipal().getName());
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), u.getUsername()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "User " + sc.getUserPrincipal() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user " + sc.getUserPrincipal() + " .");
        }

        try {
//                return u.buildDomUserFullwLoginContext(LoginContextUtilManager.reqLoginContextSession(u));
            //loginDataUtilManager should use the returndata to log any statistical stuff needed for OLAP Warehousing.
        	boolean newsecret = true;
// DEBUG SAML
        	newsecret = !System.getProperty("DWO_ENV", "app").contains("saml");
            DomUserFullwLoginContext result = u.buildDomUserFullwLoginContext(LoginContextUtilManager.forceNewLoginContextSession(u,newsecret));
            return result;
        } catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManager.class.getName()).log(Level.SEVERE, "Invalid software state, this should not have happened.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Invalid software state. This should not have happened.");
        }

    }

//	public static DomUserFullwLoginContext createUserFullwLoginContext(PersistentUser u) {
//		//al ready retrieved and cached in getCurrentUser
//        
//
//        //setting PersistentLoginContext
//        DomLoginContext domLoginContext = null;
//        try {
//            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
//            PersistentLoginContext loginContext = new PersistentLoginContext();
//            switch (loginContextList.size()) {
//                case 0:
//                    //none yet
//                    loginContext.setUserId(u.getId());
//                    loginContext.setLastLogin(null);
//                    loginContext.setRegisterTimeStamp(u.getRegisterDate().getTime());
//                    loginContextList.add(loginContext);
//                    LoginContextManager.create(loginContext);
//                    break;
//                case 1:
//                    //update if exists
//                    loginContext = loginContextList.get(0);
//                    loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
//                    LoginContextManager.edit(loginContext);
//                    break;
//                default:
//            }
//            //add or update
//            domLoginContext = loginContext.buildDomLoginContext();
//        }
//        catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//        }
//        DomUserFullwLoginContext result = new DomUserFullwLoginContext();
//        result.setDomLoginContext(domLoginContext);
//        result.setDomUserFull(u.buildDomUserFull());
//        return result;
//	}
    @PUT
    @Produces({"application/json"})
    @Path("/basicAuthLogout")
    public Response basicAuthLogout(@Context SecurityContext sc, @Context HttpServletRequest servletRequest, RestLoginContext loginContext) {
        logoutUser(sc, loginContext);
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "Logout for basic Authentication performed: " + userName + ".");
        Response r = Response.status(401).entity(e.getMessage()).build();
        if (servletRequest.getSession() != null) {
            servletRequest.getSession().invalidate();
        }
        return r;
    }

    /**
     * Returns the DomUserFull if the form parameter equals the security context
     * user name otherwise a 401. Necessary for stubborn browsers that keep
     * authorization to long in cache. POST to relax jamon resources.
     *
     * @param sc security context
     * @param user us
     * @return
     */
    @POST
    @Path("/loginUser")
    public Response loginUserWithPOST(@Context SecurityContext sc, @FormParam("user") String user) {
        return loginUser(sc, user);
    }

    /**
     * Returns the DomUserFull if the path parameter equals the security context
     * user name otherwise a 401.
     *
     * @param sc security context
     * @param user us
     * @return
     */
    @Deprecated
    @GET
    @Path("/loginUser/{user}")
    public Response loginUser(@Context SecurityContext sc, @PathParam("user") String user) {
        Response result;

        DomUserFullwLoginContext domUser = loginUser(sc);
        String realm = domUser.getDomLoginContext().getRealm();
        if (realm == null) realm = "";
		String domUserName = domUser.getDomUserFull().getUserName() + realm;
        if (domUserName.equalsIgnoreCase(user)) {
            result = Response.ok().
                    type(MediaType.APPLICATION_JSON_TYPE).
                    entity(domUser).build();
        } else {
            result = Response.status(Response.Status.UNAUTHORIZED).
                    header("WWW-Authenticate", "Basic realm=\"DWO.nl\"").
                    build();
        }
        return result;
    }

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @param loginContext
     * @return Returns null if there was an error.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/logout")
    public Boolean logoutUser(@Context SecurityContext sc, RestLoginContext loginContext) {
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
//        try {//LoginData may fail, but login should succeed.
//            //register login action
//            PersistentLogData loginData = new PersistentLogData();
//            PersistentLogDataPK ldKey = new PersistentLogDataPK();
//            ldKey.setUserId(u.getUsername());
//            ldKey.setUtcTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
//            PersistentSchoolGroup sg = SchoolGroupManager.findEntity(u.getSchoolGroupId());
//            PersistentRole g = RoleManager.findEntity((long) sg.getGroupID());
//
//            loginData.setRole(g.getGroupname());
//            loginData.setMessage(LogType.Logout);
//            loginData.setLogLevel(Level.INFO.toString());
//            LogDataManager.create(loginData);
//
//        }
//        catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
//            //return false;
//        }
        //erasing PersistentLoginContext only if proper setRegisterTimeStamp and lastlogin timestamp
        if (loginContext != null) {
            try {
                List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
                if (loginContextList.size() == 1) {
                    if (loginContext.getDomLoginContext().getRegisterTimeStamp().equals((loginContextList.get(0).getRegisterTimeStamp()))
                        && loginContext.getDomLoginContext().getLastLoginTimeStamp().equals(loginContextList.get(0).getLastLogin())
                        ) {
//                        loginContextList.get(0).setLastLogin(null); not for housekeeping users. // changed 6 maart 2019, actie voor maart 2021
                    	String env = System.getProperty("DWO_ENV", "app");
                    	if (!env.contains("saml") || u.isSingleSchoolAccount())
                    	{	loginContextList.get(0).setSecretKey(null);
                        	loginContextList.get(0).setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                    	}
                        LoginContextManager.edit(loginContextList.get(0));
                    }
                } else {
                    //logout while no login tried before.
                    LOG.log(Level.FINE, "Logging out by user {0} while user has never logged in.", u.getId());

                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
        return true;
    }
//
//    /**
//     * Updates the User data of the current user and returns a copy of the
//     * updated data.
//     *
//     * @param sc
//     * @param user
//     * @return
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/update")
//    public DomUserFull updateCurrentUser(@Context SecurityContext sc, RestUserFull user) {
//        if (user == null) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
//        }
//        //passwords are already hashed.
//        if (!ValidUserFieldsChecker.isValidEmail(user.getDomUserFull().getEmail())) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
//        }
//        if (!ValidUserFieldsChecker.isValidUserName(user.getDomUserFull().getUserName())) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
//        }
//        if (!ValidUserFieldsChecker.isValidPassword(user.getDomUserFull().getPassword())) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Password_Invalid, "The password is not correctly formatted.");
//        }
//
//        if (user.getDomUserFull().getUserName().equals(sc.getUserPrincipal().getName())) {
//            try {
//                PersistentUser dbUser = UserManager.findByUserName(user.getDomUserFull().getUserName());
//                dbUser.setGivenName(user.getDomUserFull().getGivenName());
//                dbUser.setLastname(user.getDomUserFull().getFamilyName());
//                dbUser.setInsertion(user.getDomUserFull().getInsertion());
//                dbUser.setEmail(user.getDomUserFull().getEmail());
//                dbUser.setPassword(user.getDomUserFull().getPassword());
//                //User to update is logged in user.
//                UserManager.edit(dbUser);
//                PersistentUser pUser = UserManager.findByUserName(user.getDomUserFull().getUserName());
//                return pUser.buildDomUserFull();
//            } catch (Exception e) {
//                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update user id " + sc.getUserPrincipal().getName() + " .");
//            }
//        } else {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getDomUserFull().getUserName()});
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getDomUserFull().getUserName() + ".");
//        }
//    }
/**
     * Updates the User data of the current user and returns a copy of the
     * updated data.
     *
     * @param sc
     * @param user
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomUserFull updateCurrentUser(@Context SecurityContext sc, RestUserFull user) {
        if (user == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        //passwords are already hashed.
        if (!ValidUserFieldsChecker.isValidEmail(user.getDomUserFull().getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
//        if (!ValidUserFieldsChecker.isValidUserName(user.getDomUserFull().getUserName())) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
//        }
        if (!ValidUserFieldsChecker.isValidPassword(user.getDomUserFull().getPassword())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Password_Invalid, "The password is not correctly formatted.");
        }
//clear results
        try {            
            UserDomainAuthorizer.UserState_U build = AnonDomainAuthorizer.build().submitUser(sc);
            DomContext context = user.getRestContext(); // in de test null
            build.setRealm(context == null ? null : context.getRealm());
            return build.UpdateAccount(user.getDomUserFull());
            //TODO clear all excess classcourses.
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e.getDwo2Code(),e.getDwo2Message());
        }
    }

    /**
     * Removes all the User data of the current user and returns true.
     * \texttt{StudentScoData},\texttt{StudentScoContext}, \texttt{StudentOf},
     * \texttt{TeacherOf\texttt{HasRole}, \texttt{SamlUser}, \texttt{User}.
     *
     *
     * @param sc
     * @return
     * @throws Dwo2Exception 
     */
    @GET
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeCurrentUser(@Context SecurityContext sc) throws Dwo2Exception {
        PersistentUser u = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if (u == null) {
            return Boolean.TRUE;
        }
        UserState_U state = AnonDomainAuthorizer.build().submitUser(sc);
        PersistentUser user = state.getUser();
        if (user.isSingleSchoolAccount())
        {
          LOG.warning("cannot remove singleschoolStudent " + user.getUsername());
          return Boolean.FALSE;
        }
        UserUtilManager.deleteUser(user);
        return Boolean.TRUE;
    }

    enum TotpType {
        PLAIN,
        JWT
    }

    @GET
    @Produces({"application/json"})
    @Path("/getBearerToken")
    public String getBearerToken(@Context SecurityContext sc) {
        PersistentUser user = null;
        PersistentLoginContext loginContext = null;

        try {
            user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            List<PersistentLoginContext> list = LoginContextManager.findEntities(user.getId());
            if (list.size() == 1) {
                loginContext = list.get(0);
                Long time = DwoDateUtilities.getCurrentDwoUnixTimeStamp() / TOTP.defaultPeriod;
                String timeString = time.toString();
                String result = (loginContext.getSecretKey()==null) ? null : TOTP.generateTOTP(DatatypeConverter.printHexBinary(loginContext.getSecretKey()), timeString, "8");
                result = user.getUsername()+":"+result;
                byte bytes[] = result.getBytes();
                return "\"Bearer "+java.util.Base64.getEncoder().encodeToString(bytes) + '\"'; // application/json 
            }else{
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "No login context exists.");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
        }
        
    }    
    
//    @GET
//    @Produces("application/json")
//    @Path("/verifyTOTP")
//    public
    private String verifyTOTP(@HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) {
        try {
            LOG.info("ccid = " + ccid);
            LOG.info("totp = " + totp);
            ccid = Base64.decodeAsString(ccid);
            DomClassCourse id = new DomClassCourse();
            id.setId(new PersistenceId(ccid));
            Long nativeId = MySQLPersistenceId.getNativeId(id);
            PersistentClassCourse pcc = ClassCourseManager.findEntity(nativeId);
            String accessKey = pcc.getAccessKey();

            StringTokenizer st = new StringTokenizer(totp);
            switch (TotpType.valueOf(st.nextToken())) {
                case PLAIN:
                    totp = Base64.decodeAsString(st.nextToken());
                    return String.valueOf(accessKey == null || accessKey.isEmpty() || accessKey.equals(totp));
                default:
                    throw new IllegalArgumentException("not implemented");
            }

        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "verifyTOTP failed", e);
            return "false";
        } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "verifyTOTP failed", e);
            return "false";
        }
    }

    public static String getToken(PersistentClassCourse pcc, SecurityContext sc) {
      String userName = sc.getUserPrincipal().getName();
      PersistentUser u = UserManager.findByUserName(userName);
      List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(u.getId());
      PersistentLoginContext context = loginContextList.get(0);
      SecretKey key = OAuth2Manager.getKey(context);
      JwtBuilder builder = Jwts.builder()
            .setSubject(u.getUsername())
            .setAudience("exam")
            .setIssuedAt(new Date())
            .setId(String.valueOf(pcc.getClassCourseID()))
            .setNotBefore(pcc.getNotBefore())
            .setExpiration(pcc.getNotAfter())
            .setHeaderParam("kid", context.getId());         
      return builder.signWith(key).compact();     
    }
 
    // timestamp never null, reasonable value 
    private static String notAfter(PersistentClassCourse pcc) {
    	Date notAfter = pcc.getNotAfter();
    	if (notAfter == null) {
    		Date notBefore = pcc.getNotBefore(); 
    		if (notBefore == null) notBefore = new Date();
    		return String.valueOf(notBefore.getTime()/1000L + 24*3600);
    	}
    	return String.valueOf(notAfter.getTime()/1000L); // same as expiry value
    }
    
    @GET
    @Produces("application/json")
    @Path("/verifyTOTPv2")
    public String verifyTOTPv2(@Context SecurityContext sc,
                               @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) {
        try {
            LOG.info("ccid = " + ccid);
            LOG.info("totp = " + totp);
            ccid = Base64.decodeAsString(ccid);
            DomClassCourse id = new DomClassCourse();
            id.setId(new PersistenceId(ccid));
            Long nativeId = MySQLPersistenceId.getNativeId(id);
            PersistentClassCourse pcc = ClassCourseManager.findEntity(nativeId);
            String accessKey = pcc.getAccessKey();

            StringTokenizer st = new StringTokenizer(totp);
            switch (TotpType.valueOf(st.nextToken())) {
                case PLAIN:
                    totp = Base64.decodeAsString(st.nextToken());
                    if (accessKey == null || accessKey.isEmpty() || accessKey.equals(totp))
                      return '"' + TotpType.JWT.name() + " " + getToken(pcc, sc) + " " + notAfter(pcc) + '"';
                    return "false";
                case JWT:
                    totp = st.nextToken();
                    JwtParser parser = Jwts.parser().setSigningKeyResolver(OAuth2Manager.AUTH);
                    Jws<Claims> claims = parser.parseClaimsJws(totp);
                    String username = claims.getBody().getSubject();
                    String pccid = claims.getBody().getId();
                    if (username.equals(sc.getUserPrincipal().getName()) 
                        && pccid.equals(String.valueOf(pcc.getClassCourseID()))
                        
                    )
                    {
                        return '"' + TotpType.JWT.name() + " " + getToken(pcc, sc) + " " + notAfter(pcc) + '"';                  	
                    }
                    return "false";
                default:
                    throw new IllegalArgumentException("not implemented");
            }

        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "verifyTOTP failed", e);
            return "false";
        } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "verifyTOTP failed", e);
            return "false";
        }
    }
    
    
    @PUT
    @Produces("application/json")
    @Path("linkSaml")
    public Boolean linkSaml(@Context SecurityContext sc, RestSamlUser rest) throws Dwo2Exception
    {
      UserState_U state = AnonDomainAuthorizer.build().submitUser(sc);
      PersistentUser user = state.getUser();
      String org = rest.getDomSamlUser().getSamlOrgId();
      String account = rest.getDomSamlUser().getSamlUserId();
      PersistentSamlUser saml = new PersistentSamlUser();
      saml.setSamlorgid(org);
      saml.setSamluserid(account);
      saml.setUserID(user.getId());
      saml.setAuthToken("none");
      SamlUserManager.create(saml);
      
      return Boolean.TRUE;
    }

    public static void verifyTOTP(SecurityContext sc, String ccid, String totp,
        PersistentCourse courseOf, PersistentSchoolClass classOf) throws Dwo2Exception {
      try {
        LOG.info("ccid = " + ccid);
        LOG.info("totp = " + totp);
        ccid = Base64.decodeAsString(ccid);
        DomClassCourse id = new DomClassCourse();
        id.setId(new PersistenceId(ccid));
        Long nativeId = MySQLPersistenceId.getNativeId(id);
        PersistentClassCourse pcc = ClassCourseManager.findEntity(nativeId);
        if (pcc.getCourseID().longValue() != courseOf.getCourseID().longValue())
          throw new Dwo2Exception(Dwo2ExceptionCode.Exam_AuthenticationError, "wrong course");
        
        String accessKey = pcc.getAccessKey();

        StringTokenizer st = new StringTokenizer(totp);
        switch (TotpType.valueOf(st.nextToken())) {
            case PLAIN:
                totp = Base64.decodeAsString(st.nextToken());
                if (pcc.getClassID() == classOf.getClassID().longValue()
                    && pcc.getCourseID().equals(courseOf.getCourseID())
                    )
                if (accessKey == null || accessKey.isEmpty() || accessKey.equals(totp))
                  return;
                throw new Dwo2Exception(Dwo2ExceptionCode.Exam_AuthenticationError, "not correct");
          case JWT:
                totp = st.nextToken();
                JwtParser parser = Jwts.parser().setSigningKeyResolver(OAuth2Manager.AUTH);
                Jws<Claims> claims = parser.parseClaimsJws(totp);
                String username = claims.getBody().getSubject();
                String pccid = claims.getBody().getId();
                if (username.equals(sc.getUserPrincipal().getName()) 
                    && pcc.getCourseID().equals(courseOf.getCourseID())
                    && pcc.getClassID() == classOf.getClassID().longValue()
                    && pccid.equals(String.valueOf(pcc.getClassCourseID())))
                    return;
            default:
              throw new Dwo2Exception(Dwo2ExceptionCode.Exam_AuthenticationError, "not implemented");
        }

    } catch (RuntimeException e) {
        LOG.log(Level.SEVERE, "verifyTOTP failed", e);
        throw new Dwo2Exception(Dwo2ExceptionCode.Exam_AuthenticationError, e.toString());
    } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "verifyTOTP failed", e);
        throw e;
    }

  }
}
