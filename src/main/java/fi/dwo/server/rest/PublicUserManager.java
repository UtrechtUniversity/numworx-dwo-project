package fi.dwo.server.rest;

import com.digitalmolehill.crypto.SymmetricCryptor;
import fi.dwo.rest.dom.entities.DomLoginCheck;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestNewUser;
import fi.dwo.rest.entities.RestSamlUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import javax.mail.*;
import javax.mail.internet.*;

import static java.lang.Thread.sleep;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/user")
public class PublicUserManager {

    private static final Logger LOG = Logger.getLogger(PublicUserManager.class.getName());

    @Context
    private ServletContext servletContext;

    /**
     * Registers a new user.
     *
     * @param newUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean submitNewUser(RestNewUser newUserReg) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentUser u;
        u = UserManager.findByUserName(newUserReg.getDomNewUser().getUsername());
        if (u != null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "User with user id " + u.getUsername() + " already exists.");
        }
        //invariant: usercode does not exists

        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        //set null school values if appropiate.
        if (newUserReg.getDomNewUser().getSchoolLogin() == null && newUserReg.getDomNewUser().getSchoolCode() == null) {
            newUserReg.getDomNewUser().setSchoolLogin("null"); //TODO retrieve the null school login and code from the DwoSystemParameters.
            newUserReg.getDomNewUser().setSchoolCode("null");
        }

        //TODO user EntityManager APIs
        try {
            //           school = SchoolManager.findBySchoolLogin(newUserReg.getSchoolLogin());
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchoolGroup sg join PersistentSchool s where s.schoolID = sg.schoolID and s.schoolLogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", newUserReg.getDomNewUser().getSchoolLogin());
            q.setParameter("schoolcode", newUserReg.getDomNewUser().getSchoolCode());
            q.setParameter("role", (newUserReg.getDomNewUser().getRole().name()));
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                LOG.log(Level.INFO, "Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
                String msg = String.format("Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINE, "School-manager retrieved school {0} from school login and school code for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
        }
        catch (Exception ex) {
            LOG.log(Level.WARNING, "School registration authentication failed.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_authentication_failed, "School registration authentication failed, please try again.");
        }
        finally {
            em.close();
        }

        if (!school.licenseIsValid()) {
            LOG.log(Level.INFO, "Registration failde for school {0}, school id {1}, the license expired on {1}.", new Object[]{school.getSchoolName(), school.getSchoolID(), school.getExpire()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "The license expired on " + school.getExpire());
        }

        Date now = DwoDateUtilities.getCurrentDwoDate();

        //invariant: usercode does not exists and school exists for schoollogin and schoolcode and has a valid licence.
        //adding user to school in role.         
        PersistentUser user = new PersistentUser();
        user.setEmail(newUserReg.getDomNewUser().getEmail());
        user.setGivenName(newUserReg.getDomNewUser().getGivenName());
        user.setInsertion(newUserReg.getDomNewUser().getInsertion());
        user.setLastname(newUserReg.getDomNewUser().getFamilyName());
        user.setPassword(newUserReg.getDomNewUser().getPassword());
        user.setRegisterDate(now);
        user.setUsername(newUserReg.getDomNewUser().getUsername());
        user.setSchoolGroupId(sg.getSchoolGroupID());
        user.setSingleSchoolAccount(false);
        //add user
        try {
            UserManager.create(user);
        }
        catch (EntityExistsException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        }
        catch (Exception e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Internal error.");
        }

        //user add success
        user = UserManager.findByUserName(user.getUsername());
        LOG.log(Level.INFO, "User {0} {1} {2} with usercode {3} and index {4} was added to the database.", new Object[]{user.getGivenName(), user.getInsertion(), user.getLastname(), user.getUsername(), user.getId()});

        // building hasRole
        PersistentHasRole hasRole = new PersistentHasRole();
        // buiding compound key hasRole
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getId());
        hasRole.setPersistentHasRolePK(pk);

        hasRole.setClassID(null);
        hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
        hasRole.setRegisterDate(now);
        hasRole.setRights("_"); //TODO make a rights manager
        HasRoleManager.create(hasRole);
        LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {3} was added to the database.", new Object[]{hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), newUserReg.getDomNewUser().getRole().name()});
        //success

//        //building hasRole for null school
//        PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
//        Long schoolGroupId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
//        pk.setSchoolGroupID(schoolGroupId);
//        pk.setUserID(user.getId());
//        hasRole.setPersistentHasRolePK(pk);
//
//        hasRole.setClassID(null);
//        hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
//        hasRole.setRegisterDate(now);
//        hasRole.setRights("_"); //TODO make a rights manager
//        HasRoleManager.create(hasRole);
//        LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {3} was added to the database.", new Object[]{hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), newUserReg.getDomNewUser().getRole().name()});
        return true;
    }

    /**
     * Verifies that a user, password combination. Waits a configured amount of
     * time before giving a response.
     *
     * @param loginCheck
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/loginCheck")
    public Boolean getLoginCheck(RestLoginCheck loginCheck) {
        DomLoginCheck domCheck = loginCheck.getDomLoginCheck();
        PersistentUser user = UserManager.login(domCheck.getUsername(), DomLoginCheck.crypt(domCheck.getPassword()));

        //not using sleep in synchronized semaphore resource, using 
        //<Realm className="org.apache.catalina.realm.LockOutRealm" failureCount="5">
        //in server.xml of tomcat.
        return (user != null);
    }

    /**
     * Retrieves a user from samluser.
     *
     * @param samlRestUser
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitSaml")
    public DomUserFull getSamlUser(RestSamlUser samlRestUser) {
        //should return a DomFullUser. 
        PersistentSamlUser samlUser = SamlUserManager.findEntity(samlRestUser.getDomSamlUser().getSamlUserId(), samlRestUser.getDomSamlUser().getSamlOrgId());
        if (samlUser != null
                && samlUser.getAuthToken().equals(samlRestUser.getDomSamlUser().getAuthToken()) //&& samlUser.tokenIsValid(20000) //TODO TESTING, productie aan.
                ) {//milisseconden

            LOG.log(Level.SEVERE, "equal {0}, tokenValid {1} {2} time={3}", new Object[]{samlUser.getAuthToken().equals(samlRestUser.getDomSamlUser().getAuthToken()), samlUser.tokenIsValid(20000), samlUser, System.currentTimeMillis()});

            return UserManager.findEntity(samlUser.getUserID()).buildDomUserFull();
        } else {
            LOG.log(Level.SEVERE, "Incorrect saml-authentication event for samlOrg {0} samlUser {1} and authToken {2}: {3}", new Object[]{samlRestUser.getDomSamlUser().getSamlOrgId(), samlRestUser.getDomSamlUser().getSamlUserId(), samlRestUser.getDomSamlUser().getAuthToken(), samlUser});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "The authentication is invalid, this event is logged.");
        }
    }

// Traditional FORM, KISS principe!!!
    @POST
    @Produces({"text/plain"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("registerSAML")
    public String registerSAML(@Context SecurityContext sc,
            @FormParam("userident") String userIdent,
            @FormParam("samluserid") String samlUserId,
            @FormParam("samlorgid") String samlOrgId,
            @FormParam("gn") String givenName,
            @FormParam("prefix") String insertion,
            @FormParam("fn") String familyName,
            @FormParam("email") String email,
            @FormParam("role") String role, // STUDENT/TEACHER
            @FormParam("schoolID") long schoolID,//Are dead, very dead. Send schoologin name.
            @FormParam("classname") String schoolClassName
    /*... more? ...*/
    ) {
        LOG.log(Level.FINE, "Starting registerSAML(usercode, samlUserId, samlOrgId: {0},{1},{2}", new Object[]{userIdent, samlUserId, samlOrgId});

        PersistentSchool school = SchoolManager.findEntity(schoolID);
        if (school == null) {
            LOG.log(Level.SEVERE, "SchoolID given in form does not exists!");
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal form values, this will be logged!");
        }
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(schoolClassName, school);
        LOG.log(Level.FINE, "starting secureRandom.");
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "SecureRandom failed.");
        }
        LOG.log(Level.FINE, "Creating authToken.");
        Short authToken = (short) secureRandom.nextInt();
        RoleType roleType = RoleType.NONE;
        try {
            roleType = RoleType.valueOf(role);
        }
        catch (Exception e1) {
            LOG.log(Level.SEVERE, "unknown RoleType " + role, e1);
        }
        PersistentUser pUser;
        //DONE check if username + domain exists, in tblsamluser
        LOG.log(Level.FINE, "Checking samluser.");
        PersistentSamlUser samlUser = SamlUserManager.findEntity(samlUserId, samlOrgId);
        if (samlUser == null) {
            //generate new persistentUser
            pUser = new PersistentUser();
            pUser.setEmail(email);
            pUser.setGivenName(givenName);
            pUser.setInsertion(insertion);
            pUser.setLastname(familyName);
            pUser.setPassword(Long.toHexString(secureRandom.nextLong()));
            pUser.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            //TODO Wim change the parameters.
            pUser.setUsername(userIdent + '@' + school.getSchoolLogin());
            pUser.setSchoolGroupId(SchoolGroupManager.findBySchoolAndRole(school, roleType).getSchoolGroupID());

            try {
                switch (roleType) {
                    case STUDENT:
                        pUser.setSingleSchoolAccount(true);
                        SchoolUtilManager.addSingleSchoolStudentAccount(pUser, school);
                        if (schoolClass == null) {
                            break;
                        }
                        PersistentStudentOfClass soc = new PersistentStudentOfClass();
                        soc.setPersistentStudentOfClassPK(
                                new PersistentStudentOfClassPK(pUser.getId(),
                                        schoolClass.getClassID(), pUser.getSchoolGroupId()));
                        soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
                        try {
                            StudentOfClassManager.create(soc);
                        }
                        catch (PersistenceException e) {
                            LOG.log(Level.SEVERE, null, e);
                            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
                        }
                        PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(pUser, school, roleType);
                        hr.setClassID(schoolClass.getClassID());
                        HasRoleManager.edit(hr);
                        break;
                    case TEACHER:
                        pUser.setSingleSchoolAccount(false);
                        SchoolUtilManager.addAccountAsTeacherInSchool(pUser, school);
                        //teacher do not earn free class access!
                        break;
                }
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, registration failed.");
            }
            //generate samlUser
            PersistentSamlUser sUser = new PersistentSamlUser();
            sUser.setSamluserid(samlUserId);
            sUser.setSamlorgid(samlOrgId);
            sUser.setAuthToken(authToken.toString());
            sUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            sUser.setUserID(pUser.getId());
            SamlUserManager.create(sUser);
            LOG.log(Level.FINE, "Created new  samluser.");
        } else {
            try {
                //TODO put randstring in tblsamluser (add column to database)
                samlUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                samlUser.setAuthToken(authToken.toString());
                SamlUserManager.edit(samlUser);
                if (schoolClass != null && roleType == RoleType.STUDENT) {
                    PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(UserManager.findEntity(samlUser.getUserID()), school, roleType);
                    //TODO hr == null? aanmaken of overslaan? nu fatal DWO2 exception
                    PersistentStudentOfClassPK socPK = new PersistentStudentOfClassPK(hr.getPersistentHasRolePK().getUserID(),
                            schoolClass.getClassID(), hr.getPersistentHasRolePK().getSchoolGroupID());
                    PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socPK);
                    if (soc == null) {
                        soc = new PersistentStudentOfClass();
                        soc.setPersistentStudentOfClassPK(
                                new PersistentStudentOfClassPK(hr.getPersistentHasRolePK().getId(),
                                        schoolClass.getClassID(), hr.getPersistentHasRolePK().getSchoolGroupID()));
                        soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
                        try {
                            StudentOfClassManager.create(soc);
                        }
                        catch (PersistenceException e) {
                            LOG.log(Level.SEVERE, null, e);
                            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
                        }
                    }
                    hr.setClassID(schoolClass.getClassID());
                    HasRoleManager.edit(hr);
                }
                LOG.log(Level.FINE, "Set class and update samluser.");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, login failed.");
            }
        }
        return authToken.toString();
    }

    /**
     * Used for manual testing.
     * 
     * @return 
     */    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/requestNewPassword/html")
    public String reqPasswordChangeForm() {
        String r = "<HTML><BODY><p> "+TextMapper.getText(TextMapper.LBL_REQUEST_NEW_PASSWORD)
                + "<form action=\"http://localhost:8080/dwo/rest/public/user/requestPasswordChange\" method=\"post\" >\n"
                + "<table>"
                + "<tr><td align=\"right\">"+TextMapper.getText(TextMapper.LBL_USERNAME)+": </td> <td><input type=\"text\" size=\"80\" name=\"usercode\" value=\"project_wim\"></td></tr>"
                + "<tr><td align=\"right\">"+TextMapper.getText(TextMapper.LBL_EMAIL)+":</td> <td><input type=\"text\" size=\"80\" name=\"email\" value=\"w.p.g.vanvelthoven@uu.nl\"> </td></tr>"
                + "<tr><td/><td align=\"right\"><input type=\"submit\" value=\""+TextMapper.getText(TextMapper.BTN_OK)+"\" ></td></tr>"
                + "<table>"
                + "</form>";
        r += "</p></BODY> </HTML>";
        return r;
    }

    /**
     * Registers a new user.
     *
     * @param sc
     * @param usercode
     * @param email
     * @return
     * @throws java.lang.Exception
     */
    @POST
    @Produces({"text/plain"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/requestPasswordChange")
    public String requestPasswordChange(
            @FormParam("usercode") String usercode,
            @FormParam("email") String email
    ) throws Exception {
        String result = TextMapper.getText(TextMapper.DLG_CONFIRM);;
        //Check if <username,email> exists 
        PersistentUser user = UserManager.findByUserName(usercode);
        if (user != null && user.getEmail().equals(email)) {
            //Create JSON string for changing password        

            //Password for encrypting is unix timestamp modulus 10 minutes + randomseed
            long timeslot = 78578 + DwoDateUtilities.getCurrentDwoUnixTimeStamp() / 600000;
            String seed = Long.toHexString(timeslot);
            String data = "dwoAuthCode:" + usercode + ":" + email; //TODO are ':' allowed in usercodes?
            SymmetricCryptor cryptor = new SymmetricCryptor();
            String authCode = cryptor.encrypt(seed.toCharArray(), data); //encrypt JSON String
            LOG.log(Level.INFO, "For username {0} and timeslot {1} the server generated an authcode.", new Object[]{user.getUsername(), timeslot});
            LOG.log(Level.FINER, "For username {0} and timeslot {1} the server generated authcode {2} .", new Object[]{user.getUsername(), timeslot, authCode});
            
            //place this in servlet
            String smtpServer = servletContext.getInitParameter("fi.dwo.server.rest.smtp.server");
            String smtpPort = servletContext.getInitParameter("fi.dwo.server.rest.smtp.port");
            String smtpTLS = servletContext.getInitParameter("fi.dwo.server.rest.smtp.tls");
            String smtpUser = servletContext.getInitParameter("fi.dwo.server.rest.smtp.user");
            String smtpPassword = servletContext.getInitParameter("fi.dwo.server.rest.smtp.password");
            String smtpEmail = servletContext.getInitParameter("fi.dwo.server.rest.smtp.email");//from address.
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.starttls.enable", "true");            
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            
            Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
                        @Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpUser, smtpPassword);
			}
		  });            

            // uncomment for debugging infos to stdout
            // mailSession.setDebug(true);
            Transport transport = session.getTransport();

            MimeMessage message = new MimeMessage(session);
            message.setContent("Your authcode is:"+authCode, "text/plain");
            message.setFrom(new InternetAddress(smtpEmail));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(user.getEmail()));

            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();
        } else {
            result = TextMapper.getText(TextMapper.LBL_UNKNOWN_COMBINATION);
        }
        //Always wait 30 seconds before response.        
        sleep(3000); //shorter for debugging
        //return response (ok or logging).
        return result;
    }

    /**
     * Used for manual testing.
     * 
     * @return 
     */
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/submitNewPassword/html")
    public String passwordChangeForm() {
        String r = "<HTML><BODY><p>"+TextMapper.getText(TextMapper.LBL_ENTER_AUTHCODE_FOR_NEW_PASSWORD)
                + "<form action=\"http://localhost:8080/dwo/rest/public/user/submitPasswordChange\" method=\"post\" >"
                + "<table>"
                + "<tr><td align = \"right\">authCode:</td><td><input type=\"text\" size=\"80\" name=\"authCode\" value=\"wim_project\" ></td></tr>"
                + "<tr><td align = \"right\">new password:</td><td><input type=\"text\" size=\"80\" name=\"newPassword\" value=\"pass\" ></td></tr>"
                + "<tr><td/><td align =\"right\"><input type=\""+TextMapper.getText(TextMapper.BTN_OK)+"\" value=\"Submit\"></td></tr>"
                + "</table>"
                + "</form>";
        r += "</BODY></HTML>";
        return r;
    }

    /**
     * Registers a new user.
     *
     * @param authCode
     * @param newPassword
     * @return
     * @throws java.lang.Exception
     */
    @POST
    @Produces({"text/plain"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/submitPasswordChange")
    public String submitPasswordChange(
            @FormParam("authCode") String authCode,
            @FormParam("newPassword") String newPassword
    ) throws Exception {
        //Password for encrypting current unix timestamp modulus 10 minutes + randomseed
        //decrypt JSON String
        String data = "";
        long timeslot = 78578 + DwoDateUtilities.getCurrentDwoUnixTimeStamp() / 600000;
        for (int i = 0; i < 6; i++) {
            String seed = Long.toHexString(timeslot);
            try {
                SymmetricCryptor cryptor = new SymmetricCryptor();
                data = cryptor.decrypt(seed.toCharArray(), authCode);
                if (data.startsWith("dwoAuthCode:")) {
                    break;
                }
            }
            catch (Exception e) {
                //illegal code
            }
            timeslot = timeslot - 1;
        }
        if (data.startsWith("dwoAuthCode:")) {
            PersistentUser user = UserManager.findByUserName(data.split(":")[1]);
            user.setPassword(newPassword);
            UserManager.edit(user);
            LOG.log(Level.INFO, "Updated password of user with username {0} of timeslot {1}  from valid authCode.", new Object[]{user.getUsername(), timeslot});
            LOG.log(Level.FINER, "Updated password of user with username {0} of timeslot {1} using authcode {2}.", new Object[]{user.getUsername(), timeslot, authCode});
            newPassword = TextMapper.getText(TextMapper.DLG_CONFIRM);
        } else {
            newPassword = TextMapper.getText(TextMapper.LBL_ILLEGAL_AUTHCODE);
        }
        //Always wait 30 seconds before response.        
        sleep(3000);//10 timesshorter for debugging
        //return response (ok or logging).
        return newPassword;

    }
}
