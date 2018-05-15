function AddTeacherToSchoolclassDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.addTeacherSearchForm = document.forms["addTeacherSearch"];
	this.addTeacherAddForm = document.forms["addTeacherAdd"];
	
	// Buttons 
	
	// jQuery objects
	this.$panel = jQuery("#addTeacherToSchoolclassDisplay");
	this.$addTeacherSearchForm = $(this.addTeacherSearchForm);
	this.$addTeacherAddForm = $(this.addTeacherAddForm);
	
	this.$addTeacherRow = this.$addTeacherAddForm.find("tbody tr").detach();
	this.$addTeacherTableBody = this.$addTeacherAddForm.find("tbody");
		
	// Bind handlers
	this.$addTeacherAddForm.on('submit', $.proxy(this.submitAddTeacherAddForm, this));
	this.$addTeacherSearchForm.on('submit', $.proxy(this.submitAddTeacherSearchForm, this));
	
	
	// Init
	this.$panel.hide();
}

AddTeacherToSchoolclassDisplay.prototype.show = function() {
	this.$panel.show();
}

/*
 * GUI FUNCTIONS
 */

AddTeacherToSchoolclassDisplay.prototype.searchTeacher = function() {
	var addTeacherSearchForm = this.addTeacherSearchForm;
	
	if ( addTeacherSearchForm.elements["userName"].value == "" &&
		 addTeacherSearchForm.elements["givenName"].value == "" &&
		 addTeacherSearchForm.elements["insertion"].value == "" &&
		 addTeacherSearchForm.elements["familyName"].value == "" &&
		 addTeacherSearchForm.elements["email"].value == "" ) {
			$result = this.$addTeacherTableBody.find("tr");
	} else {	
		var $result = this.$addTeacherTableBody.find("td span").filter(function() {
			el = $(this).get(0);
						
			if (el.parentElement.cellIndex == 0) val = addTeacherSearchForm.elements["userName"].value;
			if (el.parentElement.cellIndex == 1) val = addTeacherSearchForm.elements["givenName"].value;
			if (el.parentElement.cellIndex == 2) val = addTeacherSearchForm.elements["insertion"].value;
			if (el.parentElement.cellIndex == 3) val = addTeacherSearchForm.elements["familyName"].value;
			if (el.parentElement.cellIndex == 4) val = addTeacherSearchForm.elements["email"].value;
			
			return el.innerHTML.toLowerCase() == val.toLowerCase();
		}).closest("tr");
	}
	
	this.$addTeacherTableBody.find("tr").hide()
	$result.show();
}

/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

AddTeacherToSchoolclassDisplay.prototype.clear = function () {
	console.log("clear");
}

AddTeacherToSchoolclassDisplay.prototype.init = function () {
	console.log("init");
	Helpers.stretchHeight([ this.$addTeacherTableBody ]);
}

AddTeacherToSchoolclassDisplay.prototype.setSchoolClass = function(schoolclass) {
	console.log("setSchoolClass: "+schoolclass);
}

AddTeacherToSchoolclassDisplay.prototype.showTeachers = function(json) {
	console.log("showTeachers");
	console.log(json);
	
	var teachers = json, teacherName;
	
	this.$addTeacherTableBody.html("");
	
	// No Results
	if ($.isEmptyObject(teachers)) {
		$row = this.$changeTeachersRow.clone();
		$row.find("#addTeacherAddUserName").html( "Geen docenten gekoppeld" ).removeAttr("id");
		this.$addTeacherTableBody.append($row);
		return;
	}
	
	var i = 1;
	for (var id in teachers) { // TODO: probably change to array
		$row = this.$addTeacherRow.clone();		
		$row.find("#addTeacherAddId").val( id ).removeAttr("id");
		$row.find("#addTeacherAddUserName").html( teachers[id].userName ).removeAttr("id");
		$row.find("#addTeacherAddGivenName").html( teachers[id].givenName ).removeAttr("id");
		$row.find("#addTeacherAddInsertion").html( teachers[id].insertion ).removeAttr("id");
		$row.find("#addTeacherAddFamilyName").html( teachers[id].familyName ).removeAttr("id");
		$row.find("#addTeacherAddEmail").html( "niet in json" ).removeAttr("id");	// TODO: email in JSON
		 
		$row.find("input[type='checkbox'],input[type='radio']").each( function() {
			this.value = id;
		});
		
		$row.on('click keypress', $.proxy(this.clickAddTeacherAddRow, this));
		this.$addTeacherTableBody.append($row);
		i++;
	}
	this.addTeacherAddFormToggle(false);
}

AddTeacherToSchoolclassDisplay.prototype.setEmptyTableMessage = function() {
	console.log("setEmptyTableMessage");
}

AddTeacherToSchoolclassDisplay.prototype.setLoadingTableMessage = function() {
	console.log("setLoadingTableMessage");
}


/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */
AddTeacherToSchoolclassDisplay.prototype.addTeacher = function(id) {
	app.getPresenterFactory().getAddTeacherToSchoolclassPresenter().AddTeacherToSchoolClass(id);
}


/*
 * EVENT HANDLERS - add
 */

AddTeacherToSchoolclassDisplay.prototype.submitAddTeacherAddForm = function(event) {
	event.preventDefault();	
	this.addTeacher(this.addTeacherAddForm.elements["id"].value);
}
AddTeacherToSchoolclassDisplay.prototype.clickAddTeacherAddRow = function(event) {
	Helpers.selectTableRow(event);
	console.log(this.addTeacherAddForm.elements["id"].value);
	if (this.addTeacherAddForm.elements["id"].value != "") this.addTeacherAddFormToggle(true);
	else this.addTeacherAddFormToggle(false);	
}

// helpers
AddTeacherToSchoolclassDisplay.prototype.addTeacherAddFormToggle = function(value) {
	if (value) this.$addTeacherAddForm.find(':submit').prop('disabled','');
	else this.$addTeacherAddForm.find(':submit').prop('disabled','disabled');
}


/*
 * EVENT HANDLERS - search
 */

AddTeacherToSchoolclassDisplay.prototype.submitAddTeacherSearchForm = function(event) {
	event.preventDefault();	
	this.searchTeacher();
}
