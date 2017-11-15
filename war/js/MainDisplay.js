function MainDisplay() {
	// Bind DOM elements with jQuery
	this.$panel = jQuery("#mainPanel");
	this.$panels = this.$panel.find(".panel");	
	this.$nav = this.$panel.find("nav");
	this.$presentationName = jQuery("#presentationName");
	this.$schoolName = jQuery("#schoolName");
	
	// Setup Display objects
	this.loginDisplay = new LoginDisplay();
	window.jsLoginDisplay = this.loginDisplay;		
	this.welcomeDisplay = new WelcomeDisplay();
	window.jsWelcomeDisplay = this.welcomeDisplay;
	
	// Init
	this.showLoginView();
}

MainDisplay.prototype.showLoginView = function() {
	this.$panel.hide();
	this.loginDisplay.show();
}

MainDisplay.prototype.initMainView = function() {	
	this.$panels.hide();
	this.$panel.show();	
	this.initNav();	
}

MainDisplay.prototype.initNav = function() {
	this.$nav.width(app.NAV_WIDTH);
	this.$panels.width(window.innerWidth - app.NAV_WIDTH);
	this.$panels.css('left', app.NAV_WIDTH);
}

/*
 * SET MAIN DISPLAY VARIABLES
 */

MainDisplay.prototype.setSchoolName = function (schoolName) {
	this.$schoolName.html(schoolName);
};
MainDisplay.prototype.setUserRole = function (role) {
    console.log("set role " + role);
};
MainDisplay.prototype.setPresentationName = function (presentationName) {
	this.$presentationName.html(presentationName);
};

/*
 * VIEW FUNCTIONS
 */

MainDisplay.prototype.showWelcomeView = function() {
	this.initMainView();
	this.welcomeDisplay.show();
}