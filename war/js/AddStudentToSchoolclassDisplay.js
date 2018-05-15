function AddStudentToSchoolclassDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.addStudentAdd = document.forms["addStudentAdd"];
	
	// Buttons 
	
	// jQuery objects
	this.$panel = jQuery("#addStudentToSchoolclassDisplay");
	this.$addStudentAdd = $(this.addStudentAdd);
	
	this.$addStudentRow = this.$addStudentAdd.find("tbody tr").detach();
	this.$addStudentTableBody = this.$addStudentAdd.find("tbody");
		
	// Bind handlers
	
	
	// Init
	this.$panel.hide();
}

AddStudentToSchoolclassDisplay.prototype.show = function() {
	this.$panel.show();
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
}

AddStudentToSchoolclassDisplay.prototype.setSchoolClass = function(schoolclass) {
	console.log("setSchoolClass: "+schoolclass);
}

AddStudentToSchoolclassDisplay.prototype.showStudents = function(json) {
	console.log("showStudents");
	console.log(json);
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


/*
 * EVENT HANDLERS 
 */
