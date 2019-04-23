package com.nj.utill.httputils;

import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yy on 2016/12/28.
 */
public class HttpsStatusUtil {
    private final static Logger loger = LoggerFactory.getLogger(HttpsStatusUtil.class);

    private boolean flag;

    private String userKey;

    private JSONObject status_header;//状态

    private String body;

    private JSONObject bodyJson;

    private String status_mac; //状态

    public static String status_tyzxToken = "";//状态

    //状态
 /*   public static String getStatus_TYZXToken(boolean isLostEfficacy) {
        if (isLostEfficacy) {
            try {
                status_tyzxToken = getStatus_Token();
            } catch (Exception e) {
                e.printStackTrace();
                status_tyzxToken = "";
            }
        }
        return status_tyzxToken;
    }
*/
    public HttpsStatusUtil(Map<String, String> params) throws Exception {
        if (params.containsKey("userKey")) {
            this.userKey = params.get("userKey");
            params.remove("userKey");
        }
        this.flag = Boolean.parseBoolean(params.get("flag"));
        params.remove("flag");
        String activityCode = "";
        String key = "";
        if (params.containsKey("activityCode")) {
            activityCode = params.get("activityCode");
            params.remove("activityCode");
        }
        if (params.containsKey("key")) {
            key = params.get("key");
            params.remove("key");
        }
        setStatus_header(activityCode);
        setBody(params);
        setStatus_mac();
    }

    //状态
    public String getStatus_Content() {
        JSONObject json = new JSONObject();
        json.put("header", status_header);
        if (flag) {
            json.put("body", body);
        } else {
            json.put("body", bodyJson);
        }
        json.put("mac", status_mac);
        JSONObject res = new JSONObject();
        res.put("credit", json);
        return res.toString();
    }

    //在网状态
    private void setStatus_header(String activityCode) {
        JSONObject header = new JSONObject();
        //0100，对于同一交易应答与请求版本号始终一致
        header.put("version", "0100");
        //发起方填写 0：非测试交易 1：测试交易
        header.put("testFlag", 0);
        //activityCode相当于接口代码
        header.put("activityCode", activityCode);
        //0：请求 1：应答
        header.put("actionCode", 0);
        //参见《机构代码对应表》
      //  header.put("reqSys", PublicProperties.getValueByKey("tyzx_status_service_code"));
        //01:web 02:手机
        header.put("reqChannel", "01");
        //交易流水号
        header.put("reqTransID", get32Code());
        //请求8位日期码
        header.put("reqDate", DateUtil.dateToString(new Date(), "yyyyMMdd"));
        //请求的16位日期码
        header.put("reqDateTime", DateUtil.dateToString(new Date(), "yyyyMMdd24HHmmss"));
        //授权号
        header.put("authorizationCode", "10151112140001000001542155961605");
        this.status_header = header;
    }

    private String get32Code() {
        String time = Long.toString(new Date().getTime());
        for (int i = 0; i < 19; i++) {
            time += Integer.toString(new Random().nextInt(10));
        }
        return time;
    }

    private void setBody(Map<String, String> params) throws Exception {

        JSONObject json = new JSONObject();
        for (String key : params.keySet()) {
            json.put(key, params.get(key));
        }
        if (flag) {
//            System.out.println(json.toString());
            this.body = AesEncrypt.encrypt(json.toString(), userKey);
        } else {
            this.bodyJson = json;
        }
    }

