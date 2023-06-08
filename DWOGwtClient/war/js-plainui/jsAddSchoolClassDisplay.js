function AddSchoolClassDisplay() {
    var schoolClassName = "";
    var treeStructure = "";
    var useClassKey = "";
    var classKeyValue = "";

    this.clear = function () {
        schoolClassName = "";
        treeStructure = "";
        useClassKey = "";
        classKeyValue = "";
        console.log("cleared addSchoolClass fields");
    };

    this.init = function () {
        this.clear();
        console.log("intialized account display, cleared password.");
    };    
    
}
var jsAddSchoolClassDisplay = new AddSchoolClassDisplay();
console.log("constructed jsAddSchoolClassDisplay");

