/*
 * Tried to follow Google's Javascript Styleguide
 * https://google.github.io/styleguide/javascriptguide.xml?showone=Nested_functions#Nested_functions
 *
 * Variables starting with a $ tells you they are jQuery objects.
 */

function MainApp() {
	this.mainDisplay = new MainDisplay();
	window.jsMainDisplay = this.mainDisplay; // make it available for API
	
	this.NAV_WIDTH = 200;
}

MainApp.prototype.getPresenterFactory = function() {
	if (this.presenterFactory) return this.presenterFactory;
	this.presenterFactory = window.dwoAPI.DwoPresenterFactory.getDwoPresenterFactory();
	return this.presenterFactory = this.presenterFactory.getFac();
}

$(document).ready(function(){ 
	window.app = new MainApp();
});
	