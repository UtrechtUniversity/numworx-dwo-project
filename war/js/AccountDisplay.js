function AccountDisplay() {
	
	// GWT vars
	this.email = "";
	this.familyName = "";
	this.givenName = "";
	this.insertion = "";
	this.password = "";
	this.newPassword = "";
	this.activeSchoolRoleAndClass = null,
	this.schoolsRolesAndClassesList = null;
	
	// Forms 
	this.updateUserForm = document.forms["updateUser"];
	this.updateSchoolLoginsViewForm = document.forms["updateSchoolLoginsView"];
	this.addSchoolLoginForm = document.forms["addSchoolLogin"];	
	
	// jQuery objects
	this.$panel = jQuery("#accountDisplayPanel");
	this.$schoolLoginsRow = $(this.updateSchoolLoginsViewForm).find("tbody tr").detach();
	this.$schoolLoginsTableBody = $(this.updateSchoolLoginsViewForm).find("tbody");
	
	// Bind handlers
	//this.form.on('submit', $.proxy(this.login,this));
	$(this.updateUserForm).on('submit', $.proxy(this.saveUser,this));
	$(this.updateSchoolLoginsViewForm).on('submit', $.proxy(this.saveSchoolLogins,this));
	$(this.addSchoolLoginForm).on('submit', $.proxy(this.addSchoolLogin,this));
	
	// Init
	this.$panel.hide();
}

AccountDisplay.prototype.show = function() {
	this.$panel.show();
}

AccountDisplay.prototype.clear = function () {
}

AccountDisplay.prototype.init = function (json) {
}

AccountDisplay.prototype.refresh = function() {
	// Refresh update user
	this.updateUserForm.elements["email"].value = this.email;
	this.updateUserForm.elements["familyName"].value = this.familyName;
	this.updateUserForm.elements["givenName"].value = this.givenName;
	this.updateUserForm.elements["insertion"].value = this.insertion;
	
	// Refresh update school logins
	if (this.schoolsRolesAndClassesList) {
		
		if (this.activeSchoolRoleAndClass) { activeSchoolId = this.activeSchoolRoleAndClass.school.id.idString; activeRoleId = this.activeSchoolRoleAndClass.role.id.idString; }
		else { activeSchoolId = ""; activeRoleId = ""; }
		
		for (i = 0; i < this.schoolsRolesAndClassesList.length; i++) {
			el = this.schoolsRolesAndClassesList[i];
			$row = this.$schoolLoginsRow.clone();
			$row.find("#updateSchoolLoginsViewSchool").html( el.school.schoolName ).removeAttr("id");
			$row.find("#updateSchoolLoginsViewRole").html( el.role.roleName ).removeAttr("id");	
			
			$row.find("input[type='checkbox'],input[type='radio']").each( function() {
				this.value = el.role.id.idString;
				
				// Change ID and label for-attributes
				this.id = this.id + i;				
				oldFor = this.nextElementSibling.getAttribute("for");
				this.nextElementSibling.setAttribute("for", oldFor + i);
			});
			
			if (activeSchoolId.localeCompare(el.school.id.idString) == 0
				&& activeRoleId.localeCompare(el.role.id.idString) == 0) {
				$row.find("input[type='radio']").prop('checked','checked');
			}
						
			this.$schoolLoginsTableBody.append($row);	
		}
	}
	
}

AccountDisplay.prototype.updateUserView = function(json) {
	this.email = json.jsObject.email;
	this.familyName = json.jsObject.familyName;
	this.givenName = json.jsObject.givenName;
	this.insertion = json.jsObject.insertion;
	this.refresh();
}
AccountDisplay.prototype.updateSchoolLoginsView = function(json) {
	console.log("update school logins view");
	console.log(json);	
	this.activeSchoolRoleAndClass = json.jsObject.activeSchoolRoleAndClass;
	this.schoolsRolesAndClassesList = json.jsObject.schoolsRolesAndClassesList;
	this.refresh();	
}

AccountDisplay.prototype.saveUser = function(event) {
	//String givenName, String insertion, String familyName, String email, String curPassword, String newPassword, String newPasswordAgain
	event.preventDefault();		
	app.getPresenterFactory().accountPresenter.saveUser(    this.updateUserForm.elements["givenName"].value,
															this.updateUserForm.elements["insertion"].value,
															this.updateUserForm.elements["familyName"].value,
															this.updateUserForm.elements["email"].value,
															this.updateUserForm.elements["currentPassword"].value,
															this.updateUserForm.elements["newPassword"].value,
															this.updateUserForm.elements["newPasswordAgain"].value);
}

AccountDisplay.prototype.saveSchoolLogins = function(event) {
	event.preventDefault();		
	app.getPresenterFactory().accountPresenter.switchSchoolLogin( this.updateSchoolLoginsViewForm.elements["active"].value );
}

AccountDisplay.prototype.addSchoolLogin = function(event) {
	//String role, String schoolLogin, String accessCode
	event.preventDefault();		
	app.getPresenterFactory().accountPresenter.addASchoolLogin( this.addSchoolLoginForm.elements["role"].value,
																this.addSchoolLoginForm.elements["schoolLogin"].value,
																this.addSchoolLoginForm.elements["accessCode"].value );
}


//window.jsAccountDisplay = new AccountDisplay();
