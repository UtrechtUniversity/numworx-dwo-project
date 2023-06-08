function ClickedDialogDisplay() {
 
    this.clear = function () {
        console.log("cleared ClickedDialogDisplay dialog text");
    };

    this.init = function () {
        console.log("intialized msg of ClickedDialogDisplay");
    };
 
    this.showDialog = function (msg) {
        console.log("showing ClickedDialogDisplay with text: "+text);
    };

    this.hideDialog = function () {
        console.log("hiding ClickedDialogDisplay");
    };
}
var jsClickedDialogDisplay = new ClickedDialogDisplay();
console.log("constructed jsClickedDialogDisplay");


