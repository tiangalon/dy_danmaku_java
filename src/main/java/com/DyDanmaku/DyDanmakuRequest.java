package com.DyDanmaku;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;


public class DyDanmakuRequest {

     RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

     public static String User_Agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

    /**
     *  获取抖音ttwid
     * @param live_id
     * @return
     */
    public static Map<String, String> getParams(String live_id) {

        String url = "https://live.douyin.com/" + live_id;
        String ttwid = null;
        String roomId = null;
        String user_unique_id = null;
        String live_status = null;
        String live_title = null;
        String nickname = null;
        String avatar = null;
        Map<String, String> params = new HashMap<String, String>();
        CloseableHttpClient httpClient = HttpClients.createDefault();



        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpGet.setHeader("User-Agent", User_Agent);
            httpGet.setHeader("cookie", "__ac_nonce=0" + GenerateToken(20)+ ";/=" +  "live.douyin.com");
            httpGet.setConfig(defaultConfig);
            CloseableHttpResponse response = httpClient.execute(httpGet);


            if(response != null){
                HttpEntity entity = response.getEntity();   // 获取网页内容
                String result = EntityUtils.toString(entity, "UTF-8");

                /*
                *正则表达式匹配，实测用时过长，弃用
                String pattern = ".*((?<=roomId\\\":\\\")\\d+).*";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(result);
                //System.out.println("匹配结果：" + m.group());
                while (m.find()) {
                    System.out.println("Found roomId: " + m.group());
                }
                */

                roomId = result.substring(result.lastIndexOf("roomId")+11, result.lastIndexOf("roomId") + 30);
                user_unique_id = result.substring(result.indexOf("\\\"user_unique_id\\\":\\\"")+21, result.indexOf("\\\"user_unique_id\\\":\\\"") + 40);
                live_status = result.substring(result.indexOf("\\\"status_str\\\":")+17, result.indexOf("\\\"status_str\\\":") + 18);
                String temp = result.substring(result.indexOf("\\\"status_str\\\":")+21);
                live_title = temp.substring(temp.indexOf("\\\"title\\\":\\\"")+12, temp.indexOf("\\\"title\\\":\\\"") + 100);
                live_title = live_title.substring(0, live_title.indexOf("\\"));
                nickname = temp.substring(temp.indexOf("\\\"nickname\\\":\\\"")+15, temp.indexOf("\\\"nickname\\\":\\\"") + 100);
                nickname = nickname.substring(0, nickname.indexOf("\\"));
                avatar = temp.substring(temp.indexOf("\\\"avatar_thumb\\\":{\\\"url_list\\\":[\\\"")+34, temp.indexOf("\\\"avatar_thumb\\\":{\\\"url_list\\\":[\\\"") + 250);
                avatar = avatar.substring(0, avatar.indexOf("\\"));
                params.put("roomId", roomId);
                params.put("user_unique_id", user_unique_id);
                params.put("live_status", live_status);
                params.put("live_title", live_title);
                params.put("nickname", nickname);
                params.put("avatar", avatar);


                Header responseHeader = response.getFirstHeader("Set-Cookie");
                HeaderElement[] responseHeaderElements = responseHeader.getElements();
                for (int i=0; i<responseHeaderElements.length; i++){
                    if ("ttwid".equals(responseHeaderElements[i].getName())){
                        ttwid = responseHeaderElements[i].getValue();
                    }
                }
                params.put("ttwid", ttwid);

            }
            return params;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String GenerateToken(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789=_";
        int base_length = base.length();
        StringBuffer token = new StringBuffer();
        for (int i = 0; i < length; i++) {
            token.append(base.charAt((int) (Math.random() * base_length)));
        }
        return token.toString();
    }
}