    //状态
    private void setStatus_mac() {
        JSONObject json = new JSONObject();
        json.put("header", status_header);
        if (flag) {
            json.put("body", body);
        } else {
            json.put("body", bodyJson);
        }
//        System.out.println(json.toString().substring(1,json.toString().length()-1));
        this.status_mac = DigestUtils.md5Hex(json.toString().substring(1, json.toString().length() - 1));
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * post方式请求服务器(https协议)
     *
     * @param url     请求地址
     * @param content 参数
     * @param charset 编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static byte[] postHttps(String url, String content, String charset) {
        HttpsURLConnection connection = null;
        InputStream is = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL console = new URL(null, url, new sun.net.www.protocol.https.Handler());
             connection = (HttpsURLConnection) console.openConnection();
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
            connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
            connection.setUseCaches(false); //Post 请求不能使用缓存
            connection.setConnectTimeout(30000); //设置连接主机超时时间
            connection.setReadTimeout(30000); //设置从主机读取数据超时
            OutputStream out = connection.getOutputStream();
            out.write(content.getBytes(charset));
            // 刷新、关闭
            out.flush();
            out.close();
             is = connection.getInputStream();
            if (is != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                return outStream.toByteArray();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            InputStream error = ((HttpURLConnection) connection).getErrorStream();
            if (error != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                try {
                    while ((len = error.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

                return outStream.toByteArray();
            }
            return e.getMessage().getBytes();
        }
    }
    
    public static byte[] postHttps(String url, String content, String charset,Map<String,String> headers) {
    	HttpsURLConnection connection = null;
    	InputStream is = null;
    	try {
    		SSLContext sc = SSLContext.getInstance("SSL");
    		sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
    				new java.security.SecureRandom());
    		URL console = new URL(null, url, new sun.net.www.protocol.https.Handler());
    		connection = (HttpsURLConnection) console.openConnection();
    		connection.setSSLSocketFactory(sc.getSocketFactory());
    		connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
    		connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
    		connection.setRequestMethod("POST");
    		connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
    		connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
    		connection.setUseCaches(false); //Post 请求不能使用缓存
    		connection.setConnectTimeout(30000); //设置连接主机超时时间
    		connection.setReadTimeout(30000); //设置从主机读取数据超时
			for(String header : headers.keySet()) {
				connection.setRequestProperty(header, headers.get(header));
			}
    		OutputStream out = connection.getOutputStream();
    		out.write(content.getBytes(charset));
    		// 刷新、关闭
    		out.flush();
    		out.close();
    		is = connection.getInputStream();
    		if (is != null) {
    			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    			byte[] buffer = new byte[1024];
    			int len = 0;
    			while ((len = is.read(buffer)) != -1) {
    				outStream.write(buffer, 0, len);
    			}
    			is.close();
    			return outStream.toByteArray();
    		}
    		return null;
    	} catch (Exception e) {
    		e.printStackTrace();
    		InputStream error = ((HttpURLConnection) connection).getErrorStream();
    		if (error != null) {
    			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    			byte[] buffer = new byte[1024];
    			int len = 0;
    			try {
    				while ((len = error.read(buffer)) != -1) {
    					outStream.write(buffer, 0, len);
    				}
    			} catch (IOException e2) {
    				e2.printStackTrace();
    			}
    			
    			return outStream.toByteArray();
    		}
    		return e.getMessage().getBytes();
    	}
    }

    
    
    
    /**
     * post方式请求服务器(https协议)
     *
     * @param url     请求地址
     * @param content 参数
     * @param charset 编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static byte[] postHttpsFormUrl(String url, String content, String charset) {
        HttpsURLConnection connection = null;
        InputStream is = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL console = new URL(null, url, new sun.net.www.protocol.https.Handler());
             connection = (HttpsURLConnection) console.openConnection();
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
            connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
            connection.setUseCaches(false); //Post 请求不能使用缓存
            connection.setConnectTimeout(30000); //设置连接主机超时时间
            connection.setReadTimeout(30000); //设置从主机读取数据超时
            OutputStream out = connection.getOutputStream();
            out.write(content.getBytes(charset));
            // 刷新、关闭
            out.flush();
            out.close();
             is = connection.getInputStream();
            if (is != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                return outStream.toByteArray();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            InputStream error = ((HttpURLConnection) connection).getErrorStream();
            if (error != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                try {
                    while ((len = error.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

                return outStream.toByteArray();
            }
            return e.getMessage().getBytes();
        }
    }
    
    
    /**
     * post方式请求服务器(https协议)
     *
     * @param url     请求地址
     * @param content 参数
     * @param charset 编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static byte[] postHttpsFormUrl(String url, String content, String charset,Map<String,String> headers) {
    	HttpsURLConnection connection = null;
    	InputStream is = null;
    	try {
    		SSLContext sc = SSLContext.getInstance("SSL");
    		sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
    				new java.security.SecureRandom());
    		URL console = new URL(null, url, new sun.net.www.protocol.https.Handler());
    		connection = (HttpsURLConnection) console.openConnection();
    		connection.setSSLSocketFactory(sc.getSocketFactory());
    		connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
    		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    		connection.setRequestMethod("POST");
    		connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
    		connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
    		connection.setUseCaches(false); //Post 请求不能使用缓存
    		connection.setConnectTimeout(30000); //设置连接主机超时时间
    		connection.setReadTimeout(30000); //设置从主机读取数据超时
			for(String header : headers.keySet()) {
				connection.setRequestProperty(header, headers.get(header));
			}
    		OutputStream out = connection.getOutputStream();
    		out.write(content.getBytes(charset));
    		// 刷新、关闭
    		out.flush();
    		out.close();
    		is = connection.getInputStream();
    		if (is != null) {
    			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    			byte[] buffer = new byte[1024];
    			int len = 0;
    			while ((len = is.read(buffer)) != -1) {
    				outStream.write(buffer, 0, len);
    			}
    			is.close();
    			return outStream.toByteArray();
    		}
    		return null;
    	} catch (Exception e) {
    		e.printStackTrace();
    		InputStream error = ((HttpURLConnection) connection).getErrorStream();
    		if (error != null) {
    			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    			byte[] buffer = new byte[1024];
    			int len = 0;
    			try {
    				while ((len = error.read(buffer)) != -1) {
    					outStream.write(buffer, 0, len);
    				}
    			} catch (IOException e2) {
    				e2.printStackTrace();
    			}
    			
    			return outStream.toByteArray();
    		}
    		return e.getMessage().getBytes();
    	}
    }
    
    
    
    public static String doHttpPostWithJsonRequestBody(String postUrl, com.alibaba.fastjson.JSONObject requestBody)
    {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("172.16.90.253", 17091));
        HttpURLConnection url_con = null;
        StringBuilder str = new StringBuilder();
        try {
            URL url = new URL(postUrl);
            url_con = (HttpURLConnection) url.openConnection(proxy);
            url_con.setConnectTimeout(50000);
            url_con.setRequestProperty("Accept", "application/json");
            url_con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            url_con.setRequestMethod("POST");
            url_con.setDoOutput(true);
            OutputStream out = url_con.getOutputStream();
            out.write(requestBody.toJSONString().getBytes("utf-8"));
            out.flush();
            out.close();
            BufferedReader rd = new BufferedReader(new InputStreamReader(url_con.getInputStream(), "utf-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                str.append(line).append("\r\n");
            }
            rd.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }
    
    
    public static String doHttpPostWithJsonRequestBody(String postUrl, String requestBody)
    {
    	Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("172.16.90.253", 17091));
    	HttpURLConnection url_con = null;
    	StringBuilder str = new StringBuilder();
    	try {
    		URL url = new URL(postUrl);
    		url_con = (HttpURLConnection) url.openConnection(proxy);
    		url_con.setConnectTimeout(50000);
    		url_con.setRequestProperty("Accept", "application/json");
    		url_con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    		url_con.setRequestMethod("POST");
    		url_con.setDoOutput(true);
    		OutputStream out = url_con.getOutputStream();
    		out.write(requestBody.getBytes("utf-8"));
    		out.flush();
    		out.close();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(url_con.getInputStream(), "utf-8"));
    		String line;
    		while ((line = rd.readLine()) != null) {
    			str.append(line).append("\r\n");
    		}
    		rd.close();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return str.toString();
    }
    
    public static String doHttpPostWithFormurlRequestBody(String postUrl, String requestBody)
    {
    	Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("172.16.90.253", 17091));
    	HttpURLConnection url_con = null;
    	StringBuilder str = new StringBuilder();
    	try {
    		URL url = new URL(postUrl);
    		url_con = (HttpURLConnection) url.openConnection(proxy);
    		url_con.setConnectTimeout(50000);
    		url_con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    		url_con.setRequestMethod("POST");
    		url_con.setDoOutput(true);
    		OutputStream out = url_con.getOutputStream();
    		out.write(requestBody.getBytes("utf-8"));
    		out.flush();
    		out.close();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(url_con.getInputStream(), "utf-8"));
    		String line;
    		while ((line = rd.readLine()) != null) {
    			str.append(line).append("\r\n");
    		}
    		rd.close();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return str.toString();
    }
    
    
    
    /**
     * post方式请求服务器(https协议)
     *
     * @param url     请求地址
     * @param
     * @param charset 编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static byte[] getHttps(String url, String charset)
            throws NoSuchAlgorithmException, KeyManagementException,
            IOException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                new java.security.SecureRandom());
        URL console = new URL(null, url, new sun.net.www.protocol.https.Handler());
        HttpsURLConnection connection = (HttpsURLConnection) console.openConnection();
        connection.setSSLSocketFactory(sc.getSocketFactory());
        connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestMethod("GET");
        connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
        connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
        connection.setUseCaches(false); //Post 请求不能使用缓存
        connection.setConnectTimeout(30000); //设置连接主机超时时间
        connection.setReadTimeout(45000); //设置从主机读取数据超时
        // 建立实际的连接
        connection.connect();
        InputStream is = connection.getInputStream();
        if (is != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
            return outStream.toByteArray();
        }
        return null;
    }

    public static byte[] postHttp(String url, String content, String charset)
            throws NoSuchAlgorithmException, KeyManagementException,
            IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
        connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
        connection.setUseCaches(false); //Post 请求不能使用缓存
        connection.setConnectTimeout(30000); //设置连接主机超时时间
        connection.setReadTimeout(30000); //设置从主机读取数据超时
        OutputStream out = connection.getOutputStream();
        out.write(content.getBytes(charset));
        // 刷新、关闭
        out.flush();
        out.close();
        InputStream is = connection.getInputStream();
        if (is != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
            return outStream.toByteArray();
        }
        return null;
    }
    
    public static byte[] postHttp(String url, String content, String charset,Map<String,String> headers)
    		throws NoSuchAlgorithmException, KeyManagementException,
    		IOException {
    	URL httpUrl = new URL(url);
    	HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
    	connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
    	connection.setRequestMethod("POST");
    	connection.setDoOutput(true); //http正文内，因此需要设为true, 默认情况下是false
    	connection.setDoInput(true); //设置是否从httpUrlConnection读入，默认情况下是true
    	connection.setUseCaches(false); //Post 请求不能使用缓存
    	connection.setConnectTimeout(30000); //设置连接主机超时时间
    	connection.setReadTimeout(30000); //设置从主机读取数据超时
		for(String header : headers.keySet()) {
			connection.setRequestProperty(header, headers.get(header));
		}
    	OutputStream out = connection.getOutputStream();
    	out.write(content.getBytes(charset));
    	// 刷新、关闭
    	out.flush();
    	out.close();
    	InputStream is = connection.getInputStream();
    	if (is != null) {
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		byte[] buffer = new byte[1024];
    		int len = 0;
    		while ((len = is.read(buffer)) != -1) {
    			outStream.write(buffer, 0, len);
    		}
    		is.close();
    		return outStream.toByteArray();
    	}
    	return null;
    }

    /**
     * 测试用方法块
     *
     * @throws Exception
     */
 /*   private static void query3Ele() throws Exception {
        String keyUrl = "https://api.tycredit.com/credit-front-http/credit/apply-for-private-key.json";
        Map<String, String> map = new HashMap<>();
        map.put("originalTransKey", "firstGetTransKey");
        map.put("originalGenTime", DateUtil.dateToString(new Date(), "yyyyMMdd24HHmmss"));
        map.put("validPeriod", "0");
        map.put("keyType", "0");
        map.put("flag", "flase");
        map.put("activityCode", "1001");
       // String keyContent = new HttpsUtil(map).getContent();
        System.err.println("----------------------------------------------------------------------------------");
       // System.out.println(keyContent);
        System.err.println("----------------------------------------------------------------------------------");
       // String res =  HttpsUtil.postHttpWithProxy(keyUrl, keyContent, "utf-8");
      // JSONObject json = JSONObject.fromObject(res);
        String authToken = "";
     //   if (json.containsKey("credit")) {
         //   JSONObject body = json.getJSONObject("credit").getJSONObject("body");
            if (body.containsKey("transKey")) {
                authToken = body.getString("transKey");
               // authToken = AesEncrypt.decrypt(authToken, PublicProperties.getValueByKey("tyzx_home_key"));
                System.err.print("----------------------------------------------------------------------------------");
                System.out.print(authToken);
                System.err.print("----------------------------------------------------------------------------------");
            }
        }
        map.clear();
        map.put("flag", "true");
        map.put("mobileNo", "13356366270");
        map.put("idNumber", "370784199407134511");
        map.put("personName", "苑同春");
        map.put("activityCode", "1005");
        map.put("userKey", authToken);
       // keyContent = new HttpsUtil(map).getContent();
        keyUrl = "https://api.tycredit.com/credit-front-http/unified/now.json";
     //   String content = HttpsUtil.postHttpWithProxy(keyUrl, keyContent, "utf-8");
//        System.out.println(content);


    }

    //状态
    private static String getStatus_Token() throws Exception {
       // String keyUrl = PublicProperties.getValueByKey("tyzx_token_url");
        Map<String, String> map = new HashMap<>();
        map.put("originalTransKey", "firstGetTransKey");
        map.put("originalGenTime", DateUtil.dateToString(new Date(), "yyyyMMdd24HHmmss"));
        map.put("validPeriod", "0");
        map.put("keyType", "0");
        map.put("flag", "flase");
        map.put("activityCode", "1001");
        String keyContent = new HttpsStatusUtil(map).getStatus_Content();
        long costTime = 0;
        long beginTime = System.currentTimeMillis();
        String res = "";
        try {
      //      res = new String(HttpsStatusUtil.postHttps(keyUrl, keyContent, "utf-8"), "utf-8");
            loger.error(res);

            costTime = System.currentTimeMillis() - beginTime;
        } catch (Exception e) {
            loger.error(e.getMessage(), e);
            return "";
        }
        if (res == null || res.equals("")) {
            return "";
        }
        JSONObject json = JSONObject.fromObject(res);
        String authToken = "";
        if (json.containsKey("credit")) {
            JSONObject body = json.getJSONObject("credit").getJSONObject("body");
            if (body.containsKey("transKey")) {
                authToken = body.getString("transKey");
             //   authToken = AesEncrypt.decrypt(authToken, PublicProperties.getValueByKey("tyzx_status_home_key"));
                loger.error("获取天翼征信token成功:" + authToken);

                return authToken;
            }
        }
        return "";
    }
*/
    public static void main(String[] args) throws Exception {
        String token = "14daaddc75d2a59d";
        System.out.print(AesEncrypt.decrypt("cIfse5wVxy0mD0nRgFh9deI2SEsem9/YppPYMlS5Ge8=", token));
    }
}
