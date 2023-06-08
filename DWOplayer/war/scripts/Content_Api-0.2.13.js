/// <reference path="tincan.js" />
"use strict";

var verbNamespace = "http://bao.mijnklas.nl/xapi/verbs/";
var tincan = null;

var ContentApiLrs = function (cfg) {
    this.version = "1.0.1";
    this._target = null;
    this._targetUri = '';
    this.setTarget = function (postTarget, uri) {
        this._target = postTarget;
        this._targetUri = uri;
    };

    if (!cfg) {
        tincan = new TinCan();

        var safeHost = decodeURIComponent(document.location.hash.substr(1));
        if (safeHost == '') {
            console.warn("No safe host supplied, falling back to '*'.");
            safeHost = '*';
        }

        this.setTarget(window.parent, safeHost);

        tincan.addRecordStore(this);
    }
};

//TinCan.enableDebug();

ContentApiLrs.prototype = TinCan.LRS.prototype;

/**
Method should be overloaded by an environment to do per
environment specifics such that the LRS can make a call
to set the version if not provided

@method _initByEnvironment
@private
*/
ContentApiLrs.prototype._initByEnvironment = function () {
    //this.log("AmbrasoftLRS._initByEnvironment not overloaded - no environment loaded?");
};

/**
Method should be overloaded by an environment to do per
environment specifics for sending requests to the LRS

@method _makeRequest
@private
*/
ContentApiLrs.prototype._makeRequest = function (fullUrl, headers, cfg) {
    //this.log("AmbrasoftLRS._makeRequest not overloaded - no environment loaded?");
    //console.log(cfg);
    //this._target.postMessage(JSON.stringify(cfg), this._targetUri);
    this._target.postMessage(cfg.data, this._targetUri);
};

/**
Method is overloaded by the browser environment in order to test converting an
HTTP request that is greater than a defined length

@method _IEModeConversion
@private
*/
ContentApiLrs.prototype._IEModeConversion = function () {
    //this.log("AmbrasoftLRS._IEModeConversion not overloaded - browser environment not loaded.");
};

String.prototype.format = String.prototype.format || function () {
    var args = arguments;
    return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function (m, n) {
        if (m == "{{") { return "{"; }
        if (m == "}}") { return "}"; }
        return args[n];
    });
};

//#region Bao namespace
var Bao = function (cfg) {
    //this.Constants = null;

    this.init(cfg);
};

//#region Constants
Bao.prototype = {
    init: function (cfg) {
        //this.Constants = new Bao.Constants();
    }
};

//var BaoConstants = Bao.Constants = function () {

//	this.init();
//}
var BaoConstants = {};

BaoConstants.Namespace = "http://bao.mijnklas.nl/xapi";

BaoConstants.Verbs = {
    moduleData: BaoConstants.Namespace + "/verbs/moduleData",
    navigate: BaoConstants.Namespace + "/verbs/navigate"
};

BaoConstants.Activities = {
    setmoduleRequest: BaoConstants.Namespace + "/activities/set-moduledata-request",
    getmoduleRequest: BaoConstants.Namespace + "/activities/get-moduledata-request",
    interactionidentifier: BaoConstants.Namespace + "/activities/interactionidentifier"
};

BaoConstants.Extensions = {
    objectType: BaoConstants.Namespace + "/extensions/objectType",
    json: BaoConstants.Namespace + "/extensions/json",

    learningTrackAssignmentId: BaoConstants.Namespace + "/extensions/learningtrackAssignmentId",
    learningTrackId: BaoConstants.Namespace + "/extensions/learningtrackId",

    navigateType: BaoConstants.Namespace + "/extensions/navigateType",
    navigateValue: BaoConstants.Namespace + "/extensions/navigateValue",
    rate: BaoConstants.Namespace + "/extensions/rate"
};

//BaoConstants.prototype = {
//	init: function () {

//	}
//}

Bao.Constants = BaoConstants;
//#endregion Constants

var BaoExtensions = Bao.Extensions = function (cfg) {
    this.init(cfg);
};
BaoExtensions.prototype = {
    init: function (cfg) { }
};

//#endregion Bao namespace

//#region Plumbing
function isCustomMessage(msg) {
    debugger;
    console.error('isCustomMessage should not be used anymore..!!');
    var statement = msg;
    if (msg.data) {
        var tmp = JSON.parse(msg.data);

        statement = Array.isArray(tmp) ? tmp[0] : tmp;
    }
    return (statement.verb && statement.verb.id && statement.verb.id.substr(0, 34) == 'http://bao.mijnklas.nl/xapi/verbs/'
		&& statement.target && statement.target.id && statement.target.objectType && statement.target.objectType == 'Activity' && statement.target.id.substr(0, 7) == 'Custom.');
}

Bao.copyProperties = function (source, target, props) {
    if (source == null) {
        return;
    }
    for (var i = 0; i < props.length; i++) {
        if (source.hasOwnProperty(props[i]) && source[props[i]] !== null) {
            target[props[i]] = source[props[i]];
        }
    }
};

Bao.copyAndRenameProperty = function (source, target, sourceName, targetName) {
    if (source == null) {
        return;
    }
    if (source.hasOwnProperty(sourceName) && source[sourceName] !== null) {
        target[targetName] = source[sourceName];
    }
};
Bao.copyAllProperties = function (source, target) {
    if (source == null) {
        return;
    }
    for (var p in source) {
        if (source.hasOwnProperty(p) && source[p] !== null) {
            target[p] = source[p];
        }
    }
};
Bao.addPropertyIfMissing = function (cfg, propName, propValue) {
    if (!cfg.hasOwnProperty(propName) && cfg[propName] !== null) {
        cfg[propName] = propValue;
    }
};
Bao.buildVerb = function (id, cultureCode, humanReadable) {
    var cfg = { "id": id };

    if (cultureCode && humanReadable) {
        cfg.display = {};
        cfg.display[cultureCode] = humanReadable;
    }

    return new TinCan.Verb(cfg);
};
Bao.buildVerbStatement = function (verb) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement based on the given verb.</summary>
    ///   <param name="verb" type="TinCan.Verb">The verb of the statement.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return new TinCan.Statement({ "verb": verb });
};
Bao.buildTargetStatement = function (verb, target) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement based on the given verb and target.</summary>
    ///   <param name="verb" type="TinCan.Verb">The verb of the statement.</param>
    ///   <param name="target" type="TinCan.Activity">The target of the statement.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return new TinCan.Statement({ "verb": verb, "object": target });
};

