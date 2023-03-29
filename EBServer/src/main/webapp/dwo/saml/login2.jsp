<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/dwo/tablet/scripts/SCORM_12_APIWrapper.js"></script>
<title>Please login</title>
</head>
<body>
<script type="text/javascript">

function later() {
	try {
		doLMSInitialize();
        if (window.location.search) {
            var args = new URLSearchParams(window.location.search);
            var code = args.get("code");
            if (code) {
				doLMSSetValue("dme.oauth.code", code);
				doLMSFinish("");
				return;
            }
        }
   	    var authorizeEndpoint = doLMSGetValue("dme.oauth.endpoint");
   	    
   	    var clientId = doLMSGetValue("dme.oauth.client_id");
       	var codeChallenge = doLMSGetValue("dme.oauth.code_challenge");
           var redirectUri = window.location.href.split('?')[0];
           var args = new URLSearchParams({
               response_type: "code",
               client_id: clientId,
               code_challenge_method: "S256",
               code_challenge: codeChallenge,
               redirect_uri: redirectUri
           });
           window.location = authorizeEndpoint + "?" + args;
         	
 	} catch(e) {
 		console.log("Error in login");
		alert("Error in login: " + e);
	}
}
setTimeout("later()",1)
</script>
</body>
</html>