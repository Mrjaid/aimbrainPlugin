package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.aimbrain.sdk.Manager;
import com.aimbrain.sdk.exceptions.InternalException;
import com.aimbrain.sdk.exceptions.SessionException;
import com.aimbrain.sdk.faceCapture.VideoFaceCaptureActivity;
import com.aimbrain.sdk.models.FaceAuthenticateModel;
import com.aimbrain.sdk.models.SessionModel;
import com.aimbrain.sdk.server.FaceCapturesAuthenticateCallback;
import com.aimbrain.sdk.server.SessionCallback;
import com.android.volley.VolleyError;

import java.net.ConnectException;



public class FacialAuthentication{
    int videoRequestCode;
    Manager manager;
    IntegrationCallback authenticationCallback= null;

    public void authenticateUserInit() {
        if(IntegrationInterface.authoCallback!=null){
            authenticationCallback = IntegrationInterface.authoCallback;
        }
        initAimbrainSessionAndStartCapture(IntegrationInterface.userId, IntegrationInterface.context);
    }




    private void initAimbrainSessionAndStartCapture(String userId,Context context){
        try {
            System.out.println("about to create session context  is"+context);
            manager =  Manager.getInstance();
            manager.configure("b0398393-1039-4100-acdc-e0fe7a8bf9d0", "89bbb313760f4e58a02fbc02343d6413335da9d69f72475087e81148e3815521");
            manager.createSession(userId,context,new SessionCallback(){
                @Override
                public void onSessionCreated(SessionModel session) {
                    startCapture();
                }
            });
        } catch (InternalException e) {
            authenticationCallback.onFailure(e.getMessage());
            // e.printStackTrace();
        } catch (ConnectException e) {
            authenticationCallback.onFailure(e.getMessage());
            // e.printStackTrace();
        }
    }

    private void startCapture(){
        Intent intent = new Intent(IntegrationInterface.act, VideoFaceCaptureActivity.class);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_UPPER_TEXT, "Authenticate Me");
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_LOWER_TEXT, "Please Position your face within the Frame");
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_DURATION_MILLIS, 2000);
        intent.putExtra(VideoFaceCaptureActivity.EXTRA_RECORDING_HINT, "Please blink your eye now");
        videoRequestCode = new FacialEnrollment().getNewRequestId();
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

    protected void processResult(int requestCode, int resultCode, Intent data) {
       // IntegrationInterface.act.onActivityResult(requestCode, resultCode, data);
       // if(requestCode == videoRequestCode && resultCode == RESULT_OK){
            authenticateUser();
       // }
    }

    private void authenticateUser(){
        try {
            System.out.println("manager is "+manager);
            manager.sendProvidedFaceCapturesToAuthenticate(VideoFaceCaptureActivity.video, new FaceCapturesAuthenticateCallback() {

                @Override
                public void success(FaceAuthenticateModel faceAuthenticateModel) {
                    if(authenticationCallback!=null){
                        if(isValidFacialData(faceAuthenticateModel)) {
                            authenticationCallback.onSuccess(1);
                        }else{
                            authenticationCallback.onSuccess(0);
                        }
                        authenticationCallback=null;
                    }
                }
                @Override
                public void failure(VolleyError volleyError) {
                    if(authenticationCallback!=null){
                        authenticationCallback.onFailure(volleyError.getMessage());
                    }
                }
            });
        } catch (InternalException e) {
            authenticationCallback.onFailure(e.getMessage());
        } catch (ConnectException e) {
            authenticationCallback.onFailure(e.getMessage());
        } catch (SessionException e) {
            authenticationCallback.onFailure(e.getMessage());
        }
    }











    private String getVerdict(FaceAuthenticateModel faceAuthenticateModel){
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  0.9;
        Double livinessPassScore =  1.0;
        String verdict = ((Double.compare(faceScore,facePassScore)>=0) && (Double.compare(liviness,livinessPassScore)==0))? "Passed Validation" : "Failed Validation";
        verdict = ((faceScore >=facePassScore) &&  (Double.compare(liviness,livinessPassScore)!=0))? verdict + " (Try blinking your eye after pressing the capture button). " : verdict;
        return verdict;
    }

    private boolean isValidFacialData(FaceAuthenticateModel faceAuthenticateModel){
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  0.9;
        Double livinessPassScore =  1.0;
        return ((Double.compare(faceScore,facePassScore)>=0) && (Double.compare(liviness,livinessPassScore)==0));

    }

    private String getHeaderText(FaceAuthenticateModel faceAuthenticateModel){
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  7.5;
        Double livinessPassScore =  1.0;
        String verdict = ((Double.compare(faceScore,facePassScore)>=0) && (Double.compare(liviness,livinessPassScore)==0))? "Successful" : "Failed !!!";
        return verdict;
    }

}
