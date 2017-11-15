function WelcomeDisplay() {
	
	// Setup properties
	this.$panel = jQuery("#welcomeDisplayPanel");
	
	// Init
	this.$panel.hide();
}

WelcomeDisplay.prototype.show = function() {
	this.$panel.show();
}

WelcomeDisplay.prototype.clear = function() {
	this.$panel.find(".content").html("");
}

WelcomeDisplay.prototype.setWelcomeText = function(html) {
	this.$panel.find(".content").html(html);
}