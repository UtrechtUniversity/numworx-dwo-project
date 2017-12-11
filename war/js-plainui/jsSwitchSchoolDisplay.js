var schoolclasses;
function SwitchSchoolDisplay() {

    this.init = function () {
        console.log("initialized schools for teacher list");
    };

    this.clear = function () {
        console.log("cleared schools list");
    };

    this.updateView = function (data) {
        schoolclasses = data;
        console.log("updating schools for teacher list");
        console.log(data);
        var keys = data.keys();
        for(var key in keys) {
            console.log("key: "+key+", value: "+data[key]);            
        }
        console.log("updated schools for teacher list");
    };
}
var jsSwitchSchoolDisplay = new SwitchSchoolDisplay();
console.log("constructed jsSwitchSchoolDisplay");


