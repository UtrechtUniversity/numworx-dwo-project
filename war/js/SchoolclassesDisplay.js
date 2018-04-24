function SchoolclassesDisplay() {
	
	// GWT vars
	
	
	// Forms 
	this.chooseSchoolclass = document.forms["chooseSchoolclass"];
	
	// jQuery objects
	this.$panel = jQuery("#schoolclassesDisplayPanel");
	
	this.$chooseSchoolclass = $(this.chooseSchoolclass);
	
	
	// Bind handlers
	this.$chooseSchoolclass.on('submit', $.proxy(this.submitChooseSchoolclass,this));	
	
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
	console.log(json);
}

SchoolclassesDisplay.prototype.setEmptyTableMessage = function(json) {
}
SchoolclassesDisplay.prototype.setLoadingTableMessage = function(json) {
}

/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */


/*
 * EVENT HANDLERS - CHOOSE SCHOOLCLASS
 */

AccountDisplay.prototype.submitChooseSchoolclass = function(event) {
	event.preventDefault();		
	console.log("edit schoolclasses");
}