Bao.buildResultAndTargetStatement = function (verb, target, result) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement based on the given verb and result.</summary>
    ///   <param name="verb" type="TinCan.Verb">The verb of the statement.</param>
    ///   <param name="result" type="TinCan.Result">The result of the statement.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return new TinCan.Statement({ "verb": verb, "object": target, "result": result });
};

Bao.buildResultStatement = function (verb, result) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement based on the given verb and result.</summary>
    ///   <param name="verb" type="TinCan.Verb">The verb of the statement.</param>
    ///   <param name="result" type="TinCan.Result">The result of the statement.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return new TinCan.Statement({ "verb": verb, "result": result });
};
Bao.buildTargetAndResultStatement = function (verb, target, result) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement based on the given verb, target and result.</summary>
    ///   <param name="verb" type="TinCan.Verb">The verb of the statement.</param>
    ///   <param name="target" type="TinCan.Activity">The target of the statement.</param>
    ///   <param name="result" type="TinCan.Result">The result of the statement.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return new TinCan.Statement({ "verb": verb, "object": target, "result": result });
};
//#endregion Plumbing

//#region StatementBuilders
// * answered
Bao.buildAnsweredStatement = function (answer) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/answered verb.</summary>
    ///   <param name="answer" type="Bao.Answer">The answer to submit to the LRS.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/answered verb.</summary>
    ///   <param name="answer" type="Object">The Bao.Answer-like structure to submit to the LRS as a Bao.Answer.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>

    var activity = Bao.buildAnsweredActivity(answer);
    var result = Bao.buildAnsweredResult(answer);

    return Bao.buildTargetAndResultStatement(
		Bao.buildVerb("http://adlnet.gov/expapi/verbs/answered", "nl-NL", "Beantwoord"),
		activity,
		result
	);
};
// * assets
Bao.buildAssetRequestStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/assets", "nl-NL", "Eigendommen/Onderdelen"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/assets-request"));
};
Bao.buildAssetResponseStatement = function (assetCollection) {
    /// <signature>
    ///   <summary>Returns a result TinCan.Statement for the http://bao.mijnklas.nl/xapi/verbs/assets verb.</summary>
    ///   <param name="assetCollection" type="Bao.AssetCollection">The collection of (generic) assets.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/assets", "nl-NL", "Eigendommen/Onderdelen"),
		Bao.buildResultFromExtension(null, { "http://bao.mijnklas.nl/xapi/extensions/assetCollection": assetCollection }));
};
// * audio
Bao.buildAudioRequestStatement = function (uniqueId) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/audio", "nl-NL", "Geluid"),
		Bao.buildAudioRequestActivity(uniqueId));
};
Bao.buildAudioResponseStatement = function (duration, uniqueId, url, mimeType, objectType) {
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/audio", "nl-NL", "Geluid"),
		Bao.buildAudioResponseResult(duration, uniqueId, url, mimeType, objectType));
};
// * avatarData
Bao.buildAvatarDataRequestStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/avatarData", "nl-NL", "Avatar"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/avatarData-request"));
};
Bao.buildAvatarDataResponseStatement = function (avatarData) {
    /// <signature>
    ///   <summary>Returns a result TinCan.Statement for the http://bao.mijnklas.nl/xapi/verbs/avatarData verb.</summary>
    ///   <param name="avatarData" type="Bao.AvatarData">The data for the avatar of the current user.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>)
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/avatarData", "nl-NL", "Avatar"),
		Bao.buildResultFromExtension(null, { "http://bao.mijnklas.nl/xapi/extensions/avatarData": avatarData }));
};
// * benefit
Bao.buildBenefitStatement = function (benefitType, playerId) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/benefit", "nl-NL", "Je voordeel doen"),
		Bao.buildBenefitActivity(benefitType, playerId));
};
// * completed
Bao.buildComletedStatement = function (jsonArray) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/completed verb.</summary>
    ///   <param name="jsonArray" type="Array">An array of objects.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return Bao.buildResultStatement(
		Bao.buildVerb("http://adlnet.gov/expapi/verbs/completed", "nl-NL", "Afgerond"),
		Bao.buildCompletedResult(jsonArray));
};
// * content
Bao.buildContentRequestStatement = function () {
    return Bao.buildTargetStatement(
        Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/content", "nl-NL", "Inhoud"),
        Bao.buildContentActivity());
};



// * custom content
Bao.buildCustomContentRequestStatement = function (moduleType, moduleConfig) {
    moduleType = capitalizeFirstLetter(moduleType);
    var verb = Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/customContent", "nl-NL", "Custom inhoud voor module " + moduleType);
    var contentActivity = Bao.buildCustomContentActivity(moduleType, moduleConfig);
    var result = Bao.buildTargetStatement(verb, contentActivity);
    return result;
};

