<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="/dwo/tablet/scripts/SCORM_2004_APIWrapper.js"></script>
<title>Please login</title>
</head>
<body>
<script type="text/javascript">

function later() {
	try {
        if (window.location.search) {
            var args = new URLSearchParams(window.location.search);
            var code = args.get("code");
            if (code) {
              var r = parseInt(window.sessionStorage.getItem("r"));
          	  var state = args.get("state");
      		  if (r > 1024 && r <= 0xFFFF ) {
      				var location = "http://127.0.0.1:"+r+ "/local/Terminate"
      				var arg = JSON.stringify({'dme.oauth.code': code, 'dme.oauth.state': state, 'cmi.exit':'logout'});
      				arg = encodeURIComponent(arg);
      				window.location = location + "?q=" + arg
      		  } else {
      			if ("false" == doInitialize()) {
      				throw "Illegal use"
      			}
				doSetValue("dme.oauth.code",  code);
				doSetValue("dmw.oauth.state", state);
				doTerminate("");
              }
				return;
            } 
            var r = args.get("r") 
            if (r) {
            	window.sessionStorage.setItem("r", r);
            }
            var hint = args.get("idphint")
            if (hint) {
            	doSetValue("dme.oauth.idphint", hint);
            }
        }
		if ("false" == doInitialize()) {
			throw "Illegal use"
		}
   	    var authorizeEndpoint = doGetValue("dme.oauth.endpoint");
   	    
   	    var clientId = doGetValue("dme.oauth.client_id");
       	var codeChallenge = doGetValue("dme.oauth.code_challenge");
       	var state = doGetValue("dme.oauth.state")||"";
       	var idphint = doGetValue("dme.oauth.idphint")||"";
        var redirectUri = window.top.location.href.split('?')[0];
        var args = new URLSearchParams({
               response_type: "code",
               client_id: clientId,
               state: state,
               code_challenge_method: "S256",
               code_challenge: codeChallenge,
               redirect_uri: redirectUri,
               "idphint": idphint
           });
           window.open( authorizeEndpoint + "?" + args , "_top");
         	
 	} catch(e) {
 		console.error("Error in login");
		alert("Error in login: " + e);
	}
}
setTimeout("later()",1)
</script>
</body>
</html>