package com.DyDanmaku;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

import java.util.concurrent.CyclicBarrier;

public class Main {
    public static final CyclicBarrier barrier = new CyclicBarrier(2);

    /**
     * 根据roomId和user_unique_id获取Signature，由Singature.exe生成
     * Signature.exe路径：/src/main/java/com/DyDanmaku/Signature.exe,由同目录下index.js打包生成
     * @param roomId 直播间id
     * @param user_unique_id 用户唯一id
     * @return Signature
     */
    public static String sign(String roomId, String user_unique_id) throws IOException {
        String runType = String.valueOf(Main.class.getResource("Main.class"));
        String command = "";
        if (runType != null && runType.startsWith("jar:")) {
            //在jar中运行时
            command = "./Signature.exe "+ roomId + " " + user_unique_id;
        } else {
            //在IDE中运行时
            command = Main.class.getClassLoader().getResource("./Signature.exe").getPath() + " " + roomId + " " + user_unique_id;
        }

        //String command = ClassLoader.getSystemResource("Signature.exe").getPath() + " " + roomId + " " + user_unique_id;
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

    public static void getSignFile() throws IOException {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = "";
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
            String[] paths = filePath.split("/");
            String[] DirPaths = Arrays.copyOfRange(paths, 0, paths.length - 1);
            filePath = String.join("/", DirPaths);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(filePath);
        InputStream SignFile = Main.class.getClassLoader().getResourceAsStream("Signature.exe");
        if (SignFile == null) {
            System.out.println("Signature.exe not found");
            return;
        }else{
            int index;
            byte[] bytes = new byte[1024];
            FileOutputStream downloadFile = new FileOutputStream(filePath + "/Signature.exe");
            while ((index = SignFile.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            downloadFile.close();
            SignFile.close();
        }
    }


    public static void main(String[] args) throws IOException {

        //———————————————————————↓↓↓参数设定↓↓↓———————————————————————————————————————
        //直播间网页id， 即直播间网址https://live.douyin.com/后面的数字部分
        String live_id = "510200350291";

        //RetryTimes: 重试次数
        //RetryTimes = 0 表示不重试(默认)
        //RetryTimes = -1 表示无限重试
        //RetryTimes = x 表示重试x次(x>0)
        int RetryTimes = 0;

        //重试间隔(单位:秒)
        int RetryIntervalSeconds = 1;
        //—————————————————————————↑↑↑参数设定↑↑↑—————————————————————————————————

        //提供当前时间
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //从直播间网页相应中获取roomId, user_unique_id, ttwid
        String live_url = "https://live.douyin.com/" + live_id;
        String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
        Map<String, String> params = DyDanmakuRequest.getParams(live_id);
        if (params != null) {
            String roomId = params.get("roomId");
            String user_unique_id = params.get("user_unique_id");
            String ttwid = params.get("ttwid");
            String live_status = params.get("live_status");
            String live_title = params.get("live_title");
            String nickname = params.get("nickname");

            //根据roomId和user_unique_id获取Signature
            getSignFile();
            String signature = sign(roomId, user_unique_id);
            //System.out.println("Signature: " + signature);

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
            int retryCount = 0;
            do {
                date.setTime(System.currentTimeMillis());
                if (!live_status.equals("2")) {
                    if (RetryTimes != 0) {
                        System.out.print("\r                                                                                                                 ");
                        if (RetryTimes < 0) {
                            System.out.print("\r当前直播间未开播,将在" + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/∞次)[" + formatter.format(date)  + "]");
                        } else {
                            System.out.print("\r当前直播间未开播,将在" + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/" + RetryTimes + "次)[" + formatter.format(date) + "]");
                        }
                        try {
                            Thread.sleep(RetryIntervalSeconds * 1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        retryCount++;
                    } else {
                        System.out.println("当前直播间未开播！(不重试)[" + formatter.format(date) + "]");
                    }
                } else {
                    System.out.println("当前直播间已开播！[" + formatter.format(date) + "]");
                    System.out.println("直播间标题: " + live_title + "|| 主播昵称: " + nickname);

                    WebSocketClient listener = new WebSocketClient();
                    listener.connect(wss_url, UserAgent, ttwid);
                    try {
                        barrier.await();
                        barrier.reset();
                        date.setTime(System.currentTimeMillis());
                        if (RetryTimes == 0) {
                            System.out.println("连接结束！[" + formatter.format(date) + "]");
                        } else {
                            System.out.print("\r                                                                                                                 ");
                            if (RetryTimes < 0) {
                                System.out.print("\r连接结束！ " + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/∞次)[" + formatter.format(date) + "]");
                                Thread.sleep(RetryIntervalSeconds * 1000L);
                            } else {
                                System.out.print("\r连接结束！ " + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/" + RetryTimes + "次)[" + formatter.format(date) + "]");
                                Thread.sleep(RetryIntervalSeconds * 1000L);
                            }
                        }
                        retryCount++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (RetryTimes != 0) {
                    params = DyDanmakuRequest.getParams(live_id);
                    if (params != null) {
                        live_status = params.get("live_status");
                        live_title = params.get("live_title");
                        nickname = params.get("nickname");
                    }
                }
            } while (RetryTimes < 0 || retryCount < RetryTimes);

        } else {
            System.out.println("无法获取参数");
        }
    }


}