// * context
Bao.buildContextRequestStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/context", "nl-NL", "Context"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/get-context-request"));
};
Bao.buildContextResponseStatement = function (duration, jsonArray) {
    return Bao.buildTargetAndResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/context", "nl-NL", "Context"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/get-context-response"),
		Bao.buildResultFromExtension(duration, new JSonArrayExtension(jsonArray)));
};
Bao.buildCustomHelpRequestStatement = function (helpType) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/customHelp", "nl-NL", "Hulp op maat"),
		Bao.buildActivityFromExtension('http://bao.mijnklas.nl/xapi/activities/htmlHelp', { 'http://bao.mijnklas.nl/xapi/extensions/helpType': helpType }));
};
Bao.buildCustomHelpResponseStatement = function (htmlHelp) {
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/customHelp", "nl-NL", "Hulp op maat"),
		Bao.buildResultFromExtension(null, { 'http://bao.mijnklas.nl/xapi/extensions/htmlHelp': htmlHelp }));
};
// * questionSet (possible content response)
Bao.buildQuestionSetStatement = function (questions) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/questionSet verb.</summary>
    ///   <param name="questions" type="Array">An array of Bao.Question objects.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/questionSet verb.</summary>
    ///   <param name="questions" type="Array">An array of objects with a 'uniqueId' and 'value' property.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/questionSet", "nl-NL", "Lijst van vragen"),
		Bao.buildQuestionSetResult(questions));
};
// * counter
Bao.buildCounterStatement = function (remaining, failed, success, currentFailed, currentSuccess) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/counter", "nl-NL", "Teller"),
		Bao.buildCounterActivity(remaining, failed, success, currentFailed, currentSuccess));
};
//experienced
//failed
//initialized
// * logout
Bao.buildLogoutRequestStatement = function () {
    return Bao.buildVerbStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/logout", "nl-NL", "Uitloggen"));
};

Bao.buildGetUserRankRequestStatement = buildGetUserRankRequestStatement;
Bao.buildGetChampListRequestStatement = buildGetChampListRequestStatement;
Bao.buildGetSchoolChampListRequestStatement = buildGetSchoolChampListRequestStatement;
Bao.buildTableQuickRequestStatement = buildTableQuickRequestStatement;
Bao.buildTableQuickResponseStatement = buildTableQuickResponseStatement;

function buildGetUserRankRequestStatement(competitionId) {
    var verb = Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/getUserRank");
    var extensions = {
        "http://bao.mijnklas.nl/xapi/extensions/userRank": {
            "competitionId": competitionId.toString()
        }
    };
    var activity = Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/userRank", extensions);
    return Bao.buildTargetStatement(verb, activity);
};

function buildGetChampListRequestStatement(competitionId, numberOfChamps) {
    var verb = Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/getChampList");
    var extensions = {
        "http://bao.mijnklas.nl/xapi/extensions/champList": {
            "competitionId": competitionId.toString(),
            "numberOfChamps": numberOfChamps.toString()
        }
    };
    var activity = Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/champList", extensions);
    return Bao.buildTargetStatement(verb, activity);
}

function buildGetSchoolChampListRequestStatement(competitionId) {
    var verb = Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/getSchoolChampList");
    var extensions = {
        "http://bao.mijnklas.nl/xapi/extensions/schoolChampList": {
            "competitionId": competitionId.toString()
        }
    };
    var activity = Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/schoolChampList", extensions);
    return Bao.buildTargetStatement(verb, activity);
}

function buildTableQuickRequestStatement() {
    var verb = Bao.buildVerb(verbNamespace + "tableQuick");
    return Bao.buildVerbStatement(verb);
}

function buildTableQuickResponseStatement(table) {
    if (!table) {
        throw "You must provide a table as argument";
    }

    var verb = Bao.buildVerb(verbNamespace + "tableQuick");
    var extension = {
        "http://bao.mijnklas.nl/xapi/extensions/tableQuickTableToPlay": table.toString()
    };
    var result = Bao.buildResultFromExtension(null, extension);
    return Bao.buildResultStatement(verb, result);
}


// * metaData
Bao.buildMetaDataRequestStatement = function () {
    return Bao.buildVerbStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/metaData", "nl-NL", "Settings"));
};

Bao.buildCustomContentResponseStatement = function (id, jsonArray) {
    var activity = Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/" + id);
    return Bao.buildResultAndTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/customContent", "nl-NL", "Settings"),
                        activity,
                        Bao.buildResultFromExtension(id, new JSonArrayExtension(jsonArray)));
};

Bao.buildMetaDataResponseStatement = function (duration, jsonArray) {
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/metaData", "nl-NL", "Settings"),
		Bao.buildResultFromExtension(duration, new JSonArrayExtension(jsonArray)));
};
// * customMetaData
Bao.buildCustomMetaDataRequestStatement = function () {
    var verb = Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/customMetaData", "nl-NL", "Settings");
    return Bao.buildVerbStatement(verb);
};
// * moduleData
Bao.buildGetModuleDataRequestStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/moduleData", "nl-NL", "Contextuele gegevens"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/get-moduledata-request"));
};
Bao.buildGetModuleDataResponseStatement = function (duration, json) {
    return Bao.buildTargetAndResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/moduleData"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/get-moduledata-response"),
		Bao.buildResultFromExtension(duration, new JSonExtension(json)));
};
Bao.buildSetModuleDataRequestStatement = function (json) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/moduleData", "nl-NL", "Contextuele gegevens"),
		Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/set-moduledata-request", new JSonExtension(json)));
};
Bao.buildSetModuleDataResponseStatement = function (duration, response) {
    return Bao.buildTargetAndResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/moduleData"),
		Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/set-moduledata-response"),
		Bao.buildSimpleResult(duration, response));
};
// * navigate

Bao.buildNavigateCommandStatement = function (command) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/navigate", "nl-NL", "Navigeer"),
		Bao.buildNavigateActivity("Command", command));
};
Bao.buildNavigateBackStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/navigate", "nl-NL", "Navigeer"),
		Bao.buildNavigateActivity("Command", "Back"));
};
Bao.buildNavigateNextStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/navigate", "nl-NL", "Navigeer"),
		Bao.buildNavigateActivity("Command", "Next"));
};
Bao.buildNavigateBackGroundStatement = function () {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/navigate", "nl-NL", "Navigeer"),
		Bao.buildNavigateActivity("Command", "BackGround"));
};
Bao.buildNavigateByIdentifierStatement = function (uniqueIdentifier) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/navigate", "nl-NL", "Navigeer"),
		Bao.buildNavigateActivity("Identifier", uniqueIdentifier));
};
// * passed
// * experienced

