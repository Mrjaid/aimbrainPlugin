package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;

/**
 * Created by Mr Jaid on 6/1/2017.
 */

public class IntegrationInterface {
    static IntegrationCallback authoCallback;
    static String userId;
    static Context context;
    static Activity act;
    public static void authenticateUser(Activity activity,final IntegrationCallback callback,String apiKey,String APISecret){
        authoCallback=new IntegrationCallback() {
            @Override
            public void onSuccess(int authResult) {
                authoCallback = null;
                callback.onSuccess(authResult);
            }

            @Override
            public void onFailure(String error) {
                authoCallback = null;
                callback.onFailure(error);
            }
        };
        act=activity;
        FacialAuthentication auth = new FacialAuthentication();
        auth.authenticateUserInit(apiKey,APISecret);
    }

    public static void authenticateImage(Activity activity,final IntegrationCallback callback,String apiKey,String APISecret,String base64){
        authoCallback=new IntegrationCallback() {
            @Override
            public void onSuccess(int authResult) {
                authoCallback = null;
                callback.onSuccess(authResult);
            }

            @Override
            public void onFailure(String error) {
                authoCallback = null;
                callback.onFailure(error);
            }
        };
        act=activity;
        ImageAuthentication auth = new ImageAuthentication();
        auth.authenticateUserInit(apiKey,APISecret,base64);
    }

    public static void enrolUser(Activity activity,final IntegrationCallback callback,String apiKey,String APISecret){
        authoCallback=new IntegrationCallback() {
            @Override
            public void onSuccess(int authResult) {
                authoCallback = null;
                callback.onSuccess(authResult);
            }

            @Override
            public void onFailure(String error) {
                authoCallback = null;
                callback.onFailure(error);
            }
        };
        act=activity;
        FacialEnrollment enrol = new FacialEnrollment();
        enrol.initEnrolment(apiKey,APISecret);
    }
}


