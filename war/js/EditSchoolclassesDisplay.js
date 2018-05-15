function EditSchoolclassesDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.editSchoolclassForm = document.forms["editSchoolclass"];
	this.changeStudentsForm = document.forms["changeStudents"];
	this.changeTeachersForm = document.forms["changeTeachers"];
	this.changeModulesForm = document.forms["changeModules"];
	
	// Buttons 
	this.editSchoolclassFormSaveButton = this.editSchoolclassForm.elements["save"];
	this.editSchoolclassFormDeleteButton = this.editSchoolclassForm.elements["delete"];
	
	this.changeStudentsFormShowButton = this.changeStudentsForm.elements["show"];
	this.changeStudentsFormConnectButton = this.changeStudentsForm.elements["connect"];
	this.changeStudentsFormCopyOrMoveButton = this.changeStudentsForm.elements["copyOrMove"];
	
	this.changeTeachersFormShowButton = this.changeTeachersForm.elements["show"];
	this.changeTeachersFormConnectButton = this.changeTeachersForm.elements["connect"];
	
	this.changeModulesFormShowButton = this.changeModulesForm.elements["show"];
	this.changeModulesFormConnectButton = this.changeModulesForm.elements["connect"];

	
	// jQuery objects
	this.$panel = jQuery("#editSchoolclassesDisplayPanel");
	
	// Edit form elements
	this.$editSchoolclassForm = $(this.editSchoolclassForm);	
	this.$changeStudentsForm = $(this.changeStudentsForm);
	this.$changeTeachersForm = $(this.changeTeachersForm);
	this.$changeModulesForm = $(this.changeModulesForm);	
	this.$editSchoolclassFormSaveButton = $(this.editSchoolclassFormSaveButton);
	this.$editSchoolclassFormDeleteButton = $(this.editSchoolclassFormDeleteButton);
	
	// Student box elements
	this.$changeStudentsFormShowButton = $(this.changeStudentsFormShowButton);
	this.$changeStudentsFormConnectButton = $(this.changeStudentsFormConnectButton);
	this.$changeStudentsFormCopyOrMoveButton = $(this.changeStudentsFormCopyOrMoveButton);	
	this.$changeStudentsRow = this.$changeStudentsForm.find("tbody tr").detach();
	this.$changeStudentsTableBody = this.$changeStudentsForm.find("tbody");	
	
	// Teacher box elements
	this.$changeTeachersFormShowButton = $(this.changeTeachersFormShowButton);
	this.$changeTeachersFormConnectButton = $(this.changeTeachersFormConnectButton);	
	this.$changeTeachersRow = this.$changeTeachersForm.find("tbody tr").detach();
	this.$changeTeachersTableBody = this.$changeTeachersForm.find("tbody");
	
	// Modules box elements
	this.$changeModulesFormShowButton = $(this.changeModulesFormShowButton);
	this.$changeModulesFormConnectButton = $(this.changeModulesFormConnectButton);	
	this.$changeModulesRow = this.$changeModulesForm.find("tbody tr").detach();
	this.$changeModulesTableBody = this.$changeModulesForm.find("tbody");
		
	// Bind handlers
	this.$editSchoolclassForm.on('submit', $.proxy(this.submitEditSchoolclass, this));
	this.$editSchoolclassFormSaveButton.on('click', $.proxy(this.clickEditSchoolclassFormSaveButton, this));
	this.$editSchoolclassFormDeleteButton.on('click', $.proxy(this.clickEditSchoolclassFormDeleteButton, this));
	
	this.$changeStudentsForm.on('submit', $.proxy(this.submitChangeStudentsForm, this));
	this.$changeStudentsFormShowButton.on('click', $.proxy(this.clickChangeStudentsFormShowButton, this));
	this.$changeStudentsFormConnectButton.on('click', $.proxy(this.clickChangeStudentsFormConnectButton, this));
	this.$changeStudentsFormCopyOrMoveButton.on('click', $.proxy(this.clickChangeStudentsFormCopyOrMoveButton, this));
	
	this.$changeTeachersForm.on('submit', $.proxy(this.submitChangeTeachersForm, this));
	this.$changeTeachersFormShowButton.on('click', $.proxy(this.clickChangeTeachersFormShowButton, this));
	this.$changeTeachersFormConnectButton.on('click', $.proxy(this.clickChangeTeachersFormConnectButton, this));
	
	this.$changeModulesForm.on('submit', $.proxy(this.submitChangeModulesForm, this));
	this.$changeModulesFormShowButton.on('click', $.proxy(this.clickChangeModulesFormShowButton, this));
	this.$changeModulesFormConnectButton.on('click', $.proxy(this.clickChangeModulesFormConnectButton, this));
	
	// Init
	this.$panel.hide();
}

