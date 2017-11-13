function LoginDisplay() {
    var username = "";
    var password = "";

    this.clear = function () {
        username = "";
        password = "";
        console.log("cleared username and password");
    };

    this.getUsername = function () {
        return username;
        console.log("username set to " + username);
    };
    this.setUsername = function (u) {
        username = u;
        console.log("username set to " + username);
    };
    this.setPassword = function (p) {
        password = p;
        console.log("password set to " + password);
    };
    this.getPassword = function () {
        return password;
        console.log("password set to " + password);
    };
//    this.callLoginClicked = function (user, password, switchRole){
//                console.log("call..todo. ");
//    }
}
var jsLoginDisplay = new LoginDisplay();
console.log("constructed jsLoginDisplay");


