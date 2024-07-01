package com.DyDanmaku;

import java.io.*;
import java.util.Map;

public class Main {
    /**
     * 根据roomId和user_unique_id获取Signature，由Singature.exe生成
     * Signature.exe路径：/src/main/java/com/DyDanmaku/Signature.exe,由同目录下index.js打包生成
     * @param roomId 直播间id
     * @param user_unique_id 用户唯一id
     * @return Signature
     */
    public static String sign(String roomId, String user_unique_id) {
        String command = Main.class.getClassLoader().getResource("./Signature.exe").getPath() + " " + roomId + " " + user_unique_id;
        Process process = null;
        String signature = "";
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            signature = br.readLine();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (process!= null) {
                process.destroy();
            }
        }
        return signature;
    }

    public static void main(String[] args){
        //直播间网页id， 即直播间网址https://live.douyin.com/后面的数字部分
        String live_id = "20033723905";

        //从直播间网页相应中获取roomId, user_unique_id, ttwid
        String live_url = "https://live.douyin.com/" + live_id;
        String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        Map<String, String> params = myRequest.getParams(live_id);
        if (params != null) {
            String roomId = params.get("roomId");
            String user_unique_id = params.get("user_unique_id");
            String ttwid = params.get("ttwid");

            /*
             * 检查roomId, user_unique_id, ttwid是否正确
             * System.out.println("roomId: " + roomId);
             * System.out.println("user_unique_id: " + user_unique_id);
             * System.out.println("ttwid: " + ttwid);
             */

            /*
             * 根据roomId和user_unique_id获取Signature（目前无需Signature也可连接，暂时停用）
             * String signature = sign(roomId, user_unique_id);
             * System.out.println("Signature: " + signature);
             */

            //拼接url,连接websocket
            String wss_url = "wss://webcast5-ws-web-lq.douyin.com/webcast/im/push/v2/?" +
            "app_name=douyin_web&version_code=180800&webcast_sdk_version=1.0.14-beta.0" +
            "&update_version_code=1.0.14-beta.0&compress=gzip&device_platform=web&cookie_enabled=true&screen_width=1536" +
            "&screen_height=864&browser_language=zh-CN&browser_platform=Win32&browser_name=Mozilla" +
            "&browser_version=5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)" +
            "%20Chrome/126.0.0.0%20Safari/537.36&browser_online=true&tz_name=Asia/Shanghai&" +
            "cursor=r-1_d-1_u-1_fh-7385824323388707875_t-1719646741156&" +
            "internal_ext=internal_src:dim|wss_push_room_id:" + roomId + "|wss_push_did:7311183754668557878" +
            "|first_req_ms:1719646741059|fetch_time:1719646741156|seq:1|wss_info:0-1719646741156-0-0|" +
            "wrds_v:7311183754668557878&host=https://live.douyin.com&aid=6383" +
            "&live_id=1&did_rule=3&endpoint=live_pc&support_wrds=1&" +
            "user_unique_id=7311183754668557878" +
            "&im_path=/webcast/im/fetch/&identity=audience&need_persist_msg_count=15" +
            "&insert_task_id=&live_reason=&room_id=" + roomId + "&heartbeatDuration=0&signature=";
            WebSocketClient listener = new WebSocketClient();
            listener.connect(wss_url, UserAgent, ttwid);

        } else {
            System.out.println("无法获取参数");
        }
    }


}
