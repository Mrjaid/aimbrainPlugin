package com.fasyl.aimbrainplugin;

import com.aimbrain.sdk.models.FaceAuthenticateModel;
import com.android.volley.VolleyError;

/**
 * Created by Mr Jaid on 5/30/2017.
 */

public interface IntegrationCallback {
    public void onSuccess(int authenticationResult);

    public void onFailure(String error);
}
