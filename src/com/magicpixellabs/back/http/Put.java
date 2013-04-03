package com.magicpixellabs.back.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.beans.Bean;

public class Put extends BaseHttpTask {

    public Put(Callback callback, APIRequest apiRequest, TypeReference typeReference, Bean body) {
        super(callback, apiRequest, typeReference, body);
    }

    @Override
    protected String doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(String json) {

    }
}
