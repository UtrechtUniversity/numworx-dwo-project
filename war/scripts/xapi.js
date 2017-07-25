// Simplified version of Lms_Api.js, used to display statements on the page
var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];
var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

eventer(messageEvent, handleMessage, false);

var xapi = function(msg) {
		
}

TinCan.enableDebug()
// variables from Window.query "actor" "activityId" "registration" "endpoint"
var actor = new TinCan.Agent({ "mbox": "hello@learninglocker.net"})
var activityId = "http://www.dwo.nl/activiteit/105645"
var activity = new TinCan.Activity({"id": activityId})
var registration = "760e3480-ba55-4991-94b0-01820dbd23a2"
var endpoint = "http://localhost:8080/data/xAPI/"

function handleMessage(message) {
	var msg = JSON.parse(message.data);
	var isArray = msg.constructor == Array;
	if ( !isArray ) 
		xapi(msg)
	else 
	{
		var arrayLength = msg.length;
		for (var i = 0; i < arrayLength; i++) {
		    xapi(msg[i]);
		}
	}
}

function decompressFromBase64(state) {
	if(state.length > 0)
		state = LZString.decompressFromBase64(state);
	return state;
}


function createAnsweredStatement(success, duration, scoreScaled, completion) {
	//var success = true;
	//var duration  = 'PT3M15S'
	var score = { "scaled": scoreScaled };
	//var completion = false;
	var id = window.location.toString()
	var answer = Bao.buildAnswer(id, null, success, duration, score, null, null, null, null, completion);
	var statement = Bao.buildAnsweredStatement(answer);
	return statement;
}

function sendAnsweredStatement(succes, duration, scoreScaled, completion) {
	var statement = createAnsweredStatement(succes, duration, scoreScaled, completion)
	tincan.sendStatement(statement);
}

function sendModuleDataRequest() {
	var statement = new TinCan.Statement({"verb": {"id":"http://adlnet.gov/expapi/verbs/initialized"}})
    tincan.sendStatement(statement);
}

function createModuleDataStatement(moduledata) {
	var json = moduledata;
	json = LZString.compressToBase64(moduledata);
	var statement = Bao.buildSetModuleDataRequestStatement(json)
    return statement;
}

function sendModuleDataStatement(moduledata) {
	var statement = createModuleDataStatement(moduledata)
	tincan.sendStatement(statement)
}

function sendAnswerAndModuleDataStatements(succes, duration, scoreScaled, completion, moduledata) {
	var statements = [ createModuleDataStatement(moduledata), createAnsweredStatement(succes, duration, scoreScaled, completion) ]
	tincan.sendStatements(statements);
}
 

//var lrs = new ContentApiLrs();

var lrs = new TinCan.LRS(
		{ "endpoint": endpoint,
		  "username": "874349e2858d5522e25b2f4a33b6e5f9d8187670",
		  "password": "e2bab2ca1c546d09cdb411a6e3dfeed19edcd32a",
		});

tincan = new TinCan({"actor": actor, "activity": activity, "recordStores": [lrs], "registration": registration});
