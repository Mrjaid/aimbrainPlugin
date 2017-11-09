package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class aimbrainPlugin extends CordovaPlugin {
    public static IntegrationInterface resultCallBack;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("enrol")) {
            this.enrol(args, callbackContext);
            return true;
        }
        if (action.equals("authenticate")) {
            String userId = args.getString(0);
            this.authenticate(args, callbackContext);
            return true;
        }
        return false;
    }

    private void enrol(JSONArray args,final CallbackContext callbackContext) throws JSONException {
        String userId = args.getString(0);
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
            IntegrationInterface.enrolUser(act,callback,args.getString(1),args.getString(2));
            cordova.setActivityResultCallback(this);
        } else {
            callbackContext.error("userId expected.");
        }
    }

    private void authenticate(JSONArray args,final CallbackContext callbackContext) throws JSONException {
        String userId = args.getString(0);
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
            IntegrationInterface.authenticateUser(act,callback,args.getString(1),args.getString(2));
            cordova.setActivityResultCallback(this);
        } else {
            callbackContext.error("userId expected.");
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        cordova.setActivityResultCallback(this);
        resultCallBack.onActivityResult(requestCode,resultCode,intent);
    }
}
