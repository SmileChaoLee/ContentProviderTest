package com.smile.contentprovidertest;

import android.net.Uri;

public class Constant {
    public static final String authorities = "com.smile.contentprovidertest.provider01";
    public static final String providerURL = "content://"+authorities+"/employees";
    public static final Uri contentURI = Uri.parse(providerURL);
}