Bao.buildExperiencedStatement = function () {
    var statement = Bao.buildTargetStatement(
        Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/experienced", "nl-NL", "Ervaren"),
        Bao.buildActivityFromId("http://bao.mijnklas.nl/xapi/activities/experienced"));

    for (var i in statement) {
        if (statement[i] === null || statement[i] === undefined) {
            // test[i] === undefined is probably not very useful here
            delete statement[i];
        }
    }
    return statement;
};
// * rated
Bao.buildRatedStatement = function (uniqueIdentifier, scoreRaw, comment, interactionType, scoreMax) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://id.tincanapi.com/verb/rated verb.</summary>
    ///   <param name="uniqueIdentifier" type="String">The unique identifier of the rated item.</param>
    ///   <param name="uniqueIdentifier" type="Number">The raw score.</param>
    ///   <param name="uniqueIdentifier" type="String">The comment for this rating.</param>
    ///   <param name="uniqueIdentifier" type="String">The (Optional) interactionType.</param>
    ///   <param name="uniqueIdentifier" type="Number">The (Optional) maximum score.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    var result = Bao.buildSimpleResult(null, comment);

    result.score = new TinCan.Score({ "raw": scoreRaw, "max": scoreMax });

    return Bao.buildTargetAndResultStatement(
		Bao.buildVerb("http://id.tincanapi.com/verb/rated", "nl-NL", "Beoordeeld"),
		Bao.buildActivityFromDefinition(uniqueIdentifier, { "type": "http://adlnet.gov/expapi/activities/cmi.interaction", "interactionType": interactionType }),
		result
	);
};
//scored
// * skipped
Bao.buildSkippedStatement = function (uniqueIdentifier) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/skipped verb.</summary>
    ///   <param name="uniqueIdentifier" type="String">The unique identifier of the skipped item.</param>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://id.tincanapi.com/verb/skipped", "nl-NL", "Overgeslagen"),
		Bao.buildActivityFromId(uniqueIdentifier));
};
// * speak
Bao.buildSpeakStatement = function (textToPronounce, cultureCode) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/speak", "nl-NL", "Spreken"),
		Bao.buildSpeachRequestActivity(textToPronounce, cultureCode));
};
// * submitted
Bao.buildSubmittedStatement = function (interactionUniqueId, answerIsCorrect, nrOfAttempts, completed) {
    return Bao.buildTargetAndResultStatement(Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/submitted", "nl-NL", "Ingestuurd"),
		Bao.buildActivityFromId(interactionUniqueId),
		Bao.buildResultFromExtension(null, { "http://bao.mijnklas.nl/xapi/extensions/success": answerIsCorrect, "http://bao.mijnklas.nl/xapi/extensions/attempts": nrOfAttempts, "http://bao.mijnklas.nl/xapi/extensions/completed": completed }));
};
//suspended
// * tease
Bao.buildTeaseStatement = function (teaseType, playerId) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/tease", "nl-NL", "Plagen"),
		Bao.buildTeaseActivity(teaseType, playerId));
};
//terminated
Bao.buildTerminatedStatement = function (duration) {
    /// <signature>
    ///   <summary>Returns a TinCan.Statement for the http://adlnet.gov/expapi/verbs/terminated verb.</summary>
    ///   <returns type="TinCan.Statement" />
    /// </signature>
    return Bao.buildResultStatement(
		Bao.buildVerb("http://adlnet.gov/expapi/verbs/terminated", "nl-NL", "Gestopt"),
		Bao.buildDurationResult(duration));
};
// * validate
Bao.buildValidateStatement = function () {
    return Bao.buildResultStatement(
		Bao.buildVerb('http://bao.mijnklas.nl/xapi/verbs/validate'),
		{ 'id': 'http://bao.mijnklas.nl/xapi/verbs/validate' });
};
Bao.buildReadyForValidationStatement = function (isReady) {
    return Bao.buildResultStatement(
		Bao.buildVerb('http://bao.mijnklas.nl/xapi/verbs/readyForValidation'),
		Bao.buildResultFromExtension(null, { 'http://bao.mijnklas.nl/xapi/extensions/isReady': isReady }));
};
// * video
Bao.buildVideoRequestStatement = function (uniqueId) {
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/video", "nl-NL", "Video"),
		Bao.buildVideoRequestActivity(uniqueId));
};
Bao.buildVideoResponseStatement = function (duration, uniqueId, url, mimeType, objectType) {
    return Bao.buildResultStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/video", "nl-NL", "Video"),
		Bao.buildVideoResponseResult(duration, uniqueId, url, mimeType, objectType));
};
// * Generic Game-statement
Bao.buildGameStatement = function (verb, playerId, extensions) {
    if (playerId) {
        extensions = extensions || {};

        extensions['http://bao.mijnklas.nl/xapi/extensions/playerId'] = playerId;
    }
    return Bao.buildTargetStatement(
		Bao.buildVerb("http://bao.mijnklas.nl/xapi/verbs/game/" + verb),
		Bao.buildActivityFromExtension('http://bao.mijnklas.nl/xapi/activities/game/' + verb, extensions));
};
//#endregion StatementBuilders

//#region Bao methods
Bao.getTarget = function (statement) {
    return statement.target || statement.object;
};
Bao.buildActivityFromExtension = function (id, extensions) {
    return new TinCan.Activity({ "objectType": "Activity", "id": id, "definition": { "extensions": extensions } });
};
Bao.buildActivityFromDefinition = function (id, definition) {
    return new TinCan.Activity({ "objectType": "Activity", "id": id, "definition": definition });
};
Bao.buildActivityFromId = function (id) {
    return new TinCan.Activity({ "objectType": "Activity", "id": id });
};
Bao.buildDurationResult = function (duration) {
    return new TinCan.Result({ "duration": duration || null });
};
Bao.buildResultFromExtension = function (duration, extensions) {
    return new TinCan.Result({ "duration": duration, "extensions": extensions });
};
Bao.buildSimpleResult = function (duration, response) {
    return new TinCan.Result({ "duration": duration, "response": response });
};
Bao.buildTableQuickResult = function (table) {
    return new TinCan.Result({ "tableToPlay": table.toString() });
}
Bao.mapOrWrap = function (source, target, propertyName, type, wrapCallback, targetPropertyName) {
    if (source == null) {
        return;
    }

    var src = null;
    if (propertyName == undefined) {
        src = source;
    }
    else {
        if (source.hasOwnProperty(propertyName) && source[propertyName] !== null) {
            src = source[propertyName];
        }
    }

    if (src == null) {
        return;
    }

    var trgt = null;
    if (Array.isArray(target)) {
        if (!Array.isArray(src)) {
            debugger; // Cannot put properties on an array... maybe this could be adding key/value-pairs to an array..??
        }
        trgt = target;
    }
    else {
        trgt = targetPropertyName == undefined ? target[propertyName] : target[targetPropertyName];
    }

    if (Array.isArray(src)) {
        for (var i = 0; i < src.length; i++) {
            trgt.push(returnOrWrap(src[i], type, wrapCallback));
        }
    }
    else {
        var val = returnOrWrap(src, type, wrapCallback);
        if (targetPropertyName == undefined) {
            target[propertyName] = val;
        } else {
            target[targetPropertyName] = val;
        }
    }

    function returnOrWrap(instance, type, wrapCallback) {
        if (instance instanceof type) {
            return instance;
        }
        else {
            return wrapCallback(instance);
        }
    }
};
//#endregion Bao methods

