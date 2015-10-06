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
    function onClientDisconnect(container) {    
    	console.log("disconnect " + container)
    }

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

    var bootstrap = {};
    
    function setBootstrap( id, f) {
    	bootstrap[id] = f;
    }
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
    	            iframeAttrs: { width: w, height: h, style: { border:"none" }},
    	            // Container's iframe loads the following URL:
    	            uri: "MCSquared.jsp?w=" + id + "&locale=" + l,
    	            timeout: 1000000,
    	            //tunnelURI:  "http://" + window.location.host + "/scripts/tunnel.html",
    	            clientRelay: r
   	          }
    	        }
    	      );
    }
    function removeIframe(fr)
    {
    	managedHub.removeContainer(fr);
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
    
    function onBoot(topic, xwid) {
    	onBootstrap(topic,xwid );
    	this.publish(xwid + ".setState", window.cmi.suspend_data[xwid]|| {})
    }
    
    function onLogOption(topic, map ) {
    	var xwid = map.source;
    	bootstrap[xwid](topic, map) 
    }
    
    function onBootstrap(topic, xwid) {
    	bootstrap[xwid](topic, {'source': map } );
    }
    
    
    function doReset() {
    	managedHub.publish("reset", null)
    }
    function doCheck() {
    	managedHub.publish("check", null)
    }
    function doStop() {
    	managedHub.publish("stop", null)
    }
    
    managedHub.subscribe('**', onData)
    managedHub.subscribe('boot', onBoot)
    managedHub.subscribe('*.checked', onCheck)
    managedHub.subscribe('*.changed', onCheck)
    managedHub.subscribe('bootstrap', onBootstrap)
    managedHub.subscribe('*.logOption', onLogOption)
