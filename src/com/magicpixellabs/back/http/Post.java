package com.magicpixellabs.back.http;

import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.Helpers;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.beans.Bean;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class Post extends BaseHttpTask {

    public Post(Callback callback, APIRequest apiRequest, TypeReference typeReference, Bean body) {
        super(callback, apiRequest, typeReference, body);
    }

    @Override
    protected String doInBackground(Void... v) {
        InputStream stream;
        String result = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(mAPIRequest.toString());

            if (mBody != null) {
                post.setEntity(new StringEntity(mBody.toJSON()));
            } else {
                post.setEntity(new StringEntity("{}"));
            }
            post.setHeader("Content-type", "application/json");

            Log.i(TAG, mAPIRequest.toString());
            HttpResponse response = httpClient.execute(post);
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
