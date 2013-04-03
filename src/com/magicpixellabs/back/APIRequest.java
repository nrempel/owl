package com.magicpixellabs.back;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public class APIRequest implements Parcelable {

    private static final String TAG = "APIRequest";
    private static final String BASE_URL = "http://smsserver.jit.su/api/v1/";

    ///////////////
    /* RESOURCES */
    ///////////////
    public static final int RESOURCE_USERS      = 0;
    public static final int RESOURCE_MESSAGES   = 1;
    public static final int RESOURCE_NUMBERS    = 2;
    private static final LinkedHashMap<Integer, String> RESOURCES = new LinkedHashMap<Integer, String>() {
        {put(RESOURCE_USERS, "users");}
        {put(RESOURCE_MESSAGES, "messages");}
        {put(RESOURCE_NUMBERS, "numbers");}
    };

    /////////////
    /* ACTIONS */
    /////////////
    public static final int ACTION_LOGIN            = 0;
    public static final int ACTION_REGISTER_DEVICE  = 1;
    public static final int ACTION_CHOOSE_NUMBER    = 2;
    public static final int ACTION_AVAILABLE        = 3;
    private static final LinkedHashMap<Integer, String> ACTIONS = new LinkedHashMap<Integer, String>() {
        {put(ACTION_LOGIN, "login");}
        {put(ACTION_REGISTER_DEVICE, "register_device");}
        {put(ACTION_CHOOSE_NUMBER, "choose_number");}
        {put(ACTION_AVAILABLE, "available");}
    };

    private final String resource;
    private final String action;
    private final String qualifier;
    private final ArrayList<QueryString> queryStrings;


    /* Builder design pattern */

    public static class Builder {

        private String resource;
        private String action;
        private String qualifier;
        private ArrayList<QueryString> queryStrings;

        public Builder resource(int resourceId) {
            this.resource = RESOURCES.get(resourceId);
            return this;
        }

        public Builder action(int actionId) {
            this.action = ACTIONS.get(actionId);
            return this;
        }

        public Builder qualifier(String qualifier) {
            this.qualifier = qualifier;
            return this;
        }

        public Builder queryString(QueryString queryString) {
            if (queryStrings == null) queryStrings = new ArrayList<QueryString>();
            if (queryStrings.contains(queryString)) {
                Log.i(TAG, "Trying to add two query strings of the same type, ignoring");
                return this;
            }
            this.queryStrings.add(queryString);
            return this;
        }

        public APIRequest build() throws InvalidRequestException {
            if (resource == null) {
                throw new InvalidRequestException("API resource path has not been specified");
            }
            return new APIRequest(this);
        }
    }

    private APIRequest(Builder builder) {
        this.resource = builder.resource;
        this.action = builder.action;
        this.qualifier = builder.qualifier;
        this.queryStrings = builder.queryStrings;
    }

    public static class QueryString implements Parcelable{

        public QueryString() {}

        String field;
        String value;
        String option;

        //////////////////
        /* Parcel Magic */
        //////////////////
        @Override
        public int describeContents() {
            return hashCode();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(field);
            dest.writeString(value);
            dest.writeString(option);
        }

        public static final Parcelable.Creator<QueryString> CREATOR = new Parcelable.Creator<QueryString>() {
            public QueryString createFromParcel(Parcel in) {
                return new QueryString(in);
            }
            public QueryString[] newArray(int size) {
                return new QueryString[size];
            }
        };

        public QueryString(Parcel source) {
            field = source.readString();
            value = source.readString();
            option = source.readString();
        }
        //////////////////////
        /* END Parcel Magic */
        //////////////////////
    }

    public static class EmailParameter extends QueryString {
        public EmailParameter(String email) {
            this.field = "email=";
            this.value = email;
        }
    }

    public static class PasswordParameter extends QueryString {
        public PasswordParameter(String password) {
            this.field = "password=";
            this.value = password;
        }
    }

    public static class PlatformParameter extends QueryString {
        public PlatformParameter(String platform) {
            this.field = "platform=";
            this.value = platform;
        }
    }

    public static class IdParameter extends QueryString {
        public IdParameter(String id) {
            this.field = "id=";
            this.value = id;
        }
    }

    public static class NumberParameter extends QueryString {
        public NumberParameter(String number) {
            this.field = "number=";
            this.value = number;
        }
    }

    public static class UserParameter extends QueryString {
        public UserParameter(String user) {
            this.field = "user=";
            this.value = user;
        }
    }

    public static class ToParameter extends QueryString {
        public ToParameter(String to) {
            this.field = "to=";
            this.value = to;
        }
    }

    public static class BodyParameter extends QueryString {
        public BodyParameter(String body) {
            this.field = "body=";
            this.value = body;
        }
    }

    public static class CountryParameter extends QueryString {
        public CountryParameter(String country) {
            this.field = "country=";
            this.value = country;
        }
    }

    @Override
    public String toString() {
        String params = "";
        String qualifier = "";
        URLCodec urlCodec = new URLCodec();
        if (this.qualifier != null) qualifier = "/" + this.qualifier;
        if (queryStrings != null) {
            boolean first = true;
            for (QueryString queryString: queryStrings) {
                if (first) {
                    params += "?";
                    first = false;
                } else {
                    params += "&";
                }
                params += queryString.field;
                try {
                    params += urlCodec.encode(queryString.value);
                } catch (EncoderException e) {
                    Log.i(TAG, e.getMessage());
                }
                if (queryString.option != null) params += queryString.option;
            }
        }

        if (action != null) {
            return BASE_URL + resource + "." + action + qualifier + params;
        } else {
            return BASE_URL + resource + qualifier + params;
        }
    }

    public static class InvalidRequestException extends Exception {
        public InvalidRequestException() {}
        public InvalidRequestException(String message){super(message);}
    }

    //////////////////
    /* Parcel Magic */
    //////////////////
    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i(TAG, "Writing to parcel...");
        dest.writeString(resource);
        dest.writeString(action);
        dest.writeString(qualifier);
        dest.writeTypedList(queryStrings);
    }

    public static final Parcelable.Creator<APIRequest> CREATOR = new Parcelable.Creator<APIRequest>() {
        public APIRequest createFromParcel(Parcel in) {
            return new APIRequest(in);
        }
        public APIRequest[] newArray(int size) {
            return new APIRequest[size];
        }
    };

    public APIRequest(Parcel source) {
        Log.i(TAG, "Reconstructing parcel...");
        resource = source.readString();
        action = source.readString();
        qualifier = source.readString();
        queryStrings = new ArrayList<QueryString>();
        source.readTypedList(queryStrings, QueryString.CREATOR);
    }
    //////////////////////
    /* END Parcel Magic */
    //////////////////////

}