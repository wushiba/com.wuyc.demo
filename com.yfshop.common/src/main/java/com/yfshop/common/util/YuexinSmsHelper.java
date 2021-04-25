package com.yfshop.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YuexinSmsHelper {
    private static String url = "http://106.14.55.160:9000/HttpSmsMt";
    private static String yxUrl = "http://106.14.55.160:9001/HttpSmsMt";


    public static String sendSms(String content, String mobile) {
        String mttime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> smsTask = new HashMap<String, String>();
        smsTask.put("name", "hzjjb1");
        smsTask.put("pwd", MD51("de94af374bf6fff5ca2bbc2b142010ec" + mttime));
        smsTask.put("content", content);
        smsTask.put("phone", mobile);
        smsTask.put("mttime", mttime);
        smsTask.put("rpttype", "1");
        String msg = sendPost(url, smsTask);
        return msg;
    }

    public static String sendYxSms(String content, String mobile) {
        String mttime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> smsTask = new HashMap<String, String>();
        smsTask.put("name", "hzjjb2");
        smsTask.put("pwd", MD51("d41d8cd98f00b204e9800998ecf8427e" + mttime));
        smsTask.put("content", content);
        smsTask.put("phone", mobile);
        smsTask.put("mttime", mttime);
        smsTask.put("rpttype", "1");
        String msg = sendPost(yxUrl, smsTask);
        return msg;
    }

    private static String sendPost(String sendUrl, Map<String, String> params) {

        URL u = null;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
            }
            sb.substring(0, sb.length() - 1);
        }
        // 尝试发送请求
        try {
            u = new URL(sendUrl);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(6000);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        // 读取返回内容
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private static String MD51(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
