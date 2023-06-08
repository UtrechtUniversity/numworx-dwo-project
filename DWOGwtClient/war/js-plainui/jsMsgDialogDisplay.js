function MsgDialogDisplay() {
 
    this.clear = function () {
        console.log("cleared MsgDialogDisplay dialog text");
    };

    this.init = function () {
        console.log("intialized msg of MsgDialogDisplay");
    };
 
    this.showDialog = function (msg) {
        console.log("showing MsgDialogDisplay with text: "+msg);
    };

    this.hideDialog = function () {
        console.log("hiding MsgDialogDisplay");
    };
}
var jsMsgDialogDisplay = new MsgDialogDisplay();
console.log("constructed jsMsgDialogDisplay");


