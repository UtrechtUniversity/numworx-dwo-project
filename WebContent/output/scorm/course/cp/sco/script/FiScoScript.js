var scormApi;
var initialized;

function ScanParentsForApi(win) 
{ 
	var MAX_PARENTS_TO_SEARCH = 500; 
	var nParentsSearched = 0;
	while ( (win.API == null) && 
			(win.parent != null) && (win.parent != win) && 
			(nParentsSearched <= MAX_PARENTS_TO_SEARCH) 
		  )
	{ 
		nParentsSearched++; 
		win = win.parent;
	} 
	return win.API; 
} 

function GetAPI() 
{ 	
	var API = null; 
	if ((window.parent != null) && (window.parent != window)) 
	{ 
		API = ScanParentsForApi(window.parent); 
	} 
	if ((API == null) && (window.top.opener != null))
	{ 
		API = ScanParentsForApi(window.top.opener); 
	} 
	return API;
}

function LMSGetValue(iDataModelElement)
{	if(!initialized)scormApi.LMSInitialize("");
	return scormApi.LMSGetValue(iDataModelElement);
}

function LMSSetValue(iDataModelElement, iValue )
{
	return scormApi.LMSSetValue(iDataModelElement, iValue);
}

function LMSCommit(iParam)
{
	return scormApi.LMSCommit(iParam);
}

function init()
{
	scormApi = GetAPI();

	if (scormApi == null){
		alert("Error finding API instance");
	} else
	{
		scormApi.LMSInitialize("");
		initialized = true;
	}
}

function exit()
{	
	scormApi.LMSSetValue( "cmi.core.lesson_status", "incomplete" );
	scormApi.LMSSetValue("cmi.core.exit","suspend");
	scormApi.LMSCommit("");
	scormApi.LMSFinish("");
}