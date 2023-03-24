var schoolclasses;
function ResultsDisplay() {
    /**
     * Clears the results view.
     * 
     * @return {undefined}
     */
    this.clear = function () {
        console.log("cleared results view");
    };

    /**
     * Plots the results in the viewer. data contains a double array of strings with
     * the first row containing the column headers and the first column containing
     * the row headers.
     * 
     * @param {array of array} data
     * @param {boolean} zoomedClass
     * @param {boolean} zoomedCourse
     * @return {undefined}
     */
    this.plot = function (data, zoomedClass, zoomedCourse) {
        schoolclasses = data;
        console.log("Updating results view");
        console.log("ZoomedClass = " + zoomedClass + ", " + zoomedCourse);
        console.log(data);
        console.log("plotted results");
        for (var y in data) {
            for (var x in data[y]) {
                console.log("x: " + x + " y: " + y + ", value: " + data[y][x]);
            }
        }
    };

    /** Shows an empty result table.
     * 
     * @return {undefined}
     */
    this.setEmptyTableMessage = function () {
        console.log("result table is empty");
    }

    /**
     *  Shows that result data is being fetched..
     *  
     * @return {undefined}
     */
    this.setLoadingTableMessage = function () {
        console.log("result table is loading...");
    }

}
var jsResultsDisplay = new ResultsDisplay();
console.log("constructed jsResultsDisplay");


