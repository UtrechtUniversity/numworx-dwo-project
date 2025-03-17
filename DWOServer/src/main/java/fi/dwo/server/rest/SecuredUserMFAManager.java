package fi.dwo.server.rest;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentMFA;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.MFAManager;
import fi.dwo.server.rest.util.Origin;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/user/mfa")
public class SecuredUserMFAManager {
	public static final String MFA_RIGHT = "2";
	
	static public class MFA {
		public String issuer;
		public String secret = "BP26TDZUZ5SVPZJRIHCAUVREO5EWMHHV";
		public List<String>recovery = Arrays.asList("tf8i-exmo-3lcb-slkm", "boyv-yq75-z99k-r308", "w045-mq6w-mg1i-q12o");
		public String qr;
	}

	@PUT
	@Produces({"application/json"})
	@Path("create") 
	public MFA create(@Context SecurityContext sc, RestContext rest, @Context HttpServletRequest req) throws QrGenerationException, Dwo2Exception {
		UserState_U state = AnonDomainAuthorizer.build().submitUser(sc);	
		UserState_HR_R_S_SG_U hrstate = state.setHasRole(rest.getRestContext().getDomHasRole());
		PersistentUser u = hrstate.getUser();
		PersistentHasRole hr = hrstate.getHasRole();
		String right = hr.getRights();
		if (right.contains(MFA_RIGHT)) return null; // No option
		if (hrstate.getRoleType() == RoleType.STUDENT) return null; // Error
		PersistentMFA pmfa = MFAManager.findEntity(u.getId());
	    MFA mfa = new MFA();
		if (pmfa == null) {
		    pmfa = new PersistentMFA();
		    SecretGenerator secretGenerator = new DefaultSecretGenerator();
		    mfa.secret = secretGenerator.generate();
		    RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
		    mfa.recovery = Arrays.asList(recoveryCodes.generateCodes(5));
		    pmfa.setSecret(mfa.secret);
		    pmfa.setRecovery(mfa.recovery);
		    pmfa.setUserID(u.getId());
		    MFAManager.create(pmfa);
		} else {
			mfa.secret = pmfa.getSecret();
			mfa.recovery = pmfa.getRecovery();			
		}
		hr.setRights(right + MFA_RIGHT);
		HasRoleManager.editRights(hr);
		mfa.issuer = u.getUsername();
	    Response r = qrcode(req, mfa);
	    mfa.qr = Utils.getDataUriForImage((byte[])r.getEntity(), r.getMediaType().toString());
		return mfa;
	}
	
	@GET
    @Produces({"application/json"})
    @Path("/verify") 
    public boolean verify(@Context SecurityContext sc, @QueryParam("mfa") String code) {
    	if (code == null) return false;
    	MFA data = new MFA(); // fake....
    	if (data.recovery.contains(code)) {
    		data.recovery.remove(code);
    		return true;
    	}
    	TimeProvider timeProvider = new SystemTimeProvider();
    	CodeGenerator codeGenerator = new DefaultCodeGenerator();
    	CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    	// secret = the shared secret for the user
    	// code = the code submitted by the user
    	boolean successful = verifier.isValidCode(data.secret, code);  	
    	return successful;
    }
    
    @GET
    @Path("/qr") 
    public Response qr(@Context SecurityContext sc, @Context HttpServletRequest req) throws QrGenerationException {
    	MFA mfa = new MFA();
    	Principal p = sc.getUserPrincipal();
    	mfa.issuer = p == null ? "Numworx" : p.getName();
    	return qrcode(req, mfa);
    }

	Response qrcode(HttpServletRequest req, MFA mfa) throws QrGenerationException {
		String origin = Origin.of(req);
		String name = mfa.issuer;
    	QrData data = new QrData.Builder()
    			   .label(origin)
    			   .secret(mfa.secret)
    			   .issuer(name)
    			   .build();
    	QrGenerator generator = new ZxingPngQrGenerator();
    	byte[] imageData = generator.generate(data);
    	String mimeType = generator.getImageMimeType();
    	return Response.ok(imageData, mimeType)
    			.type(mimeType)
    			.build();
	}

}
