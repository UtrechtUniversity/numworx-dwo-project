function AddSchoolClassDisplay() {

    this.clear = function () {
        console.log("cleared AddSchoolClassDisplay fields");
    };

    this.init = function () {
        this.clear();
        console.log("intialized AddSchoolClassDisplay.");
    };    
    
}
var jsAddSchoolClassDisplay = new AddSchoolClassDisplay();
console.log("constructed jsAddSchoolClassDisplay");

