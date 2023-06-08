


    function logMessage(message, source) {
        var log = document.getElementById('log');
        var pre = document.createElement('pre');
        pre.className = "list-group-item";

        pre.textContent = JSON.stringify(message, null, 2).replace('\r', '<br />');

        var entry = document.createElement('div');
        var header = document.createElement('h4');
        if (source == 'content')
            header.textContent = 'Message from content ' + new Date().toGMTString();
        else
            header.textContent = 'Log entry ' + new Date().toGMTString();
        entry.appendChild(header);
        entry.appendChild(pre);
        log.insertBefore(entry, log.firstElementChild);
    }

function clearLog() {
    var log = document.getElementById('log');
    log.innerHTML = "";
}

// Simplified version of Lms_Api.js, used to display statements on the page
var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
var eventer = window[eventMethod];
var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

eventer(messageEvent, handleMessage, false);

function handleMessage(msg) {

    logMessage(JSON.parse(msg.data), "content");

    if (msg.data.indexOf("verbs/metaData") >= 0) { 

        getMetadata(context).done(function(data) {
            sendMessageToTestpage(data)
        });
    }
    if (msg.data.indexOf("verbs/content") >= 0) { 

        var context = $("#SelectedContext").val();
        if (context.length == 0) {
            logMessage("Error: Context not set", "parent");
        }
        else {
            getContent(context).done(function(data) {
                sendMessageToTestpage(data)
            });
        }
    }
    if (msg.data.indexOf("verbs/answered") >= 0) { 
        // validate answered
        validateAnswered(msg.data).done(function(data) {
            logMessage(data, "parent")
        });
    }

    if (msg.data.indexOf("verbs/context") >= 0) {
        // validate answered
        logMessage(msg.data, "parent");
        var userrole = $("#userrole").val();
        var contentmode = $("#contentmode").val();


        var text = [{ "verb": { "id": "http://bao.mijnklas.nl/xapi/verbs/context" }, "object": { "objectType": "Activity", "id": "http://bao.mijnklas.nl/xapi/activities/get-context-response" }, "result": { "duration": "PT0S", "extensions": { "http://bao.mijnklas.nl/xapi/extensions/jsonArray": [{ "name": "user.role", "value": userrole }, { "name": "content.mode", "value": contentmode }] } }, "version": "1.0.1" }];
        sendMessageToTestpage(JSON.stringify(text));
    }

    var index = msg.data.indexOf("verbs/moduleData");
    if (index >= 0) {
        var xapi = JSON.parse(msg.data);

        for (index = 0; index < xapi.length; ++index) {
            if (xapi[index].verb.id.indexOf("moduleData") > 0) {
                var htmlMessage = xapi[index].object.definition.extensions["http://bao.mijnklas.nl/xapi/extensions/json"];
                $("#moduledata").val(htmlMessage);
            };
        }       
    }   
}

function getMetadata() {
    var d = $.Deferred();

    $.get(
        baseUrl + 'api/MetaData',
        function(data) {
            //return data;
        })
        .done(function (data) {
            if (data == null)
                data = '{}';
            d.resolve(data);
        });
    return d.promise();
}

function getLtaMetadata(ltaId) {
    var d = $.Deferred();

    $.get(
        baseUrl + 'api/MetaData/lta/' + ltaId,
        function(data) {
            //return data;
        })
        .done(function (data) {
            if (data == null)
                data = '{}';
            d.resolve(data);
        });
    return d.promise();
}

function getContent(context) {
    var d = $.Deferred();

    $.get(
        baseUrl + 'api/Content/' + context,
        function(data) {
            //return data;
        })
        .done(function (data) {
            if (data == null)
                data = '{}';
            d.resolve(data);
        });
    return d.promise();
}

function getLtaContent(ltaId) {
    var d = $.Deferred();

    $.get(
        baseUrl + 'api/Content/lta/' + ltaId,
        function(data) {
            //return data;
        })
        .done(function (data) {
            if (data == null)
                data = '{}';
            d.resolve(data);
        });
    return d.promise();
}

function validateAnswered(message) {
    var d = $.Deferred();

    $.ajax({
        url: baseUrl + 'api/Validate',
        type: 'POST',
        data: message,
        contentType: "application/json;charset=utf-8",
        success: function (data) {
            //return data;
        },
        error: function (x, y, z) {
        }
    })
    .done(function (data) {
        if (data == null)
            data = '{}';
        d.resolve(data);
    });

    return d.promise();
}

function sendMessageToTestpage(message)
{
    var win = document.getElementById("iframe");
    win.contentWindow.postMessage(message, "*");
}

$(document).ready(function(){

    $('.numbersOnly').keyup(function () {
        if (this.value != this.value.replace(/[^0-9\.]/g, '')) {
            this.value = this.value.replace(/[^0-9\.]/g, '');
        }
    });

    $("#ltaButton").click(function(){
        var ltaId = $("#ltaId").val();
        var tables = $("#tableInput").val();

        if (ltaId || ltaId.length > 0) {
            //$("#SelectedContext").val("0");

            getLtaMetadata(ltaId).done(function(data) {
                sendMessageToTestpage(data)
            });                

            getLtaContent(ltaId).done(function (data) {
                sendMessageToTestpage(data)
            });                
        }
    });
}); 

