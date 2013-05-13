package com.magicpixellabs.back.http;

import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.Helpers;
import com.magicpixellabs.back.APIRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class Get extends BaseHttpTask {


    public Get(Callback callback, APIRequest apiRequest, TypeReference typeReference) {
        super(callback, apiRequest, typeReference);
    }

    @Override
    protected String doInBackground(Void... v) {
        InputStream stream;
        String result = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(mAPIRequest.toString());
            Log.i(TAG, mAPIRequest.toString());
            HttpResponse response = httpClient.execute(get);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(response.getEntity());
                stream = bufferedHttpEntity.getContent();
                result = Helpers.streamToString(stream);
            } else {
                if (response != null) {
                    Log.e(TAG, "Network request returned with status code " + response.getStatusLine().getStatusCode());
                } else {
                    Log.e(TAG, "Network request failed.");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String json) {
        // Parsing input stream failed
        if (json == null) return;
        new ParseJSONTask().execute(json);
    }
}
