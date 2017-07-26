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
var activityId = "http://www.dwo.nl/activiteit/96797"
var activity = new TinCan.Activity({"id": activityId})
var registration = "760e3480-ba55-4991-94b0-01820dbd23a3"
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
	var answer = new TinCan.Result({"success":success, "duration": duration, "score":score, "completion":completion})
	var statement = new TinCan.Statement({"result":answer,"verb":{"id":"http://adlnet.gov/expapi/verbs/answered"}})
	return statement;
}

function sendAnsweredStatement(succes, duration, scoreScaled, completion) {
	var statement = createAnsweredStatement(succes, duration, scoreScaled, completion)
	tincan.sendStatement(statement);
}

function sendModuleDataRequest() {
	tincan.getState("cmi.suspend_data", {"callback":function(x, msg) {
		msg = msg || {}
		msg.contents = msg.contents|| "{}"
		xapi(msg.contents)
	}})
	var statement = new TinCan.Statement({"verb": {"id":"http://adlnet.gov/expapi/verbs/initialized"}})
    tincan.sendStatement(statement);
}

function sendModuleDataStatement(moduledata) {
	tincan.setState("cmi.suspend_data", moduledata, {"callback":function() {
		var a = arguments;
		console.log(JSON.stringify(a));
	}});
}

function sendAnswerAndModuleDataStatements(succes, duration, scoreScaled, completion, moduledata) {
	sendModuleDataStatement(moduledata);
	sendAnsweredStatement(succes, duration, scoreScaled, completion)
}
 
function sendCompletedStatement(duration, scoreScaled) {
	var score = { "scaled": scoreScaled };
	var answer = new TinCan.Result({'duration': duration, "score":score});
	var statement = new TinCan.Statement({'result':answer, 'verb':{'id':"http://adlnet.gov/expapi/verbs/completed"}});
}

function sendTerminatedStatement() {
	var statement = new TinCan.Statement({"verb": {"id":"http://adlnet.gov/expapi/verbs/terminated"}})
    tincan.sendStatement(statement);
}

var lrs = new TinCan.LRS(
		{ "endpoint": endpoint,
		  "username": "874349e2858d5522e25b2f4a33b6e5f9d8187670",
		  "password": "e2bab2ca1c546d09cdb411a6e3dfeed19edcd32a",
		});

tincan = new TinCan({"actor": actor, "activity": activity, "recordStores": [lrs], "registration": registration});
