function AccountDisplay() {
    var schoolLogins = "";

    this.clear = function () {
        schoolLogins = "";
        console.log("cleared schoolLogin info");
    }

    this.init = function () {
        password = "";
        console.log("intialized account display, cleared password.");
    }
    this.updateView = function (schoolLogins) {        
        console.log("showing schoolLogins:" + schoolLogins);
    }
    this.showState = function (){
        console.log("state-info| schoolLogins:" + schoolLogins);
    }
    
}
var jsAccountDisplay = new AccountDisplay();
console.log("constructed jsAccountDisplay");

