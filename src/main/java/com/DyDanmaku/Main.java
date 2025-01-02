package com.DyDanmaku;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

import java.util.concurrent.CyclicBarrier;


public class Main {
    private static final Date date = new Date();
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) throws IOException {

        //———————————————————————↓↓↓参数设定↓↓↓———————————————————————————————————————
        //直播间网页id， 即直播间网址https://live.douyin.com/后面的数字部分
        String live_id = "61339262466";

        //RetryTimes: 重试次数
        //RetryTimes = 0 表示不重试(默认)
        //RetryTimes = -1 表示无限重试
        //RetryTimes = x 表示重试x次(x>0)
        int RetryTimes = 0;

        //重试间隔(单位:秒)
        int RetryIntervalSeconds = 1;
        //—————————————————————————↑↑↑参数设定↑↑↑—————————————————————————————————

        //尝试获取直播间信息
        Map<String, String> params = DyDanmakuRequest.getParams(live_id);
        if (params != null) {
            String roomId = params.get("roomId");
            String user_unique_id = params.get("user_unique_id");
            String live_status = params.get("live_status");
            String live_title = params.get("live_title");
            String nickname = params.get("nickname");

            //根据roomId和user_unique_id获取Signature
            DYSign.getSignFile();
            String signature = DYSign.sign(roomId, user_unique_id);

            int retryCount = 0;
            do {
                if (!live_status.equals("2")) {
                    if (RetryTimes != 0) {
                        System.out.print("\r                                                                                                                 ");
                        if (RetryTimes < 0) {
                            System.out.print("\r当前直播间未开播,将在" + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/∞次)[" + CurrentTime()  + "]");
                        } else {
                            System.out.print("\r当前直播间未开播,将在" + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/" + RetryTimes + "次)[" + CurrentTime() + "]");
                        }
                        try {
                            Thread.sleep(RetryIntervalSeconds * 1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        retryCount++;
                    } else {
                        System.out.println("当前直播间未开播！(不重试)[" + CurrentTime() + "]");
                    }
                } else {
                    System.out.println("当前直播间已开播！[" + CurrentTime() + "]");
                    System.out.println("直播间标题: " + live_title + "|| 主播昵称: " + nickname);

                    WebSocketClient listener = new WebSocketClient();
                    listener.connect(params, signature);

                    try {
                        barrier.await();
                        barrier.reset();

                        if (RetryTimes == 0) {
                            System.out.println("连接结束！[" + CurrentTime() + "]");
                        } else {
                            System.out.print("\r                                                                                                                 ");
                            if (RetryTimes < 0) {
                                System.out.print("\r连接结束！ " + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/∞次)[" +CurrentTime() + "]");
                                Thread.sleep(RetryIntervalSeconds * 1000L);
                            } else {
                                System.out.print("\r连接结束！ " + RetryIntervalSeconds + "秒后进行下一次重试...(" + (retryCount + 1) + "/" + RetryTimes + "次)[" + CurrentTime() + "]");
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

    public static String CurrentTime() {
        date.setTime(System.currentTimeMillis());
        return formatter.format(date);
    }


}
