/**
 * Copyright (C) 2016 MPen, Inc. All Rights Reserved.
 */

package com.mpen.api.util;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.WebUtils;

import com.mpen.api.common.Constants;
import com.mpen.api.exception.SdkException;

/**
 * 通用工具类.
 *
 * @author kai
 *
 */
public final class CommUtil {

    public static String genRecordKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 测试时通过identifiaction获取code方法.
     * 
     */
    public static String getPenCode(String identifiaction) throws SdkException {
        final String first = identifiaction.substring(0, 3);
        final String second = identifiaction.substring(3);
        return DigestUtils.md5Hex(DigestUtils.md5Hex(second + first + second));
    }

    /**
     * 获取UTC时间.
     * 
     */
    public static String getUTCTime(Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String time = sdf.format(date);
        return time;
    }

    /**
     * 获取缓存Key.
     * 
     */
    public static String getCacheKey(String str) {
        str = Constants.SCHOOL_NO + "_SpringBoot_" + str;
        return str;
    }

    /**
     * HttpClient post方法.
     * 
     */
    public static String post(String url, Map<String, String> map) throws SdkException {
        CloseableHttpClient closeableHttpClient = null;
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            closeableHttpClient = httpClientBuilder.build();
            final HttpPost httppost = new HttpPost(url);
            final List<NameValuePair> params = new ArrayList<NameValuePair>();
            map.forEach((key, value) -> {
                params.add(new BasicNameValuePair(key, value));
            });
            httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            final HttpResponse response = closeableHttpClient.execute(httppost);
            // 如果状态码为200,就是正常返回
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (Exception exception) {
            throw new SdkException(Constants.UCENTER_ERROR);
        } finally {
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * HttpClient post上传文件.
     * 
     */
    public static String post(String url, Map<String, String> headerMap, Map<String, String> bodyMap, String fileName,
        String filePath) throws SdkException {
        CloseableHttpClient closeableHttpClient = null;
        CloseableHttpResponse response = null;
        try {
            closeableHttpClient = HttpClientBuilder.create().build();
            final HttpPost httpPost = new HttpPost(url);
            final MultipartEntity customMultiPartEntity = new MultipartEntity();
            if (bodyMap != null && bodyMap.size() > 0) {
                for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
                    customMultiPartEntity.addPart(entry.getKey(),
                        new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
                }
            }
            final ContentBody fileBody = new FileBody(new File(filePath));
            customMultiPartEntity.addPart(fileName, fileBody);
            httpPost.setEntity(customMultiPartEntity);
            if (headerMap != null && headerMap.size() > 0) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            response = closeableHttpClient.execute(httpPost);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * HttpClient get方法.
     * 
     */
    public static String get(String url) throws SdkException {
        CloseableHttpClient closeableHttpClient = null;
        try {
            final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            closeableHttpClient = httpClientBuilder.build();
            final HttpGet httpGet = new HttpGet(url);
            final HttpResponse response = closeableHttpClient.execute(httpGet);
            // 如果状态码为200,就是正常返回
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new SdkException(Constants.UCENTER_ERROR);
        } finally {
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取请求Ip地址, 来自diandubi项目的com.ucenter.util.CommonUtil.
     * 
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!ip.isEmpty() && ip.indexOf(",") > 0) {
            ip = StringUtils.split(ip, ",")[0];
        }
        return ip;
    }

    public static String getScheme(HttpServletRequest request) {
        String scheme = request.getHeader("x-forwarded-proto");
        if (StringUtils.isBlank(scheme)) {
            scheme = request.getScheme();
        }
        return scheme;
    }

    public static String checkUserAgent(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return Constants.WRONG_USERAGENT;
        }
        try {
            final long time = Math
                .abs(System.currentTimeMillis() / 1000 - Long.valueOf(userAgent.substring(4, userAgent.length() - 4)));
            // 请求允许时间误差为1h
            if (time > 3600) {
                return Constants.TIME_ERROR;
            }
            // 验证user-agent,生成规则:四位随机数+时间戳（s）+md5(机数+时间戳)
            final String md5Hex = DigestUtils.md5Hex(userAgent.substring(0, userAgent.length() - 4));
            final int md5HexLen = md5Hex.length();
            final String md52IntResult = String.valueOf(Integer.valueOf(md5Hex.substring(md5HexLen - 4), 36));
            final String prefix = md52IntResult.length() > 4 ? md52IntResult.substring(0, 4)
                : String.format("%04d", Integer.valueOf(md52IntResult));
            final String suffix = userAgent.substring(userAgent.length() - 4);
            if (!prefix.substring(prefix.length() - 4).equals(suffix)) {
                return Constants.WRONG_USERAGENT;
            }
        } catch (Exception e) {
            return Constants.WRONG_USERAGENT;
        }
        return Constants.SUCCESS;
    }

    public static String getLoginId(HttpServletRequest httpRequest) {
        final String sessionId = getCookieValue(httpRequest, Constants.SESSIONKEY);
        String loginId = "";
        if (StringUtils.isNotBlank(sessionId)) {
            Map<String, String> map = Constants.GSON.fromJson(sessionId, Map.class);
            loginId = map.get(Constants.LOGINIDKEY);
            // TODO 暂时保存penId信息，以后需要对笔信息进行相关验证
            httpRequest.setAttribute(Constants.LOGINIDKEY, loginId);
            httpRequest.setAttribute(Constants.PENKEY, map.get(Constants.PENKEY));
        } else {
            loginId = getCookieValue(httpRequest, Constants.LOGINIDKEY);
        }
        return loginId;
    }

    public static String getCookieValue(HttpServletRequest request, String key) {
        final Cookie cookie = WebUtils.getCookie(request, key);
        if (cookie != null) {
            return cookie.getValue();
        }
        return StringUtils.EMPTY;
    }
    
    /**
     * linux获取本地多张网卡ip
     *   引自：http://blog.csdn.net/luckly_p/article/details/47274531
     * 
     */
    public static boolean checkLocalIp(String temp) throws SocketException {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address && StringUtils.isNotBlank(ip.getHostAddress())
                    && ip.getHostAddress().equals(temp)) {
                    return true;
                }
            }
        }
        return false;
    }
}
