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
    
    /*
     * 
     */
    this.showDialog = function (n, t, u, c) {
        schoolClassName = n;
        treeStructure = t;
        useClassKey = u;
        classKeyValue = c;
        console.log("updated schoolClassName to: " + n 
                + ", treeStructure to:" + t + ", useClassKey to:" + u + ", classKeyValue to:" + c + ".");
    };
    
    this.showState = function (){
        console.log("state schoolClassName: " + schoolClassName 
                + ", treeStructure:" + treeStructure
                + ", useClassKey:" + useClassKey 
                + ", classKeyValue:" + classKeyValue 
                + ".");
    };
    
}
var jsAddSchoolClassDisplay = new AddSchoolClassDisplay();
console.log("constructed jsAddSchoolClassDisplay");

