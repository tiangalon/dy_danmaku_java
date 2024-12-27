package com.DyDanmaku;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

public class DYSign {
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
}
