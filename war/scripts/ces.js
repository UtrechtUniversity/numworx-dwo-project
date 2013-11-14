CES = {
	"parent": null,
	"origin": "*",
	"response":	'',
	
	"setResponse": 
		function(data) {
			if( this.parent != null)
			{
				console.log(data);
				this.parent.postMessage(data, "*");
			}
			this.reponse = data;
	},

	"getResponse": 
		function() {
			console.log("getResponse: " + this.response);
			return this.response;
	},
	
	"responseEvent": 
		function(event) {
			console.log(event);
			this.parent =   event.source;
			this.origin =   event.origin;
			this.response = event.data;
			if(window.setState) 
				window.setState(this.response);
	},
}

function receiveMessage(event) {
	CES.responseEvent(event);
}

// install into window
window.addEventListener("message", receiveMessage, false);
window.CES = CES;

alert("ces.js loaded");