//#region Types
var Answer = Bao.Answer = function (cfg) {
    this.id = null;
    this.score = null;
    this.response = null;
    this.success = null;
    this.completion = null;
    this.duration = null;
    this.resultExtensions = null;
    this.targetExtensions = null;
    this.interactionType = null;
    this.expected = null;

    this.init(cfg);
};
Answer.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['id', 'response', 'success', 'completion', 'duration', 'resultExtensions', 'targetExtensions', 'interactionType', 'expected']);

        Bao.mapOrWrap(cfg, this, 'score', TinCan.Score, function (prop) { return new TinCan.Score(prop); });
    }
};
Bao.buildAnswer = function (id, response, success, duration, score, interactionType, expected, resultExtensions, targetExtensions, completion) {
    return new Bao.Answer({ "id": id, "response": response, "success": success, "duration": duration, "score": score, "interactionType": interactionType, "expected": expected, "resultExtensions": resultExtensions, "targetExtensions": targetExtensions, "completion": completion });
};
var AnswerDefinition = Bao.AnswerDefinition = function (cfg) {
    this.type = "http://adlnet.gov/expapi/activities/cmi.interaction";
    this.description = null;
    this.interactionType = null;
    this.correctResponsesPattern = (cfg.hasOwnProperty('expected') && cfg.expected !== null ? cfg.expected : null);

    this.init(cfg);
};
AnswerDefinition.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['type', 'description', 'interactionType']);
    }
};
Bao.buildAnswerDefinition = function (id, description, interactionType, correctResponsesPattern) {
    return new Bao.AnswerDefinition({ "id": id, "description": description, "interactionType": interactionType, "correctResponsesPattern": correctResponsesPattern });
};
Bao.buildAnswerDefinitionFromAnswer = function (cfg) {
    var extensions = {};

    Bao.copyAllProperties(cfg.targetExtensions, extensions);

    Bao.copyAndRenameProperty(cfg, extensions, 'expected', 'http://bao.mijnklas.nl/xapi/extensions/correctResponsesPattern');
    Bao.copyAndRenameProperty(cfg, extensions, 'interactionType', 'http://bao.mijnklas.nl/xapi/extensions/interactionType');


    var init = { "type": "http://adlnet.gov/expapi/activities/cmi.interaction", "extensions": extensions };

    Bao.copyProperties(cfg, init, ['description']);
    return new TinCan.ActivityDefinition(init);
};
//#region Verb-Types

//#region AnsweredActivity
var AnsweredActivity = Bao.AnsweredActivity = function (cfg) {
    this.id = null;
    this.definition = null;

    this.init(cfg);
};
AnsweredActivity.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['id']);
    }
};
Bao.buildAnsweredActivity = function (answer) {
    //return Bao.buildActivityFromExtension(answer.id, Bao.buildAnswerDefinition(answer));
    return Bao.buildActivityFromDefinition(answer.id, Bao.buildAnswerDefinitionFromAnswer(answer));
};
//#endregion AnsweredActivity

//#region AnsweredResult
var AnsweredResult = Bao.AnsweredResult = function (cfg) {
    this.score = null;
    this.response = null;
    this.success = null;
    this.completion = null;
    this.duration = null;
    this.extensions = null;

    this.init(cfg);
};
AnsweredResult.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['response', 'success', 'completion', 'duration']);
        Bao.copyAndRenameProperty(cfg, this, 'resultExtensions', 'extensions');

        Bao.mapOrWrap(cfg, this, 'score', TinCan.Score, function (prop) { return new TinCan.Score(prop); });
    }
};
Bao.buildAnsweredResult = function (answer) {
    return new Bao.AnsweredResult(answer);
};
//#endregion AnsweredActivity

//#region AudioRequest
var AudioRequest = Bao.AudioRequest = function (cfg) {
    //this.id = "Custom.Audio.Request"; //"http://bao.mijnklas.nl/xapi/activities/audio-request";
    this['http://bao.mijnklas.nl/xapi/extensions/uniqueId'] = null;

    this.init(cfg);
};
AudioRequest.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['http://bao.mijnklas.nl/xapi/extensions/uniqueId']);
    }
};
Bao.buildAudioRequestActivity = function (uniqueId) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/audio-request", new Bao.AudioRequest({ "http://bao.mijnklas.nl/xapi/extensions/uniqueId": uniqueId }));
};
//#endregion AudioRequest

