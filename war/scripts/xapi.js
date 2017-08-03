// Simplified version of Lms_Api.js, used to display statements on the page
var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];
var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

eventer(messageEvent, handleMessage, false);

var xapi = function(msg) {
		
}

function handleMessage(message) {
	var msg = JSON.parse(message.data);
	tincan.log("handleMessage " + message.data)
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
	var statement = Bao.buildGetModuleDataRequestStatement()
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

function sendCompletedStatement(duration, scoreScaled) {
	var score = { "scaled": scoreScaled };
	var answer = new TinCan.Result({'duration': duration, "score":score});
	var statement = new TinCan.Statement({'result':answer, 'verb':{'id':"http://adlnet.gov/expapi/verbs/completed"}});
}

function sendTerminatedStatement() {
	var statement = new TinCan.Statement({"verb": {"id":"http://adlnet.gov/expapi/verbs/terminated"}})
    tincan.sendStatement(statement);
}


var lrs = new ContentApiLrs();
TinCan.enableDebug()