var schoolclasses;
function SchoolClassesDisplay() {

    this.init = function () {
        console.log("initialized schoolClasses list");
    };

    this.clear = function () {
        console.log("cleared schoolClasses list");
    };

    this.updateView = function (data) {
        schoolclasses = data;
        console.log("updating schoolClasses list");
        console.log(data);
        var keys = data.keys();
        for(var key in keys) {
            console.log("key: "+key+", value: "+data[key]);            
        }
        console.log("updated schoolClasses list");
    };
}
var jsSchoolClassesDisplay = new SchoolClassesDisplay();
console.log("constructed jsSchoolClassesDisplay");


