package com.DyDanmaku;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.Map;

import com.DyDanmaku.gui.gui;

public class WebSocketClient {
    private WSListener Listener = new WSListener();
    private WebSocket ws;
    private gui gui;

    public WebSocketClient() {
        Listener.setGui(null);
    }

    public WebSocketClient(gui gui) {
        Listener.setGui(gui);
    }

    /**
     * websocket连接
     * @param params         包含直播间信息的map
     * @param signature      wss连接签名字符串
     */
    /*
    public void connect(Map<String, String> params, String signature) {
        connect(params, signature, false);
    }

     */

    public void connect(Map<String, String> params, String signature) {
        String roomId = params.get("roomId");
        String user_unique_id = params.get("user_unique_id");
        String ttwid = params.get("ttwid");

        String useragent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
        String wss_url = "wss://webcast5-ws-web-hl.douyin.com/webcast/im/push/v2/?" +
                "app_name=douyin_web&" +
                "version_code=180800&" +
                "webcast_sdk_version=1.0.14-beta.0&" +
                "update_version_code=1.0.14-beta.0&" +
                "compress=gzip&" +
                "device_platform=web&" +
                "cookie_enabled=true&" +
                "screen_width=2560&" +
                "screen_height=1440&" +
                "browser_language=zh-CN&" +
                "browser_platform=Win32&" +
                "browser_name=Mozilla&browser_version=5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/131.0.0.0%20Safari/537.36&" +
                "browser_online=true&" +
                "tz_name=Asia/Hong_Kong&" +
                "cursor=t-1732882891133_r-1_d-1_u-1_h-7442675243345155072&" +
                "internal_ext=internal_src:dim|wss_push_room_id:" + roomId + "|wss_push_did:" + user_unique_id + "|first_req_ms:1732882891041|" +
                "fetch_time:1732882891133|seq:1|wss_info:0-1732882891133-0-0|wrds_v:7442675340347970811&" +
                "host=https://live.douyin.com&" +
                "aid=6383&" +
                "live_id=1&" +
                "did_rule=3&" +
                "endpoint=live_pc&support_wrds=1&" +
                "user_unique_id=" + user_unique_id + "&" +
                "im_path=/webcast/im/fetch/&" +
                "identity=audience&need_persist_msg_count=15&" +
                "insert_task_id=&" +
                "live_reason=&" +
                "room_id=" + roomId + "&" +
                "heartbeatDuration=0&" +
                "signature=" + signature;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(wss_url)
            .header("User-Agent", useragent)
            .header("Cookie", "ttwid="+ ttwid)
            .build();
        ws = client.newWebSocket(request, Listener);


    }

    /**
     * websocket关闭
     */
    public void close() {
        if (ws != null) {
            ws.close(1000, "close");
            Listener.onClosing(ws, 1000, "close");
            Listener.onClosed(ws, 1000, "close");
        }
    }

}