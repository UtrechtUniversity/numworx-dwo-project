CES = {
	"response":	'',
	
	"setResponse": 
		function(data) {
			console.log("setResponse: " + data);
			alert("setResponse: " + data);
			this.reponse = data;
	},

	"getResponse": 
		function() {
			console.log("getResponse: " + this.response);
			return this.response;
	},
	
}

window.CES = CES;
