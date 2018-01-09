function ConfirmDialogDisplay() {
 
    this.clear = function () {
        console.log("cleared ConfirmDialogDisplay dialog text");
    };

    this.init = function () {
        console.log("intialized msg of ConfirmDialogDisplay");
    };
 
    this.showDialog = function (msg) {
        console.log("showing ConfirmDialogDisplay with text: "+text);
    };

    this.hideDialog = function () {
        console.log("hiding ConfirmDialogDisplay");
    };
}
var jsConfirmDialogDisplay = new ConfirmDialogDisplay();
console.log("constructed jsConfirmDialogDisplay");


