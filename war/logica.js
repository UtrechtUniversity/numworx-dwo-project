/**
 * cdplogica script en css loader
 */

var cdplogica = "http://cdplogica.toegang.nu/noordhoff/vo/fi/dwo/2014_v1_0/"
cdplogica = ""	
function script(name) {
	var elem = document.createElement('script');
	elem.src = cdplogica + name;
	elem.async = false;
	document.head.appendChild(elem);
}

function css(name) {
	var elem = document.createElement('link');
	elem.type='text/css';
	elem.rel = 'stylesheet';
	elem.href = cdplogica + name;
	document.head.appendChild(elem);
}