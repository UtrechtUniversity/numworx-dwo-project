function JsProgressDialogDisplay() {
 
    this.clear = function () {
        console.log("cleared MsgDialogDisplay dialog text");
    };

    this.init = function () {
        console.log("intialized msg of MsgDialogDisplay");
    };
 
    this.showDialog = function (msg) {
        console.log("showing MsgDialogDisplay with main text: "+msg);
    };

    this.hideDialog = function () {
        console.log("hiding MsgDialogDisplay");
    };

    this.updateDialog = function (progress, actMsg) {
        console.log("Progress is "+progress+"% working on "+actMsg);
    };
}
var JsProgressDialogDisplay = new JsProgressDialogDisplay();
console.log("constructed JsProgressDialogDisplay");


