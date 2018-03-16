package com.fasyl.aimbrainplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import com.aimbrain.sdk.Manager;
import com.aimbrain.sdk.exceptions.InternalException;
import com.aimbrain.sdk.exceptions.SessionException;
import com.aimbrain.sdk.faceCapture.VideoFaceCaptureActivity;
import com.aimbrain.sdk.models.FaceAuthenticateModel;
import com.aimbrain.sdk.models.FaceEnrollModel;
import com.aimbrain.sdk.models.SessionModel;
import com.aimbrain.sdk.server.FaceCapturesAuthenticateCallback;
import com.aimbrain.sdk.server.FaceCapturesEnrollCallback;
import com.aimbrain.sdk.server.SessionCallback;
import com.android.volley.VolleyError;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


public class ImageAuthentication {
    int videoRequestCode;
    Manager manager;
    IntegrationCallback authenticationCallback= null;
    String  base64 = null;

    public void authenticateUserInit(String apiKey,String APISecret,String base64In) {
        base64 = base64In;
        if(IntegrationInterface.authoCallback!=null){
            authenticationCallback = IntegrationInterface.authoCallback;
        }
        initAimbrainSessionAndStartCapture(IntegrationInterface.userId, IntegrationInterface.context,apiKey,APISecret);
    }




    private void initAimbrainSessionAndStartCapture(String userId,Context context,String apiKey,String APISecret){
        try {
            System.out.println("about to create session context  is"+context);
            userId=userId.substring(0,5);
            System.out.println("userId is "+userId);
            System.out.println("API KEY is "+apiKey);
            System.out.println("API Secret is "+APISecret);
            manager =  Manager.getInstance();
            manager.configure(apiKey, APISecret);
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
        enrolUser();
        // }
    }

    private void authenticateUser(){
        try {
            System.out.println("manager is "+manager);
            manager.sendProvidedFaceCapturesToAuthenticate(getBitMapArrayFromBase64(base64), new FaceCapturesAuthenticateCallback() {

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


    private List<Bitmap> getBitMapArrayFromBase64(String encodedImage){
        List<Bitmap>  returnBitMap = new ArrayList<>();
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        System.out.println("done decoding byte ... decoded byte  is "+decodedByte);
        returnBitMap.add(decodedByte);
        return returnBitMap;
    }

    private void enrolUser() {
        try {
            Manager.getInstance().sendProvidedFaceCapturesToEnroll(VideoFaceCaptureActivity.video, new FaceCapturesEnrollCallback() {
                @Override
                public void success(FaceEnrollModel faceEnrollModel) {
                    authenticateUser();
                }

                @Override
                public void failure(VolleyError volleyError) {
                    System.out.print("enrollment error .. "+volleyError.getMessage());
                    authenticationCallback.onFailure(volleyError.getMessage());
                }
            });
        } catch (InternalException e) {
            System.out.print("enrollment error .."+e.getMessage());
            authenticationCallback.onFailure(e.getMessage());
        } catch (ConnectException e) {
            System.out.print("enrollment error .. "+e.getMessage());
            authenticationCallback.onFailure(e.getMessage());
        } catch (SessionException e) {
            System.out.print("enrollment error .."+e.getMessage());
            authenticationCallback.onFailure(e.getMessage());
        }
    }






    private String getVerdict(FaceAuthenticateModel faceAuthenticateModel){
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  0.28;
        Double livinessPassScore =  1.0;
        String verdict = ((Double.compare(faceScore,facePassScore)>=0) && (Double.compare(liviness,livinessPassScore)==0))? "Passed Validation" : "Failed Validation";
        verdict = ((faceScore >=facePassScore) &&  (Double.compare(liviness,livinessPassScore)!=0))? verdict + " (Try blinking your eye after pressing the capture button). " : verdict;
        return verdict;
    }

    private boolean isValidFacialData(FaceAuthenticateModel faceAuthenticateModel){
        System.out.println("getting image verdict.. faceScore is "+ faceAuthenticateModel.getScore());
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  0.28;
        Double livinessPassScore =  1.0;
        return ((Double.compare(faceScore,facePassScore)>=0));

    }

    private String getHeaderText(FaceAuthenticateModel faceAuthenticateModel){
        Double faceScore =  faceAuthenticateModel.getScore();
        Double liviness =  faceAuthenticateModel.getLiveliness();
        Double facePassScore =  0.28;
        Double livinessPassScore =  1.0;
        String verdict = ((Double.compare(faceScore,facePassScore)>=0) && (Double.compare(liviness,livinessPassScore)==0))? "Successful" : "Failed !!!";
        return verdict;
    }

}
