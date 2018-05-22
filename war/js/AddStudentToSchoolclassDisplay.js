function AddStudentToSchoolclassDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.addStudentSearchForm = document.forms["addStudentSearch"];
	this.addStudentAddForm = document.forms["addStudentAdd"];
	
	// Buttons 
	
	// jQuery objects
	this.$panel = jQuery("#addStudentToSchoolclassDisplay");
	
	this.$addStudentSearchForm = $(this.addStudentSearchForm);
	this.$addStudentAddForm = $(this.addStudentAddForm);
	
	this.$addStudentRow = this.$addStudentAddForm.find("tbody tr").detach();
	this.$addStudentTableBody = this.$addStudentAddForm.find("tbody");
		
	// Bind handlers
	this.$addStudentAddForm.on('submit', $.proxy(this.submitAddStudentAddForm, this));
	this.$addStudentSearchForm.on('submit', $.proxy(this.submitAddStudentSearchForm, this));
	
	// Init
	this.$panel.hide();
}

AddStudentToSchoolclassDisplay.prototype.show = function() {
	this.$panel.show();
}

/*
 * GUI FUNCTIONS
 */

AddStudentToSchoolclassDisplay.prototype.searchStudent = function() {
	var addStudentSearchForm = this.addStudentSearchForm;
	
	if ( addStudentSearchForm.elements["userName"].value == "" &&
		 addStudentSearchForm.elements["givenName"].value == "" &&
		 addStudentSearchForm.elements["insertion"].value == "" &&
		 addStudentSearchForm.elements["familyName"].value == "" &&
		 addStudentSearchForm.elements["email"].value == "" ) {
			$result = this.$addStudentTableBody.find("tr");
	} else {	
		var $result = this.$addStudentTableBody.find("td span").filter(function() {
			el = $(this).get(0);
						
			if (el.parentElement.cellIndex == 0) val = addStudentSearchForm.elements["userName"].value;
			if (el.parentElement.cellIndex == 1) val = addStudentSearchForm.elements["givenName"].value;
			if (el.parentElement.cellIndex == 2) val = addStudentSearchForm.elements["insertion"].value;
			if (el.parentElement.cellIndex == 3) val = addStudentSearchForm.elements["familyName"].value;
			if (el.parentElement.cellIndex == 4) val = addStudentSearchForm.elements["email"].value;
			
			return el.innerHTML.toLowerCase() == val.toLowerCase();
		}).closest("tr");
	}
	
	this.$addStudentTableBody.find("tr").hide()
	$result.show();
}


/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

AddStudentToSchoolclassDisplay.prototype.clear = function () {
	console.log("clear");
}

AddStudentToSchoolclassDisplay.prototype.init = function () {
	console.log("init");
	Helpers.stretchHeight([ this.$addStudentTableBody ]);
}

AddStudentToSchoolclassDisplay.prototype.setSchoolClass = function(schoolclass) {
	console.log("setSchoolClass: "+schoolclass);
}

AddStudentToSchoolclassDisplay.prototype.showStudents = function(json) {
	console.log(">>> show students");
	console.log(json);
	
	var students = json, studentName;
	
	this.$addStudentTableBody.html("");
	
	// No Results
	if ($.isEmptyObject(students)) {
		$row = this.$changeStudentsRow.clone();
		$row.find("#addStudentAddUserName").html( "Geen leerlingen gekoppeld" ).removeAttr("id");
		this.$addStudentsTableBody.append($row);
		return;
	}
	
	var i = 1;
	for (var id in students) { // TODO: probably change to array
		$row = this.$addStudentRow.clone();		
		$row.find("#addStudentAddId").val( id ).removeAttr("id");
		$row.find("#addStudentAddUserName").html( students[id].userName ).removeAttr("id");
		$row.find("#addStudentAddGivenName").html( students[id].givenName ).removeAttr("id");
		$row.find("#addStudentAddInsertion").html( students[id].insertion ).removeAttr("id");
		$row.find("#addStudentAddFamilyName").html( students[id].familyName ).removeAttr("id");
		$row.find("#addStudentAddEmail").html( "niet in json" ).removeAttr("id");	// TODO: email in JSON
		 
		$row.find("input[type='checkbox'],input[type='radio']").each( function() {
			this.value = id;
		});
		
		$row.on('click keypress', $.proxy(this.clickAddTeacherAddRow, this));
		this.$addStudentsTableBody.append($row);
		i++;
	}
	this.addStudentAddFormToggle(false);
}

AddStudentToSchoolclassDisplay.prototype.setEmptyTableMessage = function() {
	console.log("setEmptyTableMessage");
}

AddStudentToSchoolclassDisplay.prototype.setLoadingTableMessage = function() {
	console.log("setLoadingTableMessage");
}


/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */
AddStudentToSchoolclassDisplay.prototype.addStudent = function(id) {
	app.getPresenterFactory().getAddStudentToSchoolclassPresenter().AddStudentToSchoolClass(id);
}

/*
 * EVENT HANDLERS - add
 */

AddStudentToSchoolclassDisplay.prototype.submitAddStudentAddForm = function(event) {
	event.preventDefault();	
	this.addTeacher(this.addStudentAddForm.elements["id"].value);
}
AddStudentToSchoolclassDisplay.prototype.clickAddTeacherAddRow = function(event) {
	Helpers.selectTableRow(event);
	console.log(this.addStudentAddForm.elements["id"].value);
	if (this.addStudentAddForm.elements["id"].value != "") this.addStudentAddFormToggle(true);
	else this.addStudentAddFormToggle(false);	
}

// helpers
AddStudentToSchoolclassDisplay.prototype.addStudentAddFormToggle = function(value) {
	if (value) this.$addStudentAddForm.find(':submit').prop('disabled','');
	else this.$addStudentAddForm.find(':submit').prop('disabled','disabled');
}


/*
 * EVENT HANDLERS - search
 */

AddStudentToSchoolclassDisplay.prototype.submitAddStudentSearchForm = function(event) {
	event.preventDefault();	
	this.searchStudent();
}
