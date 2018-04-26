function SchoolclassesDisplay() {	
	// GWT vars
	
	
	// Forms 
	this.chooseSchoolclassForm = document.forms["chooseSchoolclass"];
	this.addSchoolclassForm = document.forms["addSchoolclass"];
	
	// jQuery objects
	this.$panel = jQuery("#schoolclassesDisplayPanel");
	
	this.$chooseSchoolclassForm = $(this.chooseSchoolclassForm);
	this.$chooseSchoolclassRow = this.$chooseSchoolclassForm.find("tbody tr").detach();
	this.$chooseSchoolclassTableBody = this.$chooseSchoolclassForm.find("tbody");
	
	this.$addSchoolclassForm = $(this.addSchoolclassForm);
	
	
	// Bind handlers
	this.$chooseSchoolclassForm.on('submit', $.proxy(this.submitChooseSchoolclass,this));	
	this.$addSchoolclassForm.on('submit', $.proxy(this.submitAddSchoolclass,this));	
	
	// Init
	this.$panel.hide();
}

SchoolclassesDisplay.prototype.show = function() {
	this.$panel.show();
}


/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

SchoolclassesDisplay.prototype.clear = function () {
	console.log("CLEAR!"); // Wil jij clear aanroepen of ik?
}

SchoolclassesDisplay.prototype.init = function () {
}

SchoolclassesDisplay.prototype.updateView = function(json) {
	var schoolclasses = json;
	
	this.$chooseSchoolclassTableBody.html("");
	
	//for (i = 0; i < this.schoolclasses.length; i++) {
	var i = 1;
	for (var id in schoolclasses) { // TODO: probably change to array
		el = schoolclasses[id];
		console.log(el); console.log(id);
		$row = this.$chooseSchoolclassRow.clone();
		$row.prop('tabindex', i);
		$row.find("#chooseSchoolclassId").val( id ).removeAttr("id");
		$row.find("#chooseSchoolclassName").html( el ).removeAttr("id");

		$row.find("input[type='checkbox'],input[type='radio']").each( function() {
			this.value = id;
		});

		$row.on('click keypress', $.proxy(this.clickChooseSchoolclassRow, this));
		this.$chooseSchoolclassTableBody.append($row);
		i++;
	}
	this.chooseSchoolclassFormToggle(false);
}

SchoolclassesDisplay.prototype.setEmptyTableMessage = function(json) {
	console.log ("empty");
}
SchoolclassesDisplay.prototype.setLoadingTableMessage = function(json) {
	console.log ("loading");
}

/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */

SchoolclassesDisplay.prototype.chooseClass = function(id) {
	app.getPresenterFactory().schoolclassesPresenter.editSchoolClass(id);
}

SchoolclassesDisplay.prototype.addClass = function(id) {
	app.getPresenterFactory().schoolclassesPresenter.AddSchoolClass(this.addSchoolclassForm.elements["classname"].value,
																	this.addSchoolclassForm.elements["useClasstree"].value ? true : false,
																	this.addSchoolclassForm.elements["useClasskey"].value ? true : false,
																	this.addSchoolclassForm.elements["classkey"].value);
																	// TODO: function name start with capital?
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


/*
 * EVENT HANDLERS - ADD SCHOOLCLASS
 */

SchoolclassesDisplay.prototype.submitAddSchoolclass = function(event) {
	event.preventDefault();		
	this.addClass();
}


