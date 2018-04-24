function SchoolclassesDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.chooseSchoolclassForm = document.forms["chooseSchoolclass"];
	
	// jQuery objects
	this.$panel = jQuery("#schoolclassesDisplayPanel");
	
	this.$chooseSchoolclassForm = $(this.chooseSchoolclassForm);
	this.$chooseSchoolclassRow = this.$chooseSchoolclassForm.find("tbody tr").detach();
	this.$chooseSchoolclassTableBody = this.$chooseSchoolclassForm.find("tbody");
	
	
	// Bind handlers
	this.$chooseSchoolclassForm.on('submit', $.proxy(this.submitChooseSchoolclass,this));	
	
	// Init
	this.$panel.hide();
}

/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

SchoolclassesDisplay.prototype.show = function() {
	console.log("show schoolclasses view");
	this.$panel.show();
}

SchoolclassesDisplay.prototype.clear = function () {
}

SchoolclassesDisplay.prototype.init = function () {
}

SchoolclassesDisplay.prototype.updateView = function(json) {
	var schoolclasses = json;
	
	this.$chooseSchoolclassTableBody.html("");
	
	//for (i = 0; i < this.schoolclasses.length; i++) {
	for (var id in schoolclasses) {
		el = schoolclasses[id];
		console.log(el); console.log(id);
		$row = this.$chooseSchoolclassRow.clone();
		$row.find("#chooseSchoolclassId").val( id ).removeAttr("id");
		$row.find("#chooseSchoolclassName").html( el ).removeAttr("id");

		$row.find("input[type='checkbox'],input[type='radio']").each( function() {
			this.value = id;
		});

		$row.on('click', $.proxy(this.clickChooseSchoolclassRow, this));
		this.$chooseSchoolclassTableBody.append($row);
		
	}
	this.chooseSchoolclassFormToggle(false);
}

SchoolclassesDisplay.prototype.setEmptyTableMessage = function(json) {
}
SchoolclassesDisplay.prototype.setLoadingTableMessage = function(json) {
}

/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */

SchoolclassesDisplay.prototype.chooseClass = function(id) {
	app.getPresenterFactory().schoolclassesPresenter.editSchoolClass(id);
}


/*
 * EVENT HANDLERS - CHOOSE SCHOOLCLASS
 */

SchoolclassesDisplay.prototype.submitChooseSchoolclass = function(event) {
	event.preventDefault();	
	
	this.chooseClass(this.chooseSchoolclassForm.elements["schoolclass"].value);
}
SchoolclassesDisplay.prototype.clickChooseSchoolclassRow = function(event) {
	Helpers.selectTableRow(event);
	if (this.chooseSchoolclassForm.elements["schoolclass"].value != "") this.chooseSchoolclassFormToggle(true);
	else this.chooseSchoolclassFormToggle(false);	
}

// helpers
SchoolclassesDisplay.prototype.chooseSchoolclassFormToggle = function(value) {
	if (value) this.$chooseSchoolclassForm.find(':submit').prop('disabled','');
	else this.$chooseSchoolclassForm.find(':submit').prop('disabled','disabled');
}


