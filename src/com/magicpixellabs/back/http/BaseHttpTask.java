package com.magicpixellabs.back.http;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.models.beans.APIResponse;
import com.magicpixellabs.models.beans.Bean;

import java.io.IOException;

public abstract class BaseHttpTask extends AsyncTask<Void, Void, String> {

    Callback mCallback;

    public interface Callback {
        void onHttpResult(APIResponse<Bean> response);
    }

    protected static final String TAG = "HttpTask";

    APIRequest mAPIRequest;
    private TypeReference mTypeReference;
    Bean mBody;

    public BaseHttpTask(Callback callback, APIRequest apiRequest, TypeReference typeReference) {
        mCallback = callback;
        mAPIRequest = apiRequest;
        mTypeReference = typeReference;
    }

    public BaseHttpTask(Callback callback, APIRequest apiRequest, TypeReference typeReference, Bean body) {
        this(callback, apiRequest, typeReference);
        mBody = body;
    }

    protected class ParseJSONTask extends AsyncTask<String, Void, APIResponse<Bean>> {

        @Override
        protected APIResponse<Bean> doInBackground(String... json) {
            APIResponse<Bean> result = null;
            try {
                return new ObjectMapper().readValue(json[0], mTypeReference);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(APIResponse<Bean> response) {
            // Object mapper failed
            if (response == null) return;
            mCallback.onHttpResult(response);
        }
    }
}
