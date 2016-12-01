<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * script en css loader
 */

var deploy = "//cdn.dwo.nl/apps/"
//deploy = "//test-dwo-nl.s3.amazonaws.com/apps/"	
function script(name) {
	var elem = document.createElement('script');
	elem.src = deploy + name;
	elem.async = false;
	document.head.appendChild(elem);
}

function css(name) {
	var elem = document.createElement('link');
	elem.type='text/css';
	elem.rel = 'stylesheet';
	elem.href = deploy + name;
	document.head.appendChild(elem);
}