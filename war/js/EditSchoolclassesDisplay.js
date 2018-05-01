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
	
	// jQuery objects
	this.$panel = jQuery("#editSchoolclassesDisplayPanel");
	
	this.$editSchoolclassForm = $(this.editSchoolclassForm);	
	this.$changeStudentsForm = $(this.changeStudentsForm);
	this.$changeTeachersForm = $(this.changeTeachersForm);
	this.$changeModulesForm = $(this.changeModulesForm);
	
	this.$editSchoolclassFormSaveButton = $(this.editSchoolclassFormSaveButton);
	this.$editSchoolclassFormDeleteButton = $(this.editSchoolclassFormDeleteButton);
	
	this.$changeStudentsFormShowButton = $(this.changeStudentsFormShowButton);
	this.$changeStudentsFormConnectButton = $(this.changeStudentsFormConnectButton);
	this.$changeStudentsFormCopyOrMoveButton = $(this.changeStudentsFormCopyOrMoveButton);
	
	this.$changeStudentsRow = this.$changeStudentsForm.find("tbody tr").detach();
	this.$changeStudentsTableBody = this.$changeStudentsForm.find("tbody");
		
	// Bind handlers
	this.$editSchoolclassForm.on('submit', $.proxy(this.submitEditSchoolclass, this));
	this.$editSchoolclassFormSaveButton.on('click', $.proxy(this.clickEditSchoolclassFormSaveButton, this));
	this.$editSchoolclassFormDeleteButton.on('click', $.proxy(this.clickEditSchoolclassFormDeleteButton, this));
	
	this.$changeStudentsForm.on('submit', $.proxy(this.submitChangeStudentsForm, this));
	this.$changeStudentsFormShowButton.on('click', $.proxy(this.clickChangeStudentsFormShowButton, this));
	this.$changeStudentsFormConnectButton.on('click', $.proxy(this.clickChangeStudentsFormConnectButton, this));
	this.$changeStudentsFormCopyOrMoveButton.on('click', $.proxy(this.clickChangeStudentsFormCopyOrMoveButton, this));
	
	
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
	console.log("init");
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
	
	//this.editSchoolclassForm.elements["useClasskey"].value ? true : false,
	
	this.editSchoolclassForm.elements["classkey"].value = schoolclass.registrationKey;
}

EditSchoolclassesDisplay.prototype.showStudents = function(json) {	
	var students = json.jsObject, studentName;
	
	this.$changeStudentsTableBody.html("");
	
	var i = 1;
	for (var id in students) { // TODO: probably change to array
		studentName = students[id].givenName + (students[id].insertion ? " "+students[id].insertion : "") + " " + students[id].familyName;
		console.log(el); console.log(id);
		$row = this.$changeStudentsRow.clone();
		// $row.prop('tabindex', i);
		// $row.find("#chooseStudentId").val( id ).removeAttr("id");
		$row.find("#chooseStudentName").html( studentName ).removeAttr("id");

		// $row.find("input[type='checkbox'],input[type='radio']").each( function() {
// 			this.value = id;
// 		});

		//$row.on('click keypress', $.proxy(this.clickChangeStudentRow, this));
		this.$changeStudentsTableBody.append($row);
		i++;
	}
	//this.chooseSchoolclassFormToggle(false);
}

EditSchoolclassesDisplay.prototype.showTeachers = function(json) {	
}

EditSchoolclassesDisplay.prototype.showModules = function(json) {	
}

/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */

EditSchoolclassesDisplay.prototype.saveSchoolclass = function() {
	app.getPresenterFactory().editSchoolclassPresenter.updateAndRefresh(this.editSchoolclassForm.elements["classname"].value,
																	this.editSchoolclassForm.elements["useClasstree"].value ? true : false,
																	this.editSchoolclassForm.elements["useClasskey"].value ? true : false,
																	this.editSchoolclassForm.elements["classkey"].value);
}

EditSchoolclassesDisplay.prototype.deleteSchoolclass = function() {
	app.getPresenterFactory().editSchoolclassPresenter.removeSchoolClass();
}

EditSchoolclassesDisplay.prototype.showStudentsRequest = function() {
	app.getPresenterFactory().editSchoolclassPresenter.showStudents();
}
EditSchoolclassesDisplay.prototype.connectStudents = function() {
	app.getPresenterFactory().editSchoolclassPresenter.connectStudents();
}
EditSchoolclassesDisplay.prototype.copyOrMoveStudents = function() {
	app.getPresenterFactory().editSchoolclassPresenter.copyOrMoveStudents();	
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
