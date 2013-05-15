package com.magicpixellabs;

import android.app.AlertDialog;
import android.content.Context;

public class Dialogs {

    public static AlertDialog showErrorDialog(Context context) {
        return showErrorDialog(context, -1);
    }

    public static AlertDialog showErrorDialog(Context context, int errorNumber) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");

        switch (errorNumber) {
            case Common.ERROR_UNKNOWN:
                builder.setMessage("An unknown error has occurred.");
                break;
            case Common.ERROR_NOT_FOUND:
                builder.setMessage("The requested resource was not found.");
                break;
            case Common.ERROR_INVALID_CREDENTIALS:
                builder.setMessage("The username or password you entered was incorrect.");
                break;
            default:
                builder.setMessage("An error has occurred, please try again.");
        }

        return builder.create();
    }
}
