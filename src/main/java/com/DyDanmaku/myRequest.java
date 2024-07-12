package com.DyDanmaku;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;


public class myRequest {

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
        Map<String, String> params = new HashMap<String, String>();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", User_Agent);
            httpGet.setHeader("cookie", "__ac_nonce=0" + GenerateToken(20)+ ";/=" +  "live.douyin.com");
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
                roomId = result.substring(result.indexOf("roomId")+11, result.indexOf("roomId") + 30);
                user_unique_id = result.substring(result.indexOf("user_unique_id")+19, result.indexOf("user_unique_id") + 38);
                params.put("roomId", roomId);
                params.put("user_unique_id", user_unique_id);


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
