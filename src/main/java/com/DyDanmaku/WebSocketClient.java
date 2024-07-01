package com.DyDanmaku;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class WebSocketClient {
    /**
     * websocket连接
     * @param url websocket地址
     * @param ttwid 添加cookie的ttwid
     * @param useragent 添加请求头的User-Agent
     */
    public void connect(String url, String useragent, String ttwid) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", useragent)
            .header("Cookie", "ttwid="+ ttwid)
            .build();
        WSListener Listener = new WSListener();
        WebSocket ws = client.newWebSocket(request, Listener);
    }
}