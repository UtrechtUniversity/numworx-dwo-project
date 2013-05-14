// =========================================================
// AssetAPI class
//  5-4-2009
var state = '';
var assetAPI = {
	// asset API redirect to parent
	SetInitialized : function(GUID, Initialized) {
		return parent.assetAPI.SetInitialized(GUID, Initialized)
	},
	SetException : function(GUID, Fatal, ErrNum, Details) {
		parent.assetAPI.SetException(GUID, Fatal, ErrNum, Details);
	},
	GetUserId : function() {
		return parent.assetAPI.GetUserId();		
	},
	GetAssetData : function(GUID) {
		//alert('GetAssetData');
		return parent.assetAPI.GetAssetData(GUID);
	},
	SetAssetData : function(GUID, Value) {
		parent.assetAPI.SetAssetData(GUID, Value);
	},
	GetScore : function(GUID) {
	     //alert('GetScore');
		return parent.assetAPI.GetScore(GUID);
	},
	SetScore : function(GUID, Score) {
		parent.assetAPI.SetScore(GUID, Score);
	},
	GetResource : function(GUID, Resource) {
		return "../" + parent.assetAPI.GetResource(GUID,Resource);
	}
}

function SetInitialized(GUID, Initialized)
{	
	//alert("Initialized "+GUID);
	return assetAPI.SetInitialized(GUID, Initialized);
}
function SetScore(GUID, Score)
{	//alert("SetScore "+Score);
	return assetAPI.SetScore(GUID, Score);
}
function SetAssetData(GUID, Data)
{	//alert("SetAssetData ");
	state = Data;
	return assetAPI.SetAssetData(GUID, Data);
}
function GetAssetData(GUID)
{	
	//alert("LoadAssetData");
	return assetAPI.GetAssetData(GUID);
}

// Missing?
function SetCompleted(GUID, Completed)
{	//alert("SetCompleted (dummy) "+Completed);
	return "";
}
