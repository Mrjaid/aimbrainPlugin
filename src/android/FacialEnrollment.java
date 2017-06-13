package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class FacialEnrollment {
    SessionModel sessionModel;
    String userId;
    int videoRequestCode;
    int photoRequestCode;
    Manager manager;
    public static byte[] video;
    int captureStage=1;
    IntegrationCallback authenticationCallback= null;

    protected void initEnrolment(String apiKey,String APISecret) {
        initAimbrainSessionAndStartCapture(IntegrationInterface.userId, IntegrationInterface.context,apiKey,APISecret);

    }


    private void videoCapture(){
        Toast.makeText(IntegrationInterface.context, "about to init video *** capture stage is "+captureStage, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(IntegrationInterface.act, VideoFaceCaptureActivity.class);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_UPPER_TEXT, "Enrolment Capture "+captureStage+" of 5.");
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_LOWER_TEXT, getCaptureText(captureStage));
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_DURATION_MILLIS, 2000);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_RECORDING_HINT, "Please slowly blink both eyes now ...");
        videoRequestCode = getNewRequestId();
        IntegrationInterface.act.startActivityForResult(intent, videoRequestCode);
        aimbrainPlugin.resultCallBack = new ActivityResultCallBackInterFace() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                processResult(requestCode,resultCode,intent);
            }
        };
        if(IntegrationInterface.authoCallback!=null){
            authenticationCallback = IntegrationInterface.authoCallback;
        }
    }

    private void photoCapture(){
        Intent intent = new Intent(IntegrationInterface.act, PhotoFaceCaptureActivity.class);
        intent.putExtra("upperText", "Photo Capture");
        intent.putExtra("lowerText", "Please Position your face within the Frame");
        intent.putExtra("recordingHint","Please keep your head still..");
        photoRequestCode = getNewRequestId();
        IntegrationInterface.act.startActivityForResult(intent, photoRequestCode);
    }

    public void processResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == videoRequestCode){
            this.video = VideoFaceCaptureActivity.video;
            enrolUser();
        }else if(requestCode == photoRequestCode){
            videoCapture();
        }
    }


    private void initAimbrainSessionAndStartCapture(String userId,Context context,String apiKey,String APISecret){
        try {
            System.out.println("about to create session userID is"+userId);
            manager =  Manager.getInstance();
            manager.configure(apiKey,APISecret);

            manager.createSession(userId,context,new SessionCallback(){
                @Override
                public void onSessionCreated(SessionModel session) {
                    sessionModel = session;
                    videoCapture();
                }
            });
        } catch (InternalException e) {
            authenticationCallback.onFailure(e.getMessage());
        } catch (ConnectException e) {
            authenticationCallback.onFailure(e.getMessage());
        }
    }

    private void enrolUser() {
        try {
            Manager.getInstance().sendProvidedFaceCapturesToEnroll(VideoFaceCaptureActivity.video, new FaceCapturesEnrollCallback() {
                @Override
                public void success(FaceEnrollModel faceEnrollModel) {
                    System.out.print("success response gotten for stage "+captureStage);
                    if(captureStage<5){
                        ++captureStage;
                        videoCapture();
                    }else {
                        authenticationCallback.onSuccess(1);
                    }
                }

                @Override
                public void failure(VolleyError volleyError) {
                    System.out.print("failure response gotten for stage "+captureStage);
                    authenticationCallback.onFailure(volleyError.getMessage());
                }
            });
        } catch (InternalException e) {
            System.out.print("exception  gotten for stage "+captureStage);
            authenticationCallback.onFailure(e.getMessage());
        } catch (ConnectException e) {
            System.out.print("exception  gotten for stage "+captureStage);
            authenticationCallback.onFailure(e.getMessage());
        } catch (SessionException e) {
            System.out.print("exception  gotten for stage "+captureStage);
            authenticationCallback.onFailure(e.getMessage());
        }
    }

    private void displayDialog(String message){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(IntegrationInterface.act);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle("");
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                initBiometricCapture();
            }
        });
// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
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
