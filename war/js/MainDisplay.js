function MainDisplay() {
	// Bind DOM elements with jQuery
	this.$panel = jQuery("#mainPanel");
	this.$panels = this.$panel.find(".panel");	
	this.$nav = this.$panel.find("nav");

	this.$accountMenuSchoolName = jQuery("#accountMenuSchoolName");
	this.$accountMenuUserRole = jQuery("#accountMenuUserRole");
	this.$accountMenuPresentationName = jQuery("#accountMenuPresentationName");
	
	
	// Setup Display objects
	this.loginDisplay = new LoginDisplay();
	window.jsLoginDisplay = this.loginDisplay;		
	this.welcomeDisplay = new WelcomeDisplay();
	window.jsWelcomeDisplay = this.welcomeDisplay;
	this.accountDisplay = new AccountDisplay();
	window.jsAccountDisplay = this.accountDisplay;
	this.msgDialogDisplay = new MsgDialogDisplay();
	window.jsMsgDialogDisplay = this.msgDialogDisplay;
	this.messageDialogWithConfirmDisplay = new MessageDialogWithConfirmDisplay();
	window.jsMessageDialogWithConfirmDisplay = this.messageDialogWithConfirmDisplay;	
			
	// Init
	this.showLoginView(); // TODO: Gert moet de login view aanroepen nadat alles geinitialiseerd is.
}

MainDisplay.prototype.showLoginView = function() {
	this.$panel.hide();
	this.loginDisplay.show();
}

MainDisplay.prototype.initMainView = function() { // TODO:	remember state
	this.$panels.hide();
	this.$panel.show();	
	this.loginDisplay.hide();
}

MainDisplay.prototype.setActiveMenuItem = function() {
	// TODO: implementeren
}


/*
 * SET MAIN DISPLAY VARIABLES
 */

MainDisplay.prototype.setSchoolName = function (schoolName) {
	this.$accountMenuSchoolName.html(schoolName);
};
MainDisplay.prototype.setUserRole = function (role) {
    this.$accountMenuUserRole.html(role);
};
MainDisplay.prototype.setPresentationName = function (presentationName) {
	this.$accountMenuPresentationName.html(presentationName);
};


/*
 * VIEW FUNCTIONS
 */

MainDisplay.prototype.showWelcomeView = function() {
	console.log("show WelcomeView");
	this.initMainView();
	this.welcomeDisplay.show();
}

MainDisplay.prototype.showAccountView = function(vars) {
	this.initMainView(); 
	this.accountDisplay.show();
}