EditSchoolclassesDisplay.prototype.show = function() {
	this.$panel.show();
}

/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

EditSchoolclassesDisplay.prototype.clear = function () {
}

EditSchoolclassesDisplay.prototype.init = function () {
	this.$changeStudentsTableBody.html("");
	this.$changeTeachersTableBody.html("");
	this.$changeModulesTableBody.html("");
}

EditSchoolclassesDisplay.prototype.showSchoolClass = function(json) {	
	//console.log(json);
	var schoolclass = json.jsObject;
	
	this.editSchoolclassForm.elements["classname"].value = schoolclass.schoolClassName;

	if (schoolclass.hasRegKey == true) {
		this.editSchoolclassForm.elements["useClasskey"][0].checked = true;
		this.editSchoolclassForm.elements["useClasskey"][1].checked = false;
	} else {
		this.editSchoolclassForm.elements["useClasskey"][0].checked = false;
		this.editSchoolclassForm.elements["useClasskey"][1].checked = true;
	} 
	
	if (schoolclass.iconizer == true) {
		this.editSchoolclassForm.elements["useClasstree"][0].checked = true;
		this.editSchoolclassForm.elements["useClasstree"][1].checked = false;
	} else {
		this.editSchoolclassForm.elements["useClasstree"][0].checked = false;
		this.editSchoolclassForm.elements["useClasstree"][1].checked = true;
	} 
		
	this.editSchoolclassForm.elements["classkey"].value = schoolclass.registrationKey;
}

EditSchoolclassesDisplay.prototype.showStudents = function(json) {	
	var students = json.jsObject, studentName;
	
	this.$changeStudentsTableBody.html("");
	
	// No Results
	if ($.isEmptyObject(students)) {
		$row = this.$changeStudentsRow.clone();
		$row.find("#chooseStudentName").html( "Geen leerlingen in deze klas" ).removeAttr("id");
		this.$changeStudentsTableBody.append($row);
		return;
	}
	
	// > 0 results
	var i = 1;
	for (var id in students) { // TODO: probably change to array
		studentName = students[id].givenName + (students[id].insertion ? " "+students[id].insertion : "") + " " + students[id].familyName;
		$row = this.$changeStudentsRow.clone();
		$row.find("#chooseStudentName").html( studentName ).removeAttr("id");
		this.$changeStudentsTableBody.append($row);
		i++;
	}
}

EditSchoolclassesDisplay.prototype.showTeachers = function(json) {	
	var teachers = json.jsObject, teacherName;
	
	this.$changeTeachersTableBody.html("");
	
	// No Results
	if ($.isEmptyObject(teachers)) {
		$row = this.$changeTeachersRow.clone();
		$row.find("#chooseTeachersName").html( "Geen docenten gekopped" ).removeAttr("id");
		this.$changeTeachersTableBody.append($row);
		return;
	}
	
	var i = 1;
	for (var id in teachers) { // TODO: probably change to array
		teacherName = teachers[id].givenName + (teachers[id].insertion ? " "+teachers[id].insertion : "") + " " + teachers[id].familyName;
		$row = this.$changeTeachersRow.clone();
		$row.find("#chooseTeacherName").html( teacherName ).removeAttr("id");
		this.$changeTeachersTableBody.append($row);
		i++;
	}
}

EditSchoolclassesDisplay.prototype.showShowModels = function(json) { // TODO: change function name @Gert
	var modules = json.jsObject, moduleName;
	this.$changeModulesTableBody.html("");
	
	// No Results
	if ($.isEmptyObject(modules)) {
		$row = this.$changeModulesRow.clone();
		$row.find("#chooseModulesName").html( "Geen modules gekoppeld" ).removeAttr("id");
		this.$changeModulesTableBody.append($row);
		return;
	}
	
	// Temporary forward to next view
	//app.mainDisplay.showEditCoursesOfSchoolClassView();
	//window.JsModulesOfSchoolclassDisplay.updateTable(json);
	
	var i = 1;
	for (var id in modules) { // TODO: probably change to array
		moduleName = modules[id].name;
		$row = this.$changeModulesRow.clone();
		$row.find("#chooseModuleName").html( moduleName ).removeAttr("id");
		this.$changeModulesTableBody.append($row);
		i++;
	}	
}

/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */

// Edit form 
EditSchoolclassesDisplay.prototype.saveSchoolclass = function() {
	app.getPresenterFactory().editSchoolclassPresenter.updateAndRefresh(this.editSchoolclassForm.elements["classname"].value,
																	this.editSchoolclassForm.elements["useClasstree"].value == 1 ? true : false,
	this.editSchoolclassForm.elements["useClasskey"].value == 1 ? true : false, // TODO: doesnt work
																	this.editSchoolclassForm.elements["classkey"].value);
}
EditSchoolclassesDisplay.prototype.deleteSchoolclass = function() {
	app.getPresenterFactory().editSchoolclassPresenter.removeSchoolClass();
}

// Students
EditSchoolclassesDisplay.prototype.showStudentsRequest = function() {
	app.getPresenterFactory().editSchoolclassPresenter.showStudents();
}
EditSchoolclassesDisplay.prototype.connectStudents = function() {
	app.getPresenterFactory().editSchoolclassPresenter.connectStudents();
}
EditSchoolclassesDisplay.prototype.copyOrMoveStudents = function() {
	app.getPresenterFactory().editSchoolclassPresenter.copyOrMoveStudents();	
}

// Teachers
EditSchoolclassesDisplay.prototype.showTeachersRequest = function() {
	app.getPresenterFactory().editSchoolclassPresenter.showTeachers();
}
EditSchoolclassesDisplay.prototype.connectTeachers = function() {
	app.getPresenterFactory().editSchoolclassPresenter.connectTeachers();
}

// Modules
EditSchoolclassesDisplay.prototype.showModulesRequest = function() {
	app.getPresenterFactory().editSchoolclassPresenter.showModules();
}
EditSchoolclassesDisplay.prototype.connectModules = function() {
	app.getPresenterFactory().editSchoolclassPresenter.editModules();
}


/*
 * EVENT HANDLERS - Edit Schoolclass
 */
EditSchoolclassesDisplay.prototype.submitEditSchoolclass = function(event) {
	event.preventDefault();	
	console.log("SAVE!");
	this.saveSchoolclass();	
}
EditSchoolclassesDisplay.prototype.clickEditSchoolclassFormSaveButton = function(event) {
	event.preventDefault();		
	console.log("SAVE!");
	this.saveSchoolclass();
}
EditSchoolclassesDisplay.prototype.clickEditSchoolclassFormDeleteButton = function(event) {
	event.preventDefault();		
	console.log("DELETE!");
	this.deleteSchoolclass();
}

/*
 * EVENT HANDLERS - Students
 */
EditSchoolclassesDisplay.prototype.submitChangeStudentsForm = function(event) {
	event.preventDefault();		
}
EditSchoolclassesDisplay.prototype.clickChangeStudentsFormShowButton = function(event) {
	event.preventDefault();		
	console.log("Show students!");
	this.showStudentsRequest();
}
EditSchoolclassesDisplay.prototype.clickChangeStudentsFormConnectButton = function(event) {
	event.preventDefault();		
	console.log("Connect students!");
	this.connectStudents();
}
EditSchoolclassesDisplay.prototype.clickChangeStudentsFormCopyOrMoveButton = function(event) {
	event.preventDefault();		
	console.log("Copy or move students!");
	this.copyOrMoveStudents();
}


/*
 * EVENT HANDLERS - Teachers
 */
EditSchoolclassesDisplay.prototype.submitChangeTeachersForm = function(event) {
	event.preventDefault();		
}
EditSchoolclassesDisplay.prototype.clickChangeTeachersFormShowButton = function(event) {
	event.preventDefault();		
	console.log("Show teachers!");
	this.showTeachersRequest();
}
EditSchoolclassesDisplay.prototype.clickChangeTeachersFormConnectButton = function(event) {
	event.preventDefault();		
	console.log("Connect teachers!");
	this.connectTeachers();
}

/*
 * EVENT HANDLERS - Modules
 */
EditSchoolclassesDisplay.prototype.submitChangeModulesForm = function(event) {
	event.preventDefault();		
}
EditSchoolclassesDisplay.prototype.clickChangeModulesFormShowButton = function(event) {
	event.preventDefault();		
	console.log("Show modules!");
	this.showModulesRequest();
}
EditSchoolclassesDisplay.prototype.clickChangeModulesFormConnectButton = function(event) {
	event.preventDefault();		
	console.log("Connect modules!");
	this.connectModules();
}

