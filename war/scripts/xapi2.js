// Simplified version of Lms_Api.js, used to display statements on the page
var tincan

var xapi = function(msg) {
		
}

TinCan.enableDebug()
// variables from Window.query "actor" "activityId" "registration" "endpoint"
var actor = new TinCan.Agent({ "mbox": "hello@learninglocker.net"})
var activityId = "http://www.dwo.nl/activiteit/96797"
var activity = new TinCan.Activity({"id": activityId})
var registration = "760e3480-ba55-4991-94b0-01820dbd23a3"
var context = new TinCan.Context({"registration":registration})
var endpoint = "http://localhost:8080/data/xAPI/"
var fetch = null
	
function initFromURL(url) {
        console.log("initFromURL");

        var i,
            prop,
            qsParams = TinCan.Utils.parseURL(url).params,
            lrsCfg = {},
            contextCfg,
            extended = null
        ;

        if (qsParams.hasOwnProperty("actor")) {
            console.log("_initFromQueryString - found actor: " + qsParams.actor);
            try {
                actor = TinCan.Agent.fromJSON(qsParams.actor);
                delete qsParams.actor;
            }
            catch (ex) {
                console.log("_initFromURL - failed to set actor: " + ex);
            }
        }

        if (qsParams.hasOwnProperty("activityId")) {
            activity = new TinCan.Activity (
                {
                    id: qsParams.activityId
                }
            );
            delete qsParams.activity_id;
        }

        {
            contextCfg = {};
            contextCfg.registration = registration

            if (qsParams.hasOwnProperty("registration")) {
                //
                // stored in two locations cause we always want it in the default
                // context, but we also want to be able to get to it for Statement
                // queries
                //
                contextCfg.registration = registration = qsParams.registration;
                delete qsParams.registration;
            }
            
            context = new TinCan.Context (contextCfg);
        }

        //
        // order matters here, process the URL provided LRS last because it gets
        // all the remaining parameters so that they get passed through
        //
        if (qsParams.hasOwnProperty("endpoint")) {
        	endpoint = qsParams.endpoint
        }
}

initFromURL(window.location.href.split('#')[0])
	
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
		var state = msg.contents || ""
		if(state.length > 0)
			state = LZString.decompressFromBase64(state);
		xapi(state)
	}})
	var statement = new TinCan.Statement({"verb": {"id":"http://adlnet.gov/expapi/verbs/initialized"}})
    tincan.sendStatement(statement);
}

function sendModuleDataStatement(moduledata) {
	var Data = LZString.compressToBase64(moduledata);
	tincan.setState("cmi.suspend_data", Data, {"callback":function() {
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

tincan = new TinCan({"actor": actor, "activity": activity, "recordStores": [lrs], "context": context});
