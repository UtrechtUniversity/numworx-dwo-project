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
        console.log("show welcome view");
    };
    this.showAccountView = function () {
        console.log("show account view");
    };
    this.showSchoolclassesView = function () {
        console.log("show school classes view");
    };
    this.showSwitchSchoolView = function () {
        console.log("show school classes view");
    };
    this.showResultsView = function () {
        console.log("show school classes view");
    };
    this.showCoursesOfSchoolclassView = function () {
        console.log("show school classes view");
    };
    this.showAddStudentsView = function () {
        console.log("show school classes view");
    };
    this.showTeachersInSchoolclassView = function () {
        console.log("show school classes view");
    };
    this.showScoResultsView = function () {
        console.log("show school classes view");
    };
    this.showStudentsInSchoolclassView = function () {
        console.log("show school classes view");
    };
    this.setCurrentPanelName = function () {
        console.log("set current module name");
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
var jsMainDisplay = new JsMainDisplay();
console.log("constructed JsMainDisplay");
