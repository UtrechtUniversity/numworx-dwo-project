/// <reference path="tincan.js" />
"use strict";

//#region LmsApi
var LmsApi = function (handleMessageCallback, safeHost, safeWindow, initializeCallback) {
	debugger;
	console.warn('Add a second signature to handle plural windows and hosts..!!');
	/// <signature>
	///   <summary>Initialize handling of window.postMessage requests.</summary>
	///   <param name="handleMessageCallback" type="callback">The handler method for postmessage requests.</param>
	///   <param name="safeHost" type="Uri">The Uri of the trusted host.</param>
	///   <param name="safeWindow" type="Window">(Optional) The Window which has the trusted host opened, default 'window.parent' is assumed.</param>
	///   <param name="initializeCallback" type="Bao.Answer">(Optional) Any method that should be fired after initializing.</param>
	/// </signature>
	this.init(handleMessageCallback, safeHost, safeWindow, initializeCallback);
}

LmsApi.prototype = {
	init: function (handleMessageCallback, safeHost, safeWindow, initializeCallback) {
		var svc = this;
		var eventMethod, eventer, messageEvent;
		svc._origin = parent;
		svc._originUrl = safeHost;
		if (safeWindow) {
			svc._origin = safeWindow;
		}

		svc.handleMessageCallback = handleMessageCallback;

		eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
		eventer = window[eventMethod];
		messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

		var handleMessage = function (message) {
			if (message.origin != svc._originUrl) {
				debugger;
				return;
			}
			var msg = JSON.parse(message.data);

			svc.handleMessageCallback(msg);
		};

		eventer(messageEvent, handleMessage, false);

		if (initializeCallback) {
			initializeCallback();
		}
	}
}
//#endregion LmsApi