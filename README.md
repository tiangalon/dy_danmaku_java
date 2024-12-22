Java抖音直播间弹幕信息获取工具
---------------------------------------------------------------------------------------

修改直播间的网页ID即可通过WSS直连获取弹幕信息
实现原理：通过对直播间网页进行请求获取roomId，拼接成wss链接后建立websocket连接，使用protobuf对接收进行反序列化，得到弹幕，入场，和礼物信息

proto解析体来自：https://github.com/saermart/DouyinLiveWebFetcher

---------------------------------------------------------------------------------------
【更新】<br>
2024.7.12 13:25 修复Signature签名功能 测试可用<br>
2024.11.30 10:25 修复roomId和user_unique_id获取错误问题 测试可用<br>
2024.12.22 18:50 添加重试功能，可设定0~无限次重试次数和重试间隔时间；关闭cookie警告<br>

【效果图】：<br>
![效果图_24_7_1](https://github.com/tiangalon/dy_danmaku_java/assets/74497485/5f7e8e29-3fc6-4b68-b355-7b11ece071f0)

【参数设定】<br>
`live_id`:直播间网页id， 即直播间网址https://live.douyin.com/后面的数字部分<br>
`RetryTimes`:重试次数，0表示不重试(默认),-1表示无限重试,x(x>0)表示重试x次<br>
`RetryIntervalSeconds`:重试间隔时间，单位秒(默认1秒)<br>
