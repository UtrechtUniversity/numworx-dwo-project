// Simplified version of Lms_Api.js, used to display statements on the page
var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];
var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

eventer(messageEvent, handleMessage, false);

var xapi = function(msg) {
		
}

function handleMessage(message) {
	var msg = JSON.parse(message.data);
	xapi(msg);
}

function createAnsweredStatement {
	var success = true;
	var duration  = 'PT3M15S'
	var score = { "scaled": 0.4 };
	var completion = false;

	var answer = Bao.buildAnswer(null, null, success, duration, score, null, null, null, null, completion);
	var statement = Bao.buildAnsweredStatement(answer);
	return statement;
}

function sendAnsweredStatement() {
	var statement = createAnsweredStatement()
	tincan.sendStatement(statement);
}

function sendModuleDataRequest() {
	var statement = new TinCan.Statement();

    statement.verb = new TinCan.Verb({ "id": "http://bao.mijnklas.nl/xapi/verbs/moduleData" });
    _targetUri = "*";
    tincan.sendStatement(statement);
}

function createModuleDataStatement() {
	json = "mijn module data";
	var statement = new TinCan.Statement();
    statement.verb = new TinCan.Verb({ "id": "http://bao.mijnklas.nl/xapi/verbs/moduleData" });
    statement.object = 
    	{
    		"id": "http://bao.mijnklas.nl/xapi/activities/set-moduledata-request",
    		"objectType": "Activity",
    		"definition": {
    			"extensions": {
    				"http://bao.mijnklas.nl/xapi/extensions/objectType": "json",
    				"http://bao.mijnklas.nl/xapi/extensions/json":  json
    			}
    		}
    	}
    return statement;
}

function sendModuleDataStatement() {
	var statement = createModuleDataStatement()
	tincan.sendStatement(statement)
}

function sendAnswerAndModuleDataStatements() {
	var statements = [ createModuleDataStatement(), createAnsweredStatement() ]
	tincan.sendStatements(statements);
}
 
