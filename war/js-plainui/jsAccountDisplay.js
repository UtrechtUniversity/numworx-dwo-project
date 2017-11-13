function AccountDisplay() {
    var username = "";
    var firstName = "";
    var insertion = "";
    var familyName = "";
    var email = "";
    var password = "";
    var newPassword = "";
    var newPasswordAgain = "";

    this.clear = function () {
        username = "";
        firstName = "";
        insertion = "";
        familyName = "";
        password = "";
        email = "";
        console.log("cleared account fields");
    }

    this.init = function () {
        password = "";
        console.log("intialized account display, cleared password.");
    }
    this.updateView = function (u, f, i, n, e) {
        username = u;
        firstName = f;
        insertion = i;
        familyName = n;
        email = e;
        console.log("updated username: " + u + ", firstname:" + f + ", insertion:" + i + ", familyname:" + n + ", email: " + e + ".");
    }
    this.showState = function (){
        console.log("state-info| username: " + u + ", firstname:" + f + ", insertion:" + i + ", familyname:" + n + ", email: " + e + ".");
    }
    
}
var jsAccountDisplay = new AccountDisplay();
console.log("constructed jsAccountDisplay");

