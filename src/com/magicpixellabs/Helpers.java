package com.magicpixellabs;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@SuppressWarnings("unused")
public class Helpers {

    public static String streamToString(InputStream in) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer);
        return writer.toString();
    }

}
