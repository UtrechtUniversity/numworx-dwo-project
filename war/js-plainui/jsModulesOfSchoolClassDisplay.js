var schoolclasses;
function ModulesOfSchoolClassDisplay() {

    this.init = function () {
        console.log("initialized ModulesOfSchoolClassDisplay list");
    };

    this.clear = function () {
        console.log("cleared ModulesOfSchoolClassDisplay list");
    };

    this.setEmptyTableMessageModules = function() {
        console.log("show empty module table message.");
    };
    
    this.setLoadingTableMessageModules = function() {
        console.log("show loading module table message.");
    };
    
    this.setEmptyTableMessageSelected = function() {
        console.log("show empty selected module table message.");
    };
    
    this.setLoadingTableMessageSelected = function() {
        console.log("show loading selected modules table message.");
    };

    this.updateTable = function (p){
        console.log("showing list of selected modules.");
        console.log(p);
    };
    
    this.setTree = function (p){
        console.log("showing tree of class modules .");
        console.log(p);
    };

}
var jsModulesOfSchoolClassDisplay = new ModulesOfSchoolClassDisplay();
console.log("constructed jsModulesOfSchoolClassDisplay");


