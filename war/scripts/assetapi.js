// =========================================================
// AssetAPI class
//  5-4-2009
// 17-5-2013 breek recursie als parent.assetAPI == this
var state = '';
var assetAPI = {
	// asset API redirect to parent
	SetInitialized : function(GUID, Initialized) {
		if(this != parent.assetAPI)
			return parent.assetAPI.SetInitialized(GUID, Initialized)
		else return "";
	},
	SetException : function(GUID, Fatal, ErrNum, Details) {
		if(this != parent.assetAPI)
		parent.assetAPI.SetException(GUID, Fatal, ErrNum, Details);
	},
	GetUserId : function() {
		if(this != parent.assetAPI)
			return parent.assetAPI.GetUserId();		
		else return "student";
	},
	GetAssetData : function(GUID) {
		//alert('GetAssetData');
		if(this != parent.assetAPI)
			return parent.assetAPI.GetAssetData(GUID);
		else return "";
	},
	SetAssetData : function(GUID, Value) {
		if(this != parent.assetAPI)
			parent.assetAPI.SetAssetData(GUID, Value);
	},
	GetScore : function(GUID) {
	     //alert('GetScore');
		if(this != parent.assetAPI)
			return parent.assetAPI.GetScore(GUID);
		else return "";
	},
	SetScore : function(GUID, Score) {
		if(this != parent.assetAPI)
			parent.assetAPI.SetScore(GUID, Score);
	},
	GetResource : function(GUID, Resource) {
		return "../" + parent.assetAPI.GetResource(GUID,Resource);
	},
	SetCompleted : function(GUID, Completed) {
		if(this != parent.assetAPI)
			return parent.assetAPI.SetCompleted(GUID, Completed);
		return "";
	}
}

function SetInitialized(GUID, Initialized)
{	
	//alert("Initialized "+GUID);
	return assetAPI.SetInitialized(GUID, Initialized);
}
function SetScore(GUID, Score)
{	//alert("SetScore "+Score);
	return assetAPI.SetScore(GUID, Number(Score));
}
function SetAssetData(GUID, Data)
{	//alert("SetAssetData ");
	state = Data;
	Data = LZString.compressToBase64(Data);
	//alert("SetAssetData: size of compressed sample is: " + Data.length + " was " + state.length);
	
	return assetAPI.SetAssetData(GUID, Data);
}
function GetAssetData(GUID)
{	
	state = assetAPI.GetAssetData(GUID);
	if(state.length > 0)
		state = LZString.decompressFromBase64(state);
	//alert("LoadAssetData");
	return state;
}

// Missing?
function SetCompleted(GUID, Completed)
{	//alert("SetCompleted "+Completed);
	return assetAPI.SetCompleted(GUID,Completed);
}