//#region AudioResponse
var AudioResponse = Bao.AudioResponse = function (cfg) {
    //this.id = "Custom.Audio.Response"; //"http://bao.mijnklas.nl/xapi/activities/audio-response";
    this['http://bao.mijnklas.nl/xapi/extensions/uniqueId'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/objectType'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/mimeType'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/audio'] = null;

    this.init(cfg);
};
AudioResponse.prototype = {
    init: function (cfg) {
        this['http://bao.mijnklas.nl/xapi/extensions/objectType'] = 'audio';
        this['http://bao.mijnklas.nl/xapi/extensions/mimeType'] = 'audio/mpeg';
        this['http://bao.mijnklas.nl/xapi/extensions/audio'] = { url: null };
        Bao.copyProperties(cfg, this, ['http://bao.mijnklas.nl/xapi/extensions/uniqueId']);

        if (cfg.hasOwnProperty('url') && cfg.url !== null) {
            this['http://bao.mijnklas.nl/xapi/extensions/audio'].url = cfg.url;
        }
        else if (cfg.hasOwnProperty('audio') && cfg.audio !== null) {
            Bao.copyProperties(cfg.audio, this['http://bao.mijnklas.nl/xapi/extensions/audio'], ['url']);
        }
    }
};
Bao.buildAudioResponseResult = function (duration, uniqueId, url, mimeType, objectType) {
    var response = { "http://bao.mijnklas.nl/xapi/extensions/uniqueId": uniqueId, "url": url };

    if (mimeType && mimeType !== null) {
        response['http://bao.mijnklas.nl/xapi/extensions/mimeType'] = mimeType;
    }

    if (objectType && objectType !== null) {
        response['http://bao.mijnklas.nl/xapi/extensions/objectType'] = objectType;
    }

    return Bao.buildResultFromExtension(duration, new Bao.AudioResponse(response));
};
//#endregion AudioResponse

//#region Benefit
var Benefit = Bao.Benefit = function (cfg) {
    this['http://bao.mijnklas.nl/xapi/extensions/benefit'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/playerId'] = null;

    this.init(cfg);
};
Benefit.prototype = {
    init: function (cfg) {
        //copyProperties(cfg, this, ['benefit', 'playerId']);
        Bao.copyAndRenameProperty(cfg, this, 'benefit', 'http://bao.mijnklas.nl/xapi/extensions/benefit');
        Bao.copyAndRenameProperty(cfg, this, 'playerId', 'http://bao.mijnklas.nl/xapi/extensions/playerId');
    }
};
Bao.buildBenefitActivity = function (benefitType, playerId) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/verbs/benefit-" + benefitType, new Bao.Benefit({ "benefit": benefitType, "playerId": playerId }));
};
//#endregion Benefit

//#region CompletedResult
Bao.buildCompletedResult = function (jsonArray) {
    return Bao.buildResultFromExtension(null, new Bao.JSonArrayExtension(jsonArray));
};
//#endregion CompletedResult

//#region Content
var Content = Bao.Content = function (cfg) {
    this.init(cfg);
};
Content.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, []);
    }
};
Bao.buildContentActivity = function () {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/content-request", new Bao.Content());
};

Bao.buildCustomContentActivity = function (moduleName, moduleConfig) {
    var activityId = "http://bao.mijnklas.nl/xapi/activities/content" + moduleName;
    var extensionsId = "http://bao.mijnklas.nl/xapi/extensions/content" + moduleName;
    var extensions = {};
    extensions[extensionsId] = moduleConfig;
    var statement = (moduleConfig == null)
        ? Bao.buildActivityFromId(activityId)
        : Bao.buildActivityFromExtension(activityId, extensions);

    return statement;
};

//#endregion Counter

//#region Counter
var Counter = Bao.Counter = function (remaining, failed, success, currentFailed, currentSuccess) {
    this['http://bao.mijnklas.nl/xapi/extensions/remaining'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/failed'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/success'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/currentFailed'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/currentSuccess'] = null;
    this.init(remaining, failed, success, currentFailed, currentSuccess);
};
Counter.prototype = {
    init: function (remaining, failed, success, currentFailed, currentSuccess) {
        //copyProperties(cfg, this, []);
        this['http://bao.mijnklas.nl/xapi/extensions/remaining'] = remaining;
        this['http://bao.mijnklas.nl/xapi/extensions/failed'] = failed;
        this['http://bao.mijnklas.nl/xapi/extensions/success'] = success;
        this['http://bao.mijnklas.nl/xapi/extensions/currentFailed'] = currentFailed;
        this['http://bao.mijnklas.nl/xapi/extensions/currentSuccess'] = currentSuccess;
    }
};
Bao.buildCounterActivity = function (remaining, failed, success, currentFailed, currentSuccess) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/counter", new Bao.Counter(remaining, failed, success, currentFailed, currentSuccess));
};
//#endregion Counter

//#region Navigate
var Navigate = Bao.Navigate = function (cfg) {
    this.navigateType = null;
    this.navigateValue = null;
    this.init(cfg);
};
Navigate.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['http://bao.mijnklas.nl/xapi/extensions/navigateType', 'http://bao.mijnklas.nl/xapi/extensions/navigateValue']);
    }
};
Bao.buildNavigateActivity = function (navigateType, navigateValue) {
    return Bao.buildActivityFromExtension(null, new Bao.Navigate({ "http://bao.mijnklas.nl/xapi/extensions/navigateType": navigateType, "http://bao.mijnklas.nl/xapi/extensions/navigateValue": navigateValue }));
};
//#endregion Navigate

//#region SpeachRequest
var SpeachRequest = Bao.SpeachRequest = function (cfg) {
    //this.id = "Custom.Speach.Request"; //"http://bao.mijnklas.nl/xapi/activities/speak-request";
    this['http://bao.mijnklas.nl/xapi/extensions/textToPronounce'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/cultureCode'] = null;

    this.init(cfg);
};
SpeachRequest.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['http://bao.mijnklas.nl/xapi/extensions/textToPronounce', 'http://bao.mijnklas.nl/xapi/extensions/cultureCode']);
    }
};
Bao.buildSpeachRequestActivity = function (textToPronounce, cultureCode) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/speak-request", new Bao.SpeachRequest({ "http://bao.mijnklas.nl/xapi/extensions/textToPronounce": textToPronounce, "http://bao.mijnklas.nl/xapi/extensions/cultureCode": cultureCode }));
};
//#endregion SpeachRequest

