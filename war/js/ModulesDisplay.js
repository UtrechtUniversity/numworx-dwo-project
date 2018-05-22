function ModulesDisplay() {	
	// jQuery objects
	this.$panel = jQuery("#modulesDisplayPanel");
	this.$iframe = this.$panel.find("iframe");
	
	// Init
	this.$panel.hide();
}

ModulesDisplay.prototype.show = function() {
	this.$panel.show();
	Helpers.stretchHeight( [ this.$iframe ] );
	$(window).on('resize', $.proxy(Helpers.resizeHelpSection, this));
}


/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

ModulesDisplay.prototype.clear = function () {
	this.$iframe.attr('src', '' );
}
ModulesDisplay.prototype.openUrl = function (url) {
	this.$iframe.attr('src', url );
}

/*
 * EVENT HANDLERS
 */

ModulesDisplay.prototype.resizeIframe = function(e) {
	Helpers.stretchHeight( [ this.$iframe ] );
}