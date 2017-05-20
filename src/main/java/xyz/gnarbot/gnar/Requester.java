package xyz.gnarbot.gnar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class Requester {
    public static final OkHttpClient CLIENT = new OkHttpClient();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
}