//#region Tease
var Tease = Bao.Tease = function (cfg) {
    this['http://bao.mijnklas.nl/xapi/extensions/teaser'] = null;
    this['http://bao.mijnklas.nl/xapi/extensions/playerId'] = null;

    this.init(cfg);
};
Tease.prototype = {
    init: function (cfg) {
        //copyProperties(cfg, this, ['tease', 'playerId']);
        Bao.copyAndRenameProperty(cfg, this, 'tease', 'http://bao.mijnklas.nl/xapi/extensions/teaser');
        Bao.copyAndRenameProperty(cfg, this, 'playerId', 'http://bao.mijnklas.nl/xapi/extensions/playerId');
    }
};
Bao.buildTeaseActivity = function (teaseType, playerId) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/verbs/teaser-" + teaseType, new Bao.Tease({ "tease": teaseType, "playerId": playerId }));
};
//#endregion Tease

//#region VideoRequest
var VideoRequest = Bao.VideoRequest = function (cfg) {
    //this.id = "Custom.Video.Request"; //"http://bao.mijnklas.nl/xapi/activities/video-request";
    this.uniqueId = null;

    this.init(cfg);
};
VideoRequest.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['uniqueId']);
    }
};
Bao.buildVideoRequestActivity = function (uniqueId) {
    return Bao.buildActivityFromExtension("http://bao.mijnklas.nl/xapi/activities/video-request", new Bao.VideoRequest({ "uniqueId": uniqueId }));
};
//#endregion VideoRequest

//#region VideoResponse
var VideoResponse = Bao.VideoResponse = function (cfg) {
    //this.id = "Custom.Video.Response"; //"http://bao.mijnklas.nl/xapi/activities/video-response";
    this.uniqueId = null;
    this.objectType = null;
    this.mimeType = null;
    this.video = null;

    this.init(cfg);
};
VideoResponse.prototype = {
    init: function (cfg) {
        this.objectType = 'video';
        this.mimeType = 'video/mp4';
        this.video = { url: null };
        Bao.copyProperties(cfg, this, ['uniqueId']);

        if (cfg.hasOwnProperty('url') && cfg.url !== null) {
            this.video.url = cfg.url;
        }
        else if (cfg.hasOwnProperty('video') && cfg.video !== null) {
            Bao.copyProperties(cfg.video, this.video, ['url']);
        }
    }
};
Bao.buildVideoResponseResult = function (duration, uniqueId, url, mimeType, objectType) {
    var response = { "uniqueId": uniqueId, "url": url };

    if (mimeType && mimeType !== null) {
        response.mimeType = mimeType;
    }

    if (objectType && objectType !== null) {
        response.objectType = objectType;
    }

    return Bao.buildResultFromExtension(duration, new Bao.VideoResponse(response));
};
//#endregion VideoResponse
//#endregion Verb-Types

//#region Extension-Types
//#region QuestionSet
var QuestionSet = Bao.QuestionSet = function (cfg) {
    this.objectType = null;
    this["http://bao.mijnklas.nl/xapi/verbs/questionSet"] = null;

    this.init(cfg);
};
QuestionSet.prototype = {
    init: function (cfg) {
        this.objectType = "http://bao.mijnklas.nl/xapi/verbs/questionSet";
        this["http://bao.mijnklas.nl/xapi/verbs/questionSet"] = [];

        Bao.mapOrWrap(cfg, this, undefined, Bao.Question, function (prop) { return new Bao.Question(prop); }, 'http://bao.mijnklas.nl/xapi/verbs/questionSet');
    }/*,
	toJSON: function () {
		return this.questions;
	}*/
};
Bao.buildQuestionSetResult = function (questions) {
    return Bao.buildResultFromExtension(null, new Bao.QuestionSet(questions));
};
//#endregion QuestionSet

//#region Question
var Question = Bao.Question = function (question) {
    this.id = "http://adlnet.gov/expapi/activities/question";
    this.uniqueId = null;
    this.value = null;

    this.init(question);
};
Question.prototype = {
    init: function (question) {
        Bao.copyProperties(question, this, ['uniqueId', 'value']);
    }
};
Bao.buildQuestion = function (question) {
    return new Bao.Question(question);
};
//#endregion Question

//#region Size
var Size = Bao.Size = function (size) {
    this.width = null;
    this.height = null;
    this.init(size);
};
Size.prototype = {
    init: function (size) {
        Bao.copyProperties(size, this, ['width', 'height']);
    }
};
//#endregion Size

//#region JSonExtension
var JSonExtension = Bao.JSonExtension = function (json) {
    this["http://bao.mijnklas.nl/xapi/extensions/objectType"] = null;
    this["http://bao.mijnklas.nl/xapi/extensions/json"] = null;

    this.init(json);
};
JSonExtension.prototype = {
    init: function (json) {
        this["http://bao.mijnklas.nl/xapi/extensions/objectType"] = "http://bao.mijnklas.nl/xapi/extensions/json";
        this["http://bao.mijnklas.nl/xapi/extensions/json"] = JSON.stringify(json);
    }
};
//#endregion JSonExtension

//#region JSonArrayExtension
var JSonArrayExtension = Bao.JSonArrayExtension = function (jsonArray) {
    this["http://bao.mijnklas.nl/xapi/extensions/objectType"] = null;
    this["http://bao.mijnklas.nl/xapi/extensions/jsonArray"] = null;

    this.init(jsonArray);
};
JSonArrayExtension.prototype = {
    init: function (jsonArray) {
        this["http://bao.mijnklas.nl/xapi/extensions/objectType"] = "http://bao.mijnklas.nl/xapi/extensions/jsonArray";
        this["http://bao.mijnklas.nl/xapi/extensions/jsonArray"] = jsonArray;
    }
};
//#endregion JSonArrayExtension

//#region AvatarData
var AvatarData = Bao.AvatarData = function (cfg) {
    this.assets = null;
    this.skinColor = null; // Only used for backwards compatibility

    this.init(cfg);
};
AvatarData.prototype = {
    init: function (cfg) {
        this.assets = [];
        this.skinColor = "";

        Bao.copyProperties(cfg, this, ['assets', 'skinColor']);
    }
};
//#endregion AvatarData

//#region AssetCollection
var AssetCollection = Bao.AssetCollection = function (cfg) {
    this.assets = null;

    this.init(cfg);
};
AssetCollection.prototype = {
    init: function (cfg) {
        this.assets = [];

        Bao.mapOrWrap(cfg, this, undefined, Bao.AssetBase, function (prop) { return new Bao.AssetBase(prop); }, 'assets');
    },
    toJSON: function () {
        return this.assets;
    }
};
//#endregion AssetCollection

//#region AssetBase
var AssetBase = Bao.AssetBase = function (cfg) {
    this.assetName = null;
    this.assetType = null;
    this.init(cfg);
};
AssetBase.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['assetName', 'assetType']);
        switch (this.assetType) {
            case "http://bao.mijnklas.nl/xapi/extensions/image":
                this["http://bao.mijnklas.nl/xapi/extensions/image"] = new ImageAsset(cfg);
                break;
            case "http://bao.mijnklas.nl/xapi/extensions/sprite":
                this["http://bao.mijnklas.nl/xapi/extensions/sprite"] = new SpriteAsset(cfg);
                break;
            case "http://bao.mijnklas.nl/xapi/extensions/animationsprite":
                this["http://bao.mijnklas.nl/xapi/extensions/animationsprite"] = new AnimationSpriteAsset(cfg);
                break;
            default:
                throw { Message: "Unknown assetType", Config: cfg };
                break;
        }
    }
};
//#endregion AssetBase

//#region ImageAsset
var ImageAsset = Bao.ImageAsset = function (cfg) {
    this.url = null;
    this.size = null;
    this.init(cfg);
};
ImageAsset.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['url']);

        Bao.mapOrWrap(cfg, this, 'size', Bao.Size, function (prop) { return new Bao.Size(prop); });
    }
};
Bao.createImageAsset = function (cfg) {
    Bao.addPropertyIfMissing(cfg, 'assetName', 'NoAssetName');
    Bao.addPropertyIfMissing(cfg, 'assetType', 'http://bao.mijnklas.nl/xapi/extensions/image');
    return new Bao.AssetBase(cfg);
};
//#endregion ImageAsset

//#region AnimationSpriteFrames
var AnimationSpriteFrames = Bao.AnimationSpriteFrames = function (cfg) {
    this.width = null;
    this.height = null;
    this.count = null;
    this.init(cfg);
};
AnimationSpriteFrames.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['width', 'height', 'count']);
    }
};
//#endregion AnimationSpriteFrames

//#region AnimationSpriteAsset
var AnimationSpriteAsset = Bao.AnimationSpriteAsset = function (cfg) {
    this.url = null;
    this.size = null;
    this.frames = null;
    this.init(cfg);
};
AnimationSpriteAsset.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['url']);

        Bao.mapOrWrap(cfg, this, 'size', Bao.Size, function (prop) { return new Bao.Size(prop); });
        Bao.mapOrWrap(cfg, this, 'frames', Bao.AnimationSpriteFrames, function (prop) { return new Bao.AnimationSpriteFrames(prop); });
    }
};
Bao.createAnimationSpriteAsset = function (cfg) {
    Bao.addPropertyIfMissing(cfg, 'assetName', 'NoAssetName');
    Bao.addPropertyIfMissing(cfg, 'assetType', 'http://bao.mijnklas.nl/xapi/extensions/animationsprite');
    return new Bao.AssetBase(cfg);
};
//#endregion AnimationSpriteAsset

//#region SpriteFrame
var SpriteFrame = Bao.SpriteFrame = function (cfg) {
    this.name = null;
    this.w = null;
    this.h = null;
    this.x = null;
    this.y = null;
    this.z = null;
    this.bgx = null;
    this.bgy = null;
    this.init(cfg);
};
SpriteFrame.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['name', 'w', 'h', 'x', 'y', 'z', 'bgx', 'bgy']);
    }
};
//#endregion SpriteFrame

//#region SpriteCompositionComponent
var SpriteCompositionComponent = Bao.SpriteCompositionComponent = function (cfg) {
    this.name = null;
    this.x = null;
    this.y = null;
    this.z = null;
    this.init(cfg);
};
SpriteCompositionComponent.prototype = {
    init: function (cfg) {
        Bao.copyProperties(cfg, this, ['name', 'x', 'y', 'z']);
    }
};
//#endregion SpriteCompositionComponent

//#region SpriteComposition
var SpriteComposition = Bao.SpriteComposition = function (cfg) {
    this.name = null;
    this.components = null;
    this.init(cfg);
};
SpriteComposition.prototype = {
    init: function (cfg) {
        this.components = [];
        Bao.copyProperties(cfg, this, ['name']);

        Bao.mapOrWrap(cfg, this, 'components', Bao.SpriteCompositionComponent, function (prop) { return new Bao.SpriteCompositionComponent(prop); });
    }
};
//#endregion SpriteComposition

//#region SpriteAsset
var SpriteAsset = Bao.SpriteAsset = function (cfg) {
    this.url = null;
    this.size = null;
    this.frames = null;
    this.metadataUrl = null;
    this.cssUrl = null;
    this.compositions = null;
    this.init(cfg);
};
SpriteAsset.prototype = {
    init: function (cfg) {
        this.frames = [];
        this.compositions = [];
        Bao.copyProperties(cfg, this, ['url', 'metadataUrl', 'cssUrl']);

        Bao.mapOrWrap(cfg, this, 'size', Bao.Size, function (prop) { return new Bao.Size(prop); });
        Bao.mapOrWrap(cfg, this, 'frames', Bao.SpriteFrame, function (prop) { return new Bao.SpriteFrame(prop); });
        Bao.mapOrWrap(cfg, this, 'compositions', Bao.SpriteComposition, function (prop) { return new Bao.SpriteComposition(prop); });
    }
};
Bao.createSpriteAsset = function (cfg) {
    Bao.addPropertyIfMissing(cfg, 'assetName', 'NoAssetName');
    Bao.addPropertyIfMissing(cfg, 'assetType', 'http://bao.mijnklas.nl/xapi/extensions/sprite');
    return new Bao.AssetBase(cfg);
};
//#endregion SpriteAsset
//#endregion Extension-Types
//#endregion Types

function capitalizeFirstLetter(text) {
    return text.charAt(0).toUpperCase() + text.slice(1);
}