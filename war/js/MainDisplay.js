function MainDisplay() {
	this.activeDialogs = [];

	// Bind DOM elements with jQuery
	this.$body = $("body");
	this.$panel = jQuery("#mainPanel");
	this.$panels = this.$panel.find(".panel");	
	this.$subpanels = this.$panel.find(".subpanel");	
	this.$nav = this.$panel.find("nav");

	this.$accountMenuSchoolName = jQuery("#accountMenuSchoolName");
	this.$accountMenuUserRole = jQuery("#accountMenuUserRole");
	this.$accountMenuPresentationName = jQuery("#accountMenuPresentationName");
	this.$accountMenuBox = jQuery("#accountMenuBox");
	this.$accountMenuToggle = $("#accountMenuToggle");
	
	
	// Setup Display objects
	this.loginDisplay = new LoginDisplay();
	window.jsLoginDisplay = this.loginDisplay;		
	this.welcomeDisplay = new WelcomeDisplay();
	window.jsWelcomeDisplay = this.welcomeDisplay;
	this.accountDisplay = new AccountDisplay();
	window.jsAccountDisplay = this.accountDisplay;	
	this.schoolclassesDisplay = new SchoolclassesDisplay();
	window.jsSchoolClassesDisplay = this.schoolclassesDisplay;
	this.editSchoolclassesDisplay = new EditSchoolclassesDisplay();
	window.jsEditSchoolclassDisplay	= this.editSchoolclassesDisplay;
	
	this.msgDialogDisplay = new MsgDialogDisplay();
	window.jsMsgDialogDisplay = this.msgDialogDisplay;
	this.msgDialogWithConfirmDisplay = new MsgDialogWithConfirmDisplay();
	window.jsMessageDialogWithConfirmDisplay = this.msgDialogWithConfirmDisplay;		
	this.alertDialogWithConfirmCancelDisplay = new AlertDialogWithConfirmCancelDisplay();
	window.jsAlertDialogWithConfirmCancelDisplay = this.alertDialogWithConfirmCancelDisplay;	
	this.alertDialogWithConfirmDisplay = new AlertDialogWithConfirmDisplay();
	window.jsAlertDialogWithConfirmDisplay = this.alertDialogWithConfirmDisplay;	
	this.progressDialogWithAbortDisplay = new ProgressDialogWithAbortDisplay();
	window.jsProgressDialogWithAbortDisplay = this.progressDialogWithAbortDisplay;
	
	// Bind events
	this.$nav.find('a').on('click', $.proxy(this.clickMenuItem, this));
	this.$accountMenuBox.find('a').on('click', $.proxy(this.clickAccountMenuItem, this));
	this.$accountMenuToggle.on('click', $.proxy(this.clickAccountMenuToggle, this));
			
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
	this.$subpanels.hide();
	this.loginDisplay.hide();
}

MainDisplay.prototype.setActiveView = function(view) {
	if (view == "LOGOUT") location.reload(); // TODO: vervangen door echte logout functie
	app.getPresenterFactory().mainPresenter.selectView(view);
}



/*
 * SET MAIN DISPLAY VARIABLES
 * Maps to java implementation
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
 * Maps to java implementation
 */

MainDisplay.prototype.showWelcomeView = function() {
	this.initMainView();
	this.welcomeDisplay.show();
}

MainDisplay.prototype.showAccountView = function(vars) {
	this.initMainView(); 
	this.accountDisplay.show();
}

MainDisplay.prototype.showSchoolclassesView = function(vars) {
	this.initMainView(); 
	this.schoolclassesDisplay.show();
}

/*
 * DIALOG VIEW HELPERS
 */

MainDisplay.prototype.openDialogView = function(dialog) {
	this.activeDialogs.push(dialog);
	this.$body.addClass("overlay");
}
MainDisplay.prototype.closeDialogView = function(dialog) {
	dialog = this.activeDialogs.pop();
	if (this.activeDialogs.length == 0) this.$body.removeClass("overlay"); // remove overlay
	else this.activeDialogs[this.activeDialogs.length - 1].setFocus(); // or set focus to next dialog
}

/*
 * EVENT HANDLERS
 */

MainDisplay.prototype.clickMenuItem = function(event) {
	event.preventDefault();
	var view = event.currentTarget.hash.substr(1);
	if (view) this.setActiveView(view)
}
MainDisplay.prototype.clickAccountMenuItem = function(event) {
	event.preventDefault();
	var view = event.currentTarget.hash.substr(1);
	if (view) {
		this.$accountMenuBox.toggle();
		this.setActiveView(view);
	}
}
MainDisplay.prototype.clickAccountMenuToggle = function(event) {
	this.$accountMenuBox.toggle();
}

