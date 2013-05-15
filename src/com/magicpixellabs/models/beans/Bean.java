package com.magicpixellabs.models.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Bean {

    ObjectMapper mObjectMapper;

    public Bean() {
        mObjectMapper = new ObjectMapper();
        mObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String toJSON() throws IOException {
        return mObjectMapper.writeValueAsString(this);
    }
}