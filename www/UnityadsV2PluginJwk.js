var exec = require('cordova/exec');

var eventCallbackContext = {
  "onResume" : function(){},
  "onUnityAdsStart" : function(msg){},
  "onUnityAdsFinish" : function(msg){},
  "onUnityAdsError" : function(msg){},
  "onUnityReady" : function(msg){},
};

exports.setUp = function(gameId,testMode) {
    exec(
      function(event){
        console.log("setUp succ : "+event);
        if(event[0] == "onResume") eventCallbackContext.onResume();
        else if(event[0] == "onUnityAdsStart") eventCallbackContext.onUnityAdsStart(event[1]);
        else if(event[0] == "onUnityAdsFinish") eventCallbackContext.onUnityAdsFinish(event[1]);
        else if(event[0] == "onUnityAdsError") eventCallbackContext.onUnityAdsError(event[1]);
        else if(event[0] == "onUnityReady") eventCallbackContext.onUnityReady(event[1]);
      }
    , function(event,msg){
      console.log(msg);
    }, "UnityadsV2Plugin", "setUp", [gameId,testMode]);
};

exports.isSetUp = function(succ_func) {
    exec(succ_func, null, "UnityadsV2Plugin", "isSetUp", []);
};

exports.isReadyVideo = function(succ_func) {
    exec(succ_func, null, "UnityadsV2Plugin", "isReadyVideo", []);
};

exports.showVideo = function() {
    exec(null, null, "UnityadsV2Plugin", "showVideo", []);
};

exports.isReadyRewardVideo = function(succ_func) {
    exec(succ_func, null, "UnityadsV2Plugin", "isReadyRewardVideo", []);
};

exports.showRewardVideo = function() {
    exec(null, null, "UnityadsV2Plugin", "showRewardVideo", []);
};

exports.setOnResume = function(tmp_func) {
  eventCallbackContext.onResume = tmp_func;
};

exports.setOnUnityAdsStart = function(tmp_func) {
  eventCallbackContext.onUnityAdsStart = tmp_func;
};

exports.setOnUnityAdsFinish = function(tmp_func) {
  eventCallbackContext.onUnityAdsFinish = tmp_func;
};

exports.setOnUnityAdsError = function(tmp_func) {
  eventCallbackContext.onUnityAdsError = tmp_func;
};

exports.setOnUnityReady = function(tmp_func){
  eventCallbackContext.onUnityReady = tmp_func;
}
