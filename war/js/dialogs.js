function MsgDialogDisplay() {
		
}
MsgDialogDisplay.prototype.clear = function() {
}
MsgDialogDisplay.prototype.init = function() {
}
MsgDialogDisplay.prototype.showDialog = function(text) {	
	console.log("MsgDialogDisplay: "+text);
}
MsgDialogDisplay.prototype.hideDialog = function(event) {
}


function MsgDialogWithConfirmDisplay() {
	this.active = false;
	
	// jQuery objects
	this.$dialog = $("#MsgDialogWithConfirmDisplay");
	this.$message = $("#MsgDialogWithConfirmDisplayMessage");
	this.$confirmButton = $("#MsgDialogWithConfirmDisplayConfirmButton");
	
	// Bind handlers
	this.$confirmButton.on('click', $.proxy(this.clickConfirm,this));
	
	// Init
	this.$dialog.hide();
	
}
MsgDialogWithConfirmDisplay.prototype.clear = function() {}
MsgDialogWithConfirmDisplay.prototype.init = function() {}
MsgDialogWithConfirmDisplay.prototype.showDialog = function(text) {	
	this.$dialog.show();
	this.$message.html(text);
	this.$confirmButton.focus();
	this.active = true;	
	window.app.mainDisplay.openDialogView(this);
}
MsgDialogWithConfirmDisplay.prototype.hideDialog = function() {
	this.$dialog.hide();	
	this.$message.html("");
	this.active = false;
	window.app.mainDisplay.closeDialogView(this);
}
MsgDialogWithConfirmDisplay.prototype.clickConfirm = function(event) {
	this.hideDialog();
}
MsgDialogWithConfirmDisplay.prototype.setFocus = function() {
	this.$confirmButton.focus();
}

function AlertDialogWithConfirmCancelDisplay() {
		
}
AlertDialogWithConfirmCancelDisplay.prototype.clear = function() {
}
AlertDialogWithConfirmCancelDisplay.prototype.init = function() {
}
AlertDialogWithConfirmCancelDisplay.prototype.showDialog = function(text) {	
	console.log("AlertDialogWithConfirmCancelDisplay: "+text);
}
AlertDialogWithConfirmCancelDisplay.prototype.hideDialog = function(event) {
}


function AlertDialogWithConfirmDisplay() {
	this.active = false;
	
	// jQuery objects
	this.$dialog = $("#AlertDialogWithConfirmDisplay");
	this.$message = $("#AlertDialogWithConfirmDisplayMessage");
	this.$confirmButton = $("#AlertDialogWithConfirmDisplayConfirmButton");
	
	// Bind handlers
	this.$confirmButton.on('click', $.proxy(this.clickConfirm,this));
	
	// Init
	this.$dialog.hide();
}
AlertDialogWithConfirmDisplay.prototype.clear = function() {}
AlertDialogWithConfirmDisplay.prototype.init = function() {}
AlertDialogWithConfirmDisplay.prototype.showDialog = function(text) {	
	this.$dialog.show();
	this.$message.html(text);
	this.$confirmButton.focus();
	this.active = true;
	window.app.mainDisplay.openDialogView(this);
}
AlertDialogWithConfirmDisplay.prototype.hideDialog = function(event) {
	this.$dialog.hide();	
	this.$message.html("");
	this.active = false;
	window.app.mainDisplay.closeDialogView(this);
}
AlertDialogWithConfirmDisplay.prototype.clickConfirm = function(event) {
	this.hideDialog();
}
AlertDialogWithConfirmDisplay.prototype.setFocus = function() {
	this.$confirmButton.focus();
}




function ProgressDialogWithAbortDisplay() {
		
}
ProgressDialogWithAbortDisplay.prototype.clear = function() {
}
ProgressDialogWithAbortDisplay.prototype.init = function() {
}
ProgressDialogWithAbortDisplay.prototype.showDialog = function() {	
	console.log("ProgressDialogWithAbortDisplay");
}
ProgressDialogWithAbortDisplay.prototype.hideDialog = function(event) {
}








