/**
 * cdplogica script en css loader
 */

var cdplogica = "{14}"
var casServer = "https://app.dwo.nl/ideas/IdeasServlet"
	
function script(name) '{'
	var elem = document.createElement("script");
	elem.src = cdplogica + name;
	elem.async = false;
	document.head.appendChild(elem);
'}'

function css(name) '{'
	var elem = document.createElement("link");
	elem.type="text/css";
	elem.rel = "stylesheet";
	elem.href = cdplogica + name;
	document.head.appendChild(elem);
'}'
