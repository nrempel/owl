package com.magicpixellabs.back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.back.http.*;
import com.magicpixellabs.beans.Bean;

@SuppressWarnings("unused")
public class APICall {

    public static void get(BaseHttpTask.Callback callback, APIRequest apiRequest,
                           TypeReference typeReference) {

        new Get(callback, apiRequest, typeReference).execute();

    }

    public static void put(BaseHttpTask.Callback callback, APIRequest apiRequest,
                           TypeReference typeReference, Bean body) {

        new Put(callback, apiRequest, typeReference, body).execute();

    }

    public static void post(BaseHttpTask.Callback callback, APIRequest apiRequest,
                            TypeReference typeReference, Bean body) {

        new Post(callback, apiRequest, typeReference, body).execute();

    }

    public static void delete(BaseHttpTask.Callback callback, APIRequest apiRequest,
                              TypeReference typeReference) {

        new Delete(callback, apiRequest, typeReference).execute();

    }
}
