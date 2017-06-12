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
    public static void authenticateUser(Activity activity,final IntegrationCallback callback){
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
        auth.authenticateUserInit();
    }

    public static void enrolUser(Activity act,final IntegrationCallback callback){
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
        Intent intent = new Intent("mybankauthenticator.fasyl.com.mybankauthenticator.aimbrainAuthentication.FacialEnrolment");
        act.startActivity(intent);
    }
}


