<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ page import="fi.dwoapp.Version" %>
<%@ page import="java.util.Random" %>
<%!
	private Random rnd = new Random();

	private long timestamp() {
		return System.currentTimeMillis()/1000L;
	}

	private String nonce() {
		long l = rnd.nextLong() >>> 1; // 63 bits = 21 digits
		return Long.toOctalString(l);
	}
%>
<%
	String locale = request.getParameter("locale");
	if(locale == null) locale = "en";
%>
<meta charset="UTF-8">
<title>MC Squared Widget</title>
<script type="text/javascript" src="scripts/sha1.js" ></script>
<script type="text/javascript">

var $wnd = window;

function $(id) {
	return document.getElementById(id)
}
// To be more stringent in adhering to RFC 3986, more reserved characters:
function fixedEncodeURIComponent (str) {
	  return encodeURIComponent(str).replace(/[!'()*]/g, function(c) {
	    return '%' + c.charCodeAt(0).toString(16);
	  });
	}
function signature() {
	var url = $('lti').action;
// strip #
	var k = url.indexOf('#');
	if(k >= 0) url = url.substring(0,k);

	url = encodeURIComponent(url);
	var pfx="POST&" + url + "&";
	var text = ""
	var secret = "secret&";
	var inputs = document.getElementsByTagName("input");
	var b = false;
	var i;
	var name;
	for( i = 1; i < inputs.length; i++) {
		input = inputs[i]
		if(input.name == 'oauth_signature')
			continue;
		name = encodeURIComponent(input.name);
		value = fixedEncodeURIComponent(input.value);
		if(b) text += "&"
		b = true;
		text += name + "=" + value 
	}
	text = pfx + encodeURIComponent(text)
	n = b64_hmac_sha1(secret, text);
	$('oauth_signature').value = n;
}

window.inner = {
		getScore: function() {
				return 0;
		},
		getState: function() {
			return "{}";
		},
		setState: function(jso) {			
		},
		isCorrect: function() {
			return "null";
		},
		init: function(width, height, launchdata, randomvalues, action) {
			launch = JSON.parse(launchdata);
			className = launch.className;
			var ref = window.location.hash
			$('lti').action = action + ref;

			background = $wnd.getBackground($wnd.outer)
			document.body.style.backgroundColor = background
			uuid = $wnd.getUUID($wnd.outer)
			context = {
					assessmentMode: $wnd.getMode($wnd.outer),
					lessonMode: $wnd.getLessonMode($wnd.outer),
					background: background,
					//font: "",
					scoreMax: launch.scoreMax,
					logOption: launch.logOption,
					logID: launch.logID,
					UUID: uuid
			}
			launchdata = JSON.stringify(launch.launchData)	
			context = JSON.stringify(context)
			subscriptions = launch.subscriptions || "{}"
			$("launch_presentation_height").value = height;
			$("launch_presentation_width").value = width;
			$("oauth_consumer_key").value = className;
			$('resource_link_id').value = uuid;
			$('user_id').value = $wnd.getLearnerId($wnd.outer);
			learnername = $wnd.getLearnerName($wnd.outer); // FIXME Last, Given -> Given Last
			$('lis_person_name_full').value = learnername
			$('roles').value = $wnd.getRole($wnd.outer);
			
			$("custom_launch_data").value = launchdata
			$("custom_randomvalues").value = randomvalues
			$("custom_context").value = context
			$("custom_subscriptions").value = subscriptions
			signature()
			$('lti').submit()
		},
		enter: function() {
		},
		clearAll: function() {
		},
		insert: function(string) {
		},
		cursorToRight: function() {
		},
		cursorToLeft: function() {
		},
		getSelectionString: function() {
			return "";
		},
		removeNextElement: function() {
		},
		backspace: function() {
		},
		
}
</script>
</head>
<body>
<%
	String version = Version.VERSION;
	int dot = version.indexOf('.');
	version = version.substring(0,dot);
%>
<form id='lti' method="POST" action='http://ws.fisme.science.uu.nl/DWOmAccess/lti/widget.jsp' >
<input type='hidden' name='custom_context' id='custom_context' value='{}'>
<input type='hidden' name='custom_launch_data' id='custom_launch_data' value='{}'>
<input type='hidden' name='custom_randomvalues' id='custom_randomvalues' value='{}'>
<input type='hidden' name='custom_subscriptions' id='custom_subscriptions' value='{}'>
<input type='hidden' name='launch_presentation_height' id='launch_presentation_height' value='200'>
<input type='hidden' name='launch_presentation_locale' value='<%=locale%>'>
<input type='hidden' name='launch_presentation_width' id='launch_presentation_width' value='200'>

<input type='hidden' name='lis_person_name_full' id='lis_person_name_full' value='Anonymous Guest' >
<input name='lti_message_type' value='basic-lti-launch-request' type='hidden'>
<input name='lti_version' value='LTI-1p0' type='hidden'>

<input type='hidden' name='oauth_consumer_key' id='oauth_consumer_key' value='12345' >
<input name='oauth_nonce' value='<%=nonce() %>' type='hidden'>
<input name='oauth_signature_method' value='HMAC-SHA1' type='hidden'>
<input name='oauth_timestamp' value='<%=timestamp() %>' type='hidden'>
<input name='oauth_version' value='1.0'  type='hidden'>

<input type='hidden' name='resource_link_id' id='resource_link_id' value='000000-0-000000' >
<input type='hidden' name='roles' value='Learner' id='roles' >
<input type='hidden' name='tool_consumer_info_version' value='<%=version %>' >
<input type='hidden' name='user_id' id='user_id' value='guest' >
<input type='hidden' name='oauth_signature' id='oauth_signature' value='?' >
</form>


<div id='stub'></div>
</body>
</html>