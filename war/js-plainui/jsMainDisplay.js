function JsMainDisplay() {
    var visible = true;

    this.showMenu = function () {
        visible = true;
        console.log("show menu");
    };
    this.hideMenu = function () {
        visible = false;
        console.log("hide menu");
    };
    this.showLoginView = function () {
        console.log("show login view");
    };
    this.showWelcomeView = function () {
        console.log("show login view");
    };
    this.showAccountView = function () {
        console.log("show account get");
    };
    this.setSchoolName = function (name) {
        console.log("set name" + name);
    };
    this.setUserRole = function (role) {
        console.log("set role " + role);
    };
    this.setPresentationName = function (presentationName) {
        console.log("set presentationName " + presentationName);
    };
    this.isMenuVisible = function () {
        console.log("menu " + visible);
    };
}
var dwo = new JsMainDisplay();
console.log("constructed JsMainDisplay");
