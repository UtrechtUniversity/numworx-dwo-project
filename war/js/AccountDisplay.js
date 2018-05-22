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
	
	this.$updateUserForm = $(this.updateUserForm);
	this.$updateSchoolLoginsViewForm = $(this.updateSchoolLoginsViewForm);
	this.$addSchoolLoginForm = $(this.addSchoolLoginForm);
	
	// Bind handlers
	this.$updateUserForm.on('submit', $.proxy(this.submitUserForm,this));
	this.$updateSchoolLoginsViewForm.on('submit', $.proxy(this.submitSchoolLogins,this));
	this.$addSchoolLoginForm.on('submit', $.proxy(this.submitSchoolLoginsViewForm,this));
	$(this.updateUserForm.elements["currentPassword"]).on('keypress', $.proxy(this.changeCurrentPasswordInput,this));
	this.$addSchoolLoginForm.find("input:radio").on('change', $.proxy(this.addSchoolLoginFormToggle,this));
	
	// Init
	this.$panel.hide();
	this.addSchoolLoginFormToggle();
}

AccountDisplay.prototype.show = function() {
	this.$panel.show();
}


/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

AccountDisplay.prototype.clear = function () {
	console.log("clear");
}

AccountDisplay.prototype.init = function (json) {
	Helpers.stretchHeight( [ this.$schoolLoginsTableBody ] )
}

AccountDisplay.prototype.updateUserView = function(json) {
	this.email = json.jsObject.email;
	this.familyName = json.jsObject.familyName;
	this.givenName = json.jsObject.givenName;
	this.insertion = json.jsObject.insertion;

	this.updateUserForm.elements["email"].value = this.email;
	this.updateUserForm.elements["familyName"].value = this.familyName;
	this.updateUserForm.elements["givenName"].value = this.givenName;
	this.updateUserForm.elements["insertion"].value = this.insertion;
	this.updateUserFormToggle(false);
}

AccountDisplay.prototype.updateSchoolLoginsView = function(json) {
	this.activeSchoolRoleAndClass = json.jsObject.activeSchoolRoleAndClass;
	this.schoolsRolesAndClassesList = json.jsObject.schoolsRolesAndClassesList;

	if (this.schoolsRolesAndClassesList) {
		
		this.$schoolLoginsTableBody.html(""); // empty the table
		
		if (this.activeSchoolRoleAndClass) { activeSchoolId = this.activeSchoolRoleAndClass.school.id.idString; activeRoleId = this.activeSchoolRoleAndClass.role.id.idString; }
		else { activeSchoolId = ""; activeRoleId = ""; }
		
		for (i = 0; i < this.schoolsRolesAndClassesList.length; i++) {
			el = this.schoolsRolesAndClassesList[i];
			console.log("EL");
			console.log(el);
			$row = this.$schoolLoginsRow.clone();
			$row.find("#updateSchoolLoginsViewSchool").html( el.school.schoolName ).removeAttr("id");
			$row.find("#updateSchoolLoginsViewRole").html( el.role.roleName ).removeAttr("id");	
			
			$row.find("input[type='checkbox'],input[type='radio']").each( function() {
				this.value = el.hasRole.id.idString;
				
				// Change ID and label for-attributes
				this.id = this.id + i;				
				oldFor = this.nextElementSibling.getAttribute("for");
				this.nextElementSibling.setAttribute("for", oldFor + i);
			});
			
			
			// Set active 'active' checkbox and change style of the others
			if (activeSchoolId.localeCompare(el.school.id.idString) == 0
				&& activeRoleId.localeCompare(el.role.id.idString) == 0) {
				$row.find("input[name='active[]']").prop('checked','checked').prop('disabled','disabled').parent().addClass('ok');;
			} 
			
			$row.find("input[name='active[]']").on('change', $.proxy(this.changeActiveCheckbox,this));
			$row.find("input[name='remove[]']").on('change', $.proxy(this.changeRemoveCheckbox,this));
						
			this.$schoolLoginsTableBody.append($row);	
		}
		
		this.updateSchoolLoginsViewFormSubmitToggle();
	}
}

AccountDisplay.prototype.clearAddSchoolLogin = function() {
	this.addSchoolLoginForm.elements["role"][0].checked = false;
	this.addSchoolLoginForm.elements["role"][1].checked = false;
	this.addSchoolLoginForm.elements["role"][2].checked = false;
	this.addSchoolLoginForm.elements["schoolCode"].value = "";
	this.addSchoolLoginForm.elements["schoolLogin"].value = "";
}


/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */

AccountDisplay.prototype.saveUser = function(event) {	
	app.getPresenterFactory().getAccountPresenter().saveUser(    this.updateUserForm.elements["givenName"].value,
															this.updateUserForm.elements["insertion"].value,
															this.updateUserForm.elements["familyName"].value,
															this.updateUserForm.elements["email"].value,
															this.updateUserForm.elements["currentPassword"].value,
															this.updateUserForm.elements["newPassword"].value,
															this.updateUserForm.elements["newPasswordAgain"].value);
}

