package com.fasyl.aimbrainplugin.aimbrainPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aimbrain.sdk.Manager;
import com.aimbrain.sdk.exceptions.InternalException;
import com.aimbrain.sdk.exceptions.SessionException;
import com.aimbrain.sdk.faceCapture.PhotoFaceCaptureActivity;
import com.aimbrain.sdk.faceCapture.VideoFaceCaptureActivity;
import com.aimbrain.sdk.models.FaceAuthenticateModel;
import com.aimbrain.sdk.models.FaceEnrollModel;
import com.aimbrain.sdk.models.SessionModel;
import com.aimbrain.sdk.server.FaceCapturesEnrollCallback;
import com.aimbrain.sdk.server.SessionCallback;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.ConnectException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FacialEnrollment extends Activity {
    SessionModel sessionModel;
    String userId;
    int videoRequestCode;
    int photoRequestCode;
    Manager manager;
    public static byte[] video;
    int captureStage=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAimbrainSessionAndStartCapture(userId, this);
    }








    private void videoCapture(){
        Intent intent = new Intent(this, VideoFaceCaptureActivity.class);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_UPPER_TEXT, "Enrolment Capture "+captureStage+" of 5.");
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_LOWER_TEXT, getCaptureText(captureStage));
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_DURATION_MILLIS, 2000);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_RECORDING_HINT, "Please slowly blink both eyes now ...");
        videoRequestCode = getNewRequestId();
        startActivityForResult(intent, videoRequestCode);
    }

    private void photoCapture(){
         Intent intent = new Intent(this, PhotoFaceCaptureActivity.class);
        intent.putExtra("upperText", "Photo Capture");
        intent.putExtra("lowerText", "Please Position your face within the Frame");
        intent.putExtra("recordingHint","Please keep your head still..");
        photoRequestCode = getNewRequestId();
        startActivityForResult(intent, photoRequestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == videoRequestCode && resultCode == RESULT_OK){
            this.video = VideoFaceCaptureActivity.video;
            enrolUser();
        }else if(requestCode == photoRequestCode && resultCode == RESULT_OK){
            videoCapture();
        }
    }


    private void initAimbrainSessionAndStartCapture(String userId,Context context){
        try {
            System.out.println("about to create session userID is"+userId);
            manager =  Manager.getInstance();
            manager.configure("b0398393-1039-4100-acdc-e0fe7a8bf9d0", "89bbb313760f4e58a02fbc02343d6413335da9d69f72475087e81148e3815521");

            manager.createSession(userId,context,new SessionCallback(){
                @Override
                public void onSessionCreated(SessionModel session) {
                    sessionModel = session;
                    videoCapture();
                }
            });
        } catch (InternalException e) {
            initBiometricCapture();
            // e.printStackTrace();
        } catch (ConnectException e) {
            initBiometricCapture();
            // e.printStackTrace();
        }
    }

    private void enrolUser() {
        // Intent intent = new Intent("mybankauthenticator.fasyl.com.mybankauthenticator.activities.FaceLoader");
        //  startActivity(intent);
        try {
            Manager.getInstance().sendProvidedFaceCapturesToEnroll(VideoFaceCaptureActivity.video, new FaceCapturesEnrollCallback() {
                @Override
                public void success(FaceEnrollModel faceEnrollModel) {
                    if(captureStage<5){
                        ++captureStage;
                        videoCapture();
                    }else {
                        handleCaptureSuccess(null);
                    }
                }

                @Override
                public void failure(VolleyError volleyError) {
                    handleFacialCaptureError(null);
                }
            });
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            //   e.printStackTrace();
            initBiometricCapture();
        } catch (SessionException e) {
            initBiometricCapture();
            //  e.printStackTrace();
        }
    }

    private void handleCaptureSuccess(FaceEnrollModel faceEnrollModel){
        // method to send success result back...
    }

    private String getCaptureText(int captureStage){
        switch (captureStage){
            case 1 :{
                return "Please Position your face within the Frame";
            }
            case 2 :{
                return "Please turn your head slightly to the right";
            }
            case 3 :{
                return "Please turn your head slightly to the left";
            }
            case 4 :{
                return "Please turn your head slightly from above";
            }
            case 5 :{
                return "Please turn your head slightly from below";
            }

        }
        return "";
    }

    private void handleFacialCaptureError(VolleyError volleyError){
    // call back plugin
    }

    private String getNextId(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 11) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public int getNewRequestId(){
        SecureRandom random = new SecureRandom();
        return Integer.parseInt(new BigInteger(130, random).toString(5).substring(0, 4));
    }

    public void initHomeScreen(){
        videoCapture();
    }

    public void errorScreenOkButton(View view){
        initBiometricCapture();
    }

    private void initBiometricCapture(){
        videoCapture();
    }

}
