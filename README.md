Java抖音直播间弹幕信息获取工具
---------------------------------------------------------------------------------------

修改直播间的网页ID即可通过WSS直连获取弹幕信息
实现原理：通过对直播间网页进行请求获取roomId，拼接成wss链接后建立websocket连接，使用protobuf对接收进行反序列化，得到弹幕，入场，和礼物信息

WSS链接拼接部分及proto解析体来自：https://github.com/saermart/DouyinLiveWebFetcher

效果图：
![效果图_24_7_1](https://github.com/tiangalon/dy_danmaku_java/assets/74497485/5f7e8e29-3fc6-4b68-b355-7b11ece071f0)
