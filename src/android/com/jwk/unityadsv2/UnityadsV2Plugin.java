package com.jwk.unityadsv2;

import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.log.DeviceLog;
import com.unity3d.ads.metadata.MediationMetaData;
import com.unity3d.ads.metadata.MetaData;
import com.unity3d.ads.metadata.PlayerMetaData;
import com.unity3d.ads.misc.Utilities;
import com.unity3d.ads.properties.SdkProperties;

/**
 * Created by JWK on 2016. 8. 26..
 */
public class UnityadsV2Plugin extends CordovaPlugin {

    private String interstitialPlacementId;
    private String incentivizedPlacementId;

    public CallbackContext eventCallbackContext;

    public boolean playing = false;
    public String playingPlacementId = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i("chromium","UnityAdsV2Plugin/execute/"+action);
        if(action.equals("setUp"))
            setUp(args.getString(0),args.getBoolean(1),callbackContext);
        else if(action.equals("isSetUp"))
            isSetUp(callbackContext);
        else if(action.equals("isReadyVideo"))
            isReadyVideo(callbackContext);
        else if(action.equals("showVideo"))
            showVideo(callbackContext);
        else if(action.equals("isReadyRewardVideo"))
            isReadyRewardVideo(callbackContext);
        else if(action.equals("showRewardVideo"))
            showRewardVideo(callbackContext);
        else {
            Log.i("chromium", "UnityAdsV2Plugin/UnknownAction/" + action);
            return false;
        }
        return true;
    }

    public void setUp(final String gameId, final boolean testMode,final CallbackContext callbackContext){
        try{
            eventCallbackContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if(!SdkProperties.isInitialized()) {
                        final UnityAdsListener unityAdsListener = new UnityAdsListener();
                        UnityAds.setListener(unityAdsListener);
                        UnityAds.setDebugMode(false);

                        MediationMetaData mediationMetaData = new MediationMetaData(cordova.getActivity());
                        mediationMetaData.setName("mediationPartner");
                        mediationMetaData.setVersion("v12345");
                        mediationMetaData.setOrdinal(1);
                        mediationMetaData.commit();

                        MetaData debugMetaData = new MetaData(cordova.getActivity());
                        debugMetaData.set("test.debugOverlayEnabled", false);
                        debugMetaData.commit();
                        //Toast.makeText(cordova.getActivity(), "testtest", Toast.LENGTH_SHORT).show();
                        UnityAds.initialize(cordova.getActivity(), gameId, unityAdsListener, testMode);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void isSetUp(final CallbackContext callbackContext){
        try{
            //cordova.getActivity().runOnUiThread(new Runnable(){
            //  public void run() {
                callbackContext.success(""+(SdkProperties.isInitialized()));
            //  }
            //});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void isReadyVideo(final CallbackContext callbackContext){
        try{
          //cordova.getActivity().runOnUiThread(new Runnable(){
          //  public void run() {
              callbackContext.success(""+(UnityAds.isReady(interstitialPlacementId)));
          //  }
          //});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void show(String placementId){
        if(!playing) {
            playing = true;
            playingPlacementId = placementId;
            PlayerMetaData playerMetaData = new PlayerMetaData(cordova.getActivity());
            playerMetaData.setServerId("rikshot");
            playerMetaData.commit();

            UnityAds.show(cordova.getActivity(), placementId);
        }
    }

    public void showVideo(final CallbackContext callbackContext){
        try{
          cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              if(UnityAds.isReady(interstitialPlacementId)){
                  show(interstitialPlacementId);
              }
            }
          });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void isReadyRewardVideo(final CallbackContext callbackContext){
        try{
          //cordova.getActivity().runOnUiThread(new Runnable(){
          //  public void run() {
              callbackContext.success(""+(UnityAds.isReady(incentivizedPlacementId)));
          //  }
          //});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showRewardVideo(CallbackContext callbackContext){
        try{
          cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              if(UnityAds.isReady(incentivizedPlacementId)){
                show(incentivizedPlacementId);
              }
            }
          });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
	  public void onResume(boolean multitasking) {
      super.onResume(multitasking);
        try{
            sendKeepCallbackResult("onResume","");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendKeepCallbackResult(Object... args) throws Exception{
        PluginResult pr = new PluginResult(PluginResult.Status.OK,new JSONArray(args));
        pr.setKeepCallback(true);
        eventCallbackContext.sendPluginResult(pr);
    }

    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(final String zoneId) {
            DeviceLog.debug("onUnityAdsReady: " + zoneId);
            Utilities.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (zoneId.equals("video") ||
                                zoneId.equals("defaultZone") ||
                                zoneId.equals("defaultVideoAndPictureZone")) {
                            interstitialPlacementId = zoneId;
                            /* video ready */
                            sendKeepCallbackResult("onUnityReady", zoneId);
                        } else if (zoneId.equals("rewardedVideo") ||
                                zoneId.equals("rewardedVideoZone") ||
                                zoneId.equals("incentivizedZone")) {
                            incentivizedPlacementId = zoneId;
                            /* rewarded video ready */
                            sendKeepCallbackResult("onUnityReady", zoneId);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onUnityAdsStart(String zoneId) {
            try{
                playing = true;
                DeviceLog.debug("onUnityAdsStart: " + zoneId);
                sendKeepCallbackResult("onUnityAdsStart",playingPlacementId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUnityAdsFinish(String zoneId, UnityAds.FinishState result) {
            try{
                playing = false;
                DeviceLog.debug("onUnityAdsFinish: " + zoneId + " - " + result);
                sendKeepCallbackResult("onUnityAdsFinish",playingPlacementId);
                playingPlacementId = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
            try {
                playing = false;
                DeviceLog.debug("onUnityAdsError: " + error + " - " + message);
                sendKeepCallbackResult("onUnityAdsError", playingPlacementId);
                playingPlacementId = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
