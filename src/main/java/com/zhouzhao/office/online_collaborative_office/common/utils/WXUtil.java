package com.zhouzhao.office.online_collaborative_office.common.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WXUtil {


    public static String getOpenId(String appId, String appSecret, String code) {

        HttpUrl url = new HttpUrl.Builder().addQueryParameter("appid", appId)
                .addQueryParameter("secret", appSecret)
                .addQueryParameter("js_code", code)
                .addQueryParameter("grant_type", "authorization_code")
                .addPathSegments("sns/jscode2session")
                .host("api.weixin.qq.com")
                .scheme("https").build();
        System.out.println("url = " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.body() == null) {
                return null;
            }
            String res = response.body().string();
            System.out.println("res = " + res);
            JSONObject jsonObject = JSONObject.parseObject(res);
            return jsonObject.getString("openid");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }


    }
}
