package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.content.Intent;

import com.android.volley.VolleyError;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class aimbrainPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("enrol")) {
            String userId = args.getString(0);
            this.enrol(userId, callbackContext);
            return true;
        }
        if (action.equals("authenticate")) {
            String userId = args.getString(0);
            this.authenticate(userId, callbackContext);
            return true;
        }
        return false;
    }

    private void enrol(String userId, CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {
            callbackContext.success(userId);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void authenticate(String userId,final CallbackContext callbackContext) {
        if (userId != null && userId.length() > 0) {

            IntegrationCallback callback = new IntegrationCallback() {
                @Override
                public void onSuccess(int authResult) {
                     callbackContext.success(authResult);
                }

                @Override
                public void onFailure(String error) {

                    callbackContext.error(error);

                }
            };
            Activity act = cordova.getActivity();
            IntegrationInterface.userId=userId;
            IntegrationInterface.context = cordova.getActivity().getApplicationContext();
            IntegrationInterface.authenticateUser(act,callback);
            cordova.setActivityResultCallback(this);
        } else {
            callbackContext.error("userId expected.");
        }
        
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
