function ModulesOfSchoolclassDisplay() {	
	// GWT vars
	
	
	// Forms 
	
	// Buttons 
	
	// jQuery objects
	this.$panel = jQuery("#modulesOfSchoolclassDisplay");
		
	// Bind handlers
	
	// Init
	this.$panel.hide();
}

ModulesOfSchoolclassDisplay.prototype.show = function() {
	this.$panel.show();
}

/*
 * GUI FUNCTIONS
 */



/*
 * VIEW FUNCTIONS
 * Map to java implementation
 */

ModulesOfSchoolclassDisplay.prototype.clear = function () {
	console.log("clear");
}

ModulesOfSchoolclassDisplay.prototype.init = function () {
	console.log("init");
}


ModulesOfSchoolclassDisplay.prototype.updateTable = function(json) {
	console.log("UPDATE!");
	console.log(json);	
}



/*
 * RETURN FUNCTIONS
 * Use java callbacks
 */


/*
 * EVENT HANDLERS
 */
