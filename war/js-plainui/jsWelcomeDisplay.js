var jsWelcomeDisplay;
function WelcomeDisplay() {
    var text = "default..";

    this.clear = function () {
        text = "";
        console.log("cleared welcome text");
    };

    this.setWelcomeText = function (html){
        text = html;
        console.log("Welcome text set to: "+text);
    }
}
jsWelcomeDisplay = new WelcomeDisplay();
console.log("constructed jsWelcomeDisplay");