AccountDisplay.prototype.saveSchoolLogins = function(event) {
	var value = "";
	for (i = 0; i < this.updateSchoolLoginsViewForm.elements.length; i++) {
		if (this.updateSchoolLoginsViewForm.elements[i].name == "active[]" && this.updateSchoolLoginsViewForm.elements[i].checked && !this.updateSchoolLoginsViewForm.elements[i].disabled) value = this.updateSchoolLoginsViewForm.elements[i].value;
	}
	//console.log("set active: "+value);
	if (value != "") app.getPresenterFactory().getAccountPresenter().switchSchoolLogin( value );
	
	for (i = 0; i < this.updateSchoolLoginsViewForm.elements.length; i++) {
		if (this.updateSchoolLoginsViewForm.elements[i].name == "remove[]" && this.updateSchoolLoginsViewForm.elements[i].checked) {
			console.log("remove"+this.updateSchoolLoginsViewForm.elements[i].value);
			app.getPresenterFactory().getAccountPresenter().removeASchoolLogin(this.updateSchoolLoginsViewForm.elements[i].value);
		}
	}
	
	
}

AccountDisplay.prototype.addSchoolLogin = function(event) {
	console.log("add school login");
	app.getPresenterFactory().getAccountPresenter().addASchoolLogin( this.addSchoolLoginForm.elements["role"].value,
																this.addSchoolLoginForm.elements["schoolLogin"].value,
																this.addSchoolLoginForm.elements["schoolCode"].value );	
}


/*
 * EVENT HANDLERS - USER
 */

AccountDisplay.prototype.submitUserForm = function(event) {
	event.preventDefault();
	this.saveUser();		
}
AccountDisplay.prototype.changeCurrentPasswordInput = function(event) {
	if (event.target.value != "") this.updateUserFormToggle(true);
	else this.updateUserFormToggle(false);
}
// helpers
AccountDisplay.prototype.updateUserFormToggle = function(value) {
	if (value) this.$updateUserForm.find(':submit').prop('disabled','');
	else this.$updateUserForm.find(':submit').prop('disabled','disabled');
}


/*
 * EVENT HANDLERS - SCHOOL LOGINS
 */

AccountDisplay.prototype.submitSchoolLogins = function(event) {
	event.preventDefault();		
	this.saveSchoolLogins();
}

AccountDisplay.prototype.changeActiveCheckbox = function(event) {
	if (event.target.checked) {
		// Set others unchecked
		this.uncheckSchoolLoginsViewFormCheckboxes();
		
		// Set remove unchecked
		var $row = $(event.target.parentElement.parentElement.parentElement);
		$row.find("input[name='remove[]']").prop('checked','');
		
		// Set current checked
		event.target.checked = "checked";
	} else {
		event.target.checked = "";
	}
	this.updateSchoolLoginsViewFormSubmitToggle();
}

AccountDisplay.prototype.changeRemoveCheckbox = function(event) {
	if (event.target.checked) {
		// Set others unchecked
		this.uncheckSchoolLoginsViewFormCheckboxes();
		//var $row = $(event.target.parentElement.parentElement.parentElement);
		//$row.find("input[name='active[]']:not(:disabled)").prop('checked','');
		
		// Set current checked
		event.target.checked = "checked";
	} else {
		event.target.checked = "";
	}
	this.updateSchoolLoginsViewFormSubmitToggle();
}

// Helpers
AccountDisplay.prototype.updateSchoolLoginsViewFormSubmitToggle = function() {
	if (this.updateSchoolLoginsViewFormStateChanged()) this.$updateSchoolLoginsViewForm.find(':submit').prop('disabled','');
	else this.$updateSchoolLoginsViewForm.find(':submit').prop('disabled','disabled');
}
AccountDisplay.prototype.updateSchoolLoginsViewFormStateChanged = function () {
	for (i = 0; i < this.updateSchoolLoginsViewForm.elements.length; i++) {
		if (this.updateSchoolLoginsViewForm.elements[i].name == "active[]" && this.updateSchoolLoginsViewForm.elements[i].checked && !this.updateSchoolLoginsViewForm.elements[i].disabled) return true;
		if (this.updateSchoolLoginsViewForm.elements[i].name == "remove[]" && this.updateSchoolLoginsViewForm.elements[i].checked) return true;
	}
	return false;
}
AccountDisplay.prototype.uncheckSchoolLoginsViewFormCheckboxes = function() {
	for (i = 0; i < this.updateSchoolLoginsViewForm.elements.length; i++) {
		if ( (this.updateSchoolLoginsViewForm.elements[i].name == "active[]" || this.updateSchoolLoginsViewForm.elements[i].name == "remove[]")
			&& !this.updateSchoolLoginsViewForm.elements[i].disabled) this.updateSchoolLoginsViewForm.elements[i].checked = "";
	}
}


/*
 * EVENT HANDLERS - ADD SCHOOL LOGIN
 */

AccountDisplay.prototype.submitSchoolLoginsViewForm = function(event) {
	event.preventDefault();
	this.addSchoolLogin();		
}

//helpers 
AccountDisplay.prototype.addSchoolLoginFormToggle = function() {
	if (this.addSchoolLoginForm.elements["role"][0].checked ||
		this.addSchoolLoginForm.elements["role"][1].checked ||
		this.addSchoolLoginForm.elements["role"][2].checked ) this.$addSchoolLoginForm.find(':submit').prop('disabled','');
	else this.$addSchoolLoginForm.find(':submit').prop('disabled','disabled');
}



