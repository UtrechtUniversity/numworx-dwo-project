package fi.dwo.server.rest;

import com.digitalmolehill.crypto.SymmetricCryptor;

import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.system.MD5;
import fi.dwo.commons.system.TextMapper;
import nl.uu.fi.dwo.rest.entities.RestAuthToken;
import nl.uu.fi.dwo.rest.entities.RestLoginCheck;
import nl.uu.fi.dwo.rest.entities.RestNewUser;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import fi.dwo.commons.util.DwoDateUtilities;
import nl.uu.fi.dwo.rest.DwoLocale;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.LoginContextUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.io.UnsupportedEncodingException;

import static java.lang.Thread.sleep;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import java.util.concurrent.ThreadLocalRandom;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.URI.DefaultCharsetChanged;

import static java.lang.Thread.sleep;

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
        if (newUserReg == null || newUserReg.getDomNewUser() == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomNewUser n = newUserReg.getDomNewUser();
        if (!ValidUserFieldsChecker.isNonEmptyNorNull(n.getUsername(), n.getFamilyName(), n.getGivenName(), n.getEmail(), n.getPassword())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Required_Fields, "Required fields empty or null");
        }

        if (!ValidUserFieldsChecker.isValidEmail(newUserReg.getDomNewUser().getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if (!ValidUserFieldsChecker.isValidUserName(newUserReg.getDomNewUser().getUsername())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
        }

        PersistentUser u;
        u = UserManager.findByUserName(newUserReg.getDomNewUser().getUsername());
        if (u != null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "User with user id " + u.getUsername() + " already exists.");
        }
        //invariant: usercode does not exists

        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        //set null school values if appropiate.
        if (newUserReg.getDomNewUser().getSchoolLogin() == null && newUserReg.getDomNewUser().getSchoolCode() == null
                && newUserReg.getDomNewUser().getRole().equals(RoleType.STUDENT)) {
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
                LOG.log(Level.INFO, "Registration failed for school NULL with school login {0} and school code {1} for usercode {2}.", new Object[]{newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
                String msg = String.format("Registration failed for school with school login {0} and school code {1} for usercode {2}.", new Object[]{newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINE, "School-manager retrieved school {0} from school login and school code for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getDomNewUser().getSchoolLogin(), newUserReg.getDomNewUser().getSchoolCode(), newUserReg.getDomNewUser().getUsername()});
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "School registration authentication failed.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_authentication_failed, "School registration authentication failed, please try again.");
        } finally {
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
        } catch (EntityExistsException e) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "Username exists");
        } catch (Exception e) {
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
        LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {2} was added to the database.", new Object[]{hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), newUserReg.getDomNewUser().getRole().name()});
        //success

        //building hasRole for null school
        if (!newUserReg.getDomNewUser().getSchoolLogin().equals("null")
                || !newUserReg.getDomNewUser().getSchoolCode().endsWith("null")
                || !newUserReg.getDomNewUser().getRole().equals(RoleType.STUDENT)) {
            PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
            Long schoolGroupId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
            pk.setSchoolGroupID(schoolGroupId);
            pk.setUserID(user.getId());
            hasRole.setPersistentHasRolePK(pk);

            hasRole.setClassID(null);
            hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
            hasRole.setRegisterDate(now);
            hasRole.setRights("_"); //TODO make a rights manager
            HasRoleManager.create(hasRole);
            LOG.log(Level.INFO, "HasRole for user, schoolgroup index {0} {1} and role {3} was added to the database.", new Object[]{hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), newUserReg.getDomNewUser().getRole().name()});
        }

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
     * Retrieves a DomUserFullwLoginContext from samluser. In this case a new
     * loginContext is forced.
     *
     * @param samlRestUser
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submitSaml")
    public DomUserFullwLoginContext getSamlUser(RestSamlUser samlRestUser) {
        //should return a DomFullUser. 
        PersistentSamlUser samlUser = SamlUserManager.findEntity(samlRestUser.getDomSamlUser().getSamlUserId(), samlRestUser.getDomSamlUser().getSamlOrgId());
        if (samlUser != null
                && samlUser.getAuthToken().equals(samlRestUser.getDomSamlUser().getAuthToken()) //&& samlUser.tokenIsValid(20000) //TODO TESTING, productie aan.
                ) {//milisseconden

            LOG.log(Level.SEVERE, "equal {0}, tokenValid {1} {2} time={3}", new Object[]{samlUser.getAuthToken().equals(samlRestUser.getDomSamlUser().getAuthToken()), samlUser.tokenIsValid(20000), samlUser, System.currentTimeMillis()});

            PersistentUser user = UserManager.findEntity(samlUser.getUserID());
            try {
                return user.buildDomUserFullwLoginContext(LoginContextUtilManager.forceNewLoginContextSession(user));
            } catch (Dwo2Exception ex) {
                Logger.getLogger(PublicUserManager.class.getName()).log(Level.SEVERE, "Invalid software state, this should not have happened.", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Invalid software state. This should not have happened.");
            }
        } else {
            LOG.log(Level.SEVERE, "Incorrect saml-authentication event for samlOrg {0} samlUser {1} and authToken {2}: {3}", new Object[]{samlRestUser.getDomSamlUser().getSamlOrgId(), samlRestUser.getDomSamlUser().getSamlUserId(), samlRestUser.getDomSamlUser().getAuthToken(), samlUser});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "The authentication is invalid, this event is logged.");
        }
    }
//
//    public static DomUserFullwLoginContext createUserFullwLoginContext(
//            PersistentUser user) {
//        try {
//            PersistentLoginContext loginContext = LoginContextUtilManager.getCurrentLoginContext(user);
//            loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
//            LoginContextManager.edit(loginContext);
//            DomUserFullwLoginContext result = user.buildDomUserFullwLoginContext(loginContext);
//            return result;
//        } catch (Dwo2Exception ex) {
//            Logger.getLogger(PublicUserManager.class.getName()).log(Level.SEVERE, "", ex);
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "The LoginContext failed to be handled for this user.");
//        }
//    }

    @PUT
    @Produces({"application/json"})
    @Path("/getUserFromAuthToken")
    public DomUserFullwLoginContext getUserFromAuthToken(RestAuthToken restAuthToken) throws Dwo2RestException {
        String authToken = restAuthToken.getAuthToken();
        authToken = new String(
                Base64.getUrlDecoder().decode(authToken),
                StandardCharsets.UTF_8
        );
        char version = authToken.charAt(0);
        switch (version) {
            default:
                LOG.log(Level.SEVERE, "Unsupported AuthToken {0}, version", restAuthToken.getAuthToken());
                break;
            case '1': // Weak token, only for proof of concept, 5 minutes timeout
                String[] split = authToken.split("\f");
                if (split.length != 4) {
                    LOG.log(Level.SEVERE, "Illegal authToken {0}, wrong format", restAuthToken.getAuthToken());
                    break;
                }
                long time = 0L;
                try {
                    time = Long.valueOf(split[1]);
                } catch (NumberFormatException e) {
                    LOG.log(Level.SEVERE, "Illegal authToken {0}, no timestamp", restAuthToken.getAuthToken());
                    break;

                }
                String username = split[2];
                String md5hash = split[3];
                if (time < System.currentTimeMillis() - 5 * 60 * 1000L) {
                    LOG.log(Level.SEVERE, "AuthToken {0}, too old", restAuthToken.getAuthToken());
                    break;
                }
                PersistentUser user = UserManager.login(username, md5hash);
                if (user == null) {
                    LOG.log(Level.SEVERE, "Illegal authToken {0}, no user", restAuthToken.getAuthToken());
                    break;
                }
                try {
                    return user.buildDomUserFullwLoginContext(LoginContextUtilManager.forceNewLoginContextSession(user));
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(PublicUserManager.class.getName()).log(Level.SEVERE, "Invalid software state, this should not have happened.", ex);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Invalid software state. This should not have happened.");
                }
        }
        throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "The authentication is invalid, this event is logged.");
    }

    @POST
    @Produces({"text/plain"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/registerSAML")
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
        //Student moet singleSchool False worden. En missing hasRole voor schooladmin en teacher en student moet worden gemaakt indien user bestaat. Wim test, na implementatie.
        LOG.log(Level.FINE, "Starting registerSAML(usercode, samlUserId, samlOrgId: {0},{1},{2}", new Object[]{userIdent, samlUserId, samlOrgId});

        PersistentSchool school = SchoolManager.findEntity(schoolID);
        if (school == null) {
            LOG.log(Level.SEVERE, "SchoolID given in form does not exists!");
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal form values, this will be logged!");
        }
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(schoolClassName, school);
        LOG.log(Level.FINE, "starting secureRandom.");
//        SecureRandom secureRandom = null;
//        try {
//            secureRandom = SecureRandom.getInstanceStrong();
//        }
//        catch (NoSuchAlgorithmException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "SecureRandom failed.");
//        }
        ThreadLocalRandom secureRandom = ThreadLocalRandom.current();
        LOG.log(Level.FINE, "Creating authToken.");
        Short authToken = (short) secureRandom.nextInt();
        RoleType roleType = RoleType.NONE;
        try {
            roleType = RoleType.valueOf(role);
        } catch (Exception e1) {
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
                        } catch (PersistenceException e) {
                            LOG.log(Level.SEVERE, null, e);
                            //throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
                        }
                        PersistentHasRole hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, school, roleType);
                        hr.setClassID(schoolClass.getClassID());
                        HasRoleManager.edit(hr);
                        break;
                    case TEACHER:
                        pUser.setSingleSchoolAccount(false);
                        SchoolUtilManager.addAccountAsTeacherInSchool(pUser, school);
                        //teacher do not earn free class access!
                        break;
                    default:
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, registration failed.");
            }
            //generate samlUser
            PersistentSamlUser sUser = new PersistentSamlUser();
            sUser.setSamluserid(samlUserId);
            sUser.setSamlorgid(samlOrgId);
            sUser.setAuthToken(authToken.toString());
            sUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            sUser.setUserID(pUser.getId());
            try {
                SamlUserManager.create(sUser);
                LOG.log(Level.FINE, "Created new  samluser.");
            } catch (PersistenceException e) {
                LOG.log(Level.SEVERE, "User exits probably.", e);
            }
        } else {
            try {
                //TODO put randstring in tblsamluser (add column to database)
                samlUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                samlUser.setAuthToken(authToken.toString());
                SamlUserManager.edit(samlUser);
                if (schoolClass != null && roleType == RoleType.STUDENT) {
                    PersistentHasRole hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(UserManager.findEntity(samlUser.getUserID()), school, roleType);
                    //TODO hr == null? aanmaken of overslaan? nu fatal DWO2 exception
                    PersistentStudentOfClassPK socPK = new PersistentStudentOfClassPK(hr.getPersistentHasRolePK().getUserID(),
                            schoolClass.getClassID(), hr.getPersistentHasRolePK().getSchoolGroupID());
                    PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socPK);
                    if (soc == null) {
                        soc = new PersistentStudentOfClass();
                        soc.setPersistentStudentOfClassPK(socPK);
                        soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
                        try {
                            StudentOfClassManager.create(soc);
                        } catch (PersistenceException e) {
                            LOG.log(Level.SEVERE, "create " + soc, e);
//                            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
                        }
                    }
                    hr.setClassID(schoolClass.getClassID());
                    HasRoleManager.edit(hr);
                }
                LOG.log(Level.FINE, "Set class and update samluser.");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, login failed.");
            }
        }
        return authToken.toString();
    }

    /**
     *
     * @param sc
     * @param userIdent
     * @param samlUserId
     * @param samlOrgId
     * @param givenName
     * @param insertion
     * @param familyName
     * @param email
     * @param role Only STUDENT, TEACHER or SCHOOLADMIN are allowed
     * @param schoolID
     * @param schoolClassName
     * @return
     */
    @POST
    @Produces({"text/plain"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/registerSAMLV2")
    public String registerSAMLV2(@Context SecurityContext sc,
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
        //En missing hasRole voor schooladmin en teacher en student moet worden 
        //gemaakt indien user bestaat. Wim test, na implementatie.

        LOG.log(Level.FINE, "Starting registerSAML(usercode, samlUserId, samlOrgId: {0},{1},{2}", new Object[]{userIdent, samlUserId, samlOrgId});

        PersistentSchool school = SchoolManager.findEntity(schoolID);
        if (school == null) {
            LOG.log(Level.SEVERE, "SchoolID given in form does not exists!");
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal form values, this will be logged!");
        }
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(schoolClassName, school);
        LOG.log(Level.FINE, "starting secureRandom.");
//        SecureRandom secureRandom = null;
//        try {
//            secureRandom = SecureRandom.getInstanceStrong();
//        }
//        catch (NoSuchAlgorithmException ex) {
//            LOG.log(Level.SEVERE, null, ex);
//            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "SecureRandom failed.");
//        }
        ThreadLocalRandom secureRandom = ThreadLocalRandom.current();
        LOG.log(Level.FINE, "Creating authToken.");
        Short authToken = (short) secureRandom.nextInt();
        RoleType roleType = RoleType.NONE;
        try {
            roleType = RoleType.valueOf(role);
        } catch (Exception e1) {
            LOG.log(Level.SEVERE, "unknown RoleType " + role, e1);
        }
        if (roleType != RoleType.STUDENT || roleType != RoleType.TEACHER || roleType != RoleType.SCHOOLADMIN) {
            String msg = MessageFormat.format("Illegal RoleType {0} requested.", new Object[]{roleType.name()});
            LOG.log(Level.SEVERE, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        PersistentUser pUser = null;
        //DONE check if username + domain exists, in tblsamluser
        LOG.log(Level.FINE, "Checking samluser.");
        PersistentSamlUser samlUser = SamlUserManager.findEntity(samlUserId, samlOrgId);
        //add user if not exists
        if (samlUser == null) {
            //generate new persistentUser
            pUser = new PersistentUser();
            pUser.setEmail(email);
            pUser.setGivenName(givenName);
            pUser.setInsertion(insertion);
            pUser.setLastname(familyName);
            pUser.setPassword(Long.toHexString(secureRandom.nextLong()));
            pUser.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            pUser.setUsername(userIdent + '@' + school.getSchoolLogin());
            pUser.setSchoolGroupId(SchoolGroupManager.findBySchoolAndRole(school, roleType).getSchoolGroupID());
            //must be multi-school 
            pUser.setSingleSchoolAccount(false);
            try {
                UserManager.create(pUser);
            } catch (PersistenceException e) {
                LOG.log(Level.SEVERE, null, e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error. Can't create userlogin" + pUser.getUsername());
            }
        } else {
            //TODO put randstring in tblsamluser (add column to database)
            samlUser.setAuthTokenTimestamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            samlUser.setAuthToken(authToken.toString());
            try {
                SamlUserManager.edit(samlUser);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error. Can't update samluser with id:" + samlUser.getId() + ".");
            }
        }

        try {
            //add hasRole if not exists
            HasRoleUtilManager.getOrCreateHasRoleInSchool(pUser, school, roleType);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal form values, this will be logged!");
        }

        //add to SchoolClass if not a member
        try {
            switch (roleType) {
                case STUDENT:
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
                    } catch (PersistenceException e) {
                        LOG.log(Level.SEVERE, null, e);
                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
                    }
                    PersistentHasRole hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, school, roleType);
                    hr.setClassID(schoolClass.getClassID());
                    HasRoleManager.edit(hr);
                    break;
//                case TEACHER:
//                    if (schoolClass == null) {
//                        break;
//                    }
//                    PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
//                    toc.setPersistentTeacherOfClassPK(
//                            new PersistentTeacherOfClassPK(pUser.getId(),
//                                    schoolClass.getClassID(), pUser.getSchoolGroupId()));
//                    toc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
//                    try {
//                        TeacherOfClassManager.create(toc);
//                    } catch (PersistenceException e) {
//                        LOG.log(Level.SEVERE, null, e);
//                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, schoolclass registration failed.");
//                    }
//                    hr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, school, roleType);
//                    hr.setClassID(schoolClass.getClassID());
//                    HasRoleManager.edit(hr);
//                    break;
                default:
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Server error, registration failed.");
        }
        return authToken.toString();
    }

    /**
     * Used for manual testing.
     *
     * @param language
     * @param back
     * @return
     */
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/requestNewPassword")
    public String reqPasswordChangeForm(@QueryParam("language") String language, @QueryParam("back") String back) {
        if (language == null) {
            language = TextMapper.getLanguage();
        }
        if (back == null) {
            back = "";
        }
        String old = TextMapper.getLanguage();
        TextMapper.setLanguage(language);
        String r = "<HTML><BODY><p> " + TextMapper.getText(TextMapper.LBL_REQUEST_NEW_PASSWORD)
                + "<form action=\"requestPasswordChange\" method=\"post\" >\n"
                + "<input type='hidden' name='language' value=\"" + htmlEncode(language) + "\">\n"
                + "<input type='hidden' name='back' value=\"" + htmlEncode(back) + "\">\n"
                + "<table>"
                + "<tr><td align=\"right\">" + TextMapper.getText(TextMapper.LBL_USERNAME) + ": </td> <td><input type=\"text\" size=\"50\" name=\"usercode\" value=\""
                + "\"></td></tr>"
                + "<tr><td align=\"right\">" + TextMapper.getText(TextMapper.LBL_EMAIL) + ":</td> <td><input type=\"text\" size=\"50\" name=\"email\" value=\""
                + "\"> </td></tr>"
                + "<tr><td/><td align=\"right\"><input type=\"submit\" value=\"" + TextMapper.getText(TextMapper.BTN_OK) + "\" ></td></tr>"
                + "<table>"
                + "</form>";
        r += "</p></BODY> </HTML>";
        TextMapper.setLanguage(old);
        return r;
    }

    @SuppressWarnings("deprecation")
    private String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return URLEncoder.encode(string);
        }
    }

    private String htmlEncode(String string) {
        string = string.replace("&", "&amp;");
        string = string.replace("\"", "&quot;");
        string = string.replace("<", "&lt;");
        string = string.replace(">", "&gt;");
        string = string.replace("'", "&apos;");
        return string;
    }

    /**
     * Registers a new user.
     *
     * @param sc
     * @param usercode
     * @param email
     * @param back
     * @param request
     * @return
     * @throws java.lang.Exception
     */
    @POST
    @Produces({"text/html"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/requestPasswordChange")
    public String requestPasswordChange(
            @FormParam("usercode") String usercode,
            @FormParam("email") String email,
            @FormParam("language") String language,
            @FormParam("back") String back,
            @Context HttpServletRequest request
    ) throws Exception {
        String old = TextMapper.getLanguage();
        if (language == null) {
            language = old;
        } else {
            TextMapper.setLanguage(language);
        }
        String result = TextMapper.getText(TextMapper.DLG_CONFIRM);
        //Check if <username,email> exists 
        PersistentUser user = null;
        user = UserManager.findByUserName(usercode);
        if (user != null && user.getEmail().equalsIgnoreCase(email)) {

            //Create JSON string for changing password        
            //Password for encrypting is unix timestamp modulus 10 minutes + randomseed
            long timeslot = 78578 + DwoDateUtilities.getCurrentDwoUnixTimeStamp() / 600000;
            String seed = Long.toHexString(timeslot);
            String data = "dwoAuthCode:" + usercode + ":" + email; // ':' is NOT allowed in usercodes!
            SymmetricCryptor cryptor = new SymmetricCryptor();
            String authCode = cryptor.encrypt(seed.toCharArray(), data); //encrypt JSON String
            LOG.log(Level.INFO, "For username {0} and timeslot {1} the server generated an authcode.", new Object[]{usercode, timeslot});
            LOG.log(Level.FINER, "For username {0} and timeslot {1} the server generated authcode {2} .", new Object[]{usercode, timeslot, authCode});

            //place this in servlet
            String smtpServer = servletContext.getInitParameter("fi.dwo.server.rest.smtp.server");
            String smtpPort = servletContext.getInitParameter("fi.dwo.server.rest.smtp.port");
            String smtpTLS = servletContext.getInitParameter("fi.dwo.server.rest.smtp.tls");
            String smtpAuth = servletContext.getInitParameter("fi.dwo.server.rest.smtp.auth");
            String smtpUser = servletContext.getInitParameter("fi.dwo.server.rest.smtp.user");
            String smtpPassword = servletContext.getInitParameter("fi.dwo.server.rest.smtp.password");
            String smtpEmail = servletContext.getInitParameter("fi.dwo.server.rest.smtp.email");//from address.
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.starttls.enable", smtpTLS);
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", smtpAuth);

            Session session;
            if (smtpAuth.equals("true")) {
                session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUser, smtpPassword);
                    }
                });
            } else {
                session = Session.getDefaultInstance(props);
            }
            // uncomment for debugging infos to stdout
            // mailSession.setDebug(true);
            Transport transport = session.getTransport();

            StringBuffer url = request.getRequestURL();
            int i = url.lastIndexOf("/");
            url.setLength(i + 1);
            url.append("submitNewPassword").append("?authCode=").append(authCode).append("&language=").append(language);
            MimeMessage message = new MimeMessage(session);
// FIXME i18n         
            String content = "Your authcode is: " + authCode;
            content += "\nGo to\n";
            content += url.toString();
            message.setContent(content, "text/plain");
            message.setFrom(new InternetAddress(smtpEmail));
// FIXME Beter subject, nu  "Nieuw wachtwoord"
            message.setSubject(TextMapper.getText(TextMapper.GUIP_PASSWORD));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(user.getEmail()));

            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();
            result = TextMapper.getText(TextMapper.LBL_EMAIL_WITH_AUTHCODE_SENT);
        } else {
            result = TextMapper.getText(TextMapper.LBL_UNKNOWN_COMBINATION);
        }
        //Always wait 30 seconds before response.        
        sleep(3000); //shorter for debugging
        //return response (ok or logging).
        if (back == null || back.isEmpty()) {
            back = "requestNewPassword?language=" + urlEncode(language);
        }
        String terug = TextMapper.getText(TextMapper.BTN_BACK);
        result = "<HTML><BODY>" + result + "<P><A HREF=\""
                + htmlEncode(back) + "\">" + terug + "</A></BODY></HTML>";
        TextMapper.setLanguage(old);
        return result;
    }

    /**
     * Used for manual testing.
     *
     * @param authCode
     * @param language
     * @return
     */
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/submitNewPassword")
    public String passwordChangeForm(@QueryParam("authCode") String authCode, @QueryParam("language") String language) {
        return passwordChangeForm(authCode, language, null);
    }

    private String passwordChangeForm(String authCode, String language,
            String message) {
        if (authCode == null) {
            authCode = "";
        }
        if (language == null) {
            language = TextMapper.getLanguage();
        }
        String old = TextMapper.getLanguage();
        TextMapper.setLanguage(language);
        if (message == null) {
            message = TextMapper.getText(TextMapper.LBL_ENTER_AUTHCODE_FOR_NEW_PASSWORD);
        }

        String r = "<HTML><BODY><p>" + message
                + "<form action=\"submitPasswordChange\" method=\"post\" >"
                + "<input type='hidden' name='language' value=\"" + language + "\" >"
                + "<table>"
                + "<tr><td align = \"right\">authCode:</td><td><input type=\"text\" size=\"80\" name=\"authCode\" value=\""
                + authCode + "\" ></td></tr>"
                + "<tr><td align = \"right\">new password:</td><td><input type=\"password\" size=\"80\" name=\"newPassword\" value=\"\" ></td></tr>"
                + "<tr><td/><td align =\"right\"><input type=submit value=\"" + TextMapper.getText(TextMapper.BTN_OK) + "\"></td></tr>"
                + "</table>"
                + "</form>";
        r += "</BODY></HTML>";
        TextMapper.setLanguage(old);
        return r;
    }

    /**
     * Registers a new user.
     *
     * @param authCode
     * @param newPassword
     * @param language
     * @return
     * @throws java.lang.Exception
     */
    @POST
    @Produces({"text/html"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/submitPasswordChange")
    public String submitPasswordChange(
            @FormParam("authCode") String authCode,
            @FormParam("newPassword") String newPassword,
            @FormParam("language") String language
    ) throws Exception {
        if (!ValidUserFieldsChecker.isValidPassword(newPassword)) {
            //throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Password_Invalid, "The password has illegal characters or length.");
            DwoLocale locale = new DwoLocale(language);
            return passwordChangeForm(authCode, language,
                    Dwo2ExceptionTranslator.getLocalizedCodeExplanation(locale, Dwo2ExceptionCode.Rest_Registration_Password_Invalid));
        }

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
            } catch (Exception e) {
                //illegal code
            }
            timeslot = timeslot - 1;
        }
        if (data.startsWith("dwoAuthCode:")) {
            PersistentUser user = UserManager.findByUserName(data.split(":")[1]);

            user.setPassword(MD5.getHashString(newPassword));
            UserManager.edit(user);
            LOG.log(Level.INFO, "Updated password of user with username {0} of timeslot {1}  from valid authCode.", new Object[]{user.getUsername(), timeslot});
            LOG.log(Level.FINER, "Updated password of user with username {0} of timeslot {1} using authcode {2}.", new Object[]{user.getUsername(), timeslot, authCode});
            newPassword = TextMapper.getText(TextMapper.DLG_CONFIRM);
        } else {
            newPassword = TextMapper.getText(TextMapper.LBL_ILLEGAL_AUTHCODE);
        }
        //Always wait 30 seconds before response.        
        sleep(3000);//10 timesshorter for debugging
        return newPassword;

    }
    
    /**
     * RFC 6749 authorisation, see paragraph <b>4.3.2.  Access Token Request</b>
     * @param username a user
     * @param password a password
     * @param type "password"
     * @param scope dont care
     * @return an access_token, or null 
     */
    @POST
    @Produces({"application/json"})
    @Consumes({"application/x-www-form-urlencoded"})
    @Path("/token")
    public DomToken getAuthToken(
    		@FormParam("username") String username, 
    		@FormParam("password") String password,  
    		@FormParam("grant_type") String type, 
    		@FormParam("scope") String scope) 
    		throws WebApplicationException // TODO uitzoeken hoe je dat goed doet
    {
    	if(!"password".equals(type))
    	{
    		throw new WebApplicationException();
    	}
    	password = MD5.getHashString(password);
    	PersistentUser u = UserManager.login(username, password);
    	if(u == null)
    	{
    		throw new WebApplicationException();
    	}
    	DomToken token = new DomToken();
    	token.setToken_type(DomToken.APARAM);
    	token.setExpires_in(300); // seconds
    	String auth_token;
    	auth_token = "1\f" + System.currentTimeMillis() + "\f" + username + "\f" + password;
    	byte[] auth_token_bytes = auth_token.getBytes(StandardCharsets.UTF_8);
		auth_token = Base64.getUrlEncoder().encodeToString(auth_token_bytes);
    	token.setAccess_token(auth_token);
    	return token;
    }
}
