function LoginDisplay() {
	
	// Setup properties
	this.panel = jQuery("#loginDisplayPanel");
	this.form = jQuery("#formLogin");
	this.usernameField = this.form.find('input[name="username"]');
	this.passwordField = this.form.find('input[name="password"]');

	// Bind handlers
	this.form.on('submit', $.proxy(this.login,this));
	
	// Init
	this.panel.hide();
}

LoginDisplay.prototype.show = function() {
	this.panel.show();
}
LoginDisplay.prototype.disable = function() {
	this.usernameField.val("");
	this.passwordField.val("")
	this.panel.hide();
}

LoginDisplay.prototype.login = function(event) {
	event.preventDefault();
	
	var username = this.usernameField.val(), password = this.passwordField.val();
	
	this.disable();
	
	app.getPresenterFactory().loginPresenter.loginClicked(username,password,false);
};


