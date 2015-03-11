    /* 
     * Security Manager Callbacks 
     */

    function onMHPublish(topic, data, publishContainer, subscribeContainer) {
      /* Callback for publish requests. This example approves all publish requests. */
      return true;
    }
    function onMHSubscribe(topic, container) {
      /* Callback for subscribe requests. This example approves all subscribe requests. */ 
      return true;
    }
    function onMHUnsubscribe(topic, container) {
      /* Callback for unsubscribe requests. This example approves all subscribe requests. */ 
      return true;
    }
    function onMHSecurityAlert(source, alertType) { 
    	alert(source + " " + alertType)
    /* Callback for security alerts */  }

    function onClientSecurityAlert(source, alertType) { 
    	alert(source + " " + alertType)
    /* Handle client-side security alerts */  }
    function onClientConnect(container) {       
    	console.log("connecting " + container)
    /* Called when client connects */   }
    function onClientDisconnect(container) {     /* Called when client disconnects */ }

    function logger(msg) {
    	console.log(msg)
    }
    /* 
     * Create a Managed Hub instance 
     */

    var managedHub = new OpenAjax.hub.ManagedHub(
          { 
            onPublish:       onMHPublish,
            onSubscribe:     onMHSubscribe,
            onUnsubscribe:   onMHUnsubscribe,
            onSecurityAlert: onMHSecurityAlert 
          }
    );

    function createIframe(id, w, h, l, r) {
    	return new OpenAjax.hub.IframeContainer(managedHub , id,
    	        {
    	          Container: {
    	            onSecurityAlert: onClientSecurityAlert,
    	            onConnect:       onClientConnect,
    	            onDisconnect:    onClientDisconnect,
    	            log: 			 logger
    	          },
    	          IframeContainer: {
    	            // DOM element that is parent of this container:
    	            parent:      document.getElementById(id), 
    	            // Container's iframe will have these CSS styles:
    	            iframeAttrs: { width: w, height: h, style: { border:"black solid 1px" }},
    	            // Container's iframe loads the following URL:
    	            uri: "MCSquared.html?w=" + id + "&locale=" + l,
    	            timeout: 1000000,
    	            //tunnelURI:  "http://localhost:8888/scripts/rpc_relay.html",
    	            clientRelay: r
   	          }
    	        }
    	      );
    }
 
    // Handle security alerts:
    function client1SecurityAlertHandler(source, alertType) {
    }

    // Callback called when a subscription receives data
    function onData(topic, publisherData, subscriberData) {
      
        var messageArea = document.getElementById('messageArea');
        // XSS protection: createTextNode strips HTML markup
        var text = document.createTextNode(" " + topic + " " + JSON.stringify( publisherData ));
  	  //messageArea.innerHTML = ""; 
  	  messageArea.appendChild(text);
  	  messageArea.appendChild(document.createElement('br'));
    }

    var scores = {} 
    var total = 0
    function onCheck(topic, publisherData, subscriberData) {
    	var s = publisherData.parameters.score
    	var xwid = publisherData.source
    	scores[xwid] = s;
    	total = 0
    	for (xwid in scores) {
    		if (scores.hasOwnProperty(xwid)) {
    			total += scores[xwid]
    		}
    	}
    	console.log("total = " + total)
    	document.getElementById('totalspan').innerHTML = "" + total;
    }
    
    
    
    var hubclient1
    function onBoot(topic, xwid) {
    	this.publish(xwid + ".setState", window.cmi.suspend_data[xwid]|| {})
    }
    
    
    /* 
     * Create a OpenAjax.hub.InlineHubClient
     */
    /* Create an InlineContainer for this HubClient */

    window.container1 = new OpenAjax.hub.InlineContainer(managedHub , "client1",
      {
        Container: {
          onSecurityAlert: onClientSecurityAlert,
          onConnect:       onClientConnect,
          onDisconnect:    onClientDisconnect
        }
      }
    );

    window.hubclient1 = new OpenAjax.hub.InlineHubClient({
      HubClient: {
        onSecurityAlert: client1SecurityAlertHandler
      },
      InlineHubClient: {
        container: container1
      }
    });

    // Callback that is invoked when HubClient's attempt to connect
    // to the Managed Hub completes 
    function clientApp1HubClientConnect( hubClient, success, error ) {
      if (success) {
        /* Call hubClient1.publish(...) to publish messages  */
        /* Call hubClient1.subscribe(...) to subscribe to message topics */
        hubclient1.subscribe('**', onData)
        hubclient1.subscribe('boot', onBoot, hubClient)
        hubclient1.subscribe('*.checked', onCheck)
        hubclient1.subscribe('*.changed', onCheck)
      }
    }

    // Connect to the ManagedHub
    hubclient1.connect( clientApp1HubClientConnect );

    
    function doReset() {
    	hubclient1.publish("reset", null)
    }
    function doCheck() {
    	hubclient1.publish("check", null)
    }
    function doStop() {
    	hubclient1.publish("stop", null)
    